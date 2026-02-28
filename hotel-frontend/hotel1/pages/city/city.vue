<template>
	<view class="city-container">
		<view class="search-box">
			<view class="search-input">
				<icon type="search" size="14" color="#999" />
				<input type="text" v-model="keyword" placeholder="输入城市名或拼音" placeholder-style="color:#ccc;" />
			</view>
		</view>

		<scroll-view class="city-scroll" scroll-y :scroll-into-view="scrollId" scroll-with-animation>
			<view class="section" id="current">
				<view class="title">当前定位</view>
				<view class="city-tags">
					<view class="tag" @click="selectCity(currentCity)">
						<text class="icon">📍</text> {{ currentCity || '定位中...' }}
					</view>
				</view>
			</view>

			<view class="section" id="hot">
				<view class="title">热门城市</view>
				<view class="city-tags">
					<view class="tag" v-for="city in hotCities" :key="city" @click="selectCity(city)">{{ city }}</view>
				</view>
			</view>

			<view v-for="(group, index) in filteredCities" :key="index" :id="group.initial" class="city-list">
			        <view class="letter-title">{{ group.initial }}</view>
			        <view class="city-item" v-for="city in group.cityNameList" :key="city" @click="selectCity(city)">
			            {{ city }}
			        </view>
			    </view>
		</scroll-view>

		<view class="index-bar">
			<view v-for="letter in indexList" :key="letter" class="index-item" @click="scrollTo(letter)">
				{{ letter }}
			</view>
		</view>
	</view>
</template>

<script>
	import cityData from '@/static/city.json';
	// 引入腾讯地图SDK用于当前定位
	const QQMapWX = require('@/libs/qqmap-wx-jssdk.js');
	const qqmapsdk = new QQMapWX({
		key: 'GNVBZ-Q5I3M-PGP64-637TW-NNHYH-NNFXX'
	});

	export default {
		data() {
			return {
				keyword: '',
				scrollId: '',
				currentCity: '',
				hotCities: ['上海', '北京', '广州', '深圳', '杭州', '宁波', '舟山', '南京', '武汉'],
				indexList: cityData.map(item => item.initial),
				allCities: cityData
			};
		},
		computed: {
			filteredCities() {
				if (!this.keyword) return this.allCities;
				return this.allCities.map(group => ({
					initial: group.initial,
					cityNameList: group.cityNameList.filter(city => city.includes(this.keyword))
				})).filter(group => group.cityNameList.length > 0);
			}
		},
		onLoad() {
			this.getLocation();
		},
		methods: {
			getLocation() {
				uni.getLocation({
					type: 'wgs84',
					success: (res) => {
						qqmapsdk.reverseGeocoder({
							location: {
								latitude: res.latitude,
								longitude: res.longitude
							},
							success: (re) => {
								this.currentCity = re.result.address_component.city.replace('市',
									'');
							}
						});
					}
				});
			},
			scrollTo(letter) {
				this.scrollId = letter === '热' ? 'hot' : letter;
			},
			selectCity(city) {
				if (!city) return;
				// 使用全局事件通信通知首页更新
				uni.$emit('updateCity', city);
				uni.navigateBack();
			}
		}
	};
</script>

<style scoped>
	.city-container {
		height: 100vh;
		background: #fff;
		display: flex;
		flex-direction: column;
	}

	.search-box {
		padding: 20rpx 30rpx;
		background: #fff;
	}

	.search-input {
		background: #f2f2f2;
		height: 70rpx;
		border-radius: 35rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
	}

	.search-input input {
		flex: 1;
		margin-left: 10rpx;
		font-size: 28rpx;
	}

	.city-scroll {
		flex: 1;
		overflow: hidden;
	}

	.section {
		padding: 20rpx 30rpx;
	}

	.title {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 20rpx;
	}

	.city-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
	}

	.tag {
		background: #f5f5f5;
		padding: 12rpx 30rpx;
		border-radius: 8rpx;
		font-size: 28rpx;
		color: #333;
	}

	.letter-title {
		background: #f8f8f8;
		padding: 10rpx 30rpx;
		font-size: 24rpx;
		color: #999;
	}

	.city-item {
		padding: 30rpx;
		border-bottom: 1rpx solid #eee;
		font-size: 30rpx;
		color: #333;
	}

	.index-bar {
		position: fixed;
		right: 10rpx;
		top: 50%;
		transform: translateY(-50%);
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.index-item {
		font-size: 22rpx;
		color: #0086F6;
		padding: 4rpx 10rpx;
		font-weight: bold;
	}
</style>