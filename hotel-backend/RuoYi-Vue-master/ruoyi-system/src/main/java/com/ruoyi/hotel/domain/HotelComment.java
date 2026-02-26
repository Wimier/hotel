package com.ruoyi.hotel.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 酒店评价对象 hotel_comment
 * 
 * @author ruoyi
 * @date 2026-02-26
 */
@Data
public class HotelComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评价ID */
    private Long id;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 订单编号(冗余以方便查询) */
    @Excel(name = "订单编号(冗余以方便查询)")
    private String orderSn;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 酒店ID */
    @Excel(name = "酒店ID")
    private Long hotelId;

    /** 房型ID */
    @Excel(name = "房型ID")
    private Long roomTypeId;

    /** 评分星数(1-5) */
    @Excel(name = "评分星数(1-5)")
    private Integer score;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 评价图片(多图逗号隔开) */
    @Excel(name = "评价图片(多图逗号隔开)")
    private String images;

    /** 是否匿名:0否,1是 */
    @Excel(name = "是否匿名:0否,1是")
    private Integer isAnonymous;

    /** 商家回复内容 */
    @Excel(name = "商家回复内容")
    private String replyContent;

    /** 商家回复时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "商家回复时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date replyTime;

    /** 状态:0隐藏,1显示 */
    @Excel(name = "状态:0隐藏,1显示")
    private Integer status;

    private String userAvatar;
    private String userName;
    private String roomTypeName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setOrderSn(String orderSn) 
    {
        this.orderSn = orderSn;
    }

    public String getOrderSn() 
    {
        return orderSn;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setHotelId(Long hotelId) 
    {
        this.hotelId = hotelId;
    }

    public Long getHotelId() 
    {
        return hotelId;
    }

    public void setRoomTypeId(Long roomTypeId) 
    {
        this.roomTypeId = roomTypeId;
    }

    public Long getRoomTypeId() 
    {
        return roomTypeId;
    }

    public void setScore(Integer score) 
    {
        this.score = score;
    }

    public Integer getScore() 
    {
        return score;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setImages(String images) 
    {
        this.images = images;
    }

    public String getImages() 
    {
        return images;
    }

    public void setIsAnonymous(Integer isAnonymous) 
    {
        this.isAnonymous = isAnonymous;
    }

    public Integer getIsAnonymous() 
    {
        return isAnonymous;
    }

    public void setReplyContent(String replyContent) 
    {
        this.replyContent = replyContent;
    }

    public String getReplyContent() 
    {
        return replyContent;
    }

    public void setReplyTime(Date replyTime) 
    {
        this.replyTime = replyTime;
    }

    public Date getReplyTime() 
    {
        return replyTime;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderId", getOrderId())
            .append("orderSn", getOrderSn())
            .append("userId", getUserId())
            .append("hotelId", getHotelId())
            .append("roomTypeId", getRoomTypeId())
            .append("score", getScore())
            .append("content", getContent())
            .append("images", getImages())
            .append("isAnonymous", getIsAnonymous())
            .append("replyContent", getReplyContent())
            .append("replyTime", getReplyTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
