package com.project.platform.mapper;

import com.project.platform.entity.Likes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface LikesMapper {
    List<Likes> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM likes WHERE id = #{id}")
    Likes selectById(Integer id);

    @Select("SELECT * FROM likes")
    List<Likes> list();

    int insert(Likes entity);

    int deleteByUserId(Likes entity);

    int updateById(Likes entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT count(*) FROM likes WHERE type_code = #{typeCode} and association_id = #{id}")
    int queryLikeCount(String typeCode,Integer id);

    @Select("SELECT * FROM likes WHERE type_code = #{typeCode} and association_id = #{id}")
    List<Likes> queryLikeList(String typeCode,Integer id);

    @Select("SELECT count(*) FROM likes WHERE type_code = #{typeCode} and user_id = #{userId} and association_id = #{id}")
    int queryIsLike(String typeCode,Integer id,Integer userId);

}