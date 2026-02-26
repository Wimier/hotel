package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.HotelOrder;

/**
 * 酒店订单Service接口
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
public interface IHotelOrderService 
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
     * 批量删除酒店订单
     * 
     * @param ids 需要删除的酒店订单主键集合
     * @return 结果
     */
    public int deleteHotelOrderByIds(Long[] ids);

    /**
     * 删除酒店订单信息
     * 
     * @param id 酒店订单主键
     * @return 结果
     */
    public int deleteHotelOrderById(Long id);
}
