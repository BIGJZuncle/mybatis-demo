package com.example.chapter01.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * user 表的数据访问接口，对应 chapter01/mapper/UserMapper.xml。
 */
public interface UserMapper {

    List<User> findAll();

    User findById(@Param("id") Integer id);
}
