package com.qingluan.aigent.demo.invoke;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import java.util.ArrayList;
import java.util.List;

public class HuoshanInvoke{
    public static void main(String[] args) {
        String apiKey = "f8b6b05b-535c-46e2-ae23-920e8383ce49";
        ArkService service = ArkService.builder().apiKey(apiKey).build();
        System.out.println("----- standard request -----");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("doubao-seed-1-6-250615")// 替换 <MODEL> 为模型的Model ID , 查询Model ID：https://www.volcengine.com/docs/82379/1330310
                .messages(messages)
                .build();
        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> System.out.println(choice.getMessage().getContent()));
        // shutdown service
        service.shutdownExecutor();
    }
}