package com.hao.saa18.controller;

import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgent;
import com.alibaba.cloud.ai.dashscope.agent.DashScopeAgentOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MenuCallAgentController {

    @Value("${spring.ai.dashscope.agent.options.app-id}")
    private String appId;

    @Resource
    private DashScopeAgent dashScopeAgent;

    @GetMapping(value = "/eatAgent")
    public String eatAgent(@RequestParam(name = "msg", defaultValue = "今天吃什么") String msg) {
        DashScopeAgentOptions agentOptions = DashScopeAgentOptions.builder()
                .appId(appId)
                .build();

        Prompt prompt = new Prompt(msg, agentOptions);
        return dashScopeAgent.call(prompt).getResult().getOutput().getText();
    }
}
