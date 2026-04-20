<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('travelNoteManageAdmin.searchTitle')" prop="title">
            <el-input v-model="searchForm.title" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('travelNoteManageAdmin.searchUser')" prop="content">
            <el-input v-model="searchForm.userName" clearable></el-input>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('travelNoteManageAdmin.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('travelNoteManageAdmin.reset') }}</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <!--          <el-button type="primary" @click="add" :icon="Plus">新增</el-button>-->
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">{{ $t('travelNoteManageAdmin.batchDelete') }}</el-button>
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
          <el-table-column prop="userName" :label="$t('travelNoteManageAdmin.user')" width="120">
            <template #default="{row}">
              {{ row.user?.username }}
            </template>
          </el-table-column>
          <el-table-column prop="title" :label="$t('travelNoteManageAdmin.noteTitle')"></el-table-column>
          <el-table-column show-overflow-tooltip :label="$t('travelNoteManageAdmin.cover')">
            <template #default="scope">
              <el-image style="width: 50px; height: 50px" :src="scope.row.cover"
                        :preview-src-list="[scope.row.cover]"
                        :preview-teleported="true"></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="travelTime" :label="$t('travelNoteManageAdmin.travelTime')"></el-table-column>
          <el-table-column prop="commentsCount" :label="$t('travelNoteManageAdmin.commentsCount')">
            <template #default="{row}">
              {{ row.commentsCount || 0 }}
            </template>
          </el-table-column>
          <el-table-column prop="favoriteCount" :label="$t('travelNoteManageAdmin.favoriteCount')">
            <template #default="{row}">
              {{ row.favoriteCount || 0 }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('travelNoteManageAdmin.createTime')"></el-table-column>
          <el-table-column fixed="right" :label="$t('travelNoteManageAdmin.operation')" width="200">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.$index, scope.row)">{{ $t('travelNoteManageAdmin.view') }}</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">{{ $t('travelNoteManageAdmin.delete') }}</el-button>
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
        :title="formData.id ? $t('travelNoteManageAdmin.viewTitle') : $t('travelNoteManageAdmin.addTitle')"
        width="800"
    >
      <el-form ref="formRef" :model="formData" label-width="120px">
        <slot name="content">
          <el-form-item :label="$t('travelNoteManageAdmin.titleLabel')" prop="title"  :rules="[{required:true,message:$t('travelNoteManageAdmin.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.title" disabled></el-input>
          </el-form-item>
          <el-form-item :label="$t('travelNoteManageAdmin.coverLabel')" >
            <MyUpLoad type="imageCard" :limit="1" :files="formData.cover" @setFiles="formData.cover =$event" v-if="dialogOpen"></MyUpLoad>
          </el-form-item>
          <el-form-item :label="$t('travelNoteManageAdmin.travelTimeLabel')" prop="travelTime"  :rules="[{required:true,message:$t('travelNoteManageAdmin.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.travelTime" disabled></el-input>
          </el-form-item>
          <el-form-item :label="$t('travelNoteManageAdmin.contentLabel')" prop="content"  >
            <MyEditor disabled :content="formData.content" @content-change="formData.basicInfo =$event" v-if="dialogOpen"></MyEditor>
          </el-form-item>
        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <!--          <el-button type="primary" @click="submit" :icon="Check">提交</el-button>-->
          <el-button @click="closeDialog" :icon="Close">{{ $t('travelNoteManageAdmin.cancel') }}</el-button>
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
import MyEditor from "@/components/MyEditor.vue";
import MyUpload from "@/components/MyUpload.vue";

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
  title: undefined,
  content: undefined,

});



getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/travelNote/page", {
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
        message: this.$t('travelNoteManageAdmin.validationFailed'),
        type: 'warning'
      });
      return
    }
    //新增
    if (!formData.value.id) {
      request.post("/travelNote/add", formData.value).then(res => {
        if (!res) {
          return
        }
        dialogOpen.value = false
        ElMessage({
          message: this.$t('travelNoteManageAdmin.operationSuccess'),
          type: 'success'
        });
        getPageList()
      })
    } else {
      //更新
      request.put("/travelNote/update", formData.value).then(res => {
        if (!res) {
          return
        }
        dialogOpen.value = false
        ElMessage({
          message: this.$t('travelNoteManageAdmin.operationSuccess'),
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
  ElMessageBox.confirm(this.$t('travelNoteManageAdmin.confirmDelete', { ids: ids.join(', ') }), this.$t('travelNoteManageAdmin.hint'), {
    confirmButtonText: this.$t('travelNoteManageAdmin.confirm'),
    cancelButtonText: this.$t('travelNoteManageAdmin.cancel'),
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/travelNote/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: this.$t('travelNoteManageAdmin.operationSuccess'),
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: this.$t('travelNoteManageAdmin.deleteCancelled')
    });
    tableComponents.value.clearSelection();
  });
}
</script>

<style scoped>

</style>
