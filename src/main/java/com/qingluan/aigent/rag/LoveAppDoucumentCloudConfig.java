package com.qingluan.aigent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoveAppDoucumentCloudConfig {

    @Bean
    public Advisor loveAppCloudRag() {
        //获取apikey
        DashScopeApi dash_score_api_key = new DashScopeApi(System.getenv("DASH_SCORE_API_KEY"));
        //RAG知识库ID
        String name = "my-rag";
        //连接云服务上的RAG知识库
        DocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dash_score_api_key,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(name)
                        .build());

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
}
