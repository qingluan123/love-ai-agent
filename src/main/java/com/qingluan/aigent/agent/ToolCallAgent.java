package com.qingluan.aigent.agent;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;


@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent {

    //可用的工具
    private final ToolCallback[] avaliabletools;
    //工具调用管理者
    private final ToolCallingManager toolCallingManager;
    //要调用工具调用结果的响应
    private ChatResponse toolCallChatResponse;
    //自主维护工具调用，无需自动调用
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] avaliabletools) {
        super();
        this.avaliabletools = avaliabletools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    @Override
    public boolean think() {
        //判断是否有下一步提示，有的话就加入到记忆列表当中
        if (getNextPrompt() != null && !getNextPrompt().isEmpty()) {
            UserMessage userMessage = new UserMessage(getNextPrompt());
            getMessageList().add(userMessage);
        }
        //获得对话记忆列表的信息
        List<Message> messages = getMessageList();
        //让LLM大模型根据提示词来看用什么工具
        Prompt prompt = new Prompt(messages, chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(avaliabletools)
                    .call()
                    .chatResponse();
            this.toolCallChatResponse = chatResponse;
            //得到助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //从助手消息中得到调用了哪些工具，以及助手响应的结果
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            String result = assistantMessage.getText();
            log.info("AI思考得到的结果" + result);
            log.info("AI思考选泽了" + toolCalls.size() + "个工具来调用");

            if (toolCalls.isEmpty()) {
                //把没有调用的工具添加到上下文中
                getMessageList().add(assistantMessage);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            log.error("调用工具失败出现了问题" + e.getMessage());
            getMessageList().add(new AssistantMessage(e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        //判断调用工具是否为空
        if (!toolCallChatResponse.hasToolCalls()) {
            return "没有工具调用";
        }
        //获得对话记忆
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        //使用工具调用管理器调用工具
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        //将这个消息添加到上下文当中
        setMessageList(toolExecutionResult.conversationHistory());
        // 当前工具调用的结果
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具 " + response.name() + " 完成了它的任务！结果: " + response.responseData())
                .collect(Collectors.joining("\n"));
        //判断是否调用终止工具
        boolean tool = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminate".contains(response.name()));
        if(tool){
            setStatus(AgentStatus.FINLISH);
        }
        log.info(results);
        return results;
    }
}

