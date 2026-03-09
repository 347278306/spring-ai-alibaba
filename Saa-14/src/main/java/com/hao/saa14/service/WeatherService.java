package com.hao.saa14.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WeatherService {

    @Tool(description = "根据城市获取天气")
    public String getWeatherByCity(String city) {
        Map<String, String> map = Map.of(
                "北京", "晴天",
                "上海", "阴天",
                "广州", "小雨",
                "深圳", "多云转晴"
        );

        return map.getOrDefault(city, "抱歉，未查询到对应城市");
    }
}
