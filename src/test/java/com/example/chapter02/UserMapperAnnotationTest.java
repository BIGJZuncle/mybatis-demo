package com.example.chapter02;

import com.example.entity.User;
import com.example.chapter02.mapper.UserMapperAnnotation;
import com.example.chapter02.mapper.UserMapperBadParam;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * 注解方式 Mapper 测试类（实验 2.3 + 第 2 学时 3.1~3.3）。
 *
 * <p>加载根目录的 {@code mybatis-config.xml}，其中已用
 * {@code <mapper class="com.example.chapter02.mapper.UserMapperAnnotation"/>} 注册注解接口。</p>
 *
 * <p><b>关于测试数据</b>：课程讲义里写死了「张三 / zhangsan@qq.com / 123456」，
 * 但库里实际数据会变（例如现在 id=1 的用户名是「大象」，其余是 testuser2~10），
 * 写死值一改就挂。所以这里改成：{@code @Before} 时从库里取第一条记录作为「样例行」，
 * 后面所有查询都用它的用户名/邮箱/密码，断言逻辑与讲义完全一致。</p>
 */
public class UserMapperAnnotationTest {

    private InputStream is;
    private SqlSession session;
    private UserMapperAnnotation userMapper;

    /** 库里第一条记录，作为本次测试的样例（用户名/邮箱/密码都从它取）。 */
    private User sample;

    @Before
    public void init() throws Exception {
        is = Resources.getResourceAsStream("chapter02/mybatis-config.xml");
        assertNotNull("找不到 mybatis-config.xml", is);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true); // true = 自动提交，方便演示 CRUD
        userMapper = session.getMapper(UserMapperAnnotation.class);

        List<User> all = userMapper.findAll();
        assertFalse("user 表中应该有测试数据，请先执行 sql/User_db.sql", all.isEmpty());
        sample = all.get(0);
        System.out.println("本次测试样例：" + sample);
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

    // ==================== 2.1 注解 CRUD ====================

    @Test
    public void testFindAll() {
        List<User> users = userMapper.findAll();
        users.forEach(System.out::println);
        assertFalse("user 表中应该有测试数据", users.isEmpty());
    }

    @Test
    public void testFindById() {
        User user = userMapper.findById(sample.getId());
        System.out.println(user);
        assertNotNull("按 id 查询应该查到样例用户", user);
        assertEquals(sample.getId(), user.getId());
    }

    /**
     * 新增 + 修改 + 删除的完整闭环；临时数据在 finally 里删掉，不污染数据库。
     */
    @Test
    public void testCrudRoundTrip() {
        String suffix = String.valueOf(System.currentTimeMillis());
        User user = new User();
        user.setUsername("tmp_" + suffix);
        user.setPassword("tmp_pwd");
        user.setEmail("tmp_" + suffix + "@example.com");

        try {
            // 新增：@Options(useGeneratedKeys = true, keyProperty = "id") 会把自增主键回填到 user.id
            assertEquals(1, userMapper.addUser(user));
            assertNotNull("自增主键应该被回填到入参对象上", user.getId());
            System.out.println("新增成功，回填的主键 id=" + user.getId());

            // 查
            User saved = userMapper.findById(user.getId());
            assertNotNull(saved);
            assertEquals(user.getUsername(), saved.getUsername());

            // 改
            saved.setUsername("tmp_upd_" + suffix);
            saved.setEmail("tmp_upd_" + suffix + "@example.com");
            assertEquals(1, userMapper.updateUser(saved));
            assertEquals("tmp_upd_" + suffix, userMapper.findById(user.getId()).getUsername());
        } finally {
            // 删
            if (user.getId() != null) {
                System.out.println("清理临时数据，deleteUser 影响行数=" + userMapper.deleteUser(user.getId()));
                assertNull("删除后应该查不到", userMapper.findById(user.getId()));
            }
        }
    }

    // ==================== 3.2 @Param 多参数 ====================

    @Test
    public void testFindByNameAndEmail() {
        List<User> users = userMapper.findByNameAndEmail(sample.getUsername(), sample.getEmail());
        users.forEach(System.out::println);
        assertFalse("@Param 命名的两个参数应该能查到数据", users.isEmpty());
        assertEquals(sample.getId(), users.get(0).getId());
    }

