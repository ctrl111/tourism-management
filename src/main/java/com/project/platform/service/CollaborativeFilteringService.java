package com.project.platform.service;

import com.project.platform.entity.ScenicInfo;
import java.util.List;

/**
 * 协同过滤推荐服务接口（简化版-实时计算）
 */
public interface CollaborativeFilteringService {
    
    /**
     * 为用户推荐景点（基于物品的协同过滤，实时计算）
     * @param userId 用户ID
     * @param topN 推荐数量
     * @return 推荐的景点列表
     */
    List<ScenicInfo> recommendForUser(Integer userId, int topN);
    
    /**
     * 计算两个景点之间的相似度（实时计算）
     * @param scenicId1 景点1的ID
     * @param scenicId2 景点2的ID
     * @return 相似度分数(0-1)
     */
    double calculateSimilarity(Integer scenicId1, Integer scenicId2);
}
