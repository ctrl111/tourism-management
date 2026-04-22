<template>
  <div class="personal-center-container">
    <div class="center-wrapper">
      <el-card class="main-card" shadow="never">
        <el-tabs v-model="activeName" class="custom-tabs">
          <el-tab-pane name="profile">
            <template #label>
              <div class="tab-label">
                <el-icon><User /></el-icon>
                <span>{{ $t('personalCenter.profile') }}</span>
              </div>
            </template>
            <Profile v-if="activeName==='profile'"></Profile>
          </el-tab-pane>
          
          <el-tab-pane name="orderManage">
            <template #label>
              <div class="tab-label">
                <el-icon><ShoppingCart /></el-icon>
                <span>{{ $t('menu.myOrders') }}</span>
              </div>
            </template>
            <OrderManage v-if="activeName==='orderManage'"></OrderManage>
          </el-tab-pane>
          
          <el-tab-pane name="travelNoteManage">
            <template #label>
              <div class="tab-label">
                <el-icon><Notebook /></el-icon>
                <span>{{ $t('personalCenter.myTravelNotes') }}</span>
              </div>
            </template>
            <TravelNoteManage v-if="activeName==='travelNoteManage'"></TravelNoteManage>
          </el-tab-pane>
          
          <el-tab-pane name="favorite">
            <template #label>
              <div class="tab-label">
                <el-icon><Star /></el-icon>
                <span>{{ $t('personalCenter.myFavorites') }}</span>
              </div>
            </template>
            <Favorite v-if="activeName==='favorite'"></Favorite>
          </el-tab-pane>
          
          <el-tab-pane name="noticeManage">
            <template #label>
              <div class="tab-label">
                <el-icon><Bell /></el-icon>
                <span>{{ $t('menu.myNotifications') }}</span>
              </div>
            </template>
            <NoticeManage v-if="activeName==='noticeManage'"></NoticeManage>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue"
import { useRoute } from "vue-router"
import { User, ShoppingCart, Notebook, Star, Bell } from '@element-plus/icons-vue'
import Profile from "@/views/front/personalCenter/Profile.vue"
import OrderManage from "@/views/front/personalCenter/OrderManage.vue"
import Favorite from "@/views/front/personalCenter/Favorite.vue"
import TravelNoteManage from "@/views/front/personalCenter/TravelNoteManage.vue"
import NoticeManage from "@/views/front/personalCenter/NoticeManage.vue"

const drawerVisible = ref(false)
const unreadCount = ref(0)

const activeName = ref('profile')
const route = useRoute()

// 初始化活动标签页
if (route.query.type) {
  activeName.value = route.query.type
}

// 监听路由变化，更新活动标签页
watch(
  () => route.query.type,
  (newType) => {
    if (newType) {
      activeName.value = newType
    }
  }
)

// 聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
}
</script>

<style scoped>
.personal-center-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 32px;
  min-height: calc(100vh - 200px);
}

.center-wrapper {
  width: 100%;
}

.main-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.custom-tabs {
  min-height: 500px;
}

.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 24px;
}

.custom-tabs :deep(.el-tabs__nav-wrap) {
  padding: 0 20px;
}

.custom-tabs :deep(.el-tabs__item) {
  padding: 16px 24px;
  font-size: 15px;
  color: #606266;
  transition: all 0.3s;
}

.custom-tabs :deep(.el-tabs__item:hover) {
  color: #409eff;
}

.custom-tabs :deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 600;
}

.custom-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-label .el-icon {
  font-size: 18px;
}

@media (max-width: 768px) {
  .personal-center-container {
    padding: 16px;
  }

  .custom-tabs :deep(.el-tabs__item) {
    padding: 12px 16px;
    font-size: 14px;
  }

  .tab-label span {
    display: none;
  }
}
</style>
