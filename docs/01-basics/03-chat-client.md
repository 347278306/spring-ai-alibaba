# 03 - ChatClient API

本模块介绍 Spring AI 的 ChatClient API，这是比 ChatModel 更高级的抽象，提供了链式调用和更丰富的功能。

## 知识点讲解

### 1.1 ChatClient 简介

ChatClient 是 Spring AI 1.0 引入的高级 API，提供了fluent 风格的链式调用：

```java
// 使用 ChatClient 的链式调用
chatClient.prompt()
    .

system("你是一个助手")
    .

user("你好")
    .

call()
    .

content();
```

### 1.2 ChatClient vs ChatModel

| 特性            | ChatModel    | ChatClient        |
|---------------|--------------|-------------------|
| API 风格        | 命令式          | Fluent 链式         |
| System Prompt | 需手动构建 Prompt | 内置 `.system()` 方法 |
| 多轮对话          | 需手动维护上下文     | 内置 ChatMemory 支持  |
| Advisor 扩展    | 需要手动配置       | 内置 advisors 支持    |

### 1.3 ChatClient 核心组件

```
ChatClient
├── Prompt            # 提示词构建
│   ├── system()      # 系统提示词
│   └── user()        # 用户提示词
├── Advisors          # 增强器
│   └── .advisors()   # 添加增强器
├── Tools             # 工具注册
│   └── .tools()      # 注册工具
└── Call/Stream       # 执行方式
    ├── .call()       # 同步调用
    └── .stream()     # 流式调用
```

### 1.4 创建 ChatClient

ChatClient 通过 Builder 模式创建：

```java
// 方式1：构造方法注入
public MyController(ChatModel chatModel) {
    this.chatClient = ChatClient.builder(chatModel).build();
}

// 方式2：自动配置的 ChatClient Bean
@RestController
public class MyController {
    @Resource
    private ChatClient chatClient;  // Spring AI 自动配置
}
```

## 源码解析

### 2.1 控制器源码

```java

@RestController
@RequestMapping("chatClient")
public class ChatClientController {

    private final ChatClient chatClient;

    // 构造方法注入 ChatModel，手动构建 ChatClient
    public ChatClientController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @GetMapping("call")
    public String call(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg) {
        return chatClient.prompt()
                .user(msg)
                .call()
                .content();
    }
}
```

### 2.2 ChatClient 构建流程

```
ChatClient.builder(chatModel)
    ├── .defaultSystem(String)       // 默认系统提示词
    ├── .defaultOptions(ChatOptions) // 默认选项
    ├── .defaultAdvisors(Advisor...) // 默认增强器
    └── .build()                     // 构建实例
```

### 2.3 ChatClient API 详解

```java
// 1. 基本调用
chatClient.prompt()
    .

user("问题")
    .

call()
    .

content();                      // 返回 String

// 2. 带系统提示词
chatClient.

prompt()
    .

system("你是一个诗人")
    .

user("写一首关于春天的诗")
    .

call()
    .

content();

// 3. 流式调用
chatClient.

prompt()
    .

user("给我讲一个故事")
    .

stream()
    .

content();                      // 返回 Flux<String>

// 4. 获取完整响应
ChatResponse response = chatClient.prompt()
        .user("问题")
        .call()
        .chatResponse();                 // 返回 ChatResponse
```

## 课后练习

### 练习 1：使用 V2 版本的 ChatClient

项目中存在 `ChatClientV2Controller`，使用自动配置的 ChatClient Bean：

```java

@RestController
@RequestMapping("chatClient/v2")
public class ChatClientV2Controller {

    @Resource
    private ChatClient chatClient;  // 直接注入自动配置的 Bean

    @GetMapping("call")
    public String call(@RequestParam(name = "msg") String msg) {
        return chatClient.prompt().user(msg).call().content();
    }
}
```

**思考**：手动构建 vs 自动注入，哪个更灵活？

### 练习 2：添加系统提示词

修改控制器，添加系统提示词：

```java

@GetMapping("call")
public String call(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg) {
    return chatClient.prompt()
            .system("你是一个专业的AI助手，擅长用简洁清晰的语言回答问题。")
            .user(msg)
            .call()
            .content();
}
```

### 练习 3：流式响应

修改接口实现流式输出：

```java

@GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> stream(@RequestParam(name = "msg", defaultValue = "讲一个故事") String msg) {
    return chatClient.prompt()
            .user(msg)
            .stream()
            .content();
}
```

### 练习 4：构建多轮对话

模拟多轮对话场景：

```java

@GetMapping("multi")
public String multi() {
    // 第一轮
    String response1 = chatClient.prompt()
            .user("我想学习Java")
            .call()
            .content();

    // 第二轮（携带上下文）
    String response2 = chatClient.prompt()
            .user("有没有推荐的书籍？")
            .call()
            .content();

    return "Q1: 我想学习Java\nA1: " + response1 +
            "\nQ2: 有没有推荐的书籍？\nA2: " + response2;
}
```

### 练习 5：自定义 ChatOptions

为 ChatClient 设置默认选项：

```java
ChatClient customClient = ChatClient.builder(chatModel)
        .defaultOptions(ChatOptions.builder()
                .temperature(0.9)
                .maxTokens(2000)
                .build())
        .build();
```

## 下一步

- 回顾 [01-ChatModel 基础聊天](./01-chat-model.md)：了解底层 ChatModel
- 回顾 [02-Ollama 本地模型](./02-ollama.md)：了解多模型配置
- 学习 [Saa-05 - Prompt 基础](../02-prompt/01-prompt-basics.md)：深入 Prompt 使用
