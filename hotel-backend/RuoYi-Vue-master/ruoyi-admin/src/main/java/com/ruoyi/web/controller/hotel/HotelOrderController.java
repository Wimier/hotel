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
import com.ruoyi.hotel.domain.HotelOrder;
import com.ruoyi.hotel.service.IHotelOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 酒店订单Controller
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
@RestController
@RequestMapping("/hotel/order")
public class HotelOrderController extends BaseController
{
    @Autowired
    private IHotelOrderService hotelOrderService;

    /**
     * 查询酒店订单列表
     */
//    @PreAuthorize("@ss.hasPermi('hotel:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(HotelOrder hotelOrder)
    {
        startPage();
        List<HotelOrder> list = hotelOrderService.selectHotelOrderList(hotelOrder);
        return getDataTable(list);
    }

    /**
     * 导出酒店订单列表
     */
    @PreAuthorize("@ss.hasPermi('hotel:order:export')")
    @Log(title = "酒店订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HotelOrder hotelOrder)
    {
        List<HotelOrder> list = hotelOrderService.selectHotelOrderList(hotelOrder);
        ExcelUtil<HotelOrder> util = new ExcelUtil<HotelOrder>(HotelOrder.class);
        util.exportExcel(response, list, "酒店订单数据");
    }

    /**
     * 获取酒店订单详细信息
     */
//    @PreAuthorize("@ss.hasPermi('hotel:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hotelOrderService.selectHotelOrderById(id));
    }

    /**
     * 新增酒店订单
     */
//    @PreAuthorize("@ss.hasPermi('hotel:order:add')")
    @Log(title = "酒店订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HotelOrder hotelOrder)
    {
        return toAjax(hotelOrderService.insertHotelOrder(hotelOrder));
    }

    /**
     * 修改酒店订单
     */
//    @PreAuthorize("@ss.hasPermi('hotel:order:edit')")
    @Log(title = "酒店订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HotelOrder hotelOrder)
    {
        return toAjax(hotelOrderService.updateHotelOrder(hotelOrder));
    }

    /**
     * 删除酒店订单
     */
    @PreAuthorize("@ss.hasPermi('hotel:order:remove')")
    @Log(title = "酒店订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hotelOrderService.deleteHotelOrderByIds(ids));
    }
}
