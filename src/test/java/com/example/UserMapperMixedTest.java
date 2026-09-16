package com.example;

import com.example.entity.User;
import com.example.mapper.UserMapperMixed;
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

/**
 * 混合开发测试：同一个 Mapper 接口，SQL 一部分来自 XML，一部分来自注解。
 *
 * <ul>
 *   <li>{@code findAll} / {@code findById} → 来自
 *       {@code chapter02/mapper/UserMapperMixed.xml}；</li>
 *   <li>{@code searchByUsername} → 来自接口方法上的 {@code @Select} 注解。</li>
 * </ul>
 *
 * <p>注册时只用 {@code <mapper resource="chapter02/mapper/UserMapperMixed.xml"/>}
 * 一条即可：MyBatis 注册时会先装载 XML 里的 statement，再解析接口注解，
 * 所以不必（也不应该）再写一条 {@code <mapper class="com.example.mapper.UserMapperMixed"/>}，
 * 否则同一个 statement id 会被注册两次而报
 * "Mapped Statements collection already contains value"。</p>
 */
public class UserMapperMixedTest {

    private InputStream is;
    private SqlSession session;
    private UserMapperMixed userMapper;

    /** 库里第一条记录，作为本次测试的样例，避免依赖写死的种子数据。 */
    private User sample;

    @Before
    public void init() throws Exception {
        is = Resources.getResourceAsStream("mybatis-config.xml");
        assertNotNull("找不到 mybatis-config.xml", is);
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
        session = factory.openSession(true);
        userMapper = session.getMapper(UserMapperMixed.class);

        List<User> all = userMapper.findAll();
        assertFalse("user 表中应该有测试数据，请先执行 sql/User_db.sql", all.isEmpty());
        sample = all.get(0);
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

    /** XML 里的 findAll。 */
    @Test
    public void testFindAllFromXml() {
        List<User> users = userMapper.findAll();
        users.forEach(System.out::println);
        assertFalse("XML 中的 findAll 应该能查到数据", users.isEmpty());
    }

    /** XML 里的 findById。 */
    @Test
    public void testFindByIdFromXml() {
        User user = userMapper.findById(sample.getId());
        System.out.println(user);
        assertNotNull(user);
        assertEquals(sample.getId(), user.getId());
    }

    /** 注解里的 searchByUsername，与上面两个方法共用一个 namespace。 */
    @Test
    public void testSearchByUsernameFromAnnotation() {
        List<User> users = userMapper.searchByUsername(sample.getUsername());
        users.forEach(System.out::println);
        assertFalse("注解中的 searchByUsername 应该能查到数据", users.isEmpty());
        assertEquals("用户名唯一，应该只命中样例这一条", 1, users.size());
        assertEquals(sample.getId(), users.get(0).getId());
    }

    /** 两条 SQL 来源混用，但结果集映射规则一致（都映射成 User）。 */
    @Test
    public void testMixedSourcesMapToSameEntity() {
        User byXml = userMapper.findById(sample.getId());
        User byAnnotation = userMapper.searchByUsername(sample.getUsername()).get(0);
        assertEquals("两种来源查到的应是同一条数据", byXml.getId(), byAnnotation.getId());
        assertEquals(byXml.getUsername(), byAnnotation.getUsername());
        assertEquals(byXml.getEmail(), byAnnotation.getEmail());
        assertEquals(byXml.getCreatedAt(), byAnnotation.getCreatedAt());
    }
}
