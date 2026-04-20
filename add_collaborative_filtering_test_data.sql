-- ============================================
-- 协同过滤测试数据
-- 为现有用户添加不同的景点交互，测试个性化推荐
-- ============================================

USE tourism_db;

-- ============================================
-- 用户2 (Александр): 喜欢历史文化类景点
-- ============================================
-- 收藏：莫斯科克里姆林宫、红场、基督救世主大教堂
INSERT INTO favorite (user_id, type_code, association_id, create_time) VALUES
(2, '景点', 2, NOW()),  -- 莫斯科克里姆林宫
(2, '景点', 1, NOW()),  -- 红场
(2, '景点', 3, NOW());  -- 基督救世主大教堂

-- 订单：购买了特列季亚科夫画廊的门票
INSERT INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), '001'), 2, 4, DATE_ADD(NOW(), INTERVAL 7 DAY), 2, 1000.00, '已支付', NOW(), NOW());

-- ============================================
-- 用户3 (Екатерина): 喜欢历史文化和宫殿
-- ============================================
-- 收藏：红场、冬宫、彼得宫
INSERT INTO favorite (user_id, type_code, association_id, create_time) VALUES
(3, '景点', 1, NOW()),  -- 红场
(3, '景点', 5, NOW()),  -- 冬宫
(3, '景点', 6, NOW());  -- 彼得宫

-- 订单：购买了莫斯科克里姆林宫的门票
INSERT INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), '002'), 3, 2, DATE_ADD(NOW(), INTERVAL 10 DAY), 1, 700.00, '已支付', NOW(), NOW());

-- ============================================
-- 用户4 (Дмитрий): 喜欢宫殿和教堂
-- ============================================
-- 收藏：冬宫、伊萨基辅大教堂、特列季亚科夫画廊
INSERT INTO favorite (user_id, type_code, association_id, create_time) VALUES
(4, '景点', 5, NOW()),  -- 冬宫
(4, '景点', 7, NOW()),  -- 伊萨基辅大教堂
(4, '景点', 4, NOW());  -- 特列季亚科夫画廊

-- 订单：购买了彼得宫的门票
INSERT INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), '003'), 4, 6, DATE_ADD(NOW(), INTERVAL 14 DAY), 2, 1800.00, '已完成', NOW(), NOW());

-- ============================================
-- 用户5 (Анастасия): 喜欢自然风光
-- ============================================
-- 收藏：贝加尔湖、间歇泉谷、苏兹达尔克里姆林宫
INSERT INTO favorite (user_id, type_code, association_id, create_time) VALUES
(5, '景点', 11, NOW()),  -- 贝加尔湖
(5, '景点', 12, NOW()),  -- 间歇泉谷
(5, '景点', 9, NOW());   -- 苏兹达尔克里姆林宫

-- 订单：购买了圣三一修道院的门票
INSERT INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), '004'), 5, 10, DATE_ADD(NOW(), INTERVAL 5 DAY), 1, 500.00, '已支付', NOW(), NOW());

-- ============================================
-- 用户6 (Михаил): 喜欢文化艺术
-- ============================================
-- 收藏：马林斯基剧院、特列季亚科夫画廊、冬宫
INSERT INTO favorite (user_id, type_code, association_id, create_time) VALUES
(6, '景点', 8, NOW()),  -- 马林斯基剧院
(6, '景点', 4, NOW()),  -- 特列季亚科夫画廊
(6, '景点', 5, NOW());  -- 冬宫

-- 订单：购买了伊萨基辅大教堂的门票
INSERT INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), '005'), 6, 7, DATE_ADD(NOW(), INTERVAL 3 DAY), 1, 350.00, '已支付', NOW(), NOW());

-- ============================================
-- 验证数据
-- ============================================
SELECT '✅ 测试数据添加完成！' AS '状态';
SELECT '' AS '';

SELECT '📊 用户交互统计（v_user_scenic_score视图）:' AS '';
SELECT 
    user_id AS '用户ID',
    COUNT(*) AS '交互次数',
    SUM(total_score) AS '总评分',
    GROUP_CONCAT(scenic_id ORDER BY total_score DESC SEPARATOR ', ') AS '景点ID列表'
FROM v_user_scenic_score
GROUP BY user_id
ORDER BY user_id;

SELECT '' AS '';
SELECT '🎯 预期推荐结果:' AS '';
SELECT '用户2 (Александр): 应推荐景点5,6,7 (因为用户3,4也喜欢1,2,且他们还喜欢5,6,7)' AS '说明';
SELECT '用户3 (Екатерина): 应推荐景点3,4,7 (来自相似用户2,4)' AS '说明';
SELECT '用户4 (Дмитрий): 应推荐景点1,2,3 (来自相似用户3,6)' AS '说明';
SELECT '用户5 (Анастасия): 可能推荐其他景点或空列表（偏好独特）' AS '说明';
SELECT '用户6 (Михаил): 应推荐景点1,2,6 (来自相似用户4)' AS '说明';

SELECT '' AS '';
SELECT '🔍 景点被交互统计:' AS '';
SELECT 
    scenic_id AS '景点ID',
    COUNT(DISTINCT user_id) AS '用户数',
    GROUP_CONCAT(DISTINCT user_id ORDER BY user_id SEPARATOR ', ') AS '用户列表'
FROM v_user_scenic_score
GROUP BY scenic_id
ORDER BY COUNT(DISTINCT user_id) DESC;

SELECT '' AS '';
SELECT '✨ 下一步操作:' AS '';
SELECT '1. 重启后端服务' AS '步骤';
SELECT '2. 用用户2 (Александр) 登录，用户名: Александр, 密码: 123456' AS '步骤';
SELECT '3. 查看首页推荐景点' AS '步骤';
SELECT '4. 查看后端日志，应该看到协同过滤计算过程' AS '步骤';
SELECT '5. 切换到用户3 (Екатерина) 登录，对比推荐结果' AS '步骤';
