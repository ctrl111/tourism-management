<template>
  <div class="route-container">
    <!-- 头部操作栏 -->
    <div class="header-bar">
      <el-button
          type="primary"
          :icon="Plus"
          @click="add"
          class="create-btn"
      >
        分享路线攻略
      </el-button>
    </div>
    <!-- 搜索区 -->
    <el-card class="search-card" shadow="never">
      <div class="search-header">
        <el-input
            v-model="searchForm.tilte"
            placeholder="搜索标题"
            size="large"
            class="search-input"
            clearable
        >
          <template #prefix>
            <Search style="width: 2em; height: 2em; margin-right: 10px" />
          </template>
        </el-input>
        <el-button type="primary" :icon="Search" @click="search" class="search-button">搜索</el-button>
      </div>
      <!--      <div class="filter-area">-->
      <!--        <div class="filter-item">-->
      <!--          <span class="filter-label">行程天数：</span>-->
      <!--          <el-select v-model="searchForm.days"-->
      <!--                     placeholder="1天"-->
      <!--                     style="width: 80px"-->
      <!--                     clearable>-->
      <!--            <el-option-->
      <!--                v-for="day in [3,5,7]"-->
      <!--                :key="day"-->
      <!--                :label="`${day}天`"-->
      <!--                :value="day"-->
      <!--            />-->
      <!--          </el-select>-->
      <!--        </div>-->
      <!--        <div class="filter-item">-->
      <!--          <span class="filter-label">交通方式：</span>-->
      <!--          <el-select v-model="searchForm.transport"-->
      <!--                     placeholder="自驾"-->
      <!--                     style="width: 80px"-->
      <!--                     clearable>>-->
      <!--            <el-option-->
      <!--                v-for="type in transportTypes"-->
      <!--                :key="type"-->
      <!--                :label="type"-->
      <!--                :value="type"-->
      <!--            />-->
      <!--          </el-select>-->
      <!--        </div>-->
      <!--      </div>-->
    </el-card>

    <!-- 线路卡片列表 -->
    <el-row :gutter="20" class="route-list">
      <el-col
          v-for="item in listData"
          :key="item.id"
          :xs="24" :sm="12" :md="8" :lg="6"
          class="route-item"
      >
        <el-card
            shadow="hover"
            class="route-card"
            @click="router.push('/routeDetails/'+item.id)"
        >
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
            <!-- 用户信息叠加层 -->
            <div class="user-overlay">
              <el-avatar :size="32" :src="item.user?.avatarUrl" class="user-avatar" />
              <span class="username">{{ item.user?.username }}</span>
            </div>
            <!-- 统计信息 -->
            <div class="stats-overlay">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                {{ item.viewCount || 0 }}
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                {{ item.likesCount || 0 }}
              </div>
              <div class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                {{ item.commentCount || 0 }}
              </div>
            </div>
          </div>

          <div class="card-content">
            <h4 class="route-title">{{ item.title }}</h4>

            <div class="route-tags">
              <el-tag type="success" size="small">{{ item.days }}天行程</el-tag>
              <el-tag  size="small">预估费用<span class="current-price">￥{{ item.totalCost }}元</span></el-tag>
            </div>

            <div class="route-info">
              <div class="info-item">
                <article class="travel-content" v-html="item.content"></article>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!--    <div class="route-grid">-->
    <!--      <div-->
    <!--          v-for="route in listData"-->
    <!--          :key="route.id"-->
    <!--          class="route-card"-->
    <!--      >-->
    <!--        <!– 封面图及悬浮层 –>-->
    <!--        <div class="cover-wrapper">-->
    <!--          <el-image-->
    <!--              :src="route.cover"-->
    <!--              class="cover-image"-->
    <!--              fit="cover"-->
    <!--              :preview-src-list="[route.cover]"-->
    <!--          >-->
    <!--            <template #error>-->
    <!--              <div class="cover-error">-->
    <!--                <el-icon><Picture /></el-icon>-->
    <!--              </div>-->
    <!--            </template>-->

    <!--            <!– 封面信息叠加层 –>-->
    <!--            <div class="cover-meta">-->
    <!--              <div class="meta-left">-->
    <!--                <h3 class="title">{{ route.title }}</h3>-->
    <!--                <div class="stats">-->
    <!--                  <el-tag type="warning" effect="plain">-->
    <!--                    <el-icon><Calendar /></el-icon>-->
    <!--                    {{ route.days }}天行程-->
    <!--                  </el-tag>-->
    <!--                  <el-tag type="success" effect="plain">-->
    <!--                    <el-icon><Location /></el-icon>-->
    <!--                    {{ route.location }}-->
    <!--                  </el-tag>-->
    <!--                </div>-->
    <!--              </div>-->
    <!--              <div class="meta-right">-->
    <!--                <el-avatar-->
    <!--                    :size="40"-->
    <!--                    :src="route.user?.avatarUrl"-->
    <!--                    :title="route.user?.username"-->
    <!--                />-->
    <!--              </div>-->
    <!--            </div>-->
    <!--          </el-image>-->
    <!--        </div>-->

    <!--        <!– 底部交互信息 –>-->
    <!--        <div class="card-footer">-->
    <!--          <div class="user-info">-->
    <!--            <span class="username">{{ route.user?.username }}</span>-->
    <!--            <span class="date">{{ formatDate(route.createTime) }}</span>-->
    <!--          </div>-->
    <!--          <div class="action-buttons">-->
    <!--            <el-button-->
    <!--                type="danger"-->
    <!--                :icon="Star"-->
    <!--                circle-->
    <!--                :class="{ 'liked': route.is_liked }"-->
    <!--                @click.stop="toggleLike(route)"-->
    <!--            />-->
    <!--            <span class="count">{{ route.likes | formatNumber }}</span>-->
    <!--          </div>-->
    <!--        </div>-->

    <!--        <!– 作者操作菜单 –>-->
    <!--        <div v-if="isOwner(route)" class="owner-actions">-->
    <!--          <el-dropdown trigger="click">-->
    <!--            <el-button type="primary" text>-->
    <!--              <el-icon><More /></el-icon>-->
    <!--            </el-button>-->
    <!--            <template #dropdown>-->
    <!--              <el-dropdown-menu>-->
    <!--                <el-dropdown-item @click="edit(route)">编辑</el-dropdown-item>-->
    <!--                <el-dropdown-item @click="deleteOne(route)">删除</el-dropdown-item>-->
    <!--              </el-dropdown-menu>-->
    <!--            </template>-->
    <!--          </el-dropdown>-->
    <!--        </div>-->
    <!--      </div>-->
    <!--    </div>-->
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
        :title="formData.id ? '编辑路线' : '发布新路线'"
        width="800px"
        custom-class="note-editor-dialog"
    >
      <el-form ref="formRef" :model="formData" label-width="100px">
        <el-form-item label="线路标题" prop="title" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
          <el-input
              v-model="formData.title"
              placeholder="请输入线路标题"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>
        <el-form-item label="封面图片" prop="cover" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]" >
          <MyUpLoad type="imageCard" :limit="1" :files="formData.cover"
                    @setFiles="formData.cover =$event"></MyUpLoad>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总费用" prop="totalCost" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]" >
              <el-input-number
                  v-model="formData.totalCost"
                  :min="1"
                  controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行程天数" prop="days" >
              <el-input-number
                  v-model="formData.days"
                  :min="1"
                  :max="30"
                  controls-position="left"
                  @change="updateDaysList"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <!--        每天行程-->
        <div v-for="(day, index) in formData.daysList" :key="index">
          <el-divider >第 {{ index + 1 }} 天</el-divider>
          <el-form-item
              v-model="day.dayNumber"
              :label="`第${index + 1}天标题`"
              :prop="title"
              :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]"
          >
            <el-input v-model="day.title" />
          </el-form-item>
          <!-- 隐藏的天数字段 -->
          <el-form-item :prop="`daysList[${index}].dayNumber`" style="display: none">
            <el-input v-model="day.dayNumber" type="hidden" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="地点"  :prop="locations" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
                <el-input v-model="day.locations" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="交通方式"  :prop="transport" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
                <el-input v-model="day.transport" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="住宿信息"  :prop="accommodation" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
                <el-input v-model="day.accommodation" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="景点信息" :prop="spots" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
                <el-input v-model="day.spots" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="描述" :prop="description" :rules="[{required:true,message:'不能为空',trigger:[ 'blur','change']}]">
                <el-input type="textarea" v-model="day.description" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <el-form-item label="路线描述" prop="content" >
          <MyEditor
              :content="formData.content"
              placeholder="请输入详细行程安排..."
              @content-change="formData.content = $event"
              v-if="dialogOpen"
          ></MyEditor>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogOpen = false">取消</el-button>
        <el-button
            type="primary"
            @click="submit"
            :loading="submitting"
        >
          {{ formData.id ? '保存修改' : '立即发布' }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search, Star, View} from '@element-plus/icons-vue'
