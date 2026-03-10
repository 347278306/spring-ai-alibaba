# 02 - MCP Server

本模块介绍如何使用 Spring AI 实现 MCP (Model Context Protocol) 服务端，将应用的服务暴露给外部 MCP 客户端调用。

## 知识点讲解

### 1.1 什么是 MCP？

MCP（Model Context Protocol）是一种标准化协议，用于：

- **工具共享**：将 AI 应用的能力暴露给其他应用
- **资源访问**：提供对数据和文件的标准化访问
- **提示模板**：共享可复用的提示词

```
┌─────────────────────────────────────────────────────────────┐
│                    MCP 架构                                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐           ┌─────────────┐                  │
│  │  MCP 客户端 │  ──────▶  │  MCP 服务端 │                  │
│  │  (如 Claude)│           │  (本项目)   │                  │
│  └─────────────┘           └──────┬──────┘                  │
│                                  │                          │
│                                  ▼                          │
│                         ┌─────────────┐                    │
│                         │   工具服务   │                    │
│                         │ WeatherTool │                    │
│                         └─────────────┘                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 MCP vs Function Calling

| 特性 | Function Calling | MCP   |
|----|------------------|-------|
| 范围 | 单应用内             | 应用间   |
| 协议 | 厂商私有             | 标准化   |
| 场景 | LLM 调用工具         | 跨应用集成 |

### 1.3 Spring AI MCP 组件

| 组件                           | 说明                         |
|------------------------------|----------------------------|
| `MethodToolCallbackProvider` | 将 Spring Bean 方法暴露为 MCP 工具 |
| `ToolCallbackProvider`       | 工具提供者接口                    |

## 源码解析

### 2.1 天气服务

```java

@Service
public class WeatherService {

    @Tool(description = "根据城市获取天气")
    public String getWeatherByCity(String city) {
        Map<String, String> map = Map.of(
                "北京", "晴天",
                "上海", "阴天",
                "广州", "小雨",
                "深圳", "多云转晴"
        );
        return map.getOrDefault(city, "抱歉，未查询到对应城市");
    }
}
```

### 2.2 MCP 服务端配置

```java

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService)
                .build();
    }
}
```

### 2.3 配置

```properties
# MCP 服务端配置
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.port=8080
```

## 课后练习

### 练习 1：创建更多工具

扩展天气服务：

```java

@Service
public class ExtendedWeatherService {

    @Tool(description = "获取城市天气预报")
    public String forecast(String city, int days) {
        return city + "未来" + days + "天：晴转多云";
    }

    @Tool(description = "获取空气质量")
    public String airQuality(String city) {
        return city + "空气质量：优";
    }
}
```

### 练习 2：配置 MCP 服务

```properties
# application.properties
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.port=8080
spring.ai.mcp.server.path=/mcp
```

### 练习 3：注册多个工具

```java

@Bean
public ToolCallbackProvider allTools(
        WeatherService weatherService,
        ExtendedWeatherService extendedWeatherService
) {
    return MethodToolCallbackProvider.builder()
            .toolObjects(weatherService, extendedWeatherService)
            .build();
}
```

### 练习 4：使用 @ToolParam

```java
public class ComplexTool {

    @Tool(description = "复杂查询工具")
    public String query(
            @ToolParam(description = "查询类型") String type,
            @ToolParam(description = "查询条件") Map<String, Object> params
    ) {
        return "查询类型：" + type;
    }
}
```

### 练习 5：测试 MCP 服务

使用 MCP 客户端连接并调用：

```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/call",
  "params": {
    "name": "getWeatherByCity",
    "arguments": {
      "city": "北京"
    }
  }
}
```

## 下一步

- 学习 [01 - 工具调用](./01-tool-calling.md)：Function Calling 基础
- 学习 [03 - MCP Client](./03-mcp-client.md)：调用外部 MCP 服务
