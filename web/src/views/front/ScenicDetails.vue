<template>
  <div class="detail-container">
    <el-card
        class="detail-card"
        shadow="hover"
        :body-style="{ padding: '30px 40px' }"
    >
      <div class="header-section">
        <!-- 左侧内容 -->
        <div class="header-left">
          <div class="title">
            <el-icon name="View" style="vertical-align: middle; margin-right: 10px;"></el-icon>
            <h1>{{ info.name }}</h1>
          </div>
          <!-- 评分和评论数量 -->
          <div class="rating-info">
            <div class="rating-score">
              <span class="score">{{ info.score }}</span>
              <span class="full-score">/5分</span>
            </div>
            <div class="review-stats">
              <el-rate
                  v-model="info.score"
                  :max="5"
                  disabled
                  allow-half
                  class="rating-stars"
              ></el-rate>
              <span class="review-count">{{ info.countComment }}条点评</span>
            </div>
          </div>
        </div>
        <!-- 右侧内容 -->
        <div class="header-right">
          <div class="price-box">
            <div class="price">
              <span class="price-label">价格：</span>
              <span class="current-price">￥{{ info.price }}</span>
            </div>
            <el-button
                type="primary"
                size="large"
                @click="openBookingDialog(info)"
                class="booking-btn"
            >
              预定门票
            </el-button>
          </div>
          <div class="meta-info">
            <el-tag type="info" effect="dark" size="small">
              地址：{{ info.address }}
            </el-tag>
            <el-tag
                type="info"
                effect="dark"
                size="small"
                class="opening-hours"
            >
              开放时间：{{ info.openingHours }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="cover-image">
        <el-image
            :src="info.coverImage"
            fit="cover"
            style="width: 100%; min-height: 60vh; max-height: 80vh;"
            :preview-src-list="[info.coverImage]"
        >
          <template #error>
            <div class="image-placeholder">
              <el-icon name="Picture" style="font-size: 48px; color: #cccccc;"></el-icon>
              <p>图片加载失败</p>
            </div>
          </template>
        </el-image>
      </div>
      <el-divider style="margin: 25px 0 20px; border-color: #eee;"></el-divider>

      <div class="content-section">
        <div class="description">
          <h3>景点介绍</h3>
          <p>{{ info.description }}</p>
        </div>
      </div>
      <!-- 分页式评论模块 -->
      <div class="comments-section">
        <div class="comments-header">
          <!-- 左侧：评分信息 -->
          <div class="rating-summary">
            <h3 class="section-title">用户评价</h3>
            <div class="rating-display">
              <div class="total-score">
                <span class="score">{{ info.score }}</span>
                <span class="full-mark">/5</span>
              </div>
              <div class="rating-details">
                <el-rate
                    v-model="info.score"
                    :max="5"
                    disabled
                    allow-half
                    class="rating-stars"
                />
                <div class="review-count">{{ info.countComment }} 条点评</div>
              </div>
            </div>
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
          <div class="form-header">
            <span>你的评分：</span>
            <el-rate
                v-model="newRating"
                :max="5"
                allow-half
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            />
          </div>
          <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="写下你的真实体验..."
              class="comment-textarea"
          />
          <div class="form-actions">
            <el-button @click="cancelComment">取消</el-button>
            <el-button
                type="primary"
                @click="submitComment"
                :disabled="!newRating || !newComment"
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
                <el-rate
                    v-model="comment.score"
                    :max="5"
                    disabled
                    allow-half
                    class="user-rating"
                ></el-rate>
              </div>
              <span class="comment-date">{{ comment.createTime }}</span>
            </div>
            <div class="comment-content">
              {{ comment.content }}
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
    </el-card>
    <el-dialog
        v-model="bookingDialogVisible"
        :title="`预定 - ${currentScenic?.name}`"
        width="600px"
    >
      <el-form
          ref="bookingFormRef"
          :model="buyTicketForm"
          label-width="100px"
          :rules="bookingRules"
      >
        <el-form-item label="游玩日期" prop="visitDate" :rules="[{required:true,message:'请选择日期',trigger:[ 'blur','change']}]">
          <el-date-picker
              v-model="buyTicketForm.visitDate"
              type="date"
              placeholder="选择游玩日期"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item label="购买数量" prop="quantity" >
          <el-input-number
              v-model="buyTicketForm.number"
              :min="1"
              :max="10"
              controls-position="right"
          />
        </el-form-item>

        <el-form-item label="总价">
        <span class="total-price">
          ￥{{ totalPrice}}
        </span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button
            type="primary"
            @click="confirmBooking"
        >
          确认预定
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>
<script setup>
import {computed, ref, toRaw} from "vue";
import {useRoute} from "vue-router";
import request from "@/utils/http.js";
import {ElMessage} from "element-plus";
import {ChatDotSquare,Star, StarFilled} from "@element-plus/icons-vue";


const drawerVisible = ref(false)
const unreadCount = ref(0)

const route = useRoute()
const id = ref(route.params.id)
const info = ref({});
const pageInfo = ref({
  //当前页
  pageNum: 1,
  //分页大小
  pageSize: 10,
  //总条数
  total: 0
});

const searchForm = ref({
  scenicId: id.value,
});
const bookingFormRef = ref()
const bookingDialogVisible = ref(false)
const currentScenic = ref(null)
const buyTicketForm = ref({
  id: '',
  visitDate: '',
  ticketType: '',
  number: 1,
  totalPrice: 0,
})

// 评价相关状态
const showCommentForm = ref(false)
const newRating = ref(0)
const newComment = ref('')
const submitting = ref(false)
const commentList = ref([]);

getInfo()
loadComments()

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
  request.get("/scenicInfo/selectById/" + id.value,).then(res => {
    // 处理详情图数组
    const detailImages = res.data.detailImages
        ? res.data.detailImages.split(',').map((url, index) => ({
          id: index + 1,
          url: url,
          caption: ''
        }))
        : [];
    info.value = {
      ...res.data,
      detailImages: detailImages
    };
  })
  //增加浏览量
  request.get("/scenicInfo/putViewCount/" + id.value,).then(res => {
  })
}
function openBookingDialog(row) {
  currentScenic.value = row
  buyTicketForm.value = {
    visitDate: '',
    number: 1,
    totalPrice: 0,
  };
  bookingDialogVisible.value = true
}
//付款
function confirmBooking() {
  bookingFormRef.value.validate((valid) => {
    if (!valid){
      ElMessage({
        message: "验证失败，请检查表单!",
        type: 'warning'
      });
      return
    }
    buyTicketForm.value.id = currentScenic.value.id
    buyTicketForm.value.totalPrice = totalPrice
    request.post("/orderInfo/confirmBooking", buyTicketForm.value).then(res => {
      if (!res||res.code !== 200) {
        return
      }
      ElMessage({
        message: "预定成功",
        type: "success"
      });
      bookingDialogVisible.value = false
    })
  })
}
const totalPrice = computed(() => {
  return currentScenic.value?.price * buyTicketForm.value.number || 0;
});
function disabledDate(time) {
  const today = new Date();
  // 设置时间为当天0点，以便比较日期而不是具体时间
  today.setHours(0, 0, 0, 0);
  return time < today;
}
// 加载评论
function loadComments() {
  searchForm.value.typeCode = '景点'
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
      typeCode: '景点',
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
  newRating.value = 0
  newComment.value = ''
}
// 处理写评论按钮
function handleWriteComment(info){
  showCommentForm.value = !showCommentForm.value
}
//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
}
</script>
<style scoped>
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 30px;
}

