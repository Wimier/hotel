<template>
	<view class="room-detail-container">
		<view class="swiper-section">
			<swiper class="room-swiper" indicator-dots circular autoplay :interval="5000" indicator-active-color="#0086F6">
				<swiper-item v-for="(img, index) in imageList" :key="index">
					<image :src="img" mode="aspectFill" class="swiper-img" @click="previewImage(index)"></image>
				</swiper-item>
			</swiper>
			<view class="image-count" v-if="imageList.length > 0">{{ imageList.length }}张图片</view>
		</view>

		<view class="info-card">
			<view class="title-row">
				<text class="room-name">{{ roomInfo.typeName || '加载中...' }}</text>
				<text class="room-tag">{{ translateCategory(roomInfo.roomCategory) }}</text>
			</view>
			
			<view class="specs-grid">
				<view class="spec-item">
					<text class="spec-label">床型</text>
					<text class="spec-val">{{ roomInfo.bedType || '标准床型' }}</text>
				</view>
				<view class="spec-item">
					<text class="spec-label">窗户</text>
					<text class="spec-val">{{ roomInfo.hasWindow === '1' ? '有窗' : '无窗' }}</text>
				</view>
				<view class="spec-item">
					<text class="spec-label">早餐</text>
					<text class="spec-val">{{ roomInfo.hasBreakfast === '1' ? '含早餐' : '不含早' }}</text>
				</view>
				<view class="spec-item">
					<text class="spec-label">可住</text>
					<text class="spec-val">2人</text>
				</view>
			</view>
		</view>

		<view class="amenity-container">
			<view class="amenity-group">
				<view class="group-title">
					<text class="g-icon">🧹</text><text>清洁服务</text>
				</view>
				<view class="item-grid">
					<text class="item">每日打扫</text><text class="item">打扫工具</text>
					<text class="item">干衣机</text><text class="item">洗衣用品</text>
					<text class="item">熨衣设备</text><text class="item">熨裤机</text>
				</view>
			</view>

			<view class="amenity-group">
				<view class="group-title">
					<text class="g-icon">🧴</text><text>洗浴用品</text>
				</view>
				<view class="item-grid">
					<view class="item">牙刷 <text class="sub-tag">需前台索要</text></view>
					<view class="item">牙膏 <text class="sub-tag">需前台索要</text></view>
					<view class="item">沐浴露 <text class="brand">Curél (珂润)</text></view>
					<text class="item">洗发水</text><text class="item">护发素</text>
					<text class="item">香皂</text><text class="item">浴帽</text>
					<text class="item">梳子</text><text class="item">剃须刀</text>
				</view>
			</view>

			<view class="amenity-group">
				<view class="group-title">
					<text class="g-icon">♿</text><text>无障碍设施</text>
				</view>
				<view class="item-grid">
					<text class="item">无障碍通道</text><text class="item">无障碍淋浴</text>
					<text class="item">坐便器-带扶手</text><text class="item">改造生活区</text>
					<text class="item">视觉火灾报警器</text>
				</view>
			</view>
		</view>

		<view class="detail-section">
			<view class="section-title">房型详情</view>
			<text class="detail-content">{{ roomInfo.remark || '暂无详细描述' }}</text>
		</view>

		<view class="policy-section">
			<view class="section-title">预订须知</view>
			<view class="policy-item">
				<text class="p-tag">取消政策</text>
				<text class="p-desc">入住前1天18:00前可免费取消，逾期将扣除首晚房费作为违约金。</text>
			</view>
			<view class="policy-item">
				<text class="p-tag">入住凭证</text>
				<text class="p-desc">请携带所有入住人有效身份证件（身份证/护照/港澳台通行证）办理入住。</text>
			</view>
			<view class="policy-item">
				<text class="p-tag">隐私政策</text>
				<text class="p-desc">我们高度重视您的个人隐私。您提供的姓名、电话、身份证号等信息仅用于向公安机关及酒店系统进行实名登记，除非法律要求，否则不会向第三方透露。</text>
			</view>
		</view>

		<view class="bottom-bar">
			<view class="price-info">
				<text class="unit">¥</text>
				<text class="price">{{ roomInfo.price || 0 }}</text>
				<text class="total">/晚</text>
			</view>
			<button class="book-btn" @click="goOrderConfirm">立即预订</button>
		</view>
	</view>
</template>

