package org.example.springai_learn.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component  // 添加组件注解，使Spring能够管理这个Bean
public class EmailSendTool {

    /**
     * 发送邮件，但是需要在本机自己定义邮件
     */
    @Resource
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:826285065@qq.com}")
    private String fromEmail;  // 从配置中获取发件人邮箱，默认使用QQ邮箱
    
    @Value("${spring.mail.password}")
    private String password;
    
    @Value("${spring.mail.host:smtp.qq.com}")
    private String host;  // 默认使用QQ邮箱SMTP服务器
    
    @Value("${spring.mail.port:465}")
    private int port;  // QQ邮箱使用465端口（SSL）

    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000; // 2秒

    @Tool(description = "Send email to specified recipient")
    public boolean sendEmail(
            @ToolParam(description = "Recipient email address") String to,
            @ToolParam(description = "Email subject") String subject,
            @ToolParam(description = "Email content") String content) {

        // 1. 增强型参数校验
        if (!isValidEmail(to)) {
            System.err.println("[EmailTool] Error: Invalid recipient email - " + to);
            return false;
        }

        // 2. 确保MailSender正确配置
        ensureMailSenderConfigured();

        // 3. 构建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);  // 设置发件人
        message.setTo(to.trim());
        message.setSubject(subject != null ? subject.trim() : "(No Subject)");
        message.setText(content != null ? content.trim() : "");

        // 4. 发送邮件（带重试机制）
        return sendWithRetry(message);
    }

    // 确保MailSender正确配置
    private void ensureMailSenderConfigured() {
        if (mailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) mailSender;
            
            // 确保设置了关键属性
            if (mailSenderImpl.getHost() == null || mailSenderImpl.getHost().isEmpty()) {
                mailSenderImpl.setHost(host);
            }
            
            if (mailSenderImpl.getPort() == 0) {
                mailSenderImpl.setPort(port);
            }
            
            if (mailSenderImpl.getUsername() == null || mailSenderImpl.getUsername().isEmpty()) {
                mailSenderImpl.setUsername(fromEmail);
            }
            
            if (mailSenderImpl.getPassword() == null || mailSenderImpl.getPassword().isEmpty()) {
                mailSenderImpl.setPassword(password);
            }
            
            // QQ邮箱需要不同的属性设置
            Properties props = mailSenderImpl.getJavaMailProperties();
            
            // 清除可能存在的starttls设置（QQ邮箱不使用）
            props.remove("mail.smtp.starttls.enable");
            
            // 设置认证
            props.put("mail.smtp.auth", "true");
            
            // 设置SSL
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.ssl.enable", "true");
            
            // 设置超时
            props.put("mail.smtp.connectiontimeout", "15000");
            props.put("mail.smtp.timeout", "15000");
            props.put("mail.smtp.writetimeout", "15000");
            
            // 调试信息
            props.put("mail.debug", "true");
        }
    }

    // 带重试机制的邮件发送
    private boolean sendWithRetry(SimpleMailMessage message) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRIES) {
            try {
                if (mailSender == null) {
                    System.err.println("[EmailTool] Error: Mail sender not initialized");
                    return false;
                }

                mailSender.send(message);
                System.out.println("[EmailTool] Success: Email sent to " + message.getTo()[0]);
                return true;

            } catch (MailAuthenticationException e) {
                System.err.println("[EmailTool] Error: Authentication failed - " + e.getMessage());
                lastException = e;
                // 认证错误不需要重试
                break;
            } catch (MailSendException e) {
                attempts++;
                lastException = e;
                System.err.println("[EmailTool] Error: Send failed (attempt " + attempts + "/" + MAX_RETRIES + ") - " + e.getMessage());
                
                if (attempts < MAX_RETRIES) {
                    try {
                        // 等待一段时间后重试
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                attempts++;
                lastException = e;
                System.err.println("[EmailTool] Unexpected error (attempt " + attempts + "/" + MAX_RETRIES + "): " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                
                if (attempts < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (lastException != null) {
            System.err.println("[EmailTool] All attempts failed. Last error: " + lastException.getMessage());
        }
        return false;
    }

    // 增强的邮箱验证方法
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.trim().matches(emailRegex);
    }
}
