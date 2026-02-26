package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.HotelOrder;
import java.util.Date;
import org.apache.ibatis.annotations.Param;
/**
 * 酒店订单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
public interface HotelOrderMapper
{
    /**
     * 查询酒店订单
     * 
     * @param id 酒店订单主键
     * @return 酒店订单
     */
    public HotelOrder selectHotelOrderById(Long id);

    /**
     * 查询酒店订单列表
     * 
     * @param hotelOrder 酒店订单
     * @return 酒店订单集合
     */
    public List<HotelOrder> selectHotelOrderList(HotelOrder hotelOrder);

    /**
     * 新增酒店订单
     * 
     * @param hotelOrder 酒店订单
     * @return 结果
     */
    public int insertHotelOrder(HotelOrder hotelOrder);

    /**
     * 修改酒店订单
     * 
     * @param hotelOrder 酒店订单
     * @return 结果
     */
    public int updateHotelOrder(HotelOrder hotelOrder);

    /**
     * 删除酒店订单
     * 
     * @param id 酒店订单主键
     * @return 结果
     */
    public int deleteHotelOrderById(Long id);

    /**
     * 批量删除酒店订单
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHotelOrderByIds(Long[] ids);
    /**
     * 查询某房型的物理房间总数
     * * @param roomTypeId 房型ID
     * @return 房间总数
     */
    public Integer countTotalRoomsByTypeId(@Param("roomTypeId") Long roomTypeId);

    /**
     * 统计指定时间段内，某房型已经被预订的房间总数 (包含待支付、待入住、入住中)
     * * @param roomTypeId 房型ID
     * @param checkInDate 目标入住日期
     * @param checkOutDate 目标退房日期
     * @return 被占用的房间数
     */
    public Integer countOccupiedRooms(@Param("roomTypeId") Long roomTypeId,
                                      @Param("checkInDate") Date checkInDate,
                                      @Param("checkOutDate") Date checkOutDate);
}
