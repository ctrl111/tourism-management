import {createRouter, createWebHistory} from 'vue-router'
import AdminLayout from "@/views/layout/AdminLayout.vue";
import FrontLayout from "@/views/layout/FrontLayout.vue";

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
                        import ('../views/front/personalCenter/Favorite.vue')
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

            ]
        },
        {
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

router.beforeEach((to, from, next) => {
    next();
});
export default router
