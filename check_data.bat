@echo off
chcp 65001 >nul
echo ========================================
echo 检查协同过滤数据
echo ========================================
echo.

mysql -uroot -p12345 < sql/check_recommendation_data.sql

echo.
echo ========================================
echo 检查完成！
echo ========================================
pause
