# 02 - Prompt 模板

本模块介绍 PromptTemplate 的使用，通过模板变量实现动态提示词构建。

## 知识点讲解

### 1.1 什么是 Prompt 模板？

Prompt 模板允许在提示词中使用占位符（变量），运行时动态填充：

```
┌─────────────────────────────────────────────────────────────┐
│                    Prompt 模板示例                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  模板: "写一首关于{topic}的{format}，字数{wordCount}字"      │
│                                                             │
│  参数:                                                      │
│    topic=春天                                               │
│    format=诗                                                │
│    wordCount=100                                            │
│                                                             │
│  结果: "写一首关于春天的诗，字数100字"                         │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Spring AI 中的模板类

| 类                      | 说明     |
|------------------------|--------|
| `PromptTemplate`       | 用户消息模板 |
| `SystemPromptTemplate` | 系统消息模板 |

### 1.3 模板语法

Spring AI PromptTemplate 使用 `{变量名}` 作为占位符：

```java
PromptTemplate template = new PromptTemplate(
        "你是{role}，擅长{skill}，请回答：{question}"
);
Prompt prompt = template.create(Map.of(
        "role", "律师",
        "skill", "合同法",
        "question", "什么是合同？"
));
```

### 1.4 外部模板文件

可以将模板存储在外部文件中：

```java
// 读取 classpath 下的模板文件
@Value("classpath:/prompt/template.txt")
private Resource templateResource;

PromptTemplate template = new PromptTemplate(templateResource);
```

模板文件 `resources/prompt/template.txt`：

```
请以{format}格式介绍{topic}
```

## 源码解析

### 2.1 动态参数模板

```java

@GetMapping("chat1")
public Flux<String> chat1(
        @RequestParam("topic") String topic,
        @RequestParam("outputFormat") String outputFormat,
        @RequestParam("wordCount") String wordCount) {

    // 内联模板字符串
    PromptTemplate promptTemplate = new PromptTemplate("""
            将一个关于{topic}的故事，
            以{outputFormat}格式返回，
            字数限制{wordCount}。
            """);

    // 动态创建 Prompt
    Prompt prompt = promptTemplate.create(Map.of(
            "topic", topic,
            "outputFormat", outputFormat,
            "wordCount", wordCount
    ));

    return deepSeekChatClient.prompt(prompt).stream().content();
}
```

### 2.2 外部模板文件

```java

@Value("classpath:/prompt/template.txt")
private Resource userTemplate;

@GetMapping("chat2")
public Flux<String> chat2(
        @RequestParam("topic") String topic,
        @RequestParam("outputFormat") String outputFormat) {

    // 从文件加载模板
    PromptTemplate promptTemplate = new PromptTemplate(userTemplate);
    Prompt prompt = promptTemplate.create(Map.of(
            "topic", topic,
            "outputFormat", outputFormat
    ));

    return deepSeekChatClient.prompt(prompt).stream().content();
}
```

模板文件内容：

```
请详细介绍{topic}，以{outputFormat}格式呈现
```

### 2.3 System Prompt 模板

```java

@GetMapping("chat3")
public Flux<String> chat3(
        @RequestParam("topic") String sysTopic,
        @RequestParam("outputFormat") String userTopic) {

    // 系统消息模板
    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(
            "你是{systemTopic}助手，只回答{systemTopic}其它无可奉告，以HTML格式的结果。"
    );
    Message systemMessage = systemPromptTemplate.createMessage(
            Map.of("systemTopic", sysTopic)
    );

    // 用户消息模板
    PromptTemplate userPromptTemplate = new PromptTemplate("解释一下{userTopic}");
    Message userMessage = userPromptTemplate.createMessage(
            Map.of("userTopic", userTopic)
    );

    Prompt prompt = new Prompt(systemMessage, userMessage);
    return deepSeekChatClient.prompt(prompt).stream().content();
}
```

## 课后练习

### 练习 1：创建邮件模板

创建一个邮件生成的 Prompt 模板：

```java
PromptTemplate template = new PromptTemplate("""
        写一封{type}邮件，
        发件人：{from}，
        收件人：{to}，
        主题：{subject}，
        内容要求：{content}
        """);

Prompt prompt = template.create(Map.of(
        "type", "商务",
        "from", "张三",
        "to", "李四",
        "subject", "项目合作",
        "content", "简洁专业"
));
```

### 练习 2：创建代码生成模板

创建一个根据需求生成代码的模板：

```java
PromptTemplate template = new PromptTemplate("""
        作为{language}专家，请生成{type}代码
        
        要求：
        - 语言：{language}
        - 类型：{type}
        - 复杂度：{complexity}
        
        需求描述：{description}
        """);
```

### 练习 3：使用外部模板文件

1. 在 `resources/prompt/` 目录下创建 `summarize.txt`：

```
请用不超过{wordCount}字总结以下内容：
{content}
```

2. 在控制器中加载并使用：

```java

@Value("classpath:/prompt/summarize.txt")
private Resource summarizeTemplate;

@GetMapping("summarize")
public String summarize(
        @RequestParam("content") String content,
        @RequestParam(value = "wordCount", defaultValue = "100") int wordCount) {

    PromptTemplate template = new PromptTemplate(summarizeTemplate);
    Prompt prompt = template.create(Map.of(
            "content", content,
            "wordCount", String.valueOf(wordCount)
    ));

    return chatClient.prompt(prompt).call().content();
}
```

### 练习 4：多模板组合

组合使用系统模板和用户模板：

```java
// 系统模板
SystemPromptTemplate systemTemplate = new SystemPromptTemplate(
                "你是一个{domain}专家，用专业的语言回答问题"
        );

// 用户模板
PromptTemplate userTemplate = new PromptTemplate(
        "{question}"
);

Message systemMsg = systemTemplate.createMessage(Map.of("domain", "技术"));
Message userMsg = userTemplate.createMessage(Map.of("question", "什么是AI？"));

Prompt prompt = new Prompt(systemMsg, userMsg);
```

## 下一步

- 回顾 [01 - Prompt 基础](./01-prompt-basics.md)：了解 Prompt 基本概念
- 学习 [03 - 结构化输出](./03-structured-output.md)：让 LLM 输出结构化数据
