import request from '@/utils/request'

// 查询房型列表
export function listType(query) {
  return request({
    url: '/hotel/type/list',
    method: 'get',
    params: query
  })
}

// 查询房型详细
export function getType(id) {
  return request({
    url: '/hotel/type/' + id,
    method: 'get'
  })
}

// 新增房型
export function addType(data) {
  return request({
    url: '/hotel/type',
    method: 'post',
    data: data
  })
}

// 修改房型
export function updateType(data) {
  return request({
    url: '/hotel/type',
    method: 'put',
    data: data
  })
}

// 删除房型
export function delType(id) {
  return request({
    url: '/hotel/type/' + id,
    method: 'delete'
  })
}
