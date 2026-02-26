package com.ruoyi.hotel.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.hotel.mapper.HotelRoomMapper;
import com.ruoyi.hotel.domain.HotelRoom;
import com.ruoyi.hotel.service.IHotelRoomService;

/**
 * 房间信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
@Service
public class HotelRoomServiceImpl implements IHotelRoomService 
{
    @Autowired
    private HotelRoomMapper hotelRoomMapper;

    /**
     * 查询房间信息
     * 
     * @param id 房间信息主键
     * @return 房间信息
     */
    @Override
    public HotelRoom selectHotelRoomById(Long id)
    {
        return hotelRoomMapper.selectHotelRoomById(id);
    }

    /**
     * 查询房间信息列表
     * 
     * @param hotelRoom 房间信息
     * @return 房间信息
     */
    @Override
    public List<HotelRoom> selectHotelRoomList(HotelRoom hotelRoom)
    {
        return hotelRoomMapper.selectHotelRoomList(hotelRoom);
    }

    /**
     * 新增房间信息
     * 
     * @param hotelRoom 房间信息
     * @return 结果
     */
    @Override
    public int insertHotelRoom(HotelRoom hotelRoom)
    {
        return hotelRoomMapper.insertHotelRoom(hotelRoom);
    }

    /**
     * 修改房间信息
     * 
     * @param hotelRoom 房间信息
     * @return 结果
     */
    @Override
    public int updateHotelRoom(HotelRoom hotelRoom)
    {
        return hotelRoomMapper.updateHotelRoom(hotelRoom);
    }

    /**
     * 批量删除房间信息
     * 
     * @param ids 需要删除的房间信息主键
     * @return 结果
     */
    @Override
    public int deleteHotelRoomByIds(Long[] ids)
    {
        return hotelRoomMapper.deleteHotelRoomByIds(ids);
    }

    /**
     * 删除房间信息信息
     * 
     * @param id 房间信息主键
     * @return 结果
     */
    @Override
    public int deleteHotelRoomById(Long id)
    {
        return hotelRoomMapper.deleteHotelRoomById(id);
    }
}
