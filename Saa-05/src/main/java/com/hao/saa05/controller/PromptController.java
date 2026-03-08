package com.hao.saa05.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
public class PromptController {

    @Resource(name = "deepSeekChatModel")
    private ChatModel deepSeekChatModel;

    @Resource(name = "qwenChatModel")
    private ChatModel qwenChatModel;

    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    @GetMapping("chat1")
    public Flux<String> chat1(@RequestParam(name = "msg",defaultValue = "你是谁") String msg){
        return deepSeekChatClient.prompt()
                .system("你是一个法律助手，只能回答法律相关知识，其他问题回复，无可奉告")
                .user(msg)
                .stream()
                .content();
    }

    @GetMapping("chat2")
    public Flux<ChatResponse> chat2(@RequestParam(name = "msg",defaultValue = "你是谁") String msg){
        SystemMessage systemMessage = new SystemMessage("你是一个法律助手，只能回答法律相关知识，其他问题回复，无可奉告");
        UserMessage userMessage = new UserMessage(msg);
        Prompt prompt = new Prompt(systemMessage, userMessage);
        return deepSeekChatModel.stream(prompt);
    }

    @GetMapping("chat3")
    public Flux<String> chat3(@RequestParam(name = "msg",defaultValue = "你是谁") String msg){
        SystemMessage systemMessage = new SystemMessage("你是一个法律助手，只能回答法律相关知识，其他问题回复，无可奉告");
        UserMessage userMessage = new UserMessage(msg);
        Prompt prompt = new Prompt(systemMessage, userMessage);
        return deepSeekChatModel.stream(prompt).mapNotNull(response -> response.getResults().get(0).getOutput().getText());
    }

    @GetMapping("chat4")
    public String chat4(@RequestParam(name = "msg",defaultValue = "你是谁") String msg){
        return deepSeekChatClient.prompt()
                .user(msg)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();
    }

    /*@GetMapping("chat5")
    public String chat5(@RequestParam(name = "msg",defaultValue = "北京") String city){
        String answer = deepSeekChatClient.prompt()
                .user(city + "未来3天的天气如何？")
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();

        String toolResponse = new ToolResponseMessage(List.of(
                new ToolResponseMessage.ToolResponse("1", "获取天气", city))).getText();

        return answer + toolResponse;
    }*/
}
