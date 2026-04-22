<template>
  <div class="favorite-container">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 景点收藏 -->
      <el-tab-pane :label="$t('personalCenter.scenicFavorites')" name="SCENIC">
        <div v-loading="loading">
          <div class="scenic-grid">
            <div
              v-for="item in listData"
              :key="item.id"
              class="scenic-card"
              @click="viewScenic(item)"
            >
              <el-image :src="item.scenicInfo?.coverImage" class="scenic-cover" fit="cover">
                <template #error>
                  <div class="image-error">
                    <el-icon><Picture /></el-icon>
                    <span>{{ $t('personalCenter.imageLoadFailed') }}</span>
                  </div>
                </template>
              </el-image>
              <div class="card-content">
                <h4 class="scenic-title">{{ item.scenicInfo?.name }}</h4>
                <div class="scenic-info">
                  <div class="info-item">
                    <el-icon><Location /></el-icon>
                    <span class="address">{{ item.scenicInfo?.address }}</span>
                  </div>
                  <div class="scenic-footer">
                    <div class="price">
                      <span class="current-price">￥{{ item.scenicInfo?.price }}</span>
                    </div>
                    <el-button type="warning" size="small" plain @click.stop="handleUnfavorite(item)">
                      {{ $t('personalCenter.unfavorite') }}
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <el-empty v-if="!loading && listData.length === 0" :description="$t('personalCenter.noFavorites')" />
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pageInfo.pageNum"
            v-model:page-size="pageInfo.pageSize"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next"
            :total="pageInfo.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </el-tab-pane>

      <!-- 游记收藏 -->
      <el-tab-pane :label="$t('personalCenter.noteFavorites')" name="TRAVEL_NOTE">
        <div v-loading="loading">
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
                @click="viewTravelNote(item)"
              >
                <!-- 封面图 -->
                <div class="card-cover">
                  <el-image
                    :src="item.travelNote?.cover"
                    fit="cover"
                    class="cover-image"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon><Picture /></el-icon>
                        <span>{{ $t('personalCenter.imageLoadFailed') }}</span>
                      </div>
                    </template>
                  </el-image>
                  <!-- 标题和用户信息叠加在封面图 -->
                  <div class="cover-overlay">
                    <h4 class="note-title">{{ item.travelNote?.title }}</h4>
                    <div class="user-info">
                      <el-avatar
                        :size="32"
                        :src="item.travelNote?.user?.avatarUrl"
                        class="user-avatar"
                      />
                      <span class="username">{{ item.travelNote?.user?.username }}</span>
                    </div>
                  </div>
                </div>

                <!-- 底部信息 -->
                <div class="card-footer">
                  <div class="travel-info">
                    <span class="days">{{ $t('personalCenter.daysTrip', { days: item.travelNote?.days }) }}</span>
                    <span class="date">{{ $t('personalCenter.favoritedOn') }} {{ item.createTime }}</span>
                  </div>
                  <div class="comment-stat">
                    <el-button 
                      type="warning" 
                      size="small"
                      plain
                      @click.stop="handleUnfavorite(item)"
                    >
                      {{ $t('personalCenter.unfavorite') }}
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <el-empty v-if="!loading && listData.length === 0" :description="$t('personalCenter.noFavorites')" />
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pageInfo.pageNum"
            v-model:page-size="pageInfo.pageSize"
            :page-sizes="[12, 24, 48]"
            layout="total, sizes, prev, pager, next"
            :total="pageInfo.total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, Location, Clock, Star, StarFilled, ChatDotRound } from '@element-plus/icons-vue'
import request from '@/utils/http.js'

const router = useRouter()

// 当前激活的标签页
const activeTab = ref('SCENIC')

// 列表数据
const listData = ref([])
const loading = ref(false)

