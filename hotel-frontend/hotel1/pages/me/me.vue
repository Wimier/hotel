<template>
	<view class="my-container">
		<view class="user-header">
			<view class="avatar-box" @click="handleUpdateAvatar">
				<image :src="getImageUrl(userInfo.avatar)" class="avatar" mode="aspectFill"></image>
				<view class="edit-tag">更换</view>
			</view>
			<view class="user-info" @click="openNicknameDialog">
				<text class="nickname">{{ userInfo.nickName || '点击设置昵称' }}</text>
				<text class="username">账号：{{ userInfo.userName }}</text>
			</view>
		</view>

		<view class="stat-card">
			<view class="stat-item" @click="navTo('/pages/order/order')">
				<text class="iconfont icon-order"></text>
				<text class="label">我的订单</text>
			</view>
			<view class="stat-item" @click="navTo('/pages/comment/comment')">
				<text class="iconfont icon-comment"></text>
				<text class="label">我的评价</text>
			</view>
		</view>

		<view class="menu-list">
			<view class="menu-item" @click="openNicknameDialog">
				<view class="left">
					<text class="iconfont icon-user"></text>
					<text>修改昵称</text>
				</view>
				<text class="iconfont icon-arrow-right"></text>
			</view>

			<view class="menu-item" @click="navTo('/pages/me/password')">
				<view class="left">
					<text class="iconfont icon-lock"></text>
					<text>修改密码</text>
				</view>
				<text class="iconfont icon-arrow-right"></text>
			</view>

			<view class="menu-item" @click="showAgreement">
				<view class="left">
					<text class="iconfont icon-file"></text>
					<text>用户协议</text>
				</view>
				<text class="iconfont icon-arrow-right"></text>
			</view>
		</view>

		<view class="logout-box">
			<button class="logout-btn" @click="handleLogout">退出登录</button>
		</view>

		<uni-popup ref="nicknamePopup" type="dialog">
			<uni-popup-dialog mode="input" title="修改昵称" :value="userInfo.nickName" placeholder="请输入新昵称"
				@confirm="handleUpdateNickname"></uni-popup-dialog>
		</uni-popup>
	</view>
</template>

<script>
	import {
		getUserProfile,
		updateUserProfile,
		uploadAvatar
	} from '@/api/user.js'

	export default {
		data() {
			return {
				userInfo: {},
				baseUrl: 'http://localhost:8080'
			};
		},
		onShow() {
			this.getUserInfo();
		},
		methods: {
			// 获取用户信息
			async getUserInfo() {
				const res = await getUserProfile();
				if (res.code === 200) {
					this.userInfo = res.data;
				}
			},

			// 修改头像 logic
			handleUpdateAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					success: (res) => {
						const tempFile = res.tempFilePaths[0];
						uni.showLoading({
							title: '上传中...'
						});

						uni.uploadFile({
							url: this.baseUrl + '/system/user/profile/avatar',
							filePath: tempFile,
							name: 'avatarfile',
							header: {
								'Authorization': 'Bearer ' + uni.getStorageSync('token')
							},
							success: (uploadRes) => {
								const data = JSON.parse(uploadRes.data);
								if (data.code === 200) {
									uni.showToast({
										title: '头像更新成功'
									});
									this.getUserInfo();
								}
							}
						});
						uni.hideLoading();
					}
				});
			},

			// 修改昵称 logic
			openNicknameDialog() {
				this.$refs.nicknamePopup.open();
			},
			async handleUpdateNickname(val) {
				if (!val) return;
				const res = await updateUserProfile({
					nickName: val
				});
				if (res.code === 200) {
					uni.showToast({
						title: '昵称更新成功'
					});
					this.getUserInfo();
				}
			},

			// 导航跳转
			navTo(url) {
				// 定义你 pages.json 中所有的 TabBar 页面路径
				const tabBarPages = [
					'/pages/index/index',
					'/pages/order/order',
					'/pages/my/my',
					'/pages/comment/comment' // 如果你把评价列表也设成了底部菜单
				];

				// 判断目标路径是否在 TabBar 列表中
				if (tabBarPages.includes(url)) {
					uni.switchTab({
						url: url
					});
				} else {
					uni.navigateTo({
						url: url
					});
				}
			},

			// 用户协议 (写死展示)
			showAgreement() {
				uni.showModal({
					title: '用户协议',
					content: '本酒店预订系统致力于为您提供优质服务。用户需遵守相关法律法规，保护个人信息安全...',
					showCancel: false,
					confirmText: '我知道了'
				});
			},

			// 退出登录
			handleLogout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							uni.clearStorageSync();
							uni.reLaunch({
								url: '/pages/login/login'
							});
						}
					}
				});
			},

			getImageUrl(url) {
				if (!url) return '/static/default-avatar.png';
				return url.startsWith('http') ? url : this.baseUrl + url;
			}
		}
	}
</script>

<style scoped>
	.my-container {
		min-height: 100vh;
		background: #f8f9fb;
	}

	/* 顶部背景与信息 */
	.user-header {
		background: linear-gradient(180deg, #0086F6 0%, #00B4F6 100%);
		padding: 100rpx 40rpx 60rpx;
		display: flex;
		align-items: center;
		color: #fff;
	}

	.avatar-box {
		position: relative;
		width: 140rpx;
		height: 140rpx;
	}

	.avatar {
		width: 100%;
		height: 100%;
		border-radius: 50%;
		border: 4rpx solid rgba(255, 255, 255, 0.3);
	}

	.edit-tag {
		position: absolute;
		bottom: 0;
		right: 0;
		background: rgba(0, 0, 0, 0.5);
		font-size: 20rpx;
		padding: 4rpx 10rpx;
		border-radius: 10rpx;
	}

	.user-info {
		margin-left: 30rpx;
	}

	.nickname {
		font-size: 36rpx;
		font-weight: bold;
		display: block;
	}

	.username {
		font-size: 24rpx;
		opacity: 0.8;
		margin-top: 10rpx;
	}

	/* 快捷卡片 */
	.stat-card {
		display: flex;
		background: #fff;
		margin: -30rpx 30rpx 30rpx;
		border-radius: 20rpx;
		padding: 40rpx 0;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
	}

	.stat-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		border-right: 1rpx solid #f0f0f0;
	}

	.stat-item:last-child {
		border-right: none;
	}

	.stat-item .iconfont {
		font-size: 44rpx;
		color: #0086F6;
		margin-bottom: 10rpx;
	}

	.label {
		font-size: 26rpx;
		color: #666;
	}

	/* 列表菜单 */
	.menu-list {
		background: #fff;
		margin: 0 30rpx;
		border-radius: 20rpx;
		overflow: hidden;
	}

	.menu-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f9f9f9;
	}

	.menu-item:last-child {
		border-bottom: none;
	}

	.left {
		display: flex;
		align-items: center;
		font-size: 28rpx;
		color: #333;
	}

	.left .iconfont {
		margin-right: 20rpx;
		font-size: 32rpx;
		color: #999;
	}

	.icon-arrow-right {
		font-size: 24rpx;
		color: #ccc;
	}

	.logout-box {
		padding: 60rpx 40rpx;
	}

	.logout-btn {
		background: #fff;
		color: #ff4d4f;
		height: 88rpx;
		line-height: 88rpx;
		border-radius: 44rpx;
		font-size: 30rpx;
		border: 1rpx solid #eee;
	}
</style>