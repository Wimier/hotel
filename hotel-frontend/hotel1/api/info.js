// 引入我们在小程序里封装好的核心请求工具
import http from '@/utils/request.js'

// 查询酒店列表 (小程序端调用)
export function listHotel(query) {
  // 调用封装好的 get 方法
  return http.get('/hotel/info/list', query)
}

// 查询酒店详细
export function getHotel(id) {
  return http.get('/hotel/info/' + id)
}

// 注意：小程序端通常只需要查询功能(list和get)。
// 新增、修改、删除通常是在 Vue 管理后台做的，但如果你需要，也可以像下面这样写：
/*
export function addHotel(data) {
  return http.post('/hotel/info', data)
}
*/