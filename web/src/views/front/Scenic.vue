<template>
  <div class="scenic-container">
    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <div class="search-header">
        <el-input
            v-model="searchForm.name"
            :placeholder="$t('scenic.enterScenicName')"
            size="large"
            class="search-input"
            clearable
        >
          <template #prefix>
            <Search style="width: 2em; height: 2em; margin-right: 10px" />
          </template>
        </el-input>
        <el-button type="primary" :icon="Search" @click="search" class="search-button">{{ $t('common.search') }}</el-button>
      </div>

      <div class="filter-area">
        <div class="filter-item">
          <span class="filter-label">{{ $t('scenic.categoryLabel') }}</span>
          <el-radio-group v-model="searchForm.categoryType" @change="search">
            <el-radio-button label="" >{{ $t('scenic.allCategories') }}</el-radio-button>
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
    <div class="scenic-grid">
      <div
          v-for="item in listData"
          :key="item.id"
          class="scenic-card"
          @click="router.push('/front/scenicDetails/'+item.id)"
      >
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
              </div>
              <el-button type="primary" size="small" @click.stop="openBookingDialog(item)">{{ $t('scenic.bookNow') }}</el-button>
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
          layout="prev, pager, next, jumper"
          :page-size="pageInfo.pageSize"
          :current-page="pageInfo.currentPage"
          :total="pageInfo.total"
      />
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
import request from "@/utils/http.js";
import {Search, Star, ChatDotRound, Clock, Location, Picture} from '@element-plus/icons-vue'
import {ref, toRaw,computed} from "vue";
import router from "@/router/index.js";
import {ElMessage, ElMessageBox} from "element-plus";
import PaymentDialog from "@/components/PaymentDialog.vue";
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();


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

// 支付相关
const paymentDialogVisible = ref(false)
const paymentOrderNo = ref('')
const paymentAmount = ref(0)
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
        message: t('scenic.validationFailed'),
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
          message: t('scenic.operationSuccess'),
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
          message: t('scenic.operationSuccess'),
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
        message: t('scenic.operationSuccess'),
        type: 'success'
      });
      getPageList()
    })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: t('scenic.deleteCancelled')
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
        message: $t('order.validationFailed'),
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
        message: $t('order.bookingSuccess'),
        type: "success"
      });
      bookingDialogVisible.value = false
      
      // Бронирование успешно, открыть диалог оплаты
      paymentOrderNo.value = res.data // Номер заказа от сервера
      paymentAmount.value = totalPrice.value
      paymentDialogVisible.value = true
      
      getPageList()
    })
  })
}
const totalPrice = computed(() => {
  return currentScenic.value?.price * buyTicketForm.value.number || 0;
});

/**
 * 支付成功回调
 */
function handlePaymentSuccess() {
  ElMessage.success($t('order.paySuccess'))
  getPageList()
}

//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
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

.scenic-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-top: 24px;
}

.scenic-card {
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
  height: 200px;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: #f5f7fa;
  color: #c0c4cc;
}

.card-content {
  padding: 15px;
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
  margin-top: 15px;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  color: #ff6b6b;
  font-weight: 500;
  font-size: 16px;
}

@media (max-width: 1200px) {
  .scenic-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .scenic-grid {
    grid-template-columns: 1fr;
  }
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
