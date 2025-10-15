<template>
  <div class="detail-container">
    <div class="detail-wrapper">
      <!-- 头部信息 -->
      <div class="header-section">
        <div class="travel-meta-header">
          <h1 class="title">{{ info.title }}</h1>
          <div class="time-meta">
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>出行时间：{{ formatDate(info.travelTime) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>行程天数：{{ info.days }} 天</span>
            </div>
          </div>
        </div>

        <div class="author-bar">
          <div class="author-info">
            <el-avatar :size="48" :src="info.user?.avatarUrl" />
            <div class="author-meta">
              <span class="author-name">{{ info.user?.username }}</span>
              <span class="publish-time">{{ formatDate(info.createTime) }} 发布</span>
            </div>
          </div>
          <div class="right-actions">
            <div class="stats-bar">
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span class="count">{{ info.viewCount | formatNumber }}</span>
              </div>
              <div class="stat-item">
                <el-icon><Star /></el-icon>
                <span class="count">{{ info.likesCount | formatNumber }}</span>
              </div>
            </div>
            <el-button
                v-if="showDelete"
                type="danger"
                size="small"
                :icon="Delete"
                @click="handleDelete(info)"
                plain
            >
              删除游记
            </el-button>
          </div>
        </div>
      </div>

      <!-- 封面图 -->
      <div class="cover-section">
        <el-image
            :src="info.cover"
            fit="cover"
            class="main-cover"
            :preview-src-list="[info.cover]"
            :hide-on-click-modal="true"
            :z-index="9999"
            preview-teleported
        >
          <template #error>
            <div class="cover-error">
              <el-icon><Picture /></el-icon>
              <span>封面加载失败</span>
            </div>
          </template>
        </el-image>
      </div>

      <!-- 内容区域 -->
      <div class="content-section">
        <article class="travel-content" v-html="info.content"></article>
      </div>

      <!-- 分页式评论模块 -->
      <div class="comments-section">
        <div class="comments-header">
          <!-- 左侧：评分信息 -->
          <div class="rating-summary">
            <h3 class="section-title">用户评论</h3>
            <div class="review-count">{{ info.commentsCount }} 条评论</div>
          </div>
          <!-- 右侧：写评论按钮 -->
          <el-button
              type="primary"
              plain
              class="new-comment-btn"
              @click="handleWriteComment(info)"
          >
            <el-icon name="EditPen" class="icon-edit" />
            写评论
          </el-button>
        </div>
        <!-- 新增评论表单 -->
        <div v-if="showCommentForm" class="comment-form">
          <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
              class="comment-textarea"
          />
          <div class="form-actions">
            <el-button @click="cancelComment">取消</el-button>
            <el-button
                type="primary"
                @click="submitComment"
                :disabled=" !newComment"
            >
              提交评价
            </el-button>
          </div>
        </div>
        <!--        评论列表-->
        <div class="comment-list">
          <div
              v-for="comment in commentList"
              :key="comment.id"
              class="comment-item"
          >
            <div class="user-info">
              <el-avatar :src="comment.user.avatarUrl" class="user-avatar"></el-avatar>
              <div class="user-meta">
                <span class="username">{{ comment.user.username }}</span>
              </div>
              <span class="comment-date">{{ comment.createTime }}</span>
            </div>
            <div class="comment-content">
              {{ comment.content }}
            </div>
            <!-- 操作栏展开按钮 -->
            <div class="comment-actions">
              <div class="action-buttons">
                <el-button
                    size="small"
                    @click="toggleReplyForm(comment.id)"
                    class="reply-btn"
                >
                  回复
                </el-button>
                <el-button
                    v-if="comment.childList?.length"
                    size="small"
                    @click="toggleChildren(comment)"
                    class="toggle-btn"
                >
                  {{ comment.showChildren ? '收起' : `展开${comment.childList.length}条回复` }}
                </el-button>
              </div>
            </div>
            <!-- 回复表单 -->
            <div v-if="activeReply === comment.id" class="reply-form">
              <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  placeholder="写下你的回复..."
              />
              <div class="form-actions">
                <el-button size="small" @click="cancelReply">取消</el-button>
                <el-button
                    type="primary"
                    size="small"
                    @click="submitReply(comment)"
                    :disabled="!replyContent"
                >
                  提交
                </el-button>
              </div>
            </div>
            <!-- 子评论列表 -->
            <div
                v-if="comment.showChildren"
                class="child-comments-container"
            >
              <div class="child-comments-list">
                <div
                    v-for="child in comment.childList"
                    :key="child.id"
                    class="comment-item is-child"
                >
                  <!-- 子评论内容 -->
                  <div class="user-info">
                    <el-avatar :src="child.user.avatarUrl" class="user-avatar"></el-avatar>
                    <div class="user-meta">
                      <span class="username">{{ child.user.username }}</span>
                    </div>
                    <span class="comment-date">{{ child.createTime }}</span>
                  </div>
                  <div class="comment-content">
                    {{ child.content }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
              @current-change="currentChange"
              @size-change="sizeChange"
              background
              class="pagination"
              layout="prev, pager, next, jumper"
              :page-size="pageInfo.pageSize"
              :current-page="pageInfo.currentPage"
              :total="pageInfo.total"
          />
        </div>
      </div>
      <!-- 底部操作栏 -->
      <div class="action-footer">
        <el-tooltip :content="isLiked ?'取消点赞' : '点个赞'" placement="top">
          <el-button
              circle
              size="large"
              :type="isLiked ? 'success' : 'info'"
              :icon="Pointer"
              @click="handleLike"
              class="like-button"
          />
        </el-tooltip>
      </div>
    </div>

  </div>
</template>
<script setup>
import request from "@/utils/http.js";
import {ref,computed,toRaw} from "vue";
import {useRoute} from "vue-router";
import router from "@/router/index.js";
import tools from "@/utils/tools.js";
import { Calendar, Clock, Delete, Pointer, Star,} from "@element-plus/icons-vue";
import dayjs from "dayjs";
import {ElMessage, ElMessageBox} from "element-plus";

const currentUser = ref(tools.getCurrentUser())

const route = useRoute()
const id = ref(route.params.id)
const info = ref({});
const searchForm = ref({
  typeCode: '游记',
  parentStatus: '1',
  associationId: id.value,
});
const pageInfo = ref({
  //当前页
  pageNum: 1,
  //分页大小
  pageSize: 10,
  //总条数
  total: 0
});
const isLiked = ref(false)
// 评价相关状态
const showCommentForm = ref(false)
const newComment = ref('')
const submitting = ref(false)
const commentList = ref([]);
// 子评论相关
const showChildren = ref(false)
const activeReply = ref(null) // 当前激活的回复表单
const replyContent = ref('')
const currentParent = ref(null) // 当前回复的父评论


const drawerVisible = ref(false)
const unreadCount = ref(0)

loadComments()
getInfo()

/**
 * 改变分页数量
 * @param e
 */
function sizeChange(e) {
  pageInfo.value.pageSize = e
  loadComments()
  console.log(e)
}
/**
 * 选择分页
 * @param e
 */
function currentChange(e) {
  pageInfo.value.pageNum = e
  loadComments()
}
function getInfo() {
  request.get("/travelNote/selectById/" + id.value,).then(res => {
    info.value = res.data
    isLiked.value = res.data.liked
  })
  //增加浏览量
  request.get("/travelNote/putViewCount/" + id.value,).then(res => {
  })
}

function handleDelete(info) {
  let ids = [info.id]
  let title = info.title
  ElMessageBox.confirm(`此操作将永久删除标题为[${title}]的数据, 是否继续?`, '提示', {
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
      router.push('/travelNote')
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消删除'
    });
    tableComponents.value.clearSelection();
  });
}
// 权限判断：只有作者本人或管理员才能删除
const showDelete = computed(() => {
  if (!currentUser.value || !info.value.userId) return false
  
  // 管理员可以删除任何游记
  if (currentUser.value.type === 'ADMIN') {
    return true
  }
  
  // 作者可以删除自己的游记
  return currentUser.value.type === 'USER' && 
         currentUser.value.id === info.value.userId
})
function formatDate(value) {
  if (!value) return '--'
  return dayjs(value).format('YYYY-MM-DD')
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
function handleLike() {
  let formData = {
    typeCode: '游记',
    associationId: id.value,
  }
  let liked = isLiked.value
  //点赞
  if (!liked) {
    request.post("/likes/add", formData).then(res => {
      if (!res) {
        return
      }
      dialogOpen.value = false
      ElMessage({
        type: 'success'
      });
    })
    // 更新界面信息
    getInfo();
  } else {
    request.post("/likes/del", formData).then(res => {
      if (!res) {
        return
      }
      dialogOpen.value = false
      ElMessage({
        type: 'success'
      });
    })
    // 更新界面信息
    getInfo();
  }
}

// 加载评论
function loadComments() {
  searchForm.value.associationId = id.value
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/commentsInfo/page", {
    params: data}
  ).then(res => {
    commentList.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

// 提交评论
function submitComment (info) {
  try {
    submitting.value = true
    let formData = {
      typeCode: '游记',
      associationId: id.value,
      content: newComment.value,
    }
    request.post("/commentsInfo/add", formData).then(res => {
      if (!res) {
        return
      }
      // 更新评论区列表
      cancelComment();
      loadComments();
      getInfo();
      dialogOpen.value = false
    })
    ElMessage.success('评价提交成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
// 取消评论
function cancelComment(){
  showCommentForm.value = false
  newComment.value = ''
}
// 处理写评论按钮
function handleWriteComment(info){
  newComment.value = ''
  showCommentForm.value = !showCommentForm.value
}
// 切换回复表单
const toggleReplyForm = (commentId) => {
  replyContent.value = '';
  activeReply.value = activeReply.value === commentId ? null : commentId
  currentParent.value = commentList.value.find(c => c.id === commentId)
}
// 提交回复
const submitReply = async () => {
  try {
    const formData = {
      typeCode: '游记',
      associationId: id.value,
      content: replyContent.value,
      parentId: currentParent.value.id
    }

    const res = await request.post("/commentsInfo/add", formData)
    if (res) {
      // 更新评论区列表
      cancelComment();
      loadComments();
      getInfo();
      cancelReply()
      ElMessage.success('回复成功')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '回复失败')
  }
}

// 取消回复
const cancelReply = () => {
  activeReply.value = null
  replyContent.value = ''
  currentParent.value = null
}
// 切换方法
const toggleChildren = (comment) => {
  commentList.value.forEach(c => {
    if (c.id !== comment.id) c.showChildren = false
  })
  comment.showChildren = !comment.showChildren
}
//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
}
</script>
<style scoped>
/* 基础变量 */
:root {
  --primary-color: #409EFF;
  --success-color: #67C23A;
  --warning-color: #E6A23C;
  --danger-color: #F56C6C;
  --text-primary: #303133;
  --text-regular: #606266;
  --text-secondary: #909399;
  --border-color: #DCDFE6;
  --background-base: #f5f7fa;
}

.detail-container {
  background: var(--background-base);
  min-height: 100vh;
  padding: 20px 0;
}

.detail-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

/* 头部区域 */
.header-section {
  padding: 32px 40px 24px;
  border-bottom: 1px solid rgba(0,0,0,0.05);

  .title {
    font-size: 2.2rem;
    color: var(--text-primary);
    margin-bottom: 1.2rem;
    line-height: 1.3;
    font-weight: 600;
    letter-spacing: -0.5px;
  }
}

.time-meta {
  display: flex;
  gap: 2rem;
  margin-top: 1rem;

  .meta-item {
    display: flex;
    align-items: center;
    color: var(--text-regular);
    font-size: 0.95rem;

    .el-icon {
      margin-right: 8px;
      font-size: 1.1rem;
      color: var(--primary-color);
    }
  }
}

.author-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding-top: 1.2rem;
  border-top: 1px solid rgba(0,0,0,0.05);

  .author-info {
    display: flex;
    align-items: center;
    gap: 1rem;

    .author-meta {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .author-name {
        font-size: 1rem;
        color: var(--text-primary);
        font-weight: 500;
      }

      .publish-time {
        font-size: 0.9rem;
        color: var(--text-secondary);
      }
    }
  }

  .right-actions {
    display: flex;
    align-items: center;
    gap: 1.5rem;
  }
}

.stats-bar {
  display: flex;
  gap: 1.8rem;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: var(--text-regular);

    .el-icon {
      font-size: 1.2rem;
    }

    .count {
      font-weight: 500;
      color: var(--text-primary);
    }
  }
}

/* 封面图区域 */
.cover-section {
  position: relative;
  margin: 0 40px;
  border-radius: 8px;
  overflow: hidden;
  height: 480px;
  background: #f8f9fa;

  .main-cover {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.02);
    }
  }

  .cover-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: var(--text-secondary);

    .el-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
    }
  }
}