import {ref, reactive,toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import MyEditor from "@/components/MyEditor.vue";
import MyUpLoad from "@/components/MyUpload.vue";
import router from "@/router/index.js";


const drawerVisible = ref(false)
const unreadCount = ref(0)

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
  days: undefined,
});



getPageList()

/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/route/page", {
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
const formData = ref({
  daysList: []
});
const formRef = ref();

function updateDaysList() {
  formData.value.daysList = Array.from({ length: formData.value.days }, (_, i) => ({
    dayNumber: i + 1,
    title: '',
    locations: '',
    transport: '',
    accommodation: '',
    spots: '',
    description: ''
  }));
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
      request.post("/route/addRoute", formData.value).then(res => {
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
      request.put("/route/update", formData.value).then(res => {
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
    request.delete("/route/delBatch", {data: ids}).then(res => {
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
</script>

<style scoped>
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
.route-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.search-card {
  margin-bottom: 24px;
  border-radius: 8px;

  .search-wrapper {
    display: flex;
    gap: 16px;
    padding: 8px;
  }
}
.search-filter-card {
  margin-bottom: 20px;
}

.search-header {
  margin-bottom: 20px;
}

.search-input {
  max-width: 90%;
  margin: 0 auto;
  border-radius: 20px 0 0 20px; /* 左圆角右直角 */
  width: 90%;
  height: 50px;
}

.filter-area {
  display: flex;
  gap: 30px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
}

.filter-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.route-list {
  margin-top: 20px;
}

.route-item {
  margin-bottom: 20px;
}

.route-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.route-card:hover {
  transform: translateY(-5px);
}

.card-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.card-content {
  padding: 15px 0;
}

.route-title {
  margin: 0 0 12px;
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.route-tags {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}
/* 用户信息叠加层样式 */
.user-overlay {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  padding: 4px 8px;
  border-radius: 20px;
  z-index: 2;
}

.user-avatar {
  margin-right: 8px;
}

.username {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 统计信息叠加层 */
.stats-overlay {
  position: absolute;
  bottom: 10px;
  right: 10px;
  display: flex;
  gap: 15px;
  background: rgba(0, 0, 0, 0.6);
  padding: 6px 12px;
  border-radius: 20px;
  color: white;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.stat-item .el-icon {
  font-size: 16px;
}

/* 底部互动按钮 */
.action-footer {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.liked {
  background-color: #ff6b6b;
  border-color: #ff6b6b;
  color: white;
}

/* 调整原有卡片内容间距 */
.card-content {
  padding: 15px;
}

.route-title {
  margin-bottom: 8px;
}

.route-tags {
  margin-bottom: 8px;
}
.route-info {
  margin: 12px 0;
  font-size: 12px;
  color: #666;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 8px;
}
.travel-content {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;    /* 显示行数 */
  overflow: hidden;
  line-height: 1.5;        /* 行高建议设置 */
  max-height: 4.5em;       /* 行高×行数 = 3×1.5=4.5 */
}
.route-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.current-price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: 600;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
  margin-left: 8px;
}

.pagination-wrapper {
  margin-top: 30px;
  text-align: center;
}
.search-button {
  min-width: 88px; /* 与按钮图标尺寸匹配 */
  height: 50px; /* 与输入框高度一致 */
  border-radius: 0 4px 4px 0; /* 右圆角左直角 */
  margin-left: 0px; /* 按钮与输入框间距 */
}
</style>
