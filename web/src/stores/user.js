import { defineStore } from 'pinia'
import request from '@/utils/http.js'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: localStorage.getItem('token') || '',
    isLoggedIn: false,
    rememberMe: localStorage.getItem('rememberMe') === 'true' || false
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
    // 设置token
    setToken(token) {
      this.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.isLoggedIn = true
      if (userInfo) {
        localStorage.setItem('currentUser', JSON.stringify(userInfo))
      } else {
        localStorage.removeItem('currentUser')
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
          
          // 设置token
          this.setToken(token)
          
          // 获取当前用户信息
          const userResponse = await request.get('/common/currentUser')
          if (userResponse && userResponse.code === 200) {
            this.setUserInfo(userResponse.data)
            
            // 处理记住我功能
            if (loginData.rememberMe) {
              this.setRememberMe(true)
              localStorage.setItem('savedUsername', loginData.username)
            } else {
              this.setRememberMe(false)
            }
            
            return {
              success: true,
              data: userResponse.data,
              message: '登录成功'
            }
          }
        }
        
        return {
          success: false,
          message: response?.msg || '登录失败'
        }
      } catch (error) {
        console.error('登录失败:', error)
        return {
          success: false,
          message: error.message || '登录失败，请稍后重试'
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

    // 退出登录
    logout() {
      this.token = ''
      this.userInfo = null
      this.isLoggedIn = false
      localStorage.removeItem('token')
      localStorage.removeItem('currentUser')
      // 保留 rememberMe 和 savedUsername，方便下次登录
    },

    // 初始化用户状态（从localStorage恢复）
    async initUserState() {
      const token = localStorage.getItem('token')
      const currentUser = localStorage.getItem('currentUser')
      
      if (token) {
        this.token = token
        
        // 如果有缓存的用户信息，先使用缓存
        if (currentUser) {
          try {
            this.userInfo = JSON.parse(currentUser)
            this.isLoggedIn = true
          } catch (e) {
            console.error('解析用户信息失败:', e)
          }
        }
        
        // 尝试获取最新的用户信息
        try {
          await this.fetchUserInfo()
        } catch (error) {
          // 如果获取失败但有缓存，继续使用缓存
          console.error('刷新用户信息失败:', error)
        }
      }
    },

    // 检查登录状态
    checkLoginStatus() {
      return !!this.token && this.isLoggedIn
    }
  }
})

