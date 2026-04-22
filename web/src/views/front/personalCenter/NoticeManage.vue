<template>
  <div class="notice-manage-container">
    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <el-radio-group v-model="activeName" @change="handleTabChange" size="large">
        <el-radio-button label="ALL">{{ $t('notice.all') }}</el-radio-button>
        <el-radio-button label="UNREAD">{{ $t('notice.unread') }}</el-radio-button>
        <el-radio-button label="ORDER">{{ $t('notice.order') }}</el-radio-button>
        <el-radio-button label="SYSTEM">{{ $t('notice.systemNotification') }}</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 通知列表 -->
    <div v-loading="loading" class="notice-list">
      <el-empty v-if="!loading && listData.length === 0" :description="$t('notice.noData')" />
      
      <div 
        v-for="notice in listData" 
        :key="notice.id" 
        class="notice-card"
        :class="{ 'unread': notice.isRead === 'NO' }"
        @click="viewDetail(notice)"
      >
        <div class="notice-icon">
          <el-icon :size="24" :color="getNoticeColor(notice.typeCode)">
            <component :is="getNoticeIcon(notice.typeCode)" />
          </el-icon>
        </div>
        
        <div class="notice-content">
          <div class="notice-header">
            <span class="notice-type">{{ getTypeLabel(notice.typeCode) }}</span>
            <span class="notice-time">{{ notice.createTime }}</span>
          </div>
          <h4 class="notice-title">{{ truncateText(notice.title, 40) }}</h4>
          <p class="notice-text">{{ truncateText(notice.content, 80) }}</p>
        </div>
        
        <div class="notice-actions">
          <el-tag v-if="notice.isRead === 'NO'" type="warning" size="small">
            {{ $t('notice.unread') }}
          </el-tag>
          <el-button 
            type="danger" 
            size="small" 
            link
            @click.stop="deleteNotification(notice)"
          >
            {{ $t('notice.delete') }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="pageInfo.total > 0">
      <el-pagination
        v-model:current-page="pageInfo.pageNum"
        v-model:page-size="pageInfo.pageSize"
        :page-sizes="[10, 20, 30]"
        layout="total, sizes, prev, pager, next"
        :total="pageInfo.total"
        @size-change="getPageList"
        @current-change="getPageList"
        background
      />
    </div>

    <!-- 查看通知详情弹窗 -->
    <el-dialog
        v-model="detailDialogVisible"
        :title="$t('notice.notificationDetails')"
        width="600px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item :label="$t('notice.type')">
          <el-tag>{{ getTypeLabel(currentNotification.typeCode) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('notice.noticeTitle')">
          {{ currentNotification.title }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('notice.content')">
          <div style="white-space: pre-wrap;">{{ currentNotification.content }}</div>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('notice.time')">
          {{ currentNotification.createTime }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('notice.status')">
          <el-tag v-if="currentNotification.isRead === 'NO'" type="warning">{{ $t('notice.unread') }}</el-tag>
          <el-tag v-else type="success">{{ $t('notice.read') }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('notice.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import { ref, toRaw } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useI18n } from 'vue-i18n';
import { Bell, ShoppingCart, InfoFilled } from '@element-plus/icons-vue'

const { t: $t } = useI18n();

function getTypeLabel(typeCode) {
  const typeMap = {
    'SYSTEM': $t('notice.systemNotification'),
    'PAYMENT': $t('notice.order'),
    'ORDER': $t('notice.order')
  }
  return typeMap[typeCode] || typeCode
}

function getNoticeIcon(typeCode) {
  const iconMap = {
    'SYSTEM': InfoFilled,
    'PAYMENT': ShoppingCart,
    'ORDER': ShoppingCart
  }
  return iconMap[typeCode] || Bell
}

function getNoticeColor(typeCode) {
  const colorMap = {
    'SYSTEM': '#409eff',
    'PAYMENT': '#67c23a',
    'ORDER': '#67c23a'
  }
  return colorMap[typeCode] || '#909399'
}

function truncateText(text, maxLength) {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.substring(0, maxLength) + '...';
}

const loading = ref(false)
const listData = ref([]);
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
const activeName = ref('ALL')
const currentNotification = ref({});
const detailDialogVisible = ref(false);

getPageList()

function getPageList() {
  loading.value = true
  let data = Object.assign({}, toRaw(pageInfo.value))
  
  if (activeName.value === 'ALL') {
    delete data.typeCode
    delete data.isRead
  } else if (activeName.value === 'UNREAD') {
    delete data.typeCode
    data.isRead = 'NO'
  } else if (activeName.value === 'ORDER') {
    data.typeCode = 'PAYMENT'
    delete data.isRead
  } else if (activeName.value === 'SYSTEM') {
    data.typeCode = 'SYSTEM'
    delete data.isRead
  }
  
  request.get("/notice/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

function handleTabChange() {
  pageInfo.value.pageNum = 1;
  getPageList();
}

function viewDetail(notice) {
  currentNotification.value = { ...notice };
  detailDialogVisible.value = true;
  
  if (notice.isRead === 'NO') {
    markAsRead(notice);
  }
}

const markAsRead = (notice) => {
  if (notice.isRead === 'YES') return;
  
  const updatedNotice = { ...notice, isRead: "YES" };
  request.put("/notice/update", updatedNotice).then(res => {
    if (res) {
      getPageList()
    }
  })
};

const deleteNotification = (notice) => {
  let ids = [notice.id]
  ElMessageBox.confirm($t('notice.deleteConfirm', { ids }), $t('message.confirmOperation'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning'
  }).then(() => {
    request.delete("/notice/delBatch", { data: ids }).then(res => {
      if (res) {
        ElMessage.success($t('notice.deleteSuccess'))
        getPageList()
      }
    })
  }).catch(() => {})
};
</script>

<style scoped>
.notice-manage-container {
  padding: 20px;
}

.filter-tabs {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.notice-list {
  min-height: 400px;
}

.notice-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  cursor: pointer;
  border-left: 4px solid transparent;
}

.notice-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateX(4px);
}

.notice-card.unread {
  background: #f0f9ff;
  border-left-color: #409eff;
}

.notice-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 50%;
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notice-type {
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
}

.notice-time {
  font-size: 12px;
  color: #999;
}

.notice-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-text {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.6;
}

.notice-actions {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .notice-card {
    flex-direction: column;
  }
  
  .notice-actions {
    flex-direction: row;
    width: 100%;
    justify-content: space-between;
  }
}
</style>
