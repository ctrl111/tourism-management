@echo off
chcp 65001 >nul
echo ========================================
echo Быстрая русская локализация
echo ========================================
echo.

echo Этот скрипт заменит основные файлы на русифицированные версии.
echo.
echo Будут заменены:
echo - Login.vue
echo - Register.vue
echo.
set /p confirm="Продолжить? (Y/N): "

if /i not "%confirm%"=="Y" (
    echo Отменено пользователем.
    pause
    exit /b 0
)

echo.
echo [1/2] Замена Login.vue...
if exist "web\src\views\Login_RU.vue" (
    copy /Y "web\src\views\Login_RU.vue" "web\src\views\Login.vue" >nul
    echo ✓ Login.vue заменен
) else (
    echo ✗ Login_RU.vue не найден!
)

echo [2/2] Замена Register.vue...
if exist "web\src\views\Register_RU.vue" (
    copy /Y "web\src\views\Register_RU.vue" "web\src\views\Register.vue" >nul
    echo ✓ Register.vue заменен
) else (
    echo ✗ Register_RU.vue не найден!
)

echo.
echo ========================================
echo Готово!
echo ========================================
echo.
echo Следующие шаги:
echo 1. cd web
echo 2. npm install (если еще не установлено)
echo 3. npm run dev
echo.
echo Откройте браузер и проверьте страницы входа и регистрации.
echo Они должны быть полностью на русском языке.
echo.
pause
