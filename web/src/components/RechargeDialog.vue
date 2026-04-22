<template>
  <el-dialog
    v-model="dialogVisible"
    :title="$t('user.accountRecharge')"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="recharge-dialog">
      <!-- 当前余额 -->
      <div class="balance-info">
        <div class="balance-label">{{ $t('user.currentBalance') }}</div>
        <div class="balance-amount">¥{{ currentBalance }}</div>
      </div>

      <!-- 快捷金额选择 -->
      <div class="amount-options">
        <div class="options-label">{{ $t('user.selectAmount') }}</div>
        <div class="options-grid">
          <div
            v-for="amount in quickAmounts"
            :key="amount"
            class="amount-option"
            :class="{ active: rechargeAmount === amount }"
            @click="rechargeAmount = amount"
          >
            <span class="amount-value">¥{{ amount }}</span>
          </div>
        </div>
      </div>

      <!-- 自定义金额 -->
      <div class="custom-amount">
        <div class="custom-label">{{ $t('user.customAmount') }}</div>
        <el-input
          v-model.number="rechargeAmount"
          type="number"
          :placeholder="$t('user.enterAmount')"
          size="large"
          :min="1"
          :max="10000"
        >
          <template #prefix>¥</template>
        </el-input>
        <div class="amount-tip">{{ $t('user.amountLimit') }}</div>
      </div>

      <!-- 充值方式选择 -->
      <div class="recharge-methods">
        <div class="methods-label">{{ $t('user.selectRechargeMethod') }}</div>
        <div class="methods-grid">
          <div
            class="recharge-method"
            :class="{ active: rechargeMethod === 'ALIPAY' }"
            @click="rechargeMethod = 'ALIPAY'"
          >
            <el-icon :size="32"><CreditCard /></el-icon>
            <span>{{ $t('user.alipay') }}</span>
          </div>
          <div
            class="recharge-method"
            :class="{ active: rechargeMethod === 'WECHAT' }"
            @click="rechargeMethod = 'WECHAT'"
          >
            <el-icon :size="32"><ChatDotRound /></el-icon>
            <span>{{ $t('user.wechat') }}</span>
          </div>
          <div
            class="recharge-method"
            :class="{ active: rechargeMethod === 'BANK' }"
            @click="rechargeMethod = 'BANK'"
          >
            <el-icon :size="32"><Wallet /></el-icon>
            <span>{{ $t('user.bankCard') }}</span>
          </div>
        </div>
      </div>

      <!-- 充值说明 -->
      <el-alert
        :title="$t('user.rechargeNote')"
        type="info"
        :closable="false"
        show-icon
        class="recharge-tip"
      >
        <template #default>
          <div>{{ $t('user.rechargeNote1') }}</div>
          <div>{{ $t('user.rechargeNote2') }}</div>
          <div>{{ $t('user.rechargeNote3') }}</div>
        </template>
      </el-alert>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">{{ $t('common.cancel') }}</el-button>
        <el-button 
          type="primary" 
          @click="handleRecharge"
          :loading="recharging"
          :disabled="!rechargeAmount || rechargeAmount < 1 || rechargeAmount > 10000"
        >
          {{ recharging ? $t('user.recharging') : $t('user.confirmRecharge', { amount: rechargeAmount || 0 }) }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Wallet, CreditCard, ChatDotRound } from '@element-plus/icons-vue'
import http from '@/utils/http.js'
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  currentBalance: {
    type: [Number, String],
    default: 0
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const rechargeAmount = ref(100)
const rechargeMethod = ref('ALIPAY')
const recharging = ref(false)

const quickAmounts = [50, 100, 200, 500, 1000, 2000]

// 处理充值
const handleRecharge = async () => {
  if (recharging.value) return

  if (!rechargeAmount.value || rechargeAmount.value < 1) {
    ElMessage.warning($t('user.enterRechargeAmount'))
    return
  }

  if (rechargeAmount.value > 10000) {
    ElMessage.warning($t('user.exceedLimit'))
    return
  }

  recharging.value = true

  try {
    const res = await http.post('/payment/recharge', null, {
      params: {
        amount: rechargeAmount.value,
        rechargeMethod: rechargeMethod.value
      }
    })

    if (res) {
      ElMessage.success(res.data || $t('user.rechargeSuccess'))
      dialogVisible.value = false
      emit('success', rechargeAmount.value)
      rechargeAmount.value = 100
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || $t('user.rechargeFailed'))
  } finally {
    recharging.value = false
  }
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style scoped>
.recharge-dialog {
  padding: 20px 0;
}

.balance-info {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  color: white;
  margin-bottom: 24px;
}

.balance-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.balance-amount {
  font-size: 36px;
  font-weight: 700;
}

.amount-options,
.custom-amount,
.recharge-methods {
  margin-bottom: 24px;
}

.options-label,
.custom-label,
.methods-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  font-weight: 500;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.amount-option {
  padding: 16px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.amount-option:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.amount-option.active {
  border-color: #409eff;
  background: #409eff;
  color: white;
}

.amount-value {
  font-size: 18px;
  font-weight: 600;
}

.custom-amount :deep(.el-input) {
  margin-bottom: 8px;
}

.amount-tip {
  font-size: 12px;
  color: #909399;
}

.methods-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.recharge-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
}

.recharge-method:hover {
  border-color: #409eff;
  color: #409eff;
  background: #f0f9ff;
}

.recharge-method.active {
  border-color: #409eff;
  color: #409eff;
  background: #f0f9ff;
}

.recharge-method span {
  font-size: 14px;
  font-weight: 500;
}

.recharge-tip {
  margin-top: 16px;
}

.recharge-tip :deep(.el-alert__content) {
  font-size: 13px;
  line-height: 1.8;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
