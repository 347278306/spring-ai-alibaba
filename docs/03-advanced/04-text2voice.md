# 04 - 文生语音

本模块介绍如何使用 Spring AI 和阿里云 DashScope 实现文本转语音（Text-to-Speech, TTS）。

## 知识点讲解

### 1.1 语音合成模型

阿里云 DashScope 提供语音合成模型：

| 模型             | 说明         |
|----------------|------------|
| `cosyvoice-v2` | 语音合成 v2 版本 |

### 1.2 可用音色

| 音色 ID         | 说明       |
|---------------|----------|
| `longyingcui` | 影视银voice |
| `xiaoyun`     | 客服女声     |
| `xiaogang`    | 客服男声     |

### 1.3 Spring AI TTS API

```
┌─────────────────────────────────────────────────────────────┐
│                  Spring AI 语音模型                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   TextToSpeechModel                                         │
│   ├── call(TextToSpeechPrompt) → AudioResponse              │
│   │                                                            │
│   ├── TextToSpeechPrompt                                     │
│   │   ├── text (String)                                      │
│   │   └── options (AudioSpeechOptions)                       │
│   │                                                            │
│   └── AudioResponse                                          │
│       └── getResult().getOutput() → byte[]                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 源码解析

### 2.1 控制器源码

```java
@RestController
public class Text2VoiceController {

    public static final String BAILIAN_VOICE_MODEL = "cosyvoice-v2";
    public static final String BAILIAN_VOICE_TIMBER = "longyingcui";

    @Resource
    private TextToSpeechModel textToSpeechModel;

    @GetMapping(value = "/t2v/voice")
    public String voice(@RequestParam(name = "msg") String msg) {
        // 保存路径
        String filePath = "d:\\" + UUID.randomUUID() + ".mp3";
        
        // 构建选项
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
            .model(BAILIAN_VOICE_MODEL)
            .voice(BAILIAN_VOICE_TIMBER)
            .text(msg)
            .build();

        // 调用 TTS
        TextToSpeechPrompt prompt = new TextToSpeechPrompt(msg, options);
        byte[] audioBytes = textToSpeechModel.call(prompt)
            .getResult()
            .getOutput();

        // 保存文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(audioBytes);
        }

        return filePath;
    }
}
```

### 2.2 流程解析

```
1. 准备文本和选项 (模型、音色)
          ↓
2. 构建 TextToSpeechPrompt
          ↓
3. 调用 textToSpeechModel.call()
          ↓
4. 发送请求到 DashScope
          ↓
5. 语音合成，返回 MP3 字节
          ↓
6. 保存为音频文件
```

## 课后练习

### 练习 1：基础语音合成

调用接口生成语音：

```
GET /t2v/voice?msg=支付宝到账100元，请注意查收
```

### 练习 2：返回音频流

修改接口，直接返回音频流而非保存文件：

```java
@GetMapping(value = "/t2v/stream", produces = "audio/mpeg")
public byte[] streamVoice(@RequestParam String msg) {
    DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
        .model(BAILIAN_VOICE_MODEL)
        .voice(BAILIAN_VOICE_TIMBER)
        .text(msg)
        .build();

    return textToSpeechModel.call(new TextToSpeechPrompt(msg, options))
        .getResult()
        .getOutput();
}
```

### 练习 3：前端播放

创建音频播放页面：

```html
<audio id="player" controls></audio>
<button onclick="play()">播放</button>

<script>
async function play() {
    const text = document.getElementById('text').value;
    const response = await fetch(`/t2v/stream?msg=${encodeURIComponent(text)}`);
    const blob = await response.blob();
    document.getElementById('player').src = URL.createObjectURL(blob);
}
</script>
```

### 练习 4：切换不同音色

创建音色选择功能：

```java
@GetMapping("/t2v/voice")
public String voice(
    @RequestParam String msg,
    @RequestParam(defaultValue = "longyingcui") String voice) {
    
    DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
        .model(BAILIAN_VOICE_MODEL)
        .voice(voice)
        .text(msg)
        .build();
    
    // ...
}
```

### 练习 5：语速和音量控制

添加更多选项：

```java
DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
    .model(BAILIAN_VOICE_MODEL)
    .voice(BAILIAN_VOICE_TIMBER)
    .text(msg)
    .speed(1.0)     // 语速 0.5-2.0
    .volume(1.0)    // 音量 0.1-10
    .pitch(0)       // 音调 -500-500
    .build();
```

## 下一步

- 学习 [05 - 文本向量化](./05-embedding.md)：Embedding 基础
- 学习 [12 - RAG 知识库](../04-rag/01-rag-vectorstore.md)：向量检索应用
