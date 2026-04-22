<template>
  <div class="home-container">
    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 景点推荐板块 -->
      <el-card shadow="hover" class="section-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">{{ $t('scenic.featured') }}</span>
            <el-button type="primary" link @click="router.push('/front/scenic')">
              {{ $t('scenic.moreScenic') }}
              <el-icon>
                <ArrowRight/>
              </el-icon>
            </el-button>
          </div>
        </template>
        <!--        景点-->
        <div class="three-column-grid">
          <div v-for="item in scenicList"
               :key="item.id"
               class="scenic-card"
               @click="router.push('/front/scenicDetails/'+item.id)">
            <el-image :src="item.coverImage" class="scenic-cover" fit="cover">
              <template #error>
                <div class="image-error">
                  <el-icon>
                    <Picture/>
                  </el-icon>
                  <span>{{ $t('scenic.imageLoadFailed') }}</span>
                </div>
              </template>
            </el-image>
            <div class="card-content">
              <h4 class="scenic-title">{{ item.name }}</h4>
              <div class="scenic-tags">
                <el-tag type="info" size="small">{{ item.categoryType }}</el-tag>
              </div>
              <div class="scenic-stats">
                <div class="stat-item">
                  <el-icon><Star /></el-icon>
                  <span>{{ item.countFavorite || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>{{ item.countComment || 0 }}</span>
                </div>
              </div>
              <div class="scenic-info">
                <div class="info-item">
                  <el-icon>
                    <Location/>
                  </el-icon>
                  <span class="address">{{ item.address }}</span>
                </div>
                <div class="info-item">
                  <el-icon>
                    <Clock/>
                  </el-icon>
                  <span>{{ item.openingHours || '08:00-18:00' }}</span>
                </div>
                <div class="scenic-footer">
                  <div class="price">
                    <span class="current-price">￥{{ item.price }}</span>
                    <span class="original-price">¥{{ item.originalPrice }}</span>
                  </div>
                  <el-button type="primary" size="small" @click.stop="openBookingDialog(item)">{{ $t('scenic.bookNow') }}</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 游记板块 -->
      <el-row :gutter="24" class="double-section">
        <el-col :span="24">
          <el-card shadow="hover" class="section-card">
            <template #header>
              <div class="card-header">
                <span class="header-title">{{ $t('travelNote.hotNotes') }}</span>
                <el-button type="primary" link @click="router.push('/front/travelNote')">
                  {{ $t('travelNote.moreNotes') }}
                  <el-icon>
                    <ArrowRight/>
                  </el-icon>
                </el-button>
              </div>
            </template>
            <el-scrollbar height="520px">
              <div class="travel-notes-list">
                <div v-for="item in travelNotes"
                     :key="item.id"
                     class="note-item"
                     @click="router.push('/front/travelDetails/'+item.id)">
                  <el-image :src="item.cover" class="note-cover" fit="cover">
                    <template #error>
                      <div class="image-error">
                        <el-icon>
                          <Picture/>
                        </el-icon>
                        <span>{{ $t('scenic.imageLoadFailed') }}</span>
                      </div>
                    </template>
                  </el-image>
                  <div class="note-content">
                    <h5 class="note-title">{{ item.title }}</h5>
                    <div class="note-meta">
                      <el-avatar :size="24" :src="item.user?.avatarUrl"/>
                      <span class="author">{{ item.user?.name }}</span>
                      <el-icon>
                        <View/>
                      </el-icon>
                      {{ item.viewCount || 0 }}
                      <el-icon><Star /></el-icon>
                      {{ item.likesCount || 0 }}
                    </div>
                    <div class="travel-info">
                      <span class="days">{{ $t('travelNote.daysTrip', { days: item.days || 1 }) }}</span>
                      <span class="date">{{ $t('travelNote.tripTime', { time: item.travelTime || '' }) }}</span>
                    </div>
                    <div class="comment-stat">
                      <el-icon><ChatDotRound /></el-icon>
                      {{ item.commentsCount || 0 }}
                    </div>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </el-card>
        </el-col>
      </el-row>
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
import {computed, ref} from 'vue'
import request from "@/utils/http.js";
import router from "@/router/index.js";
import {ArrowRight, ChatDotRound, HotWater, Location, Picture, PriceTag, Star, View} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import PaymentDialog from "@/components/PaymentDialog.vue";
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();


const drawerVisible = ref(false)
const unreadCount = ref(0)


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

// 景点数据
const scenicList = ref([])

// 游记数据
const travelNotes = ref([])

// 数据获取方法
const fetchData = () => {
  try {
    // 获取推荐景点数据（使用协同过滤）
    request.get('/recommendation/forCurrentUser', {
      params: {
        limit: 6
      }
    }).then(res => {
      if (res && Array.isArray(res) && res.length > 0) {
        scenicList.value = res
      } else {
        scenicList.value = []
      }
    }).catch(error => {
      scenicList.value = []
    })
    
    // 获取游记数据
    let paramsForm = {
      pageNum: 1,
      pageSize: 6
    }
    request.get('/travelNote/homelist', {
      params: paramsForm
    }).then(res => {
      if (res && res.data && res.data.list) {
        travelNotes.value = res.data.list
      } else {
        travelNotes.value = []
      }
    }).catch(error => {
      travelNotes.value = []
    })
  } catch (error) {
    // 数据加载失败
  }
}
function disabledDate(time) {
  const today = new Date();
  // 设置时间为当天0点，以便比较日期而不是具体时间
  today.setHours(0, 0, 0, 0);
  return time < today;
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
      if (!res||res.code !== 200) {
        return
      }
      ElMessage({
        message: $t('order.bookingSuccess'),
        type: "success"
      });
      bookingDialogVisible.value = false
      
      // Предварительное бронирование успешно, открыть диалог оплаты
      paymentOrderNo.value = res.data // Номер заказа, возвращенный сервером
      paymentAmount.value = totalPrice.value
      paymentDialogVisible.value = true
    })
  })
}
const totalPrice = computed(() => {
  return currentScenic.value?.price * buyTicketForm.value.number || 0;
});

