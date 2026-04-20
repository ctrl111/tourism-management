@echo off
chcp 65001 >nul
echo ========================================
echo 验证协同过滤推荐逻辑
echo ========================================
echo.

mysql -u root -p12345 tourism_db < verify_recommendations.sql

echo.
echo ========================================
echo 验证完成
echo ========================================
pause
