# 05 - 文本向量化

本模块介绍文本向量化的概念，以及如何使用 Spring AI 实现文本到向量的转换（Embedding），这是 RAG 和语义搜索的基础。

## 知识点讲解

### 1.1 什么是 Embedding？

Embedding（嵌入）是将文本转换为数值向量的过程，使语义相似的文本在向量空间中距离更近：

```
┌─────────────────────────────────────────────────────────────┐
│                    Embedding 概念                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  文本: "我爱Java编程"     ──────▶  [0.12, -0.34, 0.56, ...] │
│  文本: "Java是编程语言"   ──────▶  [0.11, -0.31, 0.58, ...] │
│  文本: "今天天气真好"     ──────▶  [-0.89, 0.45, 0.12, ...] │
│                                                             │
│  距离近 → 语义相似                                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 向量检索场景

- **语义搜索**：找到意思相近的内容
- **RAG**：检索增强生成的知识库
- **去重**：检测相似文本
- **分类**：基于相似度的分类

### 1.3 DashScope Embedding 模型

| 模型                  | 说明   | 维度   |
|---------------------|------|------|
| `text-embedding-v4` | 最新版本 | 2048 |

### 1.4 Spring AI Embedding API

```java
// 单文本向量化
EmbeddingResponse response = embeddingModel.call(
    new EmbeddingRequest(List.of("文本"), options)
);

// 获取向量
List<Double> embedding = response.getResults().get(0).getEmbedding();
```

## 源码解析

### 2.1 控制器源码

```java
@RestController
public class Embed2VectorController {

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private VectorStore vectorStore;

    // 文本转向量
    @GetMapping("/text2Embed")
    public EmbeddingResponse text2Embed(@RequestParam("text") String text) {
        EmbeddingResponse response = embeddingModel.call(
            new EmbeddingRequest(
                List.of(text),
                DashScopeEmbeddingOptions.builder()
                    .model("text-embedding-v4")
                    .build()
            )
        );
        return response;
    }

    // 添加文档到向量库
    @GetMapping("/embed2vector/add")
    public void add() {
        List<Document> documents = List.of(
            new Document("i love java"),
            new Document("i love python")
        );
        vectorStore.add(documents);
    }

    // 相似度搜索
    @GetMapping("/embed2vector/get")
    public List<Document> getAll(@RequestParam("msg") String msg) {
        SearchRequest searchRequest = SearchRequest.builder()
            .query(msg)
            .topK(2)
            .build();

        return vectorStore.similaritySearch(searchRequest);
    }
}
```

### 2.2 向量存储流程

```
┌─────────────────────────────────────────────────────────────┐
│                    向量存储与检索流程                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  添加文档:                                                   │
│  Document("i love java")  ──▶  Embedding  ──▶  VectorStore │
│                                                             │
│  搜索:                                                      │
│  "python"  ──▶  Embedding  ──▶  VectorStore 相似度搜索     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.3 VectorStore 简介

VectorStore 是向量存储接口，支持多种后端：

| 实现                    | 说明            |
|-----------------------|---------------|
| `RedisVectorStore`    | 使用 Redis 存储向量 |
| `ChromaVectorStore`   | Chroma 向量数据库  |
| `MilvusVectorStore`   | Milvus 向量数据库  |
| `PineconeVectorStore` | Pinecone 云服务  |

## 课后练习

### 练习 1：查看向量维度

打印向量的维度：

```java
@GetMapping("/dimension")
public int dimension(@RequestParam String text) {
    EmbeddingResponse response = embeddingModel.call(
        new EmbeddingRequest(List.of(text), null)
    );
    return response.getResults().get(0).getEmbedding().size();
}
```

### 练习 2：批量向量化

将多个文本同时向量化：

```java
@GetMapping("/batch")
public EmbeddingResponse batch(@RequestParam List<String> texts) {
    return embeddingModel.call(
        new EmbeddingRequest(texts, null)
    );
}
```

### 练习 3：手动计算相似度

计算两个文本的余弦相似度：

```java
@GetMapping("/similarity")
public double similarity(@RequestParam String text1, @RequestParam String text2) {
    EmbeddingResponse r1 = embeddingModel.call(new EmbeddingRequest(List.of(text1), null));
    EmbeddingResponse r2 = embeddingModel.call(new EmbeddingRequest(List.of(text2), null));
    
    List<Double> v1 = r1.getResults().get(0).getEmbedding();
    List<Double> v2 = r2.getResults().get(0).getEmbedding();
    
    // 余弦相似度
    double dotProduct = 0;
    double norm1 = 0;
    double norm2 = 0;
    for (int i = 0; i < v1.size(); i++) {
        dotProduct += v1.get(i) * v2.get(i);
        norm1 += v1.get(i) * v1.get(i);
        norm2 += v2.get(i) * v2.get(i);
    }
    return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
}
```

### 练习 4：使用 VectorStore 存储

使用 VectorStore 进行向量检索：

```java
@GetMapping("/search")
public List<Document> search(@RequestParam String query) {
    SearchRequest request = SearchRequest.builder()
        .query(query)
        .topK(5)  // 返回前5个最相似结果
        .build();
    
    return vectorStore.similaritySearch(request);
}
```

### 练习 5：构建问答系统

结合 Embedding 和 LLM 构建简单问答系统：

```java
@GetMapping("/qa")
public String qa(@RequestParam String question) {
    // 1. 检索相关文档
    SearchRequest searchRequest = SearchRequest.builder()
        .query(question)
        .topK(3)
        .build();
    List<Document> docs = vectorStore.similaritySearch(searchRequest);
    
    // 2. 构建上下文
    String context = docs.stream()
        .map(Document::getText)
        .collect(Collectors.joining("\n"));
    
    // 3. 让 LLM 基于上下文回答
    return chatClient.prompt()
        .system("基于以下资料回答问题：" + context)
        .user(question)
        .call()
        .content();
}
```

## 下一步

- 学习 [12 - RAG 知识库](../04-rag/01-rag-vectorstore.md)：完整的 RAG 实现
- 学习 [17 - 百炼 RAG](../04-rag/02-bailian-rag.md)：阿里云百炼平台 RAG
