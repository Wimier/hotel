import apiConfig from '@/config.js'
const baseUrl = process.env.NODE_ENV === "development" ? apiConfig.dev.baseUrl : apiConfig.prod.baseUrl

const request = (options = {}) => {
    return new Promise((resolve, reject) => {
        // 1. 组装请求头，适配若依后端
        let header = options.header || {"Content-Type": "application/json"};
        const token = uni.getStorageSync('token'); //
        if (token) {
            header['Authorization'] = 'Bearer ' + token; // 若依灵魂：Bearer加空格
        }

        // 2. 发起原生请求
        uni.request({
            url: baseUrl + (options.url || ''),
            method: options.method || 'GET',
            data: options.data || {},
            header: header,
            success: (res) => {
                // 若依后端通常返回 res.data，其中包含 code, msg, rows 等
                resolve(res.data); 
            },
            fail: (err) => {
                reject(err); 
            }
        });
    });
}

const get = (url, data, options = {}) => {
    options.method = 'GET'; options.data = data; options.url = url;
    return request(options);
}

const post = (url, data, options = {}) => {
    options.method = 'POST'; options.data = data; options.url = url;
    return request(options);
}

// ✨ 新增：支持 PUT 请求，用于修改订单状态
const put = (url, data, options = {}) => {
    options.method = 'PUT'; options.data = data; options.url = url;
    return request(options);
}

// ✨ 注意：这里导出的是一个对象
export default { request, get, post, put }