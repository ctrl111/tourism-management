<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('orderManage.status')" prop="status">
            <el-select v-model="searchForm.status" :placeholder="$t('orderManage.pleaseSelect')" clearable filterable style="width: 150px">
              <el-option :label="item" :value="item" :key="item" v-for="item in statusList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('orderManage.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('orderManage.reset') }}</el-button>
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
          <el-table-column prop="userName" :label="$t('orderManage.user')" width="120" show-overflow-tooltip>
            <template #default="{row}">
              {{ row.user?.username }}
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" :label="$t('orderManage.orderNo')" min-width="220" show-overflow-tooltip></el-table-column>
          <el-table-column prop="scenicName" :label="$t('orderManage.scenic')" min-width="150" show-overflow-tooltip></el-table-column>
          <el-table-column prop="quantity" :label="$t('orderManage.quantity')" width="100"></el-table-column>
          <el-table-column prop="totalAmount" :label="$t('orderManage.totalAmount')" width="120"></el-table-column>
          <el-table-column prop="status" :label="$t('orderManage.paymentStatus')" width="140">
            <template #default="scope">
              <el-tag v-if="scope.row.status === 'PENDING'" type="warning">{{ $t('orderManage.pending') }}</el-tag>
              <el-tag v-else-if="scope.row.status === 'PAID'" type="success">{{ $t('orderManage.paid') }}</el-tag>
              <el-tag v-else-if="scope.row.status === 'CANCELLED'" type="info">{{ $t('orderManage.cancelled') }}</el-tag>
              <el-tag v-else-if="scope.row.status === 'REFUNDED'" type="danger">{{ $t('orderManage.refunded') }}</el-tag>
              <el-tag v-else type="info">{{ $t('status.' + scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="visitDate" :label="$t('orderManage.visitDate')" width="180"></el-table-column>
          <el-table-column prop="createTime" :label="$t('orderManage.createTime')" width="180"></el-table-column>
          <el-table-column fixed="right" :label="$t('orderManage.operation')" width="280">
            <template #default="scope">
              <el-button 
                :icon="Edit" 
                @click="edit(scope.$index, scope.row, $event)"
                size="small"
              >{{ $t('orderManage.edit') }}</el-button>
              <el-button 
                v-if="scope.row.status === 'PAID'"
                type="warning" 
                :icon="RefreshRight" 
                @click="refundOrder(scope.row, $event)"
                size="small"
              >{{ $t('orderManage.refund') }}</el-button>
              <el-button 
                :icon="Delete" 
                type="danger" 
                @click="deleteOne(scope.$index, scope.row, $event)"
                size="small"
              >{{ $t('orderManage.delete') }}</el-button>
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
          <el-form-item :label="$t('orderManage.orderNoLabel')" prop="orderNo"  :rules="[{required:true,message:$t('orderManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.orderNo" disabled></el-input>
          </el-form-item>
          <el-form-item :label="$t('orderManage.totalAmountLabel')" prop="totalAmount"  :rules="[{required:true,message:$t('orderManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.totalAmount"></el-input>
          </el-form-item>
          <el-form-item :label="$t('orderManage.paymentStatusLabel')" prop="status"  :rules="[{required:true,message:$t('orderManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.status"></el-input>
          </el-form-item>
          <el-form-item :label="$t('orderManage.visitDateLabel')" prop="visitDate"  :rules="[{required:true,message:$t('orderManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
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
        :title="$t('orderManage.orderDetails')"
        width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item :label="$t('orderManage.orderNoLabel')">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item :label="$t('orderManage.userName')">{{ currentOrder.user?.username }}</el-descriptions-item>
        <el-descriptions-item :label="$t('orderManage.scenicName')">{{ currentOrder.scenicName }}</el-descriptions-item>
        <el-descriptions-item :label="$t('orderManage.purchaseQuantity')">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item :label="$t('orderManage.totalAmountLabel')">{{ currentOrder.totalAmount }} ₽</el-descriptions-item>
        <el-descriptions-item :label="$t('orderManage.paymentStatusLabel')">
          <el-tag v-if="currentOrder.status === 'PENDING'" type="warning">{{ $t('orderManage.pending') }}</el-tag>
          <el-tag v-else-if="currentOrder.status === 'PAID'" type="success">{{ $t('orderManage.paid') }}</el-tag>
          <el-tag v-else-if="currentOrder.status === 'CANCELLED'" type="info">{{ $t('orderManage.cancelled') }}</el-tag>
          <el-tag v-else-if="currentOrder.status === 'REFUNDED'" type="danger">{{ $t('orderManage.refunded') }}</el-tag>
          <el-tag v-else type="info">{{ $t('status.' + currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('orderManage.visitDateLabel')">{{ currentOrder.visitDate }}</el-descriptions-item>
        <el-descriptions-item :label="$t('orderManage.createTime')">{{ currentOrder.createTime }}</el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('orderManage.close') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search, RefreshRight} from '@element-plus/icons-vue'
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
const statusList = ref(['PENDING', 'PAID', 'CANCELLED', 'REFUNDED'])
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
        message: "Проверка не пройдена, проверьте форму!",
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
          message: "Операция выполнена успешно",
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
          message: "Операция выполнена успешно",
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
  ElMessageBox.confirm(`Это действие навсегда удалит данные с ID [${ids}]. Продолжить?`, 'Подсказка', {
    confirmButtonText: 'Подтвердить',
    cancelButtonText: 'Отмена',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/orderInfo/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: "Операция выполнена успешно",
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: 'Удаление отменено'
    });
    tableComponents.value.clearSelection();
  });
}

/**
 * 退款订单
 */
function refundOrder(row, event) {
  event?.stopPropagation()
  
  ElMessageBox.confirm(
    `Подтвердить возврат заказа ${row.orderNo}? Сумма возврата ¥${row.totalAmount} будет возвращена на баланс пользователя.`,
    'Подтверждение возврата',
    {
      confirmButtonText: 'Подтвердить возврат',
      cancelButtonText: 'Отмена',
      type: 'warning'
    }
  ).then(() => {
    request.post('/payment/refund', null, {
      params: { orderNo: row.orderNo }
    }).then(res => {
      if (!res) {
        return
      }
      ElMessage.success('Возврат выполнен успешно')
      getPageList()
    })
  }).catch(() => {
    ElMessage.info('Возврат отменён')
  })
}
</script>

<style scoped>

</style>
