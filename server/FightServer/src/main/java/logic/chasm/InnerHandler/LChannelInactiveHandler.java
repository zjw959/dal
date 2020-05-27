package logic.chasm.InnerHandler;

import logic.support.LogicScriptsUtils;
import net.Session;
import thread.BaseHandler;

public class LChannelInactiveHandler extends BaseHandler {
    private Session session;
    
    public LChannelInactiveHandler(Session session) {
        this.session = session;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().channelInactive(session);
    }

}
