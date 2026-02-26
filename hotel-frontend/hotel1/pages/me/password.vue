<template>
	<view class="password-container">
		<view class="input-group">
			<view class="input-item">
				<text class="label">原密码</text>
				<input type="password" v-model="form.oldPassword" placeholder="请输入原密码" class="pwd-input" />
			</view>
			<view class="input-item">
				<text class="label">新密码</text>
				<input type="password" v-model="form.newPassword" placeholder="请输入新密码（不少于6位）" class="pwd-input" />
			</view>
			<view class="input-item">
				<text class="label">确认新密码</text>
				<input type="password" v-model="confirmPassword" placeholder="请再次输入新密码" class="pwd-input" />
			</view>
		</view>
		
		<button class="save-btn" @click="handleSave">确认修改并重新登录</button>
		<view class="tip">修改成功后，系统将自动退出并请您重新登录。</view>
	</view>
</template>

<script>
	import { updateUserPwd } from '@/api/user.js'

	export default {
		data() {
			return {
				form: {
					oldPassword: '',
					newPassword: ''
				},
				confirmPassword: ''
			};
		},
		methods: {
			async handleSave() {
				// 1. 前端基础校验
				if (!this.form.oldPassword) {
					return uni.showToast({ title: '请输入原密码', icon: 'none' });
				}
				if (this.form.newPassword.length < 6) {
					return uni.showToast({ title: '新密码不能少于6位', icon: 'none' });
				}
				if (this.form.newPassword !== this.confirmPassword) {
					return uni.showToast({ title: '两次输入的密码不一致', icon: 'none' });
				}
				if (this.form.oldPassword === this.form.newPassword) {
					return uni.showToast({ title: '新密码不能与原密码相同', icon: 'none' });
				}

				uni.showLoading({ title: '正在提交...', mask: true });
				
				try {
					// 发送 JSON Body 请求
					const res = await updateUserPwd(this.form.oldPassword, this.form.newPassword);
					
					// 2. 根据后端返回状态处理逻辑
					if (res.code === 200) {
						uni.showToast({
							title: '修改成功',
							icon: 'success',
							duration: 2000
						});
						
						// ✨ 3. 自动退出登录逻辑
						setTimeout(() => {
							// 清除本地存储的所有数据（Token、用户信息等）
							uni.clearStorageSync();
							
							// 强制跳转到登录页，并关闭所有其他页面，防止点击返回回到个人中心
							uni.reLaunch({
								url: '/pages/login/login'
							});
						}, 2000);
						
					} else {
						// 捕捉并显示后端返回的错误（例如：旧密码错误）
						uni.showToast({
							title: res.msg || '修改失败',
							icon: 'none',
							duration: 2000
						});
					}
				} catch (e) {
					uni.showToast({ title: '请求失败，请稍后再试', icon: 'none' });
				} finally {
					uni.hideLoading();
				}
			}
		}
	}
</script>

<style scoped>
	.password-container { padding: 40rpx; background: #f8f9fb; min-height: 100vh; }
	.input-group { background: #fff; border-radius: 20rpx; padding: 20rpx 30rpx; }
	.input-item { border-bottom: 1rpx solid #f5f5f5; padding: 20rpx 0; }
	.input-item:last-child { border-bottom: none; }
	.label { font-size: 26rpx; color: #999; margin-bottom: 10rpx; display: block; }
	.pwd-input { height: 80rpx; font-size: 30rpx; }

	.save-btn { background: #0086F6; color: #fff; border-radius: 50rpx; margin-top: 60rpx; font-size: 32rpx; height: 90rpx; line-height: 90rpx; }
	.tip { font-size: 24rpx; color: #bbb; text-align: center; margin-top: 30rpx; }
</style>