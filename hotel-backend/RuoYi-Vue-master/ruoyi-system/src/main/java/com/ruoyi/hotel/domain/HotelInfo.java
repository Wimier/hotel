package com.ruoyi.hotel.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.DataSource;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 酒店信息对象 hotel_info
 * * @author ruoyi
 * @date 2026-02-24
 */
public class HotelInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 酒店中文名 */
    @Excel(name = "酒店中文名")
    private String nameCn;

    /** 酒店英文名 */
    @Excel(name = "酒店英文名")
    private String nameEn;

    /** 省 */
    @Excel(name = "省")
    private String province;

    /** 市 */
    @Excel(name = "市")
    private String city;

    /** 区 */
    @Excel(name = "区")
    private String district;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String detailAddress;

    /** 经度 */
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private BigDecimal latitude;

    /** 酒店封面图/Banner */
    @Excel(name = "酒店封面图/Banner")
    private String bannerUrl;

    /** 星级评分(0-5) */
    @Excel(name = "星级评分(0-5)")
    private Long starRating;

    /** 开业时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开业时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date openingDate;

    /** 起步价格 */
    @Excel(name = "起步价格")
    private BigDecimal price;

    /** 状态: AUDITING审核中, PASS通过, REJECT不通过, OFFLINE下线 */
    @Excel(name = "状态: AUDITING审核中, PASS通过, REJECT不通过, OFFLINE下线")
    private String status;

    /** 拒绝原因 */
    @Excel(name = "拒绝原因")
    private String rejectReason;

    /** 所属商户ID */
    @Excel(name = "所属商户ID")
    private Long merchantId;

    @Excel(name = "停车")
    private String park;

    @Excel(name = "住客评分")
    private BigDecimal score;

    /** 是否有免费WiFi: 0否, 1是 */
    @Excel(name = "是否有免费WiFi", readConverterExp = "0=否,1=是")
    private Integer hasWifi;

    /** 是否有电梯: 0否, 1是 */
    @Excel(name = "是否有电梯", readConverterExp = "0=否,1=是")
    private Integer hasElevator;

    private String phone;



    // Getter 和 Setter 方法
    public void setScore(BigDecimal score) { this.score = score; }
    public BigDecimal getScore() { return score; }

    public void setHasWifi(Integer hasWifi) { this.hasWifi = hasWifi; }
    public Integer getHasWifi() { return hasWifi; }

    public void setHasElevator(Integer hasElevator) { this.hasElevator = hasElevator; }
    public Integer getHasElevator() { return hasElevator; }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setNameCn(String nameCn)
    {
        this.nameCn = nameCn;
    }

    public String getNameCn()
    {
        return nameCn;
    }

    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn;
    }


    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }


    public String getNameEn()
    {
        return nameEn;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getProvince()
    {
        return province;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return city;
    }

    public void setDistrict(String district)
    {
        this.district = district;
    }

    public String getDistrict()
    {
        return district;
    }

    public void setDetailAddress(String detailAddress)
    {
        this.detailAddress = detailAddress;
    }

    public String getDetailAddress()
    {
        return detailAddress;
    }

    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public void setBannerUrl(String bannerUrl)
    {
        this.bannerUrl = bannerUrl;
    }

    public String getBannerUrl()
    {
        return bannerUrl;
    }

    public void setStarRating(Long starRating)
    {
        this.starRating = starRating;
    }

    public Long getStarRating()
    {
        return starRating;
    }

    public void setOpeningDate(Date openingDate)
    {
        this.openingDate = openingDate;
    }

    public Date getOpeningDate()
    {
        return openingDate;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setMerchantId(Long merchantId)
    {
        this.merchantId = merchantId;
    }

    public Long getMerchantId()
    {
        return merchantId;
    }

    public String getPark() {
        return park;
    }

    public void setPark(String park) {
        this.park = park;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("nameCn", getNameCn())
                .append("nameEn", getNameEn())
                .append("province", getProvince())
                .append("city", getCity())
                .append("district", getDistrict())
                .append("detailAddress", getDetailAddress())
                .append("longitude", getLongitude())
                .append("latitude", getLatitude())
                .append("bannerUrl", getBannerUrl())
                .append("starRating", getStarRating())
                .append("openingDate", getOpeningDate())
                .append("price", getPrice())
                .append("status", getStatus())
                .append("rejectReason", getRejectReason())
                .append("merchantId", getMerchantId())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("park",getPark())
                .append("score", getScore())
                .append("hasWifi", getHasWifi())
                .append("hasElevator", getHasElevator())
                .append("phone", getPhone())
                .toString();
    }
}