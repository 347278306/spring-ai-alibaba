# 02 - 百炼 RAG

本模块介绍如何使用阿里云百炼平台的 RAG 能力，实现更强大的企业知识库问答。

## 知识点讲解

### 1.1 什么是阿里云百炼？

阿里云百炼是一站式大模型应用开发平台，提供：

- **模型服务**：通义千问等模型 API
- **RAG 平台**：一键构建知识库
- **Agent 开发**：构建智能体
- **应用编排**：可视化流程编排

### 1.2 百炼 RAG vs 自建 RAG

| 特性   | 自建 RAG    | 百炼 RAG |
|------|-----------|--------|
| 部署难度 | 需要自行部署向量库 | 一键创建   |
| 规模   | 受限于本地资源   | 弹性扩展   |
| 维护   | 需要维护基础设施  | 托管服务   |
| 检索效果 | 需要调优      | 内置优化   |

### 1.3 百炼文档检索 API

```java
DashScopeDocumentRetriever retriever = new DashScopeDocumentRetriever(
    dashScopeApi,
    DashScopeDocumentRetrieverOptions.builder()
        .indexName("ops")  // 知识库名称
        .build()
);
```

## 源码解析

### 2.1 百炼 RAG 控制器

```java
@RestController
public class BailianRagController {

    @Resource
    private ChatClient chatClient;

    @Resource
    private DashScopeApi dashScopeApi;

    @GetMapping("/bailian/rag/chat")
    public Flux<String> chat(@RequestParam("msg") String msg) {
        // 1. 创建百炼文档检索器
        DashScopeDocumentRetriever retriever = new DashScopeDocumentRetriever(
            dashScopeApi,
            DashScopeDocumentRetrieverOptions.builder()
                .indexName("ops")  // 知识库名称
                .build()
        );

        // 2. 创建文档检索增强器
        DocumentRetrievalAdvisor advisor = new DocumentRetrievalAdvisor(retriever);

        // 3. 使用增强器进行对话
        return chatClient.prompt()
            .user(msg)
            .advisors(advisor)
            .stream()
            .content();
    }
}
```

### 2.2 关键差异

| 组件  | 自建 RAG                         | 百炼 RAG                       |
|-----|--------------------------------|------------------------------|
| 检索器 | `VectorStoreDocumentRetriever` | `DashScopeDocumentRetriever` |
| 存储  | 自建向量数据库                        | 百炼知识库                        |
| 配置  | 配置 VectorStore                 | 配置 indexName                 |

### 2.3 百炼 RAG 优势

1. **无需自建向量库**：阿里云托管
2. **智能检索优化**：内置重排序等优化
3. **支持多种数据源**：PDF、Word、网页等
4. **企业级安全**：数据隔离、权限控制

## 课后练习

### 练习 1：创建百炼知识库

1. 登录阿里云百炼控制台
2. 创建知识库（上传文档）
3. 获取知识库 ID

### 练习 2：使用不同检索参数

```java
DashScopeDocumentRetriever retriever = new DashScopeDocumentRetriever(
    dashScopeApi,
    DashScopeDocumentRetrieverOptions.builder()
        .indexName("your-index")
        .topK(5)               // 返回数量
        .scoreThreshold(0.8f) // 相似度阈值
        .build()
);
```

### 练习 3：自定义系统提示词

```java
@GetMapping("/bailian/rag/chat")
public Flux<String> chat(@RequestParam("msg") String msg) {
    DashScopeDocumentRetriever retriever = new DashScopeDocumentRetriever(
        dashScopeApi,
        DashScopeDocumentRetrieverOptions.builder()
            .indexName("ops")
            .build()
    );

    DocumentRetrievalAdvisor advisor = new DocumentRetrievalAdvisor(retriever);

    return chatClient.prompt()
        .system("你是一个专业的技术支持工程师，根据知识库中的文档回答用户问题。")
        .user(msg)
        .advisors(advisor)
        .stream()
        .content();
}
```

### 练习 4：混合检索

结合百炼 RAG 和本地知识：

```java
// 百炼知识库检索器
DashScopeDocumentRetriever bailianRetriever = new DashScopeDocumentRetriever(...);

// 本地知识库检索器
VectorStoreDocumentRetriever localRetriever = VectorStoreDocumentRetriever.builder()
    .vectorStore(vectorStore)
    .build();

// 组合多个检索器
List<DocumentRetriever> retrievers = List.of(bailianRetriever, localRetriever);
```

### 练习 5：处理检索结果

获取检索到的文档信息：

```java
@GetMapping("/rag/debug")
public Map<String, Object> debug(@RequestParam("msg") String msg) {
    DashScopeDocumentRetriever retriever = new DashScopeDocumentRetriever(
        dashScopeApi,
        DashScopeDocumentRetrieverOptions.builder()
            .indexName("ops")
            .build()
    );

    // 直接调用检索
    List<Document> docs = retriever.retrieve(msg);

    return Map.of(
        "query", msg,
        "docCount", docs.size(),
        "documents", docs.stream()
            .map(Document::getText)
            .toList()
    );
}
```

## 下一步

- 回顾 [01 - RAG 向量数据库](./01-rag-vectorstore.md)：自建 RAG 实现
- 学习 [13 - 工具调用](../05-tools/01-tool-calling.md)：让 LLM 调用外部工具
