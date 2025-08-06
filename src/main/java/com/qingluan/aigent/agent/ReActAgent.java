package com.qingluan.aigent.agent;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent{

    //思考是否调用工具
    public abstract boolean think();

    //执行工具完成任务
    public abstract String act();


    @Override
    public String step() {
        try {
            boolean actthink = think();
            if(!actthink){
                setStatus(AgentStatus.FINLISH);
                return "思考完成-无需行动";
            }
            return act();
        } catch (Exception e) {
            e.printStackTrace();
            return "执行步骤失败：" + e.getMessage();
        }
    }
}
