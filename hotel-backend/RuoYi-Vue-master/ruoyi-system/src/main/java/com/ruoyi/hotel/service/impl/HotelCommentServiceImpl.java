package com.ruoyi.hotel.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.hotel.domain.HotelInfo;
import com.ruoyi.hotel.domain.HotelOrder;
import com.ruoyi.hotel.mapper.HotelInfoMapper;
import com.ruoyi.hotel.mapper.HotelOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.hotel.mapper.HotelCommentMapper;
import com.ruoyi.hotel.domain.HotelComment;
import com.ruoyi.hotel.service.IHotelCommentService;

/**
 * 酒店评价Service业务层处理
 * * @author ruoyi
 * @date 2026-02-26
 */
@Service
public class HotelCommentServiceImpl implements IHotelCommentService
{
    @Autowired
    private HotelCommentMapper hotelCommentMapper;

    @Autowired
    private HotelOrderMapper hotelOrderMapper;

    @Autowired
    private HotelInfoMapper hotelInfoMapper; // ✨ 注入酒店信息Mapper

    /**
     * 查询酒店评价
     */
    @Override
    public HotelComment selectHotelCommentById(Long id)
    {
        return hotelCommentMapper.selectHotelCommentById(id);
    }

    /**
     * 查询酒店评价列表
     */
    @Override
    public List<HotelComment> selectHotelCommentList(HotelComment hotelComment)
    {
        return hotelCommentMapper.selectHotelCommentList(hotelComment);
    }

    /**
     * 新增酒店评价
     * 逻辑：订单校验 -> 插入评价 -> 更新订单评价状态 -> 同步更新酒店评分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHotelComment(HotelComment hotelComment)
    {
        // 1. 获取关联订单详情
        HotelOrder order = hotelOrderMapper.selectHotelOrderById(hotelComment.getOrderId());

        if (order == null) {
            throw new ServiceException("订单不存在，无法评价");
        }

        // 2. 权限校验：确保是本人评价
        Long currentUserId = SecurityUtils.getUserId();
        if (!order.getUserId().equals(currentUserId)) {
            throw new ServiceException("您无权评价该订单");
        }

        // 3. 状态校验：只有状态为 3 (已完成) 的订单才能评价
        if (order.getStatus() != 3) {
            throw new ServiceException("只有已完成的订单才能进行评价");
        }

        // 4. 填充评价基础信息
        hotelComment.setUserId(currentUserId);
        hotelComment.setHotelId(order.getHotelId());
        hotelComment.setRoomTypeId(order.getRoomTypeId());
        hotelComment.setOrderSn(order.getOrderSn());
        hotelComment.setStatus(1); // 默认显示
        hotelComment.setCreateTime(DateUtils.getNowDate());

        // 5. 插入评价记录
        int rows = hotelCommentMapper.insertHotelComment(hotelComment);

        if (rows > 0) {
            // 6. 更新对应订单的评价状态为 1(已评价)
            HotelOrder updateOrder = new HotelOrder();
            updateOrder.setId(hotelComment.getOrderId());
            updateOrder.setCommentStatus(1);
            hotelOrderMapper.updateHotelOrder(updateOrder);

            // ✨ 7. 同步计算并更新酒店平均分
            this.updateHotelAverageScore(hotelComment.getHotelId());
        }

        return rows;
    }

    /**
     * 修改酒店评价
     * 逻辑：记录回复/修改时间 -> 更新内容 -> 同步更新酒店评分
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateHotelComment(HotelComment hotelComment)
    {
        // 如果是商家回复，记录回复时间
        if (hotelComment.getReplyContent() != null) {
            hotelComment.setReplyTime(DateUtils.getNowDate());
        }
        hotelComment.setUpdateTime(DateUtils.getNowDate());

        int rows = hotelCommentMapper.updateHotelComment(hotelComment);

        if (rows > 0) {
            // 获取最新数据以同步分值（防止修改了分数 score）
            HotelComment latest = hotelCommentMapper.selectHotelCommentById(hotelComment.getId());
            if (latest != null) {
                this.updateHotelAverageScore(latest.getHotelId());
            }
        }
        return rows;
    }

    /**
     * 批量删除酒店评价
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHotelCommentByIds(Long[] ids)
    {
        for (Long id : ids) {
            this.deleteHotelCommentById(id);
        }
        return ids.length;
    }

    /**
     * 删除酒店评价信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteHotelCommentById(Long id)
    {
        HotelComment comment = hotelCommentMapper.selectHotelCommentById(id);
        int rows = hotelCommentMapper.deleteHotelCommentById(id);
        if (rows > 0 && comment != null) {
            // 删除后重新计算分数
            this.updateHotelAverageScore(comment.getHotelId());
        }
        return rows;
    }

    /**
     * ✨ 私有方法：计算并同步酒店平均分
     * @param hotelId 酒店ID
     */
    private void updateHotelAverageScore(Long hotelId) {
        // 1. 调用 Mapper 查询该酒店所有状态为 1(显示) 的评价平均分
        Double avgScore = hotelCommentMapper.selectAvgScoreByHotelId(hotelId);

        HotelInfo hotelInfo = new HotelInfo();
        hotelInfo.setId(hotelId);

        if (avgScore != null) {
            // 2. 将 Double 转为 BigDecimal 并保留 1 位小数，执行四舍五入
            BigDecimal roundedScore = BigDecimal.valueOf(avgScore).setScale(1, RoundingMode.HALF_UP);
            hotelInfo.setScore(roundedScore);
        } else {
            // 如果没有评价了，分值设为 5.0 或 0
            hotelInfo.setScore(BigDecimal.valueOf(5.0));
        }

        // 3. 更新酒店表
        hotelInfoMapper.updateHotelInfo(hotelInfo);
    }
}