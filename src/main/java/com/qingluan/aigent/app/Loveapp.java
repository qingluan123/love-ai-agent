package com.qingluan.aigent.app;


import com.qingluan.aigent.advisor.Logadvisor;
import com.qingluan.aigent.chatmemory.FileBaseChatMemory;
import com.qingluan.aigent.rag.LoveAppCustomFactroy;
import com.qingluan.aigent.rag.LoveAppVectorStoreConfig;
import com.qingluan.aigent.rag.QueryRewriter;
import com.qingluan.aigent.rag.QueryTranslation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class Loveapp {

    private static final String SYSTEM_PROMPT;

    static {
        ClassPathResource classPathResource = new ClassPathResource("SystemPrompt.txt");
        try(BufferedReader Reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()))){
            SYSTEM_PROMPT = Reader.lines().collect(Collectors.joining("/n"));
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("读取 SystemPrompt.txt 失败", e);
        }
    }

    private final ChatClient chatClient;
    /**
     * 基于RAG知识库增强Ai的回复能力
     * @param message
     * @param chatId
     * @return
     */
    //使用本地知识库
    @Resource
    private VectorStore loveappvectorstore;
    //使用云平台储存知识库
    @Resource
    private Advisor loveAppCloudRag;
    //基于PGVector储存知识库
    @Resource
    private VectorStore pgVectorStore;
    @Resource
    private QueryTranslation queryTranslation;
    @Resource
    private QueryRewriter queryRewriter;
    @Resource
    private ToolCallback[] alltools;
    @Resource
    private ToolCallbackProvider toolCallbackProvider;
    public Loveapp(ChatModel dashscopeChatModel) {
        //定义一个基于内存存储的chatmemory
//        String dir = System.getProperty("user.dir") + "/chat-memory";
//        FileBaseChatMemory fileChatMemory = new FileBaseChatMemory(dir);
        //定义一个基于内存存储的chatmemory
        ChatMemory chatMemory = new InMemoryChatMemory();
        //初始化一个客户端
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        //自定义日志拦截器
                        new Logadvisor()
                )
                .build();
    }

    /**
     * //使Ai具有聊天记忆
     * @param message
     * @param chatId
     * @return
     */
    public String dochat(String message,String chatId){
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                ).call()
                .chatResponse();
        String text = response.getResult().getOutput().getText();
        log.info("text {}",text);
        return text;
    }

    public LoveReport dochatwithreport(String message,String chatId){
        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话后给我生成标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(
                        advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                                .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)
                ).call()
                .entity(LoveReport.class);

        log.info("loveReport : {}",loveReport);
        return loveReport;
    }

    public String dochatwithrag(String message,String chatId){
        //重写提示词增强语义
//        String rewriteprompt = queryRewriter.doqueryRewrite(message);
        //将提示词的语言转化
//        String translatemessage = queryTranslation.translationQuery(message);
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new Logadvisor())
                //基于本地内存的RAG知识库
                //.advisors(new QuestionAnswerAdvisor(loveappvectorstore))
                //基于云服务的RAG知识库
                .advisors(loveAppCloudRag)
                //基于PGVector的知识库
                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                //过滤元信息的查询的RAG知识库
                //.advisors(LoveAppCustomFactroy.creatCustomAdvisor(loveappvectorstore,"已婚"))
                .call()
                .chatResponse();
        String text = response.getResult().getOutput().getText();
        log.info("text : {}",text);
        return text;
    }

    /**
     * 流式输出结果
     * @param message
     * @param chatId
     * @return
     */

    public Flux<String> dochatwithragstream(String message,String chatId){
        //重写提示词增强语义
        String rewriteprompt = queryRewriter.doqueryRewrite(message);
        //将提示词的语言转化
//        String translatemessage = queryTranslation.translationQuery(message);
        Flux<String> content = chatClient.prompt()
                .user(rewriteprompt)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 20))
                .advisors(new Logadvisor())
                //基于本地内存的RAG知识库
                .advisors(new QuestionAnswerAdvisor(loveappvectorstore))
                //基于云服务的RAG知识库
                //.advisors(loveAppCloudRag)
                //基于PGVector的知识库
                //.advisors(new QuestionAnswerAdvisor(pgVectorStore))
                //过滤元信息的查询的RAG知识库
                //.advisors(LoveAppCustomFactroy.creatCustomAdvisor(loveappvectorstore,"已婚"))
                .tools(alltools)
                .stream()
                .content();
        log.info("content : {}",content);
        return content;
    }

    /**
     * 给AI提供工具使用
     * @param message
     * @param chatId
     * @return
     */

    public String dochatwithtool(String message,String chatId){
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new Logadvisor())
                //基于本地内存的RAG知识库
                .advisors(new QuestionAnswerAdvisor(loveappvectorstore))
                .tools(alltools)
                .call()
                .chatResponse();

        String text = response.getResult().getOutput().getText();
        log.info("result : {}",text);
        return text;
    }

    public String dochatwithmcptool(String message,String chatId){
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new Logadvisor())
                //基于本地内存的RAG知识库
                .advisors(new QuestionAnswerAdvisor(loveappvectorstore))
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();

        String text = response.getResult().getOutput().getText();
        log.info("result : {}",text);
        return text;
    }

    /**
     * 将Ai回答进行结构化输出为一个对象
     * @return
     */
    record LoveReport(String title, List<String> suggestions){

    }
}
