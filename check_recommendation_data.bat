@echo off
chcp 65001 >nul
echo ========================================
echo 检查推荐系统数据
echo ========================================
echo.

mysql -u root -p12345 tourism_db < check_recommendation_data.sql

echo.
echo ========================================
echo 检查完成
echo ========================================
pause
