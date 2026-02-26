import request from '@/utils/request'

// 查询酒店评价列表
export function listComment(query) {
  return request({
    url: '/hotel/comment/list',
    method: 'get',
    params: query
  })
}

// 查询酒店评价详细
export function getComment(id) {
  return request({
    url: '/hotel/comment/' + id,
    method: 'get'
  })
}

// 新增酒店评价
export function addComment(data) {
  return request({
    url: '/hotel/comment',
    method: 'post',
    data: data
  })
}

// 修改酒店评价
export function updateComment(data) {
  return request({
    url: '/hotel/comment',
    method: 'put',
    data: data
  })
}

// 删除酒店评价
export function delComment(id) {
  return request({
    url: '/hotel/comment/' + id,
    method: 'delete'
  })
}
