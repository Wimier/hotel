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
import com.ruoyi.hotel.domain.HotelRoomType;
import com.ruoyi.hotel.service.IHotelRoomTypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 房型Controller
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
@RestController
@RequestMapping("/hotel/type")
public class HotelRoomTypeController extends BaseController
{
    @Autowired
    private IHotelRoomTypeService hotelRoomTypeService;

    /**
     * 查询房型列表
     */
//    @PreAuthorize("@ss.hasPermi('hotel:type:list')")
    @GetMapping("/list")
    public TableDataInfo list(HotelRoomType hotelRoomType)
    {
        startPage();
        List<HotelRoomType> list = hotelRoomTypeService.selectHotelRoomTypeList(hotelRoomType);
        return getDataTable(list);
    }

    /**
     * 导出房型列表
     */
    @PreAuthorize("@ss.hasPermi('hotel:type:export')")
    @Log(title = "房型", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HotelRoomType hotelRoomType)
    {
        List<HotelRoomType> list = hotelRoomTypeService.selectHotelRoomTypeList(hotelRoomType);
        ExcelUtil<HotelRoomType> util = new ExcelUtil<HotelRoomType>(HotelRoomType.class);
        util.exportExcel(response, list, "房型数据");
    }

    /**
     * 获取房型详细信息
     */
//    @PreAuthorize("@ss.hasPermi('hotel:type:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hotelRoomTypeService.selectHotelRoomTypeById(id));
    }

    /**
     * 新增房型
     */
    @PreAuthorize("@ss.hasPermi('hotel:type:add')")
    @Log(title = "房型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HotelRoomType hotelRoomType)
    {
        return toAjax(hotelRoomTypeService.insertHotelRoomType(hotelRoomType));
    }

    /**
     * 修改房型
     */
    @PreAuthorize("@ss.hasPermi('hotel:type:edit')")
    @Log(title = "房型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HotelRoomType hotelRoomType)
    {
        return toAjax(hotelRoomTypeService.updateHotelRoomType(hotelRoomType));
    }

    /**
     * 删除房型
     */
    @PreAuthorize("@ss.hasPermi('hotel:type:remove')")
    @Log(title = "房型", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hotelRoomTypeService.deleteHotelRoomTypeByIds(ids));
    }
}
