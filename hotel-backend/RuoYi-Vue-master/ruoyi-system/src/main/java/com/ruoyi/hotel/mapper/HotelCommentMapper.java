package com.ruoyi.hotel.mapper;

import java.util.List;
import com.ruoyi.hotel.domain.HotelComment;

/**
 * 酒店评价Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-26
 */
public interface HotelCommentMapper 
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
     * 根据酒店ID查询评价平均分
     * @param hotelId 酒店ID
     * @return 平均分
     */
    public Double selectAvgScoreByHotelId(Long hotelId);

    /**
     * 修改酒店评价
     * 
     * @param hotelComment 酒店评价
     * @return 结果
     */
    public int updateHotelComment(HotelComment hotelComment);

    /**
     * 删除酒店评价
     * 
     * @param id 酒店评价主键
     * @return 结果
     */
    public int deleteHotelCommentById(Long id);

    /**
     * 批量删除酒店评价
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteHotelCommentByIds(Long[] ids);
}
