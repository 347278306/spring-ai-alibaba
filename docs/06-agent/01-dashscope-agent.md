# 01 - DashScope Agent

本模块介绍如何使用阿里云 DashScope Agent 构建智能体（Agent），实现更复杂的 AI 应用。

## 知识点讲解

### 1.1 什么是 Agent？

Agent（智能体）是一个能够自主思考、规划和执行任务的 AI 系统：

```
┌─────────────────────────────────────────────────────────────┐
│                    Agent 能力                                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Agent = LLM + 工具 + 记忆 + 规划                            │
│                                                             │
│  ┌─────────────┐                                           │
│  │  理解意图   │  分析用户需求                               │
│  └──────┬──────┘                                           │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │  规划分解   │  将任务拆分为子任务                         │
│  └──────┬──────┘                                           │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │  工具调用   │  调用外部工具完成子任务                     │
│  └──────┬──────┘                                           │
│         ▼                                                   │
│  ┌─────────────┐                                           │
│  │  结果整合   │  整合结果，返回给用户                       │
│  └─────────────┘                                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 DashScope Agent

阿里云 DashScope Agent 是基于通义千问的 Agent 平台：

- **预置能力**：内置多种能力（如智能点餐）
- **自定义开发**：支持自定义 Agent
- **工具扩展**：可接入 MCP 工具

### 1.3 Agent vs 普通 LLM 调用

| 特性   | 普通 LLM | Agent |
|------|--------|-------|
| 工具调用 | 手动配置   | 自动选择  |
| 任务规划 | 单轮对话   | 自主规划  |
| 多步操作 | 不支持    | 支持    |

## 源码解析

### 2.1 Agent 控制器

```java
@RestController
public class MenuCallAgentController {

    @Value("${spring.ai.dashscope.agent.options.app-id}")
    private String appId;

    @Resource
    private DashScopeAgent dashScopeAgent;

    @GetMapping(value = "/eatAgent")
    public String eatAgent(@RequestParam("msg") String msg) {
        // 1. 构建 Agent 选项
        DashScopeAgentOptions agentOptions = DashScopeAgentOptions.builder()
            .appId(appId)  // Agent 应用 ID
            .build();

        // 2. 构建 Prompt
        Prompt prompt = new Prompt(msg, agentOptions);

        // 3. 调用 Agent
        return dashScopeAgent.call(prompt)
            .getResult()
            .getOutput()
            .getText();
    }
}
```

### 2.2 配置

```properties
# DashScope Agent 配置
spring.ai.dashscope.agent.options.app-id=your-app-id
spring.ai.dashscope.agent.base-url=https://dashscope.aliyuncs.com/compatible-mode/v1
```

### 2.3 调用流程

```
用户: "今天吃什么？"
       │
       ▼
   DashScopeAgent
       │
       ├── 意图理解: 用户想要点餐/推荐
       │
       ├── 调用内置工具: 查询菜单/推荐
       │
       ├── 获取结果: "推荐宫保鸡丁..."
       │
       ▼
   返回推荐结果
```

## 课后练习

### 练习 1：创建 DashScope Agent

1. 访问阿里云百炼控制台
2. 创建 Agent 应用
3. 配置工具和能力
4. 获取 App ID

### 练习 2：使用不同 Agent

```java
@GetMapping("/agent")
public String agent(
    @RequestParam String msg,
    @RequestParam String appId) {
    
    DashScopeAgentOptions options = DashScopeAgentOptions.builder()
        .appId(appId)
        .build();
    
    return dashScopeAgent.call(new Prompt(msg, options))
        .getResult()
        .getOutput()
        .getText();
}
```

### 练习 3：流式输出

```java
@GetMapping(value = "/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamAgent(@RequestParam String msg) {
    DashScopeAgentOptions options = DashScopeAgentOptions.builder()
        .appId(appId)
        .build();
    
    return dashScopeAgent.stream(new Prompt(msg, options))
        .map(r -> r.getResult().getOutput().getText());
}
```

### 练习 4：添加系统提示词

```java
DashScopeAgentOptions options = DashScopeAgentOptions.builder()
    .appId(appId)
    .systemPrompt("你是一个专业的营养师，给出健康建议")
    .build();
```

### 练习 5：调试 Agent

查看 Agent 的调用日志：

```java
@GetMapping("/agent/debug")
public Map<String, Object> debug(@RequestParam String msg) {
    DashScopeAgentOptions options = DashScopeAgentOptions.builder()
        .appId(appId)
        .build();
    
    AgentResponse response = dashScopeAgent.call(new Prompt(msg, options));
    
    return Map.of(
        "output", response.getResult().getOutput().getText(),
        "metadata", response.getMetadata()
    );
}
```

## 学习路径总结

完成本项目学习后，你应该掌握：

1. **基础能力**：ChatModel、ChatClient、Prompt
2. **高级特性**：流式输出、记忆存储、Embedding
3. **RAG**：知识库问答
4. **工具调用**：Function Calling、MCP
5. **Agent**：智能体开发

继续深入学习：

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [阿里云百炼平台](https://bailian.console.aliyun.com/)
- [LangChain4j](https://github.com/langchain4j/langchain4j)：Java 版 LangChain
