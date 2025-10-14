# 快速开始指南

## 已完成的功能清单

### ✅ 后端功能

1. **路线推荐服务** (`RouteServiceImpl.java`)
   - 实现了 `homePage()` 方法，基于用户浏览历史进行个性化推荐
   - 自动填充用户信息、评论数、浏览数、点赞数
   - 数据不足时自动补充其他路线
   - 新增 API 接口：`GET /route/homelist`

### ✅ 前端功能

1. **Pinia 状态管理**
   - `stores/user.js` - 用户登录状态、用户信息管理
   - `stores/menu.js` - 动态菜单管理

2. **前端布局优化** (`FrontLayout.vue`)
   - 移除了所有 TODO 标记
   - 集成了 Pinia 状态管理
   - 实现了完整的登录状态显示
   - 支持动态菜单渲染
   - 用户下拉菜单功能完善

3. **景点详情页** (`ScenicDetails.vue`)
   - 已添加到路由配置
   - 路径：`/front/scenicDetails/:id`
   - 功能：景点信息展示、评论、预定

4. **路由配置**
   - 添加了根路径重定向到首页
   - 景点详情页路由已配置

## 使用方法

### 1. 首页调用路线推荐

在 `Index.vue` 中已经正确调用了路线推荐接口：

```javascript
// 获取线路数据
const travelRouteRes = request.get('/route/homelist', {
  params: paramsForm
}).then(res => {
  routeList.value = res.data.list
})
```

### 2. 跳转到景点详情

首页中已经实现了景点卡片的点击跳转：

```javascript
// 在景点卡片上
@click="router.push('/scenicDetails/'+item.id)"

// 或使用完整路径
@click="router.push('/front/scenicDetails/'+item.id)"
```

### 3. 用户登录状态管理

在任何组件中使用：

```vue
<script setup>
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 检查登录状态
if (userStore.getIsLoggedIn) {
  console.log('用户已登录:', userStore.userInfo)
}
</script>
```

### 4. 动态菜单配置

在 `stores/menu.js` 中配置不同用户类型的菜单：

```javascript
loadMenuByUserType(userType) {
  const dynamicMenus = []
  
  // VIP 用户专属菜单
  if (userType === 'VIP') {
    dynamicMenus.push({
      id: 'vip-zone',
      name: 'VIP专区',
      path: '/front/vipZone'
    })
  }
  
  this.menuContent = dynamicMenus
}
```

## 数据流示意图

### 景点推荐流程
```
用户访问首页
    ↓
Index.vue 调用 /scenicInfo/homelist
    ↓
ScenicInfoServiceImpl.homePage()
    ↓
1. 获取用户浏览历史
2. 基于历史推荐景点
3. 数据不足时补充其他景点
4. 填充评论数、评分等统计数据
    ↓
返回个性化景点列表
```

### 路线推荐流程
```
用户访问首页
    ↓
Index.vue 调用 /route/homelist
    ↓
RouteServiceImpl.homePage()
    ↓
1. 获取用户浏览历史
2. 基于历史推荐路线
3. 数据不足时补充其他路线
4. 填充用户信息、评论数、浏览数、点赞数
    ↓
返回个性化路线列表
```

### 用户登录流程
```
用户输入账号密码
    ↓
调用 userStore.login(loginData)
    ↓
发送 POST /login 请求
    ↓
成功后：
1. 保存 token 到 localStorage
2. 保存用户信息到 store
3. 设置登录状态为 true
    ↓
自动加载用户类型对应的动态菜单
    ↓
跳转到首页
```

## 核心API接口

### 景点相关
- `GET /scenicInfo/homelist` - 首页景点推荐
- `GET /scenicInfo/selectById/:id` - 获取景点详情
- `GET /scenicInfo/putViewCount/:id` - 增加浏览量

### 路线相关
- `GET /route/homelist` - 首页路线推荐（新增）
- `GET /route/page` - 路线分页列表

### 游记相关
- `GET /travelNote/homelist` - 首页游记推荐

### 用户相关
- `POST /login` - 用户登录
- `GET /getCurrentUser` - 获取当前用户信息

## 运行项目

### 后端
```bash
# 在项目根目录
mvn spring-boot:run
```

### 前端
```bash
# 进入 web 目录
cd web

# 安装依赖（如果还没安装）
npm install

# 启动开发服务器
npm run dev
```

## 测试功能

1. **测试路线推荐**
   - 访问首页
   - 查看"精品路线分享推荐"板块
   - 确认显示路线列表及统计数据

2. **测试景点详情**
   - 点击首页任意景点卡片
   - 跳转到景点详情页
   - 查看景点信息、评分、评论

3. **测试动态菜单**
   - 登录后查看顶部菜单
   - 根据用户类型显示不同菜单项

4. **测试登录状态**
   - 未登录：显示"登录"和"注册"按钮
   - 已登录：显示用户头像和下拉菜单

## 注意事项

1. **Token 管理**
   - Token 自动保存在 localStorage
   - HTTP 请求自动携带 token
   - Token 过期自动跳转登录页

2. **路由路径**
   - 前台页面统一使用 `/front/*` 前缀
   - 后台页面统一使用 `/admin/*` 前缀

3. **状态持久化**
   - 用户登录状态会在页面刷新后恢复
   - 使用 localStorage 存储 token

## 下一步开发建议

1. **完善登录页面**
   - 集成 `useUserStore` 进行登录
   - 登录成功后跳转首页

2. **开发游记详情页**
   - 参考 `ScenicDetails.vue` 实现
   - 路由：`/front/travelNoteDetails/:id`

3. **开发路线详情页**
   - 参考 `ScenicDetails.vue` 实现
   - 路由：`/front/routeDetails/:id`

4. **实现个人中心相关页面**
   - 我的订单
   - 我的收藏
   - 浏览历史

## 常见问题

### Q: 动态菜单不显示？
A: 检查 `menuStore.menuContent` 是否有数据。默认为空数组，需要调用 `loadMenuByUserType()` 加载。

### Q: 登录状态没有保持？
A: 检查 localStorage 中是否有 token，确保在应用初始化时调用了 `userStore.initUserState()`。

### Q: 路由跳转无效？
A: 确保路由路径正确，前台页面需要 `/front/` 前缀。

### Q: API 请求失败？
A: 检查后端服务是否启动，查看 `web/src/utils/http.js` 中的 baseURL 配置。