.title {
  font-size: 32px;
  color: #2c3e50;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.content-section {
  margin-top: 30px;
}

.description {
  margin-bottom: 30px;
}
.current-price {
  color: #ff6b6b;
  font-size: 18px;
  font-weight: 600;
}

.action-section {
  margin-top: 40px;
  text-align: center;
}
.rating-info {
  display: flex;
  align-items: center;
  margin: 15px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}
.header-left {
  flex: 1;
  margin-right: 40px;
}

.header-right {
  width: 280px;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.price-box {
  text-align: right;
  margin-bottom: 15px;
}
.price {
  font-size: 16px;
  margin-bottom: 12px;
}

.current-price {
  color: #ff6b6b;
  font-size: 24px;
  font-weight: 600;
}

.booking-btn {
  width: 120px;
  height: 40px;
  margin-top: 10px;
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.opening-hours {
  margin-top: 8px;
}
.rating-score {
  margin-right: 30px;
  text-align: center;
  .score {
    font-size: 28px;
    color: #ff9900;
    font-weight: 700;
  }
  .full-score {
    color: #999;
    font-size: 14px;
  }
}

.review-stats {
  flex: 1;
  .rating-stars {
    margin-right: 15px;
  }
  .review-count {
    color: #666;
    font-size: 14px;
  }
}

/* 评论模块样式 */
.comments-section {
  margin-top: 40px;
  .section-title {
    font-size: 20px;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #f5f5f5;
  &:last-child {
    border-bottom: none;
  }
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  .user-avatar {
    margin-right: 12px;
  }
  .user-meta {
    flex: 1;
    .username {
      font-weight: 500;
      color: #333;
    }
    .user-rating {
      margin-top: 4px;
    }
  }
  .comment-date {
    color: #999;
    font-size: 12px;
  }
}

.comment-content {
  color: #666;
  line-height: 1.6;
  margin-left: 48px;
}

.pagination {
  margin-top: 30px;
  justify-content: center;
}
.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.rating-summary {
  display: flex;
  align-items: center;
  gap: 30px;
}

.section-title {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 15px;
}

.total-score {
  display: flex;
  align-items: baseline;
  .score {
    font-size: 36px;
    color: #ff9900;
    font-weight: 600;
  }
  .full-mark {
    color: #999;
    font-size: 16px;
  }
}

.rating-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
  .rating-stars {
    margin-left: -3px; /* 调整星星对齐 */
  }
  .review-count {
    color: #666;
    font-size: 13px;
  }
}

.new-comment-btn {
  padding: 10px 20px;
  .icon-edit {
    margin-right: 8px;
  }
}
</style>
