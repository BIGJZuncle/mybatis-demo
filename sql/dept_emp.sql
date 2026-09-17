-- ========== 创建 dept 和 emp 表（第4节、第5节共用） ==========
-- 用途：演示 Mybatis 多表操作（一对多、多对一）
-- 执行方式：在 MySQL 中运行此脚本
-- =====================================================================

-- 指定数据库（原脚本没有 USE，直接执行会落到当前默认库，这里补上）
USE mybatis_db;

-- 如果表已存在，先删除（谨慎！）
DROP TABLE IF EXISTS emp;
DROP TABLE IF EXISTS dept;

-- 创建部门表
CREATE TABLE dept (
    deptno INT PRIMARY KEY AUTO_INCREMENT COMMENT '部门编号',
    dname VARCHAR(50) NOT NULL COMMENT '部门名称',
    loc VARCHAR(50) COMMENT '部门位置'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 创建员工表
CREATE TABLE emp (
    empno INT PRIMARY KEY AUTO_INCREMENT COMMENT '员工编号',
    ename VARCHAR(50) NOT NULL COMMENT '员工姓名',
    job VARCHAR(50) COMMENT '职位',
    mgr INT COMMENT '上级编号',
    hiredate DATE COMMENT '入职日期',
    sal DECIMAL(10,2) COMMENT '薪水',
    comm DECIMAL(10,2) COMMENT '佣金',
    deptno INT COMMENT '部门编号',
    FOREIGN KEY (deptno) REFERENCES dept(deptno)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ========== 插入示例数据 ==========

-- 插入部门数据
INSERT INTO dept (dname, loc) VALUES
('ACCOUNTING', 'NEW YORK'),
('RESEARCH', 'DALLAS'),
('SALES', 'CHICAGO'),
('OPERATIONS', 'BOSTON');

-- 插入员工数据
INSERT INTO emp (ename, job, mgr, hiredate, sal, comm, deptno) VALUES
('SMITH', 'CLERK', 7902, '1980-12-17', 800.00, NULL, 2),
('ALLEN', 'SALESMAN', 7698, '1981-02-20', 1600.00, 300.00, 3),
('WARD', 'SALESMAN', 7698, '1981-02-22', 1250.00, 500.00, 3),
('JONES', 'MANAGER', 7839, '1981-04-02', 2975.00, NULL, 2),
('MARTIN', 'SALESMAN', 7698, '1981-09-28', 1250.00, 1400.00, 3),
('BLAKE', 'MANAGER', 7839, '1981-05-01', 2850.00, NULL, 3),
('CLARK', 'MANAGER', 7839, '1981-06-09', 2450.00, NULL, 1),
('SCOTT', 'ANALYST', 7566, '1987-04-19', 3000.00, NULL, 2),
('KING', 'PRESIDENT', NULL, '1981-11-17', 5000.00, NULL, 1),
('TURNER', 'SALESMAN', 7698, '1981-09-08', 1500.00, 0.00, 3),
('ADAMS', 'CLERK', 7788, '1987-05-23', 1100.00, NULL, 2),
('JAMES', 'CLERK', 7698, '1981-12-03', 950.00, NULL, 3),
('FORD', 'ANALYST', 7566, '1981-12-03', 3000.00, NULL, 2),
('MILLER', 'CLERK', 7782, '1982-01-23', 1300.00, NULL, 1);

-- ========== 验证数据 ==========
SELECT * FROM dept;
SELECT * FROM emp;

-- 多表关联查询（测试）
SELECT e.empno, e.ename, e.job, d.dname, d.loc
FROM emp e
LEFT JOIN dept d ON e.deptno = d.deptno;
