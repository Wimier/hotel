<template>
	<view class="order-detail-container">
		<view class="status-banner" :class="'bg-status-' + orderInfo.status">
			<view class="status-content">
				<text class="status-text">{{ getStatusLabel(orderInfo.status) }}</text>
				<text class="status-desc">{{ getStatusDesc(orderInfo.status) }}</text>
			</view>
			<text class="status-icon">{{ getStatusIcon(orderInfo.status) }}</text>
		</view>

		<view class="info-card hotel-card" @click="goHotelDetail">
			<view class="hotel-header">
				<view class="h-left">
					<text class="hotel-name">{{ orderInfo.hotelName || '酒店预订' }}</text>
					<text class="room-type">{{ orderInfo.roomTypeName }} · {{ orderInfo.roomCount }}间</text>
				</view>
				<text class="arrow">进入酒店 ></text>
			</view>

			<view class="date-row">
				<view class="date-item">
					<text class="d-label">入住</text>
					<text class="d-val">{{ formatDate(orderInfo.checkInDate) }}</text>
				</view>
				<view class="nights-tag">共{{ orderInfo.totalDays }}晚</view>
				<view class="date-item">
					<text class="d-label">离店</text>
					<text class="d-val">{{ formatDate(orderInfo.checkOutDate) }}</text>
				</view>
			</view>
		</view>

		<view class="info-card">
			<view class="card-title">入住人信息</view>
			<view class="detail-item">
				<text class="i-label">入住人</text>
				<text class="i-val">{{ orderInfo.guestName }}</text>
			</view>
			<view class="detail-item">
				<text class="i-label">联系电话</text>
				<text class="i-val">{{ orderInfo.guestPhone }}</text>
			</view>
		</view>

		<view class="info-card">
			<view class="card-title">订单信息</view>
			<view class="detail-item">
				<text class="i-label">订单编号</text>
				<text class="i-val">{{ orderInfo.orderSn }}</text>
			</view>
			<view class="detail-item">
				<text class="i-label">下单时间</text>
				<text class="i-val">{{ orderInfo.createTime }}</text>
			</view>
			<view class="detail-item price-row">
				<text class="i-label">订单总额</text>
				<text class="i-val total-price">¥{{ orderInfo.totalAmount }}</text>
			</view>
		</view>

		<view class="bottom-action">
			<block v-if="orderInfo.status === 0">
				<button class="action-btn cancel-btn" @click="handleCancel">取消订单</button>
				<button class="action-btn pay-btn" @click="handlePay">立即支付</button>
			</block>

			<block v-if="orderInfo.status === 1">
				<button class="action-btn refund-btn" @click="handleRefund">申请退款</button>
			</block>
		</view>
	</view>
</template>

<script>
	import {
		getOrder,
		updateOrder
	} from '@/api/order.js'

	export default {
		data() {
			return {
				orderId: null,
				orderInfo: {}
			};
		},
		onLoad(options) {
			this.orderId = options.id;
			this.fetchDetail();
		},
		methods: {
			async fetchDetail() {
				uni.showLoading({
					title: '加载中...'
				});
				try {
					const res = await getOrder(this.orderId);
					if (res.code === 200) this.orderInfo = res.data;
				} finally {
					uni.hideLoading();
				}
			},

			// ✨ 取消订单逻辑：状态改为 4
			handleCancel() {
				uni.showModal({
					title: '提示',
					content: '确定要取消该订单吗？',
					success: async (sm) => {
						if (sm.confirm) {
							const res = await updateOrder({
								id: this.orderId,
								status: 4
							});
							if (res.code === 200) {
								uni.showToast({
									title: '订单已取消'
								});
								this.fetchDetail();
							}
						}
					}
				});
			},

			// ✨ 申请退款逻辑：状态改为 5，包含日期校验
			async handleRefund() {
				// 1. 获取当前日期 (YYYY-MM-DD)
				const now = new Date();
				const todayStr =
					`${now.getFullYear()}-${(now.getMonth() + 1).toString().padStart(2, '0')}-${now.getDate().toString().padStart(2, '0')}`;

				// 2. 比较日期：如果今天 >= 入住日期，则不允许在线退款
				if (todayStr >= this.orderInfo.checkInDate) {
					uni.showModal({
						title: '无法在线退款',
						content: '当前已超过或处于入住日期，请直接联系酒店处理退款事宜。',
						showCancel: false,
						confirmText: '我知道了'
					});
					return;
				}

				// 3. 执行退款申请
				uni.showModal({
					title: '退款确认',
					content: '申请退款后，房源将重新释放，确定继续吗？',
					success: async (sm) => {
						if (sm.confirm) {
							const res = await updateOrder({
								id: this.orderId,
								status: 5
							});
							if (res.code === 200) {
								uni.showToast({
									title: '退款成功'
								});
								this.fetchDetail();
							}
						}
					}
				});
			},

			async handlePay() {
				const res = await updateOrder({
					id: this.orderId,
					status: 1
				});
				if (res.code === 200) {
					uni.showToast({
						title: '支付成功'
					});
					this.fetchDetail();
				}
			},
			goHotelDetail() {
				uni.navigateTo({
					url: `/pages/detail/detail?id=${this.orderInfo.hotelId}&checkInDate=${this.orderInfo.checkInDate}&checkOutDate=${this.orderInfo.checkOutDate}&days=${this.orderInfo.totalDays}`
				});
			},
			formatDate(date) {
				return date ? date.substring(5).replace(/-/g, '.') : '';
			},
			getStatusLabel(status) {
				const labels = {
					0: '待支付',
					1: '待入住',
					2: '入住中',
					3: '已完成',
					4: '已取消',
					5: '已退款'
				};
				return labels[status] || '加载中';
			},
			getStatusDesc(status) {
				const descs = {
					0: '请在15分钟内完成支付',
					1: '预订成功，期待您的光临',
					4: '订单已关闭',
					5: '退款已原路返回'
				};
				return descs[status] || '';
			},
			getStatusIcon(status) {
				return status === 4 ? '✖️' : status === 5 ? '💰' : '🏨';
			}
		}
	}
