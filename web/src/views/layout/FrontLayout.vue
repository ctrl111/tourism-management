<template>
  <div class="front-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-container">
        <!-- 网站Logo和标题 -->
        <div class="logo">
          <router-link to="/front/index">
            <span class="system-name">旅游推荐系统</span>
          </router-link>
        </div>

        <!-- 主导航菜单 -->
        <el-menu
            :default-active="activeMenu"
            class="nav-menu"
            mode="horizontal"
            router
            @select="handleMenuSelect"
        >
          <el-menu-item index="/front/index">首页</el-menu-item>
          <el-menu-item index="/front/scenic">景点导航</el-menu-item>
          <el-menu-item index="/front/travelNote">游记故事</el-menu-item>
          <el-menu-item index="/front/route">路线攻略</el-menu-item>
          <el-menu-item index="/front/notice">系统通知</el-menu-item>
          <el-menu-item index="/front/feedback">意见反馈</el-menu-item>
          <el-menu-item index="/front/personalCenter">个人中心</el-menu-item>

          <!-- 动态菜单内容（根据用户类型或系统配置动态渲染） -->
          <el-menu-item
            v-for="item in menuStore.menuContent"
            :key="item.id"
            :index="item.path"
          >
            {{ item.name }}
          </el-menu-item>
        </el-menu>

        <!-- 用户操作区 -->
        <div class="user-actions">
          <template v-if="userStore.getIsLoggedIn">
            <!-- 已登录状态 -->
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="userStore.userInfo?.avatarUrl" class="user-avatar">
                  {{ userStore.userInfo?.username?.charAt(0) || 'U' }}
                </el-avatar>
                <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="personalCenter">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Document /></el-icon>我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="favorite">
                    <el-icon><Star /></el-icon>我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item command="viewHistory">
                    <el-icon><Clock /></el-icon>浏览历史
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <!-- 未登录状态 -->
            <el-button link @click="goToLogin" class="login-btn">登录</el-button>
            <el-button type="primary" @click="goToRegister" class="register-btn">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部信息 -->
    <el-footer class="footer">
      <div class="footer-content">
        <div class="footer-info">
          <p>版权所有 ©2025 旅游推荐系统</p>
          <p class="copyright-note">官方网站浏览</p>
        </div>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  User,
  Document,
  Star,
  Clock,
  SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const menuStore = useMenuStore()

const activeMenu = ref('/front/index')

/**
 * 初始化
 */
onMounted(async () => {
  await initializeApp()
  updateActiveMenu()
})

/**
 * 监听路由变化
 */
watch(
  () => route.path,
  () => {
    updateActiveMenu()
  }
)

/**
 * 初始化应用
 */
async function initializeApp() {
  // 初始化用户状态
  await userStore.initUserState()
  
  // 如果用户已登录，加载动态菜单
  if (userStore.getIsLoggedIn) {
    await menuStore.loadMenuByUserType(userStore.getUserType)
  }
}

/**
 * 更新激活的菜单项
 */
function updateActiveMenu() {
  activeMenu.value = route.path
}

/**
 * 菜单选择处理
 */
function handleMenuSelect(index) {
  activeMenu.value = index
}

/**
 * 用户下拉菜单命令处理
 */
function handleUserCommand(command) {
  switch (command) {
    case 'personalCenter':
      router.push('/front/personalCenter')
      break
    case 'orders':
      router.push('/front/orders')
      break
    case 'favorite':
      router.push('/front/favorite')
      break
    case 'viewHistory':
      router.push('/front/viewHistory')
      break
    case 'logout':
      handleLogout()
      break
  }
}

/**
 * 退出登录处理
 */
function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      // 清除登录状态
      userStore.logout()
      menuStore.setMenuContent([])

      // 跳转到首页
      router.push('/front/index')
      ElMessage.success('退出成功')
    })
    .catch(() => {
      // 用户取消操作
    })
}

/**
 * 跳转到登录页
 */
function goToLogin() {
  router.push('/login')
}

/**
 * 跳转到注册页
 */
function goToRegister() {
  router.push('/register')
}
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.header {
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: fixed;
  width: 100%;
  top: 0;
  z-index: 1000;
  height: 60px;
  line-height: 60px;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
}

.system-name {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.nav-menu {
  border-bottom: none;
  flex: 1;
  justify-content: center;
  margin: 0 40px;
}

.nav-menu .el-menu-item {
  font-size: 16px;
  font-weight: 500;
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
}

.nav-menu .el-menu-item:hover {
  background: #f0f7ff;
  border-bottom-color: #409EFF;
  color: #409EFF;
}

.nav-menu .el-menu-item.is-active {
  background: #f0f7ff;
  border-bottom-color: #409EFF;
  color: #409EFF;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: #409EFF;
  color: white;
}

.username {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.login-btn,
.register-btn {
  font-weight: 500;
}

.login-btn {
  color: #409EFF;
}

.register-btn {
  background: #409EFF;
  border-color: #409EFF;
}

.main-content {
  flex: 1;
  margin-top: 60px; /* 头部高度 */
  min-height: calc(100vh - 120px);
  padding: 0;
}

.footer {
  background: #2c3e50;
  color: #ecf0f1;
  height: auto;
  padding: 20px 0;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.footer-info p {
  margin: 5px 0;
  font-size: 14px;
}

.copyright-note {
  font-size: 12px;
  color: #bdc3c7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 10px;
  }

  .system-name {
    font-size: 18px;
  }

  .nav-menu {
    margin: 0 10px;
  }

  .nav-menu .el-menu-item {
    padding: 0 10px;
    font-size: 14px;
  }

  .username {
    display: none;
  }
}
</style>