/* 内容区域 */
.content-section {
  padding: 32px 40px;

  .travel-content {
    line-height: 1.8;
    font-size: 1rem;
    color: var(--text-regular);

    :deep(img) {
      max-width: 100%;
      height: auto;
      margin: 1.5rem 0;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }

    :deep(h2) {
      font-size: 1.5rem;
      color: var(--text-primary);
      margin: 2rem 0 1.2rem;
      padding-bottom: 0.5rem;
      border-bottom: 1px solid var(--border-color);
    }

    :deep(h3) {
      font-size: 1.3rem;
      margin: 1.5rem 0 1rem;
    }

    :deep(blockquote) {
      margin: 1.5rem 0;
      padding: 1rem 1.5rem;
      background: #f8f9fa;
      border-left: 4px solid var(--primary-color);
      color: var(--text-secondary);
    }
  }
}

/* 评论区域 */
.comments-section {
  padding: 32px 40px;
  background: #fafbfc;
  position: relative;

  .comments-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;

    .rating-summary {
      .section-title {
        font-size: 1.3rem;
        color: var(--text-primary);
        margin-bottom: 0.5rem;
      }

      .review-count {
        color: var(--text-secondary);
        font-size: 0.95rem;
      }
    }
  }
}

.comment-form {
  background: #fff;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);

  .form-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;

    .el-rate {
      transform: translateY(2px);
    }
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 1rem;
  }
}

