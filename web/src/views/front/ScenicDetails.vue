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
          <!-- 评论数量 -->
          <div class="rating-info">
            <div class="review-stats">
              <span class="review-count">{{ $t('scenic.reviewsCount', { count: info.countComment }) }}</span>
            </div>
          </div>
        </div>
        <!-- 右侧内容 -->
        <div class="header-right">
          <div class="price-box">
            <div class="price">
              <span class="price-label">{{ $t('scenic.priceLabel') }}</span>
              <span class="current-price">￥{{ info.price }}</span>
            </div>
            <el-button
                type="primary"
                size="large"
                @click="openBookingDialog(info)"
                class="booking-btn"
            >
              {{ $t('scenic.bookTicket') }}
            </el-button>
          </div>
          <div class="meta-info">
            <el-tag type="info" effect="dark" size="small">
              {{ $t('scenic.addressLabel') }} {{ info.address }}
            </el-tag>
            <el-tag
                type="info"
                effect="dark"
                size="small"
                class="opening-hours"
            >
              {{ $t('scenic.openingHours') }} {{ info.openingHours }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="cover-image">
        <el-image
            :src="info.coverImage"
            fit="cover"
            style="width: 100%; min-height: 60vh; max-height: 80vh;"
            :preview-src-list="previewImages"
            :initial-index="0"
            :z-index="10000"
            preview-teleported
            :hide-on-click-modal="true"
        >
          <template #error>
            <div class="image-placeholder">
              <el-icon name="Picture" style="font-size: 48px; color: #cccccc;"></el-icon>
              <p>{{ $t('scenic.imageLoadFailed') }}</p>
            </div>
          </template>
        </el-image>
      </div>
      
      <!-- 详情图展示 - 轮播 -->
      <div v-if="info.detailImages && info.detailImages.length > 0" class="detail-images-section">
        <h3>{{ $t('scenic.photoGallery') }}</h3>
        <div class="images-carousel-wrapper">
          <button 
            class="carousel-arrow carousel-arrow-left" 
            @click="prevImage"
            :disabled="currentImageIndex === 0"
            :class="{ 'is-disabled': currentImageIndex === 0 }"
          >
            <el-icon><ArrowLeft /></el-icon>
          </button>
          
          <div class="carousel-container">
            <div 
              class="carousel-track" 
              :style="{ transform: `translateX(-${currentImageIndex * (100 / 4)}%)` }"
            >
              <div 
                v-for="(image, index) in info.detailImages"
                :key="index"
                class="carousel-item"
              >
                <el-image
                  :src="image.url"
                  fit="cover"
                  class="carousel-image"
                  :preview-src-list="detailImageUrls"
                  :initial-index="index"
                  :z-index="10000"
                  preview-teleported
                  :hide-on-click-modal="true"
                >
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                      <span>{{ $t('scenic.imageLoadFailed') }}</span>
                    </div>
                  </template>
                </el-image>
              </div>
            </div>
          </div>
          
          <button 
            class="carousel-arrow carousel-arrow-right" 
            @click="nextImage"
            :disabled="currentImageIndex >= info.detailImages.length - 4"
            :class="{ 'is-disabled': currentImageIndex >= info.detailImages.length - 4 }"
          >
            <el-icon><ArrowRight /></el-icon>
          </button>
        </div>
      </div>
      
      <el-divider style="margin: 25px 0 20px; border-color: #eee;"></el-divider>

      <div class="content-section">
        <div class="description">
          <h3>{{ $t('scenic.scenicIntro') }}</h3>
          <p>{{ info.description }}</p>
        </div>
      </div>
      <!-- 分页式评论模块 -->
      <div class="comments-section">
        <div class="comments-header">
          <!-- 左侧：评论信息 -->
          <div class="rating-summary">
            <h3 class="section-title">{{ $t('scenic.userReviews') }}</h3>
            <div class="rating-display">
              <div class="rating-details">
                <div class="review-count">{{ $t('scenic.reviewsCount', { count: info.countComment }) }}</div>
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
            {{ $t('scenic.writeReview') }}
          </el-button>
        </div>
        <!-- 新增评论表单 -->
        <div v-if="showCommentForm" class="comment-form">
          <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              :placeholder="$t('comment.writeExperience')"
              class="comment-textarea"
          />
          <div class="form-actions">
            <el-button @click="cancelComment">{{ $t('comment.cancel') }}</el-button>
            <el-button
                type="primary"
                @click="submitComment"
                :disabled="!newComment"
            >
              {{ $t('comment.submitReview') }}
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
    <!-- 底部操作栏 -->
    <div class="action-footer">
      <el-tooltip :content="isFavorited ? $t('scenic.unfavoriteTooltip') : $t('scenic.favoriteTooltip')" placement="top">
        <el-button
            circle
            size="large"
            :type="isFavorited ? 'warning' : 'default'"
            :icon="isFavorited ? StarFilled : Star"
            @click="handleFavorite"
            :class="['favorite-button', { 'is-favorited': isFavorited }]"
        />
      </el-tooltip>
    </div>
    <el-dialog
        v-model="bookingDialogVisible"
        :title="$t('order.bookingFor', { name: currentScenic?.name })"
        width="600px"
    >
      <el-form
          ref="bookingFormRef"
          :model="buyTicketForm"
          label-width="100px"
          :rules="bookingRules"
      >
        <el-form-item :label="$t('order.visitDate')" prop="visitDate" :rules="[{required:true,message:$t('form.pleaseSelect', { field: $t('order.visitDate') }),trigger:[ 'blur','change']}]">
          <el-date-picker
              v-model="buyTicketForm.visitDate"
              type="date"
              :placeholder="$t('order.selectDate')"
              value-format="YYYY-MM-DD"
              :disabled-date="disabledDate"
          />
        </el-form-item>

        <el-form-item :label="$t('order.quantity')" prop="quantity" >
          <el-input-number
              v-model="buyTicketForm.number"
              :min="1"
              :max="10"
              controls-position="right"
          />
        </el-form-item>

        <el-form-item :label="$t('order.totalPrice')">
        <span class="total-price">
          ￥{{ totalPrice}}
        </span>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="bookingDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button
            type="primary"
            @click="confirmBooking"
        >
          {{ $t('order.confirmBooking') }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 支付对话框 -->
    <PaymentDialog
      v-model="paymentDialogVisible"
      :order-no="paymentOrderNo"
      :total-amount="paymentAmount"
      @success="handlePaymentSuccess"
    />
  </div>
</template>
<script setup>
import {computed, ref, toRaw, watch, onMounted} from "vue";
import {useRoute} from "vue-router";
import request from "@/utils/http.js";
import {ElMessage} from "element-plus";
import {ChatDotSquare, Star, StarFilled, Picture, ArrowLeft, ArrowRight} from "@element-plus/icons-vue";
import PaymentDialog from "@/components/PaymentDialog.vue";
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();


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

// 支付相关
const paymentDialogVisible = ref(false)
const paymentOrderNo = ref('')
const paymentAmount = ref(0)

// 评价相关状态
const showCommentForm = ref(false)
const newComment = ref('')
const submitting = ref(false)
const commentList = ref([]);
const isFavorited = ref(false)

// 图片预览列表
const previewImages = computed(() => {
  const images = [info.value.coverImage]
  if (info.value.detailImages && info.value.detailImages.length > 0) {
    images.push(...info.value.detailImages.map(img => img.url))
  }
  return images.filter(img => img) // 过滤掉空值
})

// 详情图URL列表
const detailImageUrls = computed(() => {
  if (info.value.detailImages && info.value.detailImages.length > 0) {
    return info.value.detailImages.map(img => img.url)
  }
  return []
})

// 轮播当前索引
const currentImageIndex = ref(0)

// 上一张
function prevImage() {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  }
}

// 下一张
function nextImage() {
  if (currentImageIndex.value < info.value.detailImages.length - 4) {
    currentImageIndex.value++
  }
}

// 监听路由变化时重置轮播索引
watch(() => route.params.id, () => {
  currentImageIndex.value = 0
})

// 监听路由参数变化，重新加载数据
watch(() => route.params.id, (newId) => {
  if (newId) {
    id.value = newId
    searchForm.value.scenicId = newId
    // 重新加载数据（getInfo会自动更新收藏状态）
    getInfo()
    loadComments()
  }
}, { immediate: true })

// 组件挂载时加载数据
onMounted(() => {
  getInfo()
  loadComments()
})

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
    console.log('后端返回的完整数据:', res.data)
    // 处理详情图数组
    const detailImages = res.data.detailImages
        ? res.data.detailImages.split(',').map((url, index) => ({
          id: index + 1,
          url: url.trim(),
          caption: ''
        }))
        : [];
    info.value = {
      ...res.data,
      detailImages: detailImages
    };
    isFavorited.value = res.data.favorited || false
    console.log('景点收藏状态:', isFavorited.value, '原始值:', res.data.favorited)
    console.log('详情图数组:', detailImages)
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
        message: $t('order.validationFailed'),
        type: 'warning'
      });
      return
    }
    buyTicketForm.value.id = currentScenic.value.id
    buyTicketForm.value.totalPrice = totalPrice.value
    request.post("/orderInfo/confirmBooking", buyTicketForm.value).then(res => {
      if (!res || res.code !== 200) {
        return
      }
      ElMessage({
        message: $t('order.bookingSuccess'),
        type: "success"
      });
      bookingDialogVisible.value = false
      
      // Бронирование успешно, открыть диалог оплаты
      paymentOrderNo.value = res.data // Номер заказа от сервера
      paymentAmount.value = totalPrice.value
      paymentDialogVisible.value = true
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
  searchForm.value.typeCode = 'SCENIC'
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
      typeCode: 'SCENIC',
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
    ElMessage.success($t('comment.reviewSubmitted'))
  } catch (error) {
    ElMessage.error(error.response?.data?.message || $t('comment.submitFailed'))
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
  showCommentForm.value = !showCommentForm.value
}
//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
}

