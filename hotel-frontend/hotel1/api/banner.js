import http from '@/utils/request.js'

// 查询首页轮播图列表
export function listBanner(query) {
  return http.get('/system/banner/list', query)
}