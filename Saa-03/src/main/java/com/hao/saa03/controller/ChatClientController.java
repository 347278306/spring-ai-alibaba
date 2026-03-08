package com.hao.saa03.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chatClient")
public class ChatClientController {

    private final ChatClient chatClient;

    public ChatClientController(ChatModel chatModel){
        this.chatClient =ChatClient.builder(chatModel).build();
    }

    @GetMapping("call")
    public String call(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg){
        return chatClient.prompt().user(msg).call().content();
    }
}
