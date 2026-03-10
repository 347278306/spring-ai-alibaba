# Spring AI Alibaba 学习文档

本学习文档基于 Spring AI Alibaba 项目，系统讲解 Spring AI 框架与阿里云 AI 服务的集成。从基础聊天到高级 Agent，覆盖 Spring
AI 的核心知识点。

## 学习路径

建议按以下顺序学习，效果最佳：

```
基础入门 (01-03) 
    ↓
Prompt 工程 (05-07) 
    ↓
高级特性 (04, 08-11) 
    ↓
RAG 知识库 (12, 17) 
    ↓
工具调用 (13-16) 
    ↓
Agent (18)
```

> **说明**：Saa-04（流式输出）可与基础入门并行学习

## 目录结构

### 01 - 基础入门篇

| 模块                                      | 主题             | 描述                                 |
|-----------------------------------------|----------------|------------------------------------|
| [Saa-01](./01-basics/01-chat-model.md)  | ChatModel 基础聊天 | 使用阿里云 DashScope 进行基本对话和流式对话        |
| [Saa-02](./01-basics/02-ollama.md)      | Ollama 本地模型    | 集成 Ollama，支持本地部署的大语言模型             |
| [Saa-03](./01-basics/03-chat-client.md) | ChatClient API | 使用 Spring AI 的 ChatClient API 进行对话 |

**核心知识点**：ChatModel、ChatClient、ChatOptions、流式响应

### 02 - Prompt 工程篇

| 模块                                            | 主题        | 描述                              |
|-----------------------------------------------|-----------|---------------------------------|
| [Saa-05](./02-prompt/01-prompt-basics.md)     | Prompt 基础 | Prompt 的基本使用方式                  |
| [Saa-06](./02-prompt/02-prompt-template.md)   | Prompt 模板 | 使用 PromptTemplate 构建动态提示词       |
| [Saa-07](./02-prompt/03-structured-output.md) | 结构化输出     | 让 LLM 输出结构化数据（JSON、Java Record） |

**核心知识点**：Prompt、PromptTemplate、SystemPrompt、UserPrompt、结构化输出

### 03 - 高级特性篇

| 模块                                         | 主题         | 描述                        |
|--------------------------------------------|------------|---------------------------|
| [Saa-04](./03-advanced/01-streaming.md)    | 流式输出       | 实现 SSE 流式响应，边说边输出         |
| [Saa-08](./03-advanced/02-memory-redis.md) | Redis 记忆存储 | 使用 Redis 存储和管理对话历史上下文     |
| [Saa-09](./03-advanced/03-text2image.md)   | 文生图        | 使用 DashScope 的图像生成能力      |
| [Saa-10](./03-advanced/04-text2voice.md)   | 文生语音       | 使用 DashScope 的语音合成能力（TTS） |
| [Saa-11](./03-advanced/05-embedding.md)    | 文本向量化      | 将文本转换为向量（Embedding）       |

**核心知识点**：Flux/Server-Sent Events、ChatMemory、VectorStore、Embedding

### 04 - RAG 知识库篇

| 模块                                       | 主题        | 描述                |
|------------------------------------------|-----------|-------------------|
| [Saa-12](./04-rag/01-rag-vectorstore.md) | RAG 向量数据库 | 结合向量数据库实现检索增强生成   |
| [Saa-17](./04-rag/02-bailian-rag.md)     | 百炼 RAG    | 使用阿里云百炼平台的 RAG 能力 |

**核心知识点**：RAG、VectorStore、DocumentRetriever、RetrievalAugmentationAdvisor

### 05 - 工具调用篇

| 模块                                      | 主题         | 描述                                  |
|-----------------------------------------|------------|-------------------------------------|
| [Saa-13](./05-tools/01-tool-calling.md) | 工具调用       | LLM 调用外部工具（Function Calling）        |
| [Saa-14](./05-tools/02-mcp-server.md)   | MCP Server | 实现 MCP (Model Context Protocol) 服务端 |
| [Saa-15](./05-tools/03-mcp-client.md)   | MCP Client | 客户端调用外部 MCP 服务                      |
| [Saa-16](./05-tools/04-mcp-baidu.md)    | MCP 百度工具   | 集成调用百度 MCP 工具                       |

**核心知识点**：@Tool、ToolCallback、MCP 协议、Function Calling

### 06 - Agent 篇

| 模块                                         | 主题              | 描述                           |
|--------------------------------------------|-----------------|------------------------------|
| [Saa-18](./06-agent/01-dashscope-agent.md) | DashScope Agent | 使用阿里云 DashScope Agent（如智能点餐） |

**核心知识点**：Agent、ToolUse、ReAct、AppID

## 课前准备

### 1. 环境要求

- Java 17+
- Maven 3.8+
- Git

### 2. 阿里云 DashScope API Key

访问 [阿里云 DashScope](https://dashscope.console.aliyun.com/) 注册并获取 API Key。

配置方式：

```properties
# application.properties
spring.ai.dashscope.api-key=${DASHSCOPE_API_KEY}
```

或在运行时设置环境变量：

```bash
export DASHSCOPE_API_KEY=your-api-key
```

### 3. 可选依赖

- **Ollama**（Saa-02）：本地运行 LLM
- **Redis**（Saa-08）：对话记忆存储
- **向量数据库**（Saa-12）：如 Milvus、Elasticsearch

## 每章节结构

每个模块的文档包含以下部分：

1. **知识点讲解** - 概念、原理、使用场景
2. **源码解析** - 关键代码分析、流程图
3. **课后练习** - 动手实践题目

## 相关资源

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Spring AI Alibaba 文档](https://spring-ai.alibaba.com/)
- [阿里云 DashScope](https://dashscope.aliyun.com/)
- [通义千问 Qwen](https://tongyi.aliyun.com/)

---

**开始学习**：推荐从 [01-ChatModel 基础聊天](./01-basics/01-chat-model.md) 开始
