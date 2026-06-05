package com.example.aicodehelper.ai;

import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;

import java.util.List;

/**
 * @Author Q
 * @Description TODO
 * @Date 2026/5/22 18:39
 * @Version 1.0.0
 **/
//@AiService
public interface AiCodeHelperService {

    @SystemMessage(fromResource = "system-prompt.txt")
    String chat(String userMessage);
//    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    @SystemMessage(fromResource = "system-prompt.txt")
    Report chatForReport(String userMessage);

    @SystemMessage(fromResource = "system-prompt.txt")
    ChatResponse chatForRequest(ChatRequest chatRequest);

    @SystemMessage(fromResource = "system-prompt.txt")
    Result<String> chatWithRag(String userMessage);

    // 学习报告
    record Report(String name, List<String> suggestionList) {}

}
