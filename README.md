# demo

课程 MyBatis 学习项目，按《第1节 Mybatis的组件_详细版.html》建立（对应 mybatis-demo 的效果）。

## 环境

- JDK 17（Maven 编译目标为 Java 8）
- Maven Wrapper 3.9.x
- MyBatis 3.5.19
- MySQL Connector/J 8.4.0
- JUnit 4.13.2

## 第一次使用

1. 确认 `src/main/resources/common/db.properties` 中的 MySQL 账号密码与本机一致（必要时也可复制 `src/main/resources/db.properties.example` 为 `db.properties` 使用）。
2. 执行建库脚本（都在 `sql/` 目录，按顺序执行）：
   - `sql/User_db.sql`：建库 `mybatis_db` + `user` 表 + 10 条示例数据；
   - `sql/dept_emp.sql`：建 `dept`（4 个部门）和 `emp`（14 名员工）两张表，用于多表关联实验。
3. 在项目根目录执行 `./mvnw.cmd test`（Windows）或 `./mvnw test`（macOS/Linux）。

如果终端提示 `JAVA_HOME` 未设置，可在 Windows 用户环境变量中设置：
`JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.11.9-hotspot`，并将 `%JAVA_HOME%\bin` 加入 `Path`。

`db.properties` 已被 `.gitignore` 排除，不要把真实密码提交到 Git。

## 目录与章节内容

| 章节 | 配置 | 内容 | 测试类 |
|---|---|---|---|
| chapter01 | `chapter01/mybatis-config.xml` | XML 方式：`UserMapper`（findAll / findById） | `UserMapperTest` |
| chapter02 | 根目录 `mybatis-config.xml` | 注解方式：`UserMapperAnnotation`（CRUD / @Param / #{} 与 ${}）；反面教材 `UserMapperBadParam`；XML+注解混合 `UserMapperMixed` | `UserMapperAnnotationTest`、`UserMapperMixedTest` |
| chapter03 | `chapter03/mybatis-config.xml` | 映射器深入：`UserMapperMapping`（resultType / resultMap / VO 别名）；多表关联：`EmpMapper`（多对一 association）、`DeptMapper`（一对多 collection） | `UserMapperMappingTest`、`EmpDeptMapperTest` |

要点速记：

- `#{}` 是预编译占位符（安全），`${}` 是字符串拼接（仅用于表名、列名等 SQL 结构，不能拼接用户输入的值）；
- 多个参数必须用 `@Param` 命名，否则报 `Parameter 'xxx' not found. Available parameters are [...]`；
- `resultType` 与 `resultMap` 互斥；字段名与属性名不一致时，`resultType` 要靠 `as` 别名，`resultMap` 则写 `property`-`column` 映射；
- 关联查询两种写法：一条 JOIN + 内联 `association`/`collection`（推荐），或嵌套 select（写法简单但有 N+1 问题）；LEFT JOIN + 内联 `collection` 时用 `notNullColumn` 去掉没有子记录时的空对象。
