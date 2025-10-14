package com.project.platform.mapper;

import com.project.platform.entity.Notice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface NoticeMapper {
    List<Notice> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM notice WHERE id = #{id}")
    Notice selectById(Integer id);

    @Select("SELECT * FROM notice")
    List<Notice> list();

    int insert(Notice entity);

    int updateById(Notice entity);

    boolean removeByIds(List<Integer> ids);

}