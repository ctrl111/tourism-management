<template>
  <el-dialog
    v-model="dialogVisible"
    :title="$t('payment.orderPayment')"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="payment-dialog">
      <!-- 订单信息 -->
      <div class="order-info">
        <div class="info-row">
          <span class="label">{{ $t('payment.orderNo') }}</span>
          <span class="value">{{ orderNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">{{ $t('payment.paymentAmount') }}</span>
          <span class="amount">¥{{ totalAmount }}</span>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <div class="methods-label">{{ $t('payment.selectPaymentMethod') }}</div>
        <div class="methods-grid">
          <div
            class="payment-method active"
          >
            <el-icon :size="32"><Wallet /></el-icon>
            <span>{{ $t('payment.balancePayment') }}</span>
            <div class="balance-tip">{{ $t('payment.currentBalance') }} ¥{{ currentBalance }}</div>
          </div>
        </div>
      </div>

      <!-- 支付说明 -->
      <el-alert
        :title="$t('payment.paymentNote')"
        type="info"
        :closable="false"
        show-icon
        class="payment-tip"
      >
        <template #default>
          <div>{{ $t('payment.paymentNote1') }}</div>
          <div>{{ $t('payment.paymentNote2') }}</div>
          <div>{{ $t('payment.paymentNote3') }}</div>
        </template>
      </el-alert>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">{{ $t('payment.payLater') }}</el-button>
        <el-button 
          type="primary" 
          @click="handlePayment"
          :loading="paying"
        >
          {{ paying ? $t('payment.paying') : $t('payment.payNowAmount', { amount: totalAmount }) }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Wallet } from '@element-plus/icons-vue'
import http from '@/utils/http.js'
import utils from '@/utils/tools.js'
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  orderNo: {
    type: String,
    default: ''
  },
  totalAmount: {
    type: [Number, String],
    default: 0
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const paymentMethod = ref('BALANCE')
const paying = ref(false)
const currentBalance = ref(0)

// 获取当前用户余额
const loadUserBalance = () => {
  const user = utils.getCurrentUser()
  if (user) {
    currentBalance.value = parseFloat(user.balance) || 0
  }
}

// 监听对话框打开，重新加载余额
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    loadUserBalance()
  }
})

// 初始化时加载余额
loadUserBalance()

// 处理支付
const handlePayment = async () => {
  if (paying.value) return

  if (!props.orderNo) {
    ElMessage.warning($t('payment.orderNoRequired'))
    return
  }

  // 检查余额
  const amount = parseFloat(props.totalAmount) || 0
  if (currentBalance.value < amount) {
    ElMessage.warning($t('payment.insufficientBalance'))
    return
  }

  paying.value = true

  try {
    const res = await http.post('/payment/pay', null, {
      params: {
        orderNo: props.orderNo,
        paymentMethod: paymentMethod.value
      }
    })

    if (res) {
      ElMessage.success($t('payment.paySuccess'))
      dialogVisible.value = false
      emit('success')
      
      // 刷新用户信息
      http.get("/common/currentUser").then(res1 => {
        let currentUser = res1.data;
        localStorage.setItem("currentUser", JSON.stringify(currentUser));
        loadUserBalance()
      })
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || $t('payment.payFailed'))
  } finally {
    paying.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
.payment-dialog {
  padding: 20px 0;
}

.order-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  font-size: 14px;
  color: #606266;
}

.value {
  font-size: 14px;
  color: #303133;
  font-family: monospace;
}

.amount {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.payment-methods {
  margin-bottom: 24px;
}

.methods-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  font-weight: 500;
}

.methods-grid {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.payment-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 40px;
  border: 2px solid #409eff;
  border-radius: 8px;
  transition: all 0.3s;
  color: #409eff;
  background: #f0f9ff;
  position: relative;
}

.payment-method span {
  font-size: 14px;
  font-weight: 500;
}

.balance-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.payment-tip {
  margin-top: 16px;
}

.payment-tip :deep(.el-alert__content) {
  font-size: 13px;
  line-height: 1.8;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
