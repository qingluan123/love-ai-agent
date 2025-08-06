package com.qingluan.aigent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;


public class LoveAppCustomFactroy {

    public static Advisor creatCustomAdvisor(VectorStore vectorStore,String status){
        //过滤的条件
        Filter.Expression build = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        DocumentRetriever build1 = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(build)
                .similarityThreshold(0.5)
                .topK(5)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(build1)
                .queryAugmenter(LoveAppContentAdvisorFactory.createInstance())
                .build();
    }
}
