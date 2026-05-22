package com.example.aicodehelper.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Q
 * @Description TODO
 * @Date 2026/5/22 18:49
 * @Version 1.0.0
 **/
@Configuration
public class AiCodeHelperServiceFactory {

    @Resource
    private ChatModel qwenChatModel;

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
        return AiServices.create(AiCodeHelperService.class, qwenChatModel);
    }
}

