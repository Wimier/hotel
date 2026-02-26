package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.HotelRoom;

/**
 * 房间信息Service接口
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
public interface IHotelRoomService 
{
    /**
     * 查询房间信息
     * 
     * @param id 房间信息主键
     * @return 房间信息
     */
    public HotelRoom selectHotelRoomById(Long id);

    /**
     * 查询房间信息列表
     * 
     * @param hotelRoom 房间信息
     * @return 房间信息集合
     */
    public List<HotelRoom> selectHotelRoomList(HotelRoom hotelRoom);

    /**
     * 新增房间信息
     * 
     * @param hotelRoom 房间信息
     * @return 结果
     */
    public int insertHotelRoom(HotelRoom hotelRoom);

    /**
     * 修改房间信息
     * 
     * @param hotelRoom 房间信息
     * @return 结果
     */
    public int updateHotelRoom(HotelRoom hotelRoom);

    /**
     * 批量删除房间信息
     * 
     * @param ids 需要删除的房间信息主键集合
     * @return 结果
     */
    public int deleteHotelRoomByIds(Long[] ids);

    /**
     * 删除房间信息信息
     * 
     * @param id 房间信息主键
     * @return 结果
     */
    public int deleteHotelRoomById(Long id);
}
