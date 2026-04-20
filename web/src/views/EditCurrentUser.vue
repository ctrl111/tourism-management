<template>
  <div style="width:600px;margin: 0 auto">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ $t('user.editProfile') }}</span>
        </div>
      </template>
      <el-form :model="formData" label-width="120px">
        <el-form-item prop="img" :label="$t('user.avatar')">
          <MyUpLoad type="imageCard" :limit="1" :files="formData.avatarUrl"
                    @setFiles="formData.avatarUrl =$event"></MyUpLoad>
        </el-form-item>
        <el-form-item prop="username" :label="$t('user.username')">
          <el-input type="text"
                    v-model="formData.username"
                    auto-complete="off"
                    :placeholder="$t('user.username')"
          ></el-input>
        </el-form-item>
        <el-form-item prop="email" :label="$t('user.email')">
          <el-input
              v-model="formData.email"
              auto-complete="off"
              :placeholder="$t('user.email')"
          ></el-input>
        </el-form-item>
        <el-form-item prop="tel" :label="$t('user.phone')">
          <el-input type="text"
                    v-model="formData.tel"
                    auto-complete="off"
                    :placeholder="$t('user.phone')"
          ></el-input>
        </el-form-item>
        <el-form-item style="width:100%;">
          <el-button type="primary" style="width:100px;" @click="handleSubmit">{{ $t('common.save') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import {ref} from 'vue';
import { useI18n } from 'vue-i18n';
import utils from "@/utils/tools.js";
import MyUpLoad from "@/components/MyUpload.vue";
import {ElMessage} from 'element-plus';
import http from "@/utils/http.js";

const { t } = useI18n();
const formData = ref({});

load();

function load() {
  formData.value = utils.getCurrentUser();
}


function handleSubmit() {
  http.post('/common/updateCurrentUser', formData.value).then(res => {
    http.get("/common/currentUser").then(res1 => {
      let currentUser = res1.data;
      localStorage.setItem("currentUser", JSON.stringify(currentUser));
      ElMessage({
        message: t('user.updateSuccess'),
        type: 'success'
      });
    })
  });
};
</script>
