<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="酒店名称" prop="nameCn">
        <el-input
          v-model="queryParams.nameCn"
          placeholder="请输入酒店名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="酒店状态" clearable>
          <el-option label="审核中" value="AUDITING" />
          <el-option label="已通过" value="PASS" />
          <el-option label="未通过" value="REJECT" />
          <el-option label="已下线" value="OFFLINE" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['hotel:info:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['hotel:info:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['hotel:info:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="hotelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="酒店封面" align="center" prop="bannerUrl" width="120">
        <template slot-scope="scope">
          <el-image
            style="width: 80px; height: 50px;border-radius: 5%"
            :src="baseUrl + scope.row.bannerUrl"
            :preview-src-list="[baseUrl + scope.row.bannerUrl]"
          >
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="酒店名称" align="center" prop="nameCn" />
      <el-table-column label="评分" align="center" prop="starRating">
        <template slot-scope="scope">
          <el-rate v-model="scope.row.starRating" disabled />
        </template>
      </el-table-column>
      <el-table-column label="价格" align="center" prop="price" />
      <el-table-column label="开业时间" align="center" prop="openingDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.openingDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="statusType(scope.row.status)">{{ statusFormat(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['hotel:info:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.status === 'AUDITING'"
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleAudit(scope.row)"
            v-hasPermi="['hotel:info:audit']"
          >审核</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['hotel:info:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="酒店名称" prop="nameCn">
          <el-input v-model="form.nameCn" placeholder="请输入酒店名称" />
        </el-form-item>
        <el-form-item label="酒店名称" prop="nameEn">
          <el-input v-model="form.nameEn" placeholder="请输入酒店名称" />
        </el-form-item>
        <el-form-item label="酒店封面">
          <image-upload v-model="form.bannerUrl" :limit="1" />
        </el-form-item>
        <el-form-item label="星级评分">
          <el-rate v-model="form.starRating" style="margin-top:10px" />
        </el-form-item>
        <el-form-item label="开业时间" prop="openingDate">
          <el-date-picker
            clearable
            v-model="form.openingDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="选择开业时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="0.1" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="停车" prop="park">
          <el-radio-group v-model="form.park">
            <el-radio label="免费">免费</el-radio>
            <el-radio label="收费">收费</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="wifi" prop="hasWifi">
          <el-radio-group v-model="form.hasWifi">
            <el-radio :label="1">有</el-radio>
            <el-radio :label="0">无</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电梯" prop="hasElevator">
          <el-radio-group v-model="form.hasElevator">
            <el-radio :label="1">有</el-radio>
            <el-radio :label="0">无</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" placeholder="请输入详细地址，或点击右侧地图选点">
            <el-button slot="append" icon="el-icon-location-information" @click="handleOpenMap">地图选点</el-button>
          </el-input>
        </el-form-item>

        <el-form-item label="省市区" v-if="form.province">
          <el-row :gutter="10">
            <el-col :span="8"><el-input v-model="form.province" readonly placeholder="省" /></el-col>
            <el-col :span="8"><el-input v-model="form.city" readonly placeholder="市" /></el-col>
            <el-col :span="8"><el-input v-model="form.district" readonly placeholder="区" /></el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="经纬度" v-if="form.longitude">
          <el-row :gutter="10">
            <el-col :span="12"><el-input v-model="form.longitude" readonly placeholder="经度(Lng)" /></el-col>
            <el-col :span="12"><el-input v-model="form.latitude" readonly placeholder="纬度(Lat)" /></el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="腾讯地图选点" :visible.sync="mapOpen" width="800px" append-to-body @opened="initTencentMap">
      <div style="margin-bottom: 15px;">
        <el-input v-model="searchKeyword" placeholder="输入地点搜索..." @keyup.enter.native="searchMapLocation">
          <el-button slot="append" icon="el-icon-search" @click="searchMapLocation">搜索</el-button>
        </el-input>
      </div>
      <div id="tx-map-container" style="width: 100%; height: 400px; border-radius: 4px;"></div>

      <div slot="footer" class="dialog-footer">
        <span style="float: left; line-height: 32px; color: #666; font-size: 13px;">
          当前选中: {{ mapTempData.detailAddress || '请在地图上点击选择位置' }}
        </span>
        <el-button type="primary" @click="confirmMapSelection">确 定</el-button>
        <el-button @click="mapOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="审核处理" :visible.sync="auditOpen" width="400px" append-to-body>
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核状态">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="PASS">通过</el-radio>
            <el-radio label="REJECT">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="拒绝原因" v-if="auditForm.status === 'REJECT'">
          <el-input v-model="auditForm.rejectReason" type="textarea" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">提交</el-button>
        <el-button @click="auditOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listHotel, getHotel, delHotel, addHotel, updateHotel } from "@/api/hotel/info";

export default {
  name: "Hotel",
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 酒店表格数据
      hotelList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示审核弹出层
      auditOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nameCn: null,
        nameEn: null,
        status: null,
        openingDate: null,
      },
      // 表单参数
      form: {},
      // 审核表单
      auditForm: {},

      // === 地图相关变量 ===
      mapOpen: false,
      mapInstance: null,
      markerLayer: null,
      searchKeyword: "",
      mapTempData: {
        province: "",
        city: "",
        district: "",
        detailAddress: "",
        longitude: null,
        latitude: null
      },
      // ====================

      // 表单校验
      rules: {
        nameCn: [{ required: true, message: "酒店名称不能为空", trigger: "blur" }],
        price: [{ required: true, message: "价格不能为空", trigger: "blur" }],
        detailAddress: [{ required: true, message: "详细地址不能为空", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询酒店列表 */
    getList() {
      this.loading = true;
      listHotel(this.queryParams).then(response => {
        this.hotelList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 状态显示格式化 */
    statusFormat(status) {
      const map = { 'AUDITING': '审核中', 'PASS': '已通过', 'REJECT': '未通过', 'OFFLINE': '已下线' };
      return map[status] || '未知';
    },
    statusType(status) {
      const map = { 'AUDITING': 'info', 'PASS': 'success', 'REJECT': 'danger', 'OFFLINE': 'warning' };
      return map[status] || '';
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        nameCn: null,
        bannerUrl: null,
        starRating: 0,
        price: 0,
        park:null,
        hasWifi:null,
        hasElevator:null,
        province: null,      // 省
        city: null,          // 市
        district: null,      // 区
        detailAddress: null, // 详细地址
        longitude: null,     // 经度
        latitude: null,      // 纬度
        status: "AUDITING"
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加酒店信息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getHotel(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改酒店信息";
      });
    },
    /** 审核按钮操作 */
    handleAudit(row) {
      this.auditForm = {
        id: row.id,
        status: 'PASS',
        rejectReason: ''
      };
      this.auditOpen = true;
    },
    /** 提交审核 */
    submitAudit() {
      updateHotel(this.auditForm).then(response => {
        this.$modal.msgSuccess("审核处理成功");
        this.auditOpen = false;
        this.getList();
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateHotel(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addHotel(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除酒店编号为"' + ids + '"的数据项？').then(function() {
        return delHotel(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    // ======== 地图相关功能开始 ========

    // 打开地图弹窗
    handleOpenMap() {
      this.mapOpen = true;
      this.searchKeyword = "";
      this.mapTempData = { ...this.form }; // 将已有的地址回显到地图暂存数据中
    },

    // 确定地图选点
    confirmMapSelection() {
      if (!this.mapTempData.longitude) {
        this.$message.warning("请先在地图上选择一个位置");
        return;
      }
      // 将地图暂存数据赋给主表单
      this.form.province = this.mapTempData.province;
      this.form.city = this.mapTempData.city;
      this.form.district = this.mapTempData.district;
      this.form.detailAddress = this.mapTempData.detailAddress;
      this.form.longitude = this.mapTempData.longitude;
      this.form.latitude = this.mapTempData.latitude;

      this.mapOpen = false;
    },

    // 初始化腾讯地图
    initTencentMap() {
      if (typeof TMap === 'undefined') {
        this.$message.error("请先在index.html中引入腾讯地图SDK");
        return;
      }

      // 默认中心点 (如果有已有经纬度，则居中；否则默认北京)
      let centerLng = this.form.longitude || 116.397128;
      let centerLat = this.form.latitude || 39.916527;
      let centerPoint = new TMap.LatLng(centerLat, centerLng);

      // 如果地图已实例化，只重置中心点和标记
      if (this.mapInstance) {
        this.mapInstance.setCenter(centerPoint);
        this.updateMarker(centerPoint);
        return;
      }

      // 初始化地图实例
      this.mapInstance = new TMap.Map(document.getElementById('tx-map-container'), {
        center: centerPoint,
        zoom: 15,
      });

      // 初始化标记层
      this.markerLayer = new TMap.MultiMarker({
        map: this.mapInstance,
        geometries: []
      });

      // 如果已有经纬度，打个标记
      if (this.form.longitude) {
        this.updateMarker(centerPoint);
      }

      // 监听地图点击事件
      this.mapInstance.on("click", (evt) => {
        let lat = evt.latLng.getLat();
        let lng = evt.latLng.getLng();
        this.updateMarker(evt.latLng);
        this.reverseGeocode(lat, lng);
      });
    },

    // 更新地图上的红色标记点
    updateMarker(latLng) {
      this.markerLayer.setGeometries([
        { position: latLng, id: 'marker' }
      ]);
    },

    // 逆地址解析：将经纬度转换为 省、市、区、详细地址文字
    reverseGeocode(lat, lng) {
      const KEY = 'GNVBZ-Q5I3M-PGP64-637TW-NNHYH-NNFXX';
      const url = `https://apis.map.qq.com/ws/geocoder/v1/?location=${lat},${lng}&key=${KEY}&output=jsonp`;

      this.$jsonp(url).then(res => {
        if (res.status === 0) {
          const result = res.result;
          this.mapTempData.longitude = lng;
          this.mapTempData.latitude = lat;
          this.mapTempData.province = result.address_component.province;
          this.mapTempData.city = result.address_component.city;
          this.mapTempData.district = result.address_component.district;
          this.mapTempData.detailAddress = result.address;
        }
      }).catch(err => {
        this.$message.error("地址解析失败");
      });
    },

    // 关键字搜索：将搜索框文字转换为经纬度并在地图上定位
    searchMapLocation() {
      if (!this.searchKeyword) return;
      const KEY = 'GNVBZ-Q5I3M-PGP64-637TW-NNHYH-NNFXX';
      const url = `https://apis.map.qq.com/ws/place/v1/suggestion?keyword=${this.searchKeyword}&key=${KEY}&output=jsonp`;

      this.$jsonp(url).then(res => {
        if (res.status === 0 && res.data.length > 0) {
          // 默认取第一条搜索结果
          const location = res.data[0].location;
          const latLng = new TMap.LatLng(location.lat, location.lng);
          this.mapInstance.setCenter(latLng);
          this.updateMarker(latLng);
          // 解析出省市区详细信息
          this.reverseGeocode(location.lat, location.lng);
        } else {
          this.$message.warning("未搜索到相关地址");
        }
      });
    }
  }
};
</script>
