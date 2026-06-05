package com.example.aicodehelper.config;

import com.example.aicodehelper.ai.llistener.EmbeddingListen;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author Q
 * @Description TODO
 * @Date 2026/6/4 下午11:18
 * @Version 1.0.0
 **/
@Configuration
public class MilvusConfig {

    @Value("${milvus.uri}")
    private String uri;

    @Bean
    public MilvusServiceClient milvusServiceClient() {
        return new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost(uri)
                        .withPort(19530)
                        .build()
        );
//        ConnectConfig config = ConnectConfig.builder()
//                .uri("http://192.168.111.131:19530")
//                .build();
//        return new MilvusClientV2(config);
    }

    @Bean
    public EmbeddingModel embeddingModel(EmbeddingModel model, EmbeddingListen embeddingListen) {
        return model.addListener(embeddingListen);
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(MilvusServiceClient milvusServiceClient) {
        return MilvusEmbeddingStore.builder()
                .milvusClient(milvusServiceClient)                            // Port for Milvus instance
                .collectionName("example2_collection")      // Name of the collection
                .dimension(1024)                            // Dimension of vectors
                .indexType(IndexType.FLAT)                 // Index type
                .metricType(MetricType.COSINE)             // Metric type
                .username("username")                      // Username for Milvus
                .password("password")                      // Password for Milvus
                .consistencyLevel(ConsistencyLevelEnum.EVENTUALLY)  // Consistency level
                .autoFlushOnInsert(true)                   // Auto flush after insert
                .idFieldName("id")                         // ID field name
                .textFieldName("text")                     // Text field name
                .metadataFieldName("metadata")             // Metadata field name
                .vectorFieldName("vector")                 // Vector field name
                .build();
    }
}
