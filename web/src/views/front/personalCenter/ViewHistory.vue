<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <!-- 类型切换选项卡 -->
      <el-tabs v-model="activeType" @tab-change="handleTypeChange">
        <el-tab-pane label="景点浏览" name="景点"></el-tab-pane>
        <el-tab-pane label="游记浏览" name="游记"></el-tab-pane>
        <el-tab-pane label="路线分享" name="路线分享"></el-tab-pane>
      </el-tabs>
      <el-card>
        <el-table ref="tableComponents"
                  :data="listData"
                  tooltip-effect="dark"
                  style="width: 100%"
                  @selection-change="selectionChange"
                  border>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="50"></el-table-column>
          <el-table-column  label="用户" key="user">
            <template #default="{row}">
              {{ row.user?.username}}
            </template>
          </el-table-column>
          <!-- 景点列 -->
          <el-table-column v-if="activeType === '景点'" label="景点名称" key="scenic">
            <template #default="{row}">
              {{ row.associationObject?.name || '-' }}
            </template>
          </el-table-column>

          <!-- 游记列 -->
          <el-table-column v-if="activeType === '游记'" label="游记标题" key="note">
            <template #default="{row}">
              {{ row.associationObject?.title || '-' }}
            </template>
          </el-table-column>

          <!-- 线路列 -->
          <el-table-column v-if="activeType === '路线分享'" label="路线分享标题" key="route">
            <template #default="{row}">
              {{ row.associationObject?.title || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="viewTime" label="浏览时间"></el-table-column>
          <el-table-column fixed="right" label="操作" width="120">
            <template #default="scope">
              <el-button :icon="Delete" type="danger" @click="deleteOne(scope.$index, scope.row)">删除</el-button>
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
          <el-form-item label="景点id" prop="attractionId"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.attractionId"></el-input>
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
  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import router from "@/router/index.js";

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
const activeType = ref('景点')
const searchForm = ref({
  type: '景点',
});

const handleTypeChange = (type) => {
  searchForm.value.type = type
  getPageList()
}

getPageList()



/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/viewHistory/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

/**
 * 查看详情
 */
function view(row) {
  if (row.typeCode==="路线分享"){
    router.push('/routeDetails/'+row.associationId)
    return
  }else if (row.typeCode==="游记"){
    router.push('/travelDetails/'+row.associationId)
    return
  }else if (row.typeCode==="景点")
    router.push('/scenicDetails/'+row.associationId)
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
        message: "验证失败，请检查表单!",
        type: 'warning'
      });
      return
    }
    //新增
    if (!formData.value.id) {
      request.post("/viewHistory/add", formData.value).then(res => {
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
      request.put("/viewHistory/update", formData.value).then(res => {
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
    request.delete("/viewHistory/delBatch", {data: ids}).then(res => {
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
