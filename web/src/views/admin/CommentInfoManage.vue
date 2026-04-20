<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <!-- 搜索和操作区 -->
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('commentManage.type')" prop="typeCode">
            <el-select v-model="searchForm.typeCode" clearable :placeholder="$t('commentManage.pleaseSelectType')" style="width: 180px">
              <el-option :label="$t('commentManage.scenic')" value="SCENIC"></el-option>
              <el-option :label="$t('commentManage.travelNote')" value="TRAVEL_NOTE"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('commentManage.user')" prop="userName">
            <el-input v-model="searchForm.userName" clearable :placeholder="$t('commentManage.searchByUser')" style="width: 150px"></el-input>
          </el-form-item>
          <el-form-item :label="$t('commentManage.content')" prop="content">
            <el-input v-model="searchForm.content" clearable :placeholder="$t('commentManage.searchContent')" style="width: 200px"></el-input>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('commentManage.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('commentManage.reset') }}</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="danger" :icon="Delete" @click="batchDelete" :disabled="selectionRows.length<=0">
            {{ $t('commentManage.batchDelete') }}
          </el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-table
            ref="tableComponents"
            :data="listData"
            tooltip-effect="dark"
            style="width: 100%"
            @selection-change="selectionChange"
            border
            v-loading="loading"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column :label="$t('commentManage.userInfo')" width="180">
            <template #default="scope">
              <div class="user-info">
                <el-avatar :size="32" :src="scope.row.user?.avatarUrl" />
                <div class="user-details">
                  <div class="username">{{ scope.row.user?.username || $t('commentManage.unknownUser') }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commentManage.commentObject')" width="280">
            <template #default="scope">
              <div class="object-info">
                <el-tag :type="scope.row.typeCode === 'SCENIC' ? 'success' : 'primary'" size="small">
                  {{ scope.row.typeCode === 'SCENIC' ? $t('commentManage.scenic') : $t('commentManage.travelNote') }}
                </el-tag>
                <div class="object-name">
                  {{ scope.row.associationName || `ID: ${scope.row.associationId}` }}
                </div>
                <el-button 
                  type="primary" 
                  link 
                  size="small"
                  :icon="Link"
                  @click="goToObject(scope.row)"
                >
                  {{ $t('commentManage.viewObject') }}
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="content" :label="$t('commentManage.commentContent')" min-width="300">
            <template #default="scope">
              <div class="comment-content">
                <el-text :line-clamp="2">{{ scope.row.content }}</el-text>
                <el-button 
                  v-if="scope.row.content && scope.row.content.length > 50"
                  type="primary" 
                  link 
                  size="small"
                  @click="viewDetail(scope.row)"
                >
                  {{ $t('commentManage.viewDetails') }}
                </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('commentManage.replyStatus')" width="120" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.parentId" type="warning" size="small">
                {{ $t('commentManage.replyComment') }}
              </el-tag>
              <el-tag v-else type="info" size="small">
                {{ $t('commentManage.mainComment') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('commentManage.commentTime')" width="180"></el-table-column>
          <el-table-column fixed="right" :label="$t('commentManage.operation')" width="120">
            <template #default="scope">
              <el-button 
                :icon="View" 
                type="primary" 
                link 
                size="small" 
                @click="viewDetail(scope.row)"
              >
                {{ $t('commentManage.details') }}
              </el-button>
              <el-button 
                :icon="Delete" 
                type="danger" 
                link 
                size="small" 
                @click="deleteOne(scope.row)"
              >
                {{ $t('commentManage.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px">
          <el-pagination
              @current-change="currentChange"
              @size-change="sizeChange"
              :page-size="pageInfo.pageSize"
              :current-page="pageInfo.currentPage"
              background
              layout="total,sizes, prev, pager, next"
              :total="pageInfo.total">
          </el-pagination>
        </div>
      </el-card>
    </el-space>

    <!-- 评论详情对话框 -->
    <el-dialog
        v-model="detailDialogVisible"
        :title="$t('commentManage.commentDetails')"
        width="600px"
    >
      <el-descriptions :column="1" border v-if="currentComment">
        <el-descriptions-item :label="$t('commentManage.userInfo')">
          <div class="user-info">
            <el-avatar :size="40" :src="currentComment.user?.avatarUrl" />
            <div class="user-details" style="margin-left: 12px;">
              <div class="username">{{ currentComment.user?.username || $t('commentManage.unknownUser') }}</div>
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('commentManage.commentType')">
          <el-tag :type="currentComment.typeCode === 'SCENIC' ? 'success' : 'primary'">
            {{ currentComment.typeCode === 'SCENIC' ? $t('commentManage.scenic') : $t('commentManage.travelNote') }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('commentManage.commentObject')">
          {{ currentComment.associationName || `ID: ${currentComment.associationId}` }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('commentManage.commentContent')">
          <div style="white-space: pre-wrap; line-height: 1.6;">
            {{ currentComment.content }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('commentManage.replyStatus')">
          <el-tag v-if="currentComment.parentId" type="warning">
            {{ $t('commentManage.replyComment') }} ({{ $t('commentManage.parentCommentId') }}: {{ currentComment.parentId }})
          </el-tag>
          <el-tag v-else type="info">
            {{ $t('commentManage.mainComment') }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('commentManage.commentTime')">
          {{ currentComment.createTime }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('commentManage.close') }}</el-button>
        <el-button type="danger" :icon="Delete" @click="deleteFromDetail">{{ $t('commentManage.deleteThisComment') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, toRaw, onMounted } from 'vue'
import request from "@/utils/http.js"
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, View, Link } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const searchForm = ref({
  typeCode: '',
  content: '',
  userName: ''
})

const listData = ref([])
const selectionRows = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)
const currentComment = ref(null)

const pageInfo = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取列表数据
function getPageList() {
  loading.value = true
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo))
  request.get("/commentsInfo/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.total = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

// 跳转到评论对象
function goToObject(row) {
  if (row.typeCode === 'SCENIC') {
    // 跳转到景点详情页
    const url = router.resolve({
      path: `/front/scenicDetails/${row.associationId}`
    })
    window.open(url.href, '_blank')
  } else if (row.typeCode === 'TRAVEL_NOTE') {
    // 跳转到游记详情页
    const url = router.resolve({
      path: `/front/travelDetails/${row.associationId}`
    })
    window.open(url.href, '_blank')
  }
}

// 搜索
function search() {
  pageInfo.currentPage = 1
  getPageList()
}

// 重置搜索
function resetSearch() {
  searchForm.value = {
    typeCode: '',
    content: '',
    userName: ''
  }
  pageInfo.currentPage = 1
  getPageList()
}

// 分页
function currentChange(val) {
  pageInfo.currentPage = val
  getPageList()
}

function sizeChange(val) {
  pageInfo.pageSize = val
  getPageList()
}

// 选择行
function selectionChange(val) {
  selectionRows.value = val
}

// 查看详情
function viewDetail(row) {
  currentComment.value = { ...row }
  detailDialogVisible.value = true
}

// 删除单个
function deleteOne(row) {
  ElMessageBox.confirm('Вы уверены, что хотите удалить этот комментарий? Восстановление невозможно.', 'Подсказка', {
    confirmButtonText: 'Подтвердить',
    cancelButtonText: 'Отмена',
    type: 'warning'
  }).then(() => {
    request.delete("/commentsInfo/delBatch", { data: [row.id] }).then(res => {
      if (res) {
        ElMessage.success('Удалено успешно')
        getPageList()
      }
    })
  }).catch(() => {
    ElMessage.info('Удаление отменено')
  })
}

// 从详情对话框删除
function deleteFromDetail() {
  ElMessageBox.confirm('Вы уверены, что хотите удалить этот комментарий? Восстановление невозможно.', 'Подсказка', {
    confirmButtonText: 'Подтвердить',
    cancelButtonText: 'Отмена',
    type: 'warning'
  }).then(() => {
    request.delete("/commentsInfo/delBatch", { data: [currentComment.value.id] }).then(res => {
      if (res) {
        ElMessage.success('Удалено успешно')
        detailDialogVisible.value = false
        getPageList()
      }
    })
  }).catch(() => {
    ElMessage.info('Удаление отменено')
  })
}

// 批量删除
function batchDelete() {
  if (selectionRows.value.length === 0) {
    ElMessage.warning('Пожалуйста, выберите данные для удаления')
    return
  }
  ElMessageBox.confirm(`Вы уверены, что хотите удалить выбранные ${selectionRows.value.length} комментариев? Восстановление невозможно.`, 'Подсказка', {
    confirmButtonText: 'Подтвердить',
    cancelButtonText: 'Отмена',
    type: 'warning'
  }).then(() => {
    let ids = selectionRows.value.map(item => item.id)
    request.delete("/commentsInfo/delBatch", { data: ids }).then(res => {
      if (res) {
        ElMessage.success('Удалено успешно')
        getPageList()
      }
    })
  }).catch(() => {
    ElMessage.info('Удаление отменено')
  })
}

onMounted(() => {
  getPageList()
})
</script>

<style scoped>
.object-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.object-name {
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-weight: 500;
  color: #303133;
}

.user-id {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.comment-content :deep(.el-text) {
  line-height: 1.6;
}
</style>
