<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>


        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('userManage.username')" prop="username">
            <el-input v-model="searchForm.username" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('userManage.phone')" prop="phone">
            <el-input v-model="searchForm.phone" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('userManage.status')" prop="status">
            <el-select v-model="searchForm.status" :placeholder="$t('userManage.pleaseSelect')" clearable style="width: 150px">
              <el-option :label="$t('status.ACTIVE')" value="ACTIVE"/>
              <el-option :label="$t('status.INACTIVE')" value="INACTIVE"/>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('userManage.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('userManage.reset') }}</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">{{ $t('userManage.add') }}</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">{{ $t('userManage.batchDelete') }}</el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-table ref="tableComponents"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%"
                  @selection-change="selectionChange"
                  border>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="username" :label="$t('userManage.userName')"></el-table-column>
          <el-table-column :label="$t('userManage.userAvatar')">
            <template #default="scope">
              <el-image style="width: 50px; height: 50px" :src="scope.row.avatarUrl"
                        :preview-src-list="[scope.row.avatarUrl]"
                        :preview-teleported="true"></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="phone" :label="$t('userManage.telephone')"></el-table-column>
          <el-table-column prop="email" :label="$t('userManage.email')"></el-table-column>
          <el-table-column prop="balance" :label="$t('userManage.accountBalance')" width="120">
            <template #default="scope">
              <el-tag type="success">¥{{ scope.row.balance || 0 }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" :label="$t('userManage.status')">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.status==='ACTIVE' || scope.row.status==='启用'">{{ $t('status.ACTIVE') }}</el-tag>
              <el-tag type="danger" v-else-if="scope.row.status==='INACTIVE' || scope.row.status==='禁用'">{{ $t('status.INACTIVE') }}</el-tag>
              <el-tag type="info" v-else>{{ $t('status.' + scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('userManage.advancedOperations')" width="260">
            <template #default="scope">
              <el-button type="primary" :icon="Money" @click="recharge(scope.row)" size="small">{{ $t('userManage.recharge') }}</el-button>
              <el-button type="success" :icon="RefreshLeft" @click="resetPassword(scope.row)" size="small">{{ $t('userManage.resetPassword') }}</el-button>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('userManage.operation')" width="200">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.$index, scope.row)">{{ $t('userManage.edit') }}</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">{{ $t('userManage.delete') }}</el-button>
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
        :title="formData.id ? $t('userManage.editTitle') : $t('userManage.addTitle')"
        width="800px"
    >
      <el-form ref="formRef" :model="formData" label-width="100px" inline>
        <slot name="content">
          <el-form-item :label="$t('userManage.avatar')" prop="avatarUrl" style="width: 100%"
                        :rules="[{required:true,message: $t('userManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl"
                      @setFiles="formData.avatarUrl =$event"></MyUpLoad>
          </el-form-item>
          <el-form-item :label="$t('userManage.usernameLabel')" prop="username"  :rules="[{required:true,message: $t('userManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.username"></el-input>
          </el-form-item>
          <el-form-item :label="$t('userManage.telephoneLabel')" prop="phone"  >
            <el-input v-model="formData.phone"></el-input>
          </el-form-item>
          <el-form-item :label="$t('userManage.emailLabel')" prop="email"  >
            <el-input v-model="formData.email"></el-input>
          </el-form-item>
          <el-form-item :label="$t('userManage.password')" prop="password" :rules="formData.id ? [] : [{required:true,message: $t('userManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input type="password" v-model="formData.password" :placeholder="formData.id ? $t('userManage.leaveEmptyIfNotChanging') : $t('userManage.passwordPlaceholder')" show-password></el-input>
          </el-form-item>
          <el-form-item :label="$t('userManage.statusLabel')" prop="status"
                        :rules="[{required:true,message: $t('userManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-radio-group v-model="formData.status">
              <el-radio label="ACTIVE">{{ $t('status.ACTIVE') }}</el-radio>
              <el-radio label="INACTIVE">{{ $t('status.INACTIVE') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">{{ $t('userManage.submit') }}</el-button>
          <el-button @click="closeDialog" :icon="Close">{{ $t('userManage.cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 充值对话框 -->
    <el-dialog
        v-model="rechargeDialogVisible"
        :title="$t('userManage.userRecharge')"
        width="500px"
    >
      <el-form :model="rechargeForm" label-width="100px">
        <el-form-item :label="$t('userManage.username')">
          <el-input v-model="rechargeForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item :label="$t('userManage.rechargeAmount')" required>
          <el-input-number 
            v-model="rechargeForm.amount" 
            :min="0.01" 
            :max="10000"
            :precision="2"
            :step="100"
            style="width: 100%"
          >
            <template #prefix>¥</template>
          </el-input-number>
        </el-form-item>
        <el-form-item :label="$t('userManage.remark')">
          <el-input 
            v-model="rechargeForm.remark" 
            type="textarea" 
            :rows="3"
            :placeholder="$t('userManage.remarkPlaceholder')"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitRecharge" :icon="Check">{{ $t('userManage.confirmRecharge') }}</el-button>
          <el-button @click="rechargeDialogVisible = false" :icon="Close">{{ $t('userManage.cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search, RefreshLeft, Money} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyUpLoad from "@/components/MyUpload.vue";

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
const searchForm = ref({
  username: undefined,
  phone: undefined,

});



getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/user/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
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
function edit(index, row) {
  formData.value = Object.assign({}, row)
  dialogOpen.value = true
}
/**
 * 关闭弹框
 */
function closeDialog() {
  dialogOpen.value = false
}

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
      request.post("/user/add", formData.value).then(res => {
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
      request.put("/user/update", formData.value).then(res => {
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
function deleteOne(index, row) {
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
    request.delete("/user/delBatch", {data: ids}).then(res => {
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
 * 重置密码
 * @param row
 */
function resetPassword(row) {
  request.post("/common/resetPassword?type=USER&id=" + row.id).then(res => {
    if (!res) {
      return
    }
    ElMessage({
      message: "Операция выполнена успешно",
      type: 'success'
    });
  })
}

// 充值相关
const rechargeDialogVisible = ref(false)
const rechargeForm = ref({
  userId: null,
  username: '',
  amount: null,
  remark: ''
})

/**
 * 打开充值对话框
 */
function recharge(row) {
  rechargeForm.value = {
    userId: row.id,
    username: row.username,
    amount: null,
    remark: ''
  }
  rechargeDialogVisible.value = true
}

/**
 * 提交充值
 */
function submitRecharge() {
  if (!rechargeForm.value.amount || rechargeForm.value.amount <= 0) {
    ElMessage.warning('Пожалуйста, введите корректную сумму пополнения')
    return
  }
  
  ElMessageBox.confirm(
    `Подтвердить пополнение счёта пользователя ${rechargeForm.value.username} на сумму ¥${rechargeForm.value.amount}?`,
    'Подтверждение пополнения',
    {
      confirmButtonText: 'Подтвердить',
      cancelButtonText: 'Отмена',
      type: 'warning'
    }
  ).then(() => {
    request.post('/payment/adminRecharge', {
      userId: rechargeForm.value.userId,
      amount: rechargeForm.value.amount,
      remark: rechargeForm.value.remark
    }).then(res => {
      if (!res) {
        return
      }
      ElMessage.success('Пополнение выполнено успешно')
      rechargeDialogVisible.value = false
      getPageList()
    })
  }).catch(() => {
    ElMessage.info('Пополнение отменено')
  })
}
</script>

<style scoped>

</style>
