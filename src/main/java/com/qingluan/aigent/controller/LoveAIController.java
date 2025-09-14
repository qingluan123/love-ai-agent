package com.qingluan.aigent.controller;


import com.qingluan.aigent.agent.Manus;
import com.qingluan.aigent.app.Loveapp;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class LoveAIController {

    @Resource
    private Loveapp loveapp;

    @Resource
    private ToolCallback[] alltools;

    @Resource
    private ChatModel dashscopeChatModel;


    @Operation(summary = "同步发送信息")
    @GetMapping("/chat/sync")
    public String dochatwithsync(String message, String chatId) {
        return loveapp.dochatwithrag(message, chatId);
    }

    @Operation(summary = "异步发送消息")
    @GetMapping(value = "/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> dochatwithsse(String message, String chatId) {
        return loveapp.dochatwithragstream(message, chatId);
    }

    @Operation(summary = "SseEmitter异步发送消息//a")
    @GetMapping("/chat/sseemitter")
    public SseEmitter dochatwithsseemitter(String message, String chatId) {
        SseEmitter sseEmitter = new SseEmitter(120000L);

        loveapp.dochatwithragstream(message, chatId)
                .subscribe(
                        chunk -> {
                            try {
                                sseEmitter.send(chunk);
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        //处理错误信息
                        sseEmitter::completeWithError,
                        //处理完成
                        sseEmitter::complete
                        );

        return sseEmitter;
    }

    @Operation(summary = "智能体思考同步消息")
    @GetMapping("/chatManus/sync")
    public String dochatwithmanussync (String userprompt){
        //每次对话都是一个实例
        Manus manus = new Manus(alltools,dashscopeChatModel);
        //同步消息
        return manus.run(userprompt);
    }

    @Operation(summary = "智能体思考异步消息")
    @GetMapping("/chatManus/sseemiter")
    public SseEmitter dochatwithmanussse (String userprompt){
        //每次对话都是一个实例
        Manus manus = new Manus(alltools,dashscopeChatModel);
        //异步消息消息
        return manus.runstream(userprompt);
    }


}
