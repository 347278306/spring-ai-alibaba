# 03 - 文生图

本模块介绍如何使用 Spring AI 和阿里云 DashScope 实现文本生成图像（Text-to-Image）。

## 知识点讲解

### 1.1 图像生成模型

阿里云 DashScope 提供多种图像生成模型：

| 模型                  | 说明   | 特点        |
|---------------------|------|-----------|
| `wanx2.1-t2i-turbo` | 快速生成 | 速度快，质量适中  |
| `wanx2.1-t2i-plus`  | 增强版  | 质量更高，生成稍慢 |

### 1.2 Spring AI 图像 API

```
┌─────────────────────────────────────────────────────────────┐
│                  Spring AI 图像模型                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ImageModel                                                │
│   ├── call(ImagePrompt) → ImageResponse                    │
│   │                                                            │
│   ├── ImagePrompt                                           │
│   │   ├── text (String)                                      │
│   │   └── options (ImageOptions)                             │
│   │                                                            │
│   └── ImageResponse                                          │
│       └── getResult().getOutput().getUrl()                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 ImageOptions 配置

```java
DashScopeImageOptions options = DashScopeImageOptions.builder()
    .model("wanx2.1-t2i-turbo")    // 模型
    .prompt("一只可爱的猫")           // 提示词
    .negativePrompt("模糊，低质量")  // 负面提示词
    .width(1024)                   // 宽度
    .height(1024)                  // 高度
    .n(1)                          // 生成数量
    .build();
```

## 源码解析

### 2.1 控制器源码

```java
@RestController
public class Text2ImageController {

    public static final String IMAGE_MODEL = "wanx2.1-t2i-turbo";

    @Resource
    private ImageModel imageModel;

    @GetMapping(value = "/t2i/image")
    public String image(@RequestParam(name = "prompt") String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(
            prompt,
            DashScopeImageOptions.builder()
                .model(IMAGE_MODEL)
                .build()
        );
        
        return imageModel.call(imagePrompt)
            .getResult()
            .getOutput()
            .getUrl();
    }
}
```

### 2.2 流程解析

```
1. 构建 ImagePrompt (文本 + 选项)
          ↓
2. 调用 imageModel.call()
          ↓
3. 发送请求到 DashScope
          ↓
4. 通义万相生成图像
          ↓
5. 返回图像 URL
```

## 课后练习

### 练习 1：基本图像生成

调用接口生成图像：

```
GET /t2i/image?prompt=一只可爱的猫咪
```

### 练习 2：添加负面提示词

修改代码，排除不想要的元素：

```java
@GetMapping("/t2i/image")
public String image(
    @RequestParam String prompt,
    @RequestParam(required = false) String negative) {
    
    ImagePrompt imagePrompt = new ImagePrompt(
        prompt,
        DashScopeImageOptions.builder()
            .model(IMAGE_MODEL)
            .negativePrompt(negative != null ? negative : "模糊，低质量，变形")
            .build()
    );
    
    return imageModel.call(imagePrompt).getResult().getOutput().getUrl();
}
```

### 练习 3：批量生成

生成多张图像：

```java
@GetMapping("/t2i/images")
public List<String> images(@RequestParam String prompt) {
    ImagePrompt imagePrompt = new ImagePrompt(
        prompt,
        DashScopeImageOptions.builder()
            .model(IMAGE_MODEL)
            .n(4)  // 生成4张
            .build()
    );
    
    ImageResponse response = imageModel.call(imagePrompt);
    return response.getResults().stream()
        .map(r -> r.getOutput().getUrl())
        .toList();
}
```

### 练习 4：前端展示

创建前端页面展示生成的图像：

```html
<img id="image" src="" style="max-width: 500px;">
<button onclick="generate()">生成图像</button>

<script>
async function generate() {
    const prompt = document.getElementById('prompt').value;
    const response = await fetch(`/t2i/image?prompt=${prompt}`);
    const url = await response.text();
    document.getElementById('image').src = url;
}
</script>
```

### 练习 5：保存到本地

将生成的图像保存到服务器：

```java
@GetMapping("/t2i/save")
public String saveImage(@RequestParam String prompt) throws IOException {
    ImageResponse response = imageModel.call(
        new ImagePrompt(prompt, DashScopeImageOptions.builder().model(IMAGE_MODEL).build())
    );
    
    String imageUrl = response.getResult().getOutput().getUrl();
    
    // 下载图像
    byte[] imageBytes = new RestTemplate().getForObject(imageUrl, byte[].class);
    
    // 保存到本地
    String fileName = UUID.randomUUID() + ".png";
    Path path = Paths.get("images", fileName);
    Files.write(path, imageBytes);
    
    return path.toString();
}
```

## 下一步

- 学习 [04 - 文生语音](./04-text2voice.md)：语音合成
- 学习 [05 - 文本向量化](./05-embedding.md)：Embedding
