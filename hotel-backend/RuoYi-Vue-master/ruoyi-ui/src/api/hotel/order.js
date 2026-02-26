import request from '@/utils/request'

// 查询酒店订单列表
export function listOrder(query) {
  return request({
    url: '/hotel/order/list',
    method: 'get',
    params: query
  })
}

// 查询酒店订单详细
export function getOrder(id) {
  return request({
    url: '/hotel/order/' + id,
    method: 'get'
  })
}

// 新增酒店订单
export function addOrder(data) {
  return request({
    url: '/hotel/order',
    method: 'post',
    data: data
  })
}

// 修改酒店订单
export function updateOrder(data) {
  return request({
    url: '/hotel/order',
    method: 'put',
    data: data
  })
}

// 删除酒店订单
export function delOrder(id) {
  return request({
    url: '/hotel/order/' + id,
    method: 'delete'
  })
}
