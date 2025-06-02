<template>
  <div class="home-page">
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="app-header">
        <div class="logo-container">
          <img src="/vite.svg" class="logo" alt="Logo" />
          <h2>SpringAI 智能助手</h2>
        </div>
        <div class="header-controls">
          <el-switch
            v-model="isDarkTheme"
            active-text="暗黑模式"
            inactive-text="亮色模式"
            inline-prompt
            style="margin-right: 20px"
            @change="toggleTheme"
          />
          <el-tooltip content="项目源码" placement="bottom" :hide-after="2000">
            <el-button type="primary" :icon="Link" circle />
          </el-tooltip>
        </div>
      </el-header>

      <!-- 主体内容 -->
      <el-main class="app-main">
        <div class="welcome-section">
          <div class="welcome-content">
            <h1 class="welcome-title">
              <span class="title-text">欢迎使用</span>
              <span class="title-highlight">SpringAI</span>
              <span class="title-text">智能助手</span>
            </h1>
            <p class="welcome-subtitle">选择您想要的AI聊天模式，开启智能对话体验</p>
            
            <!-- 功能卡片 -->
            <div class="feature-cards">
              <div class="feature-card love-app" @click="goToLoveApp">
                <div class="card-icon">
                  <el-icon><ChatDotRound /></el-icon>
                </div>
                <div class="card-content">
                  <h3>Love App 聊天</h3>
                  <p>情感丰富的AI助手，支持多轮对话和上下文理解</p>
                  <ul class="feature-list">
                    <li>✨ 情感化对话</li>
                    <li>🔗 会话连续性</li>
                    <li>🎯 上下文理解</li>
                  </ul>
                </div>
                <div class="card-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>

              <div class="feature-card manus" @click="goToManus">
                <div class="card-icon">
                  <el-icon><ChatLineRound /></el-icon>
                </div>
                <div class="card-content">
                  <h3>Manus 聊天</h3>
                  <p>专业的AI工具助手，提供精准的问题解答和技术支持</p>
                  <ul class="feature-list">
                    <li>🔧 专业工具</li>
                    <li>📚 知识问答</li>
                    <li>⚡ 快速响应</li>
                  </ul>
                </div>
                <div class="card-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
            </div>

            <!-- 技术特色 -->
            <div class="tech-highlights">
              <div class="tech-item">
                <el-icon class="tech-icon"><Lightning /></el-icon>
                <span>实时流式响应</span>
              </div>
              <div class="tech-item">
                <el-icon class="tech-icon"><Lock /></el-icon>
                <span>安全可靠</span>
              </div>
              <div class="tech-item">
                <el-icon class="tech-icon"><Star /></el-icon>
                <span>智能对话</span>
              </div>
              <div class="tech-item">
                <el-icon class="tech-icon"><Cpu /></el-icon>
                <span>高性能架构</span>
              </div>
            </div>
          </div>
        </div>
      </el-main>

      <!-- 底部信息 -->
      <el-footer class="app-footer">
        <div class="footer-content">
          <p>Copyright © {{ new Date().getFullYear() }} SpringAI</p>
          <p>基于 Vue3 + Spring Boot + ElementPlus 开发</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { inject } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ChatDotRound, 
  ChatLineRound, 
  ArrowRight, 
  Lightning, 
  Lock, 
  Star, 
  Cpu,
  Link 
} from '@element-plus/icons-vue'

const router = useRouter()
const isDarkTheme = inject('isDarkTheme')
const toggleTheme = inject('toggleTheme')

// 导航到 Love App 页面
const goToLoveApp = () => {
  router.push('/loveapp')
}

// 导航到 Manus 页面
const goToManus = () => {
  router.push('/manus')
}
</script>

<style scoped>
.home-page {
  position: relative;
  z-index: 10;
  height: 100vh;
  backdrop-filter: blur(5px);
}

.main-container {
  height: 100vh;
}

.app-header {
  background: rgba(43, 47, 54, 0.9);
  backdrop-filter: blur(20px);
  color: white;
  padding: 0 30px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.3);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  margin-right: 15px;
  animation: pulse 2s infinite;
  filter: drop-shadow(0 0 10px rgba(121, 187, 255, 0.5));
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 15px;
}

.app-main {
  padding: 60px 40px 40px;
  height: calc(100vh - 140px);
  overflow-y: auto;
  display: flex;
  justify-content: center;
  align-items: center;
}

