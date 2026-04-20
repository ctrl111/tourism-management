@echo off
echo ========================================
echo 批量添加景点数据
echo ========================================
echo.
echo 正在连接数据库并导入数据...
echo.

mysql -u root -p12345 tourism_db < sql/add_more_scenic_data.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 景点数据导入成功！
    echo ========================================
    echo.
    echo 已添加30个景点，涵盖8个分类：
    echo - 自然风光：4个
    echo - 历史文化：4个
    echo - 主题乐园：3个
    echo - 古镇村落：3个
    echo - 山岳名胜：3个
    echo - 海滨度假：3个
    echo - 草原湖泊：3个
    echo - 宗教寺庙：3个
    echo.
    echo 现在可以测试协同过滤推荐功能了！
) else (
    echo.
    echo ========================================
    echo 导入失败！请检查：
    echo 1. MySQL服务是否启动
    echo 2. 数据库连接信息是否正确
    echo 3. tourism_db数据库是否存在
    echo ========================================
)

echo.
pause
