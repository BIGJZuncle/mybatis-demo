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
2. 创建数据库 `mybatis_db`，并执行 `sql/User_db.sql` 建表 + 插入示例数据。
3. 在项目根目录执行 `./mvnw.cmd test`（Windows）或 `./mvnw test`（macOS/Linux）。

如果终端提示 `JAVA_HOME` 未设置，可在 Windows 用户环境变量中设置：
`JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.11.9-hotspot`，并将 `%JAVA_HOME%\bin` 加入 `Path`。

`db.properties` 已被 `.gitignore` 排除，不要把真实密码提交到 Git。
