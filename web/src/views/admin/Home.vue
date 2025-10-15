<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user-icon">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.userCount || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon scenic-icon">
              <el-icon :size="32"><LocationFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.scenicCount || 0 }}</div>
              <div class="stat-label">景点总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order-icon">
              <el-icon :size="32"><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.orderCount || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon note-icon">
              <el-icon :size="32"><Notebook /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.travelNoteCount || 0 }}</div>
              <div class="stat-label">游记总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon route-icon">
              <el-icon :size="32"><Guide /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.routeCount || 0 }}</div>
              <div class="stat-label">路线总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon revenue-icon">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">￥{{ (overview.totalRevenue || 0).toFixed(2) }}</div>
              <div class="stat-label">总销售额</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 景点销售TOP10 -->
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">景点销售TOP10</span>
            </div>
          </template>
          <div ref="salesChartRef" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>

      <!-- 分类统计 -->
      <el-col :span="10">
        <el-card>
          <template #header>
            <div class="card-header">
              <span class="card-title">分类统计</span>
            </div>
          </template>
          <div ref="categoryChartRef" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/http.js'
import { User, LocationFilled, ShoppingCart, Notebook, Guide, Money } from '@element-plus/icons-vue'

const salesChartRef = ref(null)
const categoryChartRef = ref(null)
let salesChart = null
let categoryChart = null

const overview = ref({
  userCount: 0,
  scenicCount: 0,
  orderCount: 0,
  travelNoteCount: 0,
  routeCount: 0,
  totalRevenue: 0
})

// 获取概览数据
function getOverview() {
  request.get('/statistics/overview').then(res => {
    overview.value = res.data
  })
}

// 初始化景点销售TOP10图表
function initSalesChart(data) {
  if (salesChart) {
    salesChart.dispose()
  }
  
  salesChart = echarts.init(salesChartRef.value)
  
  const scenicNames = data.map(item => item.scenicName)
  const salesCounts = data.map(item => item.salesCount)
  
  const option = {
    title: {
      text: '销量',
      left: 0,
      textStyle: {
        fontSize: 14,
        fontWeight: 'normal',
        color: '#666'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: scenicNames,
      axisLabel: {
        interval: 0,
        rotate: 30,
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '销量',
        type: 'bar',
        data: salesCounts,
        itemStyle: {
          color: '#5470c6'
        },
        barWidth: '60%'
      }
    ]
  }
  
  salesChart.setOption(option)
}

// 初始化分类统计图表
function initCategoryChart(data) {
  if (categoryChart) {
    categoryChart.dispose()
  }
  
  categoryChart = echarts.init(categoryChartRef.value)
  
  const chartData = data.map(item => ({
    name: item.categoryName,
    value: item.orderCount
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '订单数',
        type: 'pie',
        radius: ['35%', '60%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          fontSize: 11,
          formatter: '{b}\n{d}%',
          position: 'outer',
          alignTo: 'none',
          bleedMargin: 5
        },
        labelLine: {
          show: true,
          length: 10,
          length2: 10
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 13,
            fontWeight: 'bold'
          }
        },
        data: chartData
      }
    ]
  }
  
  categoryChart.setOption(option)
}

// 获取景点销售TOP10数据
function getScenicSalesTop10() {
  request.get('/statistics/scenicSalesTop10').then(res => {
    initSalesChart(res.data)
  })
}

// 获取分类统计数据
function getCategoryStatistics() {
  request.get('/statistics/categoryStatistics').then(res => {
    initCategoryChart(res.data)
  })
}

// 窗口大小改变时重新调整图表大小
function handleResize() {
  if (salesChart) {
    salesChart.resize()
  }
  if (categoryChart) {
    categoryChart.resize()
  }
}

onMounted(() => {
  getOverview()
  getScenicSalesTop10()
  getCategoryStatistics()
  
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  if (salesChart) {
    salesChart.dispose()
  }
  if (categoryChart) {
    categoryChart.dispose()
  }
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.scenic-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.note-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.route-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.revenue-icon {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style>
