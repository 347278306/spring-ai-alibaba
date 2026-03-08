package com.hao.saa01.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Resource
    private ChatModel chatModel;

    @GetMapping(value = "/chat")
    public String chat(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg){
        return chatModel.call(msg);
    }

    @GetMapping(value = "/streamChat")
    public Flux<String> streamChat(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg){
        return chatModel.stream(msg);
    }
}
