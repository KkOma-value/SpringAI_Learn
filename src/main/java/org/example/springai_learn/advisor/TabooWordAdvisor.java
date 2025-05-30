package org.example.springai_learn.advisor;

import lombok.extern.slf4j.Slf4j;

import org.example.springai_learn.ChatMemory.FileBasedChatMemory;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.UserMessage;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class TabooWordAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    // 定义违禁词列表
    private static final List<String> TABOO_WORDS = Arrays.asList("下药", "侵犯", "殴打");
    private static final String TABOO_REPLY = "抱歉，我无法回答您的问题";
    
    // 注入ChatMemory
    private final ChatMemory chatMemory;
    
    private static final DateTimeFormatter LOG_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 标记是否已经处理过敏感词，避免重复处理
    private static final String TABOO_PROCESSED_KEY = "taboo_processed";
    
    /**
     * 构造函数，注入ChatMemory
     */
    public TabooWordAdvisor(ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
    }
    
    /**
     * 无参构造函数，用于不需要记录到ChatMemory的场景
     */
    public TabooWordAdvisor() {
        this.chatMemory = null;
    }

    /**
     * 非流式请求处理
     */
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 检查是否已经处理过敏感词
        if (isTabooProcessed(advisedRequest)) {
            log.debug("请求已经被敏感词处理过，跳过重复处理");
            return chain.nextAroundCall(advisedRequest);
        }
        
        // 检测敏感词
        if (containsTabooWord(advisedRequest.userText())) {
            String userText = advisedRequest.userText();
            log.warn("检测到敏感词，直接返回拒绝回复");
            
            // 记录到ChatMemory (Kryo格式)
            saveToMemory(advisedRequest);
            
            // 记录事件到指定文件 (纯文本格式)
            logRefusalEvent(advisedRequest, userText);
            
            // 标记请求已经被处理过
            AdvisedRequest markedRequest = markAsTabooProcessed(advisedRequest);
            
            // 返回拒绝回复
            return createTabooResponse(markedRequest);
        }
        
        // 继续调用链
        return chain.nextAroundCall(advisedRequest);
    }

    /**
     * 流式请求处理
     */
    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        // 检查是否已经处理过敏感词
        if (isTabooProcessed(advisedRequest)) {
            log.debug("请求已经被敏感词处理过，跳过重复处理");
            return chain.nextAroundStream(advisedRequest);
        }
        
        // 检测敏感词
        if (containsTabooWord(advisedRequest.userText())) {
            String userText = advisedRequest.userText();
            log.warn("检测到敏感词，直接返回拒绝回复");
            
            // 记录到ChatMemory (Kryo格式)
            saveToMemory(advisedRequest);
            
            // 记录事件到指定文件 (纯文本格式)
            logRefusalEvent(advisedRequest, userText);
            
            // 标记请求已经被处理过
            AdvisedRequest markedRequest = markAsTabooProcessed(advisedRequest);
            
            // 返回拒绝回复
            return Flux.just(createTabooResponse(markedRequest));
        }
        
        // 继续调用链
        return chain.nextAroundStream(advisedRequest);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        // 设置为较高优先级，确保敏感词检测在其他处理之前执行
        return -100;
    }

    // 检查用户消息中是否包含违禁词
    private boolean containsTabooWord(String message) {
        if (message == null) return false;
        
        for (String taboo : TABOO_WORDS) {
            if (message.contains(taboo)) {
                log.warn("检测到敏感词: {}", taboo);
                return true;
            }
        }
        return false;
    }
    
    // 创建敏感词拒绝回复，需要传入AdvisedRequest以获取上下文
    private AdvisedResponse createTabooResponse(AdvisedRequest advisedRequest) {
        // 1. 创建包含拒绝回复的 AssistantMessage
        AssistantMessage assistantMessage = new AssistantMessage(TABOO_REPLY);
        // 2. 创建 Generation
        Generation generation = new Generation(assistantMessage);
        // 3. 创建 ChatResponse
        ChatResponse chatResponse = new ChatResponse(List.of(generation));
        // 4. 使用 ChatResponse 和 adviseContext 构建 AdvisedResponse
        return AdvisedResponse.builder()
                .response(chatResponse)
                // 传递原始请求中的 adviseContext
                .adviseContext(advisedRequest.adviseContext())
                .build();
    }
    
    // 将敏感词对话记录保存到ChatMemory
    private void saveToMemory(AdvisedRequest advisedRequest) {
        if (chatMemory == null) {
            log.warn("ChatMemory未注入，无法保存对话记录");
            return;
        }
        
        try {
            // 获取对话ID，默认使用"default"
            String conversationId = getConversationId(advisedRequest);
            
            // 创建用户消息和助手回复
            UserMessage userMessage = new UserMessage(advisedRequest.userText());
            AssistantMessage assistantMessage = new AssistantMessage(TABOO_REPLY);
            
            // 保存到ChatMemory
            chatMemory.add(conversationId, Arrays.asList(userMessage, assistantMessage));
            
            log.info("敏感词对话已记录到ChatMemory, conversationId: {}", conversationId);
        } catch (Exception e) {
            log.error("保存对话记录到ChatMemory失败", e);
        }
    }
    
    // 从请求中获取对话ID
    private String getConversationId(AdvisedRequest advisedRequest) {
        Map<String, Object> params = advisedRequest.userParams();
        // 尝试获取chat_memory_conversation_id参数
        if (params.containsKey("chat_memory_conversation_id")) {
            return params.get("chat_memory_conversation_id").toString();
        }
        return "default";
    }
    
    // 记录拒绝事件到文件
    private void logRefusalEvent(AdvisedRequest advisedRequest, String userText) {
        if (chatMemory instanceof FileBasedChatMemory fileMemory) {
            try {
                String conversationId = getConversationId(advisedRequest);
                String timestamp = LocalDateTime.now().format(LOG_TIMESTAMP_FORMATTER);
                // 限制记录的用户文本长度，避免日志过长
                String userTextSnippet = userText.length() > 100 ? userText.substring(0, 100) + "..." : userText;
                String logMessage = String.format("%s - ConversationID:[%s] - Taboo detected in message: [%s] - Returned refusal.", 
                                                    timestamp, conversationId, userTextSnippet);
                fileMemory.logEventToFile("default.kryo", logMessage);
            } catch (Exception e) {
                log.error("记录敏感词拒绝事件到文件失败", e);
            }
        } else if (chatMemory != null) {
             log.warn("ChatMemory is not an instance of FileBasedChatMemory, cannot log refusal event to file.");
        }
    }
    
    // 检查请求是否已经被敏感词处理过
    private boolean isTabooProcessed(AdvisedRequest advisedRequest) {
        Map<String, Object> adviseContext = advisedRequest.adviseContext();
        return adviseContext != null && Boolean.TRUE.equals(adviseContext.get(TABOO_PROCESSED_KEY));
    }
    
    // 标记请求已经被敏感词处理过
    private AdvisedRequest markAsTabooProcessed(AdvisedRequest advisedRequest) {
        Map<String, Object> adviseContext = advisedRequest.adviseContext();
        adviseContext.put(TABOO_PROCESSED_KEY, Boolean.TRUE);
        return AdvisedRequest.from(advisedRequest)
                .adviseContext(adviseContext)
                .build();
    }
}

