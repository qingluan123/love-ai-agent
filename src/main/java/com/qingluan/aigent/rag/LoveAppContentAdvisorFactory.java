package com.qingluan.aigent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

public class LoveAppContentAdvisorFactory {

    public static ContextualQueryAugmenter createInstance(){
        PromptTemplate promptTemplate = new PromptTemplate("""
                你应该输出内容如下:
                抱歉我只能回答与恋爱相关的知识问题，别的没办法帮到您。
                """);

        return ContextualQueryAugmenter.builder()
                .emptyContextPromptTemplate(promptTemplate)
                .allowEmptyContext(false)
                .build();
    }
}
