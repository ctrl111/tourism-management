package com.project.platform.mapper;

import com.project.platform.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;


public interface UserMapper {
    List<User> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM user WHERE id = #{id}")
    @Results(id = "userResultMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "username", column = "username"),
        @Result(property = "password", column = "password"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "email", column = "email"),
        @Result(property = "avatarUrl", column = "avatar_url"),
        @Result(property = "balance", column = "balance"),
        @Result(property = "status", column = "status"),
        @Result(property = "role", column = "role"),
        @Result(property = "createTime", column = "create_time")
    })
    User selectById(Integer id);

    @Select("SELECT * FROM user")
    @ResultMap("userResultMap")
    List<User> list();

    int insert(User entity);

    int updateById(User entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT * FROM user WHERE username = #{username}")
    @ResultMap("userResultMap")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    @ResultMap("userResultMap")
    User selectByPhone(String phone);

    @Select("SELECT * FROM user WHERE email = #{email}")
    @ResultMap("userResultMap")
    User selectByEmail(String email);

}