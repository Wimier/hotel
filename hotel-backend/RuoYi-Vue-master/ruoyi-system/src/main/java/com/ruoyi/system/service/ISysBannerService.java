package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysBanner;

/**
 * 首页轮播图Service接口
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
public interface ISysBannerService 
{
    /**
     * 查询首页轮播图
     * 
     * @param id 首页轮播图主键
     * @return 首页轮播图
     */
    public SysBanner selectSysBannerById(Long id);

    /**
     * 查询首页轮播图列表
     * 
     * @param sysBanner 首页轮播图
     * @return 首页轮播图集合
     */
    public List<SysBanner> selectSysBannerList(SysBanner sysBanner);

    /**
     * 新增首页轮播图
     * 
     * @param sysBanner 首页轮播图
     * @return 结果
     */
    public int insertSysBanner(SysBanner sysBanner);

    /**
     * 修改首页轮播图
     * 
     * @param sysBanner 首页轮播图
     * @return 结果
     */
    public int updateSysBanner(SysBanner sysBanner);

    /**
     * 批量删除首页轮播图
     * 
     * @param ids 需要删除的首页轮播图主键集合
     * @return 结果
     */
    public int deleteSysBannerByIds(Long[] ids);

    /**
     * 删除首页轮播图信息
     * 
     * @param id 首页轮播图主键
     * @return 结果
     */
    public int deleteSysBannerById(Long id);
}
