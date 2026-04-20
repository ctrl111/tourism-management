@echo off
chcp 65001 >nul
echo ========================================
echo 添加测试用户交互数据
echo ========================================
echo.
echo 这将为用户1-5添加不同的景点收藏和订单数据
echo 用于测试协同过滤推荐算法
echo.

mysql -u root -p12345 tourism_db < add_test_interactions.sql

echo.
echo ========================================
echo 测试数据添加完成
echo ========================================
echo.
echo 现在可以：
echo 1. 重启后端服务
echo 2. 用不同用户登录查看推荐结果
echo 3. 观察后端日志中的协同过滤计算过程
echo.
pause
