// api/hotel.js
import { request } from '@/utils/request.js';

// 获取酒店列表接口
export function getHotelList(data) {
  return request({
    url: '/hotel/list', // 对应你后端的 @GetMapping("/list")
    method: 'GET',
    data: data
  });
}

// 预订下单接口 (我们之前在后端写好的核心接口)
export function submitOrder(data) {
  return request({
    url: '/hotel/order',
    method: 'POST',
    data: data
  });
}