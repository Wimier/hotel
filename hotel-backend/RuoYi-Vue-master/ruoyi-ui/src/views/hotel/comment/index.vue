<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="订单编号" prop="orderSn">
        <el-input v-model="queryParams.orderSn" placeholder="请输入订单编号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="酒店ID" prop="hotelId">
        <el-input v-model="queryParams.hotelId" placeholder="请输入酒店ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="评分星数" prop="score">
        <el-select v-model="queryParams.score" placeholder="请选择评分" clearable>
          <el-option v-for="i in 5" :key="i" :label="i + '星'" :value="i" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="显示状态" clearable>
          <el-option label="显示" :value="1" />
          <el-option label="隐藏" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['hotel:comment:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['hotel:comment:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评价ID" align="center" prop="id" width="80" />
      <el-table-column label="订单编号" align="center" prop="orderSn" width="150" />
      <el-table-column label="用户ID" align="center" prop="userId" width="80" />

      <el-table-column label="评分" align="center" prop="score" width="140">
        <template slot-scope="scope">
          <el-rate v-model="scope.row.score" disabled text-color="#ff9900" />
        </template>
      </el-table-column>

      <el-table-column label="评论内容" align="center" prop="content" :show-overflow-tooltip="true" />

      <el-table-column label="评价图片" align="center" prop="images" width="120">
        <template slot-scope="scope">
          <image-preview :src="scope.row.images" :width="100" :height="50"/>
        </template>
      </el-table-column>

      <el-table-column label="匿名" align="center" prop="isAnonymous" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.isAnonymous === 1 ? 'info' : 'success'">
            {{ scope.row.isAnonymous === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="商家回复" align="center" prop="replyContent">
        <template slot-scope="scope">
          <span v-if="scope.row.replyContent" style="color: #67C23A;">已回复</span>
          <span v-else style="color: #F56C6C;">待回复</span>
        </template>
      </el-table-column>

      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" />
        </template>
      </el-table-column>

      <el-table-column label="评价时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-chat-line-round" @click="handleUpdate(scope.row)" v-hasPermi="['hotel:comment:edit']">
            {{ scope.row.replyContent ? '查看/修改回复' : '回复' }}
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['hotel:comment:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-divider content-position="left">客户评价详情</el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="订单编号">{{ form.orderSn }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评分">
              <el-rate v-model="form.score" disabled style="margin-top: 10px;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="评价内容">
          <div class="comment-content-box">{{ form.content }}</div>
        </el-form-item>
        <el-form-item label="评价图片">
          <image-preview :src="form.images" :width="100" :height="100"/>
        </el-form-item>

        <el-divider content-position="left">商家回复</el-divider>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input v-model="form.replyContent" type="textarea" :rows="4" placeholder="请输入您的回复内容，友善的回复能提升酒店形象哦~" />
        </el-form-item>
        <el-form-item v-if="form.replyTime" label="最近回复">
          <el-tag type="info">{{ parseTime(form.replyTime) }}</el-tag>
        </el-form-item>

        <el-divider content-position="left">管理操作</el-divider>
        <el-form-item label="显示状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">前端显示</el-radio>
            <el-radio :label="0">前端隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">提 交 回 复</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
//
import { listComment, getComment, delComment, addComment, updateComment } from "@/api/hotel/comment"

export default {
  name: "Comment",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      commentList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderSn: null,
        hotelId: null,
        score: null,
        status: null,
      },
      form: {},
      rules: {
        replyContent: [
          { required: true, message: "回复内容不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listComment(this.queryParams).then(response => {
        this.commentList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // ✨ 状态快速切换
    handleStatusChange(row) {
      let text = row.status === 1 ? "显示" : "隐藏";
      this.$modal.confirm('确认要"' + text + '"这条评价吗？').then(function() {
        return updateComment({ id: row.id, status: row.status });
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === 1 ? 0 : 1;
      });
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        orderId: null,
        orderSn: null,
        userId: null,
        hotelId: null,
        roomTypeId: null,
        score: null,
        content: null,
        images: null,
        isAnonymous: null,
        replyContent: null,
        replyTime: null,
        status: 1
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
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getComment(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = this.form.replyContent ? "修改商家回复" : "回复客户评价"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 在后端逻辑中，更新操作会自动设置回复时间
          updateComment(this.form).then(response => {
            this.$modal.msgSuccess("回复成功")
            this.open = false
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除评价编号为"' + ids + '"的数据项？').then(function() {
        return delComment(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('hotel/comment/export', { ...this.queryParams }, `comment_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
/* ✨ 自定义样式让界面更精美 */
.comment-content-box {
  background-color: #f8f9fa;
  padding: 10px 15px;
  border-radius: 4px;
  border-left: 4px solid #dcdfe6;
  color: #606266;
  line-height: 1.6;
}
</style>
