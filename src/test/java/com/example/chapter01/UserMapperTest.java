package com.example.chapter01;

import com.example.entity.User;
import com.example.chapter01.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * chapter01 的 UserMapper 集成测试。
 *
 * <p>运行前请确认 common/db.properties 中的数据库连接信息正确，
 * 并且已经执行 sql/User_db.sql。</p>
 */
public class    UserMapperTest {

    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void init() throws Exception {
        try (InputStream inputStream = Resources.getResourceAsStream("chapter01/mybatis-config.xml")) {
            assertNotNull("找不到 chapter01/mybatis-config.xml", inputStream);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        }
    }

    @Test
    public void testFindAll() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            List<User> users = userMapper.findAll();

            assertNotNull(users);
            assertTrue("user 表中应该至少有一条测试数据", users.size() > 0);
            users.forEach(System.out::println);
        }
    }

    @Test
    public void testFindById() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.findById(1);

            assertNotNull("id=1 的用户不存在，请先执行 sql/User_db.sql", user);
            System.out.println(user);
        }
    }
}
