<template>
  <div class="travel-container">
    <!-- 头部操作栏 -->
    <div class="header-bar">
      <el-button
          type="primary"
          :icon="Plus"
          @click="add"
          class="create-btn"
      >
        写游记
      </el-button>
    </div>

    <!-- 搜索区 -->
    <el-card class="search-card" shadow="never">
      <div class="search-wrapper">
        <el-input
            v-model="searchForm.title"
            placeholder="搜索游记标题"
            class="search-input"
            size="large"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" size="large" @click="search">
          搜索
        </el-button>
      </div>
    </el-card>

    <!-- 游记列表 -->
    <el-row :gutter="20" class="note-list">
      <el-col
          v-for="item in listData"
          :key="item.id"
          :xs="24" :sm="12" :md="8" :lg="6"
          class="note-item"
      >
        <el-card
            shadow="hover"
            class="note-card"
            @click="router.push('/travelDetails/'+item.id)"
        >
          <!-- 封面图 -->
          <div class="card-cover">
            <el-image
                :src="item.cover"
                fit="cover"
                class="cover-image"
            >
              <template #error>
                <div class="image-error">
                  <el-icon>
                    <Picture/>
                  </el-icon>
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
            <!-- 标题和用户信息叠加在封面图 -->
            <div class="cover-overlay">
              <h4 class="note-title">{{ item.title }}</h4>
              <div class="user-info">
                <el-avatar
                    :size="32"
                    :src="item.user.avatarUrl"
                    class="user-avatar"
                />
                <span class="username">{{ item.user.username }}</span>
              </div>
            </div>
            <!-- 统计信息 -->
            <div class="card-stats">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                {{ item.viewCount | formatNumber }}
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                {{ item.likesCount | formatNumber }}
              </div>
            </div>
          </div>

          <!-- 底部信息 -->
          <div class="card-footer">
            <div class="travel-info">
              <span class="days">{{ item.days }}天行程</span>
              <span class="date">行程时间：{{ item.travelTime }}</span>
              <span class="date">发布时间：{{ formatDate(item.createTime) }}</span>
            </div>
            <div class="comment-stat">
              <el-icon><ChatDotRound /></el-icon>
              {{ item.commentsCount | formatNumber }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
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
    <!-- 发布/编辑对话框 -->
    <el-dialog
        v-model="dialogOpen"
        :title="formData.id ? '编辑游记' : '发布新游记'"
        width="800px"
        custom-class="note-editor-dialog"
    >
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="游记标题" prop="title" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
          <el-input
              v-model="formData.title"
              placeholder="请输入吸引人的标题"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>
        <el-form-item label="行程信息">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item prop="travelTime" :rules="[{required:true,message:'请选择日期',trigger:[ 'blur','change']}]" >
                <el-date-picker
                    v-model="formData.travelTime"
                    type="date"
                    value-format="YYYY-MM-DD"
                    placeholder="选择出行日期"
                    style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="days" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]" >
                <el-input-number
                    v-model="formData.days"
                    :min="1"
                    :max="30"
                    placeholder="旅行天数"
                    style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="封面图片" prop="images" :rules="[{required:true,message:'请上传封面图',trigger:[ 'blur','change']}]">
          <MyUpLoad v-if="dialogOpen"  type="imageCard" :limit="1" :files="formData.images"
                    @setFiles="formData.images =$event"></MyUpLoad>
        </el-form-item>

        <el-form-item label="游记内容" prop="content" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
          <MyEditor :content="formData.content"
                    placeholder="开始书写你的精彩旅程..."
                    @content-change="formData.content =$event"
                    v-if="dialogOpen"></MyEditor>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button
            type="primary"
            @click="submit"
        >
          {{ formData.id ? '更新游记' : '立即发布' }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search,Picture,View,Star,ChatDotRound,Share} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyEditor from "@/components/MyEditor.vue";
import MyUpLoad from "@/components/MyUpload.vue";
import router from "@/router/index.js";
import dayjs from 'dayjs'


const drawerVisible = ref(false)
const unreadCount = ref(0)

const detailVisible = ref(false)
const searchFormComponents = ref();
const tableComponents = ref();
const listData = ref([]);
const pageInfo = ref({
  //当前页
  pageNum: 1,
  //分页大小
  pageSize: 12,
  //总条数
  total: 0
});
const searchForm = ref({
  title: undefined,
  content: undefined,

});



getPageList()


function formatDate(value) {
  if (!value) return '--'
  return dayjs(value).format('YYYY-MM-DD')
}
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
  formData.value = {}
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
      request.post("/travelNote/add", formData.value).then(res => {
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
      request.put("/travelNote/update", formData.value).then(res => {
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
    request.delete("/travelNote/delBatch", {data: ids}).then(res => {
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
//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
}
// 添加在setup最后
// const formatDate = (timestamp) => {
//   return dayjs(timestamp).format('YYYY-MM-DD')
// }
//
// // 在原有的formatNumber后添加
// const formatNumber = (value) => {
//   return value > 10000 ? (value/10000).toFixed(1) + 'w' : value
// }
</script>

<style scoped>
.travel-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-card {
  margin-bottom: 30px;
  border-radius: 8px;
}

.search-wrapper {
  display: flex;
  gap: 15px;
  padding: 10px 0;
}

.note-list {
  margin: 20px -10px;
}



.card-cover {
  height: 200px;
  position: relative;

  .cover-image {
    width: 100%;
    height: 100%;
    border-radius: 12px 12px 0 0;
  }

  .cover-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 15px;
    background: linear-gradient(transparent, rgba(0,0,0,0.6));
    color: #fff;
  }
}

/* 统计信息悬浮层 */
.card-stats {
  position: absolute;
  top: 15px;
  right: 15px;
  display: flex;
  gap: 12px;
  background: rgba(255,255,255,0.9);
  padding: 6px 12px;
  border-radius: 20px;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #666;
    font-size: 14px;

    .el-icon {
      color: var(--el-color-primary);
    }
  }
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;

  .user-avatar {
    border: 2px solid rgba(255,255,255,0.8);
  }

  .username {
    font-size: 14px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
/* 创建时间 */
.create-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;

  &::before {
    content: "🕒";
    margin-right: 4px;
  }
}
.card-content {
  padding: 15px;
}

/* 标题样式 */
.note-title {
  font-size: 18px;
  line-height: 1.4;
  margin: 0 0 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}



/* 顶部操作样式 */
.header-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.create-btn {
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 24px;
}


.meta-info h2 {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 8px;
}


.image-gallery h3 {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 20px;
}


/* 卡片悬停效果 */
.note-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  margin-bottom: 20px;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  :deep(.el-card__body) {
    padding: 0;
  }
}

.note-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.12);
}

.cover-image {
  border-radius: 8px;
}


.pagination-wrapper {
  margin: 40px 0;
}
.card-footer {
  padding: 12px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;

  .travel-info {
    display: flex;
    flex-direction: column;

    .days {
      color: var(--el-color-primary);
      font-weight: 500;
      font-size: 14px;
    }

    .date {
      color: #666;
      font-size: 12px;
    }
  }

  .comment-stat {
    display: flex;
    align-items: center;
    gap: 4px;
    color: #666;
    font-size: 14px;

    .el-icon {
      color: var(--el-color-warning);
    }
  }
}
/* 响应式调整 */
@media (max-width: 768px) {
  .note-title {
    font-size: 16px;
  }

  .card-stats {
    top: 10px;
    right: 10px;
    padding: 4px 8px;

    .stat-item {
      font-size: 12px;
    }
  }

  .card-footer {
    padding: 10px 12px;
  }
}
</style>
