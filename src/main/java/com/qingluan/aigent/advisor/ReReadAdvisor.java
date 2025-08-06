package com.qingluan.aigent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;

public class ReReadAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    @Override
    public String getName() {
        return "重复读取拦截器";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private AdvisedRequest before(AdvisedRequest advisedRequest){
        HashMap<String,Object> map = new HashMap<>();
        map.put("re2_input_query",advisedRequest.userText());

        return AdvisedRequest.from(advisedRequest)
                .userText("""
                        {re2_input_query}
                        Read the question again : {re2_input_query}
                        """
                )
                .userParams(map)
                .build();
    }
}
