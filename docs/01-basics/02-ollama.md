# 02 - Ollama 本地模型

本模块介绍如何在 Spring AI 中集成 Ollama，实现在本地运行大语言模型。

## 知识点讲解

### 1.1 什么是 Ollama？

Ollama 是一个本地大语言模型运行平台，支持在个人电脑上运行各种开源 LLM：

- **本地运行**：无需 API 调用，保护隐私，降低成本
- **开源模型**：支持 Llama 2、Qwen、Mistral、Gemma 等多种模型
- **跨平台**：支持 macOS、Linux、Windows

### 1.2 Ollama 与 Spring AI 集成架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring AI 应用                           │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              spring-ai-starter-model-ollama          │   │
│  └─────────────────────┬───────────────────────────────┘   │
│                        │                                     │
│                        ▼                                     │
│              ┌─────────────────────┐                         │
│              │   OllamaChatModel   │                         │
│              └──────────┬──────────┘                         │
└─────────────────────────┼───────────────────────────────────┘
                          │
                          ▼
              ┌─────────────────────┐
              │   Ollama Server     │
              │   (localhost:11434) │
              └─────────────────────┘
```

### 1.3 依赖配置

在 `pom.xml` 中添加 Ollama 依赖：

```xml

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 1.4 配置项说明

```properties
# Ollama 服务地址
spring.ai.ollama.base-url=http://localhost:11434
# 使用的模型名称
spring.ai.ollama.chat.model=qwen2.5:latest
```

### 1.5 多模型并存

项目中可以同时配置 DashScope（云端）和 Ollama（本地）两个 ChatModel，通过 `@Qualifier` 注解区分：

| Bean 名称           | 模型      | 来源           |
|-------------------|---------|--------------|
| `chatModel`       | 通义千问    | DashScope 云端 |
| `ollamaChatModel` | Qwen2.5 | Ollama 本地    |

## 源码解析

### 2.1 控制器源码

```java

@RestController
@RequestMapping("ollama")
public class OllamaController {

    // 方式1：使用 @Resource + @Qualifier 指定 Bean 名称
    @Resource
    @Qualifier("ollamaChatModel")
    private ChatModel chatModel;

    @GetMapping("chat")
    public String chat(@RequestParam(name = "msg", defaultValue = "你是谁") String msg) {
        return chatModel.call(msg);
    }

    @GetMapping("streamChat")
    public Flux<String> streamChat(@RequestParam(name = "msg", defaultValue = "你是谁") String msg) {
        return chatModel.stream(msg);
    }
}
```

### 2.2 Bean 名称解析

Spring AI 自动配置会根据配置创建不同类型的 ChatModel：

```
DashScope 配置 ──────────▶ Bean: chatModel (默认)
Ollama 配置 ──────────────▶ Bean: ollamaChatModel (带前缀)
```

### 2.3 Ollama 使用流程

```
1. 安装 Ollama
   └── macOS: brew install ollama
   └── Linux: curl -fsSL https://ollama.com/install.sh | sh
   └── Windows: 下载安装包

2. 启动 Ollama 服务
   └── ollama serve

3. 拉取模型
   └── ollama pull qwen2.5

4. 验证运行
   └── ollama list
```

## 课后练习

### 练习 1：安装并运行 Ollama

1. 下载安装 Ollama：https://ollama.com/download
2. 启动服务：`ollama serve`
3. 拉取模型：`ollama pull qwen2.5`
4. 测试对话：`ollama run qwen2.5 "你好"`

### 练习 2：切换不同模型

尝试拉取并使用其他模型：

```bash
# 拉取 Llama 2
ollama pull llama2

# 拉取 Mistral
ollama pull mistral
```

修改配置使用不同模型：

```properties
spring.ai.ollama.chat.model=llama2
```

**思考**：不同模型在中文理解、代码生成方面的表现有何差异？

### 练习 3：添加系统提示词

Ollama 支持 System Prompt，修改控制器添加系统设置：

```java

@GetMapping("chat")
public String chat(@RequestParam(name = "msg", defaultValue = "你是谁") String msg) {
    Prompt prompt = new Prompt(msg);
    prompt.setSystem("你是一个专业的Python程序员，擅长用简洁优雅的代码解决问题。");
    return chatModel.call(prompt);
}
```

### 练习 4：对比云端与本地模型响应时间

创建一个接口，同时调用 DashScope 和 Ollama，比较响应时间：

```java

@GetMapping("compare")
public Map<String, Object> compare(@RequestParam String msg) {
    long start1 = System.currentTimeMillis();
    String response1 = dashscopeChatModel.call(msg);  // 需要注入
    long time1 = System.currentTimeMillis() - start1;

    long start2 = System.currentTimeMillis();
    String response2 = ollamaChatModel.call(msg);
    long time2 = System.currentTimeMillis() - start2;

    return Map.of(
            "dashscope", Map.of("response", response1, "time", time1),
            "ollama", Map.of("response", response2, "time", time2)
    );
}
```

## 下一步

- 回顾 [01-ChatModel 基础聊天](./01-chat-model.md)：了解基础聊天 API
- 学习 [Saa-03 - ChatClient API](./03-chat-client.md)：了解更高级的对话 API
