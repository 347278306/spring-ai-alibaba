package com.hao.saa11.controller;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Embed2VectorController {

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private VectorStore vectorStore;

    @GetMapping("/text2Embed")
    public EmbeddingResponse text2Embed(@RequestParam("text") String text) {
//        EmbeddingResponse embeddingResponse = embeddingModel.call(new EmbeddingRequest(List.of(text), null));

        EmbeddingResponse embeddingResponse = embeddingModel.call(new EmbeddingRequest(List.of(text),
                DashScopeEmbeddingOptions.builder().model("text-embedding-v4").build()));

        return embeddingResponse;
    }

    /**
     * 文本向量化 后存入向量数据库RedisStack
     */
    @GetMapping("/embed2vector/add")
    public void add() {
        List<Document> documents = List.of(
                new Document("i love java"),
                new Document("i love python")
        );
        vectorStore.add(documents);
    }

    /**
     * 从向量数据库RedisStack查找，进行相似度查找
     * http://localhost:8011/embed2vector/get?msg=python
     *
     * @param msg
     * @return
     */
    @GetMapping("/embed2vector/get")
    public List<Document> getAll(@RequestParam(name = "msg") String msg) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(msg)
                .topK(2)
                .build();

        return vectorStore.similaritySearch(searchRequest);
    }
}
