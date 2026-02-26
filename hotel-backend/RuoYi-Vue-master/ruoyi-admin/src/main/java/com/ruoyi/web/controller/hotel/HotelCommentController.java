package com.ruoyi.web.controller.hotel;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.hotel.domain.HotelInfo;
import com.ruoyi.hotel.service.IHotelInfoService;
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
import com.ruoyi.hotel.domain.HotelComment;
import com.ruoyi.hotel.service.IHotelCommentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 酒店评价Controller
 * 
 * @author ruoyi
 * @date 2026-02-26
 */
@RestController
@RequestMapping("/hotel/comment")
public class HotelCommentController extends BaseController
{
    @Autowired
    private IHotelCommentService hotelCommentService;

    @Autowired
    private IHotelInfoService hotelInfoService;

    /**
     * 查询酒店评价列表
     */
//    @PreAuthorize("@ss.hasPermi('hotel:comment:list')")
    @GetMapping("/list")
//    public TableDataInfo list(HotelComment hotelComment)
//    {
//        startPage();
//        List<HotelComment> list = hotelCommentService.selectHotelCommentList(hotelComment);
//        return getDataTable(list);
//    }

    public TableDataInfo list(HotelComment hotelComment)
    {
        startPage();
        List<HotelComment> list = hotelCommentService.selectHotelCommentList(hotelComment);
        TableDataInfo rspData = getDataTable(list);

        // ✨ 核心修改：如果查询的是某个酒店的评价，顺便把该酒店的最新评分查出来
        if (hotelComment.getHotelId() != null) {
            HotelInfo hotel = hotelInfoService.selectHotelInfoById(hotelComment.getHotelId());
            if (hotel != null && hotel.getScore() != null) {
                // 将评分放入 AjaxResult 的扩展字段中（RuoYi 的 TableDataInfo 默认带 code, msg, rows, total）
                // 我们借用 msg 传递评分，或者封装一个更标准的返回类
                System.out.println("评分========="+hotel.getScore());
                rspData.setMsg(hotel.getScore().toString());
            }
        }
        return rspData;
    }

    /**
     * 导出酒店评价列表
     */
    @PreAuthorize("@ss.hasPermi('hotel:comment:export')")
    @Log(title = "酒店评价", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HotelComment hotelComment)
    {
        List<HotelComment> list = hotelCommentService.selectHotelCommentList(hotelComment);
        ExcelUtil<HotelComment> util = new ExcelUtil<HotelComment>(HotelComment.class);
        util.exportExcel(response, list, "酒店评价数据");
    }

    /**
     * 获取酒店评价详细信息
     */
//    @PreAuthorize("@ss.hasPermi('hotel:comment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(hotelCommentService.selectHotelCommentById(id));
    }

    /**
     * 新增酒店评价
     */
//    @PreAuthorize("@ss.hasPermi('hotel:comment:add')")
    @Log(title = "酒店评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HotelComment hotelComment)
    {
        return toAjax(hotelCommentService.insertHotelComment(hotelComment));
    }

    /**
     * 修改酒店评价
     */
//    @PreAuthorize("@ss.hasPermi('hotel:comment:edit')")
    @Log(title = "酒店评价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HotelComment hotelComment)
    {
        return toAjax(hotelCommentService.updateHotelComment(hotelComment));
    }

    /**
     * 删除酒店评价
     */
//    @PreAuthorize("@ss.hasPermi('hotel:comment:remove')")
    @Log(title = "酒店评价", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hotelCommentService.deleteHotelCommentByIds(ids));
    }
}
