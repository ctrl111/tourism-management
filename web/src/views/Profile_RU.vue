<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- Левая сторона: карточка личной информации -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>{{ $t('user.personalInfo') }}</span>
            </div>
          </template>
          
          <div class="profile-info">
            <!-- Аватар -->
            <div class="avatar-section">
              <el-avatar :size="120" :src="userInfo.avatarUrl || defaultAvatar" />
              <el-button 
                type="primary" 
                size="small" 
                @click="showEditDialog = true"
                class="edit-avatar-btn"
              >
                <el-icon><Edit /></el-icon>
                {{ $t('user.editInfo') }}
              </el-button>
            </div>

            <!-- Основная информация -->
            <div class="info-list">
              <div class="info-item">
                <span class="label">
                  <el-icon><User /></el-icon>
                  {{ $t('user.username') }}
                </span>
                <span class="value">{{ userInfo.username || '-' }}</span>
              </div>

              <div class="info-item">
                <span class="label">
                  <el-icon><Phone /></el-icon>
                  {{ $t('user.phone') }}
                </span>
                <span class="value">{{ userInfo.tel || '-' }}</span>
              </div>

              <div class="info-item">
                <span class="label">
                  <el-icon><Message /></el-icon>
                  {{ $t('user.email') }}
                </span>
                <span class="value">{{ userInfo.email || '-' }}</span>
              </div>

              <div class="info-item">
                <span class="label">
                  <el-icon><Wallet /></el-icon>
                  {{ $t('user.accountBalance') }}
                </span>
                <span class="value balance">₽{{ userInfo.balance || 0 }}</span>
              </div>

              <div class="info-item">
                <span class="label">
                  <el-icon><UserFilled /></el-icon>
                  {{ $t('user.role') }}
                </span>
                <span class="value">
                  <el-tag :type="userInfo.type === 'ADMIN' ? 'danger' : 'success'" size="small">
                    {{ userInfo.type === 'ADMIN' ? $t('user.admin') : $t('user.normalUser') }}
                  </el-tag>
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- Правая сторона: область действий -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <!-- Быстрые действия -->
        <el-card class="action-card">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>{{ $t('user.quickActions') }}</span>
            </div>
          </template>

          <el-row :gutter="16" class="action-buttons">
            <el-col :xs="12" :sm="8" :md="6">
              <div class="action-item" @click="showEditDialog = true">
                <el-icon :size="32" color="#409EFF"><Edit /></el-icon>
                <span>{{ $t('user.editProfile') }}</span>
              </div>
            </el-col>

            <el-col :xs="12" :sm="8" :md="6">
              <div class="action-item" @click="showPasswordDialog = true">
                <el-icon :size="32" color="#67C23A"><Lock /></el-icon>
                <span>{{ $t('user.changePassword') }}</span>
              </div>
            </el-col>

            <el-col :xs="12" :sm="8" :md="6">
              <div class="action-item" @click="handleLogout">
                <el-icon :size="32" color="#F56C6C"><SwitchButton /></el-icon>
                <span>{{ $t('user.logout') }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- Статистика аккаунта -->
        <el-card class="stats-card">
          <template #header>
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span>{{ $t('user.accountStats') }}</span>
            </div>
          </template>

          <el-row :gutter="16" class="stats-row">
            <el-col :xs="12" :sm="6">
              <div class="stat-item">
                <div class="stat-value">{{ stats.orderCount }}</div>
                <div class="stat-label">{{ $t('user.orderCount') }}</div>
              </div>
            </el-col>

            <el-col :xs="12" :sm="6">
              <div class="stat-item">
                <div class="stat-value">{{ stats.favoriteCount }}</div>
                <div class="stat-label">{{ $t('user.favoriteCount') }}</div>
              </div>
            </el-col>

            <el-col :xs="12" :sm="6">
              <div class="stat-item">
                <div class="stat-value">{{ stats.noteCount }}</div>
                <div class="stat-label">{{ $t('user.noteCount') }}</div>
              </div>
            </el-col>

            <el-col :xs="12" :sm="6">
              <div class="stat-item">
                <div class="stat-value">{{ stats.commentCount }}</div>
                <div class="stat-label">{{ $t('user.commentCount') }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- Диалог редактирования профиля -->
    <el-dialog
      v-model="showEditDialog"
      :title="$t('user.editProfile')"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="120px">
        <el-form-item :label="$t('user.avatar')" prop="avatarUrl">
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
            :placeholder="$t('form.pleaseEnter', { field: $t('user.username') })"
            clearable
          />
          <div class="form-tip">{{ $t('user.usernameTip') }}</div>
        </el-form-item>

        <el-form-item :label="$t('user.phone')" prop="tel">
          <el-input 
            v-model="editForm.tel" 
            :placeholder="$t('form.pleaseEnter', { field: $t('user.phone') })"
            clearable
          />
        </el-form-item>

        <el-form-item :label="$t('user.email')" prop="email">
          <el-input 
            v-model="editForm.email" 
            :placeholder="$t('form.pleaseEnter', { field: $t('user.email') })"
            clearable
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showEditDialog = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleUpdateInfo">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>

    <!-- Диалог изменения пароля -->
    <el-dialog
      v-model="showPasswordDialog"
      :title="$t('user.changePassword')"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="140px">
        <el-form-item :label="$t('user.oldPassword')" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password"
            :placeholder="$t('form.pleaseEnter', { field: $t('user.oldPassword') })"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item :label="$t('user.newPassword')" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password"
            placeholder="Введите новый пароль (6-20 символов)"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item :label="$t('user.confirmPassword')" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password"
            :placeholder="$t('form.pleaseEnter', { field: $t('user.confirmPassword') })"
            show-password
            clearable
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showPasswordDialog = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleUpdatePassword">{{ $t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, Phone, Message, Wallet, UserFilled, Setting, 
  Edit, Lock, SwitchButton, DataAnalysis 
} from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import http from '@/utils/http.js'
import router from '@/router/index.js'
import MyUpLoad from '@/components/MyUpload.vue'

const { t } = useI18n()

// Аватар по умолчанию
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// Информация о пользователе
const userInfo = ref({})

// Статистические данные
const stats = reactive({
  orderCount: 0,
  favoriteCount: 0,
  noteCount: 0,
  commentCount: 0
})

// Состояние отображения диалогов
const showEditDialog = ref(false)
const showPasswordDialog = ref(false)

// Форма редактирования
const editFormRef = ref(null)
const editForm = ref({
  id: '',
  username: '',
  tel: '',
  email: '',
  avatarUrl: ''
})

// Правила валидации формы редактирования
const editRules = {
  username: [
    { required: true, message: t('form.required', { field: t('user.username') }), trigger: 'blur' },
    { min: 2, max: 50, message: t('form.lengthError', { field: t('user.username'), min: 2, max: 50 }), trigger: 'blur' }
  ],
  tel: [
    { pattern: /^(\+?[78])\d{10}$/, message: t('form.formatError', { field: t('user.phone') }), trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: t('form.formatError', { field: t('user.email') }), trigger: 'blur' }
  ]
}

// Форма пароля
const passwordFormRef = ref(null)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// Правила валидации формы пароля
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error(t('form.pleaseEnter', { field: t('user.confirmPassword') })))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error(t('register.passwordNotMatch')))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: t('form.required', { field: t('user.oldPassword') }), trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: t('form.required', { field: t('user.newPassword') }), trigger: 'blur' },
    { min: 6, max: 20, message: t('form.lengthError', { field: t('user.newPassword'), min: 6, max: 20 }), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// Загрузка информации о пользователе
const loadUserInfo = () => {
  http.get('/common/currentUser').then(res => {
    if (res && res.data) {
      userInfo.value = res.data
      // Обновление локального хранилища
      localStorage.setItem('currentUser', JSON.stringify(res.data))
      
      // Инициализация формы редактирования
      editForm.value = {
        id: res.data.id,
        username: res.data.username,
        tel: res.data.tel,
        email: res.data.email,
        avatarUrl: res.data.avatarUrl
      }
    }
  })
}

// Загрузка статистических данных
const loadStats = () => {
  // Здесь можно вызвать реальный API статистики
  // Временно используем тестовые данные
  stats.orderCount = 0
  stats.favoriteCount = 0
  stats.noteCount = 0
  stats.commentCount = 0
}

// Обновление личной информации
const handleUpdateInfo = () => {
  editFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }

    http.post('/common/updateCurrentUser', editForm.value).then(res => {
      if (res) {
        ElMessage.success(t('user.updateSuccess'))
        showEditDialog.value = false
        loadUserInfo()
      }
    })
  })
}

