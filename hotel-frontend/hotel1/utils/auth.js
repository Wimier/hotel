// utils/auth.js
export const checkLogin = () => {
  const token = wx.getStorageSync('token'); // 假设你登录后把token存入了storage
  if (!token) {
    // 如果没有token，强制跳转到登录页
    wx.reLaunch({
      url: '/pages/login/login' // 这里的路径换成你实际的登录页面路径
    });
    return false;
  }
  return true;
};