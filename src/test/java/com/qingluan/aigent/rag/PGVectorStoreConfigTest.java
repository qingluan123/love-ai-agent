package com.qingluan.aigent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PGVectorStoreConfigTest {

    @Resource
    VectorStore pgVectorStore;

    @Test
    void pgVectorStore() {


        List<Document> documents = List.of(
                new Document("我是小红，我是小白的恋人", Map.of("meta1", "meta1")),
                new Document("我是小白"),
                new Document("小红是大美女", Map.of("meta2", "meta2")));


        pgVectorStore.add(documents);


        List<Document> results = this.pgVectorStore.similaritySearch(SearchRequest.builder().query("小红").topK(5).build());
        Assertions.assertNotNull(results);
        log.info("results : {}",results);

    }
}