<template>
	<view class="home-container">
		<swiper class="banner-swiper" circular autoplay :interval="3000" indicator-dots
			indicator-active-color="#ffffff">
			<swiper-item v-for="(item, index) in bannerList" :key="index" @click="goHotelDetail(item.hotelId)">
				<image class="banner-img" :src="item.imgUrl" mode="aspectFill"></image>
			</swiper-item>
		</swiper>

		<view class="search-card">
			<view class="row city-row" @click="chooseCity">
				<text class="city-name">{{ searchForm.city }}</text>
				<view class="location-btn" @click.stop="getLocation">
					<text class="icon-location">📍</text>
					<text class="loc-text">我的位置</text>
				</view>
			</view>

			<view class="row date-row" @click="openCalendar">
				<view class="date-item">
					<text class="label">入住</text>
					<text class="date-val">{{ inDateDisplay }}</text>
				</view>
				<view class="days-badge">共 {{ searchForm.days }} 晚</view>
				<view class="date-item right">
					<text class="label">离店</text>
					<text class="date-val">{{ outDateDisplay }}</text>
				</view>
			</view>

			<view class="row keyword-row">
				<input class="keyword-input" v-model="searchForm.keyword" placeholder="位置/酒店名，如：舟山海景房"
					placeholder-style="color:#ccc;" />
			</view>

			<view class="tags-row">
				<view class="tag-item" :class="{ active: searchForm.filters.includes(tag.value) }"
					v-for="tag in quickTags" :key="tag.value" @click="toggleFilter(tag.value)">
					{{ tag.label }}
				</view>
			</view>

			<button class="search-btn" @click="doSearch">查找酒店</button>
		</view>

		<view class="recommend-section">
			<view class="section-title">猜你喜欢</view>
			<view class="waterfall-list">
				<view v-if="recommendList.length === 0"
					style="text-align: center; width: 100%; color: #999; padding: 40rpx 0;">
					正在为您挑选酒店...
				</view>

				<view class="hotel-card" v-for="hotel in recommendList" :key="hotel.id"
					@click="goHotelDetail(hotel.id)">
					<image class="cover-img" :src="getImageUrl(hotel.bannerUrl)" mode="aspectFill"></image>
					<view class="hotel-info">
						<view class="hotel-name">{{ hotel.nameCn }}</view>
						<view class="score-row">
							<text class="score">{{ hotel.score || '4.5' }}分</text>
							<text class="star-text">{{ hotel.starRating }}星级</text>
						</view>
						<view class="tags-box">
							<text class="mini-tag" v-if="hotel.park === '免费'">免费停车</text>
							<text class="mini-tag" v-if="hotel.hasWifi === 1">WiFi</text>
						</view>
						<view class="price-row">
							<text class="price-symbol">¥</text>
							<text class="price-num">{{ hotel.price }}</text>
							<text class="price-unit">起</text>
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
		listBanner
	} from '@/api/banner.js'
	import {
		listHotel
	} from '@/api/info.js';
	// 引入腾讯地图SDK
	const QQMapWX = require('@/libs/qqmap-wx-jssdk.js');
	// 实例化地图对象
	const qqmapsdk = new QQMapWX({
		key: 'GNVBZ-Q5I3M-PGP64-637TW-NNHYH-NNFXX'
	});
	export default {
		data() {
			const today = new Date();
			const tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);

			// 定义生成标准 YYYY-MM-DD 的辅助函数
			const formatYYYYMMDD = (d) =>
				`${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
			return {
				bannerList: [],
				// 默认基础地址，需与后台上传路径对应
				baseUrl: 'http://localhost:8080',
				searchForm: {
					city: '上海',
					checkInDate: formatYYYYMMDD(today),
					checkOutDate: formatYYYYMMDD(tomorrow),
					realCheckIn: formatYYYYMMDD(today),
					realCheckOut: formatYYYYMMDD(tomorrow),
					days: 1,
					keyword: '',
					filters: [] // 这里存放如 ['park', 'wifi'] 这种简单的字符串数组
				},
				quickTags: [{
						label: '五星/豪华',
						value: 'star5'
					},
					{
						label: '免费停车',
						value: 'park'
					},
					{
						label: '免费WiFi',
						value: 'wifi'
					}
				],
				recommendList: [],
				pageParams: {
					pageNum: 1,
					pageSize: 10
				},
				status: 'more'
			};
		},
		onLoad() {
			this.loadRecommendHotels();
			this.getBanners();
		},
		onShow() {
			// 监听城市选择事件
			uni.$once('updateCity', (city) => {
				if (city) {
					this.searchForm.city = city;
					// 选完城市后，自动刷新一下首页的推荐酒店
					this.pageParams.pageNum = 1;
					this.loadRecommendHotels();
				}
			});

			// 确保日历等组件状态正常
			this.showCalendar = false;
		},
		computed: {
			// ✨ 新增：用于首页显示的入住日期 (格式：02月25日)
			inDateDisplay() {
				if (!this.searchForm.checkInDate) return '';
				const [y, m, d] = this.searchForm.checkInDate.split('-');
				return `${m}月${d}日`;
			},
			// ✨ 新增：用于首页显示的离店日期
			outDateDisplay() {
				if (!this.searchForm.checkOutDate) return '';
				const [y, m, d] = this.searchForm.checkOutDate.split('-');
				return `${m}月${d}日`;
			}
		},
		methods: {
			/** 获取轮播图数据 */
			async getBanners() {
				const res = await listBanner({
					status: 1
				}); // 只查询上线的广告
				if (res.code === 200) {
					// 格式化图片地址
					this.bannerList = res.rows.map(item => {
						return {
							...item,
							// 如果是相对路径则拼接 baseUrl
							imgUrl: item.imgUrl.startsWith('http') ? item.imgUrl : this.baseUrl + item.imgUrl
						}
					});
				}
			},
			// 修复图片路径显示问题
			getImageUrl(url) {
				if (!url) return '/static/default-hotel.png';
				if (url.startsWith('http')) return url;
				return 'http://localhost:8080' + url;
			},

			// 切换标签选中状态
			toggleFilter(val) {
				const idx = this.searchForm.filters.indexOf(val);
				if (idx > -1) {
					this.searchForm.filters.splice(idx, 1);
				} else {
					this.searchForm.filters.push(val);
				}
			},

			// 唤起日历
			openCalendar() {
				this.$refs.calendar.open();
			},

			onCalendarConfirm(e) {
				const dateRange = Array.isArray(e) ? e : e.range.data;
				if (!dateRange || dateRange.length < 2) return;

				// 存储组件返回的标准 YYYY-MM-DD 字符串
				const startDate = dateRange[0];
				const endDate = dateRange[dateRange.length - 1];

				this.searchForm.checkInDate = startDate;
				this.searchForm.checkOutDate = endDate;

				// 计算天数 (replace 为兼容 iOS)
				const d1 = new Date(startDate.replace(/-/g, '/'));
				const d2 = new Date(endDate.replace(/-/g, '/'));
				this.searchForm.days = Math.round((d2 - d1) / (1000 * 60 * 60 * 24));
			},

			// ✨ 执行搜索跳转：Params 里的日期已是标准 YYYY-MM-DD
			doSearch() {
				const params = encodeURIComponent(JSON.stringify(this.searchForm));
				uni.navigateTo({
					url: `/pages/hotel/list?params=${params}`
				});
			},
			// goHotelDetail(id) {
			// 	if (!id) return;
			// 	uni.navigateTo({
			// 		url: `/pages/detail/detail?id=${id}`
			// 	});
			// },
			goHotelDetail(hotelId) {
				if (!hotelId) return;

				// 1. 获取今天和明天的日期
				const now = new Date();
				const tomorrow = new Date(now.getTime() + 24 * 60 * 60 * 1000);

				const formatDate = (d) =>
					`${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;

				const checkIn = formatDate(now);
				const checkOut = formatDate(tomorrow);

				// 2. 跳转并传参
				uni.navigateTo({
					url: `/pages/detail/detail?id=${hotelId}&checkInDate=${checkIn}&checkOutDate=${checkOut}&days=1`
				});
			},

			// 加载首页推荐
			async loadRecommendHotels() {
				try {
					const res = await listHotel({
						pageNum: this.pageParams.pageNum,
						pageSize: this.pageParams.pageSize,
						status: 'PASS'
					});
					if (res.code === 200) {
						this.recommendList = res.rows || [];
					}
				} catch (error) {
					console.error('推荐列表加载失败', error);
				}
			},

			getLocation() {
				uni.showLoading({
					title: '定位中...'
				});

				uni.getLocation({
					type: 'wgs84',
					success: (res) => {
						// 使用腾讯地图 SDK 逆地址解析
						qqmapsdk.reverseGeocoder({
							location: {
								latitude: res.latitude,
								longitude: res.longitude
							},
							success: (re) => {
								uni.hideLoading();
								// 获取城市名并去除“市”字，匹配后端搜索习惯
								let city = re.result.address_component.city;
								this.searchForm.city = city.replace('市', '');

								uni.showToast({
									title: `当前定位：${this.searchForm.city}`,
									icon: 'none'
								});
							},
							fail: (error) => {
								uni.hideLoading();
								console.error('地图解析失败', error);
								uni.showToast({
									title: '地址解析失败',
									icon: 'none'
								});
							}
						});
					},
					fail: (err) => {
						uni.hideLoading();
						// 如果用户拒绝授权，引导用户去开启
						uni.showModal({
							title: '提示',
							content: '请开启定位权限以获取更精准的酒店服务',
							success: (res) => {
								if (res.confirm) {
									uni.openSetting();
								}
							}
						});
					}
				});
			},
			chooseCity() {
				uni.navigateTo({
					url: '/pages/city/city'
				});
			}
		}
	};
