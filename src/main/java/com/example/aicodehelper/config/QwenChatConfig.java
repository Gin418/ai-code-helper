package com.example.aicodehelper.config;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * @Author Q
 * @Description TODO
 * @Date 2026/5/22 14:53
 * @Version 1.0.0
 **/
//@Configuration
public class QwenChatConfig {

    @Bean
    @Primary
    public QwenChatModel myQwenChatModel() {
        return QwenChatModel.builder()
                .apiKey("sk-1faa046ac3fc4ad38dc961e0a831d140")
                .modelName("qwen-max")
                .enableSearch(true)
                .temperature(0.7F)
                .maxTokens(4096)
                .stops(List.of("Hello"))
                .build();
    }
}
