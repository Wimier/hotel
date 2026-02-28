import http from '@/utils/request.js'

// 查询评价列表（用于酒店详情页展示）
export function listComment(query) {
  return http.get('/hotel/comment/list', query)
}

//查询评价详细
export function getComment(id) {
  return http.get('/hotel/comment/' + id)
}

// 新增评价（对应你刚刚写的发表评价页面）
export function addComment(data) {
  return http.post('/hotel/comment', data)
}

// 修改评价
export function updateComment(data) {
  return http.put('/hotel/comment', data)
}

// 删除评价
export function delComment(id) {
  return http.delete('/hotel/comment/' + id)
}