package com.example.aicodehelper.ai.llistener;

import dev.langchain4j.model.embedding.listener.EmbeddingModelListener;
import dev.langchain4j.model.embedding.listener.EmbeddingModelRequestContext;
import dev.langchain4j.model.embedding.listener.EmbeddingModelResponseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author Q
 * @Description TODO
 * @Date 2026/6/4 下午6:02
 * @Version 1.0.0
 **/
@Component
@Slf4j
public class EmbeddingListen implements EmbeddingModelListener {
    @Override
    public void onRequest(EmbeddingModelRequestContext requestContext) {
        log.info("textSegments: {}", requestContext.textSegments());
        EmbeddingModelListener.super.onRequest(requestContext);
    }

    @Override
    public void onResponse(EmbeddingModelResponseContext responseContext) {
        log.info("textSegments: {}, response:{}", responseContext.textSegments(), responseContext.response());
        EmbeddingModelListener.super.onResponse(responseContext);
    }
}
