//
package com.ruoyi.hotel.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.HotelInfoMapper;
import com.ruoyi.hotel.domain.HotelInfo;
import com.ruoyi.hotel.service.IHotelInfoService;

@Service
public class HotelInfoServiceImpl implements IHotelInfoService
{
    @Autowired
    private HotelInfoMapper hotelInfoMapper;

    @Override
    public HotelInfo selectHotelInfoById(Long id)
    {
        return hotelInfoMapper.selectHotelInfoById(id);
    }

    /**
     * 查询酒店信息列表
     */
    //
    //
//
    @Override
    public List<HotelInfo> selectHotelInfoList(HotelInfo hotelInfo) {
        try {
            Long currentUserId = SecurityUtils.getUserId();

            if (SecurityUtils.isAdmin(currentUserId)) {
                hotelInfo.setMerchantId(null); // 管理员必须清空，防干扰
                return hotelInfoMapper.selectHotelInfoList(hotelInfo);
            }

            if (SecurityUtils.hasRole("hotel_merchant")) {
                // 是商户：强制使用当前登录 ID，覆盖掉前端可能传错的任何值
                hotelInfo.setMerchantId(currentUserId);
            } else {
                // ✨ 重点：普通用户，不管前端传了什么（哪怕是拦截器带的101），一律清空！
                hotelInfo.setMerchantId(null);
            }
        } catch (Exception e) {
            // 未登录状态，也必须清空
            hotelInfo.setMerchantId(null);
        }
        return hotelInfoMapper.selectHotelInfoList(hotelInfo);
    }

    /**
     * 新增酒店信息
     */
    @Override
    public int insertHotelInfo(HotelInfo hotelInfo)
    {
        // ✨ 核心修复：新增时自动绑定当前商户的 UserID 到 merchant_id 字段
        hotelInfo.setMerchantId(SecurityUtils.getUserId());
        hotelInfo.setCreateBy(SecurityUtils.getUsername());
        hotelInfo.setCreateTime(DateUtils.getNowDate());
        return hotelInfoMapper.insertHotelInfo(hotelInfo);
    }

    /**
     * 修改酒店信息
     */
    @Override
    public int updateHotelInfo(HotelInfo hotelInfo)
    {
        hotelInfo.setUpdateBy(SecurityUtils.getUsername());
        hotelInfo.setUpdateTime(DateUtils.getNowDate());
        return hotelInfoMapper.updateHotelInfo(hotelInfo);
    }

    @Override
    public int deleteHotelInfoByIds(Long[] ids)
    {
        return hotelInfoMapper.deleteHotelInfoByIds(ids);
    }

    @Override
    public int deleteHotelInfoById(Long id)
    {
        return hotelInfoMapper.deleteHotelInfoById(id);
    }
}