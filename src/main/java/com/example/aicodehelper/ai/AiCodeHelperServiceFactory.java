package com.example.aicodehelper.ai;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
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

    @Resource
    private ContentRetriever contentRetriever;

    @Bean
    public AiCodeHelperService aiCodeHelperService() {
//        return AiServices.create(AiCodeHelperService.class, qwenChatModel);
        //会话记忆
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        return AiServices.builder(AiCodeHelperService.class)
                .chatModel(qwenChatModel)
                .chatMemory(chatMemory)
                .contentRetriever(contentRetriever) // RAG 检索增强生成
//                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();
        // RAG
//        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//        // 1. 加载文档
//        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs");
//        // 2. 使用内置的 EmbeddingModel 转换文本为向量，然后存储到自动注入的内存 embeddingStore 中
//        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
//        // 构造 AI Service
//        return AiServices.builder(AiCodeHelperService.class)
//                .chatModel(qwenChatModel)
//                .chatMemory(chatMemory)
//                // RAG：从内存 embeddingStore 中检索匹配的文本片段
//                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
//                .build();
    }
}

