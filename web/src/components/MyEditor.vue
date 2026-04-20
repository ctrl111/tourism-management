<template>
  <div>
    <div class="nav" style="border: 1px solid #ccc; margin-top: 10px;color: #000">
      <!-- 工具栏 -->
      <Toolbar
          style="border-bottom: 1px solid #ccc"
          :editor="myEditor"
          :defaultConfig="toolbarConfig"
      />
      <!-- 编辑器 -->
      <Editor
          style="height: 400px; overflow-y: hidden"
          :defaultConfig="editorConfig"
          v-model="html"
          @onChange="onChange"
          @onCreated="onCreated"
      />
    </div>
  </div>
</template>
<script setup>
import {ref, defineProps, onMounted, onBeforeUnmount, watch} from 'vue';
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import utils from "@/utils/tools.js";
import {useI18n} from 'vue-i18n';

const {t: $t} = useI18n();

const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  content: {
    type: String,
    default: "",
  }
});

const myEditor = ref(null);
const html = ref(props.modelValue || props.content);

// 监听 content prop 变化
watch(() => props.content, (newVal) => {
  if (newVal !== html.value) {
    html.value = newVal || ''
  }
}, { immediate: false })

// 监听 modelValue 变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== html.value) {
    html.value = newVal || ''
  }
}, { immediate: false })

const toolbarConfig = ref({
  // toolbarKeys: [ /* 显示哪些菜单，如何排序、分组 */ ],
  // excludeKeys: [ /* 隐藏哪些菜单 */ ],
});

const editorConfig = ref({
  placeholder: $t('form.enterContent'),
  // autoFocus: false,
  MENU_CONF: {
    uploadImage: {
      server: import.meta.env.VITE_APP_API_URL + "/file/upload",
      headers: {
        token: utils.getToken()
      },
      fieldName: 'file',
      customInsert(res, insertFn) {
        insertFn(res.data.url, res.data.name, res.data.url);
      },
    },
    uploadVideo: {
      maxFileSize: 100 * 1024 * 1024, // 10M
      server: import.meta.env.VITE_APP_API_URL + "/file/upload",
      headers: {
        token: utils.getToken()
      },
      fieldName: 'file',
      customInsert(res, insertFn) {
        insertFn(res.data.url, res.data.name, res.data.url);
      },
    }
  }
});

function onCreated(editor) {
  myEditor.value = Object.seal(editor);
}

const emit = defineEmits(['update:modelValue', 'content-change']);

function onChange(editor) {
  html.value = editor.getHtml();
  // 由于我们使用的是Vue 3的Composition API，所以不需要使用this.$emit
  emit("update:modelValue", html.value);
  emit("content-change", html.value);
}

onMounted(() => {
  // 如果需要在组件挂载后执行某些操作，可以在这里添加
});

onBeforeUnmount(() => {
  if (myEditor.value != null) {
    myEditor.value.destroy();
  }
});
</script>
<style src="@wangeditor/editor/dist/css/style.css"></style>
<style>
.nav .title {
  margin-top: 10px;
  color: #000 !important;
}
</style>
