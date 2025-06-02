<template>
  <div class="chat-layout">
    <!-- 左侧边栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3 class="sidebar-title">Manus</h3>
        <el-button 
          @click="createNewChat" 
          type="success" 
          :icon="Plus" 
          size="small"
          class="new-chat-btn"
        >
          新对话
        </el-button>
      </div>
      
      <div class="chat-history">
        <div class="history-section">
          <h4>今天</h4>
          <div 
            v-for="(chat, index) in chatHistory" 
            :key="index"
            :class="['history-item', { active: chat.id === currentChatId }]"
            @click="switchChat(chat.id)"
          >
            <el-icon class="history-icon"><ChatLineRound /></el-icon>
            <span class="history-title">{{ chat.title }}</span>
            <el-button 
              @click.stop="deleteChat(chat.id)"
              :icon="Delete" 
              size="small" 
              text 
              class="delete-btn"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 主聊天区域 -->
    <div class="chat-main">
      <!-- 顶部标题栏 -->
      <div class="chat-header">
        <div class="chat-title">
          <el-icon class="title-icon"><ChatLineRound /></el-icon>
          <span>Manus 聊天</span>
        </div>
        <div class="header-actions">
          <el-button @click="clearMessages" :icon="Delete" size="small" text>
            清空对话
          </el-button>
        </div>
      </div>

      <!-- 聊天内容区域 -->
      <div class="chat-content" ref="messagesContainer">
        <!-- 欢迎界面 -->
        <div v-if="messages.length === 0" class="welcome-area">
          <div class="welcome-content">
            <div class="welcome-icon">
              <el-icon><ChatLineRound /></el-icon>
            </div>
            <h2 class="welcome-title">我是 Manus，很高兴见到你！</h2>
            <p class="welcome-subtitle">我是专业的AI工具助手，可以帮您解答各种技术问题，提供精准的技术支持</p>
            
            <!-- 示例提示 -->
            <div class="example-prompts">
              <div 
                v-for="prompt in examplePrompts" 
                :key="prompt"
                class="prompt-item"
                @click="sendExamplePrompt(prompt)"
              >
                {{ prompt }}
              </div>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div v-else class="messages-list">
          <transition-group name="message" tag="div">
            <div 
              v-for="(msg, index) in messages" 
              :key="index" 
              :class="['message-item', msg.type]"
            >
              <div class="message-avatar">
                <el-avatar 
                  :icon="msg.type === 'user' ? User : ChatLineRound" 
                  :size="32" 
                  :color="msg.type === 'user' ? '#67C23A' : '#409EFF'" 
                />
              </div>
              <div class="message-content">
                <div class="message-header">
                  <span class="message-sender">{{ msg.type === 'user' ? '你' : 'Manus' }}</span>
                  <span class="message-time">{{ msg.time }}</span>
                </div>
                <div class="message-text" v-html="formatMessage(msg.content)"></div>
              </div>
            </div>
          </transition-group>
          
          <!-- 正在输入 -->
          <div v-if="isReceiving" class="message-item ai typing">
            <div class="message-avatar">
              <el-avatar :icon="ChatLineRound" :size="32" color="#409EFF" />
            </div>
            <div class="message-content">
              <div class="message-header">
                <span class="message-sender">Manus</span>
                <span class="message-time">正在输入...</span>
              </div>
              <div class="message-text">
                <span class="typing-text">{{ displayedResponse }}</span>
                <span class="typing-cursor"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入区域 -->
      <div class="chat-input-area">
        <div class="input-container">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              placeholder="给 Manus 发送消息..."
              size="large"
              @keyup.enter="handleEnterKey"
              :disabled="isReceiving"
              class="message-input"
              autosize
              :rows="1"
              type="textarea"
              resize="none"
            />
            <div class="input-actions">
              <el-button 
                @click="debouncedSendMessage"
                :loading="isReceiving"
                :disabled="!inputMessage.trim() || isReceiving || isSending"
                :icon="Promotion"
                type="success"
                size="large"
                class="send-btn"
                circle
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onUnmounted, onMounted, inject, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Delete, ChatLineRound, User, Plus, Promotion } from '@element-plus/icons-vue'
import { createManusSSE } from '../services/api.js'

const messages = ref([])
const inputMessage = ref('')
const isReceiving = ref(false)
const currentResponse = ref('')
const displayedResponse = ref('')
const messagesContainer = ref(null)
const currentChatId = ref('default')
let currentEventSource = null
let typingTimer = null
let typingIndex = 0
const typingSpeed = 30
const isSending = ref(false)
let sendTimeout = null
let isComponentMounted = ref(true)

