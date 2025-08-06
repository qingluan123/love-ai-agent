package com.qingluan.aigent.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class SearchToolTest {
    @Value("${search-api.api-key}")
    private String apikey;

    @Test
    void webSearch() {
        SearchTool searchTool = new SearchTool(apikey);
        String result = searchTool.webSearch("请帮我查询关于广东东莞情侣约会的地方");
        Assertions.assertNotNull(result);
        log.info("result :{}",result);
    }
}