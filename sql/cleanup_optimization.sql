/*
 数据库优化清理脚本
 
 执行时间: 2026-03-02
 目的: 删除已优化掉的表和字段
 
 警告: 执行前请确保已备份数据库！
*/

USE templatev3_s;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 删除路线攻略相关表
-- ============================================

-- 删除路线天数表
DROP TABLE IF EXISTS `route_days`;

-- 删除路线表
DROP TABLE IF EXISTS `route`;

-- ============================================
-- 2. 删除景点评论表（使用统一评论表）
-- ============================================

-- 删除景点专用评论表
DROP TABLE IF EXISTS `scenic_comment`;

-- ============================================
-- 3. 删除订单详情表（简化订单系统）
-- ============================================

-- 删除订单详情子表
DROP TABLE IF EXISTS `order_item`;

-- ============================================
-- 4. 优化订单表结构（可选）
-- ============================================

-- 检查order_info表是否已有这些字段，如果没有则添加
-- 注意：如果字段已存在，这些语句会报错，可以忽略

-- 添加景点ID字段
ALTER TABLE `order_info` 
ADD COLUMN IF NOT EXISTS `scenic_id` INT NULL COMMENT '景点ID' AFTER `user_id`;

-- 添加景点名称字段（如果不存在）
-- ALTER TABLE `order_info` 
-- ADD COLUMN IF NOT EXISTS `scenic_name` VARCHAR(255) NULL COMMENT '景点名称' AFTER `scenic_id`;

-- 添加票数字段（如果不存在）
-- ALTER TABLE `order_info` 
-- ADD COLUMN IF NOT EXISTS `quantity` INT NULL COMMENT '票数' AFTER `scenic_name`;

-- 添加单价字段（如果不存在）
-- ALTER TABLE `order_info` 
-- ADD COLUMN IF NOT EXISTS `unit_price` DECIMAL(10,2) NULL COMMENT '单价' AFTER `quantity`;

-- 添加游玩日期字段（如果不存在）
-- ALTER TABLE `order_info` 
-- ADD COLUMN IF NOT EXISTS `visit_date` DATE NULL COMMENT '游玩日期' AFTER `status`;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 验证清理结果
-- ============================================

-- 查看剩余的表
SHOW TABLES;

-- 预期结果应该包含以下12个表：
-- 1. admin - 管理员表
-- 2. user - 用户表
-- 3. scenic_category - 景点分类
-- 4. scenic_info - 景点信息
-- 5. comment_info - 统一评论表
-- 6. travel_note - 旅行笔记
-- 7. order_info - 订单信息
-- 8. likes - 点赞记录
-- 9. favorite - 收藏记录
-- 10. view_history - 浏览历史
-- 11. banner - 轮播图
-- 12. notice - 公告通知

-- 查看order_info表结构
DESC order_info;

/*
 清理完成说明：
 
 ✅ 已删除的表：
 - route_days (路线天数表)
 - route (路线表)
 - scenic_comment (景点评论表)
 - order_item (订单详情表)
 
 ✅ 保留的表：
 - 12个核心业务表
 
 📝 注意事项：
 1. 如果order_info表已经包含scenic_name、quantity、visit_date等字段，
    则不需要执行ALTER TABLE语句
 2. 执行后请检查应用程序是否正常运行
 3. 建议在测试环境先执行，确认无误后再在生产环境执行
*/
