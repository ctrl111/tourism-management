/*
 继续执行第二阶段优化
 从步骤3开始（前面的步骤已完成）
*/

USE templatev3_s;

-- ========================================
-- 步骤3: 统一字段命名
-- ========================================
SELECT '========== 步骤3: 统一字段命名 ==========' AS '';

-- 将favorite表的collect_time改为create_time
ALTER TABLE `favorite` 
CHANGE COLUMN `collect_time` `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间';

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

SELECT '✅ 所有索引添加完成！' AS '';
