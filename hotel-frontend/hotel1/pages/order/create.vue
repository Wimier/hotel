<template>
	<view class="order-create-container">
		<view class="room-preview-card">
			<image :src="getImageUrl(roomType.imageUrl)" mode="aspectFill" class="room-img"></image>
			<view class="room-detail">
				<text class="hotel-name">{{ hotelInfo.nameCn || '加载中...' }}</text>
				<text class="room-type-name">{{ roomType.typeName || '房型加载中...' }}</text>
				<view class="date-info">
					<text>{{ inDateText }} 入住 - {{ outDateText }} 离店</text>
					<text class="d-count">共{{ orderForm.totalDays }}晚</text>
				</view>
			</view>
		</view>

		<view class="order-form">
			<view class="form-row">
				<text class="label">房间数量</text>
				<view class="stepper">
					<view class="step-btn" :class="{disabled: orderForm.roomCount <= 1}" @click="changeCount(-1)">-
					</view>
					<text class="count-num">{{ orderForm.roomCount }}</text>
					<view class="step-btn" @click="changeCount(1)">+</view>
				</view>
			</view>

			<view class="form-row">
				<text class="label">入住人姓名</text>
				<input class="input-field" v-model="orderForm.guestName" placeholder="姓名，每间房需填写1人"
					placeholder-style="color:#ccc;" />
			</view>

			<view class="form-row">
				<text class="label">联系手机</text>
				<input class="input-field" type="number" v-model="orderForm.guestPhone" maxlength="11"
					placeholder="用于接收确认短信" placeholder-style="color:#ccc;" />
			</view>
		</view>

		<view class="pay-method-section">
			<view class="section-title">支付方式</view>
			<radio-group @change="onPayChange">
				<label class="pay-option">
					<view class="pay-left"><text class="icon">🟢</text>微信支付</view>
					<radio value="wechat" :checked="payWay === 'wechat'" color="#0086F6" />
				</label>
				<label class="pay-option">
					<view class="pay-left"><text class="icon">🔵</text>支付宝</view>
					<radio value="alipay" :checked="payWay === 'alipay'" color="#0086F6" />
				</label>
			</radio-group>
		</view>

		<view class="submit-bar">
			<view class="price-summary">
				<text class="total-label">总计</text>
				<text class="currency">¥</text>
				<text class="total-price">{{ referenceTotal }}</text>
			</view>
			<button class="submit-btn" @click="submitOrder">提交订单</button>
		</view>
	</view>
</template>

