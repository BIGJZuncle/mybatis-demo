package com.example.chapter02.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Select;

/**
 * 第 2 学时 3.1 —— 多参数传递「错误示例」。
 *
 * <p>接口里有多个参数且没有 @Param 时，MyBatis 会把它们封成一个 ParamMap，
 * key 只有 {@code [arg0, arg1, param1, param2]}。SQL 里写 #{username} 找不到
 * 同名 key，于是运行（而不是启动）时抛错：</p>
 *
 * <pre>
 * Parameter 'username' not found. Available parameters are [arg0, arg1, param1, param2]
 * </pre>
 *
 * <p>修法见 {@link UserMapperAnnotation#login}：给每个参数加 @Param 指定名字。</p>
 */
public interface UserMapperBadParam {

    /**
     * 错误写法：两个参数却没有 @Param。
     */
    @Select("SELECT id, username, password, email, created_at, updated_at FROM `user` "
            + "WHERE username = #{username} AND password = #{password}")
    User login(String username, String password);
}
