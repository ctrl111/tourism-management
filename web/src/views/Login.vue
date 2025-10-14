<template>
  <div class="login-container">
    <div class="login-box">
      <el-card class="login-card" shadow="hover">
        <!-- 头部Logo和标题 -->
        <div class="login-header">
          <div class="logo-section">
            <img src="../assets/logo.png" alt="Logo" class="logo-img">
            <div class="title-section">
              <h2 class="system-title">旅游推荐系统</h2>
              <span class="system-subtitle">Tourism Recommendation System</span>
            </div>
          </div>
        </div>

        <!-- 登录表单 -->
        <el-form 
          ref="formRef" 
          :model="formData" 
          :rules="formRules" 
          label-width="0"
          class="login-form"
        >
          <!-- 用户名 -->
          <el-form-item prop="username">
            <el-input
              v-model.trim="formData.username"
              :prefix-icon="User"
              placeholder="请输入用户名"
              size="large"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input
              v-model.trim="formData.password"
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <!-- 用户类型 -->
          <el-form-item prop="type">
            <el-select 
              v-model="formData.type" 
              placeholder="请选择用户类型" 
              size="large"
              style="width: 100%"
            >
              <el-option label="管理员" value="ADMIN">
                <el-icon style="margin-right: 8px"><Avatar /></el-icon>
                管理员
              </el-option>
              <el-option label="普通用户" value="USER">
                <el-icon style="margin-right: 8px"><User /></el-icon>
                普通用户
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 验证码 -->
          <el-form-item prop="captcha">
            <div class="captcha-wrapper">
              <el-input
                v-model.trim="formData.captcha"
                placeholder="请输入验证码"
                size="large"
                clearable
                @keyup.enter="handleLogin"
                style="flex: 1"
              >
                <template #prefix>
                  <el-icon><View /></el-icon>
                </template>
              </el-input>
              <Captcha 
                ref="captchaRef" 
                @update:code="handleCaptchaUpdate"
                :width="120"
                :height="40"
              />
            </div>
          </el-form-item>

          <!-- 记住我和忘记密码 -->
          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="formData.rememberMe">记住我</el-checkbox>
              <router-link to="/retrievePassword" class="forgot-password">
                忘记密码？
              </router-link>
            </div>
          </el-form-item>

          <!-- 登录按钮 -->
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              :loading="loading" 
              @click="handleLogin"
              class="login-button"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>

          <!-- 注册链接 -->
          <el-form-item>
            <div class="register-link">
              还没有账号？
              <router-link to="/register" class="link-text">
                立即注册
              </router-link>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Avatar, View } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import Captcha from '@/components/Captcha.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单引用
const formRef = ref(null)
const captchaRef = ref(null)

// 加载状态
const loading = ref(false)

// 验证码
const captchaCode = ref('')

// 表单数据
const formData = reactive({
  username: '',
  password: '',
  type: 'USER',
  captcha: '',
  rememberMe: false
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 5, max: 20, message: '密码长度在 5 到 20 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码为4位', trigger: 'blur' }
  ]
}

/**
 * 处理验证码更新
 */
function handleCaptchaUpdate(code) {
  captchaCode.value = code
}

/**
 * 验证验证码
 */
function validateCaptcha() {
  if (!captchaRef.value) return false
  return captchaRef.value.validate(formData.captcha)
}

/**
 * 处理登录
 */
async function handleLogin() {
  if (loading.value) return

  try {
    // 表单验证
    const valid = await formRef.value.validate()
    if (!valid) return

    // 验证码验证
    if (!validateCaptcha()) {
      ElMessage.error('验证码错误，请重新输入')
      formData.captcha = ''
      captchaRef.value?.refresh()
      return
    }

    loading.value = true

    // 调用登录接口
    const result = await userStore.login({
      username: formData.username,
      password: formData.password,
      type: formData.type,
      rememberMe: formData.rememberMe
    })

    if (result.success) {
      ElMessage.success(result.message || '登录成功')
      
      // 根据redirect参数或用户类型跳转
      setTimeout(() => {
        const redirect = route.query.redirect
        if (redirect) {
          // 如果有重定向参数，跳转到指定页面
          router.push(redirect)
        } else {
          // 否则根据用户类型跳转到默认页面
          if (result.data.type === 'ADMIN') {
            router.push('/admin')
          } else {
            router.push('/front/index')
          }
        }
      }, 500)
    } else {
      ElMessage.error(result.message || '登录失败')
      // 刷新验证码
      formData.captcha = ''
      captchaRef.value?.refresh()
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('登录失败，请稍后重试')
    // 刷新验证码
    formData.captcha = ''
    captchaRef.value?.refresh()
  } finally {
    loading.value = false
  }
}

/**
 * 初始化
 */
onMounted(() => {
  // 如果已经登录，直接跳转
  if (userStore.checkLoginStatus()) {
    const userType = userStore.getUserType
    if (userType === 'ADMIN') {
      router.push('/admin')
    } else {
      router.push('/front/index')
    }
    return
  }

  // 加载保存的用户名
  const savedUsername = localStorage.getItem('savedUsername')
  if (savedUsername) {
    formData.username = savedUsername
    formData.rememberMe = true
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  left: -150px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  right: -100px;
  animation-delay: 5s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 10%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

.login-box {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 450px;
  padding: 20px;
}

.login-card {
  border-radius: 16px;
  padding: 20px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  margin-bottom: 10px;
}

.logo-img {
  width: 60px;
  height: 60px;
  object-fit: contain;
}

.title-section {
  text-align: left;
}

.system-title {
  font-size: 26px;
  font-weight: bold;
  color: #333;
  margin: 0;
  line-height: 1.3;
}

.system-subtitle {
  font-size: 13px;
  color: #999;
  font-style: italic;
}

.login-form {
  margin-top: 20px;
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.forgot-password {
  color: #409EFF;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.forgot-password:hover {
  color: #66b1ff;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.register-link {
  text-align: center;
  width: 100%;
  font-size: 14px;
  color: #666;
}

.link-text {
  color: #409EFF;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.link-text:hover {
  color: #66b1ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
    max-width: 100%;
    padding: 10px;
  }

  .login-card {
    padding: 15px;
  }

  .system-title {
    font-size: 22px;
  }

  .logo-img {
    width: 50px;
    height: 50px;
  }
}

/* Element Plus 样式覆盖 */
:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 8px;
}
</style>
