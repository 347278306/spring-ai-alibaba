# 01 - ChatModel 基础聊天

本模块介绍 Spring AI 的核心组件 ChatModel，学习如何通过阿里云 DashScope（通义千问）进行基础对话和流式对话。

## 知识点讲解

### 1.1 Spring AI 架构概述

Spring AI 是 Spring 生态系统中用于构建 AI 应用的框架，提供了统一的 API 来抽象不同的 AI 服务提供商。

```
┌─────────────────────────────────────────────────────────────┐
│                      Spring AI                              │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │  ChatModel  │  │   ChatClient │  │   PromptTemplate    │ │
│  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘ │
│         │                 │                      │             │
│  ┌──────┴─────────────────┴──────────────────────┴──────────┐ │
│  │                    AI Model Providers                      │ │
│  ├──────────┬──────────┬──────────┬──────────┬─────────────┤ │
│  │ DashScope │  OpenAI  │  Ollama  │  Azure   │   ...       │ │
│  │ (Qwen)    │          │          │         │             │ │
│  └──────────┴──────────┴──────────┴──────────┴─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 ChatModel 核心概念

**ChatModel** 是 Spring AI 的核心接口，定义了大语言模型交互的基础能力：

| 方法               | 返回类型                 | 说明          |
|------------------|----------------------|-------------|
| `call(Prompt)`   | `ChatResponse`       | 同步调用，返回完整响应 |
| `call(String)`   | `String`             | 简化版同步调用     |
| `stream(Prompt)` | `Flux<ChatResponse>` | 流式调用        |
| `stream(String)` | `Flux<String>`       | 简化版流式调用     |

### 1.3 DashScope 阿里云大模型服务

DashScope 是阿里云提供的 AI 模型服务，核心模型包括：

- **通义千问 (Qwen)**：文本对话
    - `qwen-plus`：Plus 版本，综合能力强
    - `qwen-turbo`：Turbo 版本，响应速度快
    - `qwen-max`：Max 版本，最强能力
- **通义万相**：图像生成
- **通义语音**：语音合成

### 1.4 配置项说明

```properties
# 必需：API Key
spring.ai.dashscope.api-key=${DASHSCOPE_API_KEY}
# 可选：自定义端点（用于代理或特殊配置）
spring.ai.dashscope.agent.base-url=https://dashscope.aliyuncs.com/compatible-mode/v1
# 可选：指定模型（默认由供应商决定）
spring.ai.dashscope.chat.options.model=qwen-plus
```

## 源码解析

### 2.1 项目结构

```
Saa-01/
├── src/main/java/com/hao/saa01/
│   ├── Saa01Application.java          # Spring Boot 启动类
│   ├── config/SaaLLMConfig.java       # LLM 配置类
│   └── controller/ChatController.java # 聊天控制器
├── src/main/resources/
│   └── application.properties          # 配置文件
└── pom.xml                            # 依赖配置
```

### 2.2 配置类源码解析

**SaaLLMConfig.java** - 配置 DashScope API Bean：

```java

@Configuration
public class SaaLLMConfig {

    @Bean
    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .build();
    }
}
```

**关键点解析**：

1. **@Configuration**：标识这是一个配置类，Spring 会自动扫描并加载
2. **DashScopeApi**：阿里云 DashScope 的 API 客户端，Spring AI 自动使用它创建 ChatModel
3. **API Key 来源**：推荐使用环境变量 `${DASHSCOPE_API_KEY}`，避免硬编码

> **自动配置原理**：引入 `spring-ai-alibaba-starter-dashscope` 后，Spring Boot 会自动检测 `DashScopeApi` Bean，并自动配置
`ChatModel`

### 2.3 控制器源码解析

**ChatController.java** - 对话接口：

```java

@RestController
public class ChatController {

    @Resource
    private ChatModel chatModel;

    // 同步对话
    @GetMapping(value = "/chat")
    public String chat(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg) {
        return chatModel.call(msg);
    }

    // 流式对话
    @GetMapping(value = "/streamChat")
    public Flux<String> streamChat(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg) {
        return chatModel.stream(msg);
    }
}
```

**关键点解析**：

1. **ChatModel 注入**：通过 `@Resource` 或 `@Autowired` 注入，Spring AI 自动配置
2. **同步调用 `call()`**：等待完整响应后返回，适合简短问答
3. **流式调用 `stream()`**：返回 `Flux<String>`，实现 Server-Sent Events (SSE)，适合长文本生成

### 2.4 调用流程图

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│   Browser    │ ──▶ │  ChatController  │ ──▶ │  ChatModel   │
└──────────────┘     └──────────────────┘     └──────┬───────┘
                                                      │
                                                      ▼
                                             ┌───────────────┐
                                             │  DashScopeApi │
                                             └───────┬───────┘
                                                     │
                                                     ▼
                                            ┌────────────────┐
                                            │  通义千问 Qwen  │
                                            └────────────────┘
```

## 课后练习

### 练习 1：修改默认模型

修改 `application.properties`，将模型从 `deepseek-v3` 改为 `qwen-plus`，观察响应差异。

```properties
spring.ai.dashscope.chat.options.model=qwen-plus
```

**思考**：不同模型的特点是什么？如何选择合适的模型？

### 练习 2：添加更多配置选项

尝试添加以下配置并观察效果：

```properties
# 设置温度参数（控制随机性，0-2 之间）
spring.ai.dashscope.chat.options.temperature=0.7
# 设置最大 token 数
spring.ai.dashscope.chat.options.max-tokens=1000
```

**思考**：`temperature` 参数对输出有什么影响？

### 练习 3：使用 ChatResponse

修改 `/chat` 接口，返回完整的 `ChatResponse` 对象而非字符串，打印响应内容：

```java

@GetMapping(value = "/chat")
public String chat(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg) {
    ChatResponse response = chatModel.call(new Prompt(msg));
    return response.getResult().getOutput().getText();
}
```

**思考**：`ChatResponse` 包含哪些信息？

### 练习 4：添加日志

为控制器添加日志，记录请求和响应：

```java

@RestController
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Resource
    private ChatModel chatModel;

    @GetMapping(value = "/chat")
    public String chat(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg) {
        log.info("收到用户消息: {}", msg);
        String response = chatModel.call(msg);
        log.info("模型响应: {}", response);
        return response;
    }
}
```

## 下一步

- 学习 [Saa-02 - Ollama 本地模型](../01-basics/02-ollama.md)：了解如何在本地运行 LLM
- 学习 [Saa-03 - ChatClient API](../01-basics/03-chat-client.md)：了解更高级的对话 API
