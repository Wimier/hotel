package com.ruoyi.hotel.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 房间信息对象 hotel_room
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
public class HotelRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 房间实体ID */
    private Long id;

    /** 所属酒店ID */
    @Excel(name = "所属酒店ID")
    private Long hotelId;

    /** 所属房型ID */
    @Excel(name = "所属房型ID")
    private Long roomTypeId;

    /** 房间号(如:8301) */
    @Excel(name = "房间号(如:8301)")
    private String roomNo;

    /** 楼层 */
    @Excel(name = "楼层")
    private Long floor;

    /** 状态(0空闲 1占用 2维修) */
    @Excel(name = "状态(0空闲 1占用 2维修)")
    private String status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public void setRoomNo(String roomNo) 
    {
        this.roomNo = roomNo;
    }

    public String getRoomNo() 
    {
        return roomNo;
    }

    public void setFloor(Long floor) 
    {
        this.floor = floor;
    }

    public Long getFloor() 
    {
        return floor;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("hotelId", getHotelId())
            .append("roomTypeId", getRoomTypeId())
            .append("roomNo", getRoomNo())
            .append("floor", getFloor())
            .append("status", getStatus())
            .toString();
    }
}
