# 03 - 结构化输出

本模块介绍如何让 LLM 输出结构化数据（JSON、Java Record 等），这是构建 AI 应用的基础能力。

## 知识点讲解

### 1.1 什么是结构化输出？

结构化输出是指 LLM 返回符合预定义格式的数据，常见格式包括：

- **JSON**：最常用，利于程序解析
- **Java Record**：Java 17+ 特性，等同于数据类
- **POJO**：普通 Java 对象

```
┌─────────────────────────────────────────────────────────────┐
│                    结构化输出示例                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  用户输入: "学号1001，我叫张三，大学计算机专业，邮箱zhangsan@   │
│           example.com"                                      │
│                                                             │
│  输出格式 (JSON):                                           │
│  {                                                          │
│    "id": "1001",                                            │
│    "name": "张三",                                          │
│    "major": "计算机专业",                                    │
│    "email": "zhangsan@example.com"                         │
│  }                                                          │
│                                                             │
│  Java Record:                                               │
│  StrudentRecord(id="1001", name="张三",                     │
│                 major="计算机专业", email="...")             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Spring AI 结构化输出 API

ChatClient 提供了 `.entity(Class<T>)` 方法直接将响应转换为对象：

```java
// 直接获取实体
Student student = chatClient.prompt()
                .user("学号1001，我叫张三，大学计算机专业，邮箱zhangsan@example.com")
                .call()
                .entity(Student.class);
```

### 1.3 Java Record 简介

Java 14 引入的 Record 是不可变的数据类：

```java
// 定义
public record Student(String id, String name, String major, String email) {
}

// 使用
Student s = new Student("1001", "张三", "计算机", "test@example.com");
String name = s.name();  // 访问器方法
```

## 源码解析

### 2.1 Student Record 定义

```java
package com.hao.saa07.records;

public record StrudentRecord(String id, String name, String magor, String email) {
}
```

> 注意：`magor` 是源码中的拼写，实际应为 `major`

### 2.2 方式一：使用 Consumer 构建 Prompt

```java

@GetMapping("chat1")
public StrudentRecord chat1(
        @RequestParam("name") String name,
        @RequestParam("email") String email) {

    return qwenChatClient.prompt()
            .user(new Consumer<ChatClient.PromptUserSpec>() {
                @Override
                public void accept(ChatClient.PromptUserSpec promptUserSpec) {
                    promptUserSpec.text("学号1001，我叫{name}，大学计算机专业，邮箱{email}")
                            .param("name", name)
                            .param("email", email);
                }
            })
            .call()
            .entity(StrudentRecord.class);
}
```

### 2.3 方式二：使用 Lambda 简化

```java

@GetMapping("chat2")
public StrudentRecord chat2(
        @RequestParam("name") String name,
        @RequestParam("email") String email) {

    String template = "学号1002，我叫{name}，大学计算机专业，邮箱{email}";

    return qwenChatClient.prompt()
            .user(promptUserSpec -> promptUserSpec
                    .text(template)
                    .param("name", name)
                    .param("email", email))
            .call()
            .entity(StrudentRecord.class);
}
```

### 2.4 执行流程

```
1. 构建 Prompt（含模板参数）
        ↓
2. 发送给 LLM，并指定输出格式
        ↓
3. LLM 返回 JSON 字符串
        ↓
4. Spring AI 自动反序列化为 Java 对象
        ↓
5. 返回 POJO/Record
```

## 课后练习

### 练习 1：创建更多 Record 类型

创建一个产品信息 Record：

```java
public record ProductRecord(
        String id,
        String name,
        BigDecimal price,
        String category,
        List<String> tags
) {
}
```

使用结构化输出：

```java

@GetMapping("product")
public ProductRecord product(@RequestParam("name") String name) {
    return chatClient.prompt()
            .user("创建一个产品：名称={name}，价格=299.9，分类=电子产品，标签=热门,新品")
            .call()
            .entity(ProductRecord.class);
}
```

### 练习 2：返回列表数据

让 LLM 返回列表：

```java
public record BookList(List<BookRecord> books) {
}

public record BookRecord(String title, String author, int price) {
}

@GetMapping("books")
public BookList books() {
    return chatClient.prompt()
            .user("推荐3本编程书籍，包含书名、作者、价格")
            .call()
            .entity(BookList.class);
}
```

### 练习 3：处理嵌套对象

创建嵌套结构的 Record：

```java
public record CompanyRecord(
        String name,
        AddressRecord address,
        List<EmployeeRecord> employees
) {
}

public record AddressRecord(String city, String street, String zipCode) {
}

public record EmployeeRecord(String name, String position, int salary) {
}

@GetMapping("company")
public CompanyRecord company() {
    return chatClient.prompt()
            .user("""
                    创建一家公司：
                    - 公司名：TechCorp
                    - 地址：北京海淀区中关村
                    - 员工：张三(CEO, 50000), 李四(CTO, 40000)
                    """)
            .call()
            .entity(CompanyRecord.class);
}
```

### 练习 4：使用 @JsonProperty 自定义字段映射

如果 JSON 字段名与 Java 字段名不一致：

```java
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRecord(
        @JsonProperty("user_id") String userId,
        @JsonProperty("user_name") String userName,
        @JsonProperty("email_address") String email
) {
}
```

### 练习 5：自定义输出格式提示

在 Prompt 中明确指定输出格式：

```java

@GetMapping("custom")
public MyRecord custom(@RequestParam("input") String input) {
    return chatClient.prompt()
            .user(""" 
                    输入：{input}
                    
                    请严格按照以下JSON格式返回：
                    {
                      "field1": "值1",
                      "field2": "值2"
                    }
                    只返回JSON，不要其他内容。
                    """)
            .param("input", input)
            .call()
            .entity(MyRecord.class);
}
```

## 下一步

- 回顾 [02 - Prompt 模板](./02-prompt-template.md)：学习模板使用
- 学习 [04 - 流式输出](../03-advanced/01-streaming.md)：实现实时响应
- 学习 [13 - 工具调用](../05-tools/01-tool-calling.md)：让 LLM 调用外部工具
