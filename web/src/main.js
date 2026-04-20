import {createApp} from 'vue'
import {createPinia} from 'pinia'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import ruLocale from 'element-plus/dist/locale/ru.mjs'
// element-plus图标
import * as ElementPlusIconsVue from "@element-plus/icons-vue"
import TuiPlus from "@wocwin/t-ui-plus"
import "@wocwin/t-ui-plus/lib/style.css"

import App from './App.vue'
import router from './router'
import i18n from './i18n'
import { useUserStore } from './stores/user'

// 俄语UI优化样式 - 精简版
import './styles/ru-ui.css'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// import "./styles/common.css";

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(i18n)
app.use(ElementPlus, {
    locale: ruLocale
})
app.use(TuiPlus)

// 在应用挂载前初始化用户状态
const userStore = useUserStore()
userStore.initUserState().then(() => {
    app.mount('#app')
})

