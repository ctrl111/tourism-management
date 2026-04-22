package com.project.platform.service.impl;

import com.project.platform.dto.UserScenicScoreDTO;
import com.project.platform.entity.ScenicInfo;
import com.project.platform.mapper.RecommendationMapper;
import com.project.platform.mapper.ScenicInfoMapper;
import com.project.platform.service.CollaborativeFilteringService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 简化版协同过滤推荐服务（适合毕业项目）
 * 特点：实时计算，不需要缓存表，代码简单易懂
 */
@Service
public class SimpleCollaborativeFilteringServiceImpl implements CollaborativeFilteringService {
    
    @Resource
    private RecommendationMapper recommendationMapper;
    
    @Resource
    private ScenicInfoMapper scenicInfoMapper;
    
    /**
     * 为用户推荐景点（基于用户的协同过滤）
     */
    @Override
    public List<ScenicInfo> recommendForUser(Integer userId, int topN) {
        List<ScenicInfo> recommendations = new ArrayList<>();
        
        // 1. 获取当前用户交互过的景点及评分
        List<UserScenicScoreDTO> currentUserScores = recommendationMapper.getScenicScoresByUser(userId);
        if (currentUserScores.isEmpty()) {
            return getHotScenics(topN);
        }
        
        // 2. 获取当前用户已交互的景点ID（用于过滤）
        Set<Integer> interactedScenicIds = currentUserScores.stream()
                .map(UserScenicScoreDTO::getScenicId)
                .collect(Collectors.toSet());
        
        // 3. 获取所有用户的评分数据
        List<UserScenicScoreDTO> allScores = recommendationMapper.getAllUserScenicScores();
        
        // 4. 构建用户-景点评分矩阵
        Map<Integer, Map<Integer, Double>> userScenicMatrix = new HashMap<>();
        for (UserScenicScoreDTO score : allScores) {
            userScenicMatrix
                    .computeIfAbsent(score.getUserId(), k -> new HashMap<>())
                    .put(score.getScenicId(), score.getTotalScore());
        }
        
        // 5. 计算当前用户与其他用户的相似度
        Map<Integer, Double> userSimilarities = new HashMap<>();
        Map<Integer, Double> currentUserVector = userScenicMatrix.get(userId);
        
        if (currentUserVector == null) {
            return getHotScenics(topN);
        }
        
        for (Map.Entry<Integer, Map<Integer, Double>> entry : userScenicMatrix.entrySet()) {
            Integer otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) {
                continue;
            }
            
            Map<Integer, Double> otherUserVector = entry.getValue();
            double similarity = calculateCosineSimilarity(currentUserVector, otherUserVector);
            
            if (similarity > 0.0) {
                userSimilarities.put(otherUserId, similarity);
            }
        }
        
        if (userSimilarities.isEmpty()) {
            return getHotScenics(topN);
        }
        
        // 6. 基于相似用户推荐景点
        Map<Integer, Double> candidateScores = new HashMap<>();
        
        for (Map.Entry<Integer, Double> simEntry : userSimilarities.entrySet()) {
            Integer similarUserId = simEntry.getKey();
            Double similarity = simEntry.getValue();
            
            Map<Integer, Double> similarUserScenics = userScenicMatrix.get(similarUserId);
            
            for (Map.Entry<Integer, Double> scenicEntry : similarUserScenics.entrySet()) {
                Integer scenicId = scenicEntry.getKey();
                Double rating = scenicEntry.getValue();
                
                if (interactedScenicIds.contains(scenicId)) {
                    continue;
                }
                
                double score = similarity * rating;
                candidateScores.merge(scenicId, score, Double::sum);
            }
        }
        
        // 7. 获取协同过滤推荐（取一半）
        int cfCount = Math.min(topN / 2, candidateScores.size());
        if (cfCount > 0) {
            List<Integer> cfRecommendedIds = candidateScores.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                    .limit(cfCount)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            
            List<ScenicInfo> cfScenics = cfRecommendedIds.stream()
                    .map(scenicInfoMapper::selectById)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            
            recommendations.addAll(cfScenics);
        }
        
