<template>
  <div class="travel-note-manage">
    <div class="header-actions">
      <el-button type="primary" :icon="Plus" @click="handleAdd">{{ $t('personalCenter.publishNote') }}</el-button>
    </div>

    <el-table
        :data="listData"
        style="width: 100%"
        border
        v-loading="loading"
    >
      <el-table-column prop="cover" :label="$t('personalCenter.cover')" width="120">
        <template #default="{ row }">
          <el-image
              :src="row.cover"
              fit="cover"
              style="width: 80px; height: 60px; border-radius: 4px;"
          />
        </template>
      </el-table-column>
      <el-table-column prop="title" :label="$t('personalCenter.title')" min-width="200" show-overflow-tooltip />
      <el-table-column prop="likesCount" :label="$t('personalCenter.likesCount')" width="100" />
      <el-table-column prop="commentsCount" :label="$t('personalCenter.commentsCount')" width="100" />
      <el-table-column prop="createTime" :label="$t('personalCenter.publishTime')" width="180" />
      <el-table-column :label="$t('common.operation')" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="handleView(row)">{{ $t('personalCenter.view') }}</el-button>
          <el-button type="warning" size="small" link @click="handleEdit(row)">{{ $t('personalCenter.edit') }}</el-button>
          <el-button type="danger" size="small" link @click="handleDelete(row)">{{ $t('personalCenter.delete') }}</el-button>
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
        :title="formData.id ? $t('personalCenter.editNote') : $t('personalCenter.publishNewNote')"
        width="800px"
        @close="closeDialog"
    >
      <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item :label="$t('personalCenter.noteTitle')" prop="title">
          <el-input v-model="formData.title" :placeholder="$t('personalCenter.pleaseEnterTitle')" />
        </el-form-item>
        <el-form-item :label="$t('personalCenter.travelDate')" prop="travelTime">
          <el-date-picker
              v-model="formData.travelTime"
              type="date"
              :placeholder="$t('personalCenter.selectTravelDate')"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item :label="$t('personalCenter.tripDays')" prop="days">
          <el-input-number v-model="formData.days" :min="1" :max="365" />
        </el-form-item>
        <el-form-item :label="$t('personalCenter.coverImage')" prop="cover">
          <MyUpload v-if="dialogOpen" type="imageCard" :limit="1" :files="formData.cover"
                    @setFiles="handleCoverChange" />
        </el-form-item>
        <el-form-item :label="$t('personalCenter.noteContent')" prop="content">
          <MyEditor :content="formData.content"
                    @content-change="handleContentChange"
                    v-if="dialogOpen" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">{{ $t('personalCenter.cancel') }}</el-button>
        <el-button type="primary" @click="submit" :loading="submitting">{{ $t('personalCenter.submit') }}</el-button>
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
  title: [{ required: true, message: 'Пожалуйста, введите заголовок заметки', trigger: 'blur' }],
  travelTime: [{ required: true, message: 'Пожалуйста, выберите дату поездки', trigger: 'change' }],
  days: [{ required: true, message: 'Пожалуйста, введите количество дней', trigger: 'blur' }],
  images: [{ required: true, message: 'Пожалуйста, загрузите обложку', trigger: 'change' }],
  content: [{ required: true, message: 'Пожалуйста, введите содержание заметки', trigger: 'blur' }]
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
 * 处理封面图片变化
 */
function handleCoverChange(files) {
  formData.value.cover = files
  // 手动触发表单验证
  if (formRef.value) {
    formRef.value.validateField('cover')
  }
}

/**
 * 处理内容变化
 */
function handleContentChange(content) {
  formData.value.content = content
  // 手动触发表单验证
  if (formRef.value) {
    formRef.value.validateField('content')
  }
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
      ElMessage.warning('Пожалуйста, заполните форму')
      return
    }

    submitting.value = true
    const url = formData.value.id ? '/travelNote/update' : '/travelNote/add'
    const method = formData.value.id ? 'put' : 'post'

    request[method](url, formData.value).then(res => {
      if (res) {
        ElMessage.success('Операция выполнена успешно')
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
  ElMessageBox.confirm(`Вы уверены, что хотите удалить заметку "${row.title}"?`, 'Подсказка', {
    confirmButtonText: 'Подтвердить',
    cancelButtonText: 'Отмена',
    type: 'warning'
  }).then(() => {
    request.delete('/travelNote/delBatch', { data: [row.id] }).then(res => {
      if (res) {
        ElMessage.success('Удалено успешно')
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


