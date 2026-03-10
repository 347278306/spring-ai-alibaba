# 02 - Redis 记忆存储

本模块介绍如何使用 Redis 存储对话历史，实现多轮对话的上下文记忆。

## 知识点讲解

### 1.1 什么是 ChatMemory？

ChatMemory（对话记忆）用于在多轮对话中保持上下文：

```
┌─────────────────────────────────────────────────────────────┐
│                    多轮对话示例                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  第一轮:                                                     │
│  U: 我想学习Java                                             │
│  A: Java是一种面向对象的编程语言...                           │
│                                                             │
│  第二轮 (带记忆):                                             │
│  U: 那它和Python有什么区别？                                  │
│  A: Java和Python的主要区别在于...                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Spring AI 记忆组件

| 组件                   | 说明            |
|----------------------|---------------|
| `ChatMemory`         | 记忆接口          |
| `InMemoryChatMemory` | 内存实现（测试用）     |
| `RedisChatMemory`    | Redis 实现（生产用） |

### 1.3 Redis 存储架构

```
┌──────────────┐      ┌──────────────┐      ┌──────────────┐
│   用户 A      │      │   用户 B      │      │   用户 C      │
└──────┬───────┘      └──────┬───────┘      └──────┬───────┘
       │                     │                     │
       │ session:a           │ session:b           │ session:c
       ▼                     ▼                     ▼
┌──────────────────────────────────────────────────────────────┐
│                      Redis                                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ session:a   │  │ session:b   │  │ session:c   │         │
│  │ [msg1,msg2] │  │ [msg1,msg2] │  │ [msg1,msg2] │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
└──────────────────────────────────────────────────────────────┘
```

### 1.4 配置项

```properties
# Redis 配置
spring.data.redis.host=localhost
spring.data.redis.port=6379

# ChatMemory 配置
spring.ai.chat.memory.conversation-duration=30m
```

## 源码解析

### 2.1 控制器源码

```java
@RestController
public class ChatMemory4RedisController {

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    @GetMapping("/chatmemory/chat")
    public String chat(@RequestParam("msg") String msg, @RequestParam("userId") String userId) {
        return qwenChatClient.prompt(msg)
            .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
            .call()
            .content();
    }
}
```

### 2.2 关键点解析

1. **ChatMemory Advisor**：Spring AI 自动注入的增强器
2. **CONVERSATION_ID 参数**：标识不同用户的会话
3. **自动上下文注入**：历史消息自动添加到 Prompt

### 2.3 自动配置原理

```
RedisChatMemory Bean
        ↓
ChatMemoryAdvisor 自动配置
        ↓
每次请求自动携带历史上下文
```

## 课后练习

### 练习 1：验证多轮对话

1. 启动 Saa-08 模块
2. 第一次请求：`/chatmemory/chat?msg=我喜欢编程&userId=user1`
3. 第二次请求：`/chatmemory/chat?msg=最喜欢的语言是什么？&userId=user1`
4. 观察第二次回复是否理解"编程"上下文

### 练习 2：不同用户隔离

使用不同 userId，观察对话是否隔离：

```bash
# 用户1
curl "localhost:8008/chatmemory/chat?msg=我叫张三&userId=user1"
curl "localhost:8008/chatmemory/chat?msg=我叫什么？&userId=user1"

# 用户2 (应该不知道用户1的名字)
curl "localhost:8008/chatmemory/chat?msg=我叫什么？&userId=user2"
```

### 练习 3：配置对话过期时间

修改配置，设置对话在 5 分钟后过期：

```properties
spring.ai.chat.memory.conversation-duration=5m
```

### 练习 4：手动管理 ChatMemory

如果需要更细粒度的控制，可以手动使用 ChatMemory：

```java
@Resource
private ChatMemory chatMemory;

public String chat(String userId, String message) {
    // 获取历史
    List<Message> history = chatMemory.get(userId, new Prompt()).get();
    
    // 添加新消息
    chatMemory.add(userId, MessageUtils.createUserMessage(message));
    
    // 构建完整 Prompt
    // ...
}
```

### 练习 5：使用 InMemoryChatMemory 测试

在测试环境使用内存记忆：

```java
@Bean
public ChatMemory chatMemory() {
    return new InMemoryChatMemory();
}
```

## 下一步

- 学习 [03 - 文生图](./03-text2image.md)：图像生成能力
- 学习 [04 - 文生语音](./04-text2voice.md)：语音合成能力
- 学习 [05 - 文本向量化](./05-embedding.md)：Embedding 基础
