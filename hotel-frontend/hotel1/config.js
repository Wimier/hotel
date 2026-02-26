const apiConfig = {
	dev: {
		baseUrl: 'http://localhost:8080' // 开发环境：严格指向你的 Spring Boot 后端
	},
	prod: {
		baseUrl: 'http://你的线上IP:8080' // 以后上线用的，现在不用管
	}
}

export default apiConfig;