<template>
	<view class="list-container">
		<view class="header-fixed">
			<view class="search-wrap">
				<view class="search-main-box">
					<view class="city-picker" @click="goCitySelect">
						<text class="c-name">{{ queryParams.city }}</text>
						<text class="c-icon">▼</text>
					</view>
					<view class="v-line"></view>
					<text class="reset-kw-btn" v-if="queryParams.keyword" @click="resetKeyword">✕</text>
					<input class="search-input" v-model="queryParams.keyword" placeholder="搜索酒店/地名"
						placeholder-style="color:#bbb;" @confirm="refreshList" />
					<view class="action-icons">
						<text class="icon-search" @click="refreshList">🔍</text>
						<view class="mini-line"></view>
						<text class="icon-map" @click="toggleMapMode">{{ isMapMode ? '📋' : '📍' }}</text>
					</view>
				</view>
			</view>

			<view class="filter-bar" v-if="!isMapMode">
				<view class="date-pill" @click="openCalendar">
					<text class="p-date">{{ displayForm.inDateText }} - {{ displayForm.outDateText }}</text>
					<text class="p-arrow">▼</text>
				</view>

				<view class="sort-group">
					<view class="sort-item" :class="{active: sortType === 'score'}" @click="handleSort('score')">欢迎度
					</view>
					<picker @change="onStarChange" :range="starOptions" range-key="label">
						<view class="sort-item" :class="{active: queryParams.starRating}">
							{{ selectedStarLabel || '星级' }}
						</view>
					</picker>
					<view class="sort-item" @click="togglePriceOrder">
						价格
						<text class="price-arrow">{{priceOrder === 'asc' ? '↑' : '↓'}}</text>
					</view>
					<view class="sort-item reset-btn" @click="resetAllFilters">重置</view>
				</view>
			</view>
		</view>

		<view class="main-content">
			<scroll-view v-if="!isMapMode" scroll-y class="scroll-body" @scrolltolower="loadMore">
				<view class="hotel-card" v-for="item in hotelList" :key="item.id" @click="goDetail(item.id)">
					<image class="h-thumb" :src="getImageUrl(item.bannerUrl)" mode="aspectFill"></image>
					<view class="h-body">
						<view class="h-name">{{item.nameCn}}</view>
						<view class="h-score-row">
							<text class="h-score">{{item.score || '4.5'}}</text>
							<text class="h-score-text">分 / 超赞</text>
							<text class="h-star">{{item.starRating}}星</text>
						</view>
						<view class="h-tags">
							<text class="t-item t-blue" v-if="item.hasWifi === 1">免费WiFi</text>
							<text class="t-item t-orange" v-if="item.park === '免费'">免费停车</text>
						</view>
						<view class="h-price-row">
							<view class="h-price">
								<text class="p-unit">¥</text>
								<text class="p-num">{{item.price}}</text>
								<text class="p-start">起</text>
							</view>
						</view>
					</view>
				</view>
				<view class="load-more">{{ loading ? '正在加载...' : (noMore ? '到底啦，没有更多了' : '继续上划加载') }}</view>
			</scroll-view>

			<view class="map-box" v-else>
				<map class="map-instance" :latitude="mapCenter.latitude" :longitude="mapCenter.longitude"
					:markers="mapMarkers" @markertap="onMarkerTap" show-location>
				</map>
				<view class="floating-card" v-if="activeHotel" @click="goDetail(activeHotel.id)">
					<image :src="getImageUrl(activeHotel.bannerUrl)" mode="aspectFill"></image>
					<view class="f-info">
						<view class="f-name">{{activeHotel.nameCn}}</view>
						<view class="f-bottom">
							<text class="f-score">⭐️ {{activeHotel.score}}分</text>
							<text class="f-price">¥{{activeHotel.price}}起</text>
						</view>
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
		listHotel
	} from '@/api/info.js'
	const QQMapWX = require('@/libs/qqmap-wx-jssdk.js');
	const qqmapsdk = new QQMapWX({
		key: 'GNVBZ-Q5I3M-PGP64-637TW-NNHYH-NNFXX'
	});

	export default {
		data() {
			return {
				hotelList: [],
				loading: false,
				noMore: false,
				isMapMode: false,
				activeHotel: null,
				mapCenter: {
					latitude: 31.2304,
					longitude: 121.4737
				},
				mapMarkers: [],
				sortType: 'default',
				priceOrder: 'asc',
				selectedStarLabel: '',
				starOptions: [{
						label: '全部星级',
						value: undefined
					},
					{
						label: '3星',
						value: 3
					},
					{
						label: '4星',
						value: 4
					},
					{
						label: '5星',
						value: 5
					}
				],
				displayForm: {
					city: '',
					inDateText: '',
					outDateText: '',
					keyword: ''
				},
				queryParams: {
					pageNum: 1,
					pageSize: 10,
					status: 'PASS',
					city: '',
					keyword: '',
					starRating: undefined,
					park: undefined,
					hasWifi: undefined
				}
			}
		},
		//
		onLoad(options) {
		    if (options.params) {
		        const params = JSON.parse(decodeURIComponent(options.params));
		        
		        // ✨ 1. 同步逻辑日期：用于发给后端请求
		        this.queryParams.city = params.city;
		        this.queryParams.keyword = params.keyword;
		        // 确保 queryParams 中也存一份标准日期，方便后续传给详情页
		        this.queryParams.checkInDate = params.checkInDate; 
		        this.queryParams.checkOutDate = params.checkOutDate;
		        this.queryParams.days = params.days || 1;
		
		        // ✨ 2. 同步显示日期：用于界面顶部的“MM.DD”展示
		        this.displayForm = {
		            city: params.city,
		            // 直接从 '2026-03-01' 截取得到 '03.01'
		            inDateText: params.checkInDate ? params.checkInDate.substring(5).replace(/-/g, '.') : '',
		            outDateText: params.checkOutDate ? params.checkOutDate.substring(5).replace(/-/g, '.') : '',
		            keyword: params.keyword
		        };
		
		        // 处理快速筛选标签
		        if (params.filters) {
		            params.filters.forEach(tag => {
		                if (tag === 'star5') this.queryParams.starRating = 5;
		                if (tag === 'park') this.queryParams.park = '免费';
		                if (tag === 'wifi') this.queryParams.hasWifi = 1;
		            });
		        }
		    }
		    this.updateMapByCity(this.queryParams.city);
		    this.loadData();
		},
		onShow() {
			uni.$once('updateCity', (city) => {
				if (city) {
					this.queryParams.city = city;
					this.updateMapByCity(city);
					this.refreshList();
				}
			});
		},
		methods: {
			resetKeyword() {
				this.queryParams.keyword = ''; // 清空输入框内容
				this.refreshList(); // 重新请求第一页数据
			},

			// ✨新增方法 2：全局筛选重置
			resetAllFilters() {
				// 恢复初始状态
				this.sortType = 'default'; // 恢复默认排序
				this.priceOrder = 'asc'; // 恢复价格升序
				this.selectedStarLabel = ''; // 清空星级显示文字

				// 重置所有查询参数
				this.queryParams.starRating = undefined; // 清空星级筛选
				this.queryParams.keyword = ''; // 清空关键字
				this.queryParams.park = undefined; // 清空停车筛选
				this.queryParams.hasWifi = undefined; // 清空WiFi筛选

				this.refreshList(); // 刷新数据
				uni.showToast({
					title: '已重置筛选',
					icon: 'none'
				});
			},
			toggleMapMode() {
				this.isMapMode = !this.isMapMode;
				if (this.isMapMode) this.generateMarkers();
			},
			updateMapByCity(cityName) {
				qqmapsdk.geocoder({
					address: cityName,
					success: (res) => {
						this.mapCenter = {
							latitude: res.result.location.lat,
							longitude: res.result.location.lng
						};
					}
				});
			},
			generateMarkers() {
				this.mapMarkers = this.hotelList.map(h => ({
					id: h.id,
					latitude: h.latitude || (this.mapCenter.latitude + (Math.random() - 0.5) * 0.01),
					longitude: h.longitude || (this.mapCenter.longitude + (Math.random() - 0.5) * 0.01),
					iconPath: '/static/location-pin.png',
					width: 24,
					height: 30,
					callout: {
						content: `¥${h.price}`,
						display: 'ALWAYS',
						padding: 5,
						borderRadius: 15,
						bgColor: '#0086F6',
						color: '#fff'
					}
				}));
			},
			async loadData() {
				if (this.loading || this.noMore) return;
				this.loading = true;
				try {
					const sendParams = {
						...this.queryParams
					};
					Object.keys(sendParams).forEach(k => {
						if (sendParams[k] === undefined || sendParams[k] === '') delete sendParams[k];
					});
					if (this.queryParams.keyword) sendParams['params[keyword]'] = this.queryParams.keyword;

					const res = await listHotel(sendParams);
					if (res.code === 200) {
						let rows = res.rows || [];
						// 欢迎度随机逻辑
						if (this.sortType === 'score') {
							rows.sort((a, b) => b.score === a.score ? Math.random() - 0.5 : b.score - a.score);
						} else if (this.sortType === 'price') {
							rows.sort((a, b) => this.priceOrder === 'asc' ? a.price - b.price : b.price - a.price);
						}
						this.hotelList = this.queryParams.pageNum === 1 ? rows : this.hotelList.concat(rows);
						if (rows.length < this.queryParams.pageSize) this.noMore = true;
						if (this.isMapMode) this.generateMarkers();
					}
				} finally {
					this.loading = false;
				}
			},
			refreshList() {
				this.queryParams.pageNum = 1;
				this.hotelList = [];
				this.noMore = false;
				this.loadData();
			},
			//
			onCalendarConfirm(e) {
			    const dateRange = Array.isArray(e) ? e : e.range.data;
			    if (!dateRange || dateRange.length < 2) return;
			
			    const startDate = dateRange[0]; // '2026-03-01'
			    const endDate = dateRange[dateRange.length - 1];
			
			    // ✨ 1. 更新后端需要的标准格式
			    this.queryParams.checkInDate = startDate;
			    this.queryParams.checkOutDate = endDate;
			
			    // ✨ 2. 更新前端显示的精简格式
			    this.displayForm.inDateText = startDate.substring(5).replace(/-/g, '.');
			    this.displayForm.outDateText = endDate.substring(5).replace(/-/g, '.');
			
			    // 3. 重新计算天数
			    const d1 = new Date(startDate.replace(/-/g, '/'));
			    const d2 = new Date(endDate.replace(/-/g, '/'));
			    this.queryParams.days = Math.round((d2 - d1) / (1000 * 60 * 60 * 24));
			
			    this.refreshList();
			},
			onStarChange(e) {
				const item = this.starOptions[e.detail.value];
				this.queryParams.starRating = item.value;
				this.selectedStarLabel = item.value ? item.label : '';
				this.refreshList();
			},
			handleSort(type) {
				this.sortType = type;
				this.refreshList();
			},
			togglePriceOrder() {
				this.sortType = 'price';
				this.priceOrder = this.priceOrder === 'asc' ? 'desc' : 'asc';
				this.refreshList();
			},
			openCalendar() {
				this.$refs.calendar.open();
			},
			goCitySelect() {
				uni.navigateTo({
					url: '/pages/city/city'
				});
			},
			getImageUrl(url) {
				return !url ? '/static/default-hotel.png' : (url.startsWith('http') ? url : 'http://localhost:8080' + url);
			},
			//
			goDetail(id) {
			    uni.navigateTo({
			        url: `/pages/detail/detail?id=${id}&checkInDate=${this.queryParams.checkInDate}&checkOutDate=${this.queryParams.checkOutDate}&days=${this.queryParams.days}`
			    });
			},
			loadMore() {
				this.queryParams.pageNum++;
				this.loadData();
			},
			onMarkerTap(e) {
				this.activeHotel = this.hotelList.find(h => h.id === e.detail.markerId);
			}
		}
	}