// Изменение пароля
const handleUpdatePassword = () => {
  passwordFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }

    http.post('/common/updatePassword', passwordForm.value).then(res => {
      if (res) {
        ElMessage.success(t('user.changePasswordSuccess') + ', ' + t('message.pleaseLoginAgain'))
        showPasswordDialog.value = false
        
        // Очистка локального хранилища и переход на страницу входа
        setTimeout(() => {
          localStorage.clear()
          router.push('/login')
        }, 1500)
      }
    })
  })
}

// Выход из системы
const handleLogout = () => {
  ElMessageBox.confirm(t('user.logoutConfirm'), t('common.confirm'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(() => {
    localStorage.clear()
    ElMessage.success(t('user.logoutSuccess'))
    router.push('/login')
  }).catch(() => {})
}

// Загрузка данных при монтировании страницы
onMounted(() => {
  loadUserInfo()
  loadStats()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

/* Карточка личной информации */
.profile-card {
  margin-bottom: 20px;
}

.profile-info {
  text-align: center;
}

.avatar-section {
  margin-bottom: 30px;
}

.avatar-section .el-avatar {
  margin-bottom: 16px;
  border: 3px solid #f0f0f0;
}

.edit-avatar-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 0 auto;
}

.info-list {
  text-align: left;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 14px;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
}

.info-item .value.balance {
  color: #f56c6c;
  font-size: 18px;
  font-weight: 600;
}

/* Карточка действий */
.action-card {
  margin-bottom: 20px;
}

.action-buttons {
  padding: 10px 0;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: #f9fafc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #ecf5ff;
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-item span {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

/* Карточка статистики */
.stats-card {
  margin-bottom: 20px;
}

.stats-row {
  padding: 10px 0;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.stat-item:nth-child(2) .stat-item {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-item:nth-child(3) .stat-item {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-item:nth-child(4) .stat-item {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

/* Подсказка формы */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* Адаптивный дизайн */
@media (max-width: 768px) {
  .profile-container {
    padding: 10px;
  }

  .action-item {
    padding: 16px;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>
