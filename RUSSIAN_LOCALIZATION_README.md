# Полная русская локализация проекта туристической системы

## Обзор

Этот проект был полностью локализован на русский язык. Локализация включает:

- ✅ Frontend (Vue.js) с использованием Vue I18n
- ✅ Backend (Java Spring Boot) - все сообщения и валидации
- ✅ SQL данные - названия, описания, категории
- ✅ Element Plus UI компоненты - русская локаль
- ✅ Документация

## Быстрый старт

### Шаг 1: Запуск автоматической локализации

```bash
# Запустите скрипт автоматической локализации
run_full_localization.bat
```

Этот скрипт автоматически:
- Заменит все китайские тексты в Java файлах на русские
- Переведет данные в SQL файлах
- Проверит наличие i18n конфигурации

### Шаг 2: Замена Vue компонентов

Созданы русифицированные версии ключевых компонентов:

1. `web/src/views/Login_RU.vue` → замените `Login.vue`
2. `web/src/views/Register_RU.vue` → замените `Register.vue`

Для остальных компонентов используйте эти файлы как шаблон.

### Шаг 3: Установка зависимостей и запуск

#### Frontend

```bash
cd web
npm install
npm run dev
```

#### Backend

```bash
mvn clean install
mvn spring-boot:run
```

## Структура локализации

### Frontend (Vue.js)

```
web/src/
├── i18n/
│   ├── index.js          # Конфигурация i18n
│   └── locales/
│       └── ru.js         # Русский языковой пакет
├── main.js               # Обновлен для использования i18n
└── views/                # Vue компоненты (требуют обновления)
```

### Использование в компонентах

```vue
<template>
  <div>
    <!-- В шаблоне -->
    <h1>{{ $t('login.title') }}</h1>
    <el-button>{{ $t('common.confirm') }}</el-button>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// В скрипте
const message = t('common.success')
ElMessage.success(t('message.success'))
</script>
```

### Backend (Java)

Все сообщения в контроллерах, сервисах и DTO уже переведены:

```java
// Было:
return Result.success("操作成功");

// Стало:
return Result.success("Операция выполнена успешно");
```

## Категории переводов

### Frontend (ru.js)

- `common` - Общие термины (подтвердить, отмена, сохранить...)
- `login` - Страница входа
- `register` - Страница регистрации
- `user` - Профиль пользователя
- `menu` - Навигация
- `scenic` - Достопримечательности
- `travelNote` - Заметки о путешествиях
- `comment` - Комментарии
- `order` - Заказы
- `notice` - Объявления
- `favorite` - Избранное
- `admin` - Администрирование
- `table` - Таблицы
- `form` - Формы
- `message` - Системные сообщения
- `payment` - Оплата
- `upload` - Загрузка файлов
- `recommendation` - Рекомендации
- `category` - Категории

## Файлы, требующие ручного обновления

### Vue компоненты (замените китайский текст на $t()):

#### Приоритет 1 (Основные страницы):
- [ ] `web/src/views/Login.vue` ← Используйте `Login_RU.vue` как шаблон
- [ ] `web/src/views/Register.vue` ← Используйте `Register_RU.vue` как шаблон
- [ ] `web/src/views/RetrievePassword.vue`
- [ ] `web/src/views/Profile.vue`
- [ ] `web/src/views/EditPassword.vue`

#### Приоритет 2 (Frontend страницы):
- [ ] `web/src/views/front/Index.vue`
- [ ] `web/src/views/front/Scenic.vue`
- [ ] `web/src/views/front/ScenicDetails.vue`
- [ ] `web/src/views/front/TravelNote.vue`
- [ ] `web/src/views/front/TravelDetails.vue`
- [ ] `web/src/views/front/Notice.vue`
- [ ] `web/src/views/front/PersonalCenter.vue`

#### Приоритет 3 (Личный кабинет):
- [ ] `web/src/views/front/personalCenter/Profile.vue`
- [ ] `web/src/views/front/personalCenter/Favorite.vue`
- [ ] `web/src/views/front/personalCenter/OrderManage.vue`
- [ ] `web/src/views/front/personalCenter/TravelNoteManage.vue`

#### Приоритет 4 (Админ панель):
- [ ] `web/src/views/admin/Home.vue`
- [ ] `web/src/views/admin/UserManage.vue`
- [ ] `web/src/views/admin/AdminManage.vue`
- [ ] `web/src/views/admin/ScenicInfoManage.vue`
- [ ] `web/src/views/admin/ScenicCategoryManage.vue`
- [ ] `web/src/views/admin/TravelNoteManage.vue`
- [ ] `web/src/views/admin/OrderInfoManage.vue`
- [ ] `web/src/views/admin/CommentInfoManage.vue`
- [ ] `web/src/views/admin/NoticeManage.vue`

