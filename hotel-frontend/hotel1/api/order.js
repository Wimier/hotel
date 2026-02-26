import http from '@/utils/request.js'

// 新增酒店订单
export function addOrder(data) {
  return http.post('/hotel/order', data)
}


// 查询订单列表
export function listOrder(query) {
  return http.get('/hotel/order/list', query)
}

// 获取订单详细信息
export function getOrder(id) {
  return http.get('/hotel/order/' + id)
}

// 修改订单（用于模拟支付或取消
//
export function updateOrder(data) {
  // ✨ 修正：使用 http.put 而不是 http()
  return http.put('/hotel/order', data)
}