.comment-list {
  .comment-item {
    padding: 1rem;
    background: #fff;
    border-radius: 8px;
    margin-bottom: 1rem;
    transition: transform 0.2s ease;
    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    &:hover {
      transform: translateY(-2px);
    }
    .user-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      margin-bottom: 0.5rem;
      .user-avatar {
        flex-shrink: 0;
      }

      .user-meta {
        flex: 1;

        .username {
          font-weight: 500;
          color: var(--text-primary);
        }
        .user-rating {
          :deep(.el-rate__icon) {
            font-size: 1rem;
          }
        }
      }
      .comment-date {
        color: var(--text-secondary);
        font-size: 0.9rem;
      }
    }
    .comment-content {
      color: var(--text-regular);
      line-height: 0.1;
      padding-left: 64px;
    }
  }
}
/* 新增评论相关样式 */
.comment-item {
  position: relative;
  margin-bottom: 1rem;
  transition: all 0.2s ease;

  &.is-child {
    margin-left: 0.5rem;
    margin-bottom: 0.3rem;
    background: var(--background-base);

    &::before {
      content: '';
      position: absolute;
      left: -1.5rem;
      top: 1.5rem;
      width: 1rem;
      height: calc(100% - 2rem);
      border-left: 2px solid var(--border-color);
      border-bottom: 2px solid var(--border-color);
    }
  }
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;

  .action-buttons {
    display: flex;
    gap: 0.8rem;
  }
  .toggle-btn {
    order: 1;
    background: var(--background-base);
    border: 1px solid var(--border-color);
    color: var(--text-secondary);
    &:hover {
      border-color: var(--primary-color);
      color: var(--primary-color);
    }
  }

  .reply-btn {
    order: 2;
    background: var(--background-base);
    border: 1px solid var(--border-color);
    color: var(--text-secondary);
    &:hover {
      border-color: var(--primary-color);
      color: var(--primary-color);
    }
  }
}

