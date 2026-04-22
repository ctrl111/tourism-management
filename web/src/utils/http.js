import axios from "axios";
import router from "../router";
import {ElMessage} from "element-plus";
import i18n from '@/i18n'

const { t } = i18n.global

// 设置 Axios 的默认基础 URL
axios.defaults.baseURL = import.meta.env.VITE_APP_API_URL;

// 创建 Axios 实例
const http = axios.create({
    timeout: 5000, headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
http.interceptors.request.use(config => {
    if (localStorage.getItem("token")) {
        config.headers["token"] = localStorage.getItem("token");
    }
    return config;
})
;

// 响应拦截器
http.interceptors.response.use(response => {
    return response.data;
}, error => {
    switch (error.response.status) {
        case 401:
            localStorage.removeItem("token");
            ElMessage({message: t('message.pleaseLogin'), type: "error"});
            // router.push("/login");
            break;
        case 409:
            ElMessage({message: error.response.data.data, type: "error"});
            break;
        case 404:
            ElMessage({message: t('http.apiNotFound'), type: "error"});
            break;
        case 500:
            ElMessage({message: t('http.serverError'), type: "error"});
            break;
        default:
            return Promise.reject(error);
    }
});

export default http;

