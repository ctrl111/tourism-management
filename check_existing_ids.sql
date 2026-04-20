-- 检查数据库中实际存在的用户和景点ID

USE tourism_db;

-- 查看所有用户
SELECT '=== 现有用户 ===' AS info;
SELECT id, username, name, role FROM user ORDER BY id LIMIT 20;

-- 查看所有景点
SELECT '=== 现有景点 ===' AS info;
SELECT id, name, category_id, price FROM scenic_info ORDER BY id LIMIT 20;

-- 查看现有的收藏数据
SELECT '=== 现有收藏数据 ===' AS info;
SELECT id, user_id, type_code, association_id, create_time 
FROM favorite 
WHERE type_code = '景点'
ORDER BY create_time DESC 
LIMIT 10;

-- 查看现有的订单数据
SELECT '=== 现有订单数据 ===' AS info;
SELECT id, order_no, user_id, scenic_id, status, create_time 
FROM order_info 
ORDER BY create_time DESC 
LIMIT 10;

-- 查看v_user_scenic_score视图数据
SELECT '=== v_user_scenic_score 视图数据 ===' AS info;
SELECT * FROM v_user_scenic_score ORDER BY user_id, total_score DESC LIMIT 20;
