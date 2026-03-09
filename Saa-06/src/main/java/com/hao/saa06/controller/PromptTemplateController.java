package com.hao.saa06.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
public class PromptTemplateController {
    @Resource(name = "deepSeekChatModel")
    private ChatModel deepSeekChatModel;

    @Resource(name = "qwenChatModel")
    private ChatModel qwenChatModel;

    @Resource(name = "deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    @Value("classpath:/prompt/template.txt")
    private org.springframework.core.io.Resource userTemplate;

    @GetMapping("chat1")
    public Flux<String> chat1(@RequestParam("topic") String topic,
                             @RequestParam("outputFormat") String outputFormat,
                             @RequestParam("wordCount") String wordCount){
        PromptTemplate promptTemplate = new PromptTemplate("""
                将一个关于{topic}的故事，
                以{outputFormat}格式返回，
                字数限制{wordCount}。
                """);

        Prompt prompt = promptTemplate.create(Map.of(
                "topic", topic,
                "outputFormat", outputFormat,
                "wordCount", wordCount
        ));
        return deepSeekChatClient.prompt(prompt).stream().content();
    }

    @GetMapping("chat2")
    public Flux<String> chat2(@RequestParam("topic") String topic,
                              @RequestParam("outputFormat") String outputFormat){
        PromptTemplate promptTemplate = new PromptTemplate(userTemplate);

        Prompt prompt = promptTemplate.create(Map.of(
                "topic", topic,
                "outputFormat", outputFormat
        ));
        return deepSeekChatClient.prompt(prompt).stream().content();
    }

    @GetMapping("chat3")
    public Flux<String> chat3(@RequestParam("topic") String sysTopic,
                              @RequestParam("outputFormat") String userTopic){
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("你是{systemTopic}助手，只回答{systemTopic}其它无可奉告，以HTML格式的结果。");
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("systemTopic", sysTopic));

        PromptTemplate userPromptTemplate = new PromptTemplate("解释一下{userTopic}");
        Message userMessage = userPromptTemplate.createMessage(Map.of("userTopic", userTopic));

        Prompt prompt = new Prompt(systemMessage, userMessage);
        return deepSeekChatClient.prompt(prompt).stream().content();
    }

    @GetMapping("chat4")
    public String chat4(@RequestParam("msg") String msg) {
        SystemMessage systemMessage = new SystemMessage("你是一个Java编程助手，拒绝回答非技术问题。");
        UserMessage userMessage = new UserMessage(msg);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return deepSeekChatModel.call(prompt).getResult().getOutput().getText();
    }
}
