# Spring AI 学习项目 (SpringAI_Learn)

## 📖 项目简介

SpringAI_Learn 是一个基于 Spring Boot 3.4.5 和 Spring AI 框架构建的现代化AI应用学习项目。该项目展示了如何使用Spring AI框架集成各种AI模型和功能，包括大语言模型聊天、RAG（检索增强生成）、AI工具调用、向量存储等核心功能。

## 🚀 技术栈

### 核心框架
- **Spring Boot**: 3.4.5
- **Spring AI**: 1.0.0-M6
- **Java**: 21

### AI 集成
- **阿里云通义千问**: Spring AI Alibaba Starter 1.0.0-M6.1
- **LangChain4j**: DashScope社区版 1.0.0-beta2
- **MCP客户端**: Spring AI MCP Client

### 数据存储
- **PostgreSQL**: 用于向量存储 (PgVector)
- **Spring JDBC**: 数据库操作

### 文档处理
- **iText**: PDF生成和处理
- **JSoup**: HTML解析和网页抓取
- **Markdown**: 文档读取器

### 工具库
- **Hutool**: 5.8.37 (Java工具库)
- **Knife4j**: 4.4.0 (API文档增强)
- **Lombok**: 1.18.30 (代码简化)

### 其他功能
- **邮件发送**: Spring Boot Mail
- **Kryo**: 5.6.2 (序列化)

## 🌟 核心功能

### 1. AI聊天系统
- **Love App聊天**: 基于SSE的实时聊天应用
- **Manus AI代理**: 智能对话代理系统
- **流式响应**: 支持SSE (Server-Sent Events) 实时流式输出

### 2. AI 代理系统
- **基础代理** (`BaseAgent`): 提供代理的基础功能和生命周期管理
- **工具调用代理** (`ToolCallAgent`): 支持函数调用的智能代理
- **ReAct代理** (`ReActAgent`): 推理-行动循环代理
- **KkomaManus**: 专用AI助手代理

### 3. AI 工具集
- **邮件发送工具** (`EmailSendTool`): 智能邮件发送功能
- **网络搜索工具** (`WebSearchTool`): 集成搜索API
- **网页抓取工具** (`WebScrapingTool`): 网页内容提取
- **文件操作工具** (`FileOperationTool`): 文件处理功能
- **PDF生成工具** (`PDFGenerationTool`): 动态PDF文档生成
- **终端操作工具** (`TerminalOperationTool`): 系统命令执行
- **资源下载工具** (`ResourceDownloadTool`): 资源下载管理
- **终止工具** (`TerminateTool`): 流程控制

### 4. RAG (检索增强生成)
- **向量存储**: 基于PgVector的向量数据库
- **文档加载器**: 支持多种文档格式加载
- **查询重写**: 智能查询优化
- **自定义顾问**: 个性化RAG策略

### 5. 聊天记忆管理
- **对话上下文**: 维护聊天历史和上下文
- **记忆持久化**: 对话记忆的存储和恢复

## 📁 项目结构

```
src/main/java/org/example/springai_learn/
├── SpringAiLearnApplication.java          # 应用启动类
├── controller/                            # 控制器层
│   ├── AiController.java                 # AI相关API接口
│   └── HealthController.java             # 健康检查接口
├── agent/                                # AI代理模块
│   ├── BaseAgent.java                    # 基础代理类
│   ├── ToolCallAgent.java               # 工具调用代理
│   ├── ReActAgent.java                  # ReAct代理
│   ├── KkomaManus.java                  # Manus代理
│   └── model/                           # 代理数据模型
├── app/                                  # 应用层
│   └── LoveApp.java                     # Love App聊天应用
├── tools/                               # AI工具集
│   ├── EmailSendTool.java               # 邮件发送工具
│   ├── WebSearchTool.java               # 网络搜索工具
│   ├── WebScrapingTool.java             # 网页抓取工具
│   ├── FileOperationTool.java           # 文件操作工具
│   ├── PDFGenerationTool.java           # PDF生成工具
│   ├── TerminalOperationTool.java       # 终端操作工具
│   ├── ResourceDownloadTool.java        # 资源下载工具
│   ├── TerminateTool.java               # 终止工具
│   └── ToolRegistration.java            # 工具注册管理
├── rag/                                 # RAG检索增强生成
│   ├── QueryRewriter.java               # 查询重写器
│   ├── PgVectorVectorStoreConfig.java   # PgVector配置
│   ├── LoveAppVectorStoreConfig.java    # Love App向量存储配置
│   ├── LoveAppRagCustomAdvisorFactory.java  # 自定义RAG顾问工厂
│   ├── LoveAppRagCloudAdvisorConfig.java    # 云端RAG顾问配置
│   └── LoveAppDocumentLoader.java       # 文档加载器
├── config/                              # 配置类
├── constant/                            # 常量定义
├── ChatMemory/                          # 聊天记忆管理
├── advisor/                             # 顾问模式实现
└── demo/                               # 演示示例
```

