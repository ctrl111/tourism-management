<template>
  <div style="width: 75%;margin: 0 auto" :key="activeName">
    <el-card>
      <el-tabs v-model="activeName" tab-position="left">
        <el-tab-pane label="订单管理" name="orderManage">
          <OrderManage v-if="activeName==='orderManage'"></OrderManage>
        </el-tab-pane>
        <el-tab-pane label="我的游记" name="travelNoteManage">
          <TravelNoteManage v-if="activeName==='travelNoteManage'"></TravelNoteManage>
        </el-tab-pane>
        <el-tab-pane label="点赞记录" name="favorite">
          <Favorite v-if="activeName==='favorite'"></Favorite>
        </el-tab-pane>
        <el-tab-pane label="浏览历史" name="viewHistory">
          <ViewHistory v-if="activeName==='viewHistory'"></ViewHistory>
        </el-tab-pane>
      </el-tabs>
    </el-card>

  </div>
</template>

<script setup>
import { ref, watch } from "vue"
import { useRoute } from "vue-router"
import ViewHistory from "@/views/front/personalCenter/ViewHistory.vue"
import OrderManage from "@/views/front/personalCenter/OrderManage.vue"
import Favorite from "@/views/front/personalCenter/Favorite.vue"
import TravelNoteManage from "@/views/front/personalCenter/TravelNoteManage.vue"

const drawerVisible = ref(false)
const unreadCount = ref(0)

const activeName = ref('orderManage')
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
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
}
</script>

<style>

</style>
