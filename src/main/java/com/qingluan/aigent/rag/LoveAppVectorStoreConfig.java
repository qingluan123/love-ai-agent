package com.qingluan.aigent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;

@Configuration
@Slf4j
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private TokenTextSplit tokenTextSplit;

    @Resource
    private KeyWordEnricher keyWordEnricher;


    @Bean
    VectorStore loveappvectorstore(EmbeddingModel dashscoreEmbeddingModel){
        //构造一个简单的向量数据库，嵌入向量转化模型
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscoreEmbeddingModel)
                .build();
        //读取文件
        List<Document> documentloader = loveAppDocumentLoader.documentloader();
        //将文本切片
//        List<Document> documents = tokenTextSplit.textSplit(documentloader);

        //增强文本的元信息
        List<Document> keywordenrichdocument = keyWordEnricher.keywordenrich(documentloader);
        //将文件放在向量数据库中
        simpleVectorStore.doAdd(keywordenrichdocument);
        //返回一个向量数据库
        return simpleVectorStore;
    }
}
