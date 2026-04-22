import { defineStore } from 'pinia'

export const useMenuStore = defineStore('menu', {
  state: () => ({
    // 动态菜单内容（可以根据用户权限或系统配置动态加载）
    menuContent: [],
    // 当前激活的菜单路径
    activeMenu: ''
  }),

  getters: {
    // 获取所有菜单
    getMenuContent: (state) => state.menuContent,
    // 获取当前激活菜单
    getActiveMenu: (state) => state.activeMenu
  },

  actions: {
    // 设置菜单内容
    setMenuContent(menuContent) {
      this.menuContent = menuContent
    },

    // 设置当前激活菜单
    setActiveMenu(path) {
      this.activeMenu = path
    },

    // 根据用户类型加载菜单（示例：可以根据实际需求修改）
    loadMenuByUserType(userType) {
      // 这里可以根据用户类型加载不同的菜单
      // 例如：普通用户、VIP用户、管理员等
      const dynamicMenus = []
      
      // 示例：VIP用户可以看到特殊菜单
      if (userType === 'VIP') {
        dynamicMenus.push({
          id: 'vip-zone',
          name: 'VIP专区',
          path: '/front/vipZone'
        })
      }

      this.menuContent = dynamicMenus
    },

    // 从API加载动态菜单（可选）
    async fetchDynamicMenus() {
      try {
        // 这里可以调用API获取动态菜单
        // const response = await request.get('/api/menus')
        // this.menuContent = response.data
        
        // 目前使用空数组，表示没有额外的动态菜单
        this.menuContent = []
      } catch (error) {
        this.menuContent = []
      }
    }
  }
})

