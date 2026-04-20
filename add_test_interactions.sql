-- 添加测试用户交互数据以验证协同过滤算法

USE tourism_db;

-- 假设已有用户ID: 1, 2, 3, 4, 5
-- 假设已有景点ID: 1-10

-- 清理可能存在的测试数据（可选）
-- DELETE FROM favorite WHERE user_id IN (1,2,3,4,5) AND type_code = '景点';

-- 用户1：喜欢自然风光类景点（景点1,2,3）
INSERT IGNORE INTO favorite (user_id, type_code, association_id, create_time) VALUES
(1, '景点', 1, NOW()),
(1, '景点', 2, NOW()),
(1, '景点', 3, NOW());

-- 用户2：喜欢自然风光和历史文化（景点1,2,4,5）
INSERT IGNORE INTO favorite (user_id, type_code, association_id, create_time) VALUES
(2, '景点', 1, NOW()),
(2, '景点', 2, NOW()),
(2, '景点', 4, NOW()),
(2, '景点', 5, NOW());

-- 用户3：喜欢历史文化类景点（景点4,5,6）
INSERT IGNORE INTO favorite (user_id, type_code, association_id, create_time) VALUES
(3, '景点', 4, NOW()),
(3, '景点', 5, NOW()),
(3, '景点', 6, NOW());

-- 用户4：喜欢现代娱乐类景点（景点7,8,9）
INSERT IGNORE INTO favorite (user_id, type_code, association_id, create_time) VALUES
(4, '景点', 7, NOW()),
(4, '景点', 8, NOW()),
(4, '景点', 9, NOW());

-- 用户5：喜欢多样化（景点1,4,7,10）
INSERT IGNORE INTO favorite (user_id, type_code, association_id, create_time) VALUES
(5, '景点', 1, NOW()),
(5, '景点', 4, NOW()),
(5, '景点', 7, NOW()),
(5, '景点', 10, NOW());

-- 添加一些订单数据（更高权重）
-- 用户1购买了景点1的门票
INSERT IGNORE INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('TEST', UNIX_TIMESTAMP(), '001'), 1, 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 2, 200, '已支付', NOW(), NOW());

-- 用户2购买了景点2的门票
INSERT IGNORE INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('TEST', UNIX_TIMESTAMP(), '002'), 2, 2, DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 100, '已支付', NOW(), NOW());

-- 用户3购买了景点5的门票
INSERT IGNORE INTO order_info (order_no, user_id, scenic_id, visit_date, number, total_price, status, create_time, update_time) VALUES
(CONCAT('TEST', UNIX_TIMESTAMP(), '003'), 3, 5, DATE_ADD(NOW(), INTERVAL 7 DAY), 3, 300, '已完成', NOW(), NOW());

SELECT '✓ 测试交互数据添加完成' AS result;

-- 验证数据
SELECT '=== 验证：用户交互统计 ===' AS info;
SELECT 
    user_id,
    COUNT(*) as interaction_count,
    GROUP_CONCAT(scenic_id ORDER BY scenic_id) as scenic_ids
FROM v_user_scenic_score
GROUP BY user_id
ORDER BY user_id;

SELECT '=== 验证：景点被交互统计 ===' AS info;
SELECT 
    scenic_id,
    COUNT(DISTINCT user_id) as user_count,
    GROUP_CONCAT(DISTINCT user_id ORDER BY user_id) as user_ids
FROM v_user_scenic_score
GROUP BY scenic_id
ORDER BY user_count DESC;
