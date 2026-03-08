package com.hao.saa02.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("ollama")
public class OllamaController {

    // 方式1
    /*@Resource(name = "ollamaChatModel")
    private ChatModel chatModel;*/

    // 方式2
    @Resource
    @Qualifier("ollamaChatModel")
    private ChatModel chatModel;

    @GetMapping("chat")
    public String chat(@RequestParam(name = "msg", defaultValue = "你是谁") String msg){
        return chatModel.call(msg);
    }

    @GetMapping("streamChat")
    public Flux<String> streamChat(@RequestParam(name = "msg", defaultValue = "你是谁") String msg){
        return chatModel.stream(msg);
    }
}
