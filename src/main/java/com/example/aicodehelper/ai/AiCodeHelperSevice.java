package com.example.aicodehelper.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * @Author Q
 * @Description TODO
 * @Date 2026/5/22 18:39
 * @Version 1.0.0
 **/
public interface AiCodeHelperSevice {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);

}