// 收藏功能
function handleFavorite() {
  let formData = {
    typeCode: 'SCENIC',
    associationId: id.value,
  }
  let favorited = isFavorited.value
  console.log('当前收藏状态:', favorited)
  //收藏
  if (!favorited) {
    request.post("/favorite/add", formData).then(res => {
      if (!res || res.code !== 200) {
        return
      }
      ElMessage({
        message: $t('scenic.favoriteAdded'),
        type: 'success'
      });
      // 更新状态
      isFavorited.value = true
      console.log('收藏后状态:', isFavorited.value)
    }).catch(err => {
      console.error('收藏失败:', err)
      // 如果是已经收藏过了，更新状态并重新获取数据
      if (err.response?.data?.msg?.includes('已经收藏')) {
        ElMessage({
          message: $t('scenic.alreadyFavorited'),
          type: 'info'
        });
        // 重新获取数据以同步状态
        getInfo()
      }
    })
  } else {
    request.post("/favorite/del", formData).then(res => {
      if (!res || res.code !== 200) {
        return
      }
      ElMessage({
        message: $t('scenic.favoriteRemoved'),
        type: 'success'
      });
      // 更新状态
      isFavorited.value = false
      console.log('取消收藏后状态:', isFavorited.value)
    }).catch(err => {
      console.error('取消收藏失败:', err)
      // 如果取消失败，重新获取数据以同步状态
      getInfo()
    })
  }
}