</script>

<style scoped>
	.order-detail-container {
		min-height: 100vh;
		background-color: #f8f9fb;
		padding-bottom: 140rpx;
	}

	.status-banner {
		height: 260rpx;
		padding: 0 40rpx;
		display: flex;
		align-items: center;
		justify-content: space-between;
		color: #fff;
	}

	.bg-status-0 {
		background: linear-gradient(135deg, #ff8e3c, #ff4d4f);
	}

	.bg-status-1 {
		background: linear-gradient(135deg, #00b4f6, #0086f6);
	}
	.bg-status-3 {
		background: linear-gradient(135deg, #6aaa17, #1cab5f);
	}

	.bg-status-4,
	.bg-status-5 {
		background: #999;
	}

	.status-text {
		font-size: 44rpx;
		font-weight: bold;
	}

	.status-desc {
		font-size: 24rpx;
		opacity: 0.9;
		margin-top: 10rpx;
	}

	.status-icon {
		font-size: 80rpx;
		opacity: 0.3;
	}

	.info-card {
		background: #fff;
		margin: -40rpx 30rpx 24rpx;
		border-radius: 24rpx;
		padding: 32rpx;
		box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.05);
	}

	.hotel-card {
		margin-top: -60rpx;
		position: relative;
		z-index: 10;
	}

	.hotel-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 30rpx;
	}

	.hotel-name {
		font-size: 34rpx;
		font-weight: bold;
	}

	.room-type {
		font-size: 26rpx;
		color: #666;
		margin-top: 8rpx;
		display: block;
	}

	.arrow {
		font-size: 24rpx;
		color: #0086f6;
	}

	.date-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 24rpx;
		background: #f9fbff;
		border-radius: 16rpx;
	}

	.d-label {
		font-size: 22rpx;
		color: #999;
	}

	.d-val {
		font-size: 32rpx;
		font-weight: bold;
	}

	.nights-tag {
		font-size: 22rpx;
		color: #0086f6;
		border: 1rpx solid #0086f6;
		border-radius: 20rpx;
		padding: 2rpx 16rpx;
	}

	.card-title {
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 24rpx;
	}

	.detail-item {
		display: flex;
		justify-content: space-between;
		margin-bottom: 20rpx;
	}

	.i-label {
		font-size: 26rpx;
		color: #999;
	}

	.i-val {
		font-size: 26rpx;
		color: #333;
	}

	.total-price {
		color: #ff4d4f;
		font-size: 34rpx;
		font-weight: bold;
	}

	.price-row {
		padding-top: 20rpx;
		border-top: 1rpx solid #f2f2f2;
		margin-top: 20rpx;
	}

	.bottom-action {
		position: fixed;
		bottom: 0;
		width: 100%;
		height: 120rpx;
		background: #fff;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		padding: 0 30rpx;
		box-sizing: border-box;
		box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
		gap: 20rpx;
	}

	.action-btn {
		height: 80rpx;
		line-height: 80rpx;
		border-radius: 40rpx;
		font-size: 28rpx;
		font-weight: bold;
		margin: 0;
		padding: 0 40rpx;
	}

	.pay-btn {
		background: #0086f6;
		color: #fff;
	}

	.cancel-btn,
	.refund-btn {
		background: #f5f5f5;
		color: #666;
	}
</style>