</script>

<style scoped>
	.home-container {
		min-height: 100vh;
		background-color: #f5f7fa;
		padding-bottom: 40rpx;
	}

	.banner-swiper {
		height: 420rpx;
		width: 100%;
	}

	.banner-img {
		width: 100%;
		height: 100%;
	}

	/* 搜索卡片 */
	.search-card {
		margin: -60rpx 30rpx 0;
		background: #ffffff;
		border-radius: 24rpx;
		padding: 30rpx;
		box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
		position: relative;
		z-index: 10;
	}

	.row {
		border-bottom: 1rpx solid #f2f2f2;
		padding: 24rpx 0;
	}

	.row:last-of-type {
		border-bottom: none;
	}

	.city-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.city-name {
		font-size: 42rpx;
		font-weight: bold;
		color: #333;
	}

	.location-btn {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.loc-text {
		font-size: 22rpx;
		color: #0086F6;
		margin-top: 4rpx;
	}

	.date-row {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.date-val {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-top: 8rpx;
		display: block;
	}

	.label {
		font-size: 24rpx;
		color: #999;
	}

	.days-badge {
		font-size: 24rpx;
		color: #0086F6;
		border: 2rpx solid #E1F1FF;
		background: #F0F8FF;
		border-radius: 30rpx;
		padding: 4rpx 20rpx;
	}

	.date-item.right {
		text-align: right;
	}

	.keyword-input {
		font-size: 30rpx;
		height: 60rpx;
	}

	/* 筛选标签 */
	.tags-row {
		display: flex;
		flex-wrap: wrap;
		padding: 20rpx 0;
		gap: 16rpx;
	}

	.tag-item {
		font-size: 26rpx;
		color: #666;
		background: #f5f5f7;
		padding: 12rpx 24rpx;
		border-radius: 12rpx;
		transition: all 0.2s;
	}

	.tag-item.active {
		background: #E1F1FF;
		color: #0086F6;
		font-weight: bold;
	}

	.search-btn {
		background: linear-gradient(90deg, #00A1FF, #0076F6);
		color: #ffffff;
		font-weight: bold;
		border-radius: 50rpx;
		margin-top: 30rpx;
		height: 90rpx;
		line-height: 90rpx;
		box-shadow: 0 8rpx 20rpx rgba(0, 118, 246, 0.3);
	}

	/* 推荐列表 */
	.recommend-section {
		padding: 40rpx 30rpx;
	}

	.section-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
	}

	.waterfall-list {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
	}

	.hotel-card {
		width: 48%;
		background: #fff;
		border-radius: 20rpx;
		overflow: hidden;
		margin-bottom: 24rpx;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
	}

	.cover-img {
		width: 100%;
		height: 260rpx;
	}

	.hotel-info {
		padding: 16rpx;
	}

	.hotel-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.score-row {
		display: flex;
		align-items: center;
		margin: 10rpx 0;
	}

	.score {
		font-size: 26rpx;
		color: #0086F6;
		font-weight: bold;
		margin-right: 12rpx;
	}

	.star-text {
		font-size: 22rpx;
		color: #999;
	}

	.tags-box {
		display: flex;
		gap: 8rpx;
		margin-bottom: 12rpx;
	}

	.mini-tag {
		font-size: 20rpx;
		color: #FF6913;
		border: 1rpx solid #FF6913;
		border-radius: 4rpx;
		padding: 2rpx 8rpx;
	}

	.price-row {
		display: flex;
		align-items: baseline;
	}

	.price-symbol {
		font-size: 24rpx;
		color: #FF4949;
	}

	.price-num {
		font-size: 30rpx;
		font-weight: bold;
		color: #FF4949;
	}

	.price-unit {
		font-size: 20rpx;
		color: #999;
		margin-left: 4rpx;
	}
</style>