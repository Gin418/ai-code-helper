package com.example.aicodehelper.ai;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.Result;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.highlevel.collection.ListCollectionsParam;
import io.milvus.param.highlevel.collection.response.ListCollectionsResponse;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;
    @Resource
    private EmbeddingModel embeddingModel;
    @Resource
    private MilvusServiceClient milvusServiceClient;

    @Test
    void chat() {
        String result = aiCodeHelperService.chat("你好，你是谁");
        System.out.println(result);
    }

    @Test
    void chatWithMemory() {
        String result = aiCodeHelperService.chat("你好，我是Xx");
        System.out.println(result);
        result = aiCodeHelperService.chat("我是谁");
        System.out.println(result);
    }

    @Test
    void chatForReport() {
        String userMessage = "你好，我是Xx，帮我制定一个AI学习计划";
        AiCodeHelperService.Report report = aiCodeHelperService.chatForReport(userMessage);
        System.out.println(report);
    }

    @Test
    void chatForRequest() {
        UserMessage userMessage = UserMessage.from("""
                请给我生成一个AI学习计划。
                严格返回 JSON 格式
                """);
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(JSON)
                .jsonSchema(JsonSchema.builder()
                        .name("Person")
                        .rootElement(JsonArraySchema.builder()
                                .items(JsonObjectSchema.builder()
                                        .addStringProperty("name")
                                        .addStringProperty("suggestion")
                                        .required("name", "suggestion")
                                        .build())
                                .build())
                        .build())
                .build();
        ChatRequest chatRequest = ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(userMessage)
                .build();
        ChatResponse request = aiCodeHelperService.chatForRequest(chatRequest);
        System.out.println(request.aiMessage().text());
    }

    @Test
    void chatWithEsayRag() {
        String result = aiCodeHelperService.chat("怎么学习 Java？有哪些常见面试题？");
        System.out.println(result);
    }

    @Test
    void chatWithRag() {
        Result<String> result = aiCodeHelperService.chatWithRag("怎么学习 Java？有哪些常见面试题？");
        System.out.println(result.content());
        System.out.println(result.sources());
    }

    @Test
    void chatWithEmbedding() {
//        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
//                .apiKey("sk-1faa046ac3fc4ad38dc961e0a831d140")
//                .modelName("text-embedding-v3")
//                .build();
//        EmbeddingModel model = embeddingModel.addListener(new EmbeddingListen());
        Response<Embedding> response = embeddingModel.embed("你好");
        Response<Embedding> response2 = embeddingModel.embed("Spring Boot是什么");
        Response<Embedding> response1 = embeddingModel.embed("Spring Boot是一个框架");
        System.out.println(response);
    }

    @Test
    void chatWithMilvus() {
//        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
//                .apiKey("sk-1faa046ac3fc4ad38dc961e0a831d140")
//                .modelName("text-embedding-v4")
//                .build();
//        EmbeddingModel model = embeddingModel.addListener(new EmbeddingListen());
//        MilvusServiceClient client = new MilvusServiceClient(
//                ConnectParam.newBuilder()
//                        .withHost("192.168.111.131")
//                        .withPort(19530)
//                        .build()
//        );
        ListCollectionsParam param =
                ListCollectionsParam.newBuilder()
                        .build();

        R<ListCollectionsResponse> response =
                milvusServiceClient.listCollections(param);

        System.out.println(response.getStatus());
        System.out.println(response.getData().collectionNames);
//        DropCollectionParam requestParam = DropCollectionParam.newBuilder()
//                .withCollectionName("example_collection")
//                .build();
//        customMilvusClient.dropCollection(requestParam);
        MilvusEmbeddingStore store = MilvusEmbeddingStore.builder()
                .milvusClient(milvusServiceClient)                            // Port for Milvus instance
                .collectionName("example_collection")      // Name of the collection
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
        Embedding embedding = embeddingModel.embed("狗是好吃的").content();
        store.add("狗是好吃的", embedding);
        Embedding queryEmbedding =
                embeddingModel.embed("你喜欢什么食物吗").content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(5)
                .minScore(0.75)
                .build();
        EmbeddingSearchResult<TextSegment> search = store.search(request);
        System.out.println(search.matches());

        // 1. Connect to Milvus server
//        ConnectConfig connectConfig = ConnectConfig.builder()
//                .uri("http://192.168.111.131:19530")
//                .build();
//
//        MilvusClientV2 client = new MilvusClientV2(connectConfig);
//        ListCollectionsResp resp = client.listCollections();
//        System.out.println(resp);
//        // 3. Create a collection in customized setup mode
//
//        // 3.1 Create schema
//        CreateCollectionReq.CollectionSchema schema = client.createSchema();
//
//        // 3.2 Add fields to schema
//        schema.addField(AddFieldReq.builder()
//                .fieldName("my_id")
//                .dataType(DataType.Int64)
//                .isPrimaryKey(true)
//                .autoID(false)
//                .build());
//
//        schema.addField(AddFieldReq.builder()
//                .fieldName("my_vector")
//                .dataType(DataType.FloatVector)
//                .dimension(5)
//                .build());
//
//        schema.addField(AddFieldReq.builder()
//                .fieldName("my_varchar")
//                .dataType(DataType.VarChar)
//                .maxLength(512)
//                .build());
//        // 3.3 Prepare index parameters
//        IndexParam indexParamForIdField = IndexParam.builder()
//                .fieldName("my_id")
//                .indexType(IndexParam.IndexType.AUTOINDEX)
//                .build();
//
//        IndexParam indexParamForVectorField = IndexParam.builder()
//                .fieldName("my_vector")
//                .indexType(IndexParam.IndexType.AUTOINDEX)
//                .metricType(IndexParam.MetricType.COSINE)
//                .build();
//
//        List<IndexParam> indexParams = new ArrayList<>();
//        indexParams.add(indexParamForIdField);
//        indexParams.add(indexParamForVectorField);
//        CreateCollectionReq customizedSetupReq1 = CreateCollectionReq.builder()
//                .collectionName("customized_setup_2")
//                .collectionSchema(schema)
//                .indexParams(indexParams)
//                .build();
//
//        client.createCollection(customizedSetupReq1);
//
//        // 3.5 Get load state of the collection
//        GetLoadStateReq customSetupLoadStateReq1 = GetLoadStateReq.builder()
//                .collectionName("customized_setup_2")
//                .build();
//
//        Boolean loaded = client.getLoadState(customSetupLoadStateReq1);
//        ListCollectionsResp resp = client.listCollections();
//        System.out.println(resp.getCollectionNames());
//        System.out.println(loaded);
    }
}