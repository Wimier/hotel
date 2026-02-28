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

