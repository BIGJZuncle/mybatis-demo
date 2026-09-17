package com.example.chapter03.mapper;

import com.example.entity.Emp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工 Mapper —— 演示<b>多对一</b>关联（多个员工属于一个部门）。
 *
 * <p>SQL 在 {@code chapter03/mapper/EmpMapper.xml}，两种写法各一份：</p>
 * <ul>
 *   <li>{@link #findAllWithDept()} / {@link #findByIdWithDept(Integer)}：
 *       <b>一条 JOIN + 内联 &lt;association&gt;</b>，一次查询拿到全部数据；</li>
 *   <li>{@link #findAllWithDeptNested()}：
 *       <b>嵌套 select（&lt;association select="..."/&gt;）</b>，
 *       先查员工再按 deptno 逐条查部门，写法简单但会产生 N+1 次查询。</li>
 * </ul>
 */
public interface EmpMapper {

    /** 多对一（JOIN + association）：查所有员工，并带出所属部门。 */
    List<Emp> findAllWithDept();

    /** 多对一（JOIN + association）：按员工号查询，并带出所属部门。 */
    Emp findByIdWithDept(@Param("empno") Integer empno);

    /** 多对一（嵌套 select）：先查员工，再逐条查部门。 */
    List<Emp> findAllWithDeptNested();

    /** 按部门号查员工（供 DeptMapper 的 collection 嵌套查询调用）。 */
    List<Emp> findByDeptno(@Param("deptno") Integer deptno);
}
