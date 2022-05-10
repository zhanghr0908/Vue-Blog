import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        token: localStorage.getItem("token"),
        userInfo: JSON.parse(sessionStorage.getItem("userInfo")),
        focusMode: false,
    },
    mutations: {
        // 通过set去设置
        SET_TOKEN: (state, token) => {
            state.token = token
            localStorage.setItem("token", token)
        },
        SET_USERINFO: (state, userInfo) => {
            state.userInfo = userInfo
            sessionStorage.setItem("userInfo", JSON.stringify(userInfo))
        },
        // 通过remove去删除
        REMOVE_INFO: (state) => {
            state.token = ""
            state.userInfo = {}
            localStorage.setItem("token", "")
            sessionStorage.setItem("userInfo", JSON.stringify(''))
        }
    },
    getters: {
        // 通过get去获取
        getUser: state => {
            return state.userInfo
        }
    },
    actions: {},
    modules: {}
})
