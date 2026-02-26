package com.ruoyi.hotel.service;

import java.util.List;
import com.ruoyi.hotel.domain.HotelComment;

/**
 * 酒店评价Service接口
 * 
 * @author ruoyi
 * @date 2026-02-26
 */
public interface IHotelCommentService 
{
    /**
     * 查询酒店评价
     * 
     * @param id 酒店评价主键
     * @return 酒店评价
     */
    public HotelComment selectHotelCommentById(Long id);

    /**
     * 查询酒店评价列表
     * 
     * @param hotelComment 酒店评价
     * @return 酒店评价集合
     */
    public List<HotelComment> selectHotelCommentList(HotelComment hotelComment);

    /**
     * 新增酒店评价
     * 
     * @param hotelComment 酒店评价
     * @return 结果
     */
    public int insertHotelComment(HotelComment hotelComment);

    /**
     * 修改酒店评价
     * 
     * @param hotelComment 酒店评价
     * @return 结果
     */
    public int updateHotelComment(HotelComment hotelComment);

    /**
     * 批量删除酒店评价
     * 
     * @param ids 需要删除的酒店评价主键集合
     * @return 结果
     */
    public int deleteHotelCommentByIds(Long[] ids);

    /**
     * 删除酒店评价信息
     * 
     * @param id 酒店评价主键
     * @return 结果
     */
    public int deleteHotelCommentById(Long id);
}
