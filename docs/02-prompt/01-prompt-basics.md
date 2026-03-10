# 01 - Prompt 基础

本模块介绍 Prompt 的基本概念和 Spring AI 中 Prompt 的使用方法。

## 知识点讲解

### 1.1 Prompt 概念

Prompt（提示词）是与 LLM 交互的核心，良好的 Prompt 是获得理想输出的关键。

```
┌─────────────────────────────────────────────────────────────┐
│                        Prompt 结构                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────────────────────────────────────────────┐     │
│   │              System Prompt (系统提示词)           │     │
│   │   "你是一个专业的法律助手，擅长回答法律问题..."     │     │
│   └─────────────────────────────────────────────────┘     │
│                          │                                  │
│   ┌─────────────────────────────────────────────────┐     │
│   │              User Prompt (用户提示词)            │     │
│   │   "什么是合同法？"                               │     │
│   └─────────────────────────────────────────────────┘     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Prompt 在 Spring AI 中的使用

Spring AI 中使用 `Prompt` 类来表示提示词：

```java
// 方式1：使用 ChatClient（推荐）
chatClient.prompt()
    .

system("你是一个法律助手")
    .

user("什么是合同法？")
    .

call()
    .

content();

// 方式2：使用 Prompt 类
SystemMessage systemMessage = new SystemMessage("你是一个法律助手");
UserMessage userMessage = new UserMessage("什么是合同法？");
Prompt prompt = new Prompt(systemMessage, userMessage);
chatModel.

call(prompt);
```

### 1.3 Message 类型

Spring AI 中定义了多种消息类型：

| 类型                    | 说明    | 使用场景             |
|-----------------------|-------|------------------|
| `UserMessage`         | 用户消息  | 用户输入             |
| `SystemMessage`       | 系统消息  | 设置 AI 角色/行为      |
| `AssistantMessage`    | AI 响应 | 多轮对话记录           |
| `ToolResponseMessage` | 工具响应  | Function Calling |

### 1.4 多模型配置

本模块演示了同时使用 DeepSeek 和 Qwen 两种模型：

```properties
# DeepSeek 配置
spring.ai.chat.model.deepseek.chat.options.model=deepseek-chat
spring.ai.chat.model.deepseek.api-key=${DEEPSEEK_API_KEY}
# Qwen 配置
spring.ai.dashscope.chat.options.model=qwen-plus
spring.ai.dashscope.api-key=${DASHSCOPE_API_KEY}
```

## 源码解析

### 2.1 控制器代码解析

```java

@GetMapping("chat1")
public Flux<String> chat1(@RequestParam(name = "msg") String msg) {
    // 使用 ChatClient 的链式 API 设置系统提示词
    return deepSeekChatClient.prompt()
            .system("你是一个法律助手，只能回答法律相关知识，其他问题回复，无可奉告")
            .user(msg)
            .stream()
            .content();
}

@GetMapping("chat2")
public Flux<ChatResponse> chat2(@RequestParam(name = "msg") String msg) {
    // 使用 Prompt 类手动构建消息
    SystemMessage systemMessage = new SystemMessage("你是一个法律助手...");
    UserMessage userMessage = new UserMessage(msg);
    Prompt prompt = new Prompt(systemMessage, userMessage);
    return deepSeekChatModel.stream(prompt);
}
```

### 2.2 Prompt 构建方式对比

| 方式            | 优点      | 缺点    |
|---------------|---------|-------|
| ChatClient 链式 | 简洁、类型安全 | 灵活性稍低 |
| Prompt 类      | 灵活、可复用  | 代码稍多  |

## 课后练习

### 练习 1：创建不同角色的 Prompt

修改控制器，创建不同角色的 Prompt：

```java

@GetMapping("chat/poet")
public Flux<String> poet(@RequestParam(name = "msg") String msg) {
    return chatClient.prompt()
            .system("你是一个著名诗人，擅长用优美的语言写诗")
            .user(msg)
            .stream()
            .content();
}

@GetMapping("chat/coder")
public Flux<String> coder(@RequestParam(name = "msg") String msg) {
    return chatClient.prompt()
            .system("你是一个资深程序员，擅长Java和Python")
            .user(msg)
            .stream()
            .content();
}
```

### 练习 2：使用 Prompt 类实现多轮对话

模拟多轮对话场景：

```java

@GetMapping("multi")
public String multiTurn() {
    // 创建带历史的消息
    List<Message> messages = new ArrayList<>();

    // 系统消息
    messages.add(new SystemMessage("你是一个有帮助的助手"));

    // 第一轮
    messages.add(new UserMessage("什么是AI？"));
    messages.add(new AssistantMessage("AI是人工智能的缩写..."));

    // 第二轮（带上下文）
    messages.add(new UserMessage("它可以做什么？"));

    Prompt prompt = new Prompt(messages);
    return chatModel.call(prompt).getResult().getOutput().getText();
}
```

### 练习 3：使用中文 Prompt

尝试不同语言的 Prompt，观察效果差异：

```java
// 中文 Prompt
.system("你是一个专业的翻译助手")

// 英文 Prompt  
.

system("You are a professional translator")

// 中英混合
.

system("你是一个AI助手，擅长用中文和英文交流")
```

### 练习 4：提取结构化信息

使用 Prompt 让模型提取结构化信息：

```java

@GetMapping("extract")
public String extract(@RequestParam String text) {
    return chatClient.prompt()
            .system("从以下文本中提取人名、地点、时间，并以JSON格式返回")
            .user("张三今天去了北京参加了明天上午9点的会议")
            .call()
            .content();
}
```

## 下一步

- 学习 [02 - Prompt 模板](./02-prompt-template.md)：使用模板动态构建 Prompt
- 学习 [03 - 结构化输出](./03-structured-output.md)：让 LLM 输出结构化数据
