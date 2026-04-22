<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('scenicManage.scenicCategory')" prop="categoryType">
            <el-input v-model="searchForm.categoryType" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.scenicName')" prop="name">
            <el-input v-model="searchForm.name" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.status')" prop="status">
            <el-select v-model="searchForm.status" :placeholder="$t('scenicManage.pleaseSelect')" clearable filterable style="width: 150px">
              <el-option :label="$t('status.ACTIVE')" value="ACTIVE"></el-option>
              <el-option :label="$t('status.INACTIVE')" value="INACTIVE"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('scenicManage.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('scenicManage.reset') }}</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">{{ $t('scenicManage.add') }}</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">{{ $t('scenicManage.batchDelete') }}</el-button>
        </el-space>
      </el-card>
      <el-card>
        <div style="overflow-x: auto;">
          <el-table ref="tableComponents"
                    :data="listData"
                    tooltip-effect="dark"
                    style="min-width: 1400px;"
                    @selection-change="selectionChange"
                    border>
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column show-overflow-tooltip prop="categoryType" :label="$t('scenicManage.scenicCategory')" width="160"></el-table-column>
            <el-table-column show-overflow-tooltip prop="name" :label="$t('scenicManage.scenicName')" width="180"></el-table-column>
            <el-table-column prop="coverImage" :label="$t('scenicManage.coverImage')" width="100" align="center">
              <template #default="scope" >
                <el-image v-if="scope.row.coverImage" style="width: 60px; height: 60px; border-radius: 4px;" :src="scope.row.coverImage" :preview-src-list="[scope.row.coverImage]" :preview-teleported="true" fit="cover"></el-image>
              </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="address" :label="$t('scenicManage.address')" width="180"></el-table-column>
            <el-table-column show-overflow-tooltip prop="description" :label="$t('scenicManage.description')" width="200"></el-table-column>
            <el-table-column show-overflow-tooltip prop="openingHours" :label="$t('scenicManage.openingHours')" width="130"></el-table-column>
            <el-table-column show-overflow-tooltip prop="price" :label="$t('scenicManage.price')" width="100" align="right"></el-table-column>
            <el-table-column show-overflow-tooltip prop="stock" :label="$t('scenicManage.ticketStock')" width="100" align="right"></el-table-column>
            <el-table-column show-overflow-tooltip prop="status" :label="$t('scenicManage.status')" width="100" align="center">
              <template #default="scope">
                <el-tag type="success" v-if="scope.row.status==='ACTIVE' || scope.row.status==='ON_SALE' || scope.row.status==='上架'">{{ $t('status.ACTIVE') }}</el-tag>
                <el-tag type="danger" v-else-if="scope.row.status==='INACTIVE' || scope.row.status==='OFF_SALE' || scope.row.status==='下架'">{{ $t('status.INACTIVE') }}</el-tag>
                <el-tag type="info" v-else>{{ $t('status.' + scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column show-overflow-tooltip prop="createTime" :label="$t('scenicManage.createTime')" width="170"></el-table-column>
            <el-table-column fixed="right" :label="$t('scenicManage.operation')" width="200">
              <template #default="scope">
                <el-button size="small" :icon="Edit" @click="edit(scope.$index, scope.row)">{{ $t('scenicManage.edit') }}</el-button>
                <el-button size="small" :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">{{ $t('scenicManage.delete') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
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
        :title="formData.id ? $t('scenicManage.editTitle') : $t('scenicManage.addTitle')"
        width="800px"
    >
      <el-form ref="formRef" :model="formData" label-width="100px" inline>
        <slot name="content">
          <el-form-item :label="$t('scenicManage.scenicCategory')" prop="categoryId" :rules="[{required:true,message: $t('scenicManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-select v-model="formData.categoryId" :placeholder="$t('scenicManage.selectCategory')" clearable filterable >
              <el-option :label="item.name"  :value="item.id" :key="item" v-for="item in categoryList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.scenicNameLabel')" prop="name"  :rules="[{required:true,message: $t('scenicManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.name"></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.addressLabel')" prop="address"  :rules="[{required:true,message: $t('scenicManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.address"></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.openingHoursLabel')" prop="openingHours"  :rules="[{required:true,message: $t('scenicManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.openingHours"></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.priceLabel')" prop="price"  :rules="[{required:true,message: $t('scenicManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input type="number" v-model="formData.price"></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.ticketStockLabel')" prop="stock"  :rules="[{required:true,message: $t('scenicManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input type="number" v-model="formData.stock"></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.coverImageLabel')" prop="coverImage" style="width: 100%"
                        :rules="[{required:true,message: $t('scenicManage.uploadCoverImage'),trigger:[ 'blur','change']}]">
            <MyUpLoad type="imageCard" :limit="1" :files="formData.coverImage"
                      @setFiles="formData.coverImage =$event"></MyUpLoad>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.detailImages')" prop="detailImages"
                        :rules="[{required:true,message: $t('scenicManage.uploadDetailImages'),trigger:[ 'blur','change']}]"
                        style="width: 100%">
            <MyUpLoad
                type="imageList"
                :limit="5"
                :files="formData.detailImages"
                @setFiles="formData.detailImages =$event">
            </MyUpLoad>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.descriptionLabel')" prop="description" style="width: 85%" >
            <el-input type="textarea" :rows="3"  v-model="formData.description"></el-input>
          </el-form-item>
          <el-form-item :label="$t('scenicManage.statusLabel')" prop="status"
                        :rules="[{required:true,message: $t('scenicManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-radio-group v-model="formData.status">
              <el-radio label="ACTIVE">{{ $t('status.ACTIVE') }}</el-radio>
              <el-radio label="INACTIVE">{{ $t('status.INACTIVE') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">{{ $t('scenicManage.submit') }}</el-button>
          <el-button @click="closeDialog" :icon="Close">{{ $t('scenicManage.cancel') }}</el-button>
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
  categoryType: undefined,
  status: undefined
});
const categoryList = ref([]);

getCategoryList()
getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/scenicInfo/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}
//获取景点分类信息
function getCategoryList() {
  request.get("/scenicCategory/list").then(res => {
    categoryList.value = res.data
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
      request.post("/scenicInfo/add", formData.value).then(res => {
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
      request.put("/scenicInfo/update", formData.value).then(res => {
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
    request.delete("/scenicInfo/delBatch", {data: ids}).then(res => {
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