    @Test
    public void testLogin() {
        User user = userMapper.login(sample.getUsername(), sample.getPassword());
        System.out.println(user);
        assertNotNull("用户名 + 密码正确时应该查到用户", user);

        // 密码故意改错一位，应该查不到
        User wrong = userMapper.login(sample.getUsername(), sample.getPassword() + "_wrong");
        assertNull("密码错误时应该查不到", wrong);
    }

    // ==================== 3.3 Map 传参 ====================

    @Test
    public void testFindUserByMap() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", sample.getUsername());
        params.put("email", sample.getEmail());
        params.put("password", sample.getPassword());

        List<User> users = userMapper.findUserByMap(params);
        users.forEach(System.out::println);
        assertFalse("Map 里的 key 就是 #{} 中的名字", users.isEmpty());
    }

    // ==================== 3.1 反面教材：多参数不加 @Param ====================

    /**
     * 3.1 的报错演示：{@code UserMapperBadParam.login} 两个参数没加 @Param。
     *
     * <p>MyBatis 只能把参数封成 ParamMap，key 是 arg0/arg1/param1/param2，
     * SQL 里的 #{username} 找不到对应 key，运行时抛出（并被包装成 PersistenceException）：</p>
     * <pre>
     * Parameter 'username' not found. Available parameters are [arg1, arg0, param1, param2]
     * </pre>
     *
     * <p>注意：如果编译时带了 {@code -parameters}（IDEA 勾选 "Store information about method
     * parameters"），参数真名会被保留，这个错误就不会出现——所以两种结果都接受，只打印实际现象。</p>
     */
    @Test
    public void testBadParamLogin() {
        UserMapperBadParam badMapper = session.getMapper(UserMapperBadParam.class);
        try {
            User user = badMapper.login(sample.getUsername(), sample.getPassword());
            System.out.println("[3.1] 未抛异常：编译时保留了参数名（-parameters），查到 " + user);
        } catch (PersistenceException e) {
            String message = e.getMessage();
            System.out.println("[3.1] 预期异常：" + message);
            assertTrue("异常信息里应提示参数找不到", message != null && message.contains("not found"));
            assertTrue("根本原因应该是 BindingException",
                    e.getCause() instanceof org.apache.ibatis.binding.BindingException);
        }
    }

    // ==================== #{} 与 ${} 的区别与安全用法 ====================

    @Test
    public void testFindByNameLikeSafeAndUnsafe() {
        String keyword = sample.getUsername();
        List<User> safe = userMapper.findByNameLike(keyword);          // #{} + CONCAT
        List<User> unsafe = userMapper.findByNameLikeUnsafe(keyword);  // ${} 字符串拼接
        System.out.println("模糊查询「" + keyword + "」：安全写法(#{} + CONCAT)命中 " + safe.size()
                + " 条；${} 写法命中 " + unsafe.size() + " 条");
        safe.forEach(System.out::println);
        assertEquals("正常入参下两种写法结果应一致", safe.size(), unsafe.size());
        assertFalse(safe.isEmpty());
    }

    /**
     * ${} 的注入风险演示：入参 {@code ' OR '1'='1} 会把 WHERE 条件破坏掉，查出全表。
     */
    @Test
    public void testUnsafeLikeCanBeInjected() {
        int allRows = userMapper.findAll().size();
        List<User> injected = userMapper.findByNameLikeUnsafe("' OR '1'='1");
        System.out.println("[注入演示] 正常模糊查询命中 " + userMapper.findByNameLike(sample.getUsername()).size()
                + " 条；传入 ' OR '1'='1 后命中 " + injected.size() + " 条（全表共 " + allRows + " 条）");
        assertEquals("注入后应查出全表，说明 ${} 拼接不安全", allRows, injected.size());
    }

    /**
     * ${} 的正确用法：拼接表名、列名这类 SQL 结构（不是「值」，所以 #{} 用不了）。
     */
    @Test
    public void testFindAllByTableName() {
        List<User> users = userMapper.findAllByTableName("user");
        users.forEach(System.out::println);
        assertFalse(users.isEmpty());
    }

    /**
     * 字段映射兜底：mapUnderscoreToCamelCase 生效（created_at → createdAt）。
     */
    @Test
    public void testCamelCaseMapping() {
        User user = userMapper.findById(sample.getId());
        assertNotNull(user);
        assertNotNull("created_at 应映射到 createdAt", user.getCreatedAt());
        assertNotNull("updated_at 应映射到 updatedAt", user.getUpdatedAt());
        assertNotNull(user.getEmail());
        assertNotNull(user.getUsername());
    }
}
