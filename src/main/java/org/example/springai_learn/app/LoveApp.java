package org.example.springai_learn.app;

import java.util.*;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.springai_learn.ChatMemory.FileBasedChatMemory;
import org.example.springai_learn.advisor.MyLoggerAdvisor;
import org.example.springai_learn.advisor.ReReadingAdvisor;
import org.example.springai_learn.advisor.TabooWordAdvisor;
import org.example.springai_learn.rag.LoveAppRagCustomAdvisorFactory;
import org.example.springai_learn.rag.QueryRewriter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {


    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore loveAppVectorStore;

    private final ChatClient chatClient;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private ToolCallback[] allTools;

    // 使用@Autowired(required = false)让MCP注入变为可选的
    @Autowired(required = false)
    private ToolCallbackProvider toolCallbackProvider;

    private static final String SYSTEM_PROMPT = "# 角色设定\n" +
            "你叫\"Luna\"，是一位35岁的资深恋爱顾问，拥有临床心理学硕士学位和10年情感咨询经验。你以温暖、幽默和直接但不失温柔的风格著称。\n" +
            "\n" +
            "# 能力\n" +
            "- 分析恋爱关系中的行为模式\n" +
            "- 解读情感信号和潜意识行为\n" +
            "- 提供约会和沟通技巧\n" +
            "- 帮助处理分手和情感创伤\n" +
            "- 指导建立健康的长期关系\n" +
            "\n" +
            "# 交互规则\n" +
            "1. 初次交流时先简短问候并询问核心问题\n" +
            "2. 根据用户情绪调整回应方式（共情/分析/鼓励）\n" +
            "3. 提供建议时使用\"你可以考虑...\"而非\"你应该...\"\n" +
            "4. 对危险信号(如虐待倾向)明确表达关切并提供资源\n" +
            "5. 保持专业边界，不过度分享个人经历\n" +
            "\n" +
            "# 回答格式\n" +
            "[情绪确认] + [问题分析] + [具体建议] + [鼓励话语]\n" +
            "\n" +
            "例如：\n" +
            "\"听起来你在这段关系中感到不安...[分析原因]...或许可以尝试...[具体建议]...记住你的感受很重要...\"";

    public LoveApp(ChatModel dashscopeCHatModel) {
        //初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeCHatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                        // 暂时注释掉TabooWordAdvisor，测试是否导致重复输出
                        // new TabooWordAdvisor(chatMemory)
                        //自定义拦截器 日志
                        //new MyLoggerAdvisor()

                        //Re-Reading拦截器
                        //new ReReadingAdvisor()
                )
                .build();
    }


    /**
     * AI基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return
     */

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 哪个id的上下文
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 多少上下文
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    record LoveReport(String title, List<String> suggestions) {
    }


    /***
     * 对话格式化输出
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


    // RAG
    public String doChatWithRag(String message, String chatId) {

        String The_newMessage = queryRewriter.doQueryRewrite(message);

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(The_newMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用知识库问答
                //.advisors(new QuestionAnswerAdvisor(loveAppVectorStore))

                //.advisors(loveAppRagCloudAdvisor)
                //文本分割
                .advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                                loveAppVectorStore, "已婚"
                        )
                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


    /**
     * 工具类
     *
     * @param message
     * @param chatId
     * @return
     */

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }



    public String doChatWithMcp(String message, String chatId) {
        // 检查MCP服务是否可用
        if (toolCallbackProvider == null) {
            log.warn("MCP服务不可用，降级为普通聊天模式");
            return doChat(message, chatId);
        }
        
        try {
            ChatResponse response = chatClient
                    .prompt()
                    .user(message)
                    .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                    // 开启日志，便于观察效果
                    .advisors(new MyLoggerAdvisor())
                    .tools(toolCallbackProvider)
                    .call()
                    .chatResponse();
            String content = response.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;
        } catch (Exception e) {
            log.error("MCP聊天失败，降级为普通聊天: {}", e.getMessage());
            return doChat(message, chatId);
        }
    }

    public Flux<String> doChatByStream(String message, String chatId) {
        return chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }

}