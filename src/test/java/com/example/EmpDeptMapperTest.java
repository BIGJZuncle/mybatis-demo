package com.example;

import com.example.entity.Dept;
import com.example.entity.Emp;
import com.example.mapper.DeptMapper;
import com.example.mapper.EmpMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 多表关联测试：多对一（&lt;association&gt;）与一对多（&lt;collection&gt;）。
 *
 * <p>数据来自 {@code sql/dept_emp.sql}：4 个部门、14 个员工，
 * 人数分布为 ACCOUNTING(1)=3、RESEARCH(2)=5、SALES(3)=6、OPERATIONS(4)=0。</p>
 *
 * <p>每种关联都验证两种写法（嵌套 select / JOIN），并互相对照，
 * 确保两种写法的结果一致。</p>
 */
public class EmpDeptMapperTest {

    private InputStream is;
    private SqlSession session;
    private EmpMapper empMapper;
    private DeptMapper deptMapper;

    @Before
    public void init() throws Exception {
        is = Resources.getResourceAsStream("chapter03/mybatis-config.xml");
        assertNotNull("找不到 chapter03/mybatis-config.xml", is);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true);
        empMapper = session.getMapper(EmpMapper.class);
        deptMapper = session.getMapper(DeptMapper.class);
    }

    @After
    public void destroy() throws Exception {
        if (session != null) {
            session.close();
        }
        if (is != null) {
            is.close();
        }
    }

    // ==================== 多对一：Emp.dept ====================

    /** JOIN + 内联 association：一条 SQL 带出部门。 */
    @Test
    public void testFindAllWithDeptByJoin() {
        List<Emp> emps = empMapper.findAllWithDept();
        emps.forEach(System.out::println);

        assertEquals("emp 表共 14 条（见 sql/dept_emp.sql）", 14, emps.size());
        emps.forEach(emp -> assertNotNull(emp.getEname() + " 应该带出所属部门", emp.getDept()));

        Emp smith = emps.get(0);
        assertEquals("SMITH", smith.getEname());
        assertEquals("SMITH 属于 RESEARCH", "RESEARCH", smith.getDept().getDname());
        assertEquals("RESEARCH 在 DALLAS", "DALLAS", smith.getDept().getLoc());
    }

    /** JOIN + association：按员工号查单个。 */
    @Test
    public void testFindByIdWithDept() {
        Emp king = empMapper.findByIdWithDept(9);
        System.out.println(king);
        assertNotNull(king);
        assertEquals("KING", king.getEname());
        assertNotNull(king.getDept());
        assertEquals("KING 属于 ACCOUNTING", "ACCOUNTING", king.getDept().getDname());
    }

    /** 嵌套 select：association 里用 select 逐条查部门，结果应与 JOIN 一致。 */
    @Test
    public void testFindAllWithDeptByNestedSelect() {
        List<Emp> nested = empMapper.findAllWithDeptNested();
        List<Emp> joined = empMapper.findAllWithDept();

        assertEquals("两种写法员工条数一致", joined.size(), nested.size());
        nested.forEach(emp -> {
            assertNotNull(emp.getEname() + " 的部门应被嵌套查询填充", emp.getDept());
            assertEquals("嵌套查询的部门号应与外键一致",
                    emp.getDeptno(), emp.getDept().getDeptno());
        });

        Map<Integer, String> joinedDeptName = joined.stream()
                .collect(Collectors.toMap(Emp::getEmpno, emp -> emp.getDept().getDname()));
        nested.forEach(emp -> assertEquals("同一员工的部门名应一致",
                joinedDeptName.get(emp.getEmpno()), emp.getDept().getDname()));
    }

    /** 按部门号查员工（DeptMapper 的 collection 内部就用它）。 */
    @Test
    public void testFindByDeptno() {
        List<Emp> sales = empMapper.findByDeptno(3);
        sales.forEach(System.out::println);
        assertEquals("SALES(3) 有 6 名员工", 6, sales.size());
        sales.forEach(emp -> assertEquals(Integer.valueOf(3), emp.getDeptno()));
    }

    // ==================== 一对多：Dept.emps ====================

    /** 嵌套 select：先查部门，再逐个部门查员工。 */
    @Test
    public void testFindAllWithEmpsByNestedSelect() {
        List<Dept> depts = deptMapper.findAllWithEmps();
        depts.forEach(dept -> System.out.println(dept + " -> " + dept.getEmps()));

        assertEquals("dept 表共 4 条", 4, depts.size());
        depts.forEach(dept -> assertNotNull(dept.getDname() + " 的员工集合不应为 null", dept.getEmps()));

        assertEquals("ACCOUNTING 3 人", 3, countEmps(depts, 1));
        assertEquals("RESEARCH 5 人", 5, countEmps(depts, 2));
        assertEquals("SALES 6 人", 6, countEmps(depts, 3));
        assertEquals("OPERATIONS 0 人", 0, countEmps(depts, 4));
    }

    /** 嵌套 select：单个部门 + 员工列表。 */
    @Test
    public void testFindByIdWithEmps() {
        Dept sales = deptMapper.findByIdWithEmps(3);
        System.out.println(sales + " -> " + sales.getEmps());
        assertNotNull(sales);
        assertEquals("SALES", sales.getDname());
        assertEquals(6, sales.getEmps().size());
        assertTrue("SALES 的员工里应该有 ALLEN",
                sales.getEmps().stream().anyMatch(emp -> "ALLEN".equals(emp.getEname())));
    }

    /** JOIN + 内联 collection：一条 SQL 查出部门及员工，OPERATIONS 应为空集合而不是「一个空员工」。 */
    @Test
    public void testFindAllWithEmpsByJoin() {
        List<Dept> depts = deptMapper.findAllWithEmpsByJoin();
        depts.forEach(dept -> System.out.println(dept + " -> " + dept.getEmps()));

        assertEquals(4, depts.size());
        assertEquals(3, countEmps(depts, 1));
        assertEquals(5, countEmps(depts, 2));
        assertEquals(6, countEmps(depts, 3));

        Dept operations = byDeptno(depts, 4);
        assertNotNull(operations.getEmps());
        assertTrue("notNullColumn=empno 应该去掉 LEFT JOIN 产生的空员工对象",
                operations.getEmps().isEmpty());
    }

    /** 两种写法结果对照。 */
    @Test
    public void testNestedAndJoinProduceSameCounts() {
        List<Dept> nested = deptMapper.findAllWithEmps();
        List<Dept> joined = deptMapper.findAllWithEmpsByJoin();

        for (Dept dept : nested) {
            assertEquals("部门 " + dept.getDname() + " 两种写法人数应一致",
                    dept.getEmps().size(), byDeptno(joined, dept.getDeptno()).getEmps().size());
        }
        assertFalse(nested.isEmpty());
    }

    // ==================== 辅助方法 ====================

    private Dept byDeptno(List<Dept> depts, int deptno) {
        return depts.stream()
                .filter(dept -> dept.getDeptno() == deptno)
                .findFirst()
                .orElseThrow(() -> new AssertionError("找不到部门 " + deptno));
    }

    private int countEmps(List<Dept> depts, int deptno) {
        Map<Integer, Dept> index = depts.stream()
                .collect(Collectors.toMap(Dept::getDeptno, Function.identity()));
        return index.get(deptno).getEmps().size();
    }
}
