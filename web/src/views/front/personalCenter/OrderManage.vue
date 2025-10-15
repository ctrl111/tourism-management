<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <!-- 搜索表单 -->
      <el-form ref="searchFormRef" :model="searchForm" inline>
        <el-form-item label="状态" prop="status">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择订单状态" 
            clearable 
            style="width: 180px"
          >
            <el-option label="已支付" value="已支付"></el-option>
            <el-option label="未支付" value="未支付"></el-option>
            <el-option label="支付失败" value="支付失败"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
          <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 订单列表 -->
      <el-table
        :data="listData"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="orderNo" label="订单号" width="240"></el-table-column>
        <el-table-column prop="scenicName" label="景点" width="150"></el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" align="center"></el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="支付状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="row.status === '已支付' ? 'success' : row.status === '未支付' ? 'warning' : 'danger'"
            >
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visitDate" label="游玩日期" width="120"></el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180"></el-table-column>
        <el-table-column fixed="right" label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewDetail(row)"
              link
            >
              查看详情
            </el-button>
            <el-button 
              v-if="row.status === '未支付'" 
              type="success" 
              size="small"
              @click="handlePay(row)"
              link
            >
              去支付
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

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="currentOrder.status === '已支付' ? 'success' : 'warning'">
            {{ currentOrder.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="景点名称">{{ currentOrder.scenicName }}</el-descriptions-item>
        <el-descriptions-item label="购买数量">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ currentOrder.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="游玩日期">{{ currentOrder.visitDate }}</el-descriptions-item>
        <el-descriptions-item label="下单时间" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentOrder?.status === '未支付'" 
          type="primary" 
          @click="handlePay(currentOrder)"
        >
          去支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/http.js'

// 搜索表单
const searchFormRef = ref(null)
const searchForm = ref({
  status: ''
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

// 订单详情
const detailDialogVisible = ref(false)
const currentOrder = ref(null)

/**
 * 获取订单列表
 */
async function getOrderList() {
  loading.value = true
  try {
    const params = {
      ...searchForm.value,
      pageNum: pageInfo.value.pageNum,
      pageSize: pageInfo.value.pageSize
    }
    const res = await request.get('/orderInfo/page', { params })
    if (res.code === 200) {
      listData.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
function search() {
  pageInfo.value.pageNum = 1
  getOrderList()
}

/**
 * 重置搜索
 */
function resetSearch() {
  searchFormRef.value?.resetFields()
  pageInfo.value.pageNum = 1
  getOrderList()
}

/**
 * 分页大小改变
 */
function handleSizeChange(size) {
  pageInfo.value.pageSize = size
  getOrderList()
}

/**
 * 页码改变
 */
function handleCurrentChange(page) {
  pageInfo.value.pageNum = page
  getOrderList()
}

/**
 * 查看详情
 */
function viewDetail(row) {
  currentOrder.value = { ...row }
  detailDialogVisible.value = true
}

/**
 * 去支付
 */
function handlePay(row) {
  ElMessage.info('支付功能开发中...')
  // TODO: 实现支付功能
}

// 页面加载时获取数据
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>

