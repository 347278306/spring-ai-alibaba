package com.hao.saa13.controller;

import com.hao.saa13.utils.DateTimeTools;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ToolCallingController {

    @Resource
    private ChatModel chatModel;

    @Resource
    private ChatClient chatClient;


    @GetMapping("/toolcall/chat")
    public String chat(@RequestParam(name = "msg", defaultValue = "你是谁现在几点") String msg) {
        // 1. 将工具注册到工具集中
        ToolCallback[] tools = ToolCallbacks.from(new DateTimeTools());

        // 2. 将工具集配置进ChatOptions对象
        ToolCallingChatOptions callingChatOptions = ToolCallingChatOptions.builder().toolCallbacks(tools).build();

        // 3. 构件提示词
        Prompt prompt = new Prompt(msg, callingChatOptions);

        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    @GetMapping("/toolcall/chat2")
    public Flux<String> chat2(@RequestParam(name = "msg", defaultValue = "你是谁现在几点") String msg) {
        return chatClient.prompt(msg)
                .tools(new DateTimeTools())
                .stream()
                .content();
    }
}
