-- ============================================
-- 修复视图定义和数据问题
-- ============================================

USE tourism_db;

-- 1. 删除旧视图
DROP VIEW IF EXISTS `v_user_scenic_score`;

-- 2. 创建新视图（支持SCENIC和景点两种type_code）
CREATE VIEW `v_user_scenic_score` AS 
SELECT 
    t.user_id,
    t.scenic_id,
    SUM(t.score) AS total_score,
    MAX(t.action_time) AS last_action_time
FROM (
    -- 从收藏表获取评分
    SELECT 
        user_id,
        association_id AS scenic_id,
        3.0 AS score,
        create_time AS action_time
    FROM favorite
    WHERE type_code IN ('SCENIC', '景点')
    
    UNION ALL
    
    -- 从订单表获取评分
    SELECT 
        user_id,
        scenic_id,
        5.0 AS score,
        create_time AS action_time
    FROM order_info
    WHERE status IN ('PAID', 'COMPLETED', '已支付', '已完成')
) t
GROUP BY t.user_id, t.scenic_id;

-- 3. 验证视图数据
SELECT '=== 视图数据验证 ===' AS status;
SELECT COUNT(*) AS total_records FROM v_user_scenic_score;
SELECT COUNT(DISTINCT user_id) AS unique_users FROM v_user_scenic_score;
SELECT COUNT(DISTINCT scenic_id) AS unique_scenics FROM v_user_scenic_score;

-- 4. 查看前10条记录
SELECT * FROM v_user_scenic_score ORDER BY total_score DESC LIMIT 10;

-- 5. 按用户统计
SELECT 
    user_id,
    COUNT(*) AS interaction_count,
    AVG(total_score) AS avg_score
FROM v_user_scenic_score
GROUP BY user_id
ORDER BY interaction_count DESC;
