package com.project.platform.mapper;

import com.project.platform.entity.Favorite;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface FavoriteMapper {
    List<Favorite> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM favorite WHERE id = #{id}")
    Favorite selectById(Integer id);

    @Select("SELECT * FROM favorite")
    List<Favorite> list();

    int insert(Favorite entity);

    int deleteFavorite(@Param("typeCode") String typeCode, @Param("associationId") Integer associationId, @Param("userId") Integer userId);

    int updateById(Favorite entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT count(*) FROM favorite WHERE type_code = #{typeCode} and association_id = #{id}")
    int queryFavoriteCount(@Param("typeCode") String typeCode, @Param("id") Integer id);

    @Select("SELECT count(*) FROM favorite WHERE type_code = #{typeCode} and association_id = #{associationId} and user_id = #{userId}")
    int queryIsFavorite(@Param("typeCode") String typeCode, @Param("associationId") Integer associationId, @Param("userId") Integer userId);

    @Select("SELECT * FROM favorite WHERE type_code = #{typeCode} and association_id = #{associationId}")
    List<Favorite> queryFavoriteList(@Param("typeCode") String typeCode, @Param("associationId") Integer associationId);

    @Select("SELECT * FROM favorite WHERE type_code = #{typeCode} and user_id = #{userId}")
    List<Favorite> selectByUserIdAndType(@Param("typeCode") String typeCode, @Param("userId") Integer userId);
}