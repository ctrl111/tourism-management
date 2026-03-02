<template>
    <div id="app">
      <el-config-provider :locale="locale" :size="size">
        <router-view/>
      </el-config-provider>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import en from 'element-plus/dist/locale/en.mjs'
import '@/assets/styles/theme.css'

const language = ref('zh-cn')
const locale = computed(() => (
    language.value === 'zh-cn' ? zhCn : en))

const size= ref('default')

</script>
<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

html, body, #app {
    height: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #e8f4f8 100%);
}

.container-shadow {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    padding: 24px;
    box-sizing: border-box;
    border-radius: 12px;
    background: white;
    transition: all 0.3s;
}

.container-shadow:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
}

.el-card{
  --el-card-border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.el-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

</style>
