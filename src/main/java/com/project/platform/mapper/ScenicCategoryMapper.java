package com.project.platform.mapper;

import com.project.platform.entity.ScenicCategory;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface ScenicCategoryMapper {
    List<ScenicCategory> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM scenic_category WHERE id = #{id}")
    ScenicCategory selectById(Integer id);

    @Select("SELECT * FROM scenic_category")
    List<ScenicCategory> list();

    int insert(ScenicCategory entity);

    int updateById(ScenicCategory entity);

    boolean removeByIds(List<Integer> ids);

}