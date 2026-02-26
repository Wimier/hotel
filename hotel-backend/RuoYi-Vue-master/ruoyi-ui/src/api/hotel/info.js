import request from '@/utils/request'

// 查询酒店列表
export function listHotel(query) {
  return request({ url: '/hotel/info/list', method: 'get', params: query })
}

// 查询酒店详细
export function getHotel(id) {
  return request({ url: '/hotel/info/' + id, method: 'get' })
}

// 新增酒店
export function addHotel(data) {
  return request({ url: '/hotel/info', method: 'post', data: data })
}

// 修改酒店
export function updateHotel(data) {
  return request({ url: '/hotel/info', method: 'put', data: data })
}

// 删除酒店信息
export function delHotel(ids) {
  return request({
    url: '/hotel/info/' + ids,
    method: 'delete'
  })
}
