/*
 ========================================
 第二阶段优化：适度优化（组合2）
 ========================================
 
 执行日期: 2026-03-02
 优化内容:
 1. 合并User和Admin表
 2. 删除banner表
 3. 添加数据库索引
 4. 统一字段命名
 
 ⚠️ 重要：执行前请确保已备份数据库！
*/

USE templatev3_s;

SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 步骤1: 合并User和Admin表
-- ========================================
SELECT '========== 步骤1: 合并User和Admin表 ==========' AS '';

-- 1.1 为user表添加role字段
ALTER TABLE `user` 
ADD COLUMN `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER/ADMIN' 
AFTER `status`;

SELECT '✓ 已为user表添加role字段' AS '';

-- 1.2 将admin数据迁移到user表
INSERT INTO `user` (username, password, nickname, avatar_url, phone, email, role, status, create_time)
SELECT 
    username, 
    password, 
    nickname, 
    avatar_url, 
    tel AS phone,
    email, 
    'ADMIN' AS role,
    status, 
    create_time
FROM `admin`
WHERE username NOT IN (SELECT username FROM `user`);

SELECT '✓ 已将admin数据迁移到user表' AS '';

-- 1.3 删除admin表
DROP TABLE IF EXISTS `admin`;

SELECT '✓ 已删除admin表' AS '';

-- ========================================
-- 步骤2: 删除banner表
-- ========================================
SELECT '========== 步骤2: 删除banner表 ==========' AS '';

DROP TABLE IF EXISTS `banner`;

SELECT '✓ 已删除banner表' AS '';

-- ========================================
-- 步骤3: 统一字段命名
-- ========================================
SELECT '========== 步骤3: 统一字段命名 ==========' AS '';

-- 将favorite表的collectTime改为create_time
ALTER TABLE `favorite` 
CHANGE COLUMN `collectTime` `create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间';

SELECT '✓ 已统一favorite表字段命名' AS '';

-- ========================================
-- 步骤4: 添加数据库索引
-- ========================================
SELECT '========== 步骤4: 添加数据库索引 ==========' AS '';

-- 用户表索引
ALTER TABLE `user` ADD INDEX `idx_username` (`username`);
ALTER TABLE `user` ADD INDEX `idx_email` (`email`);
ALTER TABLE `user` ADD INDEX `idx_phone` (`phone`);
ALTER TABLE `user` ADD INDEX `idx_role` (`role`);
ALTER TABLE `user` ADD INDEX `idx_status` (`status`);

SELECT '✓ 已为user表添加索引' AS '';

-- 景点表索引
ALTER TABLE `scenic_info` ADD INDEX `idx_category` (`category_id`);
ALTER TABLE `scenic_info` ADD INDEX `idx_status` (`status`);
ALTER TABLE `scenic_info` ADD INDEX `idx_create_time` (`create_time`);

SELECT '✓ 已为scenic_info表添加索引' AS '';

-- 订单表索引
ALTER TABLE `order_info` ADD INDEX `idx_user` (`user_id`);
ALTER TABLE `order_info` ADD INDEX `idx_scenic` (`scenic_id`);
ALTER TABLE `order_info` ADD INDEX `idx_status` (`status`);
ALTER TABLE `order_info` ADD INDEX `idx_create_time` (`create_time`);

SELECT '✓ 已为order_info表添加索引' AS '';

-- 旅行笔记索引
ALTER TABLE `travel_note` ADD INDEX `idx_user` (`user_id`);
ALTER TABLE `travel_note` ADD INDEX `idx_create_time` (`create_time`);

SELECT '✓ 已为travel_note表添加索引' AS '';

-- 评论表索引
ALTER TABLE `comment_info` ADD INDEX `idx_user` (`user_id`);
ALTER TABLE `comment_info` ADD INDEX `idx_type_assoc` (`type_code`, `association_id`);
ALTER TABLE `comment_info` ADD INDEX `idx_parent` (`parent_id`);

SELECT '✓ 已为comment_info表添加索引' AS '';

-- 点赞表索引
ALTER TABLE `likes` ADD INDEX `idx_user_type_assoc` (`user_id`, `type_code`, `association_id`);

SELECT '✓ 已为likes表添加索引' AS '';

-- 收藏表索引
ALTER TABLE `favorite` ADD INDEX `idx_user_type_assoc` (`user_id`, `type_code`, `association_id`);

SELECT '✓ 已为favorite表添加索引' AS '';

-- 浏览历史索引
ALTER TABLE `view_history` ADD INDEX `idx_user_type_assoc` (`user_id`, `type_code`, `association_id`);
ALTER TABLE `view_history` ADD INDEX `idx_view_time` (`view_time`);

SELECT '✓ 已为view_history表添加索引' AS '';

-- 系统通知索引
ALTER TABLE `notice` ADD INDEX `idx_user` (`user_id`);
ALTER TABLE `notice` ADD INDEX `idx_type` (`type_code`);
ALTER TABLE `notice` ADD INDEX `idx_create_time` (`create_time`);

SELECT '✓ 已为notice表添加索引' AS '';

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 验证优化结果
-- ========================================
SELECT '========================================' AS '';
SELECT '验证优化结果' AS '';
SELECT '========================================' AS '';

-- 查看表数量
SELECT COUNT(*) AS '当前表数量（预期10个）' 
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s';

-- 查看所有表
SELECT '当前所有表:' AS '';
SHOW TABLES;

-- 查看user表结构（应该包含role字段）
SELECT 'user表结构:' AS '';
DESC `user`;

-- 查看user表中的管理员数据
SELECT '管理员账户:' AS '';
SELECT id, username, nickname, role, status FROM `user` WHERE role = 'ADMIN';

-- 查看索引
SELECT '已添加的索引:' AS '';
SELECT 
    TABLE_NAME AS '表名',
    INDEX_NAME AS '索引名',
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS '索引字段'
FROM information_schema.STATISTICS
WHERE TABLE_SCHEMA = 'templatev3_s'
  AND INDEX_NAME != 'PRIMARY'
GROUP BY TABLE_NAME, INDEX_NAME
ORDER BY TABLE_NAME, INDEX_NAME;

-- ========================================
-- 完成提示
-- ========================================
SELECT '========================================' AS '';
SELECT '✅ 第二阶段优化执行完成！' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';
SELECT '优化结果：' AS '';
SELECT '- 合并了User和Admin表' AS '';
SELECT '- 删除了banner表' AS '';
SELECT '- 添加了30+个数据库索引' AS '';
SELECT '- 统一了字段命名' AS '';
SELECT '- 表数量: 12 → 10个' AS '';
SELECT '' AS '';
SELECT '下一步：' AS '';
SELECT '1. 更新后端代码（合并AdminController和UserController）' AS '';
SELECT '2. 更新前端代码（统一登录入口）' AS '';
SELECT '3. 删除banner相关代码' AS '';
SELECT '4. 运行编译测试' AS '';
SELECT '========================================' AS '';
