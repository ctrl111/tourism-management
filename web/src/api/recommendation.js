import http from '@/utils/http'

/**
 * 获取当前用户的个性化推荐
 * @param {number} topN - 推荐数量，默认10
 */
export function getRecommendationsForMe(topN = 10) {
  return http({
    url: '/api/recommendation/forMe',
    method: 'get',
    params: { topN }
  })
}

/**
 * 计算两个景点的相似度（用于测试）
 * @param {number} scenicId1 - 景点1的ID
 * @param {number} scenicId2 - 景点2的ID
 */
export function getSimilarity(scenicId1, scenicId2) {
  return http({
    url: '/api/recommendation/similarity',
    method: 'get',
    params: { scenicId1, scenicId2 }
  })
}
