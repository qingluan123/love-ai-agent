package com.qingluan.aigent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.stereotype.Component;

@Component
public class QueryTranslation {

    private final TranslationQueryTransformer translationQueryTransformer;

    public QueryTranslation (ChatModel dashscopeChatModel){
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);

        translationQueryTransformer =  TranslationQueryTransformer.builder()
                .chatClientBuilder(builder)
                .targetLanguage("chinese")
                .build();
    }

    //转化翻译器
    public String translationQuery(String prompt){
        Query query = new Query(prompt);
        //转化语言
        Query transformprompt = translationQueryTransformer.transform(query);
        //返回查询
        return transformprompt.text();
    }
}
