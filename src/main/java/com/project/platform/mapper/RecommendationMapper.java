package com.project.platform.mapper;

import com.project.platform.dto.UserScenicScoreDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RecommendationMapper {
    
    /**
     * 获取所有用户对景点的评分
     */
    @Select("SELECT user_id, scenic_id, total_score, last_action_time FROM v_user_scenic_score")
    List<UserScenicScoreDTO> getAllUserScenicScores();
    
    /**
     * 获取指定景点的所有用户评分
     */
    @Select("SELECT user_id, scenic_id, total_score, last_action_time " +
            "FROM v_user_scenic_score " +
            "WHERE scenic_id = #{scenicId}")
    List<UserScenicScoreDTO> getUserScoresByScenic(@Param("scenicId") Integer scenicId);
    
    /**
     * 获取指定用户的所有景点评分
     */
    @Select("SELECT user_id, scenic_id, total_score, last_action_time " +
            "FROM v_user_scenic_score " +
            "WHERE user_id = #{userId} " +
            "ORDER BY total_score DESC, last_action_time DESC")
    List<UserScenicScoreDTO> getScenicScoresByUser(@Param("userId") Integer userId);
    
    /**
     * 获取用户交互过的景点ID列表
     */
    @Select("SELECT DISTINCT scenic_id FROM v_user_scenic_score WHERE user_id = #{userId}")
    List<Integer> getUserInteractedScenicIds(@Param("userId") Integer userId);
}