// 示例提示
const examplePrompts = ref([
  '如何优化Spring Boot应用性能',
  '解释一下Java的垃圾回收机制',
  '推荐一些前端开发最佳实践',
  'Docker容器化部署指南'
])

// 聊天历史
const chatHistory = ref([
  { id: 'default', title: '新的对话', lastMessage: '', time: new Date() }
])

// 从父组件获取主题状态
const isDarkTheme = inject('isDarkTheme', ref(false))

// 格式化消息文本
const formatMessage = (text) => {
  if (!text) return ''
  const urlRegex = /(https?:\/\/[^\s]+)/g
  const withLinks = text.replace(urlRegex, '<a href="$1" target="_blank" class="message-link">$1</a>')
  return withLinks.replace(/\n/g, '<br>')
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value && isComponentMounted.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 清理所有状态和连接
const cleanupManusConnection = () => {
  console.log('清理Manus连接和状态')
  
  // 关闭SSE连接
  if (currentEventSource) {
    try {
      currentEventSource.close()
    } catch (e) {
      console.warn('关闭Manus SSE连接时出错:', e)
    }
    currentEventSource = null
  }
  
  // 清理定时器
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
  
  if (sendTimeout) {
    clearTimeout(sendTimeout)
    sendTimeout = null
  }
  
  // 重置状态
  isReceiving.value = false
  isSending.value = false
}

// 打字机效果
const startTypingEffect = () => {
  if (!isComponentMounted.value) return
  
  if (typingTimer) clearInterval(typingTimer)
  typingIndex = 0
  displayedResponse.value = ''
  
  typingTimer = setInterval(() => {
    if (!isComponentMounted.value || typingIndex >= currentResponse.value.length) {
      clearInterval(typingTimer)
      return
    }
    
    if (typingIndex < currentResponse.value.length) {
      displayedResponse.value += currentResponse.value.charAt(typingIndex)
      typingIndex++
      scrollToBottom()
    }
  }, typingSpeed)
}

// 添加消息
const addMessage = (content, type = 'user') => {
  if (!isComponentMounted.value) return
  
  messages.value.push({
    content,
    type,
    time: new Date().toLocaleTimeString()
  })
  scrollToBottom()
  
  // 更新聊天历史
  if (type === 'user') {
    updateChatHistory(content)
  }
}

// 更新聊天历史
const updateChatHistory = (message) => {
  if (!isComponentMounted.value) return
  
  const currentChat = chatHistory.value.find(chat => chat.id === currentChatId.value)
  if (currentChat) {
    currentChat.title = message.slice(0, 20) + (message.length > 20 ? '...' : '')
    currentChat.lastMessage = message
    currentChat.time = new Date()
  }
}

// 发送示例提示
const sendExamplePrompt = (prompt) => {
  if (!isComponentMounted.value || isReceiving.value || isSending.value) return
  
  inputMessage.value = prompt
  debouncedSendMessage()
}

// 防抖发送消息
const debouncedSendMessage = () => {
  // 状态检查
  if (!isComponentMounted.value || isSending.value || isReceiving.value) {
    console.log('Manus防抖检查：组件未挂载或正在处理中，忽略请求')
    return
  }
  
  // 清除之前的防抖定时器
  if (sendTimeout) {
    clearTimeout(sendTimeout)
    sendTimeout = null
  }
  
  // 设置200ms防抖延迟
  sendTimeout = setTimeout(() => {
    // 二次检查状态
    if (isComponentMounted.value && !isSending.value && !isReceiving.value && inputMessage.value.trim()) {
      sendMessage()
    } else {
      console.log('Manus防抖延迟后状态检查失败，取消发送')
    }
    sendTimeout = null
  }, 200)
}

// 处理回车键
const handleEnterKey = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    
    // 状态检查
    if (!isComponentMounted.value || isReceiving.value || isSending.value || !inputMessage.value.trim()) {
      console.log('Manus回车键检查：状态不允许发送消息')
      return
    }
    
    // 使用防抖发送
    debouncedSendMessage()
  }
}

