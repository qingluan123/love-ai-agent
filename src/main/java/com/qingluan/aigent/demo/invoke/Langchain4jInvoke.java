package com.qingluan.aigent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import jakarta.annotation.Resource;

public class Langchain4jInvoke {

    public static void main(String[] args) {
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .apiKey(Test.API_KEY)
                .modelName("qwen-plus")
                .build();
        String anwser = qwenChatModel.chat("你好你是谁");
        System.out.println(anwser);
    }
}
