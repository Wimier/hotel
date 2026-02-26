package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.HotelInfo;

/**
 * 酒店信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
public interface HotelInfoMapper 
{
    /**
     * 查询酒店信息
     * 
     * @param id 酒店信息主键
     * @return 酒店信息
     */
    public HotelInfo selectHotelInfoById(Long id);

    /**
     * 查询酒店信息列表
     * 
     * @param hotelInfo 酒店信息
     * @return 酒店信息集合
     */
    public List<HotelInfo> selectHotelInfoList(HotelInfo hotelInfo);

    /**
     * 新增酒店信息
     * 
     * @param hotelInfo 酒店信息
     * @return 结果
     */
    public int insertHotelInfo(HotelInfo hotelInfo);

    /**
     * 修改酒店信息
     * 
     * @param hotelInfo 酒店信息
     * @return 结果
     */
    public int updateHotelInfo(HotelInfo hotelInfo);

    /**
     * 删除酒店信息
     * 
     * @param id 酒店信息主键
     * @return 结果
     */
    public int deleteHotelInfoById(Long id);

    /**
     * 批量删除酒店信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHotelInfoByIds(Long[] ids);
}
