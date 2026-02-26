<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="订单编号" prop="orderSn">
        <el-input v-model="queryParams.orderSn" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="所属酒店" prop="hotelId">
        <el-select v-model="queryParams.hotelId" placeholder="请选择酒店" clearable @change="handleHotelChange">
          <el-option v-for="item in hotelOptions" :key="item.id" :label="item.nameCn" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="房型" prop="roomTypeId">
        <el-select v-model="queryParams.roomTypeId" placeholder="请先选择酒店" clearable :disabled="!queryParams.hotelId">
          <el-option v-for="item in roomTypeOptions" :key="item.id" :label="item.typeName" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="入住人" prop="guestName">
        <el-input v-model="queryParams.guestName" placeholder="姓名/手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="入住日期" prop="checkInDate">
        <el-date-picker clearable
                        v-model="queryParams.checkInDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择入住日期"
                        @change="handleQuery">
        </el-date-picker>
      </el-form-item>

      <el-form-item label="退房日期" prop="checkOutDate">
        <el-date-picker clearable
                        v-model="queryParams.checkOutDate"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="请选择退房日期"
                        @change="handleQuery">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待支付" value="0" />
          <el-option label="待入住" value="1" />
          <el-option label="入住中" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已取消" value="4" />
          <el-option label="已退款" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['hotel:order:add']">代客下单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['hotel:order:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单编号" align="center" prop="orderSn" width="160">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false">{{ scope.row.orderSn }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="预订酒店" align="center" show-overflow-tooltip>
        <template slot-scope="scope">{{ getHotelName(scope.row.hotelId) }}</template>
      </el-table-column>
      <el-table-column label="房型" align="center" show-overflow-tooltip>
        <template slot-scope="scope">{{ getRoomTypeName(scope.row.roomTypeId) }}</template>
      </el-table-column>

      <el-table-column label="入住信息" align="center" width="200">
        <template slot-scope="scope">
          <div>{{ scope.row.checkInDate }} 至 {{ scope.row.checkOutDate }}</div>
          <div style="font-size: 12px; color: #909399;">共 {{ scope.row.totalDays }} 晚 | {{ scope.row.roomCount }} 间</div>
        </template>
      </el-table-column>

      <el-table-column label="入住人" align="center">
        <template slot-scope="scope">
          <div>{{ scope.row.guestName }}</div>
          <div style="font-size: 12px; color: #909399;">{{ scope.row.guestPhone }}</div>
        </template>
      </el-table-column>

      <el-table-column label="总金额" align="center" prop="totalAmount">
        <template slot-scope="scope">
          <span style="color: #ff4949; font-weight: bold;">¥ {{ scope.row.totalAmount }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="getStatusTag(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['hotel:order:edit']">详情/修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color: #f56c6c;" @click="handleDelete(scope.row)" v-hasPermi="['hotel:order:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="预订酒店" prop="hotelId">
              <el-select v-model="form.hotelId" placeholder="请选择酒店" @change="handleHotelChangeInForm" style="width: 100%;">
                <el-option v-for="item in hotelOptions" :key="item.id" :label="item.nameCn" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预订房型" prop="roomTypeId">
              <el-select v-model="form.roomTypeId" placeholder="请选择房型" :disabled="!form.hotelId" style="width: 100%;">
                <el-option v-for="item in roomTypeOptions" :key="item.id" :label="item.typeName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="入住日期" prop="checkInDate">
              <el-date-picker clearable v-model="form.checkInDate" type="date" value-format="yyyy-MM-dd" placeholder="入住日期" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退房日期" prop="checkOutDate">
              <el-date-picker clearable v-model="form.checkOutDate" type="date" value-format="yyyy-MM-dd" placeholder="退房日期" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="房间数量" prop="roomCount">
              <el-input-number v-model="form.roomCount" :min="1" :max="10" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="小程序用户ID (测试用)" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="入住人" prop="guestName">
              <el-input v-model="form.guestName" placeholder="姓名(多个逗号隔开)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="guestPhone">
              <el-input v-model="form.guestPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>

        <div v-if="form.id">
          <el-divider content-position="left">订单状态控制</el-divider>
          <el-row>
            <el-col :span="12">
              <el-form-item label="订单状态" prop="status">
                <el-select v-model="form.status" style="width: 100%;">
                  <el-option label="待支付" :value="0" />
                  <el-option label="待入住" :value="1" />
                  <el-option label="入住中" :value="2" />
                  <el-option label="已完成" :value="3" />
                  <el-option label="已取消" :value="4" />
                  <el-option label="已退款" :value="5" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="订单金额" prop="totalAmount">
                <el-input v-model="form.totalAmount" disabled />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-form-item label="客户备注" prop="remark">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入客户备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, delOrder, addOrder, updateOrder } from "@/api/hotel/order";
