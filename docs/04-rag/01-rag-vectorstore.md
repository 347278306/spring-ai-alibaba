# 01 - RAG 向量数据库

本模块介绍如何使用 Spring AI 实现 RAG（Retrieval Augmented Generation，检索增强生成），结合向量数据库实现知识库问答。

## 知识点讲解

### 1.1 什么是 RAG？

RAG 是一种让 LLM 访问外部知识的技术：

```
┌─────────────────────────────────────────────────────────────┐
│                    RAG 架构                                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   用户问题 ──▶                                                │
│       │                                                      │
│       ▼                                                      │
│   ┌─────────────┐                                            │
│   │  检索阶段   │  在向量数据库中检索相关内容                   │
│   └──────┬──────┘                                            │
│          │                                                   │
│          ▼                                                   │
│   ┌─────────────┐                                            │
│   │  生成阶段   │  将检索内容作为上下文，让 LLM 生成答案        │
│   └──────┬──────┘                                            │
│          │                                                   │
│          ▼                                                   │
│   答案 + 引用来源                                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 RAG 工作流程

```
1. 文档预处理
   └── 文档 ──▶ 分块 ──▶ Embedding ──▶ 向量存储

2. 用户查询
   └── 用户问题 ──▶ Embedding ──▶ 向量检索

3. 增强生成
   └── 检索结果 + 用户问题 ──▶ LLM ──▶ 生成答案
```

### 1.3 Spring AI RAG 组件

| 组件                             | 说明      |
|--------------------------------|---------|
| `VectorStore`                  | 向量存储接口  |
| `DocumentRetriever`            | 文档检索器   |
| `RetrievalAugmentationAdvisor` | RAG 增强器 |
| `ChatClient`                   | 对话客户端   |

### 1.4 RAG vs 纯 LLM

| 方式    | 优点                | 缺点                  |
|-------|-------------------|---------------------|
| 纯 LLM | 简单直接              | 知识有限，可能 hallucinate |
| RAG   | 可访问最新/私有数据，需要更多组件 |

## 源码解析

### 2.1 RAG 控制器

```java
@RestController
public class RagController {

    @Resource(name = "qwenChatClient")
    private ChatClient chatClient;

    @Resource
    private VectorStore vectorStore;

    @GetMapping("/rag4aiops")
    public Flux<String> rag(@RequestParam("msg") String msg) {
        // 1. 定义系统提示词
        String systemPrompt = "你是一个运维工程师,按照给出的编码给出对应故障解释,否则回复找不到信息。";
        
        // 2. 构建 RAG 增强器
        RetrievalAugmentationAdvisor advisor = 
            RetrievalAugmentationAdvisor.builder()
                .documentRetriever(
                    VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build()
                )
                .build();

        // 3. 使用增强器进行对话
        return chatClient.prompt()
            .system(systemPrompt)
            .user(msg)
            .advisors(advisor)
            .stream()
            .content();
    }
}
```

### 2.2 关键组件

1. **VectorStore**：向量化存储，支持多种后端
2. **VectorStoreDocumentRetriever**：从 VectorStore 检索文档
3. **RetrievalAugmentationAdvisor**：将检索内容注入到 Prompt

### 2.3 配置（参考 Saa-12）

```properties
# Redis 向量库配置
spring.ai.vectorstore.redis.index=aiops-index
spring.ai.vectorstore.redis.dimensions=2048
```

## 课后练习

### 练习 1：准备知识库数据

向向量库添加文档：

```java
@PostMapping("/add")
public String add(@RequestBody List<String> documents) {
    List<Document> docs = documents.stream()
        .map(Document::new)
        .toList();
    vectorStore.add(docs);
    return "添加成功";
}
```

### 练习 2：查询知识库

先查看已存储的文档：

```java
@GetMapping("/list")
public List<Document> list() {
    SearchRequest request = SearchRequest.builder()
        .query("*")  // 查询所有
        .topK(10)
        .build();
    return vectorStore.similaritySearch(request);
}
```

### 练习 3：删除文档

删除不需要的文档：

```java
@DeleteMapping("/delete")
public String delete(@RequestParam String id) {
    vectorStore.delete(List.of(id));
    return "删除成功";
}
```

### 练习 4：调整检索参数

修改检索配置：

```java
RetrievalAugmentationAdvisor advisor = 
    RetrievalAugmentationAdvisor.builder()
        .documentRetriever(
            VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.8)  // 相似度阈值
                .topK(5)                    // 返回前5个
                .build()
        )
        .build();
```

### 练习 5：自定义文档分块

使用更好的分块策略：

```java
@Bean
public DocumentSplitter documentSplitter() {
    return new TokenTextSplitter()
        .withChunkSize(500)
        .withChunkOverlap(50);
}
```

## 下一步

- 回顾 [05 - 文本向量化](../03-advanced/05-embedding.md)：Embedding 基础
- 学习 [02 - 百炼 RAG](./02-bailian-rag.md)：阿里云百炼平台 RAG
