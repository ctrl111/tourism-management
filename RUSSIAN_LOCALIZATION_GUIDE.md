# Руководство по русской локализации проекта

## Обзор
Этот проект был полностью локализован на русский язык с использованием Vue I18n.

## Структура локализации

### Frontend (Vue.js)

#### 1. Конфигурация I18n
- Файл конфигурации: `web/src/i18n/index.js`
- Языковой пакет: `web/src/i18n/locales/ru.js`

#### 2. Использование в компонентах

```vue
<template>
  <div>
    <!-- Использование в шаблоне -->
    <h1>{{ $t('login.title') }}</h1>
    <el-button>{{ $t('common.confirm') }}</el-button>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// Использование в скрипте
const message = t('common.success')
</script>
```

#### 3. Ключи перевода

Все переводы организованы по категориям:
- `common` - Общие термины (подтвердить, отмена, сохранить и т.д.)
- `login` - Страница входа
- `register` - Страница регистрации
- `user` - Профиль пользователя
- `menu` - Навигационное меню
- `scenic` - Достопримечательности
- `travelNote` - Заметки о путешествиях
- `comment` - Комментарии
- `order` - Заказы
- `notice` - Объявления
- `favorite` - Избранное
- `admin` - Панель администратора
- `table` - Таблицы
- `form` - Формы
- `message` - Системные сообщения
- `payment` - Оплата
- `upload` - Загрузка файлов
- `recommendation` - Рекомендации
- `category` - Категории

### Backend (Java Spring Boot)

#### 1. Изменение сообщений в контроллерах

Замените все китайские сообщения на русские:

```java
// Было:
return Result.success("操作成功");

// Стало:
return Result.success("Операция выполнена успешно");
```

#### 2. Изменение сообщений об ошибках

```java
// Было:
throw new CustomException("用户名或密码错误");

// Стало:
throw new CustomException("Неверное имя пользователя или пароль");
```

#### 3. Изменение валидационных сообщений

В DTO классах:

```java
// Было:
@NotBlank(message = "用户名不能为空")
private String username;

// Стало:
@NotBlank(message = "Имя пользователя обязательно")
private String username;
```

## Файлы, требующие изменения

### Frontend Vue файлы (требуют замены текста на вызовы $t()):
1. `web/src/views/Login.vue`
2. `web/src/views/Register.vue`
3. `web/src/views/RetrievePassword.vue`
4. `web/src/views/Profile.vue`
5. `web/src/views/EditPassword.vue`
6. `web/src/views/EditCurrentUser.vue`
7. `web/src/views/front/Index.vue`
8. `web/src/views/front/Scenic.vue`
9. `web/src/views/front/ScenicDetails.vue`
10. `web/src/views/front/TravelNote.vue`
11. `web/src/views/front/TravelDetails.vue`
12. `web/src/views/front/Notice.vue`
13. `web/src/views/front/PersonalCenter.vue`
14. `web/src/views/front/personalCenter/*.vue`
15. `web/src/views/admin/*.vue`
16. `web/src/views/layout/*.vue`
17. `web/src/components/*.vue`

### Backend Java файлы (требуют замены китайских строк на русские):
1. Все контроллеры в `src/main/java/com/project/platform/controller/`
2. Все сервисы в `src/main/java/com/project/platform/service/`
3. Все DTO в `src/main/java/com/project/platform/dto/`
4. Обработчики исключений

### SQL файлы (требуют замены данных):
1. `sql (1)/tourism_db_*.sql` - Все SQL файлы с данными

## Примеры замены

### Пример 1: Login.vue

```vue
<!-- Было -->
<h1 class="brand-title">旅游推荐系统</h1>
<p class="brand-slogan">探索世界，发现美好</p>

<!-- Стало -->
<h1 class="brand-title">{{ $t('login.systemTitle') }}</h1>
<p class="brand-slogan">{{ $t('login.slogan') }}</p>
```

### Пример 2: UserController.java

```java
// Было
return Result.success("登录成功", user);

// Стало
return Result.success("Вход выполнен успешно", user);
```

### Пример 3: SQL данные

```sql
-- Было
INSERT INTO scenic_info (name, description) VALUES ('长城', '中国古代的军事防御工程');

-- Стало
INSERT INTO scenic_info (name, description) VALUES ('Великая Китайская стена', 'Древнее военное оборонительное сооружение Китая');
```

## Запуск проекта

1. Frontend:
```bash
cd web
npm install
npm run dev
```

2. Backend:
```bash
mvn clean install
mvn spring-boot:run
```

## Проверка локализации

1. Откройте браузер и перейдите на страницу входа
2. Все тексты должны быть на русском языке
3. Проверьте все формы, сообщения об ошибках и уведомления
4. Убедитесь, что Element Plus также использует русскую локализацию

## Добавление новых переводов

Чтобы добавить новый перевод:

1. Откройте `web/src/i18n/locales/ru.js`
2. Добавьте новый ключ в соответствующую категорию:

```javascript
export default {
  // ...
  myNewCategory: {
    myKey: 'Мой новый перевод'
  }
}
```

3. Используйте в компоненте:

```vue
{{ $t('myNewCategory.myKey') }}
```

## Примечания

- Все даты и числа автоматически форматируются согласно русской локали
- Element Plus компоненты (DatePicker, Pagination и т.д.) используют русский язык
- Валидационные сообщения также на русском языке
