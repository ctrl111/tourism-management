-- 更新评论类型从中文到英文
-- Update comment types from Chinese to English

USE tourism_db;

-- 更新景点评论
UPDATE comment_info 
SET type_code = 'SCENIC' 
WHERE type_code = '景点';

-- 更新游记评论
UPDATE comment_info 
SET type_code = 'TRAVEL_NOTE' 
WHERE type_code = '游记';

-- 验证更新结果
SELECT 
    type_code,
    COUNT(*) as count
FROM comment_info
GROUP BY type_code;
