package com.example.mapper;

import com.example.entity.Dept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门 Mapper —— 演示<b>一对多</b>关联（一个部门有多个员工）。
 *
 * <p>SQL 在 {@code chapter03/mapper/DeptMapper.xml}，两种写法各一份：</p>
 * <ul>
 *   <li>{@link #findAllWithEmps()} / {@link #findByIdWithEmps(Integer)}：
 *       <b>嵌套 select（&lt;collection select="..."/&gt;）</b>，
 *       先查部门，再按 deptno 查每个部门的员工；</li>
 *   <li>{@link #findAllWithEmpsByJoin()}：
 *       <b>一条 LEFT JOIN + 内联 &lt;collection&gt;</b>，
 *       一次查询搞定，注意用 notNullColumn 去掉没有员工时的空对象。</li>
 * </ul>
 */
public interface DeptMapper {

    /** 只查部门本身（不查员工）。 */
    List<Dept> findAll();

    /** 只查部门本身，按部门号；同时供 EmpMapper 的 association 嵌套查询调用。 */
    Dept findById(@Param("deptno") Integer deptno);

    /** 一对多（嵌套 select）：部门 + 员工列表。 */
    List<Dept> findAllWithEmps();

    /** 一对多（嵌套 select）：单个部门 + 员工列表。 */
    Dept findByIdWithEmps(@Param("deptno") Integer deptno);

    /** 一对多（JOIN + 内联 collection）：一条 SQL 查出部门及员工。 */
    List<Dept> findAllWithEmpsByJoin();
}
