/*
 ========================================
 数据库优化主执行脚本
 ========================================
 
 执行日期: 2026-03-02
 目的: 完成项目简化优化的数据库更新
 
 ⚠️ 重要提示：
 1. 执行前请确保已备份数据库！
 2. 建议先在测试环境执行
 3. 确认无误后再在生产环境执行
 
 执行方式：
 方式1: 在MySQL客户端中执行
   mysql -u root -p templatev3_s < execute_optimization.sql
 
 方式2: 在MySQL Workbench中打开并执行
 
 方式3: 使用Navicat等工具执行
*/

USE templatev3_s;

-- ========================================
-- 步骤1: 显示优化前的表列表
-- ========================================
SELECT '========== 优化前的表列表 ==========' AS '';
SHOW TABLES;

SELECT '========== 表数量统计 ==========' AS '';
SELECT COUNT(*) AS '优化前表数量' FROM information_schema.tables 
WHERE table_schema = 'templatev3_s';

-- ========================================
-- 步骤2: 删除不需要的表
-- ========================================
SELECT '========== 开始删除表 ==========' AS '';

SET FOREIGN_KEY_CHECKS = 0;

-- 删除路线攻略相关表
DROP TABLE IF EXISTS `route_days`;
SELECT '✓ 已删除 route_days 表' AS '';

DROP TABLE IF EXISTS `route`;
SELECT '✓ 已删除 route 表' AS '';

-- 删除景点评论表（使用统一评论表）
DROP TABLE IF EXISTS `scenic_comment`;
SELECT '✓ 已删除 scenic_comment 表' AS '';

-- 删除订单详情表（简化订单系统）
DROP TABLE IF EXISTS `order_item`;
SELECT '✓ 已删除 order_item 表' AS '';

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 步骤3: 优化订单表结构
-- ========================================
SELECT '========== 优化订单表结构 ==========' AS '';

-- 检查并添加scenic_id字段
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.columns 
WHERE table_schema = 'templatev3_s' 
  AND table_name = 'order_info' 
  AND column_name = 'scenic_id';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `order_info` ADD COLUMN `scenic_id` INT NULL COMMENT ''景点ID'' AFTER `user_id`',
    'SELECT ''scenic_id字段已存在'' AS ''''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加unit_price字段
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.columns 
WHERE table_schema = 'templatev3_s' 
  AND table_name = 'order_info' 
  AND column_name = 'unit_price';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE `order_info` ADD COLUMN `unit_price` DECIMAL(10,2) NULL COMMENT ''单价'' AFTER `quantity`',
    'SELECT ''unit_price字段已存在'' AS ''''');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SELECT '✓ 订单表结构优化完成' AS '';

-- ========================================
-- 步骤4: 显示优化后的表列表
-- ========================================
SELECT '========== 优化后的表列表 ==========' AS '';
SHOW TABLES;

SELECT '========== 表数量统计 ==========' AS '';
SELECT COUNT(*) AS '优化后表数量' FROM information_schema.tables 
WHERE table_schema = 'templatev3_s';

-- ========================================
-- 步骤5: 验证核心表是否存在
-- ========================================
SELECT '========== 验证核心表 ==========' AS '';

SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ admin 表存在'
        ELSE '✗ admin 表不存在'
    END AS '检查结果'
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'admin'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ user 表存在'
        ELSE '✗ user 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'user'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ scenic_category 表存在'
        ELSE '✗ scenic_category 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'scenic_category'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ scenic_info 表存在'
        ELSE '✗ scenic_info 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'scenic_info'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ comment_info 表存在'
        ELSE '✗ comment_info 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'comment_info'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ travel_note 表存在'
        ELSE '✗ travel_note 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'travel_note'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ order_info 表存在'
        ELSE '✗ order_info 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'order_info'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ likes 表存在'
        ELSE '✗ likes 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'likes'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ favorite 表存在'
        ELSE '✗ favorite 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'favorite'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ view_history 表存在'
        ELSE '✗ view_history 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'view_history'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ banner 表存在'
        ELSE '✗ banner 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'banner'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) = 1 THEN '✓ notice 表存在'
        ELSE '✗ notice 表不存在'
    END
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s' AND table_name = 'notice';

-- ========================================
-- 步骤6: 显示order_info表结构
-- ========================================
SELECT '========== order_info 表结构 ==========' AS '';
DESC order_info;

-- ========================================
-- 完成提示
-- ========================================
SELECT '========================================' AS '';
SELECT '✅ 数据库优化执行完成！' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';
SELECT '优化结果：' AS '';
SELECT '- 删除了4个表（route, route_days, scenic_comment, order_item）' AS '';
SELECT '- 优化了order_info表结构' AS '';
SELECT '- 保留了12个核心业务表' AS '';
SELECT '' AS '';
SELECT '下一步：' AS '';
SELECT '1. 检查应用程序编译是否正常' AS '';
SELECT '2. 运行功能测试' AS '';
SELECT '3. 更新项目文档' AS '';
SELECT '========================================' AS '';
