package com.example.aicodehelper.ai;

import dev.langchain4j.data.document.Metadata;
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
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.R;
import io.milvus.param.highlevel.collection.ListCollectionsParam;
import io.milvus.param.highlevel.collection.response.ListCollectionsResponse;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

@SpringBootTest
class AiCodeHelperServiceTest {

    @Resource
    private AiCodeHelperService aiCodeHelperService;
    @Resource
    private EmbeddingModel embeddingModel;
    @Resource
    private MilvusServiceClient milvusServiceClient;
    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

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
//        MilvusEmbeddingStore store = MilvusEmbeddingStore.builder()
//                .milvusClient(milvusServiceClient)                            // Port for Milvus instance
//                .collectionName("example_collection")      // Name of the collection
//                .dimension(1024)                            // Dimension of vectors
//                .indexType(IndexType.FLAT)                 // Index type
//                .metricType(MetricType.COSINE)             // Metric type
//                .username("username")                      // Username for Milvus
//                .password("password")                      // Password for Milvus
//                .consistencyLevel(ConsistencyLevelEnum.EVENTUALLY)  // Consistency level
//                .autoFlushOnInsert(true)                   // Auto flush after insert
//                .idFieldName("id")                         // ID field name
//                .textFieldName("text")                     // Text field name
//                .metadataFieldName("metadata")             // Metadata field name
//                .vectorFieldName("vector")                 // Vector field name
//                .build();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("title", "Java教程");
        metadata.put("userId", 123);
        metadata.put("category", "tech");
        Embedding embedding = embeddingModel.embed(TextSegment.from("Milvus 是专门用来存“AI 向量”的数据库", new Metadata(metadata))).content();
        embeddingStore.add(embedding, TextSegment.from("Milvus 是专门用来存“AI 向量”的数据库", new Metadata(metadata)));
        Embedding queryEmbedding =
                embeddingModel.embed("milvus 是什么").content();
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(5)
                .minScore(0.75)
                .build();
        EmbeddingSearchResult<TextSegment> search = embeddingStore.search(request);
        System.out.println(search.matches());
        Result<String> result = aiCodeHelperService.chatWithRag("milvus 是什么");
        System.out.println(result.content());

        // 1. Connect to Milvus server
//        ConnectConfig connectConfig = ConnectConfig.builder()
//                .uri("http://192.168.111.131:19530")
//                .build();
//
//        MilvusClientV2 client = new MilvusClientV2(connectConfig);
//        // 3. Create a collection in customized setup mode
//
//        // 3.1 Create schema
//        CreateCollectionReq.CollectionSchema schema = client.createSchema();
//
//        // 3.2 Add fields to schema
//        schema.addField(AddFieldReq.builder()
//                .fieldName("id")
//                .dataType(DataType.VarChar)
//                .isPrimaryKey(true)
//                .maxLength(36)
//                .build());
//
//        schema.addField(AddFieldReq.builder()
//                .fieldName("vector")
//                .dataType(DataType.FloatVector)
//                .dimension(1024)
//                .build());
//
//        schema.addField(AddFieldReq.builder()
//                .fieldName("text")
//                .dataType(DataType.VarChar)
//                .maxLength(512)
//                .build());
//        schema.addField(AddFieldReq.builder()
//                .fieldName("metadata")
//                .dataType(DataType.JSON)
//                .build());
////         3.3 Prepare index parameters
////        IndexParam indexParamForIdField = IndexParam.builder()
////                .fieldName("id")
////                .indexType(IndexParam.IndexType.AUTOINDEX)
////                .build();
//
//        IndexParam indexParamForVectorField = IndexParam.builder()
//                .fieldName("vector")
//                .indexType(IndexParam.IndexType.AUTOINDEX)
//                .metricType(IndexParam.MetricType.COSINE)
//                .build();
//
//        List<IndexParam> indexParams = new ArrayList<>();
////        indexParams.add(indexParamForIdField);
//        indexParams.add(indexParamForVectorField);
//        CreateCollectionReq customizedSetupReq1 = CreateCollectionReq.builder()
//                .collectionName("my_db")
//                .collectionSchema(schema)
//                .indexParams(indexParams)
//                .build();
//
//        client.createCollection(customizedSetupReq1);
//
//        // 3.5 Get load state of the collection
//        GetLoadStateReq customSetupLoadStateReq1 = GetLoadStateReq.builder()
//                .collectionName("my_db")
//                .build();
//
//        Boolean loaded = client.getLoadState(customSetupLoadStateReq1);
//        ListCollectionsResp resp = client.listCollections();
//        System.out.println(resp.getCollectionNames());
//        System.out.println(loaded);
//
//        Map<String, Object> data = new HashMap<>();
//        Map<String, Object> metadata = new HashMap<>();
//        metadata.put("title", "Java教程");
//        metadata.put("userId", 123);
//        metadata.put("category", "tech");
//        data.put("text", "跑步锻炼身体");
//        data.put("vector", embeddingModel.embed(TextSegment.from("跑步锻炼身体", new Metadata(metadata))).content().vector());
//        data.put("id", UUID.randomUUID());
//        data.put("metadata", metadata);
//
//        Gson gson = new Gson();
//        JsonObject jsonObject = gson.toJsonTree(data).getAsJsonObject();
//        InsertReq insertReq = InsertReq.builder()
//                .collectionName("my_db")
//                .data(List.of(jsonObject))
//                .build();
//        InsertResp insertResp = client.insert(insertReq);
//        System.out.println(insertResp);
//        QueryReq queryReq = QueryReq.builder()
//                .collectionName("my_db")
//                .filter("id in ['1','2','3','4']")
//                .outputFields(List.of("id", "text"))
//                .build();
//
//        QueryResp queryResp = client.query(queryReq);
//        System.out.println(queryResp.getQueryResults().size());
//
//        SearchReq searchReq = SearchReq.builder()
//                .collectionName("my_db")
//                .data(List.of(new FloatVec(embeddingModel.embed(TextSegment.from("你爱什么运动")).content().vector())))
//                .topK(5)
//                .outputFields(List.of("text", "metadata", "id"))
//                .build();
//        SearchResp searchResp = client.search(searchReq);
//        System.out.println(searchResp);
    }
}