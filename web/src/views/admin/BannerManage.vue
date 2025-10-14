<template>
  <div>
    <el-space direction="vertical" alignment="left" style="width: 100%">
      <el-card>
        <el-form ref="searchFormComponents" :model="searchForm" inline>
          <el-form-item label="标题" prop="title">
            <el-input v-model="searchForm.title" clearable></el-input>
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
          <el-table-column prop="title" label="标题"></el-table-column>
          <el-table-column prop="imageUrl" label="图片" width="130">
            <template #default="scope" >
              <el-image v-if="scope.row.imageUrl" style="width: 100px; height: 100px" :src="scope.row.imageUrl" :preview-src-list="[scope.row.coverImage]" :preview-teleported="true" ></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="linkUrl" label="跳转链接"></el-table-column>
          <el-table-column prop="sort" label="排序"></el-table-column>
          <el-table-column prop="status" label="状态"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
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
        width="800"
    >
      <el-form ref="formRef" :model="formData" label-width="100px" inline>
        <slot name="content">
          <el-form-item label="选择景区">
            <el-button
                type="primary"
                @click="openScenicDialog"
                :icon="Search">
              {{ selectedScenic ? selectedScenic.name : '选择景区' }}
            </el-button>
          </el-form-item>
          <el-form-item label="已选景区" v-if="selectedScenic">
            <el-tag type="success">
              {{ selectedScenic.name }}（ID: {{ selectedScenic.id }}）
            </el-tag>
          </el-form-item>
          <el-form-item label="标题" prop="title"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input v-model="formData.title"></el-input>
          </el-form-item>
          <el-form-item label="封面" v-if="selectedScenic">
            <el-image
                style="width: 200px; height: 120px"
                :src="selectedScenic.coverImage"
                :preview-src-list="[selectedScenic.coverImage]">
            </el-image>
          </el-form-item>
          <el-form-item label="排序" prop="sort"  :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
            <el-input type="number"  v-model="formData.sort"></el-input>
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
    <!--    景区选择弹框-->
    <el-dialog
        v-model="scenicDialogVisible"
        title="选择景区"
        width="70%"
    >
      <el-table
          :data="scenicList"
          border
          @selection-change="handleScenicSelection"
          height="400">
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
      </el-table>
      <template #footer>
        <div style="margin-top: 20px">
          <el-pagination
              @current-change="currentChangeScenic"
              @size-change="sizeChange"
              :page-size="scenicPage.pageSize"
              :current-page="scenicPage.currentPage"
              background
              layout="total,sizes, prev, pager, next"
              :total="pageInfo.total">
          </el-pagination>
        </div>
        <el-button type="primary" @click="confirmScenicSelection">确定选择</el-button>
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
//景区响应式数据
const scenicDialogVisible = ref(false);
const scenicList = ref([]);
const selectedScenic = ref(null);
const scenicPage = ref({
  //当前页
  pageNum: 1,
  //分页大小
  pageSize: 10,
  //总条数
  total: 0
});

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

});
const  statusList=ref(['上架','下架'])

getPageList()

function loadScenicList() {
  try {
    let data = Object.assign(toRaw(scenicPage.value))
    request.get("/scenicInfo/page", {
      params: data
    }).then(res => {
      scenicList.value = res.data.list
      scenicPage.value.total = res.data.total
    })
  } catch (error) {
    console.error('加载景区列表失败:', error);
  }
}
// 打开景区选择对话框
function openScenicDialog() {
  scenicDialogVisible.value = true;
  selectedScenics.value = []; // 每次打开清空选择
  loadScenicList();
}

// 处理景区选择
const selectedScenics = ref([]);
function handleScenicSelection(selection) {
  selectedScenics.value = selection;
}

// 确认选择景区
function confirmScenicSelection() {
  if (selectedScenics.value.length === 0) {
    ElMessage.warning('请至少选择一个景区');
    return;
  }
  // 这里根据业务需求，如果要单选就取第一个
  selectedScenic.value = selectedScenics.value[0];
  // 自动填充表单数据
  formData.value = {
    ...formData.value,
    title: selectedScenic.value.name,
    imageUrl: selectedScenic.value.coverImage,
    linkId: selectedScenic.value.id  // 关联景区ID
  };

  scenicDialogVisible.value = false;
}
/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/banner/page", {
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
 * 景区选择分页
 * @param e
 */
function currentChangeScenic(e) {
  scenicPage.value.pageNum = e
  loadScenicList();
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
  selectedScenic.value = null; // 新增时清除已选景区
  selectedScenics.value = [];
  dialogOpen.value = true
}
/**
 * 编辑
 * @param index
 * @param row
 */
function edit(index, row) {
  formData.value = Object.assign({}, row)
  // 根据关联ID加载完整景区信息
  request.get(`/scenicInfo/selectById/+${row.linkId}`).then(res => {
    selectedScenic.value = res.data;
  })
  dialogOpen.value = true
}
/**
 * 关闭弹框
 */
function closeDialog() {
  dialogOpen.value = false
  // 清空表单数据
  formData.value = {};
  // 清空已选景区
  selectedScenic.value = null;
  selectedScenics.value = [];
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
      request.post("/banner/add", formData.value).then(res => {
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
      request.put("/banner/update", formData.value).then(res => {
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
    request.delete("/banner/delBatch", {data: ids}).then(res => {
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
