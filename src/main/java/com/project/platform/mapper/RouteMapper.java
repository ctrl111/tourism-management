package com.project.platform.mapper;


import com.project.platform.entity.Route;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface RouteMapper{
    List<Route> queryPage(Integer offset, Integer pageSize, Map<String, Object> query);

    @Select("SELECT * FROM route WHERE id = #{id}")
    Route selectById(Integer id);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM route ORDER BY id DESC")
    List<Route> list();
}
