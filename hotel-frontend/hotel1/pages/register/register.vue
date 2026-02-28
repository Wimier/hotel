<template>
	<view class="register-container">
		<view class="header">
			<text class="welcome-text">注册新账号</text>
			<text class="sub-text">欢迎加入酒店预订平台</text>
		</view>

		<view class="form-group">
			<view class="input-item">
				<text class="icon-text">👤</text>
				<input class="input" type="text" v-model="registerForm.username" placeholder="请输入用户名 (建议字母+数字)"
					placeholder-style="color: #c0c4cc;" />
			</view>

			<view class="input-item">
				<text class="icon-text">🔒</text>
				<input class="input" type="password" v-model="registerForm.password" placeholder="请输入密码 (至少6位)"
					placeholder-style="color: #c0c4cc;" />
			</view>

			<view class="input-item">
				<text class="icon-text">🔐</text>
				<input class="input" type="password" v-model="confirmPassword" placeholder="请再次输入密码"
					placeholder-style="color: #c0c4cc;" />
			</view>

			<view class="input-item">
				<text class="icon-text">🛡️</text>
				<input class="input" type="text" v-model="registerForm.code" placeholder="请输入验证码"
					placeholder-style="color: #c0c4cc;" maxlength="4" />
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

		<button class="login-btn" :class="{ 'btn-active': isReadyToRegister }" @click="handleRegister">
			立即注册
		</button>

		<view class="bottom-links">
			<text class="link-text" @click="goLogin">已有账号？点击去登录</text>
		</view>
	</view>
</template>

<script>
	import http from '@/utils/request.js'

	export default {
		data() {
			return {
				registerForm: {
					username: '',
					password: '',
					code: '',
					uuid: ''
				},
				confirmPassword: '',
				captchaImg: '',
				isAgree: false
			};
		},
		computed: {
			isReadyToRegister() {
				return this.registerForm.username.length > 0 &&
					this.registerForm.password.length > 0 &&
					this.confirmPassword.length > 0 &&
					this.registerForm.code.length > 0 &&
					this.isAgree;
			}
		},
		onLoad() {
			this.getCodeImage();
		},
		methods: {
			toggleAgree() {
				this.isAgree = !this.isAgree;
			},

			getCodeImage() {
				http.get('/captchaImage').then(res => {
					if (res.code === 200) {
						this.captchaImg = 'data:image/gif;base64,' + res.img;
						this.registerForm.uuid = res.uuid;
					} else {
						uni.showToast({
							title: '获取验证码失败',
							icon: 'none'
						});
					}
				});
			},

			handleRegister() {
				if (!this.isAgree) {
					return uni.showToast({
						title: '请先阅读并勾选底部协议',
						icon: 'none'
					});
				}
				if (this.registerForm.username.length < 2 || this.registerForm.username.length > 20) {
					return uni.showToast({
						title: '用户名长度需在2-20之间',
						icon: 'none'
					});
				}
				if (this.registerForm.password.length < 6 || this.registerForm.password.length > 20) {
					return uni.showToast({
						title: '密码长度需在6-20之间',
						icon: 'none'
					});
				}
				if (this.registerForm.password !== this.confirmPassword) {
					return uni.showToast({
						title: '两次输入的密码不一致',
						icon: 'none'
					});
				}

				uni.showLoading({
					title: '注册中...'
				});

				http.post('/register', this.registerForm).then(res => {
					uni.hideLoading();

					if (res.code === 200) {
						uni.showModal({
							title: '注册成功',
							content: '您的账号已注册成功，请前往登录。',
							showCancel: false,
							success: () => {
								// 返回登录页
								uni.navigateBack();
							}
						});
					} else {
						uni.showToast({
							title: res.msg || '注册失败',
							icon: 'none'
						});
						this.getCodeImage();
						this.registerForm.code = '';
					}
				}).catch(err => {
					uni.hideLoading();
					uni.showToast({
						title: err.msg || '服务器异常',
						icon: 'none'
					});
					this.getCodeImage();
				});
			},

			goProtocol() {
				uni.showToast({
					title: '协议加载中...',
					icon: 'none'
				});
			},
			goLogin() {
				// 返回上一页(登录页)
				uni.navigateBack();
			}
		}
	};
</script>

<style scoped>
	/* 复用登录页的样式 */
	.register-container {
		min-height: 100vh;
		background-color: #ffffff;
		padding: 60rpx 50rpx;
	}

	.header {
		display: flex;
		flex-direction: column;
		margin-bottom: 80rpx;
		margin-top: 40rpx;
	}

	.welcome-text {
		font-size: 48rpx;
		font-weight: bold;
		color: #333333;
		margin-bottom: 16rpx;
	}

	.sub-text {
		font-size: 26rpx;
		color: #999999;
	}

	.form-group {
		margin-bottom: 40rpx;
	}

	.input-item {
		display: flex;
		align-items: center;
		height: 100rpx;
		border-bottom: 1rpx solid #eeeeee;
		margin-bottom: 20rpx;
		position: relative;
	}

	.icon-text {
		font-size: 36rpx;
		margin-right: 20rpx;
		color: #666;
	}

	.input {
		flex: 1;
		font-size: 32rpx;
		color: #333;
	}

	.captcha-box {
		width: 180rpx;
		height: 70rpx;
		background-color: #f8f8f8;
		display: flex;
		justify-content: center;
		align-items: center;
		border-radius: 8rpx;
		margin-left: 20rpx;
	}

	.captcha-img {
		width: 100%;
		height: 100%;
	}

	.agreement-box {
		display: flex;
		align-items: center;
		margin-bottom: 60rpx;
		padding-top: 20rpx;
	}

	.checkbox {
		width: 32rpx;
		height: 32rpx;
		border: 2rpx solid #cccccc;
		border-radius: 50%;
		margin-right: 16rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		transition: all 0.2s;
	}

	.checkbox.checked {
		background-color: #0086F6;
		border-color: #0086F6;
	}

	.check-icon {
		color: #ffffff;
		font-size: 24rpx;
		font-weight: bold;
	}

	.agree-text {
		font-size: 24rpx;
		color: #999999;
	}

	.link {
		font-size: 24rpx;
		color: #0086F6;
	}

	.login-btn {
		width: 100%;
		height: 90rpx;
		line-height: 90rpx;
		background-color: #a0cfff;
		color: #ffffff;
		font-size: 32rpx;
		font-weight: bold;
		border-radius: 45rpx;
		text-align: center;
		transition: all 0.3s;
	}

	.login-btn::after {
		border: none;
	}

	.login-btn.btn-active {
		background-color: #0086F6;
		box-shadow: 0 8rpx 20rpx rgba(0, 134, 246, 0.3);
	}

	.bottom-links {
		margin-top: 40rpx;
		text-align: center;
	}

	.link-text {
		font-size: 26rpx;
		color: #0086F6;
	}
</style>