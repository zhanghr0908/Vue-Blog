import axios from 'axios'
import Element from "element-ui";
import store from "./store";
import router from "./router";

// 全局axios拦截器

// axios.defaults.baseURL = "http://172.20.138.224:8083"
axios.defaults.baseURL = "http://localhost:8083"

//请求拦截（前置拦截）
axios.interceptors.request.use(config => {
    if (!config.url.includes("usuuu.com")) {
        //如果有token，统一带上
        const token = window.localStorage.getItem('token')
        if (token) { // 判断是否存在token，如果存在的话，则每个http header都加上token
            config.headers.Authorization = `${token}`;
        }
        // identification存在，可以统一设置请求头
        const identification = window.localStorage.getItem('identification')

        if (identification && !(config.url.startsWith('http://') || config.url.startsWith('https://'))) {
            config.headers.identification = identification
        }

    }

    return config
})

//响应拦截（后置拦截）
axios.interceptors.response.use(response => {
        const res = response.data;
        const identification = response.headers.identification
        if (identification) {
            //保存身份标识到localStorage
            window.localStorage.setItem('identification', identification)
        }
        // 当结果的code是否为200的情况
        if (res.code === 200) {
            return response
        } else {
            // 弹窗异常信息
            Element.Message({
                message: response.data.msg,
                type: 'error',
                duration: 2 * 1000
            })
            // 直接拒绝往下面走，并返回结果错误信息
            return Promise.reject(response.data.msg)
        }
    },
    error => {
        // console.log('err' + error)
        if (error.response.data) {
            error.message = error.response.data.msg
        }
        // 根据请求状态觉得是否登录或者提示其他
        if (error.response.status === 401||error.response.status === 500) {
            store.commit('REMOVE_INFO');
            router.push({
                path: '/login'
            });
            error.message = '请重新登录';
        }
        if (error.response.status === 403) {
            error.message = '权限不足，无法访问';
        }
        Element.Message({
            message: error.message,
            type: 'error',
            duration: 2 * 1000
        })
        return Promise.reject(error)
    })
