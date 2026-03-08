package com.hao.saa01.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaaLLMConfig {

    /**
     * 方式1:${}
     * 持有yml文件配置：spring.ai.dashscope.api-key=${aliQwen-api}
     */
/*    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public DashScopeApi dashScopeApi(){
        return DashScopeApi.builder().apiKey(apiKey).build();
    }*/

    /**
     * 方式2:System.getenv("环境变量")
     * 持有yml文件配置：spring.ai.dashscope.api-key=${aliQwen-api}
     * @return
     */
    @Bean
    public DashScopeApi dashScopeApi(){
        return DashScopeApi.builder().apiKey(System.getenv("DASHSCOPE_API_KEY")).build();
    }
}
