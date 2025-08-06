package com.qingluan.aigent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class FileBaseChatMemory implements ChatMemory {

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);

        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    private final String BASE_DIR;

    public FileBaseChatMemory(String dir) {
        this.BASE_DIR = dir;
        File file = new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        //获得信息的集合
        List<Message> conversation = getConversation(conversationId);
        //往这个集合里面添加信息
        conversation.addAll(messages);
        //再保存起来
        saveConversation(conversationId,conversation);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        //获得消息集合
        List<Message> conversation = getConversation(conversationId);
        //获得最新的消息
        return conversation.stream().skip(Math.max(0,conversation.size()-lastN)).toList();
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if(file.exists()){
            file.delete();
        }
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        //获得文件对象
        File file = getConversationFile(conversationId);
        //将信息写入文件中
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Message> getConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        //创建一个集合接收消息
        List<Message> messages = new ArrayList<>();
        //读取里面的消息
        if(file.exists()){
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messages;
    }
}
