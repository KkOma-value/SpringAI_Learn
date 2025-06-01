package org.example.springai_learn.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

// 添加测试专用属性，禁用邮件验证
@SpringBootTest
@TestPropertySource(properties = {
        "spring.mail.test-connection=false",
        "spring.boot.mail.sender.auto-validation=false"
})
class EmailSendToolTest {
    @Autowired
    private EmailSendTool emailTool;  // 使用依赖注入

    @Test
    void sendEmail() {

        boolean result = emailTool.sendEmail("Your Email", "QQ邮箱测试邮件",
                "这是一封从QQ邮箱发送到Gmail的测试邮件，用于验证邮件发送功能。\n\n发送时间：" + 
                java.time.LocalDateTime.now());
        assertTrue(result);
    }
}