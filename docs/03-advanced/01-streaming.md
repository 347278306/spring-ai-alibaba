# 01 - 流式输出

本模块介绍如何使用 Spring AI 实现流式输出（Streaming），实现 Server-Sent Events (SSE) 实时响应。

## 知识点讲解

### 1.1 什么是流式输出？

流式输出是指 LLM 在生成完整响应前，实时推送部分内容给客户端，适用于：

- **长文本生成**：文章、故事、代码
- **实时交互**：打字机效果
- **低延迟体验**：减少等待时间

```
┌─────────────────────────────────────────────────────────────┐
│                    流式输出 vs 同步输出                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  同步输出:                                                   │
│  ┌─────────────────────────────────────┐                    │
│  │ 请求 ──────────────────────────▶ 响应 │                   │
│  │        (等待全部生成完成)             │                   │
│  └─────────────────────────────────────┘                    │
│                                                             │
│  流式输出:                                                   │
│  ┌─────────────────────────────────────┐                    │
│  │ 请求 ──────────────────────────┐    │                    │
│  │         ⬇ 片段1 "今天"           │    │                    │
│  │         ⬇ 片段2 "天气"          │    │                    │
│  │         ⬇ 片段3 "很好"          │    │                   │
│  │         ⬇ 片段4 "..."          │    │                   │
│  └─────────────────────────────────────┘                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Spring AI 流式 API

| 方法                             | 返回类型                 | 说明        |
|--------------------------------|----------------------|-----------|
| `ChatModel.stream(String)`     | `Flux<String>`       | 简化版流式     |
| `ChatModel.stream(Prompt)`     | `Flux<ChatResponse>` | 完整版流式     |
| `ChatClient.prompt().stream()` | `Flux<String>`       | Client 流式 |

### 1.3 Flux 响应式编程

Spring AI 使用 Project Reactor 的 `Flux` 实现流式输出：

```java
@GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamChat(@RequestParam String msg) {
    return chatClient.prompt()
        .user(msg)
        .stream()
        .content();
}
```

**关键点**：

- `produces = MediaType.TEXT_EVENT_STREAM_VALUE`：声明 SSE 响应
- `Flux<String>`：异步流式数据

## 源码解析

### 2.1 ChatModel 流式调用

```java
@GetMapping("/chatModel/deepSeek")
public Flux<String> deepSeekModel(@RequestParam("question") String question){
    return deepSeekChatModel.stream(question);
}
```

### 2.2 ChatClient 流式调用

```java
@GetMapping("/chatClient/deepSeek")
public Flux<String> deepSeekClient(@RequestParam("question") String question){
    return deepSeekChatClient.prompt()
        .user(question)
        .stream()
        .content();
}
```

### 2.3 完整流程

```
1. 客户端发起请求 (Accept: text/event-stream)
          ↓
2. Controller 返回 Flux<String>
          ↓
3. Spring MVC 处理 Flux，启用 SSE
          ↓
4. 数据以流式方式推送到客户端
```

## 课后练习

### 练习 1：创建流式接口

修改 Saa-01 项目，添加流式输出接口：

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> stream(@RequestParam(name = "msg") String msg) {
    return chatModel.stream(msg);
}
```

### 练习 2：前端 SSE 消费

使用 JavaScript 消费 SSE 流：

```javascript
async function chat() {
    const response = await fetch('/stream?msg=讲个故事');
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    
    while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        console.log(decoder.decode(value));
    }
}
```

### 练习 3：自定义 SSE 格式

在流式输出中添加额外信息：

```java
@GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent> streamChat(@RequestParam String msg) {
    return chatModel.stream(msg)
        .map(content -> ServerSentEvent.builder()
            .data(content)
            .event("message")
            .build());
}
```

### 练习 4：流式输出与结构化输出结合

尝试让流式输出返回结构化数据：

```java
// 注意：流式输出通常不用于结构化输出
// 如需结构化输出，使用同步调用
@GetMapping("/structured")
public StudentRecord structured(@RequestParam String input) {
    return chatClient.prompt()
        .user(input)
        .call()
        .entity(StudentRecord.class);
}
```

### 练习 5：错误处理

为流式接口添加错误处理：

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> stream(@RequestParam String msg) {
    return chatModel.stream(msg)
        .onErrorResume(e -> Flux.just("Error: " + e.getMessage()));
}
```

## 下一步

- 回顾 [01 - ChatModel 基础聊天](../01-basics/01-chat-model.md)：了解基础 API
- 学习 [02 - Redis 记忆存储](./02-memory-redis.md)：实现多轮对话
