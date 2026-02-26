package com.ruoyi.hotel.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.hotel.mapper.HotelOrderMapper;
import com.ruoyi.hotel.mapper.HotelRoomTypeMapper;
import com.ruoyi.hotel.domain.HotelOrder;
import com.ruoyi.hotel.domain.HotelRoomType;
import com.ruoyi.hotel.service.IHotelOrderService;

/**
 * 酒店订单Service业务层处理
 * * @author ruoyi
 * @date 2026-02-24
 */
@Service
public class HotelOrderServiceImpl implements IHotelOrderService
{
    @Autowired
    private HotelOrderMapper hotelOrderMapper;

    // 引入房型Mapper，用于获取单价
    @Autowired
    private HotelRoomTypeMapper roomTypeMapper;

    /**
     * 查询酒店订单
     */
    @Override
    public HotelOrder selectHotelOrderById(Long id)
    {
        return hotelOrderMapper.selectHotelOrderById(id);
    }

    /**
     * 查询酒店订单列表
     */
    //
    @Override
    public List<HotelOrder> selectHotelOrderList(HotelOrder hotelOrder)
    {
        Long currentUserId = SecurityUtils.getUserId();

        // 1. 如果是超级管理员，什么都不过滤，看全量数据
        if (SecurityUtils.isAdmin(currentUserId)) {
            return hotelOrderMapper.selectHotelOrderList(hotelOrder);
        }

        // 2. 核心判断：区分商户和小程序用户
        // 逻辑：如果用户的角色里包含 "merchant"（商户），则按商户ID过滤
        // 如果是普通用户，则按用户ID过滤
        if (SecurityUtils.hasRole("hotel_merchant")) {
            // 商户逻辑：看自己酒店下的所有订单
            hotelOrder.setMerchantId(currentUserId);
        } else {
            // 小程序用户逻辑：只看自己下的订单
            hotelOrder.setUserId(currentUserId);
        }

        return hotelOrderMapper.selectHotelOrderList(hotelOrder);
    }

    /**
     * 新增酒店订单 (携程级核心预订逻辑)
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 必须加事务，报错时回滚
    public int insertHotelOrder(HotelOrder hotelOrder)
    {
        // 1. 校验日期合法性
        if (hotelOrder.getCheckInDate() == null || hotelOrder.getCheckOutDate() == null) {
            throw new ServiceException("入住日期和退房日期不能为空");
        }
        long diffInMillies = hotelOrder.getCheckOutDate().getTime() - hotelOrder.getCheckInDate().getTime();
        long days = diffInMillies / (1000 * 60 * 60 * 24);
        if (days <= 0) {
            throw new ServiceException("退房日期必须晚于入住日期");
        }
        hotelOrder.setTotalDays((long) days);

        // 2. 获取房型信息（查价格）
        HotelRoomType roomType = roomTypeMapper.selectHotelRoomTypeById(hotelOrder.getRoomTypeId());
        if (roomType == null) {
            throw new ServiceException("抱歉，该房型信息不存在");
        }
        // 假设状态 1 代表下架或停用 (根据你的实际字典调整)
        if ("1".equals(roomType.getStatus())) {
            throw new ServiceException("抱歉，该房型已停售");
        }

        // 3. 校验库存防超卖（动态计算该日期区间的剩余房间）
        int availableRooms = checkInventory(hotelOrder);
        if (availableRooms < hotelOrder.getRoomCount()) {
            throw new ServiceException("抱歉，该时段房源火爆，仅剩 " + availableRooms + " 间");
        }

        // 4. 后端强制计算总金额 (单价 * 天数 * 房间数)，绝对不能信任前端传的金额
        BigDecimal price = roomType.getPrice() != null ? roomType.getPrice() : BigDecimal.ZERO;
        BigDecimal totalAmount = price
                .multiply(new BigDecimal(days))
                .multiply(new BigDecimal(hotelOrder.getRoomCount()));
        hotelOrder.setTotalAmount(totalAmount);

        // 5. 生成唯一业务订单号 OrderSn (规则: HO + 时间戳 + 用户ID后4位)
        // 这样不仅唯一，还能防止竞争对手通过ID自增猜测你的订单量
        String timeStr = DateUtils.dateTimeNow();
        long userIdSuffix = hotelOrder.getUserId() != null ? hotelOrder.getUserId() % 10000 : (long)(Math.random() * 10000);
        String orderSn = "HO" + timeStr + String.format("%04d", userIdSuffix);
        hotelOrder.setOrderSn(orderSn);

        // 6. 初始化状态
        hotelOrder.setStatus(0); // 0 代表待支付
        hotelOrder.setCreateTime(DateUtils.getNowDate());

        // 7. 执行插入
        return hotelOrderMapper.insertHotelOrder(hotelOrder);
    }

    /**
     * 修改酒店订单
     */
    @Override
    public int updateHotelOrder(HotelOrder hotelOrder)
    {
        hotelOrder.setUpdateTime(DateUtils.getNowDate());
        return hotelOrderMapper.updateHotelOrder(hotelOrder);
    }

    /**
     * 批量删除酒店订单
     */
    @Override
    public int deleteHotelOrderByIds(Long[] ids)
    {
        return hotelOrderMapper.deleteHotelOrderByIds(ids);
    }

    /**
     * 删除酒店订单信息
     */
    @Override
    public int deleteHotelOrderById(Long id)
    {
        return hotelOrderMapper.deleteHotelOrderById(id);
    }

    /**
     * 私有方法：校验时段内的可用库存
     */
    /**
     * 私有方法：校验时段内的可用库存
     */
    private int checkInventory(HotelOrder order) {
        // 【第一步】查询该房型总共有多少个实体房间
        Integer totalRooms = hotelOrderMapper.countTotalRoomsByTypeId(order.getRoomTypeId());
        if (totalRooms == null) {
            totalRooms = 0;
        }

        // 【第二步】查询该时段内已经被预订（且状态为有效）的房间数量之和
        Integer occupiedRooms = hotelOrderMapper.countOccupiedRooms(order.getRoomTypeId(),
                order.getCheckInDate(),
                order.getCheckOutDate());
        if (occupiedRooms == null) {
            occupiedRooms = 0;
        }

        // 【第三步】返回剩余可用数量
        return totalRooms - occupiedRooms;
    }
}