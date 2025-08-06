package com.qingluan.aigent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class Logadvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    @Override
    public String getName() {
        return "自定义的Advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);

        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);

        this.after(advisedResponse);

        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);

        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);


        return new MessageAggregator().aggregateAdvisedResponse(advisedResponses,
                advisedResponse->this.after(advisedResponse));
    }

    private AdvisedRequest before(AdvisedRequest advisedRequest){
        log.info("AI的Request:{}",advisedRequest);
        return advisedRequest;
    }

    private void after(AdvisedResponse advisedResponse){
        log.info("AI的Response：{}",advisedResponse.response().getResult().getOutput().getText());
    }
}
