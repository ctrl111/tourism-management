/*
 数据库优化验证脚本
 
 用途: 验证数据库优化是否成功执行
 执行方式: mysql -u root -p templatev3_s < verify_optimization.sql
*/

USE templatev3_s;

SELECT '========================================' AS '';
SELECT '数据库优化验证报告' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';

-- 1. 检查表数量
SELECT '1️⃣  表数量检查' AS '';
SELECT COUNT(*) AS '当前表数量（预期12个）' 
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s';
SELECT '' AS '';

-- 2. 检查已删除的表
SELECT '2️⃣  已删除表检查' AS '';

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ route 表已删除'
        ELSE '❌ route 表仍然存在'
    END AS '检查结果'
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'route'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ route_days 表已删除'
        ELSE '❌ route_days 表仍然存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'route_days'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ scenic_comment 表已删除'
        ELSE '❌ scenic_comment 表仍然存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'scenic_comment'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ order_item 表已删除'
        ELSE '❌ order_item 表仍然存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'order_item';

SELECT '' AS '';

-- 3. 检查保留的核心表
SELECT '3️⃣  核心表检查（应该全部存在）' AS '';

SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ admin'
        ELSE '❌ admin 表不存在'
    END AS '检查结果'
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'admin'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ user'
        ELSE '❌ user 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'user'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ scenic_category'
        ELSE '❌ scenic_category 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'scenic_category'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ scenic_info'
        ELSE '❌ scenic_info 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'scenic_info'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ comment_info'
        ELSE '❌ comment_info 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'comment_info'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ travel_note'
        ELSE '❌ travel_note 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'travel_note'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ order_info'
        ELSE '❌ order_info 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'order_info'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ likes'
        ELSE '❌ likes 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'likes'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ favorite'
        ELSE '❌ favorite 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'favorite'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ view_history'
        ELSE '❌ view_history 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'view_history'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ banner'
        ELSE '❌ banner 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'banner'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ notice'
        ELSE '❌ notice 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'notice';

SELECT '' AS '';

-- 4. 检查order_info表的新字段
SELECT '4️⃣  order_info表字段检查' AS '';

SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ scenic_id 字段存在'
        ELSE '❌ scenic_id 字段不存在'
    END AS '检查结果'
FROM information_schema.columns 
WHERE table_schema = 'templatev3_s' 
  AND table_name = 'order_info' 
  AND column_name = 'scenic_id'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ unit_price 字段存在'
        ELSE '❌ unit_price 字段不存在'
    END
FROM information_schema.columns 
WHERE table_schema = 'templatev3_s' 
  AND table_name = 'order_info' 
  AND column_name = 'unit_price'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ scenic_name 字段存在'
        ELSE '❌ scenic_name 字段不存在'
    END
FROM information_schema.columns 
WHERE table_schema = 'templatev3_s' 
  AND table_name = 'order_info' 
  AND column_name = 'scenic_name'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ quantity 字段存在'
        ELSE '❌ quantity 字段不存在'
    END
FROM information_schema.columns 
WHERE table_schema = 'templatev3_s' 
  AND table_name = 'order_info' 
  AND column_name = 'quantity'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✅ visit_date 字段存在'
        ELSE '❌ visit_date 字段不存在'
    END
FROM information_schema.columns 
WHERE table_schema = 'templatev3_s' 
  AND table_name = 'order_info' 
  AND column_name = 'visit_date';

SELECT '' AS '';

-- 5. 显示所有表列表
SELECT '5️⃣  当前所有表列表' AS '';
SHOW TABLES;

SELECT '' AS '';
SELECT '========================================' AS '';
SELECT '验证完成！' AS '';
SELECT '========================================' AS '';
