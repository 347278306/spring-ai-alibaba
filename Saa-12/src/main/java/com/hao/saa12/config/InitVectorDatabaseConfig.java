package com.hao.saa12.config;

import cn.hutool.crypto.SecureUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class InitVectorDatabaseConfig {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("classpath:ops.txt")
    private Resource opsFile;

    @PostConstruct
    public void init() {
        // 1. 读取文件
        TextReader textReader = new TextReader(opsFile);
        textReader.setCharset(Charset.defaultCharset());

        // 2. 文件转为向量（开启分词）
        List<Document> documents = new TokenTextSplitter().transform(textReader.read());

        // 3. 存入向量数据库RedisStack
//        vectorStore.add(documents);

        // 4. 去重
        String sourceMetadata = (String) textReader.getCustomMetadata().get("source");
        String sourceMd5 = SecureUtil.md5(sourceMetadata);
        String redisKey = "vector-sourceMd5:" + sourceMd5;

        // 判断是否存入过
        Boolean retFlag = redisTemplate.opsForValue().setIfAbsent(redisKey, "1");

        if (Boolean.TRUE.equals(retFlag)) {
            vectorStore.add(documents);
        }

    }
}
