<template>
	<view class="comment-detail-container">
		<view class="info-card">
			<view class="hotel-header">
				<text class="hotel-name">{{ detail.hotelName || '酒店评价' }}</text>
				<text class="order-sn">订单号：{{ detail.orderSn }}</text>
			</view>
			<text class="room-type">{{ detail.roomTypeName }}</text>
		</view>

		<view class="section">
			<view class="section-title">我的评分</view>
			<view class="rate-box">
				<uni-rate v-model="form.score" :size="30" margin="10" />
				<text class="score-text">{{ scoreLabels[form.score - 1] }}</text>
			</view>

			<view class="content-box">
				<textarea class="comment-input" v-model="form.content" placeholder="修改您的评价内容..." maxlength="500" />
				<text class="word-count">{{ form.content.length }}/500</text>
			</view>
		</view>

		<view class="section">
			<view class="section-title">评价图片</view>
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
		</view>

		<view class="reply-section" v-if="detail.replyContent">
			<view class="reply-title">商家回复：</view>
			<view class="reply-content">{{ detail.replyContent }}</view>
			<view class="reply-date">回复时间：{{ detail.replyTime }}</view>
		</view>

		<view class="footer">
			<button class="submit-btn" @click="handleUpdate">保存修改</button>
		</view>
	</view>
</template>

<script>
	import {
		getComment,
		updateComment
	} from '@/api/comment.js'

	export default {
		data() {
			return {
				commentId: null,
				detail: {}, // 原始详情
				uploadList: [], // 预览图
				serverImageList: [], // 提交给后端的路径
				scoreLabels: ['非常差', '差', '一般', '满意', '非常满意'],
				form: {
					id: null,
					score: 5,
					content: '',
					images: '',
					isAnonymous: 0
				},
				baseUrl: 'http://localhost:8080'
			};
		},
		onLoad(options) {
			if (options.id) {
				this.commentId = options.id;
				this.fetchDetail();
			}
		},
		methods: {
			// 获取评价详情
			async fetchDetail() {
				uni.showLoading({
					title: '加载中...'
				});
				try {
					const res = await getComment(this.commentId);
					if (res.code === 200) {
						this.detail = res.data;

						this.form.id = res.data.id;
						this.form.score = res.data.score;
						this.form.content = res.data.content || '';
						this.form.isAnonymous = res.data.isAnonymous;

						// 处理图片
						if (res.data.images) {
							this.serverImageList = res.data.images.split(',');
							this.uploadList = this.serverImageList.map(path =>
								path.startsWith('http') ? path : this.baseUrl + path
							);
						}
					}
				} finally {
					uni.hideLoading();
				}
			},
			// 提交
			async handleUpdate() {
				if (this.form.content.length < 5) {
					return uni.showToast({
						title: '评价内容不能少于5个字',
						icon: 'none'
					});
				}

				uni.showLoading({
					title: '保存中...'
				});
				try {
					this.form.images = this.serverImageList.join(',');
					const res = await updateComment(this.form);
					if (res.code === 200) {
						uni.showToast({
							title: '修改成功'
						});
						setTimeout(() => uni.navigateBack(), 1500);
					}
				} finally {
					uni.hideLoading();
				}
			},
			// 图片处理
			chooseImage() {
				uni.chooseImage({
					count: 4 - this.uploadList.length,
					success: (res) => {
						res.tempFilePaths.forEach(path => {
							uni.uploadFile({
								url: this.baseUrl + '/common/upload',
								filePath: path,
								name: 'file',
								header: {
									'Authorization': 'Bearer ' + uni.getStorageSync('token')
								},
								success: (uploadRes) => {
									const data = JSON.parse(uploadRes.data);
									this.uploadList.push(path);
									this.serverImageList.push(data.fileName);
								}
							});
						});
					}
				});
			},
			removeImg(index) {
				this.uploadList.splice(index, 1);
				this.serverImageList.splice(index, 1);
			},
			previewImg(index) {
				uni.previewImage({
					current: this.uploadList[index],
					urls: this.uploadList
				});
			}
		}
	}
</script>

<style scoped>
	.comment-detail-container {
		min-height: 100vh;
		background: #f8f9fb;
		padding: 24rpx;
		padding-bottom: 140rpx;
	}

	.info-card {
		background: #fff;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 24rpx;
	}

	.hotel-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.hotel-name {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
	}

	.order-sn {
		font-size: 22rpx;
		color: #999;
	}

	.room-type {
		font-size: 24rpx;
		color: #666;
	}

	.section {
		background: #fff;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 24rpx;
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
		color: #333;
	}

	.rate-box {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;
	}

	.score-text {
		margin-left: 20rpx;
		color: #ff9900;
		font-size: 28rpx;
	}

	.content-box {
		background: #fafafa;
		border-radius: 12rpx;
		padding: 20rpx;
	}

	.comment-input {
		width: 100%;
		height: 200rpx;
		font-size: 28rpx;
	}

	.word-count {
		display: block;
		text-align: right;
		font-size: 22rpx;
		color: #ccc;
	}

	.image-grid {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 15rpx;
	}

	.image-item,
	.upload-btn {
		width: 150rpx;
		height: 150rpx;
		border-radius: 12rpx;
		position: relative;
	}

	.image-item image {
		width: 100%;
		height: 100%;
		border-radius: 12rpx;
	}

	.delete-btn {
		position: absolute;
		top: -10rpx;
		right: -10rpx;
		width: 36rpx;
		height: 36rpx;
		background: rgba(0, 0, 0, 0.5);
		color: #fff;
		border-radius: 50%;
		text-align: center;
		line-height: 32rpx;
	}

	.upload-btn {
		border: 2rpx dashed #ddd;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.reply-section {
		background: #fffbe6;
		border-radius: 20rpx;
		padding: 30rpx;
		border: 1rpx solid #ffe58f;
	}

	.reply-title {
		font-size: 26rpx;
		font-weight: bold;
		color: #856404;
		margin-bottom: 10rpx;
	}

	.reply-content {
		font-size: 26rpx;
		color: #856404;
		line-height: 1.6;
	}

	.reply-date {
		font-size: 22rpx;
		color: #999;
		margin-top: 10rpx;
	}

	.footer {
		position: fixed;
		bottom: 0;
		left: 0;
		width: 100%;
		padding: 20rpx 40rpx;
		background: #fff;
		box-sizing: border-box;
	}

	.submit-btn {
		background: #0086F6;
		color: #fff;
		border-radius: 44rpx;
		font-size: 30rpx;
		height: 88rpx;
		line-height: 88rpx;
	}
</style>