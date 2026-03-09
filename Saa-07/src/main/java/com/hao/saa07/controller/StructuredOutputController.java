package com.hao.saa07.controller;

import com.hao.saa07.records.StrudentRecord;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

@RestController
public class StructuredOutputController {

    @Resource(name = "qwenChatClient")
    private ChatClient qwenChatClient;

    @GetMapping("chat1")
    public StrudentRecord chat1(@RequestParam("name") String name, @RequestParam("email") String email) {
        return qwenChatClient.prompt().user(new Consumer<ChatClient.PromptUserSpec>() {
            @Override
            public void accept(ChatClient.PromptUserSpec promptUserSpec) {
                promptUserSpec.text("学号1001，我叫{name}，大学计算机专业，邮箱{email}")
                        .param("name", name)
                        .param("email", email);
            }
        }).call().entity(StrudentRecord.class);
    }

    @GetMapping("chat2")
    public StrudentRecord chat2(@RequestParam("name") String name, @RequestParam("email") String email) {
        String stringTemplate = """
                学号1002，我叫{name}，大学计算机专业，邮箱{email}
                """;
        return qwenChatClient.prompt().user(promptUserSpec -> promptUserSpec.text(stringTemplate)
                .param("name", name)
                .param("email", email)).call().entity(StrudentRecord.class);
    }

}
