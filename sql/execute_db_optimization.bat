@echo off
chcp 65001 >nul
echo ========================================
echo 数据库优化脚本执行工具
echo ========================================
echo.
echo ⚠️  重要提示：
echo 1. 请确保已备份数据库！
echo 2. 请确保MySQL服务正在运行
echo 3. 默认配置：
echo    - 数据库: templatev3_s
echo    - 用户名: root
echo    - 密码: 12345
echo.
echo ========================================
echo.

set /p confirm="确认要执行数据库优化吗？(输入 yes 继续): "

if /i not "%confirm%"=="yes" (
    echo.
    echo 已取消执行。
    pause
    exit /b
)

echo.
echo 开始执行数据库优化...
echo.

REM 执行SQL脚本
mysql -u root -p12345 templatev3_s < execute_optimization.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✅ 数据库优化执行成功！
    echo ========================================
    echo.
    echo 优化结果：
    echo - 删除了4个表
    echo - 优化了order_info表结构
    echo - 保留了12个核心业务表
    echo.
    echo 下一步：
    echo 1. 编译后端项目: mvn clean compile
    echo 2. 编译前端项目: cd web ^&^& npm run build
    echo 3. 运行功能测试
    echo.
) else (
    echo.
    echo ========================================
    echo ❌ 执行失败！
    echo ========================================
    echo.
    echo 可能的原因：
    echo 1. MySQL服务未运行
    echo 2. 数据库连接信息错误
    echo 3. 权限不足
    echo.
    echo 请检查错误信息并重试。
    echo.
)

pause
