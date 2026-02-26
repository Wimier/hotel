package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 首页轮播图对象 sys_banner
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
public class SysBanner extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 图片外链地址 (必备, 字符串) */
    @Excel(name = "图片外链地址 (必备, 字符串)")
    private String imgUrl;

    /** 点击后跳转的酒店ID (可选, 数字) */
    @Excel(name = "点击后跳转的酒店ID (可选, 数字)")
    private Long hotelId;

    /** 轮播排序, 越小越靠前 (可选, 数字) */
    @Excel(name = "轮播排序, 越小越靠前 (可选, 数字)")
    private Long sortOrder;

    /** 状态: 0下线, 1上线 (必备, 数字) */
    @Excel(name = "状态: 0下线, 1上线 (必备, 数字)")
    private Integer status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setImgUrl(String imgUrl) 
    {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() 
    {
        return imgUrl;
    }

    public void setHotelId(Long hotelId) 
    {
        this.hotelId = hotelId;
    }

    public Long getHotelId() 
    {
        return hotelId;
    }

    public void setSortOrder(Long sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder() 
    {
        return sortOrder;
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
            .append("imgUrl", getImgUrl())
            .append("hotelId", getHotelId())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
