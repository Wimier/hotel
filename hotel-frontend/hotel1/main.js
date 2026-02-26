import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}

// main.js

// 需要登录才能访问的页面白名单（不需要登录的页面）
const whiteList = [
  '/pages/login/login', 
  '/pages/register/register'
]

// 检查登录状态的方法
function hasToken() {
  return !!uni.getStorageSync('token')
}

// 添加页面跳转拦截器
const list = ['navigateTo', 'redirectTo', 'reLaunch', 'switchTab']
list.forEach(item => {
  uni.addInterceptor(item, {
    invoke(e) {
      // 获取目标页面的 url (去掉参数部分)
      const url = e.url.split('?')[0]
      
      // 如果不在白名单且没登录
      if (!whiteList.includes(url) && !hasToken()) {
        uni.reLaunch({
          url: '/pages/login/login'
        })
        return false // 终止跳转
      }
      return true // 继续跳转
    },
    fail(err) { // 失败回调
      console.log(err)
    }
  })
})
// #endif