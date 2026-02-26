<template>
	<view class="order-list-container">
		<view class="status-tabs">
			<view v-for="tab in tabs" :key="tab.value" 
				class="tab-item" :class="{ active: currentTab === tab.value }"
				@click="handleTabChange(tab.value)">
				{{ tab.label }}
			</view>
		</view>

		<scroll-view scroll-y class="order-scroll" @scrolltolower="loadMore">
			<view v-if="orderList.length === 0" class="empty-box">暂无相关订单</view>
			
			<view class="order-card" v-for="order in orderList" :key="order.id" @click="goDetail(order.id)">
				<view class="card-header">
					<text class="order-sn">单号：{{ order.orderSn }}</text>
					<text class="status-text" :class="'status-' + order.status">
						{{ getStatusLabel(order.status) }}
					</text>
				</view>
				
				<view class="card-body">
					<view class="hotel-info">
						<text class="h-name">{{ order.hotelName || '酒店预订' }}</text>
						<text class="r-type">{{ order.roomTypeName }} · {{ order.roomCount }}间</text>
					</view>
					<view class="date-info">
						{{ formatDate(order.checkInDate) }} - {{ formatDate(order.checkOutDate) }}
						<text class="total-days">共{{ order.totalDays }}晚</text>
					</view>
				</view>

				<view class="card-footer">
					<view class="price-box">
						<text class="label">实付款</text>
						<text class="unit">¥</text>
						<text class="amount">{{ order.totalAmount }}</text>
					</view>
					
					<view class="actions">
					    <button v-if="order.status === 0" class="btn pay-btn" @click.stop="payVirtual(order)">立即支付</button>
					    
					    <button v-if="order.status === 3 && order.commentStatus === 0" 
					            class="btn comment-btn" 
					            @click.stop="goComment(order.id)">去评价</button>
					            
					    <text v-if="order.status === 3 && order.commentStatus === 1" class="commented-tag">已评价</text>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { listOrder, updateOrder } from '@/api/order.js'

	export default {
		data() {
			return {
				currentTab: -1, // -1 代表全部
				tabs: [
					{ label: '全部', value: -1 },
					{ label: '待支付', value: 0 },
					{ label: '待入住', value: 1 },
					{ label: '已完成', value: 3 } // ✨ 已完成状态对应后端 3
				],
				orderList: [],
				queryParams: { pageNum: 1, pageSize: 10 }
			};
		},
		onShow() {
			this.refreshList();
		},
		methods: {
			async fetchOrders() {
				const params = { ...this.queryParams };
				if (this.currentTab !== -1) params.status = this.currentTab;
				
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
			// ✨ 更新了状态标签映射
			getStatusLabel(status) {
				const labels = { 
					0: '待支付', 
					1: '待入住', 
					2: '入住中', 
					3: '已完成', 
					4: '已取消', 
					5: '已退款' 
				};
				return labels[status] || '未知';
			},
			formatDate(dateStr) {
				return dateStr ? dateStr.substring(5).replace(/-/g, '.') : '';
			},
			async payVirtual(order) {
			    uni.showLoading({ title: '模拟支付中...', mask: true });
			    try {
			        const res = await updateOrder({ id: order.id, status: 1 });
			        if (res.code === 200) {
			            uni.showToast({ title: '支付成功', icon: 'success' });
			            this.refreshList(); 
			        } else {
			            uni.showToast({ title: res.msg || '支付失败', icon: 'none' });
			        }
			    } catch (e) {
			        uni.showToast({ title: '请求失败，请检查网络', icon: 'none' });
			    } finally {
			        uni.hideLoading();
			    }
			},
			// ✨ 跳转到评价页面
			goComment(orderId) {
				uni.navigateTo({
					url: `/pages/order/comment?orderId=${orderId}`
				});
			},
			goDetail(id) {
				uni.navigateTo({ url: `/pages/order/order-detail?id=${id}` });
			},
			loadMore() {
				this.queryParams.pageNum++;
				this.fetchOrders();
			}
		}
	}
</script>

<style scoped>
	.order-list-container { background: #f8f9fb; min-height: 100vh; }
	.status-tabs { display: flex; background: #fff; height: 90rpx; align-items: center; position: sticky; top: 0; z-index: 10; border-bottom: 1rpx solid #eee; }
	.tab-item { flex: 1; text-align: center; font-size: 28rpx; color: #666; position: relative; }
	.tab-item.active { color: #0086F6; font-weight: bold; }
	.tab-item.active::after { content: ''; position: absolute; bottom: -10rpx; left: 50%; transform: translateX(-50%); width: 40rpx; height: 4rpx; background: #0086F6; }
	
	.order-scroll { height: calc(100vh - 90rpx); padding: 20rpx; box-sizing: border-box; }
	.order-card { background: #fff; border-radius: 20rpx; padding: 24rpx; margin-bottom: 24rpx; box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.02); }
	.card-header { display: flex; justify-content: space-between; border-bottom: 1rpx solid #f2f2f2; padding-bottom: 16rpx; }
	.order-sn { font-size: 24rpx; color: #999; }
	.status-text { font-size: 26rpx; font-weight: bold; }
	.status-0 { color: #ff4d4f; }
	.status-1 { color: #0086F6; }
	.status-3 { color: #52c41a; }
	
	.card-body { padding: 24rpx 0; }
	.h-name { font-size: 32rpx; font-weight: bold; color: #333; display: block; }
	.r-type { font-size: 26rpx; color: #666; margin-top: 8rpx; display: block; }
	.date-info { font-size: 24rpx; color: #999; margin-top: 16rpx; }
	.total-days { margin-left: 20rpx; color: #333; }
	
	.card-footer { display: flex; justify-content: space-between; align-items: center; border-top: 1rpx solid #f2f2f2; padding-top: 16rpx; }
	.price-box { display: flex; align-items: baseline; }
	.label { font-size: 24rpx; color: #999; margin-right: 8rpx; }
	.unit { font-size: 24rpx; color: #333; font-weight: bold; }
	.amount { font-size: 36rpx; font-weight: bold; color: #333; }
	
	.actions { display: flex; gap: 16rpx; }
	.btn { font-size: 24rpx; border-radius: 30rpx; padding: 0 30rpx; height: 60rpx; line-height: 60rpx; margin: 0; border: none; }
	.pay-btn { background: #0086F6; color: #fff; }
	/* ✨ 评价按钮样式 */
	.comment-btn { background: #fff; color: #0086F6; border: 1rpx solid #0086F6; }
	
	.empty-box { text-align: center; color: #999; padding-top: 100rpx; font-size: 28rpx; }
</style>