<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="状态" prop="status">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable filterable style="width: 150px">
              <el-option :label="item" :value="item" :key="item" v-for="item in statusList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <!--          <el-button type="primary" @click="add" :icon="Plus">新增</el-button>-->
          <!--          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">批量删除</el-button>-->
        </el-space>
      </el-card>
      <el-card>
        <el-table ref="tableComponents"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%"
                  @selection-change="selectionChange"
                  @row-click="showDetail"
                  border>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="50"></el-table-column>
          <el-table-column prop="userName" label="用户" width="120">
            <template #default="{row}">
              {{ row.user?.username }}
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="订单号" width="300"></el-table-column>
          <el-table-column prop="scenicName" label="景点" width="120"></el-table-column>
          <el-table-column prop="quantity" label="数量" width="120"></el-table-column>
          <el-table-column prop="totalAmount" label="订单总金额" width="120"></el-table-column>
          <el-table-column prop="status" label="支付状态" width="120"></el-table-column>
          <el-table-column prop="visitDate" label="预定日期" width="180"></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
          <el-table-column fixed="right" label="操作" width="200">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.$index, scope.row,$event)">编辑</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row,scope.event)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px">
          <el-pagination
              @current-change="currentChange"
              @size-change="sizeChange"
              :page-size="pageInfo.pageSize"
              :current-page="pageInfo.currentPage"
              background
              layout="total,sizes, prev, pager, next"
              :total="pageInfo.total">
          </el-pagination>
        </div>
      </el-card>
    </el-space>
    <el-dialog
        v-model="dialogOpen"
        v-if="dialogOpen"
        :title="formData.id?'编辑':'新增'"
        width="500"
    >
      <el-form ref="formRef" :model="formData" label-width="100px">
        <slot name="content">
          <el-form-item label="订单号" prop="orderNo"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.orderNo" disabled></el-input>
          </el-form-item>
          <el-form-item label="订单总金额" prop="totalAmount"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.totalAmount"></el-input>
          </el-form-item>
          <el-form-item label="支付状态" prop="status"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.status"></el-input>
          </el-form-item>
          <el-form-item label="预定日期" prop="visitDate"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-date-picker
                v-model="formData.visitDate"
                type="date"
                placeholder="选择游玩日期"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
            />
          </el-form-item>

        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">提交</el-button>
          <el-button @click="closeDialog" :icon="Close">取消</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
        v-model="detailDialogVisible"
        title="订单详情"
        width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户姓名">{{ currentOrder.user?.username }}</el-descriptions-item>
        <el-descriptions-item label="景点名称">{{ currentOrder.scenicName }}</el-descriptions-item>
        <el-descriptions-item label="购买数量">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ currentOrder.totalAmount }}元</el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="currentOrder.status === '已支付' ? 'success' : 'danger'">
            {{ currentOrder.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预定日期">{{ currentOrder.visitDate }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
        <el-descriptions-item label="订单明细" :span="2">
          <!-- 添加空状态处理 -->
          <el-table
              :data="currentOrder?.orderItemList"
              border
              style="width: 100%; margin-top: 10px"
              v-loading="detailLoading"
          >
            <!-- 列定义保持不变 -->
            <el-table-column prop="scenicName" label="景点" width="200">
              <template #default="{row}">{{ row.scenicName }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80">
              <template #default="{row}">{{ row.quantity || 0 }}张</template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="100">
              <template #default="{row}">
                ￥{{ (Number(row.price) || 0).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="小计" width="120">
              <template #default="{row}">
                ￥{{ ((Number(row.price) || 0) * (Number(row.quantity) || 0)).toFixed(2) }}
              </template>
            </el-table-column>
            <!-- 空状态 -->
            <template #empty>
              <div class="empty-tip">暂无订单明细数据</div>
            </template>
          </el-table>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

const detailDialogVisible = ref(false);
const currentOrder = ref({});
const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const pageInfo = ref({
  //当前页
  pageNum: 1,
  //分页大小
  pageSize: 10,
  //总条数
  total: 0
});
const statusList = ref(['已支付', '未支付', '支付失败'])
const searchForm = ref({

});



getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/orderInfo/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

function disabledDate(time) {
  const today = new Date();
  // 设置时间为当天0点，以便比较日期而不是具体时间
  today.setHours(0, 0, 0, 0);
  return time < today;
}
/**
 * 选择分页
 * @param e
 */
function currentChange(e) {
  pageInfo.value.pageNum = e
  getPageList()
}

/**
 * 改变分页数量
 * @param e
 */
function sizeChange(e) {
  pageInfo.value.pageSize = e
  getPageList()
  console.log(e)
}

/**
 * 搜索
 */
function search() {
  pageInfo.value.pageNum = 1
  getPageList()
}

/**
 * 重置搜索框
 */
function resetSearch() {
  searchFormComponents.value.resetFields();
  getPageList()
}

const dialogOpen = ref(false);
const formData = ref({});
const formRef = ref();

/**
 * 新增
 */
function add() {
  formData.value = {}
  dialogOpen.value = true
}
/**
 * 编辑
 * @param index
 * @param row
 */
function edit(index, row, event) {
  event?.stopPropagation();
  formData.value = Object.assign({}, row)
  dialogOpen.value = true
}
/**
 * 关闭弹框
 */
function closeDialog() {
  dialogOpen.value = false
}

// 显示详情方法
function showDetail(row) {
  currentOrder.value = { ...row };
  detailDialogVisible.value = true;
};
/**
 * 提交数据
 */
function submit() {
  formRef.value.validate((valid) => {
    if (!valid){
      ElMessage({
        message: "验证失败，请检查表单!",
        type: 'warning'
      });
      return
    }
    //新增
    if (!formData.value.id) {
      request.post("/orderInfo/add", formData.value).then(res => {
        if (!res) {
          return
        }
        dialogOpen.value = false
        ElMessage({
          message: "操作成功",
          type: 'success'
        });
        getPageList()
      })
    } else {
      //更新
      request.put("/orderInfo/update", formData.value).then(res => {
        if (!res) {
          return
        }
        dialogOpen.value = false
        ElMessage({
          message: "操作成功",
          type: 'success'
        });
        getPageList()
      })
    }
  })
}
const selectionRows = ref([]);

/**
 * 多选
 * @param rows
 */
function selectionChange(rows) {
  selectionRows.value = rows
}

/**
 * 单个删除
 * @param index
 * @param row
 */
function deleteOne(index, row,event) {
  event?.stopPropagation();
  batchDelete([row])
}

/**
 * 批量删除
 * @param rows
 */
function batchDelete(rows) {
  if (!rows) {
    rows = selectionRows.value;
  }
  let ids = rows.map(item => item.id);
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/orderInfo/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: "操作成功",
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除'
    });
    tableComponents.value.clearSelection();
  });
}
</script>

<style scoped>

</style>
