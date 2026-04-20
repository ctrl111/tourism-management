@echo off
chcp 65001 >nul
echo ========================================
echo 检查数据库中现有的用户和景点ID
echo ========================================
echo.

mysql -u root -p12345 tourism_db < check_existing_ids.sql

echo.
echo ========================================
echo 检查完成
echo ========================================
echo.
echo 根据上面的结果，修改 add_test_interactions.sql
echo 使用实际存在的用户ID和景点ID
echo.
pause
