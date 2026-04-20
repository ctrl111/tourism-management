<template>
  <div style="width:600px;margin: 0 auto">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('user.changePassword') }}</span>
        </div>
      </template>
      <el-form :model="formData" label-width="140px">
        <el-form-item prop="oldPassword" :label="$t('user.oldPassword')">
          <el-input type="password"
                    v-model="formData.oldPassword"
                    auto-complete="off"
                    :placeholder="$t('user.oldPassword')"
          ></el-input>
        </el-form-item>
        <el-form-item prop="newPassword" :label="$t('user.newPassword')">
          <el-input type="password"
                    v-model="formData.newPassword"
                    auto-complete="off"
                    :placeholder="$t('user.newPassword')"
          ></el-input>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button type="primary" style="width:100px;" @click="handleSubmit">{{ $t('user.changePassword') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import http from "@/utils/http.js";
import {ref} from "vue";
import {ElMessage} from "element-plus";
import { useI18n } from 'vue-i18n';
import router from "@/router/index.js";

const { t } = useI18n();

const formData = ref({
  id: "",
  oldPassword: '',
  newPassword: '',
})

function handleSubmit() {
  http.post('/common/updatePassword', formData.value).then(res => {
    if (!res) {
      return
    }
    localStorage.clear()
    ElMessage({
      message: t('user.changePasswordSuccess') + ', ' + t('message.pleaseLoginAgain'),
      type: 'success'
    });
    router.push({path: "/login"})
  })
}
</script>
