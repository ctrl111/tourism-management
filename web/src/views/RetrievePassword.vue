<template>
  <div class="main-context">
    <el-card class="box-card">
      <el-space direction="vertical" style="width: 100%" size="large">
        <el-space>
          <img src="../assets/logo.png" width="100%" style="width: 55px">
          <el-space direction="vertical" style="width: 100%" size="small">
            <h2 style="font-style: oblique">{{ $t('retrievePassword.title') }}</h2>
            <span style="font-style: oblique;font-size: 15px">Туристическая система</span>
          </el-space>
        </el-space>
        <el-form :model="formData" label-width="100px" :rules="rules" ref="formRef" style="width: 100%">
          <el-form-item :label="$t('user.userType')" prop="type">
            <el-select v-model="formData.type" :placeholder="$t('form.pleaseSelect', { field: $t('user.userType') })">
              <el-option :label="$t('user.admin')" value="ADMIN"></el-option>
              <el-option :label="$t('user.normalUser')" value="USER"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('user.phone')" prop="tel"
                        :rules="[{required:true,message:$t('form.pleaseEnter', { field: $t('user.phone') }),trigger:[ 'blur','change']}]">
            <el-input
                style="width: 180px"
                :placeholder="$t('form.pleaseEnter', { field: $t('user.phone') })"
                v-model.trim="formData.tel"
                clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('login.captcha')" prop="code"
                        :rules="[{required:true,message:$t('form.pleaseEnter', { field: $t('login.captcha') }),trigger:[ 'blur','change']}]">
            <el-input
                style="width: 180px"
                :placeholder="$t('form.pleaseEnter', { field: $t('login.captcha') })"
                v-model.trim="formData.code"
                clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item :label="$t('retrievePassword.newPassword')" prop="password"
                        :rules="[{required:true,message:$t('form.pleaseEnter', { field: $t('retrievePassword.newPassword') }),trigger:[ 'blur','change']}]">
            <el-input
                style="width: 180px"
                :placeholder="$t('form.pleaseEnter', { field: $t('retrievePassword.newPassword') })"
                show-password
                v-model.trim="formData.password"
                clearable
            >
            </el-input>
          </el-form-item>
          <el-form-item label="" style="width: 100%">
            <el-space direction="vertical" alignment="left" style="width: 100%">
              <el-button @click="submitForm()" type="success" style="width: 100%">{{ $t('retrievePassword.retrieve') }}</el-button>
              <router-link tag="span" :to="{path:'login'}">
                <el-button type="text" class="button" style="float: right">{{ $t('retrievePassword.backToLogin') }}</el-button>
              </router-link>
            </el-space>

          </el-form-item>
        </el-form>
      </el-space>
    </el-card>
  </div>
</template>
<script setup>
import {ref} from 'vue';
import {ElMessage} from 'element-plus';
import { useI18n } from 'vue-i18n';
import http from "@/utils/http.js";
import router from "@/router/index.js";

const { t } = useI18n();
const formRef = ref(null);
const formData = ref({
  type: 'USER',
  tel: '',
  code: '',
  password: ''
});

const submitForm = () => {
  formRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    http.post("/common/retrievePassword", formData.value).then(res => {
      if (!res) {
        return
      }
      ElMessage({
        message: t('retrievePassword.retrieveSuccess'),
        type: "success"
      });
      router.push({path: "/login"})
    });
  });
}

</script>

<style scoped>
.main-context {
  height: 100vh; /* 使 .app 高度为视口高度 */
  background: url("../assets/login.png") no-repeat center center fixed;
  background-size: cover; /* 使用 cover 保持图片比例 */
  display: flex;
  justify-content: center;
  align-items: center;
  color: white; /* 根据背景图片调整文字颜色 */
}

.box-card {
  width: 350px;
  margin: 0 auto;
  text-align: center;
}
</style>
