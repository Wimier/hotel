<template>
	<view class="comment-container">
		<view class="order-info-card">
			<image :src="getImageUrl(orderInfo.hotelImg)" mode="aspectFill" class="hotel-img"></image>
			<view class="order-detail">
				<text class="hotel-name">{{ orderInfo.hotelName || '酒店预订' }}</text>
				<text class="room-type">{{ orderInfo.roomTypeName }}</text>
				<text class="order-date">入住日期：{{ orderInfo.checkInDate }}</text>
			</view>
		</view>

		<view class="section score-section">
			<text class="section-title">总体评分</text>
			<view class="rate-box">
				<uni-rate v-model="form.score" :size="30" margin="10" />
				<text class="score-text">{{ scoreLabels[form.score - 1] }}</text>
			</view>
		</view>

		<view class="section content-section">
			<textarea 
				class="comment-input" 
				v-model="form.content" 
				placeholder="入住体验如何？环境、服务还满意吗？(字数不少于5字)"
				maxlength="500"
			/>
			<text class="word-count">{{ form.content.length }}/500</text>
		</view>

		<view class="section upload-section">
			<view class="section-title">上传图片 (最多4张)</view>
			<view class="image-grid">
				<view class="image-item" v-for="(img, index) in uploadList" :key="index">
					<image :src="img" mode="aspectFill" @click="previewImg(index)"></image>
					<view class="delete-btn" @click="removeImg(index)">×</view>
				</view>
				<view class="upload-btn" v-if="uploadList.length < 4" @click="chooseImage">
					<text class="plus">+</text>
					<text class="txt">添加图片</text>
				</view>
			</view>
		</view>

		<view class="anonymous-row">
			<label class="checkbox-label" @click="form.isAnonymous = form.isAnonymous === 0 ? 1 : 0">
				<checkbox :checked="form.isAnonymous === 1" color="#0086F6" style="transform:scale(0.8)" />
				<text>匿名评价</text>
			</label>
			<text class="hint">勾选后将隐藏您的头像和昵称</text>
		</view>

		<view class="footer">
			<button class="submit-btn" :disabled="!isReady" @click="submitComment">发布评价</button>
		</view>
	</view>
</template>

<script>
	import { addComment } from '@/api/comment.js'
	import { getOrder } from '@/api/order.js'
	

	export default {
		data() {
			return {
				orderId: null,
				orderInfo: {},
				uploadList: [], // 预览图列表
				serverImageList: [], // 上传后的服务器路径列表
				scoreLabels: ['非常差', '差', '一般', '满意', '非常满意'],
				form: {
					orderId: null,
					score: 5,
					content: '',
					images: '',
					isAnonymous: 0
				}
			};
		},
		computed: {
			isReady() {
				return this.form.content.length >= 5 && this.form.score > 0;
			}
		},
		onLoad(options) {
			if (options.orderId) {
				this.orderId = options.orderId;
				this.form.orderId = options.orderId;
				this.fetchOrderInfo();
			}
		},
		methods: {
			// 获取订单信息用于展示
			async fetchOrderInfo() {
				const res = await getOrder(this.orderId);
				if (res.code === 200) {
					this.orderInfo = res.data;
				}
			},
			// 选择图片
			chooseImage() {
				uni.chooseImage({
					count: 4 - this.uploadList.length,
					sizeType: ['compressed'],
					success: (res) => {
						this.uploadImages(res.tempFilePaths);
					}
				});
			},
			// 上传图片到后端
			uploadImages(paths) {
				uni.showLoading({ title: '上传中...' });
				paths.forEach(path => {
					// 假设使用标准的 RuoYi 上传接口
					uni.uploadFile({
						url: 'http://localhost:8080/common/upload', 
						filePath: path,
						name: 'file',
						header: { 'Authorization': 'Bearer ' + uni.getStorageSync('token') },
						success: (uploadRes) => {
							const data = JSON.parse(uploadRes.data);
							if (data.url) {
								this.uploadList.push(path);
								this.serverImageList.push(data.fileName); // 存储服务器相对路径
							}
						}
					});
				});
				uni.hideLoading();
			},
			removeImg(index) {
				this.uploadList.splice(index, 1);
				this.serverImageList.splice(index, 1);
			},
			previewImg(index) {
				uni.previewImage({ current: this.uploadList[index], urls: this.uploadList });
			},
			// ✨ 提交评价逻辑
			async submitComment() {
				uni.showLoading({ title: '正在发布...' });
				try {
					// 将图片数组转为逗号隔开的字符串
					this.form.images = this.serverImageList.join(',');
					
					const res = await addComment(this.form);
					if (res.code === 200) {
						uni.showToast({ title: '评价成功', icon: 'success' });
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					}
				} finally {
					uni.hideLoading();
				}
			},
			getImageUrl(url) {
				if (!url) return '/static/default.png';
				return url.startsWith('http') ? url : 'http://localhost:8080' + url;
			}
		}
	}
</script>

<style scoped>
	.comment-container { min-height: 100vh; background: #f8f9fb; padding: 24rpx; }
	.order-info-card { background: #fff; border-radius: 20rpx; padding: 24rpx; display: flex; margin-bottom: 24rpx; }
	.hotel-img { width: 120rpx; height: 120rpx; border-radius: 12rpx; background: #f0f0f0; }
	.order-detail { margin-left: 20rpx; flex: 1; }
	.hotel-name { font-size: 28rpx; font-weight: bold; color: #333; display: block; }
	.room-type { font-size: 24rpx; color: #666; margin-top: 8rpx; display: block; }
	.order-date { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }

	.section { background: #fff; border-radius: 20rpx; padding: 30rpx; margin-bottom: 24rpx; }
	.section-title { font-size: 28rpx; font-weight: bold; color: #333; margin-bottom: 24rpx; display: block; }
	
	.rate-box { display: flex; align-items: center; }
	.score-text { margin-left: 20rpx; font-size: 28rpx; color: #ff9900; }

	.comment-input { width: 100%; height: 240rpx; font-size: 28rpx; color: #333; line-height: 1.6; }
	.word-count { text-align: right; font-size: 22rpx; color: #ccc; display: block; }

	.image-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15rpx; }
	.image-item, .upload-btn { width: 150rpx; height: 150rpx; border-radius: 12rpx; position: relative; }
	.image-item image { width: 100%; height: 100%; border-radius: 12rpx; }
	.delete-btn { position: absolute; top: -10rpx; right: -10rpx; width: 36rpx; height: 36rpx; background: rgba(0,0,0,0.5); color: #fff; border-radius: 50%; text-align: center; line-height: 32rpx; font-size: 24rpx; }
	
	.upload-btn { border: 2rpx dashed #ddd; display: flex; flex-direction: column; align-items: center; justify-content: center; background: #fafafa; }
	.upload-btn .plus { font-size: 50rpx; color: #999; line-height: 1; }
	.upload-btn .txt { font-size: 22rpx; color: #999; margin-top: 10rpx; }

	.anonymous-row { display: flex; align-items: center; justify-content: space-between; padding: 10rpx 10rpx 40rpx; }
	.checkbox-label { display: flex; align-items: center; font-size: 26rpx; color: #666; }
	.hint { font-size: 22rpx; color: #999; }

	.footer { padding: 40rpx 0; }
	.submit-btn { background: linear-gradient(90deg, #00B4F6, #0086F6); color: #fff; height: 88rpx; line-height: 88rpx; border-radius: 44rpx; font-size: 30rpx; font-weight: bold; }
	.submit-btn[disabled] { background: #ccc; }
</style>