        // 8. 补充热门景点（另一半）
        int hotCount = topN - recommendations.size();
        if (hotCount > 0) {
            List<ScenicInfo> hotScenics = getHotScenics(hotCount * 2);
            
            Set<Integer> recommendedIds = recommendations.stream()
                    .map(ScenicInfo::getId)
                    .collect(Collectors.toSet());
            
            List<ScenicInfo> filteredHotScenics = hotScenics.stream()
                    .filter(s -> !recommendedIds.contains(s.getId()) && !interactedScenicIds.contains(s.getId()))
                    .limit(hotCount)
                    .collect(Collectors.toList());
            
            recommendations.addAll(filteredHotScenics);
        }
        
        return recommendations;
    }
    
    /**
     * 获取热门景点（当没有推荐结果时的备选方案）
     * 按收藏数和评论数排序
     */
    private List<ScenicInfo> getHotScenics(int topN) {
        List<ScenicInfo> allScenics = scenicInfoMapper.list();
        
        // 按热度排序：收藏数 + 评论数
        return allScenics.stream()
                .sorted((s1, s2) -> {
                    int hot1 = (s1.getCountFavorite() != null ? s1.getCountFavorite() : 0) 
                             + (s1.getCountComment() != null ? s1.getCountComment() : 0);
                    int hot2 = (s2.getCountFavorite() != null ? s2.getCountFavorite() : 0) 
                             + (s2.getCountComment() != null ? s2.getCountComment() : 0);
                    return Integer.compare(hot2, hot1); // 降序
                })
                .limit(topN)
                .collect(Collectors.toList());
    }
    
    /**
     * 实时计算两个景点的相似度
     */
    @Override
    public double calculateSimilarity(Integer scenicId1, Integer scenicId2) {
        return calculateSimilarityRealtime(scenicId1, scenicId2);
    }
    
    /**
     * 实时计算相似度（使用余弦相似度）
     */
    private double calculateSimilarityRealtime(Integer scenicId1, Integer scenicId2) {
        // 获取两个景点的用户评分
        List<UserScenicScoreDTO> users1 = recommendationMapper.getUserScoresByScenic(scenicId1);
        List<UserScenicScoreDTO> users2 = recommendationMapper.getUserScoresByScenic(scenicId2);
        
        if (users1.isEmpty() || users2.isEmpty()) {
            return 0.0;
        }
        
        // 转换为Map方便查找
        Map<Integer, Double> map1 = users1.stream()
                .collect(Collectors.toMap(UserScenicScoreDTO::getUserId, UserScenicScoreDTO::getTotalScore));
        Map<Integer, Double> map2 = users2.stream()
                .collect(Collectors.toMap(UserScenicScoreDTO::getUserId, UserScenicScoreDTO::getTotalScore));
        
        // 计算余弦相似度
        return calculateCosineSimilarity(map1, map2);
    }
    
    /**
     * 计算余弦相似度
     */
    private double calculateCosineSimilarity(Map<Integer, Double> vector1, Map<Integer, Double> vector2) {
        // 找到共同用户
        Set<Integer> commonUsers = new HashSet<>(vector1.keySet());
        commonUsers.retainAll(vector2.keySet());
        
        if (commonUsers.isEmpty()) {
            return 0.0;
        }
        
        // 计算点积
        double dotProduct = 0.0;
        for (Integer userId : commonUsers) {
            dotProduct += vector1.get(userId) * vector2.get(userId);
        }
        
        // 计算向量模
        double norm1 = Math.sqrt(vector1.values().stream()
                .mapToDouble(v -> v * v)
                .sum());
        double norm2 = Math.sqrt(vector2.values().stream()
                .mapToDouble(v -> v * v)
                .sum());
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        // 余弦相似度
        double similarity = dotProduct / (norm1 * norm2);
        
        // 四舍五入到4位小数
        return Math.round(similarity * 10000.0) / 10000.0;
    }
}
