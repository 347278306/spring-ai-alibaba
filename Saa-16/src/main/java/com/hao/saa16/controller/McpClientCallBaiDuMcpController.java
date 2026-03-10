package com.hao.saa16.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class McpClientCallBaiDuMcpController {

    @Resource
    private ChatClient chatClient;

    @Resource
    private ChatModel chatModel;

    @GetMapping("mcpclient/chat")
    public Flux<String> chat(@RequestParam(name = "msg", defaultValue = "查询61.149.121.66归属地") String msg) {
        return chatClient.prompt(msg)
                .stream()
                .content();
    }

    @GetMapping("mcpclient/chat2")
    public Flux<String> chat2(@RequestParam(name = "msg", defaultValue = "查询61.149.121.66归属地") String msg) {
        return chatModel.stream(msg);
    }
}