<script>
	import {
		getHotel
	} from '@/api/info.js'
	import {
		getType
	} from '@/api/type.js'
	import {
		addOrder
	} from '@/api/order.js'

	export default {
		data() {
			return {
				hotelId: null,
				roomTypeId: null,
				hotelInfo: {},
				roomType: {},
				payWay: 'wechat',
				orderForm: {
					userId: null, // 实际开发应从系统获取当前登录用户ID
					hotelId: null,
					roomTypeId: null,
					roomCount: 1,
					checkInDate: '', // 存储格式 YYYY-MM-DD
					checkOutDate: '', // 存储格式 YYYY-MM-DD
					guestName: '',
					guestPhone: '',
					totalDays: 1
				}
			};
		},
		computed: {
			// 前端预览总价
			referenceTotal() {
				const price = parseFloat(this.roomType.price || 0);
				return (price * this.orderForm.roomCount * this.orderForm.totalDays).toFixed(2);
			},
			// ✨ 新增：界面显示的入住日期 (MM-DD)
			inDateText() {
				return this.orderForm.checkInDate ? this.orderForm.checkInDate.substring(5) : '';
			},
			// ✨ 新增：界面显示的离店日期 (MM-DD)
			outDateText() {
				return this.orderForm.checkOutDate ? this.orderForm.checkOutDate.substring(5) : '';
			}
		},
		onLoad(options) {
			const realUserId = uni.getStorageSync('userId');

			if (!realUserId) {
				// 如果没登录，引导去登录页或报错
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				// setTimeout(() => { uni.navigateTo({ url: '/pages/login/login' }) }, 1500);
			} else {
				this.orderForm.userId = realUserId; // ✨ 赋值真实 ID
				console.log(this.orderForm.userId)
			}
			// 1. 接收从详情页 (detail.vue) 传来的标准参数
			if (options.hotelId && options.roomTypeId) {
				this.hotelId = options.hotelId;
				this.roomTypeId = options.roomTypeId;

				this.orderForm.hotelId = options.hotelId;
				this.orderForm.roomTypeId = options.roomTypeId;
				this.orderForm.checkInDate = options.checkIn; // '2026-03-01'
				this.orderForm.checkOutDate = options.checkOut; // '2026-03-03'
				this.orderForm.totalDays = parseInt(options.days) || 1;
			}

			// 2. ✨ 修正：调用正确的数据加载方法
			this.fetchStaticData();
		},
		methods: {
			async fetchStaticData() {
				uni.showLoading({
					title: '加载中...'
				});
				try {
					const [hRes, tRes] = await Promise.all([
						getHotel(this.hotelId),
						getType(this.roomTypeId)
					]);
					if (hRes.code === 200) this.hotelInfo = hRes.data;
					if (tRes.code === 200) this.roomType = tRes.data;
				} catch (e) {
					console.error('静态数据加载失败', e);
				} finally {
					uni.hideLoading();
				}
			},

			changeCount(delta) {
				const res = this.orderForm.roomCount + delta;
				if (res >= 1) this.orderForm.roomCount = res;
				// 注意：此处不进行上限拦截，由 submitOrder 捕获后端库存报错
			},

			async submitOrder() {
				// 1. 必填校验
				if (!this.orderForm.guestName || !this.orderForm.guestPhone) {
					return uni.showToast({
						title: '请填写完整信息',
						icon: 'none'
					});
				}

				uni.showLoading({
					title: '正在提交...'
				});

				try {
					// 2. 提交数据到后端
					// 后端会自动根据日期校验库存并计算金额
					const res = await addOrder({
						userId: this.orderForm.userId,
						hotelId: this.orderForm.hotelId,
						roomTypeId: this.orderForm.roomTypeId,
						roomCount: this.orderForm.roomCount,
						checkInDate: this.orderForm.checkInDate,
						checkOutDate: this.orderForm.checkOutDate,
						guestName: this.orderForm.guestName,
						guestPhone: this.orderForm.guestPhone
					});

					// 3. 处理结果
					if (res.code === 200) {
						uni.showToast({
							title: '下单成功',
							icon: 'success'
						});
						// 延迟跳转，让用户看清提示
						setTimeout(() => {
							uni.navigateTo({
								url: '/pages/order/order'
							}); // 跳转至你编写的订单列表页
						}, 1500);
					} else {
						// ✨ 核心逻辑：直接展示后端抛出的库存不足信息
						// 例如："抱歉，该时段房源火爆，仅剩 X 间"
						uni.showModal({
							title: '预订失败',
							content: res.msg || '下单失败，请重试',
							showCancel: false
						});
					}
				} catch (e) {
					uni.showToast({
						title: '网络繁忙，请稍后再试',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			getImageUrl(url) {
				if (!url) return '/static/default-hotel.png';
				const firstUrl = url.split(',')[0];
				return firstUrl.startsWith('http') ? firstUrl : 'http://localhost:8080' + firstUrl;
			},
			onPayChange(e) {
				this.payWay = e.detail.value;
			}
		}
	};
</script>

<style scoped>
	.order-create-container {
		background: #f8f9fb;
		min-height: 100vh;
		padding: 24rpx;
	}

	.room-preview-card {
		background: #fff;
		border-radius: 20rpx;
		padding: 24rpx;
		display: flex;
		align-items: center;
		margin-bottom: 24rpx;
	}

	.room-img {
		width: 150rpx;
		height: 150rpx;
		border-radius: 12rpx;
		flex-shrink: 0;
		background: #f0f0f0;
	}

	.room-detail {
		margin-left: 24rpx;
		flex: 1;
	}

	.hotel-name {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 8rpx;
		display: block;
	}

	.room-type-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 12rpx;
		display: block;
	}

	.date-info {
		font-size: 24rpx;
		color: #0086F6;
		background: #f0f8ff;
		padding: 8rpx 16rpx;
		border-radius: 8rpx;
		display: inline-block;
	}

	.order-form,
	.pay-method-section {
		background: #fff;
		border-radius: 20rpx;
		padding: 0 30rpx;
		margin-bottom: 24rpx;
	}

	.form-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 110rpx;
		border-bottom: 1rpx solid #f8f8f8;
	}

	.form-row:last-child {
		border-bottom: none;
	}

	.label {
		font-size: 28rpx;
		color: #333;
	}

	.input-field {
		flex: 1;
		text-align: right;
		font-size: 28rpx;
		color: #333;
	}

	.stepper {
		display: flex;
		align-items: center;
		border: 1rpx solid #eee;
		border-radius: 8rpx;
	}

	.step-btn {
		width: 60rpx;
		height: 60rpx;
		text-align: center;
		line-height: 56rpx;
		font-size: 36rpx;
		background: #f9f9f9;
		color: #666;
	}

	.step-btn.disabled {
		color: #ccc;
	}

	.count-num {
		width: 80rpx;
		text-align: center;
		font-size: 30rpx;
		font-weight: bold;
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		padding: 30rpx 0 20rpx;
		border-bottom: 1rpx solid #f8f8f8;
	}

	.pay-option {
		display: flex;
		align-items: center;
		justify-content: space-between;
		height: 100rpx;
	}

	.pay-left {
		display: flex;
		align-items: center;
		font-size: 28rpx;
	}

	.icon {
		margin-right: 16rpx;
		font-size: 32rpx;
	}

	.submit-bar {
		position: fixed;
		left: 0;
		bottom: 0;
		width: 100%;
		height: 120rpx;
		background: #fff;
		border-top: 1rpx solid #eee;
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 0 40rpx;
		box-sizing: border-box;
		z-index: 99;
	}

	.total-label {
		font-size: 24rpx;
		color: #666;
		margin-right: 10rpx;
	}

	.currency {
		color: #ff4d4f;
		font-size: 28rpx;
		font-weight: bold;
	}

	.total-price {
		color: #ff4d4f;
		font-size: 44rpx;
		font-weight: bold;
	}

	.submit-btn {
		background: linear-gradient(90deg, #00B4F6, #0086F6);
		color: #fff;
		border-radius: 44rpx;
		height: 88rpx;
		line-height: 88rpx;
		padding: 0 60rpx;
		margin: 0;
		font-weight: bold;
		font-size: 30rpx;
	}
</style>