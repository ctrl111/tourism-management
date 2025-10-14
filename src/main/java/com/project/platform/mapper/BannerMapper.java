package com.project.platform.mapper;

import com.project.platform.entity.Banner;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface BannerMapper {
    List<Banner> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM banner WHERE id = #{id}")
    Banner selectById(Integer id);

    @Select("SELECT * FROM banner")
    List<Banner> list();

    int insert(Banner entity);

    int updateById(Banner entity);

    boolean removeByIds(List<Integer> ids);

}