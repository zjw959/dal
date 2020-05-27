package javascript.dynamic;

import org.apache.log4j.Logger;

import net.http.HttpRequestWrapper;
import script.IDynamicScript;
import script.ILifeCycle;

public class TestJustFun implements IDynamicScript, ILifeCycle {

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        LOGGER.info("just for fun execute");
        return HttpRequestWrapper.HttpRet.OK.desc();
    }

    @Override
    public void init() {
        LOGGER.info("TestJustFun INIT again");
    }

    @Override
    public void destroy() {
        LOGGER.info("TestJustFun destroy again");
    }

    private static Logger LOGGER = Logger.getLogger(TestJustFun.class);
}
