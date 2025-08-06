package com.qingluan.aigent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
@Slf4j
public class KeyWordEnricher {

    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> keywordenrich (List<Document> doucument){
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel, 4);
        List<Document> enricherdoucuments = keywordMetadataEnricher.apply(doucument);
        return enricherdoucuments;
    }
}
