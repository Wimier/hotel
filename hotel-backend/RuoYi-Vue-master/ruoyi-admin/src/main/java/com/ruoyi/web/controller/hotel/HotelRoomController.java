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
import com.ruoyi.hotel.domain.HotelRoom;
import com.ruoyi.hotel.service.IHotelRoomService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 房间信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-24
 */
@RestController
@RequestMapping("/hotel/room")
public class HotelRoomController extends BaseController
{
    @Autowired
    private IHotelRoomService hotelRoomService;

    /**
     * 查询房间信息列表
     */
    @PreAuthorize("@ss.hasPermi('hotel:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(HotelRoom hotelRoom)
    {
        startPage();
        List<HotelRoom> list = hotelRoomService.selectHotelRoomList(hotelRoom);
        return getDataTable(list);
    }

    /**
     * 导出房间信息列表
     */
    @PreAuthorize("@ss.hasPermi('hotel:room:export')")
    @Log(title = "房间信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HotelRoom hotelRoom)
    {
        List<HotelRoom> list = hotelRoomService.selectHotelRoomList(hotelRoom);
        ExcelUtil<HotelRoom> util = new ExcelUtil<HotelRoom>(HotelRoom.class);
        util.exportExcel(response, list, "房间信息数据");
    }

    /**
     * 获取房间信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('hotel:room:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hotelRoomService.selectHotelRoomById(id));
    }

    /**
     * 新增房间信息
     */
    @PreAuthorize("@ss.hasPermi('hotel:room:add')")
    @Log(title = "房间信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HotelRoom hotelRoom)
    {
        return toAjax(hotelRoomService.insertHotelRoom(hotelRoom));
    }

    /**
     * 修改房间信息
     */
    @PreAuthorize("@ss.hasPermi('hotel:room:edit')")
    @Log(title = "房间信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HotelRoom hotelRoom)
    {
        return toAjax(hotelRoomService.updateHotelRoom(hotelRoom));
    }

    /**
     * 删除房间信息
     */
    @PreAuthorize("@ss.hasPermi('hotel:room:remove')")
    @Log(title = "房间信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hotelRoomService.deleteHotelRoomByIds(ids));
    }
}
