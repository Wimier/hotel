package com.ruoyi.hotel.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 酒店订单对象 hotel_order
 * 
 * @author ruoyi
 * @date 2026-02-24
 */

@Data
public class HotelOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long id;

    /** 订单编号(唯一业务号) */
    @Excel(name = "订单编号(唯一业务号)")
    private String orderSn;

    /** 小程序用户ID */
    @Excel(name = "小程序用户ID")
    private Long userId;

    /** 酒店ID */
    @Excel(name = "酒店ID")
    private Long hotelId;

    /** 房型ID */
    @Excel(name = "房型ID")
    private Long roomTypeId;

    /** 预订房间数量 */
    @Excel(name = "预订房间数量")
    private Long roomCount;

    /** 入住日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入住日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkInDate;

    /** 退房日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "退房日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkOutDate;

    /** 入住天数 */
    @Excel(name = "入住天数")
    private Long totalDays;

    /** 订单总金额 */
    @Excel(name = "订单总金额")
    private BigDecimal totalAmount;

    /** 入住人姓名(逗号隔开) */
    @Excel(name = "入住人姓名(逗号隔开)")
    private String guestName;

    /** 入住人电话 */
    @Excel(name = "入住人电话")
    private String guestPhone;

    /** 订单状态:0待支付,1待入住,2入住中,3已完成,4已取消,5已退款 */
    @Excel(name = "订单状态:0待支付,1待入住,2入住中,3已完成,4已取消,5已退款")
    private Integer status;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date payTime;


    /** 酒店名称 */
    private String hotelName;

    /** 房型名称 */
    private String roomTypeName;

    private Integer commentStatus;
    private Integer commentId;
    private String roomImage;
    private Long merchantId;

    public String getHotelName()
    {
        return hotelName;
    }

    public void setHotelName(String hotelName)
    {
        this.hotelName = hotelName;
    }

    public String getRoomTypeName()
    {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName)
    {
        this.roomTypeName = roomTypeName;
    }
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public void setRoomCount(Long roomCount) 
    {
        this.roomCount = roomCount;
    }

    public Long getRoomCount() 
    {
        return roomCount;
    }

    public void setCheckInDate(Date checkInDate) 
    {
        this.checkInDate = checkInDate;
    }

    public Date getCheckInDate() 
    {
        return checkInDate;
    }

    public void setCheckOutDate(Date checkOutDate) 
    {
        this.checkOutDate = checkOutDate;
    }

    public Date getCheckOutDate() 
    {
        return checkOutDate;
    }

    public void setTotalDays(Long totalDays) 
    {
        this.totalDays = totalDays;
    }

    public Long getTotalDays() 
    {
        return totalDays;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setGuestName(String guestName) 
    {
        this.guestName = guestName;
    }

    public String getGuestName() 
    {
        return guestName;
    }

    public void setGuestPhone(String guestPhone) 
    {
        this.guestPhone = guestPhone;
    }

    public String getGuestPhone() 
    {
        return guestPhone;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setPayTime(Date payTime) 
    {
        this.payTime = payTime;
    }

    public Date getPayTime() 
    {
        return payTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderSn", getOrderSn())
            .append("userId", getUserId())
            .append("hotelId", getHotelId())
            .append("roomTypeId", getRoomTypeId())
            .append("roomCount", getRoomCount())
            .append("checkInDate", getCheckInDate())
            .append("checkOutDate", getCheckOutDate())
            .append("totalDays", getTotalDays())
            .append("totalAmount", getTotalAmount())
            .append("guestName", getGuestName())
            .append("guestPhone", getGuestPhone())
            .append("status", getStatus())
            .append("payTime", getPayTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
