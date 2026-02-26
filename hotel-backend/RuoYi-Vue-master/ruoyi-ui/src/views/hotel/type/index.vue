<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属酒店" prop="hotelId">
        <el-select
          v-model="queryParams.hotelId"
          placeholder="请选择酒店"
          clearable
          size="small"
          @change="handleQuery"
        >
          <el-option
            v-for="item in hotelOptions"
            :key="item.id"
            :label="item.nameCn"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="房型名称" prop="typeName">
        <el-input
          v-model="queryParams.typeName"
          placeholder="请输入房型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="基准价格" prop="price">
        <el-input
          v-model="queryParams.price"
          placeholder="请输入基准价格"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否有窗" prop="hasWindow">
        <el-select v-model="queryParams.hasWindow" placeholder="请选择" clearable @change="handleQuery">
          <el-option label="有窗" value="1" />
          <el-option label="无窗" value="0" />
        </el-select>
      </el-form-item>

      <el-form-item label="是否含早" prop="hasBreakfast">
        <el-select v-model="queryParams.hasBreakfast" placeholder="请选择" clearable @change="handleQuery">
          <el-option label="含早" value="1" />
          <el-option label="不含早" value="0" />
        </el-select>
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
          v-hasPermi="['hotel:type:add']"
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
          v-hasPermi="['hotel:type:edit']"
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
          v-hasPermi="['hotel:type:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['hotel:type:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="房型ID" align="center" prop="id" />
      <el-table-column label="所属酒店" align="center">
        <template slot-scope="scope">
          {{ getHotelName(scope.row.hotelId) }}
        </template>
      </el-table-column>
      <el-table-column label="房型名称" align="center" prop="typeName" />
      <el-table-column label="房型大类" align="center" prop="roomCategory">
        <template slot-scope="scope">
          <el-tag>
            {{ getCategoryLabel(scope.row.roomCategory) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="基准价格" align="center" prop="price" />
      <el-table-column label="床型" align="center" prop="bedType" />
      <el-table-column label="是否有窗" align="center" prop="hasWindow">
        <template slot-scope="scope">
          <el-tag :type="scope.row.hasWindow === '1' ? 'success' : 'danger'">
            {{ scope.row.hasWindow === '1' ? '有窗' : '无窗' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否含早" align="center" prop="hasBreakfast">
        <template slot-scope="scope">
          <el-tag :type="scope.row.hasBreakfast === '1' ? 'success' : 'danger'">
            {{ scope.row.hasBreakfast === '1' ? '含早' : '不含早' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="房型图片" align="center" prop="imageUrl" width="100">
        <template slot-scope="scope">
          <el-image
            style="width: 60px; height: 40px"
            :src="baseUrl + (scope.row.imageUrl ? scope.row.imageUrl.split(',')[0] : '')"
            :preview-src-list="getPreviewList(scope.row.imageUrl)"
            fit="cover"
          >
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="房型详情描述" align="center" prop="remark" />
      <el-table-column label="状态(0正常 1停售)" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['hotel:type:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['hotel:type:remove']"
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

    <!-- 添加或修改房型对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属酒店" prop="hotelId">
          <el-select
            v-model="form.hotelId"
            placeholder="请选择酒店"
            clearable
            size="small"
            @change="handleQuery"
          >
            <el-option
              v-for="item in hotelOptions"
              :key="item.id"
              :label="item.nameCn"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房型名称(如:标准双床房)" prop="typeName">
          <el-input v-model="form.typeName" placeholder="请输入房型名称(如:标准双床房)" />
        </el-form-item>
        <el-form-item label="基准价格" prop="price">
          <el-input v-model="form.price" placeholder="请输入基准价格" />
        </el-form-item>
        <el-form-item label="房型大类" prop="roomCategory">
          <el-select v-model="form.roomCategory" placeholder="请选择房型分类" @change="handleCategoryChange">
            <el-option label="单人房" value="SINGLE" />
            <el-option label="大床房" value="KING" />
            <el-option label="双人房/双床房" value="TWIN" />
            <el-option label="三人房/家庭房" value="FAMILY" />
            <el-option label="套房" value="SUITE" />
          </el-select>
          <div class="help-block" style="color: #999; font-size: 12px; line-height: 1.5;">
            * 此处选择将决定小程序端筛选按钮的分类
          </div>
        </el-form-item>

        <el-form-item label="床型明细">
          <div v-for="(item, index) in bedConfigs" :key="index" style="margin-bottom: 10px; display: flex; align-items: center;">
            <el-select v-model="item.type" placeholder="规格" style="width: 160px; margin-right: 10px">
              <el-option label="大床(1.8m)" value="大床(1.8m)" />
              <el-option label="双人床(1.5m)" value="双人床(1.5m)" />
              <el-option label="单人床(1.2m)" value="单人床(1.2m)" />
              <el-option label="沙发床" value="沙发床" />
            </el-select>
            <el-input-number v-model="item.count" :min="1" :max="5" style="width: 120px; margin-right: 10px" />
            <span style="margin-right: 10px">张</span>
            <el-button v-if="bedConfigs.length > 1" type="text" icon="el-icon-delete" @click="removeBedConfig(index)">删除</el-button>
          </div>
          <el-button type="text" icon="el-icon-plus" @click="addBedConfig">添加组合床型</el-button>
        </el-form-item>
        <el-form-item label="是否有窗" prop="hasWindow">
          <el-select v-model="form.hasWindow" placeholder="请选择" clearable @change="handleQuery">
            <el-option label="有窗" value="1" />
            <el-option label="无窗" value="0" />
          </el-select>
        </el-form-item>

        <el-form-item label="是否含早" prop="hasBreakfast">
          <el-select v-model="form.hasBreakfast" placeholder="请选择" clearable @change="handleQuery">
            <el-option label="含早" value="1" />
            <el-option label="不含早" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="房型图片" prop="imageUrl">
          <image-upload v-model="form.imageUrl"/>
        </el-form-item>
        <el-form-item label="房型详情描述" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listType, getType, delType, addType, updateType } from "@/api/hotel/type"
import { listHotel } from "@/api/hotel/info";
export default {
  name: "Type",
  data() {
    return {
      // 遮罩层
      bedConfigs: [
        { type: '大床(1.8m)', count: 1 }
      ],
      loading: true,
      baseUrl: process.env.VUE_APP_BASE_API,
      hotelOptions: [],
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
      // 房型表格数据
      typeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        hotelId: null,
        typeName: null,
        price: null,
        bedType: null,
        hasWindow: null,
        roomCategory: null,
        hasBreakfast: null,
        imageUrl: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        hotelId: [
          { required: true, message: "所属酒店不能为空", trigger: "change" }
        ],
        roomCategory: [
          { required: true, message: "房型分类不能为空", trigger: "change" }
        ],
        typeName: [
          { required: true, message: "房型名称(如:标准双床房)不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList();
    this.getHotelList();
  },
  methods: {
    getHotelName(id) {
      const target = this.hotelOptions.find(item => String(item.id) === String(id));
      return target ? target.nameCn : id;
    },
    getCategoryLabel(category) {
      const map = {
        'SINGLE': '单人房',
        'KING': '大床房',
        'TWIN': '双人房/双床房',
        'FAMILY': '三人房/家庭房',
        'SUITE': '套房'
      };
      return map[category] || '未知';
    },
    getCategoryTag(category) {
      const map = {
        'SINGLE': 'info',
        'KING': 'success',
        'TWIN': '',
        'FAMILY': 'warning',
        'SUITE': 'danger'
      };
      return map[category] || 'info';
    },
    handleCategoryChange(val) {
      if (val === 'SINGLE') {
        this.bedConfigs = [{ type: '单人床(1.2m)', count: 1 }];
      } else if (val === 'KING') {
        this.bedConfigs = [{ type: '大床(1.8m)', count: 1 }];
      } else if (val === 'TWIN') {
        this.bedConfigs = [{ type: '单人床(1.2m)', count: 2 }];
      }
    },
    addBedConfig() {
      this.bedConfigs.push({ type: '', count: 1 });
    },
    /** 删除一种床型组合 */
    removeBedConfig(index) {
      this.bedConfigs.splice(index, 1);
    },
    getPreviewList(url) {
      if (!url) return [];
      // 1. 按逗号分割成数组
      // 2. 给每个路径加上 baseUrl 前缀
      return url.split(',').map(item => this.baseUrl + item);
    },
    getHotelList() {
      listHotel().then(response => {
        this.hotelOptions = response.rows;
      });
    },
    /** 查询房型列表 */
    getList() {
      this.loading = true
      listType(this.queryParams).then(response => {
        this.typeList = response.rows
        console.log(this.typeList)
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        hotelId: null,
        typeName: null,
        price: null,
        bedType: null,
        hasWindow: null,
        hasBreakfast: null,
        imageUrl: null,
        roomCategory: null,
        remark: null,
        status: null,
        createBy: null,
        createTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加房型"
    },
    /** 修改按钮操作 */
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getType(id).then(response => {
        this.form = response.data;

        // --- 完善解析逻辑开始 ---
        if (this.form.bedType) {
          // 1. 先按 " + " 分割成多条床型记录
          const parts = this.form.bedType.split(' + ');

          // 2. 将每条记录解析回 { type: 'xxx', count: n }
          this.bedConfigs = parts.map(part => {
            // 利用正则匹配：数字 + "张" + 规格
            // 比如 "2张单人床(1.2m)" -> count: 2, type: "单人床(1.2m)"
            const match = part.match(/^(\d+)张(.+)$/);
            if (match) {
              return {
                count: parseInt(match[1]),
                type: match[2]
              };
            }
            // 如果不匹配正则（防报错），则作为一条默认记录
            return { count: 1, type: part };
          });
        } else {
          // 如果字段为空，给一个默认配置
          this.bedConfigs = [{ type: '大床(1.8m)', count: 1 }];
        }
        // --- 完善解析逻辑结束 ---

        this.open = true;
        this.title = "修改房型";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.form.bedType = this.bedConfigs
        .map(item => `${item.count}张${item.type}`)
        .join(' + ');
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateType(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addType(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除房型编号为"' + ids + '"的数据项？').then(function() {
        return delType(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('hotel/type/export', {
        ...this.queryParams
      }, `type_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
