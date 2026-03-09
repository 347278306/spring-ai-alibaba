package com.hao.saa09.controller;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Text2ImageController {

    public static final String IMAGE_MODEL = "wanx2.1-t2i-turbo";

    @Resource
    private ImageModel imageModel;

    @GetMapping(value = "/t2i/image")
    public String image(@RequestParam(name = "prompt", defaultValue = "刺猬") String prompt) {
        return imageModel.call(
                        new ImagePrompt(prompt, DashScopeImageOptions.builder().model(IMAGE_MODEL).build())
                ).getResult()
                .getOutput()
                .getUrl();
    }
}
