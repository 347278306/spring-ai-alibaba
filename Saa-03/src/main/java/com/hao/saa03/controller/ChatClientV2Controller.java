package com.hao.saa03.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("chatClient/v2")
public class ChatClientV2Controller {

    @Resource
    private ChatClient chatClient;


    @GetMapping("call")
    public String call(@RequestParam(name = "msg", defaultValue = "你是谁？") String msg){
        return chatClient.prompt().user(msg).call().content();
    }
}
