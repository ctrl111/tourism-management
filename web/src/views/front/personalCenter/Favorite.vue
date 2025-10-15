<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <!-- 搜索表单 -->
      <el-form ref="searchFormRef" :model="searchForm" inline>
        <el-form-item label="景点名称" prop="scenicName">
          <el-input
            v-model="searchForm.scenicName"
            placeholder="请输入景点名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 收藏列表 -->
      <el-table
        :data="listData"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="scenicName" label="景点名称" width="200"></el-table-column>
        <el-table-column prop="scenicInfo.address" label="地址" min-width="200">
          <template #default="{ row }">
            {{ row.scenicInfo?.address || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="scenicInfo.ticketPrice" label="门票价格" width="120" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">
              ¥{{ row.scenicInfo?.ticketPrice || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="收藏时间" width="180"></el-table-column>
        <el-table-column fixed="right" label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewScenic(row)"
              link
            >
              查看景点
            </el-button>
            <el-button 
              type="danger" 
              size="small"
              @click="handleUnfavorite(row)"
              link
            >
              取消收藏
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 20px; text-align: center">
        <el-pagination
          v-model:current-page="pageInfo.pageNum"
          v-model:page-size="pageInfo.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageInfo.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-space>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/http.js'

const router = useRouter()

// 搜索表单
const searchFormRef = ref(null)
const searchForm = ref({
  scenicName: ''
})

// 列表数据
const listData = ref([])
const loading = ref(false)

// 分页信息
const pageInfo = ref({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

/**
 * 获取收藏列表
 */
async function getFavoriteList() {
  loading.value = true
  try {
    const params = {
      ...searchForm.value,
      pageNum: pageInfo.value.pageNum,
      pageSize: pageInfo.value.pageSize
    }
    const res = await request.get('/favorite/page', { params })
    if (res.code === 200) {
      listData.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function search() {
  pageInfo.value.pageNum = 1
  getFavoriteList()
}

/**
 * 重置搜索
 */
function resetSearch() {
  searchFormRef.value?.resetFields()
  pageInfo.value.pageNum = 1
  getFavoriteList()
}

/**
 * 分页大小改变
 */
function handleSizeChange(size) {
  pageInfo.value.pageSize = size
  getFavoriteList()
}

/**
 * 页码改变
 */
function handleCurrentChange(page) {
  pageInfo.value.pageNum = page
  getFavoriteList()
}

/**
 * 查看景点详情
 */
function viewScenic(row) {
  if (row.scenicId) {
    router.push(`/front/scenicDetails/${row.scenicId}`)
  } else {
    ElMessage.warning('景点信息不完整')
  }
}

/**
 * 取消收藏
 */
async function handleUnfavorite(row) {
  try {
    await ElMessageBox.confirm('确定要取消收藏该景点吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete(`/favorite/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('取消收藏成功')
      getFavoriteList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  getFavoriteList()
})
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>

