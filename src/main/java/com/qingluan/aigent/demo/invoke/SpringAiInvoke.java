package com.qingluan.aigent.demo.invoke;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.qingluan.AiAngentApplication;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;


    @Override
    public void run(String... args) throws Exception {
            ChatResponse response = dashscopeChatModel.call(new Prompt("你好你是谁"));
            AssistantMessage output = response.getResult().getOutput();
            log.info("output:{}",output);
    }
}
