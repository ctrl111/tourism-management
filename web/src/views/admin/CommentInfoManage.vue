<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="类型" prop="typeCode">
            <el-select v-model="searchForm.typeCode" clearable placeholder="请选择类型" style="width: 150px">
              <el-option label="景点" value="景点"></el-option>
              <el-option label="游记" value="游记"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="内容" prop="content">
            <el-input v-model="searchForm.content" clearable placeholder="搜索评论内容"></el-input>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" @click="search">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="danger" @click="batchDelete" :disabled="selectionRows.length<=0">
            批量删除
          </el-button>
        </el-space>
      </el-card>
      <el-card>
        <el-table
            ref="tableComponents"
            :data="listData"
            tooltip-effect="dark"
            style="width: 100%"
            @selection-change="selectionChange"
            border>
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="typeCode" label="类型" width="100"></el-table-column>
          <el-table-column prop="associationId" label="关联ID" width="100"></el-table-column>
          <el-table-column prop="content" label="评论内容" min-width="300"></el-table-column>
          <el-table-column prop="userId" label="用户ID" width="100"></el-table-column>
          <el-table-column prop="parentId" label="父评论ID" width="120"></el-table-column>
          <el-table-column prop="createTime" label="评论时间" width="180"></el-table-column>
          <el-table-column fixed="right" label="操作" width="120">
            <template #default="scope">
              <el-button type="danger" size="small" @click="deleteOne(scope.row)">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, toRaw, onMounted } from 'vue'
import request from "@/utils/http.js"
import { ElMessage, ElMessageBox } from 'element-plus'

const searchForm = ref({
  typeCode: '',
  content: ''
})

const listData = ref([])
const selectionRows = ref([])

const pageInfo = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 获取列表数据
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo))
  request.get("/commentsInfo/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.total = res.data.total
  })
}

// 搜索
function search() {
  pageInfo.currentPage = 1
  getPageList()
}

// 重置搜索
function resetSearch() {
  searchForm.value = {
    typeCode: '',
    content: ''
  }
  pageInfo.currentPage = 1
  getPageList()
}

// 分页
function currentChange(val) {
  pageInfo.currentPage = val
  getPageList()
}

function sizeChange(val) {
  pageInfo.pageSize = val
  getPageList()
}

// 选择行
function selectionChange(val) {
  selectionRows.value = val
}

// 删除单个
function deleteOne(row) {
  ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    request.delete("/commentsInfo/delBatch", [row.id]).then(res => {
      if (res) {
        ElMessage.success('删除成功')
        getPageList()
      }
    })
  })
}

// 批量删除
function batchDelete() {
  if (selectionRows.value.length === 0) {
    ElMessage.warning('请选择要删除的数据')
    return
  }
  ElMessageBox.confirm('确定要删除选中的评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    let ids = selectionRows.value.map(item => item.id)
    request.delete("/commentsInfo/delBatch", ids).then(res => {
      if (res) {
        ElMessage.success('删除成功')
        getPageList()
      }
    })
  })
}

onMounted(() => {
  getPageList()
})
</script>

<style scoped>
</style>
