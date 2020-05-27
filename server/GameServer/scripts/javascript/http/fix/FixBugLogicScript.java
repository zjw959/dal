package javascript.http.fix;

import org.apache.log4j.Logger;

import logic.constant.EScriptIdDefine;
import net.http.HttpRequestWrapper;
import script.IHttpScript;

public class FixBugLogicScript implements IHttpScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.FIX_BUG_LOGIC.Value();
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        LOGGER.info("hello i am FixBugLogicScript. i want change my print! change!");
        return HttpRequestWrapper.HttpRet.OK.desc();
    }

    private static final Logger LOGGER = Logger.getLogger(FixBugLogicScript.class);
}
