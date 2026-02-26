<template>
  <view class="login-container">
    <view class="header">
      <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
      <text class="welcome-text">欢迎来到酒店预订</text>
      <text class="sub-text">请输入您的账号密码进行登录</text>
    </view>

    <view class="form-group">
      <view class="input-item">
        <text class="icon-text">👤</text>
        <input 
          class="input" 
          type="text" 
          v-model="loginForm.username" 
          placeholder="请输入用户名" 
          placeholder-style="color: #c0c4cc;"
        />
      </view>

      <view class="input-item">
        <text class="icon-text">🔒</text>
        <input 
          class="input" 
          :password="!showPassword" 
          v-model="loginForm.password" 
          placeholder="请输入密码" 
          placeholder-style="color: #c0c4cc;"
        />
        <view class="eye-btn" @click="togglePassword">
          <text v-if="showPassword">👁️</text>
          <text v-else>🙈</text>
        </view>
      </view>

      <view class="input-item">
        <text class="icon-text">🛡️</text>
        <input 
          class="input" 
          type="text" 
          v-model="loginForm.code" 
          placeholder="请输入验证码" 
          placeholder-style="color: #c0c4cc;"
          maxlength="4"
        />
        <view class="captcha-box" @click="getCodeImage">
          <image v-if="captchaImg" :src="captchaImg" class="captcha-img" mode="aspectFit"></image>
          <text v-else style="font-size: 24rpx; color: #999;">加载中...</text>
        </view>
      </view>
    </view>

    <view class="agreement-box" @click="toggleAgree">
      <view class="checkbox" :class="{ 'checked': isAgree }">
        <text v-if="isAgree" class="check-icon">✓</text>
      </view>
      <text class="agree-text">我已阅读并同意</text>
      <text class="link" @click.stop="goProtocol">《用户服务协议》</text>
    </view>

    <button 
      class="login-btn" 
      :class="{ 'btn-active': isReadyToLogin }" 
      @click="handleLogin"
    >
      立即登录
    </button>

    <view class="bottom-links">
      <text class="link-text" @click="goRegister">没有账号？点击注册</text>
    </view>
  </view>
</template>

<script>
import http from '@/utils/request.js'

export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        code: '',     
        uuid: ''      
      },
      captchaImg: '', 
      isAgree: false, 
      showPassword: false // 新增：控制密码是否显示的开关
    };
  },
  computed: {
    // 账号、密码、验证码填了，且勾选了协议，按钮才亮起
    isReadyToLogin() {
      return this.loginForm.username.length > 0 && 
             this.loginForm.password.length > 0 && 
             this.loginForm.code.length > 0 && 
             this.isAgree;
    }
  },
  onLoad() {
    // 页面一加载，就去向后端请求验证码图片
    this.getCodeImage();
  },
  methods: {
    // 切换密码显示状态
    togglePassword() {
      this.showPassword = !this.showPassword;
    },

    // 切换协议勾选状态
    toggleAgree() {
      this.isAgree = !this.isAgree;
    },
    
    // 获取图形验证码
    getCodeImage() {
      http.get('/captchaImage').then(res => {
        if (res.code === 200) {
          this.captchaImg = 'data:image/gif;base64,' + res.img;
          this.loginForm.uuid = res.uuid;
        } else {
          uni.showToast({ title: '获取验证码失败', icon: 'none' });
        }
      }).catch(err => {
        console.error('验证码请求失败', err);
      });
    },

    // 提交登录
    handleLogin() {
      if (!this.isAgree) {
        uni.showToast({ title: '请先阅读并勾选底部协议', icon: 'none' });
        return;
      }
      if (!this.loginForm.username || !this.loginForm.password) {
        uni.showToast({ title: '账号和密码不能为空', icon: 'none' });
        return;
      }
      if (!this.loginForm.code) {
        uni.showToast({ title: '请输入验证码', icon: 'none' });
        return;
      }
    
      uni.showLoading({ title: '登录中...' });
      
      // 1. 发起登录请求
      http.post('/login', this.loginForm).then(res => {
        if (res.code === 200 && res.token) {
          // 2. 存储 Token
          uni.setStorageSync('token', res.token);
          
          // ✨ 关键步骤：登录成功后立即请求获取用户信息
          this.getUserDetail(); 
    
        } else {
          uni.hideLoading();
          uni.showToast({ title: res.msg || '登录失败', icon: 'none' });
          this.getCodeImage();
          this.loginForm.code = ''; 
        }
      }).catch(err => {
        uni.hideLoading();
        uni.showToast({ title: err.msg || '服务器异常', icon: 'none' });
        this.getCodeImage(); 
      });
    },
    
    // ✨ 新增方法：获取并存储用户信息
    getUserDetail() {
      http.get('/getInfo').then(res => {
        uni.hideLoading();
        if (res.code === 200) {
          // 1. 存储真实的 userId，供下单页面 create.vue 使用
          uni.setStorageSync('userId', res.user.userId);
          // 2. (可选) 存储整个用户信息或用户名
          uni.setStorageSync('userInfo', res.user);
          
          uni.showToast({ title: '登录成功', icon: 'success' });
          
          // 3. 跳转页面
          setTimeout(() => {
            uni.navigateBack({
              fail: () => { uni.switchTab({ url: '/pages/index/index' }); }
            });
          }, 1000);
        } else {
          uni.showToast({ title: '获取用户信息失败', icon: 'none' });
        }
      }).catch(err => {
        uni.hideLoading();
        console.error('获取用户信息异常', err);
      });
    },

    goProtocol() {
      uni.showToast({ title: '协议加载中...', icon: 'none' });
    },
    
    // 跳转注册页
    goRegister() {
      uni.navigateTo({
        url: '/pages/register/register'
      });
    }
  }
};
</script>