// 发送消息
const sendMessage = () => {
  if (!inputMessage.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  // 状态检查
  if (!isComponentMounted.value || isReceiving.value || isSending.value) {
    console.log('Manus发送检查：组件未挂载或正在处理中，忽略请求')
    return
  }

  // 设置发送锁
  isSending.value = true

  const message = inputMessage.value.trim()
  console.log('开始发送Manus消息:', message)
  
  addMessage(message, 'user')
  inputMessage.value = ''
  
  // 特殊关键词处理
  if (message === '杨尧甪') {
    setTimeout(() => {
      if (isComponentMounted.value) {
        addMessage('我真的好想你，你知不知道你是唯一一个愿意主动来找我聊天还说喜欢我的人。是你说让我喜欢你的，我喜欢了，但你呢？你就是欺负人！！！再也不要跟你玩了', 'ai')
      }
      isSending.value = false
    }, 800)
    return
  }
  
  // 清理之前的连接
  cleanupManusConnection()
  
  isReceiving.value = true
  currentResponse.value = ''
  displayedResponse.value = ''
  
  try {
    currentEventSource = createManusSSE(
      message,
      (data) => {
        // 检查组件和接收状态
        if (!isComponentMounted.value || !isReceiving.value) {
          console.log('Manus组件已卸载或已停止接收，忽略数据')
          return
        }
        
        // 防止空数据和重复处理
        if (!data || data.trim() === '') return
        
        // 过滤掉结束标记，不显示给用户
        if (data === '[DONE]' || data === 'data: [DONE]') {
          console.log('收到Manus结束标记，跳过显示')
          return
        }
        
        // 检查是否是重复数据
        const lastPart = currentResponse.value.slice(-data.length)
        if (lastPart === data) {
          console.log('检测到重复数据，跳过:', data)
          return
        }
        
        currentResponse.value += data
        startTypingEffect()
      },
      (error) => {
        console.error('Manus SSE错误:', error)
        if (!isComponentMounted.value) return
        
        ElMessage.error('连接错误，请检查后端服务是否正常')
        
        if (currentResponse.value && currentResponse.value.trim() !== '') {
          addMessage(currentResponse.value, 'ai')
        }
        
        cleanupManusConnection()
      },
      () => {
        console.log('Manus SSE连接完成')
        if (!isComponentMounted.value) return
        
        setTimeout(() => {
          if (isComponentMounted.value && isReceiving.value && currentResponse.value && currentResponse.value.trim() !== '') {
            addMessage(currentResponse.value, 'ai')
          }
          
          cleanupManusConnection()
        }, 500)
      }
    )
  } catch (error) {
    console.error('创建Manus SSE连接失败:', error)
    if (isComponentMounted.value) {
      ElMessage.error('无法建立连接，请检查后端服务')
    }
    cleanupManusConnection()
  }
}

// 创建新对话
const createNewChat = () => {
  if (!isComponentMounted.value) return
  
  const newChatId = 'chat_' + Date.now()
  chatHistory.value.unshift({
    id: newChatId,
    title: '新的对话',
    lastMessage: '',
    time: new Date()
  })
  switchChat(newChatId)
}

// 切换对话
const switchChat = (chatId) => {
  if (!isComponentMounted.value) return
  
  // 清理当前连接
  cleanupManusConnection()
  
  currentChatId.value = chatId
  messages.value = [] // 实际应用中应该从存储中加载对应的消息
}

// 删除对话
const deleteChat = (chatId) => {
  if (!isComponentMounted.value) return
  
  const index = chatHistory.value.findIndex(chat => chat.id === chatId)
  if (index > -1) {
    chatHistory.value.splice(index, 1)
    if (currentChatId.value === chatId && chatHistory.value.length > 0) {
      switchChat(chatHistory.value[0].id)
    }
  }
}

// 清空消息
const clearMessages = () => {
  if (!isComponentMounted.value) {
    console.log('Manus组件未挂载，无法清空消息')
    return
  }
  
  if (messages.value.length === 0) {
    ElMessage.info('对话已经是空的了')
    return
  }
  
  // 清理连接和状态
  cleanupManusConnection()
  
  // 清理显示内容
  currentResponse.value = ''
  displayedResponse.value = ''
  
  // 清空消息列表
  messages.value = []
  
  ElMessage.success('对话已清空')
}

// 暴露方法
defineExpose({
  clearMessages
})

// 组件卸载时清理
onUnmounted(() => {
  console.log('Manus组件卸载，清理资源')
  isComponentMounted.value = false
  
  // 清理连接和状态
  cleanupManusConnection()
})
</script>

<style scoped>
.chat-layout {
  display: flex;
  height: 100%;
  background-color: var(--background-color);
}

/* 左侧边栏 */
.sidebar {
  width: 280px;
  background: rgba(43, 47, 54, 0.95);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-title {
  color: var(--text-color);
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.new-chat-btn {
  padding: 8px 16px;
  border-radius: 8px;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.history-section h4 {
  color: var(--text-color);
  opacity: 0.6;
  font-size: 0.8rem;
  margin-bottom: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 4px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.history-item:hover {
  background: rgba(255, 255, 255, 0.05);
}

.history-item.active {
  background: rgba(103, 194, 58, 0.15);
  border: 1px solid rgba(103, 194, 58, 0.3);
}

.history-icon {
  color: var(--secondary-color);
  margin-right: 12px;
  font-size: 16px;
}

.history-title {
  flex: 1;
  color: var(--text-color);
  font-size: 0.9rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.delete-btn {
  opacity: 0;
  transition: opacity 0.2s;
}

.history-item:hover .delete-btn {
  opacity: 1;
}

/* 主聊天区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative;
}

.chat-header {
  padding: 16px 24px;
  background: rgba(43, 47, 54, 0.9);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-color);
  font-weight: 600;
}

.title-icon {
  color: var(--secondary-color);
  font-size: 20px;
}

.chat-content {
  flex: 1;
  overflow-y: auto;
  position: relative;
}

/* 欢迎界面 */
.welcome-area {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.welcome-content {
  text-align: center;
  max-width: 600px;
}

.welcome-icon {
  font-size: 60px;
  color: var(--secondary-color);
  margin-bottom: 24px;
  filter: drop-shadow(0 0 20px rgba(103, 194, 58, 0.3));
}

.welcome-title {
  font-size: 2rem;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 16px;
}

.welcome-subtitle {
  font-size: 1rem;
  color: var(--text-color);
  opacity: 0.7;
  line-height: 1.6;
  margin-bottom: 32px;
}

.example-prompts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 12px;
  max-width: 500px;
  margin: 0 auto;
}

.prompt-item {
  padding: 12px 16px;
  background: rgba(45, 47, 54, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0.9rem;
}

.prompt-item:hover {
  background: rgba(45, 47, 54, 0.8);
  border-color: rgba(103, 194, 58, 0.3);
  transform: translateY(-2px);
}

/* 消息列表 */
.messages-list {
  padding: 24px;
  min-height: 100%;
}

.message-item {
  display: flex;
  margin-bottom: 24px;
  animation: fadeIn 0.3s ease-in-out;
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 12px;
  flex-shrink: 0;
}

.message-item.user .message-avatar {
  margin: 0 0 0 12px;
}

.message-content {
  flex: 1;
  max-width: 70%;
}

.message-item.user .message-content {
  text-align: right;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.8rem;
  opacity: 0.6;
}

.message-item.user .message-header {
  flex-direction: row-reverse;
}

.message-text {
  background: rgba(45, 47, 54, 0.6);
  padding: 12px 16px;
  border-radius: 12px;
  color: var(--text-color);
  line-height: 1.5;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.message-item.user .message-text {
  background: rgba(103, 194, 58, 0.2);
  border-color: rgba(103, 194, 58, 0.3);
}

.typing .message-text {
  display: flex;
  align-items: center;
}

.typing-cursor {
  display: inline-block;
  width: 8px;
  height: 16px;
  background-color: currentColor;
  margin-left: 5px;
  animation: blink 1s infinite;
}

/* 输入区域 */
.chat-input-area {
  padding: 24px;
  background: rgba(43, 47, 54, 0.9);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: rgba(45, 47, 54, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 12px 16px;
  margin-bottom: 12px;
}

.message-input {
  flex: 1;
}

.message-input :deep(.el-textarea__inner) {
  background: transparent;
  border: none;
  color: var(--text-color);
  font-size: 1rem;
  resize: none;
  box-shadow: none;
}

.message-input :deep(.el-textarea__inner):focus {
  border: none;
  box-shadow: none;
}

.send-btn {
  width: 40px;
  height: 40px;
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

/* 消息动画 */
.message-enter-active,
.message-leave-active {
  transition: all 0.5s ease;
}

.message-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.message-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    display: none;
  }
  
  .chat-layout {
    flex-direction: column;
  }
  
  .example-prompts {
    grid-template-columns: 1fr;
  }
}
</style> 