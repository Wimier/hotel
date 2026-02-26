<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="酒店" prop="hotelId">
        <el-select v-model="queryParams.hotelId" placeholder="选择酒店" clearable filterable size="small">
          <el-option
            v-for="item in hotelOptions"
            :key="item.id"
            :label="item.nameCn"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="广告状态" clearable>
          <el-option label="上线" :value="1" />
          <el-option label="下线" :value="0" />
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
          v-hasPermi="['system:banner:add']"
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
          v-hasPermi="['system:banner:edit']"
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
          v-hasPermi="['system:banner:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:banner:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bannerList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" width="80" />

      <el-table-column label="广告图片" align="center" prop="imgUrl" width="150">
        <template slot-scope="scope">
          <image-preview :src="scope.row.imgUrl" :width="120" :height="60"/>
        </template>
      </el-table-column>

      <el-table-column label="跳转酒店" align="center" prop="hotelId" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="100" />

      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '上线' : '下线' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:banner:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:banner:remove']"
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

    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">

        <el-form-item label="广告图片" prop="imgUrl">
          <image-upload v-model="form.imgUrl" :limit="1"/>
        </el-form-item>

        <el-form-item label="跳转酒店" prop="hotelId">
          <el-select v-model="form.hotelId" placeholder="请选择要跳转的酒店" filterable style="width: 100%">
            <el-option
              v-for="item in hotelOptions"
              :key="item.id"
              :label="item.nameCn"
              :value="item.id"
            >
              <span style="float: left">{{ item.nameCn }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">ID: {{ item.id }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="排序权重" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" controls-position="right" :min="0" placeholder="越小越靠前" />
        </el-form-item>

        <el-form-item label="展示状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上线展示</el-radio>
            <el-radio :label="0">下线隐藏</el-radio>
          </el-radio-group>
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
import { listBanner, getBanner, delBanner, addBanner, updateBanner } from "@/api/system/banner"
import { listHotel } from "@/api/hotel/info"
export default {
  name: "Banner",
  data() {
    return {
      hotelOptions: [], // ✨ 存储现有酒店列表
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      bannerList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        hotelId: null,
        status: null,
      },
      form: {},
      // ✨ 严格的表单校验规则
      rules: {
        imgUrl: [
          { required: true, message: "请上传广告图片", trigger: "change" }
        ],
        hotelId: [
          { required: true, message: "必须关联一个酒店ID", trigger: "blur" }
        ],
        sortOrder: [
          { required: true, message: "请输入排序号", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getHotelList();
  },
  methods: {
    getHotelList() {
      // 传一个较大的 pageSize 确保拿全，或者只查状态为正常的酒店
      listHotel({ pageNum: 1, pageSize: 100 }).then(response => {
        this.hotelOptions = response.rows;
      });
    },
    /** 查询列表 */
    getList() {
      this.loading = true
      listBanner(this.queryParams).then(response => {
        this.bannerList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        imgUrl: null,
        hotelId: null,
        sortOrder: 0,
        status: 1 // ✨ 默认上线
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加首页广告"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getBanner(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改首页广告"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateBanner(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addBanner(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除编号为"' + ids + '"的广告项？').then(function() {
        return delBanner(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('system/banner/export', {
        ...this.queryParams
      }, `banner_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
