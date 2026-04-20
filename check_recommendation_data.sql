-- 检查推荐系统数据

-- 1. 查看用户-景点评分视图数据
SELECT '=== 用户-景点评分数据 ===' AS info;
SELECT 
    user_id,
    scenic_id,
    total_score,
    last_action_time
FROM v_user_scenic_score
ORDER BY user_id, total_score DESC
LIMIT 20;

-- 2. 统计每个用户的交互数量
SELECT '=== 每个用户的交互数量 ===' AS info;
SELECT 
    user_id,
    COUNT(*) as interaction_count,
    SUM(total_score) as total_score
FROM v_user_scenic_score
GROUP BY user_id
ORDER BY interaction_count DESC;

-- 3. 统计每个景点被交互的次数
SELECT '=== 每个景点被交互的次数 ===' AS info;
SELECT 
    scenic_id,
    COUNT(*) as user_count,
    AVG(total_score) as avg_score
FROM v_user_scenic_score
GROUP BY scenic_id
ORDER BY user_count DESC
LIMIT 10;

-- 4. 查看收藏数据
SELECT '=== 景点收藏数据 ===' AS info;
SELECT 
    type_code,
    association_id as scenic_id,
    COUNT(*) as favorite_count
FROM favorite
WHERE type_code = '景点'
GROUP BY association_id
ORDER BY favorite_count DESC
LIMIT 10;

-- 5. 查看订单数据
SELECT '=== 景点订单数据 ===' AS info;
SELECT 
    scenic_id,
    status,
    COUNT(*) as order_count
FROM order_info
GROUP BY scenic_id, status
ORDER BY order_count DESC
LIMIT 10;

-- 6. 检查是否有足够的交叉数据（多个用户喜欢同一景点）
SELECT '=== 交叉数据检查 ===' AS info;
SELECT 
    scenic_id,
    GROUP_CONCAT(user_id) as users,
    COUNT(DISTINCT user_id) as user_count
FROM v_user_scenic_score
GROUP BY scenic_id
HAVING user_count > 1
ORDER BY user_count DESC
LIMIT 10;
