/*
 订单表结构优化脚本
 
 目的: 简化订单系统，将订单详情合并到订单主表
 执行时间: 2026-03-02
*/

USE templatev3_s;

-- 查看当前order_info表结构
DESC order_info;

-- 为order_info表添加缺失的字段（如果不存在）

-- 添加景点ID
ALTER TABLE `order_info` 
ADD COLUMN `scenic_id` INT NULL COMMENT '景点ID' AFTER `user_id`;

-- 添加单价字段
ALTER TABLE `order_info` 
ADD COLUMN `unit_price` DECIMAL(10,2) NULL COMMENT '单价' AFTER `quantity`;

-- 确保所有必需字段都存在
-- 以下字段应该已经存在，如果不存在请取消注释：

-- ALTER TABLE `order_info` 
-- ADD COLUMN `scenic_name` VARCHAR(255) NULL COMMENT '景点名称' AFTER `scenic_id`;

-- ALTER TABLE `order_info` 
-- ADD COLUMN `quantity` INT NULL COMMENT '票数' AFTER `scenic_name`;

-- ALTER TABLE `order_info` 
-- ADD COLUMN `visit_date` DATE NULL COMMENT '游玩日期' AFTER `status`;

-- 查看更新后的表结构
DESC order_info;

/*
 预期的order_info表结构：
 
 - id (INT) - 主键
 - order_no (VARCHAR) - 订单号
 - user_id (INT) - 用户ID
 - scenic_id (INT) - 景点ID [新增]
 - scenic_name (VARCHAR) - 景点名称
 - quantity (INT) - 票数
 - unit_price (DECIMAL) - 单价 [新增]
 - total_amount (DECIMAL) - 总金额
 - status (VARCHAR) - 订单状态
 - visit_date (DATE) - 游玩日期
 - create_time (TIMESTAMP) - 创建时间
*/
