<template>
	<view class="comment-list-container">
		<view class="score-header">
			<view class="left">
				<text class="big-score">{{ averageScore }}</text>
				<view class="score-detail">
					<text class="score-label">综合评分</text>
					<uni-rate :value="averageScore" readonly :size="16" />
				</view>
			</view>
			<view class="right">
				<text class="total-count">共 {{ total }} 条评价</text>
			</view>
		</view>

		<scroll-view scroll-y class="comment-scroll" @scrolltolower="loadMore">
			<view v-if="commentList.length === 0" class="empty-box">暂无评价信息</view>

			<view class="comment-item" v-for="(item, index) in commentList" :key="item.id">
				<view class="u-info">
					<image :src="item.isAnonymous === 1 ? '/static/anonymous-avatar.png' : item.userAvatar"
						class="avatar" mode="aspectFill"></image>
					<view class="u-right">
						<view class="u-top">
							<text class="u-name">{{ item.isAnonymous === 1 ? '匿***户' : (item.userName || '手机用户') }}</text>
							<text class="c-date">{{ formatDate(item.createTime) }}</text>
						</view>
						<uni-rate :value="item.score" readonly :size="14" />
					</view>
				</view>

				<view class="c-content">
					<text>{{ item.content }}</text>
				</view>

				<view class="c-images" v-if="item.imageList && item.imageList.length > 0">
					<image v-for="(img, imgIdx) in item.imageList" :key="imgIdx" :src="img" mode="aspectFill"
						@click="previewImg(index, imgIdx)"></image>
				</view>

				<text class="room-type-tag">房型：{{ item.roomTypeName || '酒店客房' }}</text>

				<view class="reply-box" v-if="item.replyContent">
					<text class="reply-title">商家回复：</text>
					<text class="reply-text">{{ item.replyContent }}</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { listComment } from '@/api/comment.js'

	export default {
		data() {
			return {
				hotelId: null,
				commentList: [],
				total: 0,
				averageScore: 5.0,
				queryParams: {
					pageNum: 1,
					pageSize: 10
				},
				baseUrl: 'http://localhost:8080'
			};
		},
		onLoad(options) {
			if (options.hotelId) {
				this.hotelId = options.hotelId;
				this.fetchComments();
			}
		},
		methods: {
			async fetchComments() {
				const params = {
					...this.queryParams,
					hotelId: this.hotelId,
					status: 1 // 仅显示审核通过的评价
				};
				const res = await listComment(params);
				if (res.code === 200) {
					const rows = res.rows.map(item => {
						// 1. 处理评价图片路径
						let imgs = [];
						if (item.images) {
							imgs = item.images.split(',').map(url => this.getFullUrl(url));
						}
						
						// 2. ✨ 处理系统用户头像路径（仅在非匿名时处理）
						let realAvatar = item.userAvatar ? this.getFullUrl(item.userAvatar) : '/static/anonymous-avatar.png';

						return {
							...item,
							imageList: imgs,
							userAvatar: realAvatar
						};
					});

					this.commentList = this.queryParams.pageNum === 1 ? rows : this.commentList.concat(rows);
					this.total = res.total;

					// ✨ 核心逻辑：从后端 Controller 的 msg 字段中读取酒店总分
					if (this.queryParams.pageNum === 1 && res.msg) {
						this.averageScore = parseFloat(res.msg).toFixed(1);
					}
				}
			},
			// 通用的图片/头像 URL 补全方法
			getFullUrl(url) {
				if (!url) return '';
				return url.startsWith('http') ? url : this.baseUrl + url;
			},
			formatDate(date) {
				return date ? date.substring(0, 10) : '';
			},
			previewImg(commentIdx, imgIdx) {
				uni.previewImage({
					current: this.commentList[commentIdx].imageList[imgIdx],
					urls: this.commentList[commentIdx].imageList
				});
			},
			loadMore() {
				if (this.commentList.length < this.total) {
					this.queryParams.pageNum++;
					this.fetchComments();
				}
			}
		}
	}
</script>

<style scoped>
	.comment-list-container {
		background: #f8f9fb;
		min-height: 100vh;
	}

	.score-header {
		background: #fff;
		padding: 40rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 1rpx solid #eee;
	}

	.left {
		display: flex;
		align-items: center;
	}

	.big-score {
		font-size: 60rpx;
		font-weight: bold;
		color: #ff9900;
		margin-right: 20rpx;
	}

	.score-label {
		font-size: 24rpx;
		color: #999;
		display: block;
	}

	.total-count {
		font-size: 24rpx;
		color: #999;
	}

	.comment-scroll {
		height: calc(100vh - 160rpx);
		padding: 20rpx;
		box-sizing: border-box;
	}

	.comment-item {
		background: #fff;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.u-info {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background: #f0f0f0;
	}

	.u-right {
		margin-left: 20rpx;
		flex: 1;
	}

	.u-top {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 4rpx;
	}

	.u-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
	}

	.c-date {
		font-size: 22rpx;
		color: #bbb;
	}

	.c-content {
		font-size: 28rpx;
		color: #333;
		line-height: 1.6;
		margin: 20rpx 0;
	}

	.c-images {
		display: flex;
		flex-wrap: wrap;
		gap: 10rpx;
		margin-bottom: 20rpx;
	}

	.c-images image {
		width: 160rpx;
		height: 160rpx;
		border-radius: 8rpx;
	}

	.room-type-tag {
		font-size: 22rpx;
		color: #999;
		background: #f5f5f5;
		padding: 4rpx 16rpx;
		border-radius: 20rpx;
	}

	.reply-box {
		margin-top: 24rpx;
		background: #f9f9f9;
		padding: 20rpx;
		border-radius: 12rpx;
		border-left: 6rpx solid #0086F6;
	}

	.reply-title {
		font-size: 24rpx;
		font-weight: bold;
		color: #0086F6;
		display: block;
		margin-bottom: 8rpx;
	}

	.reply-text {
		font-size: 24rpx;
		color: #666;
		line-height: 1.5;
	}

	.empty-box {
		text-align: center;
		padding-top: 100rpx;
		color: #999;
		font-size: 28rpx;
	}
</style>