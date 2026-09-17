package com.example.chapter03;

import com.example.entity.User;
import com.example.entity.Vo;
import com.example.chapter03.mapper.UserMapperMapping;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * 映射器演示测试：resultType 与 resultMap（含 VO 别名映射）。
 *
 * <p>加载 {@code chapter03/mybatis-config.xml}，SQL 全部在
 * {@code chapter03/mapper/UserMapperMapping.xml} 里。</p>
 */
public class UserMapperMappingTest {

    private InputStream is;
    private SqlSession session;
    private UserMapperMapping userMapper;

    @Before
    public void init() throws Exception {
        is = Resources.getResourceAsStream("chapter03/mybatis-config.xml");
        assertNotNull("找不到 chapter03/mybatis-config.xml", is);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true);
        userMapper = session.getMapper(UserMapperMapping.class);
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

    // ==================== resultType ====================

    @Test
    public void testSelectAllByResultType() {
        List<User> users = userMapper.selectAll();
        users.forEach(System.out::println);
        assertFalse("resultType 自动映射应该能查到数据", users.isEmpty());
        assertNotNull("id 应该有值（字段名与属性名一致）", users.get(0).getId());
        assertNotNull(users.get(0).getUsername());
    }

    @Test
    public void testSelectById() {
        User first = userMapper.selectAll().get(0);
        User user = userMapper.selectById(first.getId());
        System.out.println(user);
        assertNotNull(user);
        assertEquals(first.getId(), user.getId());
    }

    // ==================== resultType + VO 别名 ====================

    @Test
    public void testSelectAllByVoWithAlias() {
        List<Vo> vos = userMapper.selectAllByVo();
        vos.forEach(System.out::println);
        assertFalse("as 别名映射到 VO 应该能查到数据", vos.isEmpty());
        assertNotNull("p1 来自 id as p1", vos.get(0).getP1());
        assertNotNull("p2 来自 username as p2", vos.get(0).getP2());
        assertNotNull("p3 来自 password as p3", vos.get(0).getP3());
    }

    // ==================== resultMap ====================

    @Test
    public void testSelectAllByVoResultMap() {
        List<Vo> byMap = userMapper.selectAllByVoResultMap();
        List<Vo> byAlias = userMapper.selectAllByVo();
        byMap.forEach(System.out::println);

        assertEquals("两种写法查出的条数应该一样", byAlias.size(), byMap.size());
        assertEquals("resultMap 与 as 别名应得到同一份数据",
                byAlias.get(0).getP1(), byMap.get(0).getP1());
        assertEquals(byAlias.get(0).getP2(), byMap.get(0).getP2());
    }

    @Test
    public void testSelectAllByResultMap() {
        List<User> byMap = userMapper.selectAllByResultMap();
        List<User> byType = userMapper.selectAll();
        byMap.forEach(System.out::println);

        assertEquals(byType.size(), byMap.size());
        assertEquals("resultMap 显式映射 id/username/email/created_at 应该都对",
                byType.get(0).getId(), byMap.get(0).getId());
        assertEquals(byType.get(0).getUsername(), byMap.get(0).getUsername());
        assertNotNull("created_at → createdAt 映射应该生效", byMap.get(0).getCreatedAt());
    }

    // ==================== XML 方式 CRUD ====================

    @Test
    public void testXmlCrudRoundTrip() {
        String suffix = String.valueOf(System.currentTimeMillis());
        User user = new User();
        user.setUsername("xml_" + suffix);
        user.setPassword("xml_pwd");
        user.setEmail("xml_" + suffix + "@example.com");

        try {
            assertEquals(1, userMapper.insertUser(user));
            assertNotNull("useGeneratedKeys 应该回填主键", user.getId());
            System.out.println("XML 新增成功，回填主键 id=" + user.getId());

            User saved = userMapper.selectById(user.getId());
            assertNotNull(saved);
            assertEquals(user.getUsername(), saved.getUsername());

            saved.setUsername("xml_upd_" + suffix);
            saved.setEmail("xml_upd_" + suffix + "@example.com");
            assertEquals(1, userMapper.updateUser(saved));
            assertEquals("xml_upd_" + suffix, userMapper.selectById(user.getId()).getUsername());
        } finally {
            if (user.getId() != null) {
                System.out.println("清理临时数据，deleteById 影响行数=" + userMapper.deleteById(user.getId()));
                assertNull("删除后应查不到", userMapper.selectById(user.getId()));
            }
        }
    }
}
