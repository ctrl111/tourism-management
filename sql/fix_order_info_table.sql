/*
 修复order_info表结构
 
 添加缺失的字段
*/

USE templatev3_s;

-- 添加景点名称字段
ALTER TABLE `order_info` 
ADD COLUMN `scenic_name` VARCHAR(255) NULL COMMENT '景点名称' AFTER `scenic_id`;

-- 添加票数字段
ALTER TABLE `order_info` 
ADD COLUMN `quantity` INT NULL COMMENT '票数' AFTER `scenic_name`;

-- 添加单价字段
ALTER TABLE `order_info` 
ADD COLUMN `unit_price` DECIMAL(10,2) NULL COMMENT '单价' AFTER `quantity`;

-- 查看更新后的表结构
DESC order_info;

SELECT '✅ order_info表结构修复完成！' AS '';