.reply-to {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-left: 0.5rem;
  &::before {
    content: '·';
    margin-right: 0.3rem;
  }
}

.reply-form {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;

  .form-actions {
    margin-top: 1rem;
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
  }
}

.child-comments {
  margin-top: 0.5rem;
  border-left: 2px solid var(--border-color);
  padding-left: 1rem;
}
.pagination-wrapper {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}

/* 底部操作栏 */
.action-footer {
  position: fixed; /* 从sticky改为fixed */
  right: 8%;
  bottom: 3%;
  padding: 12px;
  margin: 0;
  border-radius: 40px;
  background: rgba(255,255,255,0.5);
  box-shadow: 0 6px 16px rgba(0,0,0,0.12);
  transform: translateY(0);
  z-index: 2000;

  .el-button {
    + .el-button {
      margin-left: 8px;
    }
  }

  .like-button {
    &.is-liked {
      background: var(--success-color);
      border-color: var(--success-color);
    }
  }
}
.child-comments-container {
  max-height: 430px; /* 固定高度 */
  overflow-y: auto;  /* 启用滚动 */
  margin-top: 8px;
  padding-right: 8px; /* 给滚动条留出空间 */

  /* 自定义滚动条样式 */
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background: var(--border-color);
    border-radius: 4px;
  }
}
/* 响应式设计 */
@media (max-width: 768px) {
  .child-comments-list {
    max-height: 100px;
  }
  .comment-actions .action-buttons {
    flex-direction: column;
    width: 100%;

    .el-button {
      width: 100%;
      justify-content: center;
    }
  }
  /* 子评论优化 */
  .child-comments-container {
    margin-top: 0.5rem;
    margin-left: 44px;
    position: relative;
    max-height: 300px; /* 新增固定高度 */
    overflow-y: auto; /* 新增滚动条 */

    &::before {
      content: "";
      position: absolute;
      left: -20px;
      top: 0;
      bottom: 0;
      width: 2px;
      background: var(--border-color);
    }

    .child-comments-list {
      max-height: 300px;
      overflow-y: auto;
      padding-right: 8px;

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-thumb {
        background: var(--border-color);
        border-radius: 4px;
      }
    }

    .comment-item.is-child {
      padding: 0.8rem 1rem;
      margin-bottom: 0.3rem;
      background: var(--background-base);
      border-radius: 6px;

      &::before {
        display: none;
      }

      .user-avatar {
        width: 32px;
        height: 32px;
      }

      .comment-content {
        padding-left: 40px;
      }
    }
  }

  .comment-item.is-child {
    margin-left: 1.5rem;

    &::before {
      left: -1rem;
      width: 0.8rem;
    }
  }
  .detail-wrapper {
    margin: 0 12px;
    border-radius: 8px;
  }

  .child-comments-list {
    max-height: 200px;
    overflow-y: auto;
    padding: 0.5rem 1rem;

    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--border-color);
      border-radius: 4px;
    }
  }

  .comment-item.is-child {
    margin: 0.5rem 0;
    padding: 1rem;
    background: white;
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  }


  @keyframes bounce {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-3px); }
  }
  .header-section,
  .content-section,
  .comments-section {
    padding: 24px 20px;
  }

  .header-section .title {
    font-size: 1.8rem;
  }

  .cover-section {
    margin: 0 20px;
    height: 300px;
  }

  .time-meta {
    flex-direction: column;
    gap: 0.8rem;
  }

  .author-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;

    .stats-bar {
      width: 100%;
      justify-content: space-between;
    }
  }

  .comment-form .form-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .comment-item .comment-content {
    padding-left: 0;
  }

  .action-footer {
    margin: 10px 20px;
    padding: 10px;
    justify-content: center;

    .el-button {
      padding: 8px 12px;
    }
  }
}

@media (max-width: 480px) {
  .comment-item .user-info {
    flex-wrap: wrap;

    .comment-date {
      width: 100%;
      margin-top: 0.5rem;
    }
  }
}
</style>
