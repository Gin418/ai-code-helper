package com.example.aicodehelper;

import com.example.aicodehelper.ai.AiCodeHelper;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeHelperApplicationTests {

    @Resource
    private AiCodeHelper aiCodeHelper;

    @Test
    void chat() {
        aiCodeHelper.chat("你好，我是Xx");
    }

    @Test
    void chatWithMessage() {
        UserMessage userMessage = UserMessage.from(
                TextContent.from("描述图片"),
                ImageContent.from("http://dev-pms.hwhhotels.com/static/img/hwh-icon-logo.c5550ba7.png")
        );
        aiCodeHelper.chatWithMessage(userMessage);
    }

}
