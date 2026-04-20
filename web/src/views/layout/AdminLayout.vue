<template>
  <el-container class="admin-wrapper" style="height: 100%;">
    <el-aside width="200px" class="my-aside">
      <h3 class="title">{{ $t('menu.systemName') }}</h3>
      <el-menu
          style="width: 100%; overflow: hidden;"
          active-text-color="#409EFF"
          background-color="#000000"
          text-color="white"
          :default-active="useRoute().path"
          :default-openeds="['user', 'scenic', 'content']"
          @select="handleMenuSelect"
          router>
        <el-menu-item index="/admin">
          <el-icon>
            <HomeOutlined/>
          </el-icon>
          <span>{{ $t('menu.home') }}</span>
        </el-menu-item>

        <!-- 用户管理 -->
        <el-sub-menu index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>{{ $t('menu.userManage') }}</span>
          </template>
          <el-menu-item index="/admin/admin">
            <el-icon><Lock /></el-icon>
            <span>{{ $t('menu.adminManage') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/user">
            <el-icon><UserFilled /></el-icon>
            <span>{{ $t('menu.touristManagement') }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 景点信息管理 -->
        <el-sub-menu index="scenic">
          <template #title>
            <el-icon><Grid /></el-icon>
            <span>{{ $t('menu.scenicInfoManagement') }}</span>
          </template>
          <el-menu-item index="/admin/scenicCategory">
            <el-icon><Menu /></el-icon>
            <span>{{ $t('menu.categoryManagement') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/scenicInfo">
            <el-icon><LocationFilled /></el-icon>
            <span>{{ $t('menu.scenicManagement') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/commentsInfo">
            <el-icon><ChatDotSquare /></el-icon>
            <span>{{ $t('menu.commentManagement') }}</span>
          </el-menu-item>
          <el-menu-item index="/admin/order">
            <el-icon><ShoppingCart /></el-icon>
            <span>{{ $t('menu.orderManagement') }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 内容审核 -->
        <el-sub-menu index="content">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>{{ $t('menu.contentReview') }}</span>
          </template>
          <el-menu-item index="/admin/travelNote">
            <el-icon><Notebook /></el-icon>
            <span>{{ $t('menu.travelNoteSharing') }}</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/admin/notice">
          <el-icon><Message /></el-icon>
          <span>{{ $t('menu.systemNoticeManagement') }}</span>
        </el-menu-item>



      </el-menu>
      </el-aside>
    <el-container>

      <el-header height="60px">
        <el-row :gutter="5">
          <el-col :span="6" style="margin-top: 20px;">
            <el-space>
<!--              <router-link to="/admin">-->
<!--                <HomeOutlined/>-->
<!--              </router-link>-->
            </el-space>
          </el-col>
          <el-col :span="6">
          </el-col>
          <el-col :span="9">
          </el-col>
          <el-col :span="3">
            <div style="text-align: right;">
              <el-space style="margin-top: 15px;">
                <el-dropdown v-if="isUserLogin">
                  <div>
                    <el-space>
                      <el-avatar style="width: 30px;height: 30px;border-radius: 50%"
                                 shape="square" :size="30" :src="currentUser.avatarUrl"></el-avatar>
                      <span style="font-size: 16px">  {{ currentUser.username }}</span></el-space>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item><span @click="editCurrentUser">{{ $t('user.personalInfo') }}</span></el-dropdown-item>
                      <el-dropdown-item><span @click="editPassword">{{ $t('user.changePassword') }}</span></el-dropdown-item>
                      <el-dropdown-item divided><span @click="logout">{{ $t('common.logout') }}</span></el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>

              </el-space>
            </div>
          </el-col>
        </el-row>
      </el-header>
      <el-main style="background-color: RGB(245,245,245)" class="my-main">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import {ref, computed, onMounted} from "vue";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {useRoute} from 'vue-router';
import {HomeOutlined} from "@ant-design/icons-vue";
import {
  User,
  UserFilled,
  Lock,
  Grid,
  Menu,
  LocationFilled,
  ChatDotSquare,
  ShoppingCart,
  Document,
  Notebook,
  Message
} from '@element-plus/icons-vue'
import "@/styles/admin.css";
import { useUserStore } from '@/stores/user'

// 使用 Pinia Store 统一管理登录状态
const userStore = useUserStore()
const activeIndex = ref(useRoute().path)

// 从 store 获取登录状态和用户信息
const isUserLogin = computed(() => userStore.getIsLoggedIn)
const currentUser = computed(() => userStore.userInfo)

// 初始化用户状态
onMounted(async () => {
  // 只加载管理员会话
  const hasAdminSession = await userStore.initUserState('ADMIN')
  
  // 检查登录状态和权限
  if (!hasAdminSession || !userStore.getIsLoggedIn) {
    ElMessage.warning('Пожалуйста, войдите в аккаунт администратора')
    window.location.href = "/login"
    return
  }
  
  if (userStore.getUserType !== 'ADMIN') {
    ElMessage.warning('У вас нет прав доступа к системе управления')
    window.location.href = "/login"
  }
})

function handleMenuSelect(key, keyPath) {
  activeIndex.value = key
}

function logout() {
  ElMessage({
    message: 'Выход выполнен успешно, переход на страницу входа',
    type: 'success'
  });
  
  // 使用 store 的 logout 方法统一清除状态
  userStore.logout()
  
  // 使用 window.location.href 强制跳转到登录页并刷新页面
  // 添加 fromLogout 参数，告诉登录页不要自动登录
  setTimeout(() => {
    window.location.href = "/login?fromLogout=admin"
  }, 500)
}

function editCurrentUser() {
  router.push({path: "/admin/editCurrentUser"})
}

function editPassword() {
  router.push({path: "/admin/editPassword"})
}

const isCollapse = ref(true)

</script>

<style scoped>
.title {
  color: white;
  width: 100%;
  text-align: center;
  margin: 15px 5px;
}
.my-main::-webkit-scrollbar {
  display: none; /* 隐藏滚动条 */
}
.my-aside {
  background-color: #000000;
  overflow-x: hidden;
}

.el-menu {
  border-right: 0px;
}
</style>

