package javascript.dynamic.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CLoginMsg;

import logic.support.MessageUtils;
import message.MessageHandler;
import message.SMessage;
import net.http.HttpRequestWrapper;
import script.IDynamicScript;

/**
 * MPingHandlerForTest 替换测试DEMO类
 */
public class MPingHandlerForTest extends MessageHandler implements IDynamicScript {

    @Override
    public void action() {
        LOGGER.debug("just for fun MPingHandlerForTest");
        SMessage msg = new SMessage(S2CLoginMsg.Pong.MsgID.eMsgID_VALUE, new byte[0]);
        MessageUtils.send(getCtx(), msg);
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        LOGGER.info("just for fun execute");
        return HttpRequestWrapper.HttpRet.OK.desc();
    }

    private static Logger LOGGER = Logger.getLogger(MPingHandlerForTest.class);

}