## 🛠️ 环境要求

- **Java**: 21+
- **Maven**: 3.6+
- **PostgreSQL**: 12+ (如需使用向量存储功能)
- **Node.js**: 16+ (如需运行前端)

## ⚙️ 配置说明

### 1. 数据库配置

在 `application.yml` 中配置PostgreSQL数据库：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-host:5432/your-database
    username: your-username
    password: your-password
```

### 2. AI模型配置

配置阿里云通义千问API密钥（在环境变量或配置文件中）：

```yaml
spring:
  ai:
    alibaba:
      api-key: your-api-key
```

### 3. 邮件服务配置

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 465
    username: your-email@qq.com
    password: your-auth-code  # QQ邮箱授权码
```

### 4. 搜索API配置

```yaml
search-api:
  api-key: your-search-api-key
```

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd SpringAI_Learn
```

### 2. 配置环境

复制并编辑配置文件：

```bash
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

在 `application-local.yml` 中填入你的API密钥和数据库配置。

### 3. 启动应用

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者编译后启动
mvn clean package
java -jar target/SpringAI_Learn-0.0.1-SNAPSHOT.jar
```

### 4. 访问应用

- **应用地址**: http://localhost:8088/api
- **API文档**: http://localhost:8088/api/swagger-ui.html
- **健康检查**: http://localhost:8088/api/health

## 📚 API 文档

### 主要接口

#### 1. Love App 聊天

```http
GET /api/ai/love_app/chat/sse?message={message}&chatId={chatId}
```

**说明**: 启动Love App的SSE聊天流，支持实时流式响应。

**参数**:
- `message`: 用户消息内容
- `chatId`: 聊天会话ID

**响应**: SSE流式数据

#### 2. Manus AI 聊天

```http
GET /api/ai/manus/chat?message={message}
```

**说明**: 与Manus AI代理进行对话。

**参数**:
- `message`: 用户消息内容

**响应**: SSE流式数据

#### 3. 健康检查

```http
GET /api/health
```

**说明**: 检查应用运行状态。

### SSE 响应格式

所有SSE接口都遵循以下响应格式：

```
data: {"type":"message","content":"AI回复内容"}

data: [DONE]
```

## 🔧 开发指南

### 1. 添加新的AI工具

1. 在 `tools` 包下创建新的工具类
2. 实现工具的核心逻辑
3. 在 `ToolRegistration` 中注册新工具
4. 在代理中使用新工具

示例：

```java
@Component
public class MyCustomTool {
    
    @Tool("工具描述")
    public String myToolFunction(String parameter) {
        // 工具逻辑实现
        return "工具执行结果";
    }
}
```

### 2. 创建新的AI代理

1. 继承 `BaseAgent` 类
2. 实现特定的代理逻辑
3. 配置所需的工具和参数
4. 在控制器中暴露代理接口

### 3. 扩展RAG功能

1. 在 `rag` 包下添加新的文档加载器
2. 配置向量存储策略
3. 自定义查询重写逻辑
4. 实现自定义顾问模式

## 🚨 注意事项

1. **API密钥安全**: 不要将API密钥提交到版本控制系统
2. **数据库连接**: 确保PostgreSQL服务正常运行
3. **内存管理**: AI模型可能消耗较多内存，建议分配足够的JVM堆内存
4. **并发控制**: SSE连接数量有限，注意连接池管理
5. **日志监控**: 开发环境下建议开启DEBUG日志以便调试

## 📝 开发计划

- [ ] 支持更多AI模型提供商
- [ ] 实现对话历史的持久化存储
- [ ] 添加用户认证和权限控制
- [ ] 优化向量存储性能
- [ ] 扩展更多实用AI工具
- [ ] 完善错误处理和重试机制

## 🤝 贡献指南

欢迎提交Issue和Pull Request来帮助改进项目！

## 📄 许可证

MIT License

## 📞 联系方式

如有问题，请提交Issue或联系项目维护者。

---

**注意**: 这是一个学习项目，主要用于探索和学习Spring AI框架的各种功能。在生产环境中使用前，请确保充分测试和安全评估。 
