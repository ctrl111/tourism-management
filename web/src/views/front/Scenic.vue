<template>
  <div class="scenic-container">
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <div class="search-header">
        <el-input
            v-model="searchForm.name"
            placeholder="请输入景点名称"
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

      <div class="filter-area">
        <div class="filter-item">
          <span class="filter-label">分类：</span>
          <el-radio-group v-model="searchForm.categoryType" @change="search">
            <el-radio-button label="" >全部</el-radio-button>
            <el-radio-button
                v-for="item in categoryList"
                :key="item.id"
                :label="item.name"
            >
              {{ item.name }}
            </el-radio-button>
          </el-radio-group>
        </div>

        <!--          <div class="filter-item">-->
        <!--            <span class="filter-label">排序：</span>-->
        <!--            <el-select-->
        <!--                v-model="searchForm.sortType"-->
        <!--                placeholder="推荐排序"-->
        <!--                style="width: 200px"-->
        <!--            >-->
        <!--              <el-option label="推荐排序" value="1"/>-->
        <!--              <el-option label="好评优先" value="2"/>-->
        <!--              <el-option label="人气最高" value="3"/>-->
        <!--            </el-select>-->
        <!--          </div>-->
      </div>
    </el-card>

    <!-- 景点列表 -->
    <el-row :gutter="24" class="scenic-list">
      <el-col
          v-for="item in listData"
          :key="item.id"
          :xs="24" :sm="12" :md="8" :lg="6"
          class="scenic-item"
      >
        <el-card
            shadow="never"
            class="scenic-card"
            @click="router.push('/front/scenicDetails/'+item.id)"
        >
          <div class="card-cover">
            <el-image
                :src="item.coverImage"
                fit="cover"
                class="cover-image"
                lazy
            >
              <template #placeholder>
                <div class="image-placeholder">
                  <el-icon class="placeholder-icon"><Picture /></el-icon>
                  <span class="placeholder-text">加载中...</span>
                </div>
              </template>
              <template #error>
                <div class="image-placeholder">
                  <el-icon class="placeholder-icon"><Picture /></el-icon>
                  <span class="placeholder-text">暂无图片</span>
                </div>
              </template>
            </el-image>
            <div class="price-badge">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ item.price }}</span>
            </div>
          </div>

          <div class="card-content">
            <h4 class="scenic-title">{{ item.name }}</h4>

            <div class="scenic-tags">
              <el-tag class="category-tag" size="small">{{ item.categoryType }}</el-tag>
              <div class="rating-tag">
                <el-icon class="star-icon"><Star /></el-icon>
                <span>{{ item.score }}</span>
              </div>
            </div>

            <div class="scenic-info">
              <div class="info-item">
                <el-icon class="info-icon"><Location /></el-icon>
                <span class="address">{{ item.address || '暂无地址' }}</span>
              </div>
            </div>

            <div class="scenic-footer">
              <div class="comment-count">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ item.countComment }}条评论</span>
              </div>
              <el-button 
                type="primary" 
                size="small" 
                class="book-btn"
                @click.stop="openBookingDialog(item)"
              >
                立即预定
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
          @current-change="currentChange"
          @size-change="sizeChange"
          background
          layout="prev, pager, next, jumper"
          :page-size="pageInfo.pageSize"
          :current-page="pageInfo.currentPage"
          :total="pageInfo.total"
      />
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
import request from "@/utils/http.js";
import {Search, Star, ChatDotRound} from '@element-plus/icons-vue'
import {ref, toRaw,computed} from "vue";
import router from "@/router/index.js";
import {ElMessage, ElMessageBox} from "element-plus";


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
const searchForm = ref({
  categoryType: undefined,
  status: undefined
});
const categoryList = ref([]);
const statusList = ref(['上架', '下架'])

getCategoryList()
getPageList()


function disabledDate(time) {
  const today = new Date();
  // 设置时间为当天0点，以便比较日期而不是具体时间
  today.setHours(0, 0, 0, 0);
  return time < today;
}
/**
 * 获取分页数据
 */
function getPageList() {
  let data = Object.assign(toRaw(searchForm.value), toRaw(pageInfo.value))
  request.get("/scenicInfo/page", {
    params: data
  }).then(res => {
    listData.value = res.data.list
    pageInfo.value.total = res.data.total
  })
}

//获取景点分类信息
function getCategoryList() {
  request.get("/scenicCategory/list").then(res => {
    categoryList.value = res.data
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
  dialogOpen.value = false
}

/**
 * 提交数据
 */
function submit() {
  formRef.value.validate((valid) => {
    if (!valid) {
      ElMessage({
        message: "验证失败，请检查表单!",
        type: 'warning'
      });
      return
    }
    //新增
    if (!formData.value.id) {
      request.post("/scenicInfo/add", formData.value).then(res => {
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
      request.put("/scenicInfo/update", formData.value).then(res => {
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
    request.delete("/scenicInfo/delBatch", {data: ids}).then(res => {
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
    buyTicketForm.value.id = currentScenic.value.id,
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
</script>

<style scoped>
.scenic-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 32px;
}

.search-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.search-header {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  border-radius: 8px;
}

.search-button {
  min-width: 100px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border: none;
  font-weight: 600;
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
}

.filter-area {
  display: flex;
  gap: 30px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  white-space: nowrap;
}

.scenic-list {
  margin-top: 24px;
}

.scenic-item {
  margin-bottom: 24px;
}

.scenic-card {
  cursor: pointer;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
}

.scenic-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
  border-color: #1890ff;
}

.card-cover {
  position: relative;
  height: 200px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8f4f8 100%);
}

.cover-image {
  width: 100%;
  height: 100%;
  transition: transform 0.3s;
}

.scenic-card:hover .cover-image {
  transform: scale(1.1);
}

.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8f4f8 100%);
  color: #bfbfbf;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 8px;
  opacity: 0.5;
}

.placeholder-text {
  font-size: 14px;
  color: #8c8c8c;
}

.price-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: linear-gradient(135deg, #ff9f00 0%, #ff6b00 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.price-symbol {
  font-size: 12px;
}

.price-value {
  font-size: 18px;
}

.card-content {
  padding: 16px;
}

.scenic-title {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 700;
  color: #262626;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.scenic-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.category-tag {
  background: #e6f7ff;
  color: #1890ff;
  border: none;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 4px;
}

.rating-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #faad14;
  font-size: 14px;
  font-weight: 600;
}

.star-icon {
  font-size: 16px;
}

.scenic-info {
  margin: 12px 0;
  font-size: 13px;
  color: #8c8c8c;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.info-icon {
  font-size: 14px;
  color: #1890ff;
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
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.comment-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #8c8c8c;
}

.book-btn {
  background: linear-gradient(135deg, #ff9f00 0%, #ff6b00 100%);
  border: none;
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: 600;
  transition: all 0.3s;
}

.book-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.4);
}

.pagination-wrapper {
  margin-top: 40px;
  text-align: center;
}

:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
}

:deep(.el-radio-button__inner) {
  border-radius: 6px;
  margin: 0 4px;
  border: 1px solid #d9d9d9;
  transition: all 0.3s;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}
</style>
