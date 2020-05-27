package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.TestHandlerParam;

public class TestTeamHandler implements TeamActionHandler<TestHandlerParam> {

    @Override
    public void process(TestHandlerParam json) {
        System.out.println("json=" + json.getTest());
    }

}
