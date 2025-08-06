package com.qingluan.aigent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveappTest {

    @Resource
    private Loveapp loveapp;
    @Test
    void dochat() {
        //第一轮对话
        //String dochat = loveapp.dochat("你好，你是谁","1");
        //第二轮对话
        String dochat =  loveapp.dochat("我是小白","1");
        Assertions.assertNotNull(dochat);
        //第三轮对话
        dochat = loveapp.dochat("我的另一半是小红,我们感情出现了隔阂，改如何解决","1");
        Assertions.assertNotNull(dochat);
        //dochat =  loveapp.dochat("我是单身我遇到了问题，不敢追求我喜欢的异性,你有什么办法帮我追求吗","1");
        //第四轮对话
        dochat = loveapp.dochat("请问我的另一半是谁,刚刚跟你说了你帮我回忆一下","1");
        Assertions.assertNotNull(dochat);
    }


    @Test
    void dochatwithreport() {
        //第一轮对话
        Loveapp.LoveReport report = loveapp.dochatwithreport("你好，我是小白，给我生成我跟小红的恋爱报告", "2");
        Assertions.assertNotNull(report);
    }

    @Test
    void dochatwithrag() {
        //对话测试
        String chatid = UUID.randomUUID().toString();
        String message = "How to Maintain My Relationship with My Wife";
        String dochatwithrag = loveapp.dochatwithrag(message, chatid);
        Assertions.assertNotNull(dochatwithrag);
    }

    @Test
    void dochatwithCloudRag() {
        //对话测试
        String chatid = UUID.randomUUID().toString();
        String message = "我已婚了，我跟妻子有争吵，怎么维护我们之间的关系";
        String dochatwithrag = loveapp.dochatwithrag(message, chatid);
        Assertions.assertNotNull(dochatwithrag);
    }


    @Test
    void testDochatwithPGVectorrag() {
        String chatid = UUID.randomUUID().toString();
        String message = "我已婚了，我跟妻子有争吵，怎么维护我们之间的关系";
        String dochatwithrag = loveapp.dochatwithrag(message, chatid);
        Assertions.assertNotNull(dochatwithrag);
    }

    @Test
    void dochatwithragrewrite() {
        //对话测试
        String chatid = UUID.randomUUID().toString();
        String message = "我和妻子发生了冲突，怎么办";
        String dochatwithrag = loveapp.dochatwithrag(message, chatid);
        Assertions.assertNotNull(dochatwithrag);
    }

    @Test
    void dochatwithragfilter() {
        //对话测试
        String chatid = UUID.randomUUID().toString();
        String message = "我单身，请给我关于怎么约会的信息";
        String dochatwithrag = loveapp.dochatwithrag(message, chatid);
        Assertions.assertNotNull(dochatwithrag);
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        //testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地,在网站上搜索");

        // 测试资源下载：图片下载
        //testMessage("帮我在网上搜索一张适合做手机壁纸的星空情侣图片，再下载为.jpg格式");

        // 测试文件操作：保存用户档案
        //testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘情人节约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveapp.dochatwithtool(message, chatId);
        Assertions.assertNotNull(answer);
    }



    @Test
    void dochatwithmcptool() {
        String chatId = UUID.randomUUID().toString();
        String message = "帮我在网上下载一张关于花的图片";
        String answer = loveapp.dochatwithtool(message, chatId);
        Assertions.assertNotNull(answer);
    }
}