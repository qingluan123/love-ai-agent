package com.qingluan.aigent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Manus1 {

    @Resource
    private Manus manus;

    @Test
    void run1(){
        String userPrompt = """  
                生成一份‘国庆节约会计划’PDF，包含餐厅预订、活动流程和礼物清单""";
        String answer = manus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }

}
