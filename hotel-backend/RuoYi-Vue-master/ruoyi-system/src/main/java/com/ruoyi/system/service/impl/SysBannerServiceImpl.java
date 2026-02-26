package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysBannerMapper;
import com.ruoyi.system.domain.SysBanner;
import com.ruoyi.system.service.ISysBannerService;

/**
 * 首页轮播图Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
@Service
public class SysBannerServiceImpl implements ISysBannerService 
{
    @Autowired
    private SysBannerMapper sysBannerMapper;

    /**
     * 查询首页轮播图
     * 
     * @param id 首页轮播图主键
     * @return 首页轮播图
     */
    @Override
    public SysBanner selectSysBannerById(Long id)
    {
        return sysBannerMapper.selectSysBannerById(id);
    }

    /**
     * 查询首页轮播图列表
     * 
     * @param sysBanner 首页轮播图
     * @return 首页轮播图
     */
    @Override
    public List<SysBanner> selectSysBannerList(SysBanner sysBanner)
    {
        return sysBannerMapper.selectSysBannerList(sysBanner);
    }

    /**
     * 新增首页轮播图
     * 
     * @param sysBanner 首页轮播图
     * @return 结果
     */
    @Override
    public int insertSysBanner(SysBanner sysBanner)
    {
        sysBanner.setCreateTime(DateUtils.getNowDate());
        return sysBannerMapper.insertSysBanner(sysBanner);
    }

    /**
     * 修改首页轮播图
     * 
     * @param sysBanner 首页轮播图
     * @return 结果
     */
    @Override
    public int updateSysBanner(SysBanner sysBanner)
    {
        return sysBannerMapper.updateSysBanner(sysBanner);
    }

    /**
     * 批量删除首页轮播图
     * 
     * @param ids 需要删除的首页轮播图主键
     * @return 结果
     */
    @Override
    public int deleteSysBannerByIds(Long[] ids)
    {
        return sysBannerMapper.deleteSysBannerByIds(ids);
    }

    /**
     * 删除首页轮播图信息
     * 
     * @param id 首页轮播图主键
     * @return 结果
     */
    @Override
    public int deleteSysBannerById(Long id)
    {
        return sysBannerMapper.deleteSysBannerById(id);
    }
}
