package logic.chasm.InnerHandler;

import java.util.Map;

import logic.support.LogicScriptsUtils;
import thread.BaseHandler;

public class LReqFightBeginHandler extends BaseHandler {
    private Map<String, String> teamInfoMap;
    
    public LReqFightBeginHandler(Map<String, String> teamInfoMap) {
        this.teamInfoMap = teamInfoMap;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().reqFightBeginHandler(teamInfoMap);
    }
}
