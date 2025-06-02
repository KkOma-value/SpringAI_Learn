# SpringAI 前端项目

这是一个基于 Vue3 + Element Plus 的前端聊天应用，用于与 SpringBoot 后端的 AI 接口进行交互。

## 技术栈

- **Vue 3** - 前端框架
- **Element Plus** - UI 组件库
- **Axios** - HTTP 请求库
- **Vite** - 构建工具
- **Server-Sent Events (SSE)** - 实时流式数据传输

## 功能特性

- 🚀 支持两种不同的 AI 聊天模型
  - Love App 聊天 (支持 chatId)
  - Manus 聊天
- 💬 实时流式聊天响应
- 🎨 现代化的聊天界面
- 📱 响应式设计
- 🔄 自动滚动到最新消息
- 🧹 一键清空聊天记录

## 后端接口

项目对接以下 SpringBoot 后端接口：

### 1. Love App 聊天接口
```
GET /api/ai/love_app/chat/sse
参数:
- message: 聊天消息
- chatId: 聊天会话ID
响应: text/event-stream (SSE流)
```

### 2. Manus 聊天接口
```
GET /api/ai/manus/chat
参数:
- message: 聊天消息
响应: SseEmitter (SSE流)
```

## 安装和运行

### 1. 安装依赖
```bash
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

项目将在 http://localhost:3000 启动

### 3. 构建生产版本
```bash
npm run build
```

## 项目结构

```
src/
├── components/          # Vue 组件
│   ├── LoveAppChat.vue # Love App 聊天组件
│   └── ManusChat.vue   # Manus 聊天组件
├── services/           # 服务层
│   └── api.js         # API 接口和 SSE 连接管理
├── App.vue            # 根组件
└── main.js           # 应用入口
```

## 主要功能

### SSE 流式聊天
- 使用原生 EventSource API 处理 SSE 连接
- 实时显示 AI 回复的流式数据
- 自动处理连接错误和重连

### 用户体验
- 发送消息时显示加载状态
- 实时显示 AI 正在输入的内容
- 消息时间戳显示
- 自动滚动到最新消息

### 错误处理
- 网络连接异常提示
- 后端服务不可用提示
- 用户友好的错误信息

## 配置说明

### 后端服务地址
在 `src/services/api.js` 中修改后端服务地址：

```javascript
const api = axios.create({
  baseURL: 'http://localhost:8123/api',  // 修改为你的后端地址
  // ...
})
```

### 跨域配置
如果遇到跨域问题，需要在后端配置 CORS，或者在前端配置代理。

## 开发说明

- 项目使用 Vue 3 Composition API
- UI 组件基于 Element Plus
- 状态管理使用 Vue 3 的 reactive 系统
- 支持现代浏览器的 ES6+ 特性

## 注意事项

1. 确保后端服务运行在 `http://localhost:8123`
2. 后端需要支持 CORS 跨域请求
3. SSE 连接需要现代浏览器支持
4. 建议在开发时打开浏览器控制台查看连接状态 