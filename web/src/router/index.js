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
                },
                {
                    path: 'routePlan',
                    name: 'admin-routePlan',
                    component: () =>
                        import ('../views/admin/RoutePlanManage.vue')
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
                }

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
 * 完全公开的路由（无需登录）
 */
const publicRoutes = [
    '/login',
    '/register', 
    '/retrievePassword',
    '/',
    '/front/index',
    '/front/scenic',
    '/front/travelNote',
    '/front/route'
]

/**
 * 需要登录的前端路由
 */
const requireAuthFrontRoutes = [
    '/front/personalCenter',
    '/front/notice'
]

/**
 * 检查路径是否需要认证
 */
function requiresAuth(path) {
    // 管理员路由都需要登录
    if (path.startsWith('/admin')) {
        return true
    }
    
    // 检查是否在需要登录的前端路由列表中
    return requireAuthFrontRoutes.some(route => path.startsWith(route))
}

/**
 * 检查路径是否公开访问
 */
function isPublicRoute(path) {
    console.log('📋 检查公开路由:', path)
    
    // 精确匹配公开路由
    if (publicRoutes.includes(path)) {
        console.log('  ✓ 在公开路由列表中')
        return true
    }
    
    // 检查详情页路由（允许未登录访问，支持任意ID格式）
    if (path.match(/^\/front\/scenicDetails\/.+$/) ||
        path.match(/^\/front\/travelDetails\/.+$/) ||
        path.match(/^\/front\/routeDetails\/.+$/)) {
        console.log('  ✓ 是详情页路由')
        return true
    }
    
    // 其他前端路由默认允许访问（除了需要认证的）
    if (path.startsWith('/front') && !requiresAuth(path)) {
        console.log('  ✓ 前端路由且不需要认证')
        return true
    }
    
    console.log('  ✗ 不是公开路由')
    return false
}

/**
 * 路由守卫
 */
router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    
    // 调试信息
    console.log('🔍 路由守卫:', {
        path: to.path,
        isLoggedIn: userStore.getIsLoggedIn,
        userType: userStore.getUserType,
        isPublic: isPublicRoute(to.path),
        requiresAuth: requiresAuth(to.path)
    })
    
    // 如果访问登录页
    if (to.path === '/login') {
        // 已登录用户访问登录页，根据用户类型重定向
        if (userStore.getIsLoggedIn) {
            const userType = userStore.getUserType
            if (userType === 'ADMIN') {
                next('/admin')
            } else if (userType === 'USER') {
                next('/front/index')
            } else {
                // 用户类型未知，允许访问登录页
                next()
            }
        } else {
            // 未登录，允许访问
            next()
        }
        return
    }
    
    // 如果已登录
    if (userStore.getIsLoggedIn) {
        // 检查权限：管理员路由只能管理员访问
        if (to.path.startsWith('/admin')) {
            if (userStore.getUserType === 'ADMIN') {
                next()
            } else {
                ElMessage.warning('您没有权限访问该页面')
                next('/front/index')
            }
            return
        }
        
        // 其他路由正常访问
        next()
        return
    }
    
    // 未登录用户
    // 检查是否为公开路由
    if (isPublicRoute(to.path)) {
        console.log('✅ 公开路由，允许访问')
        next()
        return
    }
    
    // 需要登录的路由 - 只弹窗提示，不跳转
    if (requiresAuth(to.path)) {
        console.log('⚠️ 需要登录，阻止访问')
        ElMessage.warning('请先登录')
        next(false) // 阻止导航，停留在当前页面
        return
    }
    
    // 其他路由：404或未匹配的路由
    console.log('❓ 未知路由，路径:', to.path)
    if (to.path === '/404' || to.matched.length === 0) {
        next()
    } else {
        // 理论上不应该到这里，因为前端路由都应该被 isPublicRoute 捕获
        console.warn('⚠️ 未识别的路由，允许访问:', to.path)
        next()
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
