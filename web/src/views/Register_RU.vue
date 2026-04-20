<template>
  <div class="register-container">
    <div class="left-section">
      <div class="background-overlay"></div>
      <div class="decoration-content">
        <div class="brand-section">
          <div class="brand-icon">
            <el-icon :size="60"><UserFilled /></el-icon>
          </div>
          <h1 class="brand-title">{{ $t('register.title') }}</h1>
          <p class="brand-slogan">Начните свое путешествие исследований</p>
        </div>
        
        <div class="features">
          <div class="feature-item" v-for="(feature, index) in features" :key="index">
            <el-icon :size="24" class="feature-icon">
              <component :is="feature.icon" />
            </el-icon>
            <div class="feature-text">
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.desc }}</p>
            </div>
          </div>
        </div>

        <div class="decoration-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
        </div>
      </div>
    </div>

    <div class="right-section">
      <div class="register-box">
        <div class="register-header">
          <h2 class="register-title">Создать аккаунт</h2>
          <p class="register-subtitle">Заполните информацию для завершения регистрации</p>
        </div>

        <el-form 
          ref="formRef" 
          :model="formData" 
          :rules="rules" 
          label-width="0"
          class="register-form"
        >
          <el-form-item prop="account">
            <el-input
              v-model.trim="formData.account"
              placeholder="Введите телефон или email"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model.trim="formData.password"
              type="password"
              placeholder="Введите пароль (6-20 символов)"
              size="large"
              show-password
              clearable
              maxlength="20"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model.trim="formData.confirmPassword"
              type="password"
              placeholder="Введите пароль еще раз"
              size="large"
              show-password
              clearable
              maxlength="20"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-button 
            type="primary" 
            size="large" 
            @click="submitForm"
            class="register-button"
          >
            {{ $t('register.register') }}
          </el-button>

          <div class="login-section">
            <span class="login-text">{{ $t('register.hasAccount') }}</span>
            <router-link to="/login" class="login-link">
              {{ $t('register.goLogin') }}
            </router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, PictureFilled, Camera, TrendCharts } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import http from '@/utils/http.js'
import router from '@/router/index.js'

const { t } = useI18n()
const formRef = ref(null)
const formData = ref({
  account: '',
  password: '',
  confirmPassword: ''
})

const features = [
  {
    icon: 'PictureFilled',
    title: 'Гибкая регистрация',
    desc: 'Регистрация по телефону или email, свободный выбор'
  },
  {
    icon: 'Camera',
    title: 'Несколько способов входа',
    desc: 'Поддержка входа по телефону, email, имени пользователя'
  },
  {
    icon: 'TrendCharts',
    title: 'Бонус для новых пользователей',
    desc: 'При регистрации 1000 рублей на баланс, посещайте избранные места'
  }
]

const validateAccount = (rule, value, callback) => {
  if (!value) {
    callback(new Error(t('register.pleaseEnterPhone')))
  } else {
    // Убираем + и пробелы для проверки
    const cleanValue = value.replace(/[+\s-]/g, '')
    const isPhone = /^[78]\d{10}$/.test(cleanValue)
    const isEmail = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)
    
    if (!isPhone && !isEmail) {
      callback(new Error('Введите правильный формат телефона или email'))
    } else {
      callback()
    }
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error(t('register.pleaseEnterPassword')))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error(t('register.passwordLength')))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error(t('register.pleaseEnterConfirmPassword')))
  } else if (value !== formData.value.password) {
    callback(new Error(t('register.passwordNotMatch')))
  } else {
    callback()
  }
}

const rules = ref({
  account: [{ validator: validateAccount, trigger: 'blur' }],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
})

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    
    // Убираем + и пробелы для проверки
    const cleanAccount = formData.value.account.replace(/[+\s-]/g, '')
    const isPhone = /^[78]\d{10}$/.test(cleanAccount)
    const requestData = {
      password: formData.value.password,
      confirmPassword: formData.value.confirmPassword
    }
    
    if (isPhone) {
      requestData.phone = formData.value.account
    } else {
      requestData.email = formData.value.account
    }
    
    http.put('/common/register', requestData).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: t('register.registerSuccess') + '! Подарок 1000 рублей на баланс, пожалуйста, войдите',
        type: 'success',
        duration: 3000
      })
      setTimeout(() => {
        router.push({ path: '/login' })
      }, 1500)
    })
  })
}
</script>

<style scoped>
/* Стили остаются такими же, как в оригинальном файле */
.register-container {
  min-height: 100vh;
  display: flex;
  background: #f5f7fa;
}

.left-section {
  flex: 1;
  background: url('https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=1200&q=80') center/cover no-repeat;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(24, 144, 255, 0.85) 0%, rgba(9, 109, 217, 0.9) 100%);
  z-index: 1;
}

.decoration-content {
  position: relative;
  z-index: 2;
  color: white;
  max-width: 500px;
}

.brand-section {
  text-align: center;
  margin-bottom: 60px;
}

.brand-icon {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
  backdrop-filter: blur(10px);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.brand-title {
  font-size: 42px;
  font-weight: 700;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.brand-slogan {
  font-size: 24px;
  font-weight: 700;
  opacity: 0.95;
  margin: 0;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.features {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  transition: all 0.3s;
  animation: slideIn 0.6s ease-out backwards;
}

.feature-item:nth-child(1) { animation-delay: 0.1s; }
.feature-item:nth-child(2) { animation-delay: 0.2s; }
.feature-item:nth-child(3) { animation-delay: 0.3s; }

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(10px);
}

.feature-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-text h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
  line-height: 1.6;
}

.decoration-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 1;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  animation: rotate 20s linear infinite;
}

.shape-1 {
  width: 400px;
  height: 400px;
  top: -200px;
  right: -200px;
}

.shape-2 {
  width: 300px;
  height: 300px;
  bottom: -150px;
  left: -150px;
  animation-delay: 5s;
}

.shape-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 20%;
  animation-delay: 10s;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.right-section {
  flex: 0 0 500px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  box-shadow: -8px 0 32px rgba(0, 0, 0, 0.12);
  overflow-y: auto;
}

.register-box {
  width: 100%;
  max-width: 400px;
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-title {
  font-size: 32px;
  font-weight: 700;
  color: #262626;
  margin: 0 0 12px 0;
}

.register-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.register-form {
  margin-top: 32px;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: none;
  transition: all 0.3s;
  margin-bottom: 24px;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(24, 144, 255, 0.3);
}

.login-section {
  text-align: center;
  font-size: 14px;
}

.login-text {
  color: #8c8c8c;
}

.login-link {
  color: #1890ff;
  text-decoration: none;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.3s;
}

.login-link:hover {
  color: #096dd9;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 0 0 1px #e8e8e8 inset;
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #1890ff inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #1890ff inset;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

@media (max-width: 1024px) {
  .left-section {
    display: none;
  }

  .right-section {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .right-section {
    padding: 20px;
  }

  .register-title {
    font-size: 28px;
  }
}
</style>