<style scoped>
.login-container { min-height: 100vh; background-color: #ffffff; padding: 60rpx 50rpx; }

.header { display: flex; flex-direction: column; margin-bottom: 80rpx; margin-top: 40rpx; }
.logo { width: 120rpx; height: 120rpx; border-radius: 20rpx; margin-bottom: 30rpx; background-color: #f5f5f5; }
.welcome-text { font-size: 48rpx; font-weight: bold; color: #333333; margin-bottom: 16rpx; }
.sub-text { font-size: 26rpx; color: #999999; }

.form-group { margin-bottom: 40rpx; }
.input-item { display: flex; align-items: center; height: 100rpx; border-bottom: 1rpx solid #eeeeee; margin-bottom: 20rpx; position: relative; }
.icon-text { font-size: 36rpx; margin-right: 20rpx; color: #666; }
.input { flex: 1; font-size: 32rpx; color: #333; }

/* 密码小眼睛按钮样式 */
.eye-btn { padding: 10rpx 20rpx; font-size: 32rpx; color: #999; display: flex; align-items: center; justify-content: center; }

/* 验证码图片样式 */
.captcha-box { width: 180rpx; height: 70rpx; background-color: #f8f8f8; display: flex; justify-content: center; align-items: center; border-radius: 8rpx; margin-left: 20rpx; }
.captcha-img { width: 100%; height: 100%; }

.agreement-box { display: flex; align-items: center; margin-bottom: 60rpx; padding-top: 20rpx; }
.checkbox { width: 32rpx; height: 32rpx; border: 2rpx solid #cccccc; border-radius: 50%; margin-right: 16rpx; display: flex; align-items: center; justify-content: center; transition: all 0.2s; }
.checkbox.checked { background-color: #0086F6; border-color: #0086F6; }
.check-icon { color: #ffffff; font-size: 24rpx; font-weight: bold; }
.agree-text { font-size: 24rpx; color: #999999; }
.link { font-size: 24rpx; color: #0086F6; }

.login-btn { width: 100%; height: 90rpx; line-height: 90rpx; background-color: #a0cfff; color: #ffffff; font-size: 32rpx; font-weight: bold; border-radius: 45rpx; text-align: center; transition: all 0.3s; }
.login-btn::after { border: none; }
.login-btn.btn-active { background-color: #0086F6; box-shadow: 0 8rpx 20rpx rgba(0, 134, 246, 0.3); }

.bottom-links { margin-top: 40rpx; text-align: center; }
.link-text { font-size: 26rpx; color: #0086F6; }
</style>