import {createRouter, createWebHistory} from 'vue-router'
import { ElMessage } from 'element-plus'
import AdminLayout from "@/views/layout/AdminLayout.vue";
import FrontLayout from "@/views/layout/FrontLayout.vue";
import { useUserStore } from '@/stores/user'
import i18n from '@/i18n'

const { t } = i18n.global

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
                    path: 'commentsInfo',
                    name: 'admin-commentsInfo',
                    component: () =>
                        import ('../views/admin/CommentInfoManage.vue')
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
                    path: 'order',
                    name: 'admin-order',
                    component: () =>
                        import ('../views/admin/OrderInfoManage.vue')
                },



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
    '/front/travelNote'
]

/**
 * 需要登录的前端路由
 */
const requireAuthFrontRoutes = [
    '/front/personalCenter'
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
    // 精确匹配公开路由
    if (publicRoutes.includes(path)) {
        return true
    }
    
    // 检查详情页路由（允许未登录访问，支持任意ID格式）
    if (path.match(/^\/front\/scenicDetails\/.+$/) ||
        path.match(/^\/front\/travelDetails\/.+$/)) {
        return true
    }
    
    // 其他前端路由默认允许访问（除了需要认证的）
    if (path.startsWith('/front') && !requiresAuth(path)) {
        return true
    }
    
    return false
}

/**
 * 路由守卫
 */
router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    
    // 根据目标路径判断需要的用户类型
    const targetUserType = to.path.startsWith('/admin') ? 'ADMIN' : 'USER'
    
    // 如果目标路径的用户类型与当前不同，需要切换会话
    if (userStore.getUserType !== targetUserType) {
        await userStore.initUserState(targetUserType)
    }
    
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
                ElMessage.warning(t('router.noPermission'))
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
        next()
        return
    }
    
    // 需要登录的路由 - 只弹窗提示，不跳转
    if (requiresAuth(to.path)) {
        ElMessage.warning(t('router.pleaseLogin'))
        next(false) // 阻止导航，停留在当前页面
        return
    }
    
    // 其他路由：404或未匹配的路由
    if (to.path === '/404' || to.matched.length === 0) {
        next()
    } else {
        next()
    }
})

/**
 * 路由错误处理
 */
router.onError((error) => {
    ElMessage.error(t('router.pageLoadError'))
})

export default router
