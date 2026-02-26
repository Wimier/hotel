<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :split="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增房间</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roomList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="房间号" align="center" prop="roomNo">
        <template slot-scope="scope">
          <el-tag effect="dark">{{ scope.row.roomNo }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="所属酒店" align="center">
        <template slot-scope="scope">
          {{ getHotelName(scope.row.hotelId) }}
        </template>
      </el-table-column>

      <el-table-column label="所属房型" align="center">
        <template slot-scope="scope">
          {{ getRoomTypeProperty(scope.row.roomTypeId, 'typeName') }}
        </template>
      </el-table-column>

      <el-table-column label="房型大类" align="center">
        <template slot-scope="scope">
          <el-tag size="small" type="primary">
            {{ getCategoryLabel(getRoomTypeProperty(scope.row.roomTypeId, 'roomCategory')) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="床型配置" align="center" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ getRoomTypeProperty(scope.row.roomTypeId, 'bedType') }}
        </template>
      </el-table-column>

      <el-table-column label="楼层" align="center" prop="floor" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '空闲' : '占用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属酒店" prop="hotelId">
          <el-select v-model="form.hotelId" placeholder="请选择酒店" @change="handleHotelChangeInForm">
            <el-option v-for="item in hotelOptions" :key="item.id" :label="item.nameCn" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属房型" prop="roomTypeId">
          <el-select v-model="form.roomTypeId" placeholder="请选择房型" :disabled="!form.hotelId">
            <el-option v-for="item in roomTypeOptions" :key="item.id" :label="item.typeName" :value="item.id" />
          </el-select>
          <div v-if="form.roomTypeId" style="margin-top:10px; font-size:12px; color:#999; background:#f4f4f5; padding:5px;">
            <div><i class="el-icon-collection-tag"></i> 分类：<el-tag size="mini" type="success">{{ getCategoryLabel(getRoomTypeProperty(form.roomTypeId, 'roomCategory')) }}</el-tag></div>
            <div><i class="el-icon-info"></i> 床型：<span style="font-weight: bold; color: #333;">{{ getRoomTypeProperty(form.roomTypeId, 'bedType') }}</span></div>
          </div>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="-1" :max="100" />
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
import { listRoom, getRoom, delRoom, addRoom, updateRoom } from "@/api/hotel/room";
import { listHotel } from "@/api/hotel/info";
import { listType } from "@/api/hotel/type"; // 房型接口

export default {
  name: "Room",
  data() {
    return {
      loading: true,
      showSearch: true,
      roomList: [],
      hotelOptions: [],      // 酒店列表
      roomTypeOptions: [],   // 联动房型列表（受酒店过滤）
      allRoomTypeOptions: [],// 全量房型对照表（用于表格显示）
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        hotelId: null,
        roomTypeId: null,
        roomNo: null
      },
      form: {},
      rules: {
        hotelId: [{ required: true, message: "所属酒店不能为空", trigger: "change" }],
        roomTypeId: [{ required: true, message: "房型不能为空", trigger: "change" }],
        roomNo: [{ required: true, message: "房间号不能为空", trigger: "blur" }]
      }
    };
  },
  created() {
    this.getList();
    this.getHotelList();
    this.getAllRoomTypes(); // 初始化拉取全量对照表
  },
  methods: {
    /** 查询房间列表 */
    getList() {
      this.loading = true;
      listRoom(this.queryParams).then(response => {
        this.roomList = response.rows;
        this.loading = false;
      });
    },
    /** 获取所有酒店 */
    getHotelList() {
      listHotel().then(response => {
        this.hotelOptions = response.rows;
      });
    },
    /** 获取全量房型用于表格翻译 */
    getAllRoomTypes() {
      listType().then(response => {
        this.allRoomTypeOptions = response.rows;
      });
    },
    /** 搜索栏：酒店切换联动房型 */
    handleHotelChange(val) {
      this.queryParams.roomTypeId = null;
      if (val) {
        listType({ hotelId: val }).then(response => {
          this.roomTypeOptions = response.rows;
        });
      } else {
        this.roomTypeOptions = [];
      }
      this.handleQuery();
    },
    /** 弹窗内：酒店切换联动房型 */
    handleHotelChangeInForm(val) {
      this.form.roomTypeId = null;
      if (val) {
        listType({ hotelId: val }).then(response => {
          this.roomTypeOptions = response.rows;
        });
      }
    },
    /** 核心翻译函数：根据ID获取房型任意属性 */
    getRoomTypeProperty(id, propName) {
      const target = this.allRoomTypeOptions.find(item => String(item.id) === String(id));
      return target ? target[propName] : '';
    },
    /** 翻译酒店名称 */
    getHotelName(id) {
      const target = this.hotelOptions.find(item => String(item.id) === String(id));
      return target ? target.nameCn : id;
    },
    /** 翻译分类中文 */
    getCategoryLabel(category) {
      const map = {
        'SINGLE': '单人房',
        'KING': '大床房',
        'TWIN': '双人房/双床房',
        'FAMILY': '三人房/家庭房',
        'SUITE': '套房'
      };
      return map[category] || category || '未设置';
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = { id: null, hotelId: null, roomTypeId: null, roomNo: null, floor: 1, status: "0" };
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
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加房间";
    },
    handleUpdate(row) {
      this.reset();
      // 修改时需要根据当前酒店ID先加载一次房型下拉列表，否则下拉框可能没选项
      listType({ hotelId: row.hotelId }).then(response => {
        this.roomTypeOptions = response.rows;
        getRoom(row.id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改房间";
        });
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateRoom(this.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addRoom(this.form).then(() => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const ids = row.id;
      this.$modal.confirm('是否确认删除房间号为"' + row.roomNo + '"的数据？').then(() => {
        return delRoom(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      });
    }
  }
};
</script>
