package com.project.platform.mapper;

import com.project.platform.entity.ViewHistory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface ViewHistoryMapper {
    List<ViewHistory> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM view_history WHERE id = #{id}")
    ViewHistory selectById(Integer id);

    @Select("SELECT * FROM view_history")
    List<ViewHistory> list();

    int insert(ViewHistory entity);

    int updateById(ViewHistory entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT count(*) FROM view_history WHERE type_code = #{typeCode} and association_id = #{id}")
    int queryViewCount(String typeCode,Integer id);

    @Select("SELECT * FROM view_history WHERE type_code = #{typeCode} and association_id = #{id}")
    List<ViewHistory> queryViewList(String typeCode,Integer id);


}