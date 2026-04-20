package com.project.platform.mapper;

import com.project.platform.entity.CommentInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface CommentInfoMapper {
    List<CommentInfo> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM comment_info WHERE id = #{id}")
    CommentInfo selectById(Integer id);

    @Select("SELECT * FROM comment_info")
    List<CommentInfo> list();

    int insert(CommentInfo entity);

    int updateById(CommentInfo entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT count(*) FROM comment_info WHERE type_code = #{typeCode} and association_id = #{id}")
    int queryCommentsCount(String typeCode,Integer id);

    @Select("SELECT * FROM comment_info WHERE type_code = #{typeCode} and association_id = #{id}")
    List<CommentInfo> queryCommentsList(String typeCode,Integer id);


    @Select("SELECT * FROM comment_info WHERE association_id = #{id} and parent_id = #{parentId} order by create_time desc")
    List<CommentInfo> queryCommentsListByChild(String typeCode,Integer id, Integer parentId);

    @Select("SELECT * FROM comment_info WHERE parent_id = #{parentId}")
    List<CommentInfo> queryCommentsByParentId(Integer parentId);

    Map<String, Object> getStatistics();

}