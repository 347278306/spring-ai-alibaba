# 03 - MCP Client

本模块介绍如何使用 Spring AI 作为 MCP 客户端，调用外部 MCP 服务提供的工具。

## 知识点讲解

### 1.1 MCP 客户端场景

MCP 客户端可以调用其他应用暴露的 MCP 服务：

```
┌─────────────────────────────────────────────────────────────┐
│                    MCP 客户端架构                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐           ┌─────────────┐                  │
│  │ 本应用      │  ──────▶  │  MCP 服务端  │                  │
│  │ (MCP Client)│           │  (天气API)  │                  │
│  └─────────────┘           └─────────────┘                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 MCP 客户端使用场景

- **调用第三方工具**：如百度的 MCP 服务
- **调用自建服务**：企业内部 MCP 服务
- **组合多个服务**：同时调用多个 MCP 服务

### 1.3 Spring AI MCP 客户端

使用 `@McpTool` 或 `McpToolCallbackProvider` 调用外部 MCP：

```java
@McpTool(description = "外部天气服务")
private ExternalWeatherService weatherService;
```

## 源码解析

### 2.1 Saa-15 MCP Client

```java
@RestController
public class McpClientController {

    @Resource
    private ChatClient chatClient;

    @Resource
    private ChatModel chatModel;

    @GetMapping("mcpclient/chat")
    public Flux<String> chat(@RequestParam("msg") String msg) {
        return chatClient.prompt(msg)
            .stream()
            .content();
    }

    @GetMapping("mcpclient/chat2")
    public Flux<String> chat2(@RequestParam("msg") String msg) {
        return chatModel.stream(msg);
    }
}
```

### 2.2 MCP 配置

```properties
# MCP 客户端配置
spring.ai.mcp.client.enabled=true
spring.ai.mcp.client.servers.weather.server-url=ws://localhost:8080/mcp
```

## 课后练习

### 练习 1：配置外部 MCP 服务

```properties
spring.ai.mcp.client.servers.my-server.server-url=ws://localhost:8080/mcp
spring.ai.mcp.client.servers.my-server.name=my-server
```

### 练习 2：创建 MCP 工具类

```java
@McpTool(name = "search", description = "搜索工具")
public class SearchTool {
    public String search(String query) {
        return "搜索结果：" + query;
    }
}
```

### 练习 3：在 ChatClient 中使用 MCP 工具

```java
@GetMapping("/chat")
public String chat(@RequestParam String msg) {
    return chatClient.prompt(msg)
        .tools(myMcpTool)
        .call()
        .content();
}
```

### 练习 4：调试 MCP 调用

添加日志查看工具调用：

```java
@Bean
public ToolCallLoggingAdvisor loggingAdvisor() {
    return new ToolCallLoggingAdvisor();
}
```

## 下一步

- 回顾 [02 - MCP Server](./02-mcp-server.md)：MCP 服务端
- 学习 [04 - MCP 百度工具](./04-mcp-baidu.md)：调用百度 MCP