.welcome-section {
  width: 100%;
  max-width: 1200px;
  text-align: center;
}

.welcome-content {
  animation: fadeInUp 1s ease-out;
}

.welcome-title {
  font-size: 4rem;
  font-weight: 700;
  margin-bottom: 30px;
  line-height: 1.2;
  text-shadow: 0 0 30px rgba(121, 187, 255, 0.3);
}

.title-text {
  color: var(--text-color);
}

.title-highlight {
  background: linear-gradient(135deg, #79bbff, #95d475);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 20px rgba(121, 187, 255, 0.4));
}

.welcome-subtitle {
  font-size: 1.3rem;
  color: var(--text-color);
  opacity: 0.8;
  margin-bottom: 80px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 40px;
  margin-bottom: 100px;
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
}

.feature-card {
  background: rgba(45, 47, 54, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 25px;
  padding: 35px;
  box-shadow: 
    0 15px 35px rgba(0, 0, 0, 0.3),
    0 5px 15px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.23, 1, 0.320, 1);
  display: flex;
  align-items: center;
  gap: 25px;
  text-align: left;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(121, 187, 255, 0.05), 
    rgba(149, 212, 117, 0.05));
  opacity: 0;
  transition: opacity 0.4s;
}

.feature-card:hover {
  transform: translateY(-15px) scale(1.03);
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.4),
    0 10px 30px rgba(121, 187, 255, 0.2);
  border-color: rgba(121, 187, 255, 0.3);
}

.feature-card:hover::before {
  opacity: 1;
}

.love-app {
  border-left: 4px solid var(--primary-color);
}

.love-app:hover {
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.4),
    0 10px 30px rgba(64, 158, 255, 0.3);
}

.manus {
  border-left: 4px solid var(--secondary-color);
}

.manus:hover {
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.4),
    0 10px 30px rgba(103, 194, 58, 0.3);
}

.card-icon {
  font-size: 55px;
  flex-shrink: 0;
  filter: drop-shadow(0 0 15px currentColor);
}

.love-app .card-icon {
  color: var(--primary-color);
}

.manus .card-icon {
  color: var(--secondary-color);
}

.card-content {
  flex: 1;
}

.card-content h3 {
  font-size: 1.6rem;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-color);
}

.card-content p {
  color: var(--text-color);
  opacity: 0.8;
  margin-bottom: 18px;
  line-height: 1.6;
  font-size: 1rem;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-list li {
  color: var(--text-color);
  opacity: 0.7;
  font-size: 0.95rem;
  margin-bottom: 8px;
  line-height: 1.4;
}

.card-arrow {
  font-size: 28px;
  color: var(--text-color);
  opacity: 0.4;
  transition: all 0.4s;
}

.feature-card:hover .card-arrow {
  opacity: 1;
  transform: translateX(8px);
  color: var(--primary-color);
}

.tech-highlights {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 25px;
  max-width: 600px;
  margin: 0 auto;
}

.tech-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: rgba(45, 47, 54, 0.6);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  color: var(--text-color);
  font-weight: 500;
  transition: all 0.3s ease;
}

.tech-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  border-color: rgba(121, 187, 255, 0.2);
  background: rgba(45, 47, 54, 0.8);
}

.tech-icon {
  font-size: 22px;
  color: var(--primary-color);
  filter: drop-shadow(0 0 8px currentColor);
}

.app-footer {
  background: rgba(43, 47, 54, 0.9);
  backdrop-filter: blur(20px);
  text-align: center;
  color: var(--text-color);
  padding: 20px 0;
  height: 70px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 -4px 30px rgba(0, 0, 0, 0.3);
}

.footer-content {
  font-size: 14px;
  opacity: 0.7;
}

.footer-content p:first-child {
  margin-bottom: 5px;
}

/* 动画 */
@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(60px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-title {
    font-size: 2.8rem;
  }
  
  .feature-cards {
    grid-template-columns: 1fr;
    gap: 25px;
  }
  
  .feature-card {
    flex-direction: column;
    text-align: center;
    padding: 25px;
  }
  
  .tech-highlights {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .app-main {
    padding: 30px 20px 20px;
  }
}

@media (max-width: 480px) {
  .welcome-title {
    font-size: 2.2rem;
  }
  
  .welcome-subtitle {
    font-size: 1.1rem;
  }
  
  .feature-card {
    padding: 20px;
  }
  
  .card-icon {
    font-size: 45px;
  }
}
</style> 