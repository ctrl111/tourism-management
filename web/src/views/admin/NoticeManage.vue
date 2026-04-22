<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item :label="$t('noticeManage.title')" prop="title">
            <el-input v-model="searchForm.title" clearable></el-input>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.user')" prop="userId" class="form-item-wide">
            <el-select
                v-model="searchForm.userId"
                :placeholder="$t('noticeManage.pleaseSelectUser')"
                style="width: 200px"
            >
              <el-option
                  v-for="user in userList"
                  :key="user.id"
                  :label="user.username"
                  :value="user.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">{{ $t('noticeManage.search') }}</el-button>
            <el-button :icon="Refresh" @click="resetSearch">{{ $t('noticeManage.reset') }}</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="addPublish" :icon="Plus">{{ $t('noticeManage.publishNotice') }}</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">
            {{ $t('noticeManage.batchDelete') }}
          </el-button>
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
          <el-table-column prop="typeCode" :label="$t('noticeManage.noticeType')"></el-table-column>
          <el-table-column :label="$t('noticeManage.user')" key="user">
            <template #default="{row}">
              {{ row.user?.username }}
            </template>
          </el-table-column>
          <el-table-column prop="title" :label="$t('noticeManage.title')" min-width="150" show-overflow-tooltip></el-table-column>
          <el-table-column prop="content" :label="$t('noticeManage.contentLabel')" min-width="250" show-overflow-tooltip>
            <template #default="scope">
              <div class="content-cell">{{ scope.row.content }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" :label="$t('noticeManage.time')" width="180"></el-table-column>
          <el-table-column :label="$t('noticeManage.status')" width="100">
            <template #default="scope">
              <el-tag v-if="scope.row.isRead==='未读'" type="warning">{{ $t('noticeManage.unread') }}</el-tag>
              <el-tag v-else-if="scope.row.isRead==='已读'" type="success">{{ $t('noticeManage.read') }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column fixed="right" :label="$t('noticeManage.operation')" width="260">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.$index, scope.row)">{{ $t('noticeManage.editView') }}</el-button>
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">{{ $t('noticeManage.delete') }}</el-button>
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
        :title="formData.id ? $t('noticeManage.editTitle') : $t('noticeManage.addTitle')"
        width="500"
    >
      <el-form ref="formRef" :model="formData" label-width="100px">
        <slot name="content">
          <el-form-item :label="$t('noticeManage.noticeTypeLabel')" prop="typeCode"
                        :rules="[{required:true,message: $t('noticeManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.typeCode" disabled></el-input>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.titleLabel')" prop="title"
                        :rules="[{required:true,message: $t('noticeManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.title"></el-input>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.contentLabel')" prop="content"
                        :rules="[{required:true,message: $t('noticeManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input type="textarea" v-model="formData.content"></el-input>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.statusLabel')" prop="isRead" width="100">
            <template #default="scope">
              <el-tag v-if="formData.isRead==='未读'" type="warning">{{ $t('noticeManage.unread') }}</el-tag>
              <el-tag v-else-if="formData.isRead==='已读'" type="success">{{ $t('noticeManage.read') }}</el-tag>
            </template>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.publishTime')" prop="createTime"
                        :rules="[{required:true,message: $t('noticeManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.createTime" disabled></el-input>
          </el-form-item>
        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submit" :icon="Check">{{ $t('noticeManage.submit') }}</el-button>
          <el-button @click="closeDialog" :icon="Close">{{ $t('noticeManage.cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog
        v-model="dialogOpenPublish"
        v-if="dialogOpenPublish"
        :title="$t('noticeManage.publishNoticeTitle')"
        width="500"
    >
      <el-form ref="publishFormRef" :model="formData" label-width="100px">
        <slot name="content">
          <el-form-item
              :label="$t('noticeManage.userLabel')"
              prop="userIds"
              :rules="[{ required:true, message: $t('noticeManage.pleaseSelectAtLeastOneUser'), trigger: ['change', 'blur'] }]"
          >
            <el-select
                v-model="formData.userIds"
                multiple
                filterable
                :placeholder="$t('noticeManage.pleaseSelectUser')"
                style="width: 100%;"
                @change="handleUserSelectChange"
            >
              <el-option
                  :label="$t('noticeManage.allUsers')"
                  value="all"
                  @click="toggleSelectAll"
              >
              <span style="color: #409EFF; font-weight: bold;">
                {{ isSelectAll ? $t('noticeManage.allSelected') : $t('noticeManage.selectAllUsers') }}
              </span>
              </el-option>
              <el-option
                  v-for="user in userList"
                  :key="user.id"
                  :label="user.username"
                  :value="user.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.noticeTypeLabel')" prop="typeCode"
                        :rules="[{required:true,message: $t('noticeManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-radio-group v-model="formData.typeCode">
              <el-radio :label="$t('noticeManage.systemNotice')"></el-radio>
              <el-radio :label="$t('noticeManage.order')"></el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.titleLabel')" prop="title"
                        :rules="[{required:true,message: $t('noticeManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input v-model="formData.title"></el-input>
          </el-form-item>
          <el-form-item :label="$t('noticeManage.contentLabel')" prop="content"
                        :rules="[{required:true,message: $t('noticeManage.cannotBeEmpty'),trigger:[ 'blur','change']}]">
            <el-input type="textarea" v-model="formData.content"></el-input>
          </el-form-item>
        </slot>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitPublishForm" :icon="Check">{{ $t('noticeManage.publish') }}</el-button>
          <el-button @click="closeDialog" :icon="Close">{{ $t('noticeManage.cancel') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Plus, Refresh, Search} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const userList = ref([]);
const allUserIds = ref([]);
const thisUserIds = ref([]);

const isSelectAll = ref(false);

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


loadUserList();
getPageList()

function loadUserList() {
  request.get("/user/list",).then(res => {
    userList.value = res.data;
    res.data.forEach(user => {
      allUserIds.value.push(user.id);
    })
  })
}

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/notice/page", {
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

// 处理选择变化
function handleUserSelectChange(selected) {
  // 如果选中包含"全部用户"选项
  if (selected.includes('all')) {
    formData.value.userIds = allUserIds.value;
    isSelectAll.value = true;
  } else {
    isSelectAll.value = false;
  }
}

// 手动切换全选
function toggleSelectAll() {
  if (isSelectAll.value) {
    formData.value.userIds = allUserIds.value;
  } else {
    formData.value.userIds = [];
  }
  isSelectAll.value = !isSelectAll.value;
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
const dialogOpenPublish = ref(false);
const formData = ref({});
const formRef = ref();
const publishFormRef = ref();

/**
 * 发布通知
 */
function addPublish() {
  formData.value = {}
  dialogOpenPublish.value = true
}

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
  dialogOpenPublish.value = false
}

/**
 * 提交数据
 */
function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage({
        message: "Проверка не пройдена, проверьте форму!",
        type: 'warning'
      });
      return
    }
    //新增
    if (!formData.value.id) {
      request.post("/notice/add", formData.value).then(res => {
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
      request.put("/notice/update", formData.value).then(res => {
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

function submitPublishForm() {
  publishFormRef.value.validate((valid) => {
    if (!valid) {
      ElMessage({
        message: "Проверка не пройдена, проверьте форму!",
        type: 'warning'
      });
      return
    }
    //新增
    if (!formData.value.id) {
      request.post("/notice/add", formData.value).then(res => {
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
      request.put("/notice/update", formData.value).then(res => {
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
    request.delete("/notice/delBatch", {data: ids}).then(res => {
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
.form-item-wide .el-form-item__content {
  width: 200px;
}

.content-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.5;
}
</style>
