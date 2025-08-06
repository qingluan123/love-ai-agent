package com.qingluan.aigent.agent;

import com.qingluan.aigent.advisor.Logadvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

@Component
public class Manus extends ToolCallAgent{


    public Manus(ToolCallback[] alltools, ChatModel dashscopeChatModel) {
        super(alltools);
        this.setName("Manus");
        String SYSTEM_PROMPT = """  
                You are YuManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                """;
        setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """  
                Based on user needs, proactively select the most appropriate tool or combination of tools.  
                For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                After using each tool, clearly explain the execution results and suggest the next steps.  
                If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                """;
        setNextPrompt(NEXT_STEP_PROMPT);
        setMax_step(15);
        //初始化对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new Logadvisor())
                .build();
        setChatClient(chatClient);
    }
}
