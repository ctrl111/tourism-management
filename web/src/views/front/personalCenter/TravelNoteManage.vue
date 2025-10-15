<template>
  <div class="travel-note-manage">
    <div class="header-actions">
      <el-button type="primary" :icon="Plus" @click="handleAdd">发布游记</el-button>
    </div>

    <el-table
        :data="listData"
        style="width: 100%"
        border
        v-loading="loading"
    >
      <el-table-column prop="cover" label="封面" width="120">
        <template #default="{ row }">
          <el-image
              :src="row.cover"
              fit="cover"
              style="width: 80px; height: 60px; border-radius: 4px;"
              :preview-src-list="[row.cover]"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="destination" label="目的地" width="120" />
      <el-table-column prop="days" label="天数" width="80">
        <template #default="{ row }">
          {{ row.days }} 天
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览量" width="100" />
      <el-table-column prop="likesCount" label="点赞数" width="100" />
      <el-table-column prop="commentsCount" label="评论数" width="100" />
      <el-table-column prop="createTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="handleView(row)">查看</el-button>
          <el-button type="warning" size="small" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
          background
          layout="prev, pager, next, total"
          :page-size="pageInfo.pageSize"
          :current-page="pageInfo.pageNum"
          :total="pageInfo.total"
          @current-change="currentChange"
      />
    </div>

    <!-- 编辑/新增弹窗 -->
    <el-dialog
        v-model="dialogOpen"
        :title="formData.id ? '编辑游记' : '发布游记'"
        width="800px"
        @close="closeDialog"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="游记标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入游记标题" />
        </el-form-item>
        <el-form-item label="目的地" prop="destination">
          <el-input v-model="formData.destination" placeholder="请输入目的地" />
        </el-form-item>
        <el-form-item label="出行时间" prop="travelTime">
          <el-date-picker
              v-model="formData.travelTime"
              type="date"
              placeholder="选择出行时间"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="行程天数" prop="days">
          <el-input-number v-model="formData.days" :min="1" :max="365" />
        </el-form-item>
        <el-form-item label="封面图片" prop="cover">
          <MyUpload v-model="formData.cover" />
        </el-form-item>
        <el-form-item label="游记内容" prop="content">
          <MyEditor v-model="formData.content" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="submit" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, toRaw, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/http.js'
import { useUserStore } from '@/stores/user'
import MyUpload from '@/components/MyUpload.vue'
import MyEditor from '@/components/MyEditor.vue'

const router = useRouter()
const userStore = useUserStore()
const currentUser = computed(() => userStore.userInfo)

const loading = ref(false)
const listData = ref([])
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const dialogOpen = ref(false)
const formRef = ref()
const formData = ref({})
const submitting = ref(false)

const rules = {
  title: [{ required: true, message: '请输入游记标题', trigger: 'blur' }],
  destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
  travelTime: [{ required: true, message: '请选择出行时间', trigger: 'change' }],
  days: [{ required: true, message: '请输入行程天数', trigger: 'blur' }],
  cover: [{ required: true, message: '请上传封面图片', trigger: 'change' }],
  content: [{ required: true, message: '请输入游记内容', trigger: 'blur' }]
}

onMounted(() => {
  getPageList()
})

/**
 * 获取分页数据
 */
function getPageList() {
  loading.value = true
  const params = {
    pageNum: pageInfo.value.pageNum,
    pageSize: pageInfo.value.pageSize,
    userId: currentUser.value.id
  }
  
  request.get('/travelNote/page', { params }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  }).finally(() => {
    loading.value = false
  })
}

/**
 * 分页切换
 */
function currentChange(e) {
  pageInfo.value.pageNum = e
  getPageList()
}

/**
 * 查看游记
 */
function handleView(row) {
  router.push(`/front/travelDetails/${row.id}`)
}

/**
 * 新增游记
 */
function handleAdd() {
  formData.value = {
    days: 1
  }
  dialogOpen.value = true
}

/**
 * 编辑游记
 */
function handleEdit(row) {
  formData.value = Object.assign({}, row)
  dialogOpen.value = true
}

/**
 * 提交
 */
function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning('请完善表单信息')
      return
    }

    submitting.value = true
    const url = formData.value.id ? '/travelNote/update' : '/travelNote/add'
    const method = formData.value.id ? 'put' : 'post'

    request[method](url, formData.value).then(res => {
      if (res) {
        ElMessage.success('操作成功')
        closeDialog()
        getPageList()
      }
    }).finally(() => {
      submitting.value = false
    })
  })
}

/**
 * 关闭弹窗
 */
function closeDialog() {
  dialogOpen.value = false
  formData.value = {}
}

/**
 * 删除游记
 */
function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除游记"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete('/travelNote/delBatch', { data: [row.id] }).then(res => {
      if (res) {
        ElMessage.success('删除成功')
        getPageList()
      }
    })
  })
}
</script>

<style scoped>
.travel-note-manage {
  padding: 20px;
}

.header-actions {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>


