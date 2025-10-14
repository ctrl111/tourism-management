<template>
  <div class="captcha-container">
    <canvas
      ref="captchaCanvas"
      :width="width"
      :height="height"
      @click="refreshCaptcha"
      class="captcha-canvas"
      title="点击刷新验证码"
    ></canvas>
    <el-icon class="refresh-icon" @click="refreshCaptcha" title="刷新验证码">
      <Refresh />
    </el-icon>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  width: {
    type: Number,
    default: 120
  },
  height: {
    type: Number,
    default: 40
  },
  length: {
    type: Number,
    default: 4
  }
})

const emit = defineEmits(['update:code'])

const captchaCanvas = ref(null)
const captchaCode = ref('')

/**
 * 生成随机字符
 */
function randomChar() {
  const chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'
  return chars.charAt(Math.floor(Math.random() * chars.length))
}

/**
 * 生成随机颜色
 */
function randomColor(min, max) {
  const r = randomNum(min, max)
  const g = randomNum(min, max)
  const b = randomNum(min, max)
  return `rgb(${r},${g},${b})`
}

/**
 * 生成随机数
 */
function randomNum(min, max) {
  return Math.floor(Math.random() * (max - min) + min)
}

/**
 * 绘制验证码
 */
function drawCaptcha() {
  const canvas = captchaCanvas.value
  if (!canvas) return

  const ctx = canvas.getContext('2d')
  
  // 清空画布
  ctx.fillStyle = randomColor(220, 255)
  ctx.fillRect(0, 0, props.width, props.height)

  // 生成验证码文本
  let code = ''
  for (let i = 0; i < props.length; i++) {
    code += randomChar()
  }
  captchaCode.value = code

  // 绘制文字
  for (let i = 0; i < code.length; i++) {
    const char = code[i]
    ctx.font = `bold ${randomNum(20, 28)}px Arial`
    ctx.fillStyle = randomColor(50, 160)
    ctx.textBaseline = 'middle'
    
    // 随机旋转
    const x = (props.width / (props.length + 1)) * (i + 1)
    const y = props.height / 2
    const angle = randomNum(-30, 30) * (Math.PI / 180)
    
    ctx.save()
    ctx.translate(x, y)
    ctx.rotate(angle)
    ctx.fillText(char, 0, 0)
    ctx.restore()
  }

  // 绘制干扰线
  for (let i = 0; i < 3; i++) {
    ctx.strokeStyle = randomColor(100, 200)
    ctx.beginPath()
    ctx.moveTo(randomNum(0, props.width), randomNum(0, props.height))
    ctx.lineTo(randomNum(0, props.width), randomNum(0, props.height))
    ctx.stroke()
  }

  // 绘制干扰点
  for (let i = 0; i < 30; i++) {
    ctx.fillStyle = randomColor(0, 255)
    ctx.beginPath()
    ctx.arc(randomNum(0, props.width), randomNum(0, props.height), 1, 0, 2 * Math.PI)
    ctx.fill()
  }

  // 发送验证码给父组件
  emit('update:code', code)
}

/**
 * 刷新验证码
 */
function refreshCaptcha() {
  drawCaptcha()
}

/**
 * 验证验证码
 */
function validate(input) {
  return input.toLowerCase() === captchaCode.value.toLowerCase()
}

// 暴露方法给父组件
defineExpose({
  validate,
  refresh: refreshCaptcha
})

onMounted(() => {
  drawCaptcha()
})
</script>

<style scoped>
.captcha-container {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.captcha-canvas {
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: border-color 0.3s;
}

.captcha-canvas:hover {
  border-color: #409EFF;
}

.refresh-icon {
  font-size: 20px;
  color: #409EFF;
  cursor: pointer;
  transition: transform 0.3s;
}

.refresh-icon:hover {
  transform: rotate(180deg);
}
</style>