/**
 * Обработчик успешной оплаты
 */
function handlePaymentSuccess() {
  ElMessage.success($t('order.paySuccess'))
  fetchData()
}

//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
}
fetchData()
</script>

<style scoped>
.home-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

.banner-carousel {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.carousel-image {
  width: 100%;
  height: 400px;
  cursor: pointer;
}

.main-content {
  padding: 0 20px;
}

.section-card {
  margin-bottom: 24px;
  border-radius: 12px;
}

.three-column-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.scenic-card {
  border-radius: 12px;
  overflow: hidden;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.scenic-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.scenic-cover {
  width: 100%;
  height: 220px;
  object-fit: cover;
  flex-shrink: 0;
}

.scenic-info {
  padding: 16px;
}

/* 游记列表样式 */
.travel-notes-list {
  padding: 8px 0;
}

.note-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  margin-bottom: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background 0.3s;
  cursor: pointer;
}

.note-item:hover {
  background: #e9ecef;
}

.note-cover {
  width: 140px;
  height: 100px;
  border-radius: 8px;
  flex-shrink: 0;
  object-fit: cover;
}

.note-content {
  flex: 1;
  min-width: 0;
}

.note-title {
  margin: 0 0 8px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.note-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0;
  font-size: 13px;
  color: #666;
  flex-wrap: wrap;
}

/* 线路列表样式 */
.route-item {
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 8px;
  background: #f8f9fa;
}

.route-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.route-title {
  font-weight: 500;
}

.route-stats {
  display: flex;
  gap: 12px;
  margin: 8px 0;
  align-items: center;
  color: #666;
  font-size: 13px;
}

.route-covers {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  height: 80px;
}

.route-cover {
  width: 100%;
  height: 80px;
  border-radius: 4px;
}
.card-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.scenic-title {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
  min-height: 48px;
  max-height: 48px;
  word-break: break-word;
}
.scenic-tags {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}
.travel-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 13px;

  .days {
    color: var(--el-color-primary);
    font-weight: 500;
  }

  .date {
    color: #666;
  }
}
.scenic-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 13px;
  color: #666;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.scenic-info {
  margin: 12px 0;
  font-size: 12px;
  color: #666;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  margin-bottom: 8px;
  line-height: 1.6;
  font-size: 13px;
}

.info-item .address {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-word;
}
.scenic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  color: #ff6b6b;
  font-weight: 600;
  font-size: 20px;
  font-variant-numeric: tabular-nums;
}

.original-price {
  color: #999;
  font-size: 13px;
  text-decoration: line-through;
  font-variant-numeric: tabular-nums;
}
/* 新增样式 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.username {
  font-size: 14px;
  color: #666;
}

.route-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px
}

.route-covers {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin-bottom: 12px;
  height: 100px;
}

.route-cover {
  width: 100%;
  height: 100%;
  border-radius: 6px;
}

.cover-error {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
  color: #c0c4cc;
}

/* Адаптивность для мобильных устройств - улучшенная поддержка русского */
@media (max-width: 768px) {
  .home-container {
    padding: 0 12px;
  }
  
  .three-column-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .note-item {
    flex-direction: column;
  }
  
  .note-cover {
    width: 100%;
    height: 180px;
  }
  
  .scenic-title {
    font-size: 15px;
    min-height: 45px;
    max-height: 45px;
    line-height: 1.5;
  }
  
  .card-header .header-title {
    font-size: 18px;
    line-height: 1.4;
  }
  
  .scenic-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .scenic-footer .el-button {
    width: 100%;
    white-space: normal;
    height: auto;
    min-height: 36px;
    line-height: 1.3;
    padding: 8px 16px;
  }
  
  .route-covers {
    grid-template-columns: repeat(2, 1fr);
    height: 80px;
  }

  .user-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .info-item {
    font-size: 12px;
  }
  
  .stat-item {
    font-size: 12px;
  }
}

/* Адаптивность для планшетов */
@media (min-width: 769px) and (max-width: 1024px) {
  .three-column-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

/* Адаптивность для больших экранов */
@media (min-width: 1400px) {
  .three-column-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
