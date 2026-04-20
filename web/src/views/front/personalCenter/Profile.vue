<template>
  <div class="profile-wrapper">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card" shadow="never">
      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="100" :src="userInfo.avatarUrl || defaultAvatar" class="user-avatar" />
          <div class="user-info">
            <h2 class="user-name">{{ userInfo.username || $t('user.notSet') }}</h2>
            <el-tag :type="userInfo.type === 'ADMIN' ? 'danger' : 'success'" size="small" class="user-tag">
              {{ userInfo.type === 'ADMIN' ? $t('user.admin') : $t('user.normalUser') }}
            </el-tag>
          </div>
        </div>
        <el-button type="primary" @click="showEditDialog = true" class="edit-btn">
          <el-icon><Edit /></el-icon>
          {{ $t('personalCenter.editProfile') }}
        </el-button>
      </div>

      <el-divider />

      <div class="info-grid">
        <div class="info-item">
          <div class="info-label">
            <el-icon><User /></el-icon>
            <span>{{ $t('user.username') }}</span>
          </div>
          <div class="info-value">{{ userInfo.username || '-' }}</div>
        </div>

        <div class="info-item">
          <div class="info-label">
            <el-icon><Phone /></el-icon>
            <span>{{ $t('user.phone') }}</span>
          </div>
          <div class="info-value">{{ userInfo.phone || '-' }}</div>
        </div>

        <div class="info-item">
          <div class="info-label">
            <el-icon><Message /></el-icon>
            <span>{{ $t('user.email') }}</span>
          </div>
          <div class="info-value">{{ userInfo.email || '-' }}</div>
        </div>

        <div class="info-item">
          <div class="info-label">
            <el-icon><Wallet /></el-icon>
            <span>{{ $t('user.accountBalance') }}</span>
          </div>
          <div class="info-value balance">¥{{ userInfo.balance || 0 }}</div>
        </div>

        <div class="info-item">
          <div class="info-label">
            <el-icon><CircleCheck /></el-icon>
            <span>{{ $t('user.accountStatus') }}</span>
          </div>
          <div class="info-value">
            <el-tag :type="userInfo.status === 'ACTIVE' ? 'success' : 'danger'" size="small">
              {{ $t(`status.${userInfo.status}`) || userInfo.status }}
            </el-tag>
          </div>
        </div>

        <div class="info-item">
          <div class="info-label">
            <el-icon><Calendar /></el-icon>
            <span>{{ $t('user.registrationTime') }}</span>
          </div>
          <div class="info-value">{{ userInfo.createTime || '-' }}</div>
        </div>
      </div>
    </el-card>

    <!-- 快捷操作 -->
    <el-card class="action-card" shadow="never">
      <template #header>
        <div class="card-title">
          <el-icon><Operation /></el-icon>
          <span>{{ $t('user.quickActions') }}</span>
        </div>
      </template>

      <div class="action-grid">
        <div class="action-item" @click="showEditDialog = true">
          <div class="action-icon" style="background: #ecf5ff; color: #409eff;">
            <el-icon :size="28"><Edit /></el-icon>
          </div>
          <span class="action-text">{{ $t('personalCenter.editProfile') }}</span>
        </div>

        <div class="action-item" @click="showPasswordDialog = true">
          <div class="action-icon" style="background: #f0f9ff; color: #67c23a;">
            <el-icon :size="28"><Lock /></el-icon>
          </div>
          <span class="action-text">{{ $t('user.changePassword') }}</span>
        </div>

        <div class="action-item" @click="handleLogout">
          <div class="action-icon" style="background: #fef0f0; color: #f56c6c;">
            <el-icon :size="28"><SwitchButton /></el-icon>
          </div>
          <span class="action-text">{{ $t('common.logout') }}</span>
        </div>
      </div>
    </el-card>

    <!-- 编辑资料对话框 -->
    <el-dialog
      v-model="showEditDialog"
      :title="$t('personalCenter.editPersonalInfo')"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item :label="$t('personalCenter.avatar')" prop="avatarUrl">
          <MyUpLoad 
            type="imageCard" 
            :limit="1" 
            :files="editForm.avatarUrl"
            @setFiles="editForm.avatarUrl = $event"
          />
        </el-form-item>

        <el-form-item :label="$t('user.username')" prop="username">
          <el-input 
            v-model="editForm.username" 
            :placeholder="$t('user.pleaseEnterUsername')"
            clearable
          />
          <div class="form-tip">{{ $t('user.usernameTip') }}</div>
        </el-form-item>

        <el-form-item :label="$t('user.phone')" prop="phone">
          <el-input 
            v-model="editForm.phone" 
            :placeholder="$t('register.pleaseEnterPhone')"
            clearable
          />
        </el-form-item>

        <el-form-item :label="$t('user.email')" prop="email">
          <el-input 
            v-model="editForm.email" 
            :placeholder="$t('register.pleaseEnterEmail')"
            clearable
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showEditDialog = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleUpdateInfo">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      :title="$t('user.changePassword')"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item :label="$t('personalCenter.oldPassword')" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password"
            :placeholder="$t('personalCenter.pleaseEnterOldPassword')"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item :label="$t('personalCenter.newPassword')" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password"
            :placeholder="$t('personalCenter.pleaseEnterNewPassword')"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item :label="$t('personalCenter.confirmPassword')" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password"
            :placeholder="$t('personalCenter.pleaseConfirmPassword')"
            show-password
            clearable
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showPasswordDialog = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleUpdatePassword">{{ $t('personalCenter.confirmPasswordChange') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Edit, Lock, SwitchButton, User, Phone, Message, 
  Wallet, CircleCheck, Calendar, Operation 
} from '@element-plus/icons-vue'
import http from '@/utils/http.js'
import utils from '@/utils/tools.js'
import router from '@/router/index.js'
import MyUpLoad from '@/components/MyUpload.vue'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 用户信息
const userInfo = ref({})

