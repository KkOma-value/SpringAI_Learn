package org.example.springai_learn.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class LangChain4j {
    public static void main(String[] args) {
        QwenChatModel build = QwenChatModel.builder()
                .apiKey(TestApiKey.key)
                .modelName("qwen-max")
                .build();
        String ans = build.chat("我的对象不要我了,我该怎么办");
        System.out.println(ans);
    }
}
