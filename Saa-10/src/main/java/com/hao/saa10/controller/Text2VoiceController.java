package com.hao.saa10.controller;


import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioSpeechOptions;
import jakarta.annotation.Resource;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.util.UUID;

@RestController
public class Text2VoiceController {

    public static final String BAILIAN_VOICE_MODEL = "cosyvoice-v2";
    public static final String BAILIAN_VOICE_TIMBER = "longyingcui";

    @Resource
    private TextToSpeechModel textToSpeechModel;

    @GetMapping(value = "/t2v/voice")
    public String voice(@RequestParam(name = "msg", defaultValue = "温馨提醒，支付宝到账100元请注意查收") String msg) {
        String filePath = "d:\\" + UUID.randomUUID() + ".mp3";
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                .model(BAILIAN_VOICE_MODEL)
                .voice(BAILIAN_VOICE_TIMBER)
                .text(msg)
                .build();

        TextToSpeechPrompt prompt = new TextToSpeechPrompt(msg, options);
        byte[] output = textToSpeechModel.call(prompt).getResult().getOutput();

        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            fileOutputStream.write(output);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }
}
