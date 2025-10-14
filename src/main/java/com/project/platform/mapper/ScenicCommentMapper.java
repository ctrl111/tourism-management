package com.project.platform.mapper;

import com.project.platform.entity.ScenicComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;


public interface ScenicCommentMapper {
    List<ScenicComment> queryPage(Integer offset, Integer pageSize, @Param("query") Map<String, Object> query);

    int queryCount(@Param("query") Map<String, Object> query);

    @Select("SELECT * FROM scenic_comment WHERE id = #{id}")
    ScenicComment selectById(Integer id);

    @Select("SELECT * FROM scenic_comment")
    List<ScenicComment> list();

    int insert(ScenicComment entity);

    int updateById(ScenicComment entity);

    boolean removeByIds(List<Integer> ids);

    @Select("SELECT * FROM scenic_comment WHERE scenic_id = #{id}  order by create_time desc")
    List<ScenicComment> queryCommentsListByTop(Integer id);

    @Select("SELECT score FROM scenic_comment WHERE scenic_id = #{id}  order by create_time desc")
    List<Double> queryRatingsByScenicId(Integer id);

    @Select("SELECT * FROM scenic_comment WHERE scenic_id = #{id}  order by create_time desc")
    List<ScenicComment> queryCommentsListByChild(Integer id,Integer parentId);

    @Select("SELECT count(*) FROM scenic_comment WHERE scenic_id = #{id}")
    int selectByScenicId (Integer id);

    @Select("SELECT * FROM scenic_comment WHERE scenic_id = #{id}")
    List<ScenicComment> selectByScenicsId (Integer id);

}