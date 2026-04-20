@echo off
chcp 65001 >nul
echo ========================================
echo Полная русская локализация проекта
echo ========================================
echo.

echo [1/3] Локализация Java файлов...
python localize_java_to_russian.py
if %errorlevel% neq 0 (
    echo Ошибка при локализации Java файлов!
    pause
    exit /b 1
)
echo.

echo [2/3] Локализация SQL файлов...
python localize_sql_to_russian.py
if %errorlevel% neq 0 (
    echo Ошибка при локализации SQL файлов!
    pause
    exit /b 1
)
echo.

echo [3/3] Проверка frontend конфигурации...
if exist "web\src\i18n\index.js" (
    echo ✓ Frontend i18n конфигурация найдена
) else (
    echo ✗ Frontend i18n конфигурация не найдена!
    echo Пожалуйста, убедитесь, что файлы i18n созданы в web/src/i18n/
)
echo.

echo ========================================
echo Локализация завершена!
echo ========================================
echo.
echo Следующие шаги:
echo 1. Проверьте изменения в Java файлах
echo 2. Проверьте изменения в SQL файлах
echo 3. Обновите Vue компоненты для использования $t()
echo 4. Запустите проект и протестируйте
echo.
echo Подробная инструкция: RUSSIAN_LOCALIZATION_GUIDE.md
echo.
pause
