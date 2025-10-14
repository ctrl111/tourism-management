<template>
  <div class="feedback-container">
    <!-- 头部 -->
    <div class="header">
      <h2>提交反馈</h2>
      <button class="close-btn" @click="closeFeedbackModal">×</button>
    </div>

    <!-- 表单主体 -->
    <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" v-if="!isSubmitted">
      <!-- 反馈类型 -->
      <el-form-item label="反馈类型" prop="typeCode" class="form-group">
        <el-select v-model="feedbackForm.typeCode" placeholder="请选择类型" class="form-control">
          <el-option label="功能建议" value="功能建议"></el-option>
          <el-option label="内容纠错" value="内容纠错"></el-option>
          <el-option label="推荐不准确" value="推荐不准确"></el-option>
          <el-option label="系统Bug" value="系统Bug"></el-option>
          <el-option label="体验问题" value="体验问题"></el-option>
          <el-option label="其他" value="其他"></el-option>
        </el-select>
      </el-form-item>
      <!-- 详细描述 -->
      <el-form-item label="问题描述" prop="content" class="form-group">
        <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="5"
            placeholder="请详细描述您的问题或建议..."
            class="form-control"
        ></el-input>
      </el-form-item>

      <!-- 图片上传 -->
      <el-form-item label="上传截图（最多3张）" class="form-group">
        <MyUpLoad type="imageCard" :limit="3" :files="feedbackForm.screenshotUrls"
                  @setFiles="feedbackForm.screenshotUrls =$event"></MyUpLoad>
      </el-form-item>

      <!-- 联系信息 -->
      <el-form-item label="联系方式（可选）" class="form-group">
        <div class="contact-fields">
          <el-input v-model="feedbackForm.contactEmail" placeholder="邮箱" class="form-control"></el-input>
          <el-input v-model="feedbackForm.contactPhone" placeholder="手机号" class="form-control"></el-input>
        </div>
      </el-form-item>

      <!-- 选项组 -->
      <el-form-item class="form-group options">
        <el-checkbox v-model="feedbackForm.allowFollowUp">允许客服人员联系我</el-checkbox>
        <el-checkbox v-model="feedbackForm.isAnonymous">匿名提交</el-checkbox>
      </el-form-item>

      <!-- 提交按钮 -->
      <el-button type="primary" @click="submitFeedback" class="submit-btn">提交反馈</el-button>
    </el-form>

    <!-- 提交后提示 -->
    <div class="success-message" v-if="isSubmitted">
      <p>🎉 感谢您的反馈！</p>
      <p>关注邮箱通知可获取处理进展</p>
    </div>

  </div>
</template>
<script setup>
import request from "@/utils/http.js";
import {Check, Close, Delete, Edit, Refresh, Plus, Search} from '@element-plus/icons-vue'
import {ref, toRaw} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRoute} from "vue-router";
import MyUpload from "@/components/MyUpload.vue";
import MyUpLoad from "@/components/MyUpload.vue";


const drawerVisible = ref(false)
const unreadCount = ref(0)

// 反馈表单数据
const feedbackForm = ref({
  typeCode: "",
  content: "",
  screenshotUrls: '',
  contactEmail: "",
  contactPhone: "",
  allowFollowUp: false,
  isAnonymous: false,
  status: "待处理",
});
// 表单验证规则
const feedbackRules = {
  typeCode: [{ required: true, message: "请选择反馈类型", trigger: "change" }],
  content: [{ required: true, message: "请输入问题描述", trigger: "blur" }],
};
// 是否已提交
const isSubmitted = ref(false);

// 表单引用
const feedbackFormRef = ref(null);
// 提交反馈
const submitFeedback = () => {
  feedbackFormRef.value.validate((valid) => {
    if (valid) {
      request.post("/feedback/add", feedbackForm.value).then(res => {
        if (!res) {
          return
        }
        ElMessage({
          message: "反馈提交成功",
          type: 'success'
        });
        feedbackForm.value = {};
      })
    } else {
      ElMessage.warning("请填写完整信息");
    }
  });
};

// 关闭反馈弹窗
const closeFeedbackModal = () => {
  // 这里可以触发父组件关闭弹窗的逻辑
  ElMessage.info("关闭反馈窗口");
};

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
      request.post("/feedback/add", formData.value).then(res => {
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
      request.put("/feedback/update", formData.value).then(res => {
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
    request.delete("/feedback/delBatch", {data: ids}).then(res => {
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

// 切换标签
function handleTabChange(){
  pageInfo.value.pageNum = 1; // 切换标签时重置页码
  getPageList();
};
// 点击通知行
const handleRowClick = (row) => {
  if (!row.isRead) {
    markAsRead(row);
  }
  // 跳转到通知详情页（可根据实际需求实现）
  console.log('查看通知详情:', row);
};
/**
 * 查看通知详情
 */
function viewDetail(row) {
  currentNotification.value = {...row};
  detailDialogVisible.value = true;
}
// 标记为已读
const markAsRead = (row) => {
  row.isRead = "已读";
  //更新
  request.put("/feedback/update", row).then(res => {
    if (!res) {
      return
    }
    dialogOpen.value = false
    ElMessage({
      message: "标记已读成功",
      type: 'success'
    });
    getPageList()
  })
};

// 删除通知
const deleteNotification = (row) => {
  let ids = [row.id]
  ElMessageBox.confirm(`此操作将永久删除ID为[${ids}]的数据, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    request.delete("/feedback/delBatch", {data: ids}).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: "删除成功",
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
};
//聊天窗口
const handleChatToggle = () => {
  drawerVisible.value = !drawerVisible.value
  console.log(`聊天窗口状态: ${drawerVisible.value ? '打开' : '关闭'}`)
}
</script>

<style scoped>
.feedback-container {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.form-control {
  width: 100%;
}

.voice-input-btn {
  margin-top: 10px;
  color: #409eff;
  cursor: pointer;
}

.contact-fields {
  display: flex;
  gap: 10px;
}

.options {
  display: flex;
  gap: 20px;
}

.submit-btn {
  width: 100%;
  margin-top: 20px;
}

.success-message {
  text-align: center;
  padding: 20px;
}

.success-message p {
  margin: 10px 0;
  font-size: 16px;
}
</style>
