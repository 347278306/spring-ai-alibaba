package com.hao.saa04.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("stream")
public class StreamOutputController {

    @Resource(name = "deepSeekChatModel")
    private ChatModel deepSeekChatModel;

    @Resource(name = "qwenChatModel")
    private ChatModel qwenChatModel;

    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    @GetMapping("/chatModel/deepSeek")
    public Flux<String> deepSeekModel(@RequestParam(name = "question",defaultValue = "你是谁") String question){
        return deepSeekChatModel.stream(question);
    }

    @GetMapping("/chatModel/qwen")
    public Flux<String> qwenModel(@RequestParam(name = "question",defaultValue = "你是谁") String question){
        return qwenChatModel.stream(question);
    }

    @GetMapping("/chatClient/deepSeek")
    public Flux<String> deepSeekClient(@RequestParam(name = "question",defaultValue = "你是谁") String question){
        return deepSeekChatClient.prompt().user(question).stream().content();
    }

    @GetMapping("/chatClient/qwen")
    public Flux<String> qwenClient(@RequestParam(name = "question",defaultValue = "你是谁") String question){
        return qwenChatClient.prompt().user(question).stream().content();
    }
}