// 分页信息
const pageInfo = ref({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

/**
 * 获取收藏列表
 */
async function getFavoriteList() {
  loading.value = true
  try {
    const params = {
      typeCode: activeTab.value,
      pageNum: pageInfo.value.pageNum,
      pageSize: pageInfo.value.pageSize
    }
    const res = await request.get('/favorite/page', { params })
    if (res.code === 200) {
      listData.value = res.data.list || []
      pageInfo.value.total = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('Ошибка загрузки списка избранного')
  } finally {
    loading.value = false
  }
}

/**
 * 标签页切换
 */
function handleTabChange() {
  pageInfo.value.pageNum = 1
  getFavoriteList()
}

/**
 * 分页大小改变
 */
function handleSizeChange(size) {
  pageInfo.value.pageSize = size
  getFavoriteList()
}

/**
 * 页码改变
 */
function handleCurrentChange(page) {
  pageInfo.value.pageNum = page
  getFavoriteList()
}

/**
 * 查看景点详情
 */
function viewScenic(row) {
  if (row.associationId) {
    router.push(`/front/scenicDetails/${row.associationId}`)
  } else {
    ElMessage.warning('Информация о достопримечательности неполная')
  }
}

/**
 * 查看游记详情
 */
function viewTravelNote(row) {
  if (row.associationId) {
    router.push(`/front/travelDetails/${row.associationId}`)
  } else {
    ElMessage.warning('Информация о заметке неполная')
  }
}

/**
 * 取消收藏
 */
async function handleUnfavorite(row) {
  const itemType = activeTab.value === 'SCENIC' ? 'достопримечательность' : 'заметку'
  try {
    await ElMessageBox.confirm(`Вы уверены, что хотите удалить из избранного эту ${itemType}?`, 'Подсказка', {
      confirmButtonText: 'Подтвердить',
      cancelButtonText: 'Отмена',
      type: 'warning'
    })
    
    const res = await request.delete(`/favorite/${row.id}`)
    if (res.code === 200) {
      ElMessage.success('Удалено из избранного')
      getFavoriteList()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Ошибка удаления из избранного')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  getFavoriteList()
})
</script>

<style scoped>
.favorite-container {
  padding: 20px;
}

/* 景点卡片样式 - 复用自 Scenic.vue */
.scenic-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 24px;
}

.scenic-card {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: transform 0.3s;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.scenic-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.scenic-cover {
  width: 100%;
  height: 160px;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 160px;
  background: #f5f7fa;
  color: #c0c4cc;
}

.favorite-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(255, 193, 7, 0.9);
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  z-index: 1;
}

.card-content {
  padding: 12px;
}

.scenic-title {
  margin: 0 0 10px;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.scenic-tags {
  margin-bottom: 10px;
  display: flex;
  gap: 8px;
}

.scenic-info {
  margin: 8px 0;
  font-size: 12px;
  color: #666;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 8px;
}

.address {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.scenic-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  color: #ff6b6b;
  font-weight: 500;
  font-size: 15px;
}

/* 游记卡片样式 - 复用自 TravelNote.vue */
.note-list {
  margin: 20px -10px;
}

.note-item {
  margin-bottom: 20px;
}

.note-card {
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.note-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.note-card :deep(.el-card__body) {
  padding: 0;
}

.card-cover {
  height: 200px;
  position: relative;
}

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

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  border: 2px solid rgba(255,255,255,0.8);
}

.username {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-footer {
  padding: 12px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;
}

.travel-info {
  display: flex;
  flex-direction: column;
}

.days {
  color: var(--el-color-primary);
  font-weight: 500;
  font-size: 14px;
}

.date {
  color: #666;
  font-size: 12px;
}

.comment-stat {
  display: flex;
  align-items: center;
  gap: 4px;
}

.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 1200px) {
  .scenic-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .scenic-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .card-cover {
    height: 160px;
  }
}

@media (max-width: 480px) {
  .scenic-grid {
    grid-template-columns: 1fr;
  }
}
</style>
