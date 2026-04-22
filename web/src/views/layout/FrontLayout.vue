<template>
  <div class="front-layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-container">
        <!-- 网站Logo和标题 -->
        <div class="logo">
          <router-link to="/front/index">
            <span class="system-name">{{ $t('menu.systemName') }}</span>
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
          <el-menu-item index="/front/index">{{ $t('menu.home') }}</el-menu-item>
          <el-menu-item index="/front/scenic">{{ $t('menu.scenicNavigation') }}</el-menu-item>
          <el-menu-item index="/front/travelNote">{{ $t('menu.travelStories') }}</el-menu-item>

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
                    <el-icon><User /></el-icon>{{ $t('menu.personalCenter') }}
                  </el-dropdown-item>
                  <el-dropdown-item command="notice">
                    <el-icon><Bell /></el-icon>{{ $t('menu.myNotifications') }}
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Document /></el-icon>{{ $t('menu.myOrders') }}
                  </el-dropdown-item>
                  <el-dropdown-item command="travelNoteManage">
                    <el-icon><Notebook /></el-icon>{{ $t('menu.myTravelNotes') }}
                  </el-dropdown-item>
                  <el-dropdown-item command="favorite">
                    <el-icon><Star /></el-icon>{{ $t('menu.favorite') }}
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>{{ $t('common.logout') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <!-- 未登录状态 -->
            <el-button link @click="goToLogin" class="login-btn">{{ $t('common.login') }}</el-button>
            <el-button type="primary" @click="goToRegister" class="register-btn">{{ $t('common.register') }}</el-button>
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
          <p>{{ $t('common.copyright') }}</p>
          <p class="copyright-note">{{ $t('common.copyrightNote') }}</p>
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
  SwitchButton,
  Notebook,
  Bell
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useMenuStore } from '@/stores/menu'
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();
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
  try {
    // 优先尝试加载普通用户会话，如果没有则尝试其他类型
    await userStore.initUserState('USER')
    
    // 如果用户已登录，加载动态菜单
    if (userStore.getIsLoggedIn) {
      try {
        await menuStore.loadMenuByUserType(userStore.getUserType)
      } catch (error) {
        // 失败也不影响页面显示
      }
    }
  } catch (error) {
    // 即使初始化失败，也允许用户继续访问页面
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
    case 'notice':
      router.push('/front/personalCenter?type=noticeManage')
      break
    case 'orders':
      // 跳转到个人中心的订单管理标签页
      router.push('/front/personalCenter?type=orderManage')
      break
    case 'travelNoteManage':
      // 跳转到个人中心的游记管理标签页
      router.push('/front/personalCenter?type=travelNoteManage')
      break
    case 'favorite':
      // 跳转到个人中心的收藏标签页
      router.push('/front/personalCenter?type=favorite')
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
  ElMessageBox.confirm($t('common.logoutConfirm'), $t('message.confirmOperation'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  })
    .then(() => {
      // 清除登录状态
      userStore.logout()
      menuStore.setMenuContent([])
      
      ElMessage.success($t('common.logoutSuccess'))

      // 使用 window.location.href 强制跳转到登录页并刷新页面
      // 添加 fromLogout 参数，告诉登录页不要自动登录
      setTimeout(() => {
        window.location.href = '/login?fromLogout=user'
      }, 500)
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