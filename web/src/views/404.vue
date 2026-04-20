<template>
  <div class="not-found">
    <el-card class="card" shadow="hover">
      <h1 class="error-code">404</h1>
      <h2 class="error-message">{{ $t('common.pageNotFound') }}</h2>
      <p class="error-description">{{ $t('common.pageNotFoundDesc') }}</p>
      <el-button type="primary" @click="goHome">{{ $t('common.backToHome') }}</el-button>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { t } = useI18n()

const goHome = () => {
  router.push({ path: '/' })
}
</script>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f9f9f9;
}

.card {
  text-align: center;
  padding: 40px;
  border-radius: 10px;
  max-width: 500px;
}

.error-code {
  font-size: 100px;
  margin: 0;
  color: #f56c6c;
  font-weight: 700;
}

.error-message {
  font-size: 24px;
  margin: 20px 0 10px;
  color: #606266;
}

.error-description {
  font-size: 14px;
  color: #909399;
  margin-bottom: 30px;
}
</style>