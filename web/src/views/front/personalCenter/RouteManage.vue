<template>
  <div class="route-manage">
    <div class="header-actions">
      <el-button type="primary" :icon="Plus" @click="handleAdd">发布路线</el-button>
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
      <el-table-column prop="days" label="天数" width="80">
        <template #default="{ row }">
          {{ row.days }} 天
        </template>
      </el-table-column>
      <el-table-column prop="totalCost" label="预估费用" width="120">
        <template #default="{ row }">
          ￥{{ row.totalCost }}
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
        :title="formData.id ? '编辑路线' : '发布路线'"
        width="800px"
        @close="closeDialog"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="路线标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入路线标题" />
        </el-form-item>
        <el-form-item label="路线描述" prop="description">
          <el-input
              v-model="formData.description"
              type="textarea"
              :rows="3"
              placeholder="请输入路线描述"
          />
        </el-form-item>
        <el-form-item label="行程天数" prop="days">
          <el-input-number v-model="formData.days" :min="1" :max="365" />
        </el-form-item>
        <el-form-item label="预估费用" prop="totalCost">
          <el-input-number
              v-model="formData.totalCost"
              :min="0"
              :precision="2"
              :step="100"
              placeholder="请输入预估费用"
          />
        </el-form-item>
        <el-form-item label="封面图片" prop="cover">
          <MyUpload v-model="formData.cover" />
        </el-form-item>
        <el-form-item label="详细内容" prop="content">
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
  title: [{ required: true, message: '请输入路线标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入路线描述', trigger: 'blur' }],
  days: [{ required: true, message: '请输入行程天数', trigger: 'blur' }],
  totalCost: [{ required: true, message: '请输入预估费用', trigger: 'blur' }],
  cover: [{ required: true, message: '请上传封面图片', trigger: 'change' }],
  content: [{ required: true, message: '请输入详细内容', trigger: 'blur' }]
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
  
  request.get('/route/page', { params }).then(res => {
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
 * 查看路线
 */
function handleView(row) {
  router.push(`/front/routeDetails/${row.id}`)
}

/**
 * 新增路线
 */
function handleAdd() {
  formData.value = {
    days: 1,
    totalCost: 0
  }
  dialogOpen.value = true
}

/**
 * 编辑路线
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
    const url = formData.value.id ? '/route/update' : '/route/add'
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
 * 删除路线
 */
function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除路线"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete('/route/delBatch', { data: [row.id] }).then(res => {
      if (res) {
        ElMessage.success('删除成功')
        getPageList()
      }
    })
  })
}
</script>

<style scoped>
.route-manage {
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


