<template>
	<view class="detail-container">
		<view class="header-section">
			<swiper class="banner-swiper" circular autoplay :interval="4000" indicator-dots
				indicator-active-color="#ffffff">
				<swiper-item>
					<image :src="getImageUrl(hotelInfo.bannerUrl)" mode="aspectFill" class="banner-img"></image>
				</swiper-item>
			</swiper>

			<view class="hotel-brief-card">
				<view class="title-row">
					<text class="name">{{ hotelInfo.nameCn || '加载中...' }}</text>
					<text class="nameEn">{{hotelInfo.nameEn}}</text>
					<text class="star" v-if="hotelInfo.starRating">{{ hotelInfo.starRating }}星级</text>
				</view>
				<view class="score-row">
					<text class="score">{{ hotelInfo.score || '4.5' }}分</text>
					<text class="score-desc">&nbsp;|&nbsp;</text>
					<text class="title">开业：</text>
					<text>
						{{ hotelInfo.openingDate ? hotelInfo.openingDate.substring(0, 4) : '未知' }}年
					</text>
				</view>
				<view class="phone-row">
					<text class="title">联系：</text>
					<text class="score">
						{{hotelInfo.phone}}
					</text>
				</view>
				<view class="date-row">
					
					<text class="comment-link" @click="goAllComments()">查看评论 ></text>
				</view>
				
				<view class="address-row" @click="openMap">
					<view class="addr-text">
						<text class="icon-loc">📍</text>
						<text>{{ (hotelInfo.province || '') + (hotelInfo.city || '') + (hotelInfo.district || '') + (hotelInfo.detailAddress || '') }}</text>
					</view>
					<text class="map-link">地图/周边 ></text>
				</view>
			</view>
		</view>

		<view class="date-section" @click="openCalendar">
			<view class="date-box">
				<view class="item">
					<text class="label">入住</text>
					<text class="val">{{ searchForm.checkInDate ? searchForm.checkInDate.substring(5) : '选择日期' }}</text>
				</view>
				<view class="nights">共{{ searchForm.days }}晚</view>
				<view class="item">
					<text class="label">离店</text>
					<text
						class="val">{{ searchForm.checkOutDate ? searchForm.checkOutDate.substring(5) : '选择日期' }}</text>
				</view>
			</view>
			<text class="change-btn">修改日期 ></text>
		</view>

		<view class="facility-section">
			<view class="f-item" v-if="hotelInfo.hasWifi === 1">
				<text class="f-icon">📶</text>
				<text>免费WiFi</text>
			</view>
			<view class="f-item" v-if="hotelInfo.park === '免费' || hotelInfo.park === '1'">
				<text class="f-icon">🅿️</text>
				<text>免费停车</text>
			</view>
			<view class="f-item" v-if="hotelInfo.hasElevator === 1">
				<text class="f-icon">🛗</text>
				<text>有电梯</text>
			</view>
		</view>

		<view class="filter-sticky-wrap">
			<scroll-view scroll-x class="category-scroll" :show-scrollbar="false">
				<view class="filter-tab" :class="{ active: selectedCategory === 'ALL' }"
					@click="selectedCategory = 'ALL'">全部</view>
				<view class="filter-tab" v-for="tab in categoryTabs" :key="tab.key"
					:class="{ active: selectedCategory === tab.key }" @click="selectedCategory = tab.key">
					{{ tab.label }}
				</view>
			</scroll-view>
		</view>

		<view class="room-list-section">
			<view v-if="filteredRooms.length === 0" class="empty-list">暂无对应房型</view>

			<view class="room-card" v-for="room in filteredRooms" :key="room.id" @click="goToDetail(room.id)">
				<image :src="getImageUrl(room.imageUrl)" mode="aspectFill" class="room-img"></image>

				<view class="room-info">
					<view class="r-top">
						<text class="r-name">{{ room.typeName }}</text>
						<view class="r-tags">
							<text class="tag">{{ room.hasWindow === '1' ? '有窗' : '无窗' }}</text>
							<text class="tag">{{ room.hasBreakfast === '1' ? '含早' : '不含早' }}</text>
							<text class="tag">{{ room.bedType }}</text>
						</view>
					</view>

					<view class="r-bottom">
						<view class="price-wrap">
							<text class="price-symbol">¥</text>
							<text class="price-val">{{ room.price }}</text>
						</view>
						<button class="book-btn" @click="goBooking(room)">预订</button>
					</view>
				</view>
			</view>
		</view>

		<wu-calendar ref="calendar" :insert="false" mode="range" color="#0086F6"
			@confirm="onCalendarConfirm"></wu-calendar>
	</view>
</template>

