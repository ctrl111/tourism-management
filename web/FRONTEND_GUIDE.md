# 前端功能实现指南

## 已完成的功能

### 1. 状态管理系统 (Pinia Stores)

#### 用户状态管理 (`stores/user.js`)
- **功能**：
  - 用户登录/登出
  - 用户信息管理
  - Token管理
  - 登录状态持久化

- **使用方法**：
```javascript
import { useUserStore } from '@/stores/user'

// 在组件中使用
const userStore = useUserStore()

// 登录
await userStore.login({ username: 'xxx', password: 'xxx' })

// 获取用户信息
const userInfo = userStore.getUserInfo

// 检查登录状态
const isLoggedIn = userStore.getIsLoggedIn

// 退出登录
userStore.logout()
```

#### 菜单状态管理 (`stores/menu.js`)
- **功能**：
  - 动态菜单管理
  - 根据用户类型加载不同菜单
  - 激活菜单项追踪

- **使用方法**：
```javascript
import { useMenuStore } from '@/stores/menu'

const menuStore = useMenuStore()

// 加载用户类型对应的菜单
menuStore.loadMenuByUserType('VIP')

// 获取菜单内容
const menus = menuStore.getMenuContent
```

### 2. 前端布局 (`FrontLayout.vue`)

#### 已实现功能
- ✅ 用户登录状态显示
- ✅ 动态菜单渲染
- ✅ 用户下拉菜单
- ✅ 退出登录
- ✅ 路由激活状态追踪

#### 菜单结构
**固定菜单**：
- 首页
- 景点导航
- 游记故事
- 路线攻略
- 系统通知
- 意见反馈

**动态菜单**：
- 根据 `menuStore.menuContent` 动态渲染
- 可以根据用户类型(VIP、普通用户等)显示不同菜单

### 3. 路由配置

#### 已添加的路由
```javascript
// 景点详情页
{
  path: 'scenicDetails/:id',
  name: 'front-scenicDetails',
  component: () => import('../views/front/ScenicDetails.vue')
}

// 默认重定向
{
  path: "/",
  redirect: "/front/index"
}
```

### 4. HTTP拦截器

#### 功能
- ✅ 自动携带Token
- ✅ 401错误自动跳转登录
- ✅ 统一错误处理
- ✅ 响应数据自动解包

## 景点详情页使用

### 访问方式
```javascript
// 通过路由跳转
router.push('/front/scenicDetails/' + scenicId)

// 或在模板中
<router-link :to="`/front/scenicDetails/${item.id}`">
  查看详情
</router-link>
```

### 功能特性
- 景点基本信息展示
- 评分和评论系统
- 门票预定功能
- 用户评价列表
- 分页功能

## 动态菜单扩展

### 方式1：根据用户类型
在 `stores/menu.js` 中的 `loadMenuByUserType` 方法中配置：

```javascript
loadMenuByUserType(userType) {
  const dynamicMenus = []
  
  if (userType === 'VIP') {
    dynamicMenus.push({
      id: 'vip-zone',
      name: 'VIP专区',
      path: '/front/vipZone'
    })
  }
  
  if (userType === 'ADMIN') {
    dynamicMenus.push({
      id: 'admin-panel',
      name: '管理面板',
      path: '/admin/home'
    })
  }
  
  this.menuContent = dynamicMenus
}
```

### 方式2：从API获取
在 `stores/menu.js` 中的 `fetchDynamicMenus` 方法：

```javascript
async fetchDynamicMenus() {
  const response = await request.get('/api/menus')
  this.menuContent = response.data
}
```

## 下一步工作建议

### 需要完善的页面
1. ✅ 景点详情页 - 已完成
2. ⏳ 游记详情页 - 待开发
3. ⏳ 路线详情页 - 待开发
4. ⏳ 订单列表页 - 待开发
5. ⏳ 收藏列表页 - 待开发
6. ⏳ 浏览历史页 - 待开发

### 登录功能集成
需要在 `Login.vue` 中集成用户store：

```javascript
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

async function handleLogin() {
  const response = await userStore.login(loginForm)
  if (response.code === 200) {
    router.push('/front/index')
  }
}
```

### 路由守卫
可以在 `router/index.js` 中添加路由守卫保护需要登录的页面：

```javascript
import { useUserStore } from '@/stores/user'

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 需要登录的页面
  const authRequired = [
    '/front/personalCenter',
    '/front/orders',
    '/front/favorite'
  ]
  
  if (authRequired.includes(to.path) && !userStore.getIsLoggedIn) {
    next('/login')
  } else {
    next()
  }
})
```

## 技术栈
- Vue 3 (Composition API)
- Pinia (状态管理)
- Vue Router 4
- Element Plus (UI组件库)
- Axios (HTTP客户端)

## 项目结构
```
web/src/
├── stores/           # Pinia状态管理
│   ├── user.js       # 用户状态
│   └── menu.js       # 菜单状态
├── views/
│   ├── layout/       # 布局组件
│   │   ├── FrontLayout.vue
│   │   └── AdminLayout.vue
│   └── front/        # 前台页面
│       ├── Index.vue
│       ├── Scenic.vue
│       ├── ScenicDetails.vue
│       └── ...
├── router/           # 路由配置
├── utils/            # 工具函数
│   ├── http.js       # HTTP请求封装
│   └── tools.js
└── components/       # 公共组件
```

