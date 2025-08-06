package com.qingluan.aigent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenTextSplit {

    public List<Document> textSplit(List<Document> documents){
        TokenTextSplitter split = new TokenTextSplitter();
        return split.apply(documents);
    }

    public List<Document> textSplits(List<Document> documents){
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(800,500,5,
                10000,true);
        return tokenTextSplitter.apply(documents);
    }
}