/**
 * Обработчик успешной оплаты
 */
function handlePaymentSuccess() {
  ElMessage.success($t('order.paySuccess'))
  // Можно перейти на страницу заказов или выполнить другие действия
}
</script>
<style scoped>
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

.detail-card {
  border-radius: 12px;
  overflow: visible;
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

.detail-images-section {
  margin: 30px 0;
}

.detail-images-section h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409eff;
}

.images-carousel-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
}

.carousel-arrow {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(31, 45, 61, 0.7);
  border: none;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  z-index: 10;
}

.carousel-arrow:hover:not(.is-disabled) {
  background-color: rgba(31, 45, 61, 0.9);
  transform: scale(1.1);
}

.carousel-arrow.is-disabled {
  background-color: rgba(31, 45, 61, 0.3);
  cursor: not-allowed;
  opacity: 0.5;
}

.carousel-container {
  flex: 1;
  overflow: hidden;
  border-radius: 8px;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease;
}

.carousel-item {
  flex: 0 0 25%;
  padding: 0 8px;
}

.carousel-image {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.carousel-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.cover-image {
  position: relative;
  overflow: visible;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: #f5f7fa;
  color: #c0c4cc;
  border-radius: 8px;
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

.rating-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
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

/* 确保图片预览器在最顶层 */
:deep(.el-image-viewer__wrapper) {
  z-index: 10001 !important;
}

:deep(.el-image-viewer__mask) {
  z-index: 10000 !important;
}

/* 底部操作栏 */
.action-footer {
  position: fixed;
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

  .favorite-button {
    transition: all 0.3s ease;
    
    &.is-favorited {
      background: #E6A23C;
      border-color: #E6A23C;
      color: #fff;
      
      &:hover {
        background: #d89b38;
        border-color: #d89b38;
        transform: scale(1.05);
      }
    }
    
    &:not(.is-favorited) {
      &:hover {
        color: #E6A23C;
        border-color: #E6A23C;
      }
    }
  }
}
</style>
