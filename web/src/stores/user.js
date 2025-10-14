import { defineStore } from 'pinia'
import request from '@/utils/http.js'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: localStorage.getItem('token') || '',
    isLoggedIn: false
  }),

  getters: {
    // 获取用户信息
    getUserInfo: (state) => state.userInfo,
    // 是否已登录
    getIsLoggedIn: (state) => !!state.token && state.isLoggedIn,
    // 获取用户类型
    getUserType: (state) => state.userInfo?.type || ''
  },

  actions: {
    // 设置token
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      this.isLoggedIn = true
    },

    // 登录
    async login(loginData) {
      try {
        const response = await request.post('/login', loginData)
        if (response && response.code === 200) {
          this.setToken(response.data.token)
          this.setUserInfo(response.data.user)
          return response
        }
        return response
      } catch (error) {
        console.error('登录失败:', error)
        throw error
      }
    },

    // 获取当前用户信息
    async fetchUserInfo() {
      try {
        const response = await request.get('/getCurrentUser')
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
      localStorage.removeItem('userInfo')
    },

    // 初始化用户状态（从localStorage恢复）
    async initUserState() {
      const token = localStorage.getItem('token')
      if (token) {
        this.token = token
        // 尝试获取用户信息
        await this.fetchUserInfo()
      }
    }
  }
})

