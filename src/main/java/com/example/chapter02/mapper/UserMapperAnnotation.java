package com.example.chapter02.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 用户 Mapper 接口（注解方式）。
 *
 * <p>注解方式不需要 {@code UserMapperAnnotation.xml}，但必须在
 * {@code mybatis-config.xml} 中用
 * {@code <mapper class="com.example.chapter02.mapper.UserMapperAnnotation"/>} 注册。</p>
 *
 * <p>所有 SQL 都写成带反引号的 {@code `user`}，因为 user 在 MySQL 8 中容易和
 * 内置函数混淆；开了 mapUnderscoreToCamelCase，所以 created_at 能自动映射到 createdAt。</p>
 */
public interface UserMapperAnnotation {

    /**
     * 查询所有用户（@Select 注解）。
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` ORDER BY id")
    List<User> findAll();

    /**
     * 根据 ID 查询用户。单个参数时 #{id} 名字随便写也能取到值，
     * 但加上 @Param 更清晰（见 {@link #login}）。
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` WHERE id = #{id}")
    User findById(Integer id);

    /**
     * 添加用户（@Insert 注解 + 获取自增主键）。
     *
     * <p>{@code useGeneratedKeys = true} 配合 {@code keyProperty = "id"}，
     * 插入后 MySQL 生成的主键会被回填到入参对象的 id 属性上。</p>
     */
    @Insert("INSERT INTO `user`(username, password, email) VALUES(#{username}, #{password}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addUser(User user);

    /**
     * 更新用户（@Update 注解）。
     */
    @Update("UPDATE `user` SET username = #{username}, email = #{email} WHERE id = #{id}")
    int updateUser(User user);

    /**
     * 删除用户（@Delete 注解）。
     */
    @Delete("DELETE FROM `user` WHERE id = #{id}")
    int deleteUser(Integer id);

    /**
     * 多条件查询（@Param 注解指定参数名）。
     *
     * <p>没有 @Param 时 MyBatis 只能用 arg0/arg1/param1/param2 取值，
     * 所以这里必须用 @Param 把 #{name}、#{email} 和形参对应起来。</p>
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` "
            + "WHERE username = #{name} AND email = #{email}")
    List<User> findByNameAndEmail(@Param("name") String username, @Param("email") String email);

    /**
     * 登录：多个参数 + @Param 的标准写法。
     *
     * <p>对比 {@link UserMapperBadParam#login}（没有 @Param 的反面教材）。</p>
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` "
            + "WHERE username = #{username} AND password = #{password}")
    User login(@Param("username") String username, @Param("password") String password);

    /**
     * 参数较多时改用 Map 传递，Map 的 key 就是 #{} 里写的名字。
     *
     * <p>Map 方式不用加 @Param，但参数名失去了编译期检查，写错只能运行时报错。</p>
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` "
            + "WHERE username = #{username} AND email = #{email} AND password = #{password}")
    List<User> findUserByMap(Map<String, Object> params);

    /**
     * 模糊查询 —— 反面教材：用 ${} 直接拼字符串。
     *
     * <p>${} 是「字符串拼接」，参数会原样拼进 SQL 再编译，存在 <b>SQL 注入</b> 风险：
     * 传入 {@code ' OR '1'='1} 就能把 WHERE 条件破坏掉，查出全表数据。
     * 此处仅用于课程演示，实际开发禁止这样写。</p>
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` "
            + "WHERE username LIKE '%${username}%' ORDER BY id")
    List<User> findByNameLikeUnsafe(@Param("username") String username);

    /**
     * 模糊查询 —— 正确写法：#{} 是预编译占位符，通配符交给 CONCAT 在 SQL 侧拼。
     *
     * <p>#{} 会生成 {@code ?} 并把值交给 PreparedStatement 绑定，
     * 参数永远只是「数据」，不会被当成 SQL 语法，因此没有注入风险。</p>
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` "
            + "WHERE username LIKE CONCAT('%', #{keyword}, '%') ORDER BY id")
    List<User> findByNameLike(@Param("keyword") String keyword);

    /**
     * 动态表名查询 —— ${} 的正确用法。
     *
     * <p>表名属于 SQL 语法的一部分，不能用 #{}（那会变成 {@code FROM 'user'}
     * 字符串字面量而语法错误），只能用 ${}。代价是没有预编译保护，
     * 所以 tableName 必须在 Java 侧做白名单校验，绝不能直接来自用户输入。</p>
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM ${tableName} ORDER BY id")
    List<User> findAllByTableName(@Param("tableName") String tableName);
}
