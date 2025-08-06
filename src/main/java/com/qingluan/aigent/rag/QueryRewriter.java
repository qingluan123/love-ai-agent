package com.qingluan.aigent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

@Component
public class QueryRewriter {

    private final RewriteQueryTransformer rewriteQueryTransformer;

    public QueryRewriter (ChatModel dashscopeChatModel){
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);

        rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }



    public String doqueryRewrite(String prompt){
        Query query = new Query(prompt);
        //执行重写
        Query rewriteprompt = rewriteQueryTransformer.transform(query);
        //返回重写
        return rewriteprompt.text();
    }

}
