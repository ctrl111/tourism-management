import {createRouter, createWebHistory} from 'vue-router'
import { ElMessage } from 'element-plus'
import AdminLayout from "@/views/layout/AdminLayout.vue";
import FrontLayout from "@/views/layout/FrontLayout.vue";
import { useUserStore } from '@/stores/user'

const router = createRouter({
    history: createWebHistory(),
    routes: getRoutes()
})

function getRoutes() {
    let defaultRoutes = [
        {
            path: '/admin',
            name: 'admin',
            component: AdminLayout,
            redirect: "/admin/home",
            children: [{
                path: "home",
                name: "admin-home",
                component: () =>
                    import ('../views/admin/Home.vue')
            },
                {
                    path: 'editCurrentUser',
                    name: 'admin-editCurrentUser',
                    component: () =>
                        import ('../views/EditCurrentUser.vue')
                },
                {
                    path: 'editPassword',
                    name: 'admin-editPassword',
                    component: () =>
                        import ('../views/EditPassword.vue')
                },
                {
                    path: 'admin',
                    name: 'Admin',
                    component: () =>
                        import ('../views/admin/AdminManage.vue')
                },
                {
                    path: 'user',
                    name: 'admin-user',
                    component: () =>
                        import ('../views/admin/UserManage.vue')
                },
                {
                    path: 'scenicCategory',
                    name: 'admin-scenicCategory',
                    component: () =>
                        import ('../views/admin/ScenicCategoryManage.vue')
                },
                {
                    path: 'scenicInfo',
                    name: 'admin-scenicInfo',
                    component: () =>
                        import ('../views/admin/ScenicInfoManage.vue')
                },
                {
                    path: 'scenicComment',
                    name: 'admin-scenicComment',
                    component: () =>
                        import ('../views/admin/ScenicCommentManage.vue')
                },
                {
                    path: 'viewHistory',
                    name: 'admin-viewHistory',
                    component: () =>
                        import ('../views/front/personalCenter/ViewHistory.vue')
                },
                {
                    path: 'travelNote',
                    name: 'admin-travelNote',
                    component: () =>
                        import ('../views/admin/TravelNoteManage.vue')
                },
                {
                    path: 'favorite',
                    name: 'admin-favorite',
                    component: () =>
                        import ('../views/admin/Favorite.vue')
                },
                {
                    path: 'notice',
                    name: 'admin-notice',
                    component: () =>
                        import ('../views/admin/NoticeManage.vue')
                },
                {
                    path: 'banner',
                    name: 'admin-banner',
                    component: () =>
                        import ('../views/admin/BannerManage.vue')
                },
                {
                    path: 'order',
                    name: 'admin-order',
                    component: () =>
                        import ('../views/admin/OrderInfoManage.vue')
                },
                {
                    path: 'orderItem',
                    name: 'admin-orderItem',
                    component: () =>
                        import ('../views/admin/OrderItemManage.vue')
                }

            ]
        },
        {
            path: '/front',
            name: 'front',
            component: FrontLayout,
            redirect: "/front/Index",
            children: [{
                path: "index",
                name: "index",
                component: () =>
                    import ('../views/front/Index.vue')
            },
                {
                    path: 'scenic',
                    name: 'scenic',
                    component: () => import('../views/front/Scenic.vue')
                },
                {
                    path: 'travelNote',
                    name: 'travelNote',
                    component: () => import('../views/front/TravelNote.vue')
                },
                {
                    path: 'route',
                    name: 'route',
                    component: () => import('../views/front/Route.vue')
                },
                {
                    path: 'notice',
                    name: 'notice',
                    component: () => import('../views/front/Notice.vue')
                },
                {
                    path: 'feedback',
                    name: 'feedback',
                    component: () => import('../views/front/Feedback.vue')
                },
                {
                    path: 'personalCenter',
                    name: 'personalCenter',
                    component: () => import('../views/front/PersonalCenter.vue')
                },
                {
                    path: 'scenicDetails/:id',
                    name: 'front-scenicDetails',
                    component: () => import('../views/front/ScenicDetails.vue')
                },
                {
                    path: 'travelDetails/:id',
                    name: 'front-travelDetails',
                    component: () => import('../views/front/TravelDetails.vue')
                },
                {
                    path: 'routeDetails/:id',
                    name: 'front-routeDetails',
                    component: () => import('../views/front/RouteDetails.vue')
                },

            ]
        },
        {
            path: "/",
            redirect: "/front/index"
        }, {
            path: "/login",
            name: "login",
            component: () =>
                import ('../views/Login.vue')
        }, {
            path: "/register",
            name: "register",
            component: () =>
                import ('../views/Register.vue')
        }, {
            path: "/retrievePassword",
            name: "front-retrievePassword",
            component: () =>
                import ('../views/RetrievePassword.vue')
        }];
    defaultRoutes.push({
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        meta: {
            name: ''
        },
        component: () => import ('../views/404.vue')
    })
    console.log('getDynamicRoutes', defaultRoutes)
    return defaultRoutes;
}

/**
 * 白名单：不需要登录就能访问的路由
 */
const whiteList = ['/login', '/register', '/retrievePassword', '/front/index']

/**
 * 需要登录的前端路由（部分页面）
 */
const requireAuthFrontRoutes = ['/front/personalCenter', '/front/orders', '/front/favorite', '/front/viewHistory']

/**
 * 路由守卫
 */
router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    
    // 获取token
    const token = userStore.token || localStorage.getItem('token')
    
    // 如果已登录
    if (token) {
        if (to.path === '/login') {
            // 已登录用户访问登录页，重定向到首页
            const userType = userStore.getUserType
            if (userType === 'ADMIN') {
                next('/admin')
            } else {
                next('/front/index')
            }
        } else {
            // 确保用户信息已加载
            if (!userStore.userInfo) {
                try {
                    await userStore.fetchUserInfo()
                    next()
                } catch (error) {
                    // 获取用户信息失败，清除登录状态
                    userStore.logout()
                    ElMessage.error('登录已过期，请重新登录')
                    next(`/login?redirect=${to.path}`)
                }
            } else {
                // 检查权限：管理员路由只能管理员访问
                if (to.path.startsWith('/admin')) {
                    if (userStore.getUserType === 'ADMIN') {
                        next()
                    } else {
                        ElMessage.warning('您没有权限访问该页面')
                        next('/front/index')
                    }
                } else {
                    next()
                }
            }
        }
    } else {
        // 未登录
        if (whiteList.includes(to.path)) {
            // 在白名单中，直接放行
            next()
        } else if (to.path.startsWith('/admin')) {
            // 管理员路由需要登录
            ElMessage.warning('请先登录')
            next(`/login?redirect=${to.path}`)
        } else if (requireAuthFrontRoutes.some(route => to.path.startsWith(route))) {
            // 部分前端路由需要登录
            ElMessage.warning('请先登录')
            next(`/login?redirect=${to.path}`)
        } else if (to.path.startsWith('/front')) {
            // 其他前端路由可以访问
            next()
        } else {
            // 其他路由，重定向到登录页
            next(`/login?redirect=${to.path}`)
        }
    }
})

/**
 * 路由错误处理
 */
router.onError((error) => {
    console.error('路由错误:', error)
    ElMessage.error('页面加载失败，请刷新重试')
})

export default router
