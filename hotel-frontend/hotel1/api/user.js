import http from '@/utils/request.js'

// 查询个人信息
export function getUserProfile() {
  return http.get('/system/user/profile')
}

// 修改个人信息（昵称等）
export function updateUserProfile(data) {
  return http.put('/system/user/profile', data)
}

// 修改密码
export function updateUserPwd(oldPassword, newPassword) {
  // 在标准的 uni-app/RuoYi 请求封装中：
  // put(url, data, config)
  // 第二个参数 data 会自动转为 JSON 放在请求体(Body)中
  return http.put('/system/user/profile/updatePwd', {
    oldPassword: oldPassword,
    newPassword: newPassword
  })
}