package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 混合开发示例：同一个 Mapper 接口，一部分 SQL 写在 XML 里，一部分用注解写。
 *
 * <p>findAll / findById 的 SQL 在 {@code chapter02/mapper/UserMapperMixed.xml} 中，
 * searchByUsername 的 SQL 用 {@link Select} 注解写在本文件里。</p>
 *
 * <p>工作机制：MyBatis 注册这个接口时，会先把 XML 里的 statement 装进
 * Configuration，再解析接口上的注解；如果某个 id 已经在 XML 里存在，
 * 注解那一条就会被跳过（<b>XML 优先</b>），因此不会报
 * "Mapped Statements collection already contains value"。</p>
 *
 * <p>注册方式和纯 XML 完全一样，用
 * {@code <mapper resource="chapter02/mapper/UserMapperMixed.xml"/>} 即可，
 * 不需要再写 {@code <mapper class="..."/>}。</p>
 */
public interface UserMapperMixed {

    /** SQL 来自 XML。 */
    List<User> findAll();

    /** SQL 来自 XML。 */
    User findById(@Param("id") Integer id);

    /** SQL 来自注解。 */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` "
            + "WHERE username LIKE CONCAT('%', #{keyword}, '%') ORDER BY id")
    List<User> searchByUsername(@Param("keyword") String keyword);
}
