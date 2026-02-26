package com.ruoyi.hotel.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 房型对象 hotel_room_type
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
public class HotelRoomType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 房型ID */
    private Long id;

    /** 所属酒店ID */
    @Excel(name = "所属酒店ID")
    private Long hotelId;

    /** 房型名称(如:标准双床房) */
    @Excel(name = "房型名称(如:标准双床房)")
    private String typeName;

    /** 基准价格 */
    @Excel(name = "基准价格")
    private BigDecimal price;

    /** 床型 */
    @Excel(name = "床型")
    private String bedType;

    @Excel(name = "房间大类")
    private String roomCategory;

    /** 是否有窗(0无 1有) */
    @Excel(name = "是否有窗(0无 1有)")
    private String hasWindow;

    /** 是否含早(0无 1有) */
    @Excel(name = "是否含早(0无 1有)")
    private String hasBreakfast;

    /** 房型图片 */
    @Excel(name = "房型图片")
    private String imageUrl;

    /** 状态(0正常 1停售) */
    @Excel(name = "状态(0正常 1停售)")
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

    public void setTypeName(String typeName) 
    {
        this.typeName = typeName;
    }

    public String getTypeName() 
    {
        return typeName;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setBedType(String bedType) 
    {
        this.bedType = bedType;
    }

    public String getBedType() 
    {
        return bedType;
    }

    public void setRoomCategory(String roomCategory)
    {
        this.roomCategory = roomCategory;
    }

    public String getRoomCategory()
    {
        return roomCategory;
    }

    public void setHasWindow(String hasWindow) 
    {
        this.hasWindow = hasWindow;
    }

    public String getHasWindow() 
    {
        return hasWindow;
    }

    public void setHasBreakfast(String hasBreakfast) 
    {
        this.hasBreakfast = hasBreakfast;
    }

    public String getHasBreakfast() 
    {
        return hasBreakfast;
    }

    public void setImageUrl(String imageUrl) 
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() 
    {
        return imageUrl;
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
            .append("typeName", getTypeName())
            .append("price", getPrice())
            .append("bedType", getBedType())
            .append("hasWindow", getHasWindow())
            .append("hasBreakfast", getHasBreakfast())
            .append("imageUrl", getImageUrl())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