// 引入关联数据接口
import { listHotel } from "@/api/hotel/info";
import { listType } from "@/api/hotel/type";

export default {
  name: "Order",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      orderList: [],

      // 关联字典数据
      hotelOptions: [],
      allRoomTypeOptions: [],
      roomTypeOptions: [], // 联动用的

      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderSn: null,
        hotelId: null,
        roomTypeId: null,
        guestName: null,
        status: null,
      },
      form: {},
      rules: {
        hotelId: [{ required: true, message: "酒店不能为空", trigger: "change" }],
        roomTypeId: [{ required: true, message: "房型不能为空", trigger: "change" }],
        checkInDate: [{ required: true, message: "入住日期不能为空", trigger: "blur" }],
        checkOutDate: [{ required: true, message: "退房日期不能为空", trigger: "blur" }],
        guestName: [{ required: true, message: "入住人姓名不能为空", trigger: "blur" }],
        guestPhone: [{ required: true, message: "联系电话不能为空", trigger: "blur" }],
        userId: [{ required: true, message: "用户ID不能为空(后台代客下单暂用)", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getList();
    this.getHotelList();
    this.getAllRoomTypes();
  },
  methods: {
    getList() {
      this.loading = true;
      listOrder(this.queryParams).then(response => {
        this.orderList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取字典数据 */
    getHotelList() {
      listHotel().then(res => { this.hotelOptions = res.rows; });
    },
    getAllRoomTypes() {
      listType().then(res => { this.allRoomTypeOptions = res.rows; });
    },
    /** 搜索栏下拉联动 */
    handleHotelChange(val) {
      this.queryParams.roomTypeId = null;
      if (val) {
        listType({ hotelId: val }).then(res => { this.roomTypeOptions = res.rows; });
      } else {
        this.roomTypeOptions = [];
      }
      this.handleQuery();
    },
    /** 表单下拉联动 */
    handleHotelChangeInForm(val) {
      this.form.roomTypeId = null;
      if (val) {
        listType({ hotelId: val }).then(res => { this.roomTypeOptions = res.rows; });
      }
    },
    /** 表格翻译函数 */
    getHotelName(id) {
      const target = this.hotelOptions.find(item => String(item.id) === String(id));
      return target ? target.nameCn : id;
    },
    getRoomTypeName(id) {
      const target = this.allRoomTypeOptions.find(item => String(item.id) === String(id));
      return target ? target.typeName : id;
    },
    getStatusLabel(status) {
      const map = { 0: '待支付', 1: '待入住', 2: '入住中', 3: '已完成', 4: '已取消', 5: '已退款' };
      return map[status] || '未知';
    },
    getStatusTag(status) {
      const map = { 0: 'warning', 1: 'success', 2: '', 3: 'info', 4: 'danger', 5: 'info' };
      return map[status] || '';
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        id: null,
        userId: 1001, // 默认给个测试ID
        hotelId: null,
        roomTypeId: null,
        roomCount: 1, // 默认1间
        checkInDate: null,
        checkOutDate: null,
        guestName: null,
        guestPhone: null,
        remark: null
      };
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "后台代客下单";
    },
    handleUpdate(row) {
      this.reset();
      listType({ hotelId: row.hotelId }).then(res => {
        this.roomTypeOptions = res.rows;
        getOrder(row.id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改订单状态";
        });
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateOrder(this.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addOrder(this.form).then(() => {
              this.$modal.msgSuccess("下单成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除订单编号为"' + row.orderSn + '"的数据？').then(() => {
        return delOrder(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      });
    },
    handleExport() {
      this.download('hotel/order/export', { ...this.queryParams }, `order_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