// 对话框显示状态
const showEditDialog = ref(false)
const showPasswordDialog = ref(false)

// 编辑表单
const editFormRef = ref(null)
const editForm = ref({
  id: '',
  username: '',
  phone: '',
  email: '',
  avatarUrl: ''
})

// 编辑表单验证规则
const editRules = {
  username: [
    { required: true, message: 'Пожалуйста, введите имя пользователя', trigger: 'blur' },
    { min: 2, max: 50, message: 'Имя пользователя должно быть от 2 до 50 символов', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^(\+?[78])\d{10}$/, message: 'Пожалуйста, введите корректный номер телефона (формат: +7XXXXXXXXXX, 7XXXXXXXXXX или 8XXXXXXXXXX)', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: 'Пожалуйста, введите корректный email', trigger: 'blur' }
  ]
}

// 密码表单
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码表单验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('Пожалуйста, подтвердите пароль'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('Пароли не совпадают'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: 'Пожалуйста, введите текущий пароль', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: 'Пожалуйста, введите новый пароль', trigger: 'blur' },
    { min: 6, max: 20, message: 'Пароль должен быть от 6 до 20 символов', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserInfo = () => {
  http.get('/common/currentUser').then(res => {
    if (res && res.data) {
      userInfo.value = res.data
      // 更新本地存储
      localStorage.setItem('currentUser', JSON.stringify(res.data))
      
      // 初始化编辑表单
      editForm.value = {
        id: res.data.id,
        username: res.data.username,
        phone: res.data.phone,
        email: res.data.email,
        avatarUrl: res.data.avatarUrl
      }
    }
  })
}

// 更新个人信息
const handleUpdateInfo = () => {
  editFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }

    http.post('/common/updateCurrentUser', editForm.value).then(res => {
      if (res) {
        ElMessage.success('Личные данные успешно обновлены')
        showEditDialog.value = false
        loadUserInfo()
      }
    })
  })
}

// 修改密码
const handleUpdatePassword = () => {
  passwordFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }

    http.post('/common/updatePassword', passwordForm.value).then(res => {
      if (res) {
        ElMessage.success('Пароль успешно изменён, пожалуйста, войдите снова')
        showPasswordDialog.value = false
        
        // 清空本地存储并跳转到登录页
        setTimeout(() => {
          localStorage.clear()
          router.push('/login')
        }, 1500)
      }
    })
  })
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('Вы уверены, что хотите выйти из системы?', 'Подсказка', {
    confirmButtonText: 'Подтвердить',
    cancelButtonText: 'Отмена',
    type: 'warning'
  }).then(() => {
    localStorage.clear()
    ElMessage.success('Вы вышли из системы')
    router.push('/login')
  }).catch(() => {})
}

// 页面加载时获取数据
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-wrapper {
  padding: 0;
}

.profile-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  border: 3px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-name {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.user-tag {
  width: fit-content;
}

.edit-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-top: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #909399;
}

.info-label .el-icon {
  font-size: 16px;
}

.info-value {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.balance {
  color: #f56c6c;
  font-size: 20px;
  font-weight: 600;
}

.action-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: #fafafa;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.action-item:hover .action-icon {
  transform: scale(1.1);
}

.action-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

@media (max-width: 1200px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .avatar-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .action-grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
