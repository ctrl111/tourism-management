-- ============================================
-- 验证协同过滤推荐逻辑
-- ============================================

USE tourism_db;

-- 查看用户2的交互历史
SELECT '========== 用户2 (Александр) 的交互历史 ==========' AS '';
SELECT 
    v.user_id AS '用户ID',
    v.scenic_id AS '景点ID',
    s.name AS '景点名称',
    v.total_score AS '评分',
    v.last_action_time AS '最后交互时间'
FROM v_user_scenic_score v
JOIN scenic_info s ON v.scenic_id = s.id
WHERE v.user_id = 2
ORDER BY v.total_score DESC;

-- 查看用户3的交互历史
SELECT '' AS '';
SELECT '========== 用户3 (Екатерина) 的交互历史 ==========' AS '';
SELECT 
    v.user_id AS '用户ID',
    v.scenic_id AS '景点ID',
    s.name AS '景点名称',
    v.total_score AS '评分',
    v.last_action_time AS '最后交互时间'
FROM v_user_scenic_score v
JOIN scenic_info s ON v.scenic_id = s.id
WHERE v.user_id = 3
ORDER BY v.total_score DESC;

-- 计算用户2和用户3的共同景点（用于相似度计算）
SELECT '' AS '';
SELECT '========== 用户2和用户3的共同兴趣 ==========' AS '';
SELECT 
    v2.scenic_id AS '景点ID',
    s.name AS '景点名称',
    v2.total_score AS '用户2评分',
    v3.total_score AS '用户3评分'
FROM v_user_scenic_score v2
JOIN v_user_scenic_score v3 ON v2.scenic_id = v3.scenic_id
JOIN scenic_info s ON v2.scenic_id = s.id
WHERE v2.user_id = 2 AND v3.user_id = 3;

-- 用户3喜欢但用户2没交互的景点（应该推荐给用户2）
SELECT '' AS '';
SELECT '========== 应该推荐给用户2的景点（来自用户3） ==========' AS '';
SELECT 
    v3.scenic_id AS '景点ID',
    s.name AS '景点名称',
    v3.total_score AS '用户3评分',
    '用户2未交互' AS '状态'
FROM v_user_scenic_score v3
JOIN scenic_info s ON v3.scenic_id = s.id
WHERE v3.user_id = 3
AND v3.scenic_id NOT IN (
    SELECT scenic_id FROM v_user_scenic_score WHERE user_id = 2
);

-- 查看所有用户的交互矩阵
SELECT '' AS '';
SELECT '========== 用户-景点交互矩阵 ==========' AS '';
SELECT 
    user_id AS '用户',
    MAX(CASE WHEN scenic_id = 1 THEN '✓' ELSE '' END) AS '景点1',
    MAX(CASE WHEN scenic_id = 2 THEN '✓' ELSE '' END) AS '景点2',
    MAX(CASE WHEN scenic_id = 3 THEN '✓' ELSE '' END) AS '景点3',
    MAX(CASE WHEN scenic_id = 4 THEN '✓' ELSE '' END) AS '景点4',
    MAX(CASE WHEN scenic_id = 5 THEN '✓' ELSE '' END) AS '景点5',
    MAX(CASE WHEN scenic_id = 6 THEN '✓' ELSE '' END) AS '景点6',
    MAX(CASE WHEN scenic_id = 7 THEN '✓' ELSE '' END) AS '景点7',
    MAX(CASE WHEN scenic_id = 8 THEN '✓' ELSE '' END) AS '景点8',
    MAX(CASE WHEN scenic_id = 9 THEN '✓' ELSE '' END) AS '景点9',
    MAX(CASE WHEN scenic_id = 10 THEN '✓' ELSE '' END) AS '景点10',
    MAX(CASE WHEN scenic_id = 11 THEN '✓' ELSE '' END) AS '景点11',
    MAX(CASE WHEN scenic_id = 12 THEN '✓' ELSE '' END) AS '景点12'
FROM v_user_scenic_score
GROUP BY user_id
ORDER BY user_id;

-- 景点名称对照表
SELECT '' AS '';
SELECT '========== 景点ID对照表 ==========' AS '';
SELECT 
    id AS 'ID',
    name AS '景点名称',
    (SELECT COUNT(*) FROM v_user_scenic_score WHERE scenic_id = scenic_info.id) AS '交互用户数'
FROM scenic_info
ORDER BY id;