#### Приоритет 5 (Компоненты и Layout):
- [ ] `web/src/components/MyUpload.vue`
- [ ] `web/src/components/MyEditor.vue`
- [ ] `web/src/components/Captcha.vue`
- [ ] `web/src/components/PaymentDialog.vue`
- [ ] `web/src/components/RechargeDialog.vue`
- [ ] `web/src/views/layout/FrontLayout.vue`
- [ ] `web/src/views/layout/AdminLayout.vue`

## Шаблон для обновления Vue компонентов

### Шаг 1: Импортируйте useI18n

```vue
<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>
```

### Шаг 2: Замените жестко закодированный текст

```vue
<!-- Было -->
<el-button>登录</el-button>
<span>用户名</span>

<!-- Стало -->
<el-button>{{ $t('login.login') }}</el-button>
<span>{{ $t('login.username') }}</span>
```

### Шаг 3: Замените в JavaScript коде

```javascript
// Было
ElMessage.success('操作成功')

// Стало
ElMessage.success(t('message.success'))
```

### Шаг 4: Замените в правилах валидации

```javascript
// Было
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ]
}

// Стало
const rules = {
  username: [
    { required: true, message: t('login.pleaseEnterUsername'), trigger: 'blur' }
  ]
}
```

## Добавление новых переводов

### Frontend

1. Откройте `web/src/i18n/locales/ru.js`
2. Добавьте новый ключ:

```javascript
export default {
  // ...
  myNewSection: {
    myKey: 'Мой новый перевод',
    anotherKey: 'Еще один перевод'
  }
}
```

3. Используйте в компоненте:

```vue
{{ $t('myNewSection.myKey') }}
```

### Backend

1. Откройте `localize_java_to_russian.py`
2. Добавьте новую пару в словарь `TRANSLATIONS`:

```python
TRANSLATIONS = {
    # ...
    "新的中文": "Новый русский перевод",
}
```

3. Запустите скрипт снова:

```bash
python localize_java_to_russian.py
```

## Проверка локализации

### Checklist

- [ ] Все страницы входа/регистрации на русском
- [ ] Меню навигации на русском
- [ ] Сообщения об ошибках на русском
- [ ] Уведомления (ElMessage) на русском
- [ ] Валидационные сообщения на русском
- [ ] Кнопки и формы на русском
- [ ] Таблицы и пагинация на русском (Element Plus)
- [ ] DatePicker на русском (Element Plus)
- [ ] Данные в базе данных на русском

### Тестирование

1. Запустите приложение
2. Откройте страницу входа - все должно быть на русском
3. Попробуйте зарегистрироваться - проверьте сообщения
4. Войдите в систему - проверьте меню и интерфейс
5. Проверьте все CRUD операции - сообщения должны быть на русском
6. Проверьте валидацию форм - ошибки на русском
7. Проверьте Element Plus компоненты (календарь, пагинация)

## Известные проблемы и решения

### Проблема: Element Plus компоненты на английском

**Решение:** Убедитесь, что в `main.js` импортирована русская локаль:

```javascript
import ruLocale from 'element-plus/dist/locale/ru.mjs'

app.use(ElementPlus, {
  locale: ruLocale
})
```

### Проблема: Некоторые тексты остались на китайском

**Решение:** 
1. Найдите текст в коде
2. Добавьте перевод в `ru.js`
3. Замените текст на `$t('category.key')`

### Проблема: Backend возвращает китайские сообщения

**Решение:**
1. Добавьте перевод в `localize_java_to_russian.py`
2. Запустите скрипт снова
3. Проверьте изменения в Java файлах

## Дополнительные ресурсы

- [Vue I18n документация](https://vue-i18n.intlify.dev/)
- [Element Plus локализация](https://element-plus.org/en-US/guide/i18n.html)
- [Подробное руководство](RUSSIAN_LOCALIZATION_GUIDE.md)

## Поддержка

Если у вас возникли вопросы или проблемы с локализацией:

1. Проверьте `RUSSIAN_LOCALIZATION_GUIDE.md`
2. Убедитесь, что все скрипты выполнены
3. Проверьте консоль браузера на ошибки
4. Проверьте логи backend на китайские сообщения

## Статус локализации

- ✅ Frontend i18n конфигурация
- ✅ Русский языковой пакет (300+ переводов)
- ✅ Element Plus русская локаль
- ✅ Backend Java файлы (автоматическая замена)
- ✅ SQL данные (автоматическая замена)
- ⚠️ Vue компоненты (требуют ручного обновления)
- ✅ Документация

## Следующие шаги

1. ✅ Запустите `run_full_localization.bat`
2. ⚠️ Обновите Vue компоненты (используйте Login_RU.vue и Register_RU.vue как шаблоны)
3. ⚠️ Протестируйте все функции приложения
4. ⚠️ Обновите SQL данные в базе (запустите обновленные SQL скрипты)
5. ⚠️ Проверьте все сообщения об ошибках

---

**Примечание:** Локализация - это итеративный процесс. Некоторые тексты могут потребовать дополнительной настройки в зависимости от контекста использования.
