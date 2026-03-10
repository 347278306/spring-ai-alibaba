# 04 - MCP 百度工具

本模块介绍如何集成百度 MCP 工具，实现 IP 查询等功能。

## 知识点讲解

### 1.1 百度 MCP 服务

百度提供多种 MCP 工具供调用：

- **IP 查询**：查询 IP 归属地
- **天气查询**：查询城市天气
- **翻译服务**：多语言翻译

### 1.2 使用百度 MCP 的优势

- **官方维护**：百度官方提供的稳定服务
- **无需自建**：直接调用百度接口
- **免费额度**：提供一定免费调用量

### 1.3 接入方式

1. 在百度千帆 AppBuilder 注册 MCP 服务
2. 获取 MCP 服务端点
3. 在应用中配置并调用

## 源码解析

### 2.1 Saa-16 控制器

```java
@RestController
public class McpClientCallBaiDuMcpController {

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

### 2.2 使用示例

查询 IP 归属地：

```
GET /mcpclient/chat?msg=查询61.149.121.66归属地
```

## 课后练习

### 练习 1：申请百度 MCP 服务

1. 访问百度千帆 AppBuilder
2. 创建或加入 MCP 服务
3. 获取 API Key 和服务端点

### 练习 2：配置百度 MCP

```properties
spring.ai.mcp.client.servers.baidu.ip.server-url=your-mcp-url
spring.ai.mcp.client.servers.baidu.ip.api-key=your-api-key
```

### 练习 3：测试不同工具

```java
// IP 查询
"查询 8.8.8.8 的归属地"

// 天气查询
"北京今天天气怎么样"

// 翻译
"把 Hello 翻译成中文"
```

### 练习 4：添加错误处理

```java
@GetMapping("mcpclient/chat")
public Flux<String> chat(@RequestParam("msg") String msg) {
    try {
        return chatClient.prompt(msg)
            .stream()
            .content();
    } catch (Exception e) {
        return Flux.just("调用失败：" + e.getMessage());
    }
}
```

## 下一步

- 回顾 [01 - 工具调用](./01-tool-calling.md)：Function Calling
- 学习 [18 - Agent 调用](../06-agent/01-dashscope-agent.md)：Agent 开发