<script>
	import { getType } from '@/api/type.js' 
	
	export default {
	    data() {
	        return {
	            roomId: null,
	            roomInfo: {},
	            imageList: [],
	            // ✨ 新增：存储从酒店页面传来的日期信息
	            checkInDate: '',
	            checkOutDate: '',
	            totalDays: 1,
	            // ✨ 分类映射字典
	            categoryMap: {
	                'SINGLE': '单人房',
	                'KING': '大床房',
	                'TWIN': '双人房/双床房',
	                'FAMILY': '三人房/家庭房',
	                'SUITE': '套房'
	            },
	            baseUrl: 'http://localhost:8080'
	        };
	    },
	    onLoad(options) {
	        if (options.id) {
	            this.roomId = options.id;
	            
	            // ✨ 1. 接收从酒店详情页传来的日期参数
	            this.checkInDate = options.checkInDate || '';
	            this.checkOutDate = options.checkOutDate || '';
	            this.totalDays = options.days || 1;
	            
	            this.fetchRoomDetail();
	        }
	    },
	    methods: {
	        async fetchRoomDetail() {
	            uni.showLoading({ title: '加载中...' });
	            try {
	                const res = await getType(this.roomId); 
	                if (res.code === 200) {
	                    this.roomInfo = res.data; 
	                    this.handleImages(this.roomInfo.imageUrl);
	                }
	            } catch (e) {
	                uni.showToast({ title: '数据获取失败', icon: 'none' });
	            } finally {
	                uni.hideLoading();
	            }
	        },
	        
	        // ✨ 翻译大类名称
	        translateCategory(key) {
	            if (!key) return '';
	            return this.categoryMap[key] || key;
	        },
	
	        handleImages(urlStr) {
	            if (!urlStr) {
	                this.imageList = ['/static/default-hotel.png'];
	                return;
	            }
	            this.imageList = urlStr.split(',').map(url => {
	                return url.startsWith('http') ? url : this.baseUrl + url;
	            });
	        },
	
	        previewImage(current) {
	            uni.previewImage({ current: this.imageList[current], urls: this.imageList });
	        },
	
	        // ✨ 2. 修改跳转逻辑：将日期信息继续传递给下单页
	        goOrderConfirm() {
	            // 跳转下单页，携带酒店ID、房型ID以及日期参数
	            // 注意：参数名要与 create.vue 的 onLoad 接收名保持一致 (checkIn, checkOut, days)
	            uni.navigateTo({
	                url: `/pages/order/create?hotelId=${this.roomInfo.hotelId}&roomTypeId=${this.roomInfo.id}&checkIn=${this.checkInDate}&checkOut=${this.checkOutDate}&days=${this.totalDays}`
	            });
	        }
	    }
	};
</script>

<style scoped>
	.room-detail-container { background: #f8f9fb; min-height: 100vh; padding-bottom: 140rpx; }

	.swiper-section { position: relative; }
	.room-swiper { height: 500rpx; }
	.swiper-img { width: 100%; height: 100%; }
	.image-count { position: absolute; right: 30rpx; bottom: 30rpx; background: rgba(0,0,0,0.5); color: #fff; padding: 4rpx 20rpx; border-radius: 20rpx; font-size: 22rpx; }

	.info-card { background: #fff; margin-bottom: 20rpx; padding: 30rpx; }
	.title-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 30rpx; }
	.room-name { font-size: 38rpx; font-weight: bold; color: #333; }
	.room-tag { font-size: 22rpx; color: #0086F6; background: #eef7ff; padding: 4rpx 16rpx; border-radius: 6rpx; }
	.specs-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20rpx; background: #f9f9f9; padding: 24rpx; border-radius: 12rpx; }
	.spec-item { display: flex; align-items: center; font-size: 26rpx; }
	.spec-label { color: #999; width: 80rpx; }
	.spec-val { color: #333; font-weight: 500; }

	/* 设施板块 */
	.amenity-container { background: #fff; margin-bottom: 20rpx; padding: 30rpx; }
	.amenity-group { margin-bottom: 35rpx; }
	.group-title { font-size: 28rpx; font-weight: bold; color: #333; margin-bottom: 20rpx; display: flex; align-items: center; }
	.g-icon { margin-right: 10rpx; font-size: 32rpx; }
	.item-grid { display: grid; grid-template-columns: 1fr 1fr; row-gap: 15rpx; column-gap: 20rpx; }
	.item { font-size: 24rpx; color: #666; display: flex; align-items: center; }
	.sub-tag { font-size: 18rpx; color: #999; background: #f0f0f0; padding: 2rpx 6rpx; border-radius: 4rpx; margin-left: 6rpx; }
	.brand { color: #0086F6; margin-left: 6rpx; }

	.detail-section, .policy-section { background: #fff; margin-bottom: 20rpx; padding: 30rpx; }
	.section-title { font-size: 30rpx; font-weight: bold; color: #333; margin-bottom: 15rpx; }
	.detail-content { font-size: 26rpx; color: #666; line-height: 1.6; }

	.policy-item { margin-bottom: 20rpx; }
	.p-tag { font-size: 24rpx; color: #333; font-weight: bold; display: block; margin-bottom: 6rpx; }
	.p-desc { font-size: 24rpx; color: #888; line-height: 1.4; }

	.bottom-bar { position: fixed; left: 0; bottom: 0; width: 100%; height: 120rpx; background: #fff; display: flex; align-items: center; justify-content: space-between; padding: 0 40rpx; box-sizing: border-box; border-top: 1rpx solid #eee; z-index: 100; box-shadow: 0 -4rpx 20rpx rgba(0,0,0,0.05); }
	.price-info { color: #ff4d4f; }
	.unit { font-size: 28rpx; font-weight: bold; }
	.price { font-size: 48rpx; font-weight: bold; }
	.total { font-size: 24rpx; color: #999; }
	.book-btn { background: linear-gradient(90deg, #ffb84d, #ff9a14); color: #fff; margin: 0; padding: 0 60rpx; height: 80rpx; line-height: 80rpx; border-radius: 40rpx; font-size: 30rpx; font-weight: bold; }
</style>