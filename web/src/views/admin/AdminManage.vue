<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('adminManage.username')" prop="username">
            <el-input v-model="searchForm.username" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('adminManage.phone')" prop="phone">
            <el-input v-model="searchForm.phone" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('adminManage.status')" prop="status">
            <el-select v-model="searchForm.status" :placeholder="$t('adminManage.pleaseSelect')" clearable style="width: 150px">
              <el-option :label="$t('status.ACTIVE')" value="ACTIVE"/>
              <el-option :label="$t('status.INACTIVE')" value="INACTIVE"/>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('adminManage.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('adminManage.reset') }}</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">{{ $t('adminManage.add') }}</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">
            {{ $t('adminManage.batchDelete') }}
          </el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-table ref="tableComponents"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%"
                  border
                  @selection-change="selectionChange">
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="username" :label="$t('adminManage.userName')" width="150" show-overflow-tooltip></el-table-column>
          <el-table-column :label="$t('adminManage.userAvatar')" width="100" align="center">
            <template #default="scope">
              <el-image style="width: 50px; height: 50px" :src="scope.row.avatarUrl"
                        :preview-src-list="[scope.row.avatarUrl]"
                        :preview-teleported="true"></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="phone" :label="$t('adminManage.telephone')" width="150"></el-table-column>
          <el-table-column prop="email" :label="$t('adminManage.email')" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column prop="status" :label="$t('adminManage.status')" width="120" align="center">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.status==='ACTIVE'">{{ $t('status.ACTIVE') }}</el-tag>
              <el-tag type="danger" v-if="scope.row.status==='INACTIVE'">{{ $t('status.INACTIVE') }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('adminManage.advancedOperations')" width="160">
            <template #default="scope">
              <el-button type="success" size="small" :icon="RefreshLeft" @click="resetPassword(scope.row)">{{ $t('adminManage.resetPassword') }}</el-button>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('adminManage.operation')" width="200">
            <template #default="scope">
              <el-button size="small" :icon="Edit" @click="edit(scope.$index, scope.row)">{{ $t('adminManage.edit') }}</el-button>
              <el-button size="small" :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">{{ $t('adminManage.delete') }}</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top: 20px">
          <el-pagination
              @current-change="currentChange"
              @size-change="sizeChange"
              :page-size="pageInfo.pageSize"
              :current-page="pageInfo.pageNum"
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
        :title="formData.id ? $t('adminManage.editTitle') : $t('adminManage.addTitle')"
        width="800px"
    >
      <el-form ref="formRef" :model="formData" label-width="100px" inline>
        <el-form-item :label="$t('adminManage.avatar')" prop="avatarUrl" style="width: 100%"
                      :rules="[{required:true,message: $t('adminManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
          <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl"
                    @setFiles="formData.avatarUrl =$event"></MyUpLoad>
        </el-form-item>
        <el-form-item :label="$t('adminManage.usernameLabel')" prop="username"
                      :rules="[{required:true,message: $t('adminManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
          <el-input v-model="formData.username" :placeholder="$t('adminManage.usernamePlaceholder')" :disabled="formData.id!=null"></el-input>
        </el-form-item>
        <el-form-item :label="$t('adminManage.telephoneLabel')" prop="phone"
                      :rules="[{required:true,message: $t('adminManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
          <el-input v-model="formData.phone" :placeholder="$t('adminManage.telephonePlaceholder')"></el-input>
        </el-form-item>
        <el-form-item :label="$t('adminManage.emailLabel')" prop="email"
                      :rules="[{required:true,message: $t('adminManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
          <el-input v-model="formData.email" :placeholder="$t('adminManage.emailPlaceholder')"></el-input>
        </el-form-item>
        <el-form-item :label="$t('adminManage.statusLabel')" prop="status"
                      :rules="[{required:true,message: $t('adminManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
          <el-radio-group v-model="formData.status">
            <el-radio label="ACTIVE">{{ $t('status.ACTIVE') }}</el-radio>
            <el-radio label="INACTIVE">{{ $t('status.INACTIVE') }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">{{ $t('adminManage.submit') }}</el-button>
          <el-button @click="closeDialog" :icon="Close">{{ $t('adminManage.cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search, RefreshLeft} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyUpLoad from "@/components/MyUpload.vue";
import MyEditor from "@/components/MyEditor.vue";

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
const searchForm = ref({});


getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/admin/page", {
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
  dialogOpen.value = true
  formData.value = {}
}

/**
 * 编辑
 * @param index
 * @param row
 */
function edit(index, row) {
  dialogOpen.value = true
  formData.value = Object.assign({}, row)
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
  //新增
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage({
        message: "Проверка не пройдена, проверьте обязательные поля!",
        type: 'warning'
      });
      return
    }
    if (!formData.value.id) {
      request.post("/admin/add", formData.value).then(res => {
        if (!res) {
          return
        }
        dialogOpen.value = false
        ElMessage({
          message: res.msg + " Пароль нового пользователя по умолчанию: 123456",
          type: 'success'
        });
        getPageList()
      })
    } else {
      //更新
      request.put("/admin/update", formData.value).then(res => {
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
    request.delete("/admin/delBatch", {data: ids}).then(res => {
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
  request.post("/common/resetPassword?type=ADMIN&id=" + row.id).then(res => {
    if (!res) {
      return
    }
    ElMessage({
      message: "Операция выполнена успешно",
      type: 'success'
    });
  })
}


</script>

<style scoped>

</style>