</script>

<style scoped>
	.list-container {
		min-height: 100vh;
		background-color: #f8f9fb;
	}

	.header-fixed {
		position: sticky;
		top: 0;
		z-index: 100;
		background: #fff;
		padding: 20rpx 24rpx 10rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.03);
	}

	/* 搜索框美化 */
	.search-main-box {
		display: flex;
		align-items: center;
		background: #f2f3f5;
		height: 84rpx;
		border-radius: 42rpx;
		padding: 0 30rpx;
	}

	.city-picker {
		display: flex;
		align-items: center;
		min-width: 100rpx;
	}

	.c-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.c-icon {
		font-size: 16rpx;
		color: #999;
		margin-left: 8rpx;
	}

	.v-line {
		width: 2rpx;
		height: 30rpx;
		background: #ddd;
		margin: 0 24rpx;
	}

	.search-input {
		flex: 1;
		font-size: 26rpx;
		color: #333;
	}

	.action-icons {
		display: flex;
		align-items: center;
	}

	.mini-line {
		width: 1rpx;
		height: 24rpx;
		background: #ccc;
		margin: 0 20rpx;
	}

	.icon-search {
		font-size: 32rpx;
	}

	.icon-map {
		font-size: 36rpx;
	}

	/* 过滤栏美化 */
	.filter-bar {
		display: flex;
		align-items: center;
		height: 80rpx;
		margin-top: 10rpx;
	}

	.date-pill {
		background: #f0f7ff;
		color: #0086F6;
		font-size: 22rpx;
		font-weight: bold;
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
		display: flex;
		align-items: center;
	}

	.p-arrow {
		font-size: 14rpx;
		margin-left: 8rpx;
	}

	.sort-group {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		gap: 40rpx;
	}

	.sort-item {
		font-size: 24rpx;
		color: #666;
		position: relative;
	}

	.sort-item.active {
		color: #0086F6;
		font-weight: bold;
	}

	/* 酒店卡片美化 */
	.scroll-body {
		height: calc(100vh - 200rpx);
		padding: 20rpx;
		box-sizing: border-box;
	}

	.hotel-card {
		display: flex;
		background: #fff;
		border-radius: 24rpx;
		padding: 20rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.02);
	}

	.h-thumb {
		width: 190rpx;
		height: 240rpx;
		border-radius: 16rpx;
	}

	.h-body {
		flex: 1;
		margin-left: 24rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.h-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #1a1a1a;
		display: -webkit-box;
		-webkit-box-orient: vertical;
		-webkit-line-clamp: 2;
		overflow: hidden;
	}

	.h-score-row {
		display: flex;
		align-items: baseline;
		margin-top: 8rpx;
	}

	.h-score {
		font-size: 34rpx;
		color: #0086F6;
		font-weight: bold;
	}

	.h-score-text {
		font-size: 22rpx;
		color: #0086F6;
		margin: 0 12rpx;
	}

	.h-star {
		font-size: 22rpx;
		color: #999;
	}

	.h-tags {
		display: flex;
		gap: 12rpx;
		margin-top: 10rpx;
	}

	.t-item {
		font-size: 20rpx;
		padding: 4rpx 12rpx;
		border-radius: 6rpx;
	}

	.t-blue {
		color: #0086F6;
		background: #ebf5ff;
	}

	.t-orange {
		color: #ff6a00;
		background: #fff4eb;
	}

	.h-price-row {
		display: flex;
		justify-content: flex-end;
		margin-top: 10rpx;
	}

	.p-unit {
		color: #ff4d4f;
		font-size: 24rpx;
		font-weight: bold;
	}

	.p-num {
		color: #ff4d4f;
		font-size: 44rpx;
		font-weight: bold;
		margin: 0 4rpx;
	}

	.p-start {
		color: #999;
		font-size: 20rpx;
	}

	/* 地图模式 */
	.map-box {
		width: 100%;
		height: calc(100vh - 194rpx);
		position: relative;
	}

	.map-instance {
		width: 100%;
		height: 100%;
	}

	.floating-card {
		position: absolute;
		bottom: 40rpx;
		left: 30rpx;
		right: 30rpx;
		background: #fff;
		border-radius: 28rpx;
		padding: 24rpx;
		display: flex;
		box-shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.12);
	}

	.floating-card image {
		width: 110rpx;
		height: 110rpx;
		border-radius: 16rpx;
	}

	.f-info {
		flex: 1;
		margin-left: 20rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.f-name {
		font-size: 28rpx;
		font-weight: bold;
	}

	.f-bottom {
		display: flex;
		justify-content: space-between;
	}

	.f-score {
		font-size: 24rpx;
		color: #0086F6;
	}

	.f-price {
		font-size: 28rpx;
		color: #ff4d4f;
		font-weight: bold;
	}

	.load-more {
		text-align: center;
		padding: 40rpx;
		font-size: 24rpx;
		color: #999;
	}

	/* ✨新增：输入框左侧“✕”图标样式 */
	.reset-kw-btn {
		font-size: 22rpx;
		color: #fff;
		background: #ccc;
		/* 浅灰色圆形背景 */
		width: 30rpx;
		height: 30rpx;
		line-height: 28rpx;
		text-align: center;
		border-radius: 50%;
		/* 圆形 */
		margin-right: 12rpx;
		/* 与输入框的间距 */
		flex-shrink: 0;
		/* 防止被挤压 */
	}

	/* ✨新增：筛选栏“重置”胶囊按钮样式 */
	.sort-item.reset-btn {
		color: #888;
		/* 辅助色文字 */
		background: #f0f0f0;
		/* 浅灰底色 */
		padding: 6rpx 20rpx;
		/* 增加点击区域 */
		border-radius: 30rpx;
		/* 胶囊形状 */
		font-size: 22rpx;
		margin-left: 10rpx;
		font-weight: normal;
		/* 区别于选中的加粗状态 */
	}

	/* 按压效果 */
	.reset-btn:active {
		background: #e5e5e5;
	}
</style>