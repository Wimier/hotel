package com.ruoyi.hotel.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.HotelRoomTypeMapper;
import com.ruoyi.hotel.domain.HotelRoomType;
import com.ruoyi.hotel.service.IHotelRoomTypeService;

/**
 * 房型Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
@Service
public class HotelRoomTypeServiceImpl implements IHotelRoomTypeService 
{
    @Autowired
    private HotelRoomTypeMapper hotelRoomTypeMapper;

    /**
     * 查询房型
     * 
     * @param id 房型主键
     * @return 房型
     */
    @Override
    public HotelRoomType selectHotelRoomTypeById(Long id)
    {
        return hotelRoomTypeMapper.selectHotelRoomTypeById(id);
    }

    /**
     * 查询房型列表
     * 
     * @param hotelRoomType 房型
     * @return 房型
     */
    @Override
    public List<HotelRoomType> selectHotelRoomTypeList(HotelRoomType hotelRoomType)
    {
        return hotelRoomTypeMapper.selectHotelRoomTypeList(hotelRoomType);
    }

    /**
     * 新增房型
     * 
     * @param hotelRoomType 房型
     * @return 结果
     */
    @Override
    public int insertHotelRoomType(HotelRoomType hotelRoomType)
    {
        hotelRoomType.setCreateTime(DateUtils.getNowDate());
        return hotelRoomTypeMapper.insertHotelRoomType(hotelRoomType);
    }

    /**
     * 修改房型
     * 
     * @param hotelRoomType 房型
     * @return 结果
     */
    @Override
    public int updateHotelRoomType(HotelRoomType hotelRoomType)
    {
        return hotelRoomTypeMapper.updateHotelRoomType(hotelRoomType);
    }

    /**
     * 批量删除房型
     * 
     * @param ids 需要删除的房型主键
     * @return 结果
     */
    @Override
    public int deleteHotelRoomTypeByIds(Long[] ids)
    {
        return hotelRoomTypeMapper.deleteHotelRoomTypeByIds(ids);
    }

    /**
     * 删除房型信息
     * 
     * @param id 房型主键
     * @return 结果
     */
    @Override
    public int deleteHotelRoomTypeById(Long id)
    {
        return hotelRoomTypeMapper.deleteHotelRoomTypeById(id);
    }
}
