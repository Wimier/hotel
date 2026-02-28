package com.ruoyi.web.controller.hotel;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.hotel.domain.HotelInfo;
import com.ruoyi.hotel.service.IHotelInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils; // 必须确保导入了安全工具类

/**
 * 酒店信息Controller
 * * @author ruoyi
 * @date 2026-02-24
 */
@RestController
@RequestMapping("/hotel/info")
public class HotelInfoController extends BaseController
{
    @Autowired
    private IHotelInfoService hotelInfoService;

    /**
     * 查询酒店信息列表
     */
//    @PreAuthorize("@ss.hasPermi('hotel:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(HotelInfo hotelInfo)
    {
        // 逻辑：如果不是管理员，只能查询属于自己的商户数据
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            hotelInfo.setMerchantId(SecurityUtils.getUserId());
        }
        startPage();
        List<HotelInfo> list = hotelInfoService.selectHotelInfoList(hotelInfo);
        return getDataTable(list);
    }

    /**
     * 获取酒店信息详细信息
     */
//    @PreAuthorize("@ss.hasPermi('hotel:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hotelInfoService.selectHotelInfoById(id));
    }
    /**
     * 新增酒店信息
     */
    @PreAuthorize("@ss.hasPermi('hotel:info:add')")
    @Log(title = "酒店信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HotelInfo hotelInfo)
    {
        // 1. 自动绑定商户ID（当前登录用户的ID）
        hotelInfo.setMerchantId(SecurityUtils.getUserId());

        // 2. 自动设置创建人
        hotelInfo.setCreateBy(SecurityUtils.getUsername());

        // 3. 强制初始状态为审核中，防止商户直接发布
        hotelInfo.setStatus("AUDITING");

        return toAjax(hotelInfoService.insertHotelInfo(hotelInfo));
    }

    /**
     * 修改酒店信息
     */
    @PreAuthorize("@ss.hasPermi('hotel:info:edit')")
    @Log(title = "酒店信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HotelInfo hotelInfo)
    {
        // 1. 自动设置更新人
        hotelInfo.setUpdateBy(SecurityUtils.getUsername());

        // 2. 安全逻辑：如果是商户修改，状态强制打回“审核中”
        // 防止商户把已通过的酒店偷偷改成非法内容
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            hotelInfo.setStatus("AUDITING");
        }

        return toAjax(hotelInfoService.updateHotelInfo(hotelInfo));
    }

    /**
     * 删除酒店信息
     */
    @PreAuthorize("@ss.hasPermi('hotel:info:remove')")
    @Log(title = "酒店信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hotelInfoService.deleteHotelInfoByIds(ids));
    }

    /**
     * 导出酒店信息列表
     */
    @PreAuthorize("@ss.hasPermi('hotel:info:export')")
    @Log(title = "酒店信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HotelInfo hotelInfo)
    {
        if (!SecurityUtils.isAdmin(SecurityUtils.getUserId())) {
            hotelInfo.setMerchantId(SecurityUtils.getUserId());
        }
        List<HotelInfo> list = hotelInfoService.selectHotelInfoList(hotelInfo);
        ExcelUtil<HotelInfo> util = new ExcelUtil<HotelInfo>(HotelInfo.class);
        util.exportExcel(response, list, "酒店信息数据");
    }
}