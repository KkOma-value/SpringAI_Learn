<template>
  <div class="chat-page">
    <div class="back-button-container">
      <el-button @click="goBack" :icon="ArrowLeft" type="success" plain size="small" class="back-btn">
        返回首页
      </el-button>
    </div>
    <ManusChat ref="chatComponent" />
  </div>
</template>

<script setup>
import { inject, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import ManusChat from '../components/ManusChat.vue'

const router = useRouter()
const chatComponent = ref(null)

const goBack = () => {
  router.push('/')
}

// 暴露清空聊天方法
defineExpose({
  clearMessages: () => {
    if (chatComponent.value) {
      chatComponent.value.clearMessages()
    }
  }
})
</script>

<style scoped>
.chat-page {
  position: relative;
  z-index: 10;
  height: 100vh;
  backdrop-filter: blur(5px);
}

.back-button-container {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 20;
}

.back-btn {
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}
</style> 