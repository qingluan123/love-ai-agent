package com.qingluan.aigent.agent;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Slf4j
public abstract class BaseAgent {

    //智能体的名字
    private String name;

    //提示词信息
    private String systemPrompt;
    private String nextPrompt;

    //执行状态
    private AgentStatus status = AgentStatus.FREE;

    //LLM客户端交互
    private ChatClient chatClient;

    //执行的步骤
    private int max_step = 10;
    private int current_step = 0;

    //记忆上下文传递的信息
    private List<Message> messageList = new ArrayList<>();

    public String run(String userprompt) {
        //判断当前状态是否为空闲状态
        if (this.status != AgentStatus.FREE) {
            throw new RuntimeException("Could Not RUN Beacuse the status :" + this.status);
        }

        //判断用户传输的提示词是否为空
        if (StrUtil.isBlank(userprompt)) {
            throw new RuntimeException("Userprompt Not Null");
        }

        //更改状态为运行状态
        this.status = AgentStatus.RUNNING;
        //把用户的信息记录到上下文当中保存
        UserMessage userMessage = new UserMessage(userprompt);
        messageList.add(userMessage);
        //定义这个集合来接收执行结果
        List<String> results = new ArrayList<>();

        try {
            for (int i = 0; i < max_step && status != AgentStatus.FINLISH; i++) {
                int start_step = i + 1;
                current_step = start_step;
                log.info("Excuting step:" + start_step + "/" + max_step);
                //执行当前步骤的结果
                String stepresult = step();
                log.info("step:" + start_step + stepresult);
                //将当前步骤的结果添加到集合中
                results.add(stepresult);
            }
            if (current_step >= max_step) {
                status = AgentStatus.FINLISH;
                log.info("达到了最大步骤" + max_step);
            }
        } catch (Exception e) {
            status = AgentStatus.ERROR;
            log.error("Error executing agent", e);
            return "执行中发生错误" + e.getMessage();
        }
        return StrUtil.join(",", results);
    }

    public abstract String step();


    public SseEmitter runstream(String userprompt) {
        //创建SseEmitter对象
        SseEmitter sseEmitter = new SseEmitter(120000L);

        //防止主线程阻塞开启一个子线程来执行
        CompletableFuture.runAsync(() -> {
            try {
                //判断当前状态是否为空闲状态
                if (this.status != AgentStatus.FREE) {
                    sseEmitter.send("无法从状态运行代理" + this.status);
                    sseEmitter.complete();
                    return;
                }

                //判断用户传输的提示词是否为空
                if (StrUtil.isBlank(userprompt)) {
                    sseEmitter.send("不能输入空提示词");
                    sseEmitter.complete();
                    return;
                }

                //更改状态为运行状态
                this.status = AgentStatus.RUNNING;
                //把用户的信息记录到上下文当中保存
                UserMessage userMessage = new UserMessage(userprompt);
                messageList.add(userMessage);
                //定义这个集合来接收执行结果
//           List<String> results = new ArrayList<>();

                try {
                    for (int i = 0; i < max_step && status != AgentStatus.FINLISH; i++) {
                        int start_step = i + 1;
                        current_step = start_step;
                        log.info("Excuting step:{}/{}",start_step ,max_step);
                        //执行当前步骤的结果
                        String stepresult = step();
                        log.info("step:" + start_step + ":" + stepresult);
                        //将当前步骤的结果添加到集合中
//                   results.add(stepresult);
                        //直接把消息发送出去
                        sseEmitter.send(stepresult);
                    }
                    if (current_step >= max_step) {
                        status = AgentStatus.FINLISH;
                        sseEmitter.send("达到了最大步骤：" + max_step);
                    }
                    sseEmitter.complete();
                } catch (Exception e) {
                    status = AgentStatus.ERROR;
                    log.error("Error executing agent", e);
                    try {
                        sseEmitter.send("发送出现了异常" + e.getMessage());
                        sseEmitter.complete();
                    } catch (IOException ex) {
                        sseEmitter.completeWithError(ex);
                    }
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }

        });

        sseEmitter.onTimeout(()->{
            this.status = AgentStatus.ERROR;
            log.error("执行超时");

        });

        sseEmitter.onCompletion(()->{
           if(this.status == AgentStatus.RUNNING){
               status = AgentStatus.FINLISH;
           }
           log.info("sse执行完成");
        });

        return sseEmitter;
    }
}
