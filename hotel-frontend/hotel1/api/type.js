import http from '@/utils/request.js'

// 查询房型列表
export function listType(query) {
  return http.get('/hotel/type/list', query)
}

// 查询房型详细
export function getType(id) {
  return http.get('/hotel/type/' + id)
}