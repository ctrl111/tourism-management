<template>
  <div class="notification-container">
    <!-- 标题 -->
    <h1 class="notification-title">{{ $t('menu.myNotifications') }}</h1>

    <!-- 分类标签 -->
    <el-tabs v-model="activeName" @tab-change="handleTabChange">
      <el-tab-pane :label="$t('notice.all')" name="ALL"></el-tab-pane>
      <el-tab-pane :label="$t('notice.unread')" name="UNREAD"></el-tab-pane>
      <el-tab-pane :label="$t('notice.order')" name="ORDER"></el-tab-pane>
      <el-tab-pane :label="$t('notice.systemNotification')" name="SYSTEM"></el-tab-pane>
    </el-tabs>

    <!-- 通知列表 -->
    <el-card class="notification-list">
      <el-table
          ref="tableComponents"
          :data="listData"
          tooltip-effect="dark"
          style="width: 100%"
          @selection-change="selectionChange"
          @row-click="handleRowClick"
          border>
        <el-table-column prop="typeCode" :label="$t('notice.notificationType')">
          <template #default="scope">
            {{ getTypeLabel(scope.row.typeCode) }}
          </template>
        </el-table-column>
        <el-table-column prop="title" :label="$t('notice.noticeTitle')"></el-table-column>
        <el-table-column prop="content" :label="$t('notice.content')"></el-table-column>
        <el-table-column prop="createTime" :label="$t('notice.time')" width="180"></el-table-column>
        <el-table-column :label="$t('notice.status')" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.isRead==='NO'" type="warning">{{ $t('notice.unread') }}</el-tag>
            <el-tag v-else-if="scope.row.isRead==='YES'" type="success">{{ $t('notice.read') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('notice.operation')" width="180">
          <template #default="{ row }">
            <el-button type="text" @click.stop="viewDetail(row)">{{ $t('notice.view') }}</el-button>
            <el-button type="text" @click.stop="markAsRead(row)">{{ $t('notice.markAsRead') }}</el-button>
            <el-button type="text" @click.stop="deleteNotification(row)">{{ $t('notice.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
          background
          layout="prev, pager, next, jumper"
          :page-size="pageInfo.pageSize"
          :current-page="pageInfo.currentPage"
          :total="pageInfo.total"
          @current-change="currentChange"
      />
    </div>
    <!-- 查看通知详情弹窗 -->
    <el-dialog
        v-model="detailDialogVisible"
        :title="$t('notice.notificationDetails')"
        width="500px"
    >
      <el-form :model="currentNotification" label-width="80px">
        <el-form-item :label="$t('notice.type')">
          <el-input :value="getTypeLabel(currentNotification.typeCode)" readonly></el-input>
        </el-form-item>
        <el-form-item :label="$t('notice.noticeTitle')">
          <el-input v-model="currentNotification.title" readonly></el-input>
        </el-form-item>
        <el-form-item :label="$t('notice.content')">
          <el-input v-model="currentNotification.content" type="textarea" :rows="4" readonly></el-input>
        </el-form-item>
        <el-form-item :label="$t('notice.time')">
          <el-input v-model="currentNotification.createTime" readonly></el-input>
        </el-form-item>
        <el-form-item :label="$t('notice.status')">
          <el-tag v-if="currentNotification.isRead === 'NO'" type="warning">{{ $t('notice.unread') }}</el-tag>
          <el-tag v-else-if="currentNotification.isRead === 'YES'" type="success">{{ $t('notice.read') }}</el-tag>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('notice.close') }}</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRoute} from "vue-router";
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();

// Функция для получения русского названия типа уведомления
function getTypeLabel(typeCode) {
  const typeMap = {
    'SYSTEM': $t('notice.systemNotification'),
    'PAYMENT': $t('notice.order'),
    'ORDER': $t('notice.order')
  }
  return typeMap[typeCode] || typeCode
}

const drawerVisible = ref(false)
const unreadCount = ref(0)

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
const activeName = ref('ALL')

// 类型代码映射
const typeCodeMap = {
  'ALL': null,           // 全部
  'UNREAD': null,        // 未读
  'ORDER': 'ORDER',      // 订单类型（可能是PAYMENT或ORDER）
  'SYSTEM': 'SYSTEM'     // 系统通知
}

getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  
  // 根据标签页类型设置不同的过滤参数
  if (activeName.value === 'ALL') {
    // 查询所有通知
    delete data.typeCode
    delete data.isRead
  } else if (activeName.value === 'UNREAD') {
    // 查询未读通知
    delete data.typeCode
    data.isRead = 'NO'
  } else if (activeName.value === 'ORDER') {
    // 查询订单相关通知（PAYMENT类型）
    data.typeCode = 'PAYMENT'
    delete data.isRead
  } else if (activeName.value === 'SYSTEM') {
    // 查询系统通知
    data.typeCode = 'SYSTEM'
    delete data.isRead
  }
  
  request.get("/notice/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

// 当前选中的通知详情
const currentNotification = ref({});
const detailDialogVisible = ref(false);

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
        message: t('notice.validationFailed'),
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
          message: t('notice.operationSuccess'),
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
          message: t('notice.operationSuccess'),
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
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/notice/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: t('notice.operationSuccess'),
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: t('notice.deleteCancelled')
    });
    tableComponents.value.clearSelection();
  });
}

// 切换标签
function handleTabChange(){
  pageInfo.value.pageNum = 1; // 切换标签时重置页码
  getPageList();
};
// 点击通知行
const handleRowClick = (row) => {
  if (!row.isRead) {
    markAsRead(row);
  }
  // 跳转到通知详情页（可根据实际需求实现）
  console.log('查看通知详情:', row);
};
/**
 * 查看通知详情
 */
function viewDetail(row) {
  currentNotification.value = {...row};
  detailDialogVisible.value = true;
}
// 标记为已读
const markAsRead = (row) => {
  row.isRead = "YES";
  //更新
  request.put("/notice/update", row).then(res => {
    if (!res) {
      return
    }
    dialogOpen.value = false
    ElMessage({
      message: $t('notice.markReadSuccess'),
      type: 'success'
    });
    getPageList()
  })
};

// 删除通知
const deleteNotification = (row) => {
  let ids = [row.id]
  ElMessageBox.confirm($t('notice.deleteConfirm', { ids }), $t('message.confirmOperation'), {
    confirmButtonText: $t('common.confirm'),
    cancelButtonText: $t('common.cancel'),
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/notice/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: $t('notice.deleteSuccess'),
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: $t('notice.deleteCancelled')
    });
    tableComponents.value.clearSelection();
  });
};
//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
}
</script>

<style scoped>
.notification-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.notification-title {
  font-size: 24px;
  margin-bottom: 20px;
}

.notification-list {
  margin-top: 20px;
}

.pagination-wrapper {
  margin-top: 50px;
  text-align: right;
}

.el-table .el-button {
  padding: 0;
}
</style>
