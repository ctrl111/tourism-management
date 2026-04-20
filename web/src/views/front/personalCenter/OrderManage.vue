<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <!-- 搜索表单 -->
      <el-form ref="searchFormRef" :model="searchForm" inline>
        <el-form-item :label="$t('order.status')" prop="status">
          <el-select 
            v-model="searchForm.status" 
            :placeholder="$t('order.selectOrderStatus')" 
            clearable 
            style="width: 180px"
          >
            <el-option :label="$t('order.pending')" value="PENDING"></el-option>
            <el-option :label="$t('order.paid')" value="PAID"></el-option>
            <el-option :label="$t('order.cancelled')" value="CANCELLED"></el-option>
            <el-option :label="$t('order.refunded')" value="REFUNDED"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="search">{{ $t('common.search') }}</el-button>
          <el-button :icon="Refresh" @click="resetSearch">{{ $t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>

      <!-- 订单列表 -->
      <el-table
        :data="listData"
        style="width: 100%"
        v-loading="loading"
        border
      >
        <el-table-column prop="orderNo" :label="$t('order.orderNo')" width="240"></el-table-column>
        <el-table-column prop="scenicName" :label="$t('order.scenicName')" width="150"></el-table-column>
        <el-table-column prop="quantity" :label="$t('order.purchaseQuantity')" width="80" align="center"></el-table-column>
        <el-table-column prop="totalAmount" :label="$t('order.orderAmount')" width="120" align="center">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" :label="$t('order.paymentStatus')" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'PENDING'" type="warning">{{ $t('order.pending') }}</el-tag>
            <el-tag v-else-if="row.status === 'PAID'" type="success">{{ $t('order.paid') }}</el-tag>
            <el-tag v-else-if="row.status === 'CANCELLED'" type="info">{{ $t('order.cancelled') }}</el-tag>
            <el-tag v-else-if="row.status === 'REFUNDED'" type="danger">{{ $t('order.refunded') }}</el-tag>
            <el-tag v-else>{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visitDate" :label="$t('order.visitDate')" width="120"></el-table-column>
        <el-table-column prop="createTime" :label="$t('order.orderDate')" width="180"></el-table-column>
        <el-table-column fixed="right" :label="$t('common.operation')" width="220" align="center">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewDetail(row)"
              link
            >
              {{ $t('order.viewDetails') }}
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING'" 
              type="success" 
              size="small"
              @click="handlePay(row)"
              link
            >
              {{ $t('order.goPay') }}
            </el-button>
            <el-button 
              v-if="row.status === 'PENDING'" 
              type="danger" 
              size="small"
              @click="handleCancel(row)"
              link
            >
              {{ $t('order.cancelOrder') }}
            </el-button>
            <el-button 
              v-if="row.status === 'PAID'" 
              type="warning" 
              size="small"
              @click="handleRefund(row)"
              link
            >
              {{ $t('order.applyRefund') }}
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
      :title="$t('order.orderDetails')"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item :label="$t('order.orderNo')">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item :label="$t('order.status')">
          <el-tag v-if="currentOrder.status === 'PENDING'" type="warning">{{ $t('order.pending') }}</el-tag>
          <el-tag v-else-if="currentOrder.status === 'PAID'" type="success">{{ $t('order.paid') }}</el-tag>
          <el-tag v-else-if="currentOrder.status === 'CANCELLED'" type="info">{{ $t('order.cancelled') }}</el-tag>
          <el-tag v-else-if="currentOrder.status === 'REFUNDED'" type="danger">{{ $t('order.refunded') }}</el-tag>
          <el-tag v-else>{{ currentOrder.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('order.scenicName')">{{ currentOrder.scenicName }}</el-descriptions-item>
        <el-descriptions-item :label="$t('order.purchaseQuantity')">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item :label="$t('order.orderAmount')">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ currentOrder.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('order.visitDate')">{{ currentOrder.visitDate }}</el-descriptions-item>
        <el-descriptions-item :label="$t('order.orderDate')" :span="2">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('common.close') }}</el-button>
        <el-button 
          v-if="currentOrder?.status === 'PENDING'" 
          type="primary" 
          @click="handlePay(currentOrder)"
        >
          {{ $t('order.goPay') }}
        </el-button>
        <el-button 
          v-if="currentOrder?.status === 'PENDING'" 
          type="danger" 
          @click="handleCancel(currentOrder)"
        >
          {{ $t('order.cancelOrder') }}
        </el-button>
        <el-button 
          v-if="currentOrder?.status === 'PAID'" 
          type="warning" 
          @click="handleRefund(currentOrder)"
        >
          {{ $t('order.applyRefund') }}
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 支付对话框 -->
    <PaymentDialog
      v-model="paymentDialogVisible"
      :order-no="paymentOrderNo"
      @success="handlePaymentSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/http.js'
import PaymentDialog from '@/components/PaymentDialog.vue'
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();

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
const paymentDialogVisible = ref(false)
const paymentOrderNo = ref('')

function handlePay(row) {
  paymentOrderNo.value = row.orderNo
  paymentDialogVisible.value = true
}

/**
 * 支付成功回调
 */
function handlePaymentSuccess() {
  paymentDialogVisible.value = false
  ElMessage.success($t('order.paySuccess'))
  getOrderList()
}

/**
 * 取消订单
 */
function handleCancel(row) {
  ElMessageBox.confirm(
    $t('order.cancelOrderConfirm', { orderNo: row.orderNo }),
    $t('order.cancelOrder'),
    {
      confirmButtonText: $t('order.confirmCancel'),
      cancelButtonText: $t('order.thinkAgain'),
      type: 'warning'
    }
  ).then(() => {
    request.post('/payment/cancelOrder', null, {
      params: { orderNo: row.orderNo }
    }).then(res => {
      if (res.code === 200) {
        ElMessage.success($t('order.orderCancelled'))
        getOrderList()
      }
    })
  }).catch(() => {
    ElMessage.info($t('order.operationCancelled'))
  })
}

/**
 * 申请退款
 */
function handleRefund(row) {
  ElMessageBox.confirm(
    $t('order.refundConfirmMsg', { amount: row.totalAmount }),
    $t('order.applyRefund'),
    {
      confirmButtonText: $t('order.confirmRefund'),
      cancelButtonText: $t('common.cancel'),
      type: 'warning'
    }
  ).then(() => {
    request.post('/payment/refund', null, {
      params: { orderNo: row.orderNo }
    }).then(res => {
      if (res.code === 200) {
        ElMessage.success($t('order.refundSuccess'))
        getOrderList()
      }
    })
  }).catch(() => {
    ElMessage.info($t('order.refundCancelled'))
  })
}

// 页面加载时获取数据
onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>

