# 数据库优化SQL执行说明

## 📋 文件说明

本目录包含以下SQL脚本：

1. **execute_optimization.sql** ⭐ 主执行脚本（推荐）
   - 包含完整的优化流程
   - 自动检查和验证
   - 带有详细的执行日志

2. **cleanup_optimization.sql** - 清理脚本
   - 仅删除不需要的表
   - 简单直接

3. **update_order_info_table.sql** - 订单表优化脚本
   - 单独优化订单表结构

## 🚀 执行步骤

### 方式1: 使用MySQL命令行（推荐）

```bash
# 1. 进入sql目录
cd sql

# 2. 执行主脚本
mysql -u root -p templatev3_s < execute_optimization.sql

# 输入密码: 12345
```

### 方式2: 使用Navicat

1. 打开Navicat
2. 连接到数据库 `templatev3_s`
3. 点击"查询" → "新建查询"
4. 打开文件 `execute_optimization.sql`
5. 点击"运行"按钮

### 方式3: 使用MySQL Workbench

1. 打开MySQL Workbench
2. 连接到数据库
3. File → Open SQL Script
4. 选择 `execute_optimization.sql`
5. 点击执行按钮（闪电图标）

### 方式4: 使用phpMyAdmin

1. 登录phpMyAdmin
2. 选择数据库 `templatev3_s`
3. 点击"SQL"标签
4. 复制 `execute_optimization.sql` 的内容
5. 粘贴并执行

## ⚠️ 重要提示

### 执行前检查清单

- [ ] 已备份数据库（重要！）
- [ ] 确认数据库名称为 `templatev3_s`
- [ ] 确认MySQL用户名为 `root`
- [ ] 确认MySQL密码为 `12345`
- [ ] 已关闭正在运行的应用程序

### 备份命令

```bash
# 备份整个数据库
mysqldump -u root -p templatev3_s > templatev3_s_backup_$(date +%Y%m%d_%H%M%S).sql

# 或者只备份要删除的表
mysqldump -u root -p templatev3_s route route_days scenic_comment order_item > deleted_tables_backup.sql
```

## 📊 预期结果

### 执行前
- 表数量: 16个
- 包含: route, route_days, scenic_comment, order_item

### 执行后
- 表数量: 12个
- 删除了: route, route_days, scenic_comment, order_item
- 优化了: order_info表结构

### 保留的12个核心表

1. ✅ admin - 管理员表
2. ✅ user - 用户表
3. ✅ scenic_category - 景点分类
4. ✅ scenic_info - 景点信息
5. ✅ comment_info - 统一评论表
6. ✅ travel_note - 旅行笔记
7. ✅ order_info - 订单信息（已优化）
8. ✅ likes - 点赞记录
9. ✅ favorite - 收藏记录
10. ✅ view_history - 浏览历史
11. ✅ banner - 轮播图
12. ✅ notice - 公告通知

## 🔍 验证方法

### 1. 检查表数量

```sql
SELECT COUNT(*) AS '表数量' 
FROM information_schema.tables 
WHERE table_schema = 'templatev3_s';
```

预期结果: 12

### 2. 检查已删除的表

```sql
-- 这些查询应该返回空结果
SHOW TABLES LIKE 'route';
SHOW TABLES LIKE 'route_days';
SHOW TABLES LIKE 'scenic_comment';
SHOW TABLES LIKE 'order_item';
```

### 3. 检查order_info表结构

```sql
DESC order_info;
```

应该包含以下字段:
- scenic_id (新增)
- unit_price (新增)
- scenic_name
- quantity
- visit_date

## 🐛 常见问题

### Q1: 执行时提示"表不存在"
**A:** 这是正常的，说明表已经被删除或从未创建。可以忽略。

### Q2: 提示"字段已存在"
**A:** 这是正常的，说明字段已经存在。脚本会自动跳过。

### Q3: 执行失败，如何回滚？
**A:** 使用备份恢复：
```bash
mysql -u root -p templatev3_s < templatev3_s_backup_YYYYMMDD_HHMMSS.sql
```

### Q4: 如何确认执行成功？
**A:** 检查以下几点：
1. 表数量变为12个
2. route、route_days、scenic_comment、order_item表不存在
3. order_info表包含新字段
4. 没有报错信息

## 📝 执行日志示例

成功执行后，你应该看到类似以下的输出：

```
========== 优化前的表列表 ==========
16 rows in set

========== 开始删除表 ==========
✓ 已删除 route_days 表
✓ 已删除 route 表
✓ 已删除 scenic_comment 表
✓ 已删除 order_item 表

========== 优化订单表结构 ==========
✓ 订单表结构优化完成

========== 优化后的表列表 ==========
12 rows in set

========== 验证核心表 ==========
✓ admin 表存在
✓ user 表存在
✓ scenic_category 表存在
...

✅ 数据库优化执行完成！
```

## 🔄 回滚方案

如果需要回滚到优化前的状态：

```bash
# 使用备份恢复
mysql -u root -p templatev3_s < templatev3_s_backup.sql
```

## 📞 需要帮助？

如果遇到问题：
1. 检查MySQL服务是否运行
2. 确认数据库连接信息正确
3. 查看MySQL错误日志
4. 确保有足够的权限执行DDL操作

## ✅ 执行完成后的检查清单

- [ ] 数据库表数量为12个
- [ ] route相关表已删除
- [ ] scenic_comment表已删除
- [ ] order_item表已删除
- [ ] order_info表结构已更新
- [ ] 应用程序可以正常编译
- [ ] 核心功能测试通过

---

**最后更新:** 2026-03-02  
**版本:** 1.0
