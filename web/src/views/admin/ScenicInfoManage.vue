<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="景点分类" prop="categoryType">
            <el-input v-model="searchForm.categoryType" clearable></el-input>
          </el-form-item>
          <el-form-item label="景点名称" prop="name">
            <el-input v-model="searchForm.name" clearable></el-input>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="searchForm.status" placeholder="请选择" clearable filterable style="width: 150px">
              <el-option :label="item"  :value="item" :key="item" v-for="item in statusList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="">
            <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
            <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <el-space>
          <el-button type="primary" @click="add" :icon="Plus">新增</el-button>
          <el-button type="danger" :icon="Delete" @click="batchDelete(null)" :disabled="selectionRows.length<=0">批量删除</el-button>
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
          <el-table-column prop="id" label="ID" width="50"></el-table-column>
          <el-table-column show-overflow-tooltip prop="categoryType" label="景点分类"  width="120"></el-table-column>
          <el-table-column show-overflow-tooltip prop="name" label="景点名称"></el-table-column>
          <el-table-column prop="coverImage" label="封面图" width="130">
            <template #default="scope" >
              <el-image v-if="scope.row.coverImage" style="width: 100px; height: 100px" :src="scope.row.coverImage" :preview-src-list="[scope.row.coverImage]" :preview-teleported="true" ></el-image>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip prop="address" label="地址" width="120"></el-table-column>
          <el-table-column  show-overflow-tooltip prop="description" label="描述" width="120"></el-table-column>
          <el-table-column show-overflow-tooltip prop="openingHours" label="开放时间" width="100"></el-table-column>
          <el-table-column  show-overflow-tooltip prop="price" label="收费价格" width="90"></el-table-column>
          <el-table-column show-overflow-tooltip prop="stock" label="门票库存" width="90"></el-table-column>
          <el-table-column  show-overflow-tooltip  prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag type="success" v-if="scope.row.status==='上架'">上架</el-tag>
              <el-tag type="danger" v-if="scope.row.status==='下架'">下架</el-tag>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip prop="createTime" label="创建时间" width="180"></el-table-column>
          <el-table-column fixed="right" label="操作" width="200">
            <template #default="scope">
              <el-button :icon="Edit" @click="edit(scope.$index, scope.row)">编辑</el-button>
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
        width="800px"
    >
      <el-form ref="formRef" :model="formData" label-width="100px" inline>
        <slot name="content">
          <el-form-item label="景点分类" prop="categoryId" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-select v-model="formData.categoryId" placeholder="请选择分类" clearable filterable >
              <el-option :label="item.name"  :value="item.id" :key="item" v-for="item in categoryList"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="景点名称" prop="name"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.name"></el-input>
          </el-form-item>
          <el-form-item label="地址" prop="address"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.address"></el-input>
          </el-form-item>
          <el-form-item label="开放时间" prop="openingHours"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.openingHours"></el-input>
          </el-form-item>
          <el-form-item label="收费价格" prop="price"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input type="number" v-model="formData.price"></el-input>
          </el-form-item>
          <el-form-item label="门票库存" prop="stock"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input type="number" v-model="formData.stock"></el-input>
          </el-form-item>
          <el-form-item label="封面图" prop="coverImage" style="width: 100%"
                        :rules="[{required:true,message:'请上传封面图',trigger:[ 'blur','change']}]">
            <MyUpLoad type="imageCard" :limit="1" :files="formData.coverImage"
                      @setFiles="formData.coverImage =$event"></MyUpLoad>
          </el-form-item>
          <el-form-item label="详情图" prop="detailImages"
                        :rules="[{required:true,message:'请上传详情图片',trigger:[ 'blur','change']}]"
                        style="width: 100%">
            <MyUpLoad
                type="imageList"
                :limit="5"
                :files="formData.detailImages"
                @setFiles="formData.detailImages =$event">
            </MyUpLoad>
          </el-form-item>
          <el-form-item label="描述" prop="description" style="width: 85%" >
            <el-input type="textarea" :rows="3"  v-model="formData.description"></el-input>
          </el-form-item>
          <el-form-item label="状态" prop="status"
                        :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-radio-group v-model="formData.status">
              <el-radio label="上架"></el-radio>
              <el-radio label="下架"></el-radio>
            </el-radio-group>
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
const  statusList=ref(['上架','下架'])

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
      request.post("/scenicInfo/add", formData.value).then(res => {
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
      request.put("/scenicInfo/update", formData.value).then(res => {
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
    request.delete("/scenicInfo/delBatch", {data: ids}).then(res => {
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
