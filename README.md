# Spring AI Alibaba

Spring AI Alibaba 项目演示了 Spring AI 框架与阿里云 AI 服务的集成，包含 18 个示例模块，涵盖从基础聊天到高级 Agent 的各种功能。

## 环境要求

- Java 17+
- Maven 3.8+

## 快速开始

```bash
# 克隆项目后，构建整个项目
mvn clean install

# 运行单个模块
cd Saa-01 && mvn spring-boot:run
```

## 模块说明

| 模块         | 功能             | 描述                                  |
|------------|----------------|-------------------------------------|
| **Saa-01** | 基础聊天           | 使用阿里云 DashScope (Qwen) 进行基本对话和流式对话  |
| **Saa-02** | Ollama 本地 LLM  | 集成 Ollama，支持本地部署的大语言模型              |
| **Saa-03** | ChatClient API | 使用 Spring AI 的 ChatClient API 进行对话  |
| **Saa-04** | 流式输出           | 实现 SSE 流式响应，边说边输出                   |
| **Saa-05** | Prompt 基础      | Prompt 的基本使用方式                      |
| **Saa-06** | Prompt 模板      | 使用 PromptTemplate 构建动态提示词           |
| **Saa-07** | 结构化输出          | 让 LLM 输出结构化数据（JSON、Java Record 等）   |
| **Saa-08** | Redis 记忆存储     | 使用 Redis 存储和管理对话历史上下文               |
| **Saa-09** | 文生图            | 使用 DashScope 的图像生成能力                |
| **Saa-10** | 文生语音           | 使用 DashScope 的语音合成能力（TTS）           |
| **Saa-11** | 文本向量化          | 将文本转换为向量（Embedding）                 |
| **Saa-12** | RAG 知识库        | 结合向量数据库实现检索增强生成                     |
| **Saa-13** | 工具调用           | LLM 调用外部工具（Function Calling）        |
| **Saa-14** | MCP Server     | 实现 MCP (Model Context Protocol) 服务端 |
| **Saa-15** | MCP Client     | 客户端调用外部 MCP 服务                      |
| **Saa-16** | MCP 百度工具       | 集成调用百度 MCP 工具                       |
| **Saa-17** | 百炼 RAG         | 使用阿里云百炼平台的 RAG 能力                   |
| **Saa-18** | Agent 调用       | 使用阿里云 DashScope Agent（如智能点餐）        |

## 技术栈

- **Spring Boot**: 3.5.5
- **Spring AI**: 1.1.2
- **Spring AI Alibaba**: 1.1.2.0
- **阿里云 DashScope**: 通义千问 / 图像生成 / 语音合成
- **Ollama**: 本地 LLM 运行时
- **Redis**: 对话记忆存储
- **向量数据库**: 支持多种向量存储

## 配置说明

大部分模块需要在 `application.properties` 或 `application.yml` 中配置阿里云 DashScope API Key：

```properties
spring.ai.dashscope.api-key=your-api-key
```

## License

MIT
