<template>
  <div class="home-container">
    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 景点推荐板块 -->
      <el-card shadow="hover" class="section-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">精选景点推荐</span>
            <el-button type="primary" link @click="router.push('/front/scenic')">
              更多景点
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
                  <span>图片加载失败</span>
                </div>
              </template>
            </el-image>
            <div class="card-content">
              <h4 class="scenic-title">{{ item.name }}</h4>
              <div class="scenic-tags">
                <el-tag type="info" size="small">{{ item.categoryType }}</el-tag>
                <el-tag type="warning" size="small">{{ item.score }}分</el-tag>
                <el-tag type="warning" size="small">{{ item.countComment }}条点评</el-tag>
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
                  <el-button type="primary" size="small" @click.stop="openBookingDialog(item)">立即预定</el-button>
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
                <span class="header-title">热门游记分享</span>
                <el-button type="primary" link @click="router.push('/front/travelNote')">
                  更多游记
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
                        <span>图片加载失败</span>
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
                      {{ item.viewCount | formatNumber }}
                      <el-icon><Star /></el-icon>
                      {{ item.likesCount | formatNumber }}
                    </div>
                    <p class="note-excerpt">{{ item.excerpt }}</p>
                    <div class="travel-info">
                      <span class="days">{{ item.days }}天行程</span>
                      <span class="date">行程时间：{{ item.travelTime }}</span>
                    </div>
                    <div class="comment-stat">
                      <el-icon><ChatDotRound /></el-icon>
                      {{ item.commentsCount | formatNumber }}
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
        :title="`预定 - ${currentScenic?.name}`"
        width="600px"
    >
      <el-form
          ref="bookingFormRef"
          :model="buyTicketForm"
          label-width="100px"
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
import {computed, ref} from 'vue'
import request from "@/utils/http.js";
import router from "@/router/index.js";
import {ArrowRight, ChatDotRound, HotWater, Location, Picture, PriceTag, Star, View} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";


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

// 景点数据
const scenicList = ref([])

// 游记数据
const travelNotes = ref([])

// 数据获取方法
const fetchData = () => {
  try {
    let paramsForm = {
      pageNum: 1,
      pageSize: 6
    }
    // 获取景点数据
    const scenicData = request.get('/scenicInfo/homelist', {
      params: paramsForm
    }).then(res => {
      scenicList.value = res.data.list
    })
    // 获取游记数据
    const travelNoteData = request.get('/travelNote/homelist', {
      params: paramsForm
    }).then(res => {
      travelNotes.value = res.data.list
    })
  } catch (error) {
    console.error('数据加载失败:', error)
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
      getPageList()
    })
  })
}
const totalPrice = computed(() => {
  return currentScenic.value?.price * buyTicketForm.value.number || 0;
});
//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
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
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.scenic-card {
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: transform 0.3s;
  cursor: pointer;
}

.scenic-card:hover {
  transform: translateY(-5px);
}

.scenic-cover {
  width: 100%;
  height: 200px;
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
  width: 120px;
  height: 80px;
  border-radius: 6px;
  flex-shrink: 0;
}

.note-content {
  flex: 1;
  min-width: 0;
}

.note-title {
  margin: 0 0 8px;
  font-size: 14px;
}

.note-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 8px 0;
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
  padding: 15px 0;
}
.scenic-title {
  margin: 0 0 12px;
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.scenic-tags {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}
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
.scenic-info {
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
.scenic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.current-price {
  color: #ff6b6b;
  font-weight: 500;
  font-size: 16px;
}

.original-price {
  color: #999;
  font-size: 12px;
  text-decoration: line-through;
  margin-left: 8px;
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

/* 响应式调整 */
@media (max-width: 768px) {
  .route-covers {
    grid-template-columns: repeat(2, 1fr);
    height: 80px;
  }

  .user-info {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
