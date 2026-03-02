<template>
  <div class="register-container">
    <!-- 左侧装饰区域 -->
    <div class="left-section">
      <div class="background-overlay"></div>
      <div class="decoration-content">
        <div class="brand-section">
          <div class="brand-icon">
            <el-icon :size="60"><UserFilled /></el-icon>
          </div>
          <h1 class="brand-title">加入我们</h1>
          <p class="brand-slogan">开启您的旅行探索之旅</p>
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

    <!-- 右侧注册表单区域 -->
    <div class="right-section">
      <div class="register-box">
        <div class="register-header">
          <h2 class="register-title">创建账户</h2>
          <p class="register-subtitle">填写信息完成注册</p>
        </div>

        <el-form 
          ref="formRef" 
          :model="formData" 
          :rules="rules" 
          label-width="0"
          class="register-form"
        >
          <!-- 用户类型选择 -->
          <div class="type-selector">
            <div 
              class="type-option" 
              :class="{ active: formData.type === 'USER' }"
              @click="formData.type = 'USER'"
            >
              <el-icon :size="20"><User /></el-icon>
              <span>普通用户</span>
            </div>
            <div 
              class="type-option" 
              :class="{ active: formData.type === 'ADMIN' }"
              @click="formData.type = 'ADMIN'"
            >
              <el-icon :size="20"><Avatar /></el-icon>
              <span>管理员</span>
            </div>
          </div>

          <!-- 用户名 -->
          <el-form-item prop="username">
            <el-input
              v-model.trim="formData.username"
              placeholder="请输入用户名"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input
              v-model.trim="formData.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              clearable
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 昵称 -->
          <el-form-item prop="nickname">
            <el-input
              v-model.trim="formData.nickname"
              placeholder="请输入昵称"
              size="large"
              clearable
            >
              <template #prefix>
                <el-icon><Postcard /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 注册按钮 -->
          <el-button 
            type="primary" 
            size="large" 
            @click="submitForm"
            class="register-button"
          >
            注 册
          </el-button>

          <!-- 登录链接 -->
          <div class="login-section">
            <span class="login-text">已有账号？</span>
            <router-link to="/login" class="login-link">
              去登录
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
import { User, Lock, Avatar, UserFilled, Postcard, PictureFilled, Camera, TrendCharts } from '@element-plus/icons-vue'
import http from '@/utils/http.js'
import router from '@/router/index.js'

const formRef = ref(null)
const formData = ref({
  type: 'USER',
  username: '',
  nickname: '',
  password: ''
})

const features = [
  {
    icon: 'PictureFilled',
    title: '个性化头像',
    desc: '上传您的专属头像，展示独特个性'
  },
  {
    icon: 'Camera',
    title: '记录旅程',
    desc: '分享旅行照片，记录精彩瞬间'
  },
  {
    icon: 'TrendCharts',
    title: '智能推荐',
    desc: '获取个性化景点推荐服务'
  }
]

const rules = ref({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度在 5 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ]
})

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    http.put('/common/register', formData.value).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: '注册成功，正在跳转',
        type: 'success'
      })
      router.push({ path: '/login' })
    })
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  background: #f5f7fa;
}

/* 左侧装饰区域 */
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

/* 右侧注册表单区域 */
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

/* 用户类型选择器 */
.type-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.type-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border: 2px solid #e8e8e8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #595959;
  background: white;
}

.type-option:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.type-option.active {
  border-color: #1890ff;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
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

/* Element Plus 样式覆盖 */
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

/* 响应式设计 */
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
