<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
			// 程序启动时检查一次
			const token = uni.getStorageSync('token');
			if (!token) {
				// 如果没有token，跳转到登录页
				// 注意：这里要用定时器或者 nextTick，防止由于页面还没初始化导致的跳转失败
				setTimeout(() => {
					uni.reLaunch({
						url: '/pages/login/login'
					});
				}, 100);
			}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>
