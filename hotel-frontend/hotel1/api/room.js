import http from '@/utils/request.js'

// 查询房型列表
export function listRoom(query) {
  return http.get('/hotel/room/list', query)
}

// 查询房型详细
export function getRoom(id) {
  return http.get('/hotel/room/' + id)
}