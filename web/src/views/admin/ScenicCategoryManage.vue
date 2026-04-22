<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('categoryManage.categoryName')" prop="name">
            <el-input v-model="searchForm.name" clearable></el-input>
          </el-form-item>

          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('categoryManage.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('categoryManage.reset') }}</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">{{ $t('categoryManage.add') }}</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">{{ $t('categoryManage.batchDelete') }}</el-button>
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
          <el-table-column prop="name" :label="$t('categoryManage.categoryName')" show-overflow-tooltip></el-table-column>
          <el-table-column prop="createTime" :label="$t('categoryManage.createTime')" width="200"></el-table-column>
          <el-table-column fixed="right" :label="$t('categoryManage.operation')" width="200">
            <template #default="scope">
              <el-button size="small" :icon="Edit" @click="edit(scope.$index, scope.row)">{{ $t('categoryManage.edit') }}</el-button>
              <el-button size="small" :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">{{ $t('categoryManage.delete') }}</el-button>
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
        :title="formData.id ? $t('categoryManage.editTitle') : $t('categoryManage.addTitle')"
        width="500"
    >
      <el-form ref="formRef" :model="formData" label-width="100px">
        <slot name="content">
          <el-form-item :label="$t('categoryManage.categoryNameLabel')" prop="name"  :rules="[{required:true,message: $t('categoryManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.name"></el-input>
          </el-form-item>

        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">{{ $t('categoryManage.submit') }}</el-button>
          <el-button @click="closeDialog" :icon="Close">{{ $t('categoryManage.cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

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
  name: undefined,

});



getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/scenicCategory/page", {
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
      request.post("/scenicCategory/add", formData.value).then(res => {
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
      request.put("/scenicCategory/update", formData.value).then(res => {
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
    request.delete("/scenicCategory/delBatch", {data: ids}).then(res => {
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
</script>

<style scoped>

</style>
