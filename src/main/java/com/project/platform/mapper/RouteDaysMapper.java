package com.project.platform.mapper;

import com.project.platform.entity.RouteDays;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface RouteDaysMapper {
    List<RouteDays> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM route_days WHERE id = #{id}")
    RouteDays selectById(Integer id);

    @Select("SELECT * FROM route_days")
    List<RouteDays> list();

    int insert(RouteDays entity);

    int updateById(RouteDays entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT * FROM route_days WHERE route_id = #{id} ORDER BY day_number ASC")
    List<RouteDays> queryByRouteId(Integer id);
}