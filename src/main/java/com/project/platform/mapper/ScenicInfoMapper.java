package com.project.platform.mapper;

import com.project.platform.entity.ScenicInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface ScenicInfoMapper {
    List<ScenicInfo> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM scenic_info WHERE id = #{id}")
    ScenicInfo selectById(Integer id);

    @Select("SELECT COUNT(*) FROM scenic_info WHERE category_id = #{categoryId}")
    int countByCategoryId(Integer categoryId);

    @Select("SELECT * FROM scenic_info")
    List<ScenicInfo> list();

    int insert(ScenicInfo entity);

    int updateById(ScenicInfo entity);

    boolean removeByIds(List<Integer> ids);

}