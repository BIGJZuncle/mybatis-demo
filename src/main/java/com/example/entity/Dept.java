package com.example.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 部门实体类（dept 表）。
 *
 * <p>与 emp 表是<b>一对多</b>关系：一个部门有多个员工，因此这里放一个
 * {@code List<Emp>}，由 Mapper XML 里的 {@code <collection>} 嵌套查询或 JOIN 填充。</p>
 *
 * <p>注：原文件里的 {@code @TableLogic} / {@code deleted} 是 MyBatis-Plus 的特性，
 * 本项目是纯 MyBatis，且 sql/dept_emp.sql 里也没有 deleted 列，故已去掉。</p>
 */
public class Dept implements Serializable {

    private Integer deptno;   // 部门编号（主键）
    private String dname;     // 部门名称
    private String loc;       // 部门位置

    /** 一对多：一个部门有多个员工（关联集合）。 */
    private List<Emp> emps;

    public Dept() {
    }

    public Dept(Integer deptno, String dname, String loc) {
        this.deptno = deptno;
        this.dname = dname;
        this.loc = loc;
    }

    public Integer getDeptno() {
        return deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public List<Emp> getEmps() {
        return emps;
    }

    public void setEmps(List<Emp> emps) {
        this.emps = emps;
    }

    /** 只打印部门自身字段，避免和 {@link Emp#toString()} 相互递归。 */
    @Override
    public String toString() {
        return "Dept{" +
                "deptno=" + deptno +
                ", dname='" + dname + '\'' +
                ", loc='" + loc + '\'' +
                ", emps=" + (emps == null ? "null" : emps.size() + "人") +
                '}';
    }
}
