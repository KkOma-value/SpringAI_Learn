<template>
  <div id="app" :class="{ 'dark-theme': isDarkTheme }">
    <div class="background-container">
      <div class="animated-gradient"></div>
      <div class="particles"></div>
      <div class="meteors"></div>
    </div>
    
    <router-view />
  </div>
</template>

<script setup>
import { ref, provide, onMounted } from 'vue'

const isDarkTheme = ref(true) // 默认使用暗色主题

// 提供给子组件使用的主题状态
provide('isDarkTheme', isDarkTheme)
provide('toggleTheme', () => {
  isDarkTheme.value = !isDarkTheme.value
})

// 随机生成粒子效果
onMounted(() => {
  createParticles()
  createMeteors()
})

// 创建背景粒子
function createParticles() {
  const container = document.querySelector('.particles')
  if (!container) return
  
  // 清空现有粒子
  container.innerHTML = ''
  
  // 创建30个随机粒子
  for (let i = 0; i < 30; i++) {
    const particle = document.createElement('div')
    particle.classList.add('particle')
    
    // 随机位置和大小
    const size = Math.random() * 6 + 2
    const posX = Math.random() * 100
    const posY = Math.random() * 100
    const delay = Math.random() * 5
    const duration = Math.random() * 15 + 10
    
    particle.style.width = `${size}px`
    particle.style.height = `${size}px`
    particle.style.left = `${posX}%`
    particle.style.top = `${posY}%`
    particle.style.animationDelay = `${delay}s`
    particle.style.animationDuration = `${duration}s`
    
    container.appendChild(particle)
  }
}

// 创建流星效果
function createMeteors() {
  const container = document.querySelector('.meteors')
  if (!container) return
  
  // 清空现有流星
  container.innerHTML = ''
  
  // 创建8个流星
  for (let i = 0; i < 8; i++) {
    const meteor = document.createElement('div')
    meteor.classList.add('meteor')
    
    // 随机位置和延迟
    const startX = Math.random() * 100
    const delay = Math.random() * 10
    const duration = Math.random() * 3 + 2
    
    meteor.style.left = `${startX}%`
    meteor.style.animationDelay = `${delay}s`
    meteor.style.animationDuration = `${duration}s`
    
    container.appendChild(meteor)
  }
}
</script>

<style>
:root {
  --primary-color: #409EFF;
  --secondary-color: #67C23A;
  --background-color: #f5f7fa;
  --text-color: #303133;
  --card-bg: #ffffff;
  --border-color: #ebeef5;
  --shadow-color: rgba(0, 0, 0, 0.1);
  --message-user-bg: #409EFF;
  --message-ai-bg: #f5f7fa;
  --message-ai-border: #e4e7ed;
  --header-bg: #409EFF;
  --footer-bg: #f4f4f5;
}

.dark-theme {
  --primary-color: #79bbff;
  --secondary-color: #95d475;
  --background-color: #1a1a1a;
  --text-color: #e4e7ed;
  --card-bg: #2d2f36;
  --border-color: #414243;
  --shadow-color: rgba(0, 0, 0, 0.4);
  --message-user-bg: #0a5dc2;
  --message-ai-bg: #363739;
  --message-ai-border: #4c4d4f;
  --header-bg: #2b2f36;
  --footer-bg: #2b2f36;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  transition: background-color 0.3s, color 0.3s, border-color 0.3s;
}

html, body {
  height: 100%;
  overflow: hidden;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  color: var(--text-color);
  background-color: var(--background-color);
  height: 100vh;
  overflow: hidden;
  position: relative;
}

.background-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.animated-gradient {
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  background: linear-gradient(45deg, 
    rgba(64, 158, 255, 0.08), 
    rgba(103, 194, 58, 0.08), 
    rgba(230, 162, 60, 0.08), 
    rgba(245, 108, 108, 0.08));
  animation: gradientAnimation 20s ease infinite;
  z-index: 1;
}

.dark-theme .animated-gradient {
  background: linear-gradient(45deg, 
    rgba(64, 158, 255, 0.15), 
    rgba(103, 194, 58, 0.15), 
    rgba(230, 162, 60, 0.15), 
    rgba(245, 108, 108, 0.15));
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 2;
}

.particle {
  position: absolute;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: floatAnimation linear infinite;
}

.dark-theme .particle {
  background-color: rgba(255, 255, 255, 0.1);
}

.meteors {
  position: absolute;
  width: 100%;
  height: 100%;
  z-index: 3;
}

.meteor {
  position: absolute;
  width: 2px;
  height: 2px;
  background: linear-gradient(45deg, #fff, transparent);
  border-radius: 50%;
  animation: meteorFall linear infinite;
  opacity: 0;
}

.meteor::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 80px;
  height: 2px;
  background: linear-gradient(90deg, 
    rgba(255, 255, 255, 0.8) 0%, 
    rgba(255, 255, 255, 0.4) 50%, 
    transparent 100%);
  transform: rotate(45deg);
  transform-origin: 0 50%;
}

.dark-theme .meteor::before {
  background: linear-gradient(90deg, 
    rgba(121, 187, 255, 0.8) 0%, 
    rgba(121, 187, 255, 0.4) 50%, 
    transparent 100%);
}

/* 动画 */
@keyframes gradientAnimation {
  0% { transform: rotate(0deg); }
  50% { transform: rotate(180deg); }
  100% { transform: rotate(360deg); }
}

@keyframes floatAnimation {
  0% { 
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% { 
    transform: translateY(-100vh) rotate(360deg);
    opacity: 0;
  }
}

@keyframes meteorFall {
  0% {
    transform: translateY(-100px) translateX(-100px);
    opacity: 1;
  }
  70% {
    opacity: 1;
  }
  100% {
    transform: translateY(100vh) translateX(100vh);
    opacity: 0;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 