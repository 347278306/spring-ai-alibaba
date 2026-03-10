# 01 - 工具调用

本模块介绍如何使用 Spring AI 的 Function Calling（工具调用）功能，让 LLM 能够调用外部工具扩展能力。

## 知识点讲解

### 1.1 什么是 Function Calling？

Function Calling（函数调用）允许 LLM 在回答问题时调用预先定义的外部函数：

```
┌─────────────────────────────────────────────────────────────┐
│                    工具调用流程                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  用户: "今天天气怎么样？"                                     │
│       │                                                     │
│       ▼                                                     │
│  LLM: 识别需要调用天气函数                                    │
│       │                                                     │
│       ▼                                                     │
│  调用外部函数 getWeather(city="北京")                        │
│       │                                                     │
│       ▼                                                     │
│  返回结果: "晴天，25°C"                                      │
│       │                                                     │
│       ▼                                                     │
│  LLM: "今天北京天气晴朗，气温25°C"                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Spring AI 工具调用组件

| 组件                       | 说明         |
|--------------------------|------------|
| `@Tool`                  | 标记方法为可调用工具 |
| `ToolCallback`           | 工具回调接口     |
| `ToolCallbacks`          | 工具注册工具类    |
| `ToolCallingChatOptions` | 工具调用选项     |

### 1.3 定义工具

使用 `@Tool` 注解标记方法：

```java
public class WeatherTools {

    @Tool(description = "根据城市获取天气")
    public String getWeather(String city) {
        // 调用天气 API
        return "晴天，25°C";
    }
}
```

### 1.4 注册工具

```java
// 方式1：使用 ToolCallbacks
ToolCallback[] tools = ToolCallbacks.from(new WeatherTools());

// 方式2：使用 ChatClient
chatClient.

prompt()
    .

tools(new WeatherTools())
        .

call()
    .

content();
```

## 源码解析

### 2.1 工具类定义

```java
public class DateTimeTools {

    @Tool(description = "获取当前时间", returnDirect = false)
    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }
}
```

**关键点**：

- `@Tool`：标记该方法可被 LLM 调用
- `description`：描述工具用途，帮助 LLM 选择
- `returnDirect`：是否直接返回结果给用户

### 2.2 方式一：使用 ChatModel

```java

@GetMapping("/toolcall/chat")
public String chat(@RequestParam("msg") String msg) {
    // 1. 将工具注册到工具集中
    ToolCallback[] tools = ToolCallbacks.from(new DateTimeTools());

    // 2. 配置工具调用选项
    ToolCallingChatOptions options = ToolCallingChatOptions.builder()
            .toolCallbacks(tools)
            .build();

    // 3. 构建 Prompt
    Prompt prompt = new Prompt(msg, options);

    // 4. 调用
    return chatModel.call(prompt).getResult().getOutput().getText();
}
```

### 2.3 方式二：使用 ChatClient（推荐）

```java

@GetMapping("/toolcall/chat2")
public Flux<String> chat2(@RequestParam("msg") String msg) {
    return chatClient.prompt(msg)
            .tools(new DateTimeTools())
            .stream()
            .content();
}
```

## 课后练习

### 练习 1：创建天气查询工具

创建天气工具类：

```java
public class WeatherTool {

    @Tool(description = "获取指定城市的天气信息")
    public String getWeather(@ToolParam(description = "城市名称") String city) {
        Map<String, String> weather = Map.of(
                "北京", "晴天 25°C",
                "上海", "多云 28°C",
                "广州", "小雨 30°C"
        );
        return weather.getOrDefault(city, "未找到该城市天气信息");
    }
}
```

### 练习 2：使用 ChatClient 注册多个工具

```java

@GetMapping("/multi")
public String multi(@RequestParam String msg) {
    return chatClient.prompt(msg)
            .tools(new WeatherTool(), new DateTimeTools())
            .call()
            .content();
}
```

### 练习 3：带参数的天气查询

```java

@GetMapping("/weather")
public String weather(@RequestParam String city) {
    return chatClient.prompt()
            .user("查询" + city + "的天气")
            .tools(new WeatherTool())
            .call()
            .content();
}
```

### 练习 4：处理工具调用错误

添加异常处理：

```java
public class SafeWeatherTool {

    @Tool(description = "获取天气")
    public String getWeather(String city) {
        try {
            // 调用外部 API
            return callWeatherApi(city);
        } catch (Exception e) {
            return "查询失败：" + e.getMessage();
        }
    }
}
```

### 练习 5：使用 @ToolParam 细化参数描述

```java
public class CalcTool {

    @Tool(description = "计算器工具")
    public double calc(
            @ToolParam(description = "第一个数字") double a,
            @ToolParam(description = "运算符 + - * /") String operator,
            @ToolParam(description = "第二个数字") double b
    ) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> b != 0 ? a / b : 0;
            default -> 0;
        };
    }
}
```

## 下一步

- 学习 [02 - MCP Server](./02-mcp-server.md)：实现 MCP 协议服务端
- 学习 [03 - MCP Client](./03-mcp-client.md)：调用外部 MCP 服务
