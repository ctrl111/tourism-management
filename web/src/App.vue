<template>
    <div id="app">
      <el-config-provider :locale="locale" :size="size">
        <router-view/>
      </el-config-provider>
    </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import ru from 'element-plus/dist/locale/ru.mjs'
import '@/assets/styles/theme.css'
import '@/styles/ru-ui.css'
import '@/styles/front.css'
import '@/styles/admin.css'

const language = ref('ru')
const locale = computed(() => ru)

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
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
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

/* Глобальная оптимизация для русского текста */
body {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-rendering: optimizeLegibility;
}

/* Оптимизация переполнения текста */
.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.text-ellipsis-2 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.text-ellipsis-3 {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

/* Утилиты для отступов */
.mt-8 { margin-top: 8px; }
.mt-12 { margin-top: 12px; }
.mt-16 { margin-top: 16px; }
.mt-20 { margin-top: 20px; }
.mt-24 { margin-top: 24px; }

.mb-8 { margin-bottom: 8px; }
.mb-12 { margin-bottom: 12px; }
.mb-16 { margin-bottom: 16px; }
.mb-20 { margin-bottom: 20px; }
.mb-24 { margin-bottom: 24px; }

.p-8 { padding: 8px; }
.p-12 { padding: 12px; }
.p-16 { padding: 16px; }
.p-20 { padding: 20px; }
.p-24 { padding: 24px; }

/* Утилиты для flexbox */
.flex { display: flex; }
.flex-col { flex-direction: column; }
.flex-wrap { flex-wrap: wrap; }
.items-center { align-items: center; }
.justify-center { justify-content: center; }
.justify-between { justify-content: space-between; }
.gap-8 { gap: 8px; }
.gap-12 { gap: 12px; }
.gap-16 { gap: 16px; }
.gap-20 { gap: 20px; }
.gap-24 { gap: 24px; }

</style>

