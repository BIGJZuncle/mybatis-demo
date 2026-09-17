package com.example.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工实体类（emp 表）。
 *
 * <p>与 dept 表是<b>多对一</b>关系：多个员工属于一个部门，因此这里放一个
 * {@link Dept} 对象，由 Mapper XML 里的 {@code <association>} 或嵌套 select 填充。</p>
 *
 * <p>注：原文件里的 {@code @TableLogic} / {@code deleted} 是 MyBatis-Plus 的特性，
 * 本项目是纯 MyBatis，且 sql/dept_emp.sql 里也没有 deleted 列，故已去掉。</p>
 */
public class Emp implements Serializable {

    private Integer empno;     // 员工编号（主键）
    private String ename;      // 员工姓名
    private String job;        // 职位
    private Integer mgr;       // 上级编号
    private Date hiredate;     // 入职日期
    private Double sal;        // 薪水
    private Double comm;       // 佣金
    private Integer deptno;    // 部门编号（外键）

    /** 多对一：一个员工属于一个部门（关联对象）。 */
    private Dept dept;

    public Emp() {
    }

    public Emp(Integer empno, String ename, String job, Integer mgr,
               Date hiredate, Double sal, Double comm, Integer deptno) {
        this.empno = empno;
        this.ename = ename;
        this.job = job;
        this.mgr = mgr;
        this.hiredate = hiredate;
        this.sal = sal;
        this.comm = comm;
        this.deptno = deptno;
    }

    public Integer getEmpno() {
        return empno;
    }

    public void setEmpno(Integer empno) {
        this.empno = empno;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Integer getMgr() {
        return mgr;
    }

    public void setMgr(Integer mgr) {
        this.mgr = mgr;
    }

    public Date getHiredate() {
        return hiredate;
    }

    public void setHiredate(Date hiredate) {
        this.hiredate = hiredate;
    }

    public Double getSal() {
        return sal;
    }

    public void setSal(Double sal) {
        this.sal = sal;
    }

    public Double getComm() {
        return comm;
    }

    public void setComm(Double comm) {
        this.comm = comm;
    }

    public Integer getDeptno() {
        return deptno;
    }

    public void setDeptno(Integer deptno) {
        this.deptno = deptno;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }

    /**
     * 输出里带上部门名，方便在测试结果中直观看到 association 是否填充成功。
     * {@link Dept#toString()} 不打印员工列表，所以不会递归。
     */
    @Override
    public String toString() {
        return "Emp{" +
                "empno=" + empno +
                ", ename='" + ename + '\'' +
                ", job='" + job + '\'' +
                ", sal=" + sal +
                ", comm=" + comm +
                ", deptno=" + deptno +
                ", dept=" + (dept == null ? null : dept.getDname() + "/" + dept.getLoc()) +
                '}';
    }
}
