package com.project.platform.controller;

import com.project.platform.entity.ScenicInfo;
import com.project.platform.service.CollaborativeFilteringService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.dto.CurrentUserDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推荐系统管理接口
 */
@RestController
@RequestMapping("/recommendation")
@Slf4j
public class RecommendationController {
    
    @Resource
    private CollaborativeFilteringService collaborativeFilteringService;
    
    @Resource
    private com.project.platform.service.ScenicCategoryService scenicCategoryService;
    
    @Resource
    private com.project.platform.mapper.CommentInfoMapper commentInfoMapper;
    
    @Resource
    private com.project.platform.mapper.FavoriteMapper favoriteMapper;
    
    /**
     * 获取当前用户的个性化推荐
     */
    @GetMapping("/forCurrentUser")
    public List<ScenicInfo> getRecommendationsForCurrentUser(@RequestParam(defaultValue = "10") Integer limit) {
        CurrentUserDTO user = CurrentUserThreadLocal.getCurrentUser();
        if (user == null) {
            log.warn("Пользователь не авторизован, возвращаем пустые рекомендации");
            return List.of();
        }
        
        try {
            List<ScenicInfo> recommendations = collaborativeFilteringService.recommendForUser(user.getId(), limit);
            log.info("为用户{}返回{}个推荐景点", user.getId(), recommendations.size());
            
            // 填充额外信息（分类名称、评论数、收藏数）
            enrichScenicInfo(recommendations);
            
            return recommendations;
        } catch (Exception e) {
            log.error("Ошибка получения рекомендаций", e);
            return List.of();
        }
    }
    
    /**
     * 获取指定用户的推荐（用于测试）
     */
    @GetMapping("/forUser")
    public List<ScenicInfo> getRecommendationsForUser(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<ScenicInfo> recommendations = collaborativeFilteringService.recommendForUser(userId, limit);
            log.info("为用户{}返回{}个推荐景点", userId, recommendations.size());
            
            // 填充额外信息
            enrichScenicInfo(recommendations);
            
            return recommendations;
        } catch (Exception e) {
            log.error("Ошибка получения рекомендаций", e);
            return List.of();
        }
    }
    
    /**
     * 填充景点的额外信息
     */
    private void enrichScenicInfo(List<ScenicInfo> scenicList) {
        if (scenicList == null || scenicList.isEmpty()) {
            return;
        }
        
        // 批量查询所有分类
        List<com.project.platform.entity.ScenicCategory> allCategories = scenicCategoryService.list();
        Map<Integer, String> categoryMap = allCategories.stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.project.platform.entity.ScenicCategory::getId,
                        com.project.platform.entity.ScenicCategory::getName
                ));
        
        // 填充每个景点的信息
        for (ScenicInfo scenic : scenicList) {
            // 设置分类名称
            String categoryName = categoryMap.get(scenic.getCategoryId());
            if (categoryName != null) {
                scenic.setCategoryType(categoryName);
            }
            
            // 查询评论数
            int commentCount = commentInfoMapper.queryCommentsCount("景点", scenic.getId());
            scenic.setCountComment(commentCount);
            
            // 查询收藏数
            int favoriteCount = favoriteMapper.queryFavoriteCount("景点", scenic.getId());
            scenic.setCountFavorite(favoriteCount);
        }
    }
    
    /**
     * 获取当前用户的个性化推荐（旧接口，保留兼容性）
     */
    @GetMapping("/forMe")
    public Map<String, Object> getRecommendationsForMe(@RequestParam(defaultValue = "10") Integer topN) {
        Map<String, Object> result = new HashMap<>();
        
        CurrentUserDTO user = CurrentUserThreadLocal.getCurrentUser();
        if (user == null) {
            result.put("success", false);
            result.put("message", "Пожалуйста, войдите в систему");
            return result;
        }
        
        try {
            List<ScenicInfo> recommendations = collaborativeFilteringService.recommendForUser(user.getId(), topN);
            result.put("success", true);
            result.put("data", recommendations);
            result.put("total", recommendations.size());
        } catch (Exception e) {
            log.error("Ошибка получения рекомендаций", e);
            result.put("success", false);
            result.put("message", "Ошибка получения рекомендаций: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 计算两个景点的相似度（用于测试）
     */
    @GetMapping("/similarity")
    public Map<String, Object> getSimilarity(@RequestParam Integer scenicId1, @RequestParam Integer scenicId2) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            double similarity = collaborativeFilteringService.calculateSimilarity(scenicId1, scenicId2);
            result.put("success", true);
            result.put("scenicId1", scenicId1);
            result.put("scenicId2", scenicId2);
            result.put("similarity", similarity);
        } catch (Exception e) {
            log.error("Ошибка расчёта схожести", e);
            result.put("success", false);
            result.put("message", "Ошибка расчёта: " + e.getMessage());
        }
        
        return result;
    }
}
