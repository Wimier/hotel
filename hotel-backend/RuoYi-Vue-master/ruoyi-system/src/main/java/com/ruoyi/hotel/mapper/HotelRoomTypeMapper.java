package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.HotelRoomType;

/**
 * 房型Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
public interface HotelRoomTypeMapper 
{
    /**
     * 查询房型
     * 
     * @param id 房型主键
     * @return 房型
     */
    public HotelRoomType selectHotelRoomTypeById(Long id);

    /**
     * 查询房型列表
     * 
     * @param hotelRoomType 房型
     * @return 房型集合
     */
    public List<HotelRoomType> selectHotelRoomTypeList(HotelRoomType hotelRoomType);

    /**
     * 新增房型
     * 
     * @param hotelRoomType 房型
     * @return 结果
     */
    public int insertHotelRoomType(HotelRoomType hotelRoomType);

    /**
     * 修改房型
     * 
     * @param hotelRoomType 房型
     * @return 结果
     */
    public int updateHotelRoomType(HotelRoomType hotelRoomType);

    /**
     * 删除房型
     * 
     * @param id 房型主键
     * @return 结果
     */
    public int deleteHotelRoomTypeById(Long id);

    /**
     * 批量删除房型
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHotelRoomTypeByIds(Long[] ids);
}
