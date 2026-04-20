import { defineStore } from 'pinia'
import request from '@/utils/http.js'
import i18n from '@/i18n'

const { t } = i18n.global

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: '',
    isLoggedIn: false,
    rememberMe: localStorage.getItem('rememberMe') === 'true' || false,
    userType: null // 'USER' 或 'ADMIN'
  }),

  getters: {
    // 获取用户信息
    getUserInfo: (state) => state.userInfo,
    // 是否已登录
    getIsLoggedIn: (state) => !!state.token && state.isLoggedIn,
    // 获取用户类型
    getUserType: (state) => state.userInfo?.type || '',
    // 获取用户名
    getUsername: (state) => state.userInfo?.username || '',
    // 是否记住密码
    getRememberMe: (state) => state.rememberMe
  },

  actions: {
    // 获取存储 key（根据用户类型）
    _getStorageKey(key, userType) {
      const type = userType || this.userType || 'user'
      const prefix = type === 'ADMIN' ? 'admin' : 'user'
      return `${prefix}_${key}`
    },

    // 设置token
    setToken(token, userType) {
      this.token = token
      const key = this._getStorageKey('token', userType)
      if (token) {
        localStorage.setItem(key, token)
      } else {
        localStorage.removeItem(key)
      }
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.isLoggedIn = true
      this.userType = userInfo?.type
      
      if (userInfo) {
        const key = this._getStorageKey('currentUser', userInfo.type)
        localStorage.setItem(key, JSON.stringify(userInfo))
      }
    },

    // 设置记住我
    setRememberMe(remember) {
      this.rememberMe = remember
      if (remember) {
        localStorage.setItem('rememberMe', 'true')
      } else {
        localStorage.removeItem('rememberMe')
        localStorage.removeItem('savedUsername')
      }
    },

    // 登录
    async login(loginData) {
      try {
        // 调用后端登录接口
        const response = await request.post('/common/login', loginData)
        
        if (response && response.code === 200) {
          const token = response.data
          
          // 临时设置 token 用于请求用户信息
          this.token = token
          localStorage.setItem('token', token)
          
          // 获取当前用户信息
          const userResponse = await request.get('/common/currentUser')
          if (userResponse && userResponse.code === 200) {
            const userData = userResponse.data
            
            // 根据用户类型保存 token 和用户信息
            this.setToken(token, userData.type)
            this.setUserInfo(userData)
            
            // 保持临时 token 用于后续 HTTP 请求（不清除）
            
            // 处理记住我功能
            if (loginData.rememberMe) {
              this.setRememberMe(true)
              const usernameKey = userData.type === 'ADMIN' ? 'admin_savedUsername' : 'user_savedUsername'
              localStorage.setItem(usernameKey, loginData.username)
            } else {
              this.setRememberMe(false)
            }
            
            return {
              success: true,
              data: userData,
              message: t('message.loginSuccess')
            }
          }
        }
        
        return {
          success: false,
          message: response?.msg || t('message.loginFailed')
        }
      } catch (error) {
        console.error('登录失败:', error)
        return {
          success: false,
          message: error.message || t('message.loginFailedRetry')
        }
      }
    },

    // 获取当前用户信息
    async fetchUserInfo() {
      try {
        const response = await request.get('/common/currentUser')
        if (response && response.code === 200) {
          this.setUserInfo(response.data)
          return response.data
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取失败，清除登录状态
        this.logout()
      }
    },

    // 退出登录（只清除当前类型的会话）
    logout() {
      const currentType = this.userType
      
      // 清除当前用户类型的数据
      if (currentType) {
        const tokenKey = this._getStorageKey('token', currentType)
        const userKey = this._getStorageKey('currentUser', currentType)
        localStorage.removeItem(tokenKey)
        localStorage.removeItem(userKey)
      }
      
      // 清除 Store 状态
      this.token = ''
      this.userInfo = null
      this.isLoggedIn = false
      this.userType = null
      
      // 检查是否还有其他类型的会话
      const hasUserSession = localStorage.getItem('user_token')
      const hasAdminSession = localStorage.getItem('admin_token')
      
      // 只有在没有任何会话时才清除临时 token
      if (!hasUserSession && !hasAdminSession) {
        localStorage.removeItem('token')
      }
      
      // 保留 rememberMe 和 savedUsername，方便下次登录
    },

    // 初始化用户状态（根据页面类型加载对应的会话）
    async initUserState(expectedType = null) {
      // 优先尝试加载指定类型的会话，否则尝试两种类型
      const typesToTry = expectedType ? [expectedType] : ['ADMIN', 'USER']
      
      for (const type of typesToTry) {
        const tokenKey = this._getStorageKey('token', type)
        const userKey = this._getStorageKey('currentUser', type)
        
        const token = localStorage.getItem(tokenKey)
        const currentUser = localStorage.getItem(userKey)
        
        if (token && currentUser) {
          this.token = token
          this.userType = type
          
          // 设置临时 token 用于 HTTP 请求
          localStorage.setItem('token', token)
          
          try {
            this.userInfo = JSON.parse(currentUser)
            this.isLoggedIn = true
            
            // 尝试刷新用户信息
            try {
              await this.fetchUserInfo()
            } catch (error) {
              console.error('刷新用户信息失败:', error)
              // 如果刷新失败但有缓存，继续使用缓存
            }
            
            console.log('✅ 已加载', type, '会话')
            return true
          } catch (e) {
            console.error('解析用户信息失败:', e)
          }
        }
      }
      
      // 如果没有找到任何会话，清空状态
      this.token = ''
      this.userInfo = null
      this.isLoggedIn = false
      this.userType = null
      
      return false
    },

    // 检查登录状态
    checkLoginStatus() {
      return !!this.token && this.isLoggedIn
    }
  }
})

