<template>
	<view class="comment-list-container">
		<view class="tab-header">
			<view v-for="tab in tabs" :key="tab.value" class="tab-item" :class="{ active: currentTab === tab.value }"
				@click="handleTabChange(tab.value)">
				{{ tab.label }}
			</view>
		</view>

		<scroll-view scroll-y class="list-scroll" @scrolltolower="loadMore">
			<view v-if="orderList.length === 0" class="empty-state">
				<image src="/static/empty-box.png" mode="aspectFit"></image>
				<text>暂无相关订单评价</text>
			</view>

			<view class="order-card" v-for="item in orderList" :key="item.id">
				<view class="card-main" @click="goOrderDetail(item.id)">
				    <image :src="getImageUrl(item.roomImage)" mode="aspectFill" class="hotel-img"></image>
				    <view class="info-right">
				        <text class="hotel-name">{{ item.hotelName || '酒店预订' }}</text>
				        <text class="room-desc">{{ item.roomTypeName }} · 1间</text>
				        <text class="date-desc">入住时间：{{ formatDate(item.checkInDate) }}</text>
				    </view>
				</view>

				<view class="card-footer">
					<view class="price-info">实付款：<text class="price">¥{{ item.totalAmount }}</text></view>

					<button v-if="currentTab === 0" class="action-btn comment-btn"
						@click.stop="goToEvaluate(item.id)">去评价</button>

					<button v-else class="action-btn view-btn" @click.stop="goCommentDetail(item.id)">修改评价</button>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import {
		listOrder
	} from '@/api/order.js'

	export default {
		data() {
			return {
				currentTab: 0, // 0: 待评价, 1: 已评价
				tabs: [{
						label: '待评价',
						value: 0
					},
					{
						label: '已评价',
						value: 1
					}
				],
				orderList: [],
				queryParams: {
					pageNum: 1,
					pageSize: 10
				},
				baseUrl: 'http://localhost:8080'
			};
		},
		onShow() {
			this.refreshList();
		},
		methods: {
			goCommentDetail(orderId) {
			    const order = this.orderList.find(o => o.id === orderId);
			    if (order && order.commentId) {
			        uni.navigateTo({
			            url: `/pages/comment/comment-detail?id=${order.commentId}`
			        });
			    } else {
			        uni.showToast({ title: '未找到评价信息', icon: 'none' });
			    }
			},
			async fetchOrders() {
				const params = {
					...this.queryParams,
					status: 3, // 必须是已完成订单
					commentStatus: this.currentTab // 0为未评，1为已评
				};

				const res = await listOrder(params);
				if (res.code === 200) {
					this.orderList = this.queryParams.pageNum === 1 ? res.rows : this.orderList.concat(res.rows);
				}
			},
			handleTabChange(val) {
				this.currentTab = val;
				this.refreshList();
			},
			refreshList() {
				this.queryParams.pageNum = 1;
				this.fetchOrders();
			},
			loadMore() {
				this.queryParams.pageNum++;
				this.fetchOrders();
			},
			// 跳转至写评价页面
			goToEvaluate(orderId) {
				uni.navigateTo({
					url: `/pages/order/comment?orderId=${orderId}`
				});
			},
			goOrderDetail(id) {
				uni.navigateTo({
					url: `/pages/order/order-detail?id=${id}`
				});
			},
			//
			getImageUrl(url) {
			    // 1. 如果字段为空，返回默认图
			    if (!url) return '/static/default.png';
			
			    // 2. ✨ 关键：必须先用逗号拆分，只取第一个路径
			    // 这一步会将 "/profile/1.jpg,/profile/2.jpg" 变成 "/profile/1.jpg"
			    const firstUrl = url.split(',')[0];
			
			    // 3. 判断拆分后的第一个路径是否是完整 http 链接
			    if (firstUrl.startsWith('http')) {
			        return firstUrl;
			    }
			
			    // 4. 否则拼接后端地址
			    // 请确保你的 data() 里定义了 baseUrl: 'http://localhost:8080'
			    return this.baseUrl + firstUrl;
			},
			formatDate(date) {
				return date ? date.substring(0, 10) : '';
			}
		}
	}
</script>

<style scoped>
	.comment-list-container {
		background-color: #f8f9fb;
		min-height: 100vh;
	}

	/* 顶部Tab样式 */
	.tab-header {
		display: flex;
		background: #fff;
		height: 90rpx;
		align-items: center;
		position: sticky;
		top: 0;
		z-index: 10;
		border-bottom: 1rpx solid #eee;
	}

	.tab-item {
		flex: 1;
		text-align: center;
		font-size: 28rpx;
		color: #666;
		position: relative;
	}

	.tab-item.active {
		color: #0086F6;
		font-weight: bold;
	}

	.tab-item.active::after {
		content: '';
		position: absolute;
		bottom: -10rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 40rpx;
		height: 4rpx;
		background: #0086F6;
	}

	.list-scroll {
		height: calc(100vh - 90rpx);
		padding: 20rpx;
		box-sizing: border-box;
	}

	/* 卡片样式 */
	.order-card {
		background: #fff;
		border-radius: 20rpx;
		padding: 24rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.02);
	}

	.card-main {
		display: flex;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f2f2f2;
	}

	.hotel-img {
		width: 140rpx;
		height: 140rpx;
		border-radius: 12rpx;
		background: #f0f0f0;
	}

	.info-right {
		margin-left: 20rpx;
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.hotel-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.room-desc {
		font-size: 24rpx;
		color: #666;
	}

	.date-desc {
		font-size: 22rpx;
		color: #999;
	}

	.card-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 20rpx;
	}

	.price-info {
		font-size: 24rpx;
		color: #999;
	}

	.price {
		font-size: 30rpx;
		color: #333;
		font-weight: bold;
	}

	.action-btn {
		height: 60rpx;
		line-height: 60rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		margin: 0;
		padding: 0 30rpx;
		border: none;
	}

	.comment-btn {
		background: #0086F6;
		color: #fff;
	}

	.view-btn {
		background: #f5f5f5;
		color: #666;
	}

	.empty-state {
		text-align: center;
		padding-top: 200rpx;
		color: #999;
		font-size: 28rpx;
	}

	.empty-state image {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 20rpx;
		opacity: 0.5;
	}
</style>