<script>
	import {
		getHotel
	} from '@/api/info.js'
	import {
		listType
	} from '@/api/type.js'

	export default {
		data() {
			return {
				hotelId: null,
				hotelInfo: {},
				roomList: [],
				selectedCategory: 'ALL', // 当前选中的分类
				// 房型转换字典
				categoryMap: {
					'SINGLE': '单人房',
					'KING': '大床房',
					'TWIN': '双人房/双床房',
					'FAMILY': '三人房/家庭房',
					'SUITE': '套房'
				},
				searchForm: {
					checkInDate: '02.25',
					checkOutDate: '02.26',
					days: 1
				}
			};
		},
		onLoad(options) {
			if (options.id) {
				this.hotelId = options.id;
				// ✨ 核心修改：接收从列表页传来的标准日期参数
				if (options.checkInDate && options.checkOutDate) {
					this.searchForm.checkInDate = options.checkInDate;
					this.searchForm.checkOutDate = options.checkOutDate;
					this.searchForm.days = parseInt(options.days) || 1;
				}
				this.fetchHotelData();
			}
		},
		computed: {
			// 动态生成滑动 Tab 列表
			categoryTabs() {
				if (!this.roomList.length) return [];
				// 获取去重后的房型大类标识符
				const categories = [...new Set(this.roomList.map(item => item.roomCategory))].filter(c => c);
				return categories.map(c => ({
					key: c,
					label: this.categoryMap[c] || c
				}));
			},
			// 根据选中分类过滤后的房型列表
			filteredRooms() {
				if (!this.roomList.length) return [];
				if (this.selectedCategory === 'ALL') return this.roomList;
				return this.roomList.filter(item => item.roomCategory === this.selectedCategory);
			}
		},
		methods: {
			goAllComments() {
			    uni.navigateTo({
			        url: `/pages/hotel/comment-list?hotelId=${this.hotelId}`
			    });
			},
			goToDetail(id) {
				uni.navigateTo({
				        url: `/pages/room-detail/room-detail?id=${id}&checkInDate=${this.searchForm.checkInDate}&checkOutDate=${this.searchForm.checkOutDate}&days=${this.searchForm.days}`
				    });
			},
			async fetchHotelData() {
				uni.showLoading({
					title: '加载中...'
				});
				try {
					const [infoRes, typeRes] = await Promise.all([
						getHotel(this.hotelId),
						listType({
							hotelId: this.hotelId
						})
					]);
					if (infoRes.code === 200) this.hotelInfo = infoRes.data; //
					if (typeRes.code === 200) {
						// 分页接口数据在 rows
						this.roomList = typeRes.rows || [];
					}
				} catch (e) {
					console.error('获取详情失败:', e);
				} finally {
					uni.hideLoading();
				}
			},
			getImageUrl(url) {
				if (!url) return '/static/default-hotel.jpg';

				// ✨ 核心修改：如果包含多个图片（如逗号分隔），只取第一个
				const firstUrl = url.split(',')[0];

				// 判断是否已经是完整路径
				if (firstUrl.startsWith('http')) {
					return firstUrl;
				}
				// 否则拼接后端服务器地址
				return 'http://localhost:8080' + firstUrl;
			},
			translateCategory(key) {
				return this.categoryMap[key] || key;
			},

			// ✨ 修改：保存标准日期格式
			onCalendarConfirm(e) {
				const range = Array.isArray(e) ? e : e.range.data;
				if (!range || range.length < 2) return;

				this.searchForm.checkInDate = range[0]; // 保存 YYYY-MM-DD
				this.searchForm.checkOutDate = range[range.length - 1];

				const d1 = new Date(this.searchForm.checkInDate.replace(/-/g, '/'));
				const d2 = new Date(this.searchForm.checkOutDate.replace(/-/g, '/'));
				this.searchForm.days = Math.round((d2 - d1) / (1000 * 60 * 60 * 24));
			},

			// ✨ 修改：跳转下单页时携带标准日期参数
			goBooking(room) {
				uni.navigateTo({
					url: `/pages/order/create?hotelId=${this.hotelId}&roomTypeId=${room.id}&checkIn=${this.searchForm.checkInDate}&checkOut=${this.searchForm.checkOutDate}&days=${this.searchForm.days}`
				});
			},
			openCalendar() {
				this.$refs.calendar.open();
			},
			openMap() {
				const lat = parseFloat(this.hotelInfo.latitude);
				const lng = parseFloat(this.hotelInfo.longitude);
				if (!lat || !lng) return uni.showToast({
					title: '暂无坐标',
					icon: 'none'
				});
				uni.openLocation({
					latitude: lat,
					longitude: lng,
					name: this.hotelInfo.nameCn
				});
			}
		}
	};
</script>

<style scoped>
	.detail-container {
		background: #f8f9fb;
		min-height: 100vh;
		padding-bottom: 60rpx;
	}

	/* 酒店头部 Banner */
	.banner-swiper {
		height: 420rpx;
	}

	.banner-img {
		width: 100%;
		height: 100%;
		background: #eee;
	}

	/* 酒店简报 */
	.hotel-brief-card {
		margin: -40rpx 24rpx 0;
		background: #fff;
		border-radius: 20rpx;
		padding: 30rpx;
		position: relative;
		z-index: 10;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.06);
	}

	.title-row {
		display: flex;
		align-items: baseline;
		gap: 16rpx;
	}

	.name {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}
	.nameEn{
		font-size: 28rpx;
		color: #787878;
	}

	.star {
		font-size: 24rpx;
		color: #0086F6;
		background: #eef7ff;
		padding: 4rpx 12rpx;
		border-radius: 6rpx;
	}

	.score-row {
		display: flex;
		align-items: center;
		margin-top: 15rpx;
		font-size: 26rpx;
		color: #0086F6;
		font-weight: bold;
	}
	.score-row .score{
		color: #e50000;
	}
	.score-row .score-desc{
		color: #333;
	}
	.phone-row {
		display: flex;
		align-items: center;
		margin-top: 15rpx;
		font-size: 26rpx;
		color: #666666;
	}
	.phone-row .title{
		color: #ff5500;
	}
	
	.date-row{
		color: #666;
		display: flex;
		align-items: center;
		margin-top: 15rpx;
		font-size: 26rpx;
		font-weight: bold;
	}

	.address-row {
		margin-top: 25rpx;
		padding-top: 25rpx;
		border-top: 1rpx solid #f2f2f2;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.addr-text {
		flex: 1;
		font-size: 26rpx;
		color: #666;
		display: flex;
	}

	.map-link {
		font-size: 24rpx;
		color: #0086F6;
	}

	/* 日期筛选 */
	.date-section {
		margin: 20rpx 24rpx;
		background: #fff;
		border-radius: 16rpx;
		padding: 24rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.date-box {
		display: flex;
		align-items: center;
		gap: 40rpx;
	}

	.val {
		font-size: 30rpx;
		font-weight: bold;
	}

	.nights {
		font-size: 22rpx;
		color: #0086F6;
		border: 1rpx solid #E1F1FF;
		padding: 2rpx 20rpx;
		border-radius: 30rpx;
		background: #F0F8FF;
	}

	/* 设施展示 */
	.facility-section {
		margin: 0 24rpx 20rpx;
		background: #fff;
		border-radius: 16rpx;
		padding: 20rpx;
		display: flex;
		gap: 50rpx;
	}

	.f-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 8rpx;
	}

	.f-icon {
		font-size: 38rpx;
	}

	.f-item text:last-child {
		font-size: 22rpx;
		color: #666;
	}

	/* ✨吸顶滑动筛选栏样式 */
	.filter-sticky-wrap {
		background: #fff;
		padding: 20rpx 0;
		position: sticky;
		top: 0;
		z-index: 99;
		box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.02);
	}

	.category-scroll {
		white-space: nowrap;
		padding: 0 24rpx;
	}

	.filter-tab {
		display: inline-block;
		padding: 12rpx 32rpx;
		margin-right: 20rpx;
		background: #f5f7f9;
		border-radius: 30rpx;
		font-size: 24rpx;
		color: #666;
		transition: all 0.2s;
	}

	.filter-tab.active {
		background: #0086F6;
		color: #fff;
		font-weight: bold;
	}

	/* ✨扁平化列表样式 */
	.room-list-section {
		padding: 20rpx 24rpx;
	}

	.room-card {
		display: flex;
		background: #fff;
		border-radius: 20rpx;
		padding: 24rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.03);
	}

	.room-img {
		width: 160rpx;
		height: 180rpx;
		border-radius: 12rpx;
		flex-shrink: 0;
		background: #f9f9f9;
	}

	.room-info {
		flex: 1;
		margin-left: 24rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.r-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.r-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 8rpx;
		margin-top: 10rpx;
	}

	.tag {
		font-size: 20rpx;
		color: #888;
		background: #f2f2f2;
		padding: 2rpx 10rpx;
		border-radius: 4rpx;
	}

	.r-bottom {
		display: flex;
		justify-content: space-between;
		align-items: flex-end;
		margin-top: 20rpx;
	}

	.price-symbol {
		color: #ff4d4f;
		font-size: 24rpx;
		font-weight: bold;
	}

	.price-val {
		color: #ff4d4f;
		font-size: 38rpx;
		font-weight: bold;
	}

	.book-btn {
		background: linear-gradient(90deg, #ffb84d, #ff9a14);
		color: #fff;
		border-radius: 10rpx;
		width: 130rpx;
		height: 60rpx;
		line-height: 60rpx;
		font-size: 26rpx;
		font-weight: bold;
		margin: 0;
		padding: 0;
	}

	.empty-list {
		text-align: center;
		padding: 100rpx;
		color: #999;
		font-size: 26rpx;
	}

	.change-btn {
		font-size: 22rpx;
		color: #787878;
	}
</style>