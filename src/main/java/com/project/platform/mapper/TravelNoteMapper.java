package com.project.platform.mapper;

import com.project.platform.entity.TravelNote;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface TravelNoteMapper {
    List<TravelNote> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM travel_note WHERE id = #{id}")
    TravelNote selectById(Integer id);

    @Select("SELECT * FROM travel_note")
    List<TravelNote> list();

    int insert(TravelNote entity);

    int updateById(TravelNote entity);

    boolean removeByIds(List<Integer> ids);

}