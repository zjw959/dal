package javascript.http;

import org.apache.log4j.Logger;

import net.http.HttpRequestWrapper;
import script.IHttpScript;

public class TestHttpScript implements IHttpScript {

    @Override
    public int getScriptId() {
        return 11;
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        LOGGER.info(this.getClass().getClassLoader().hashCode());
        LOGGER.info(this.getClass().getClassLoader().toString());
        LOGGER.info("hello, i am TestHttpScript. now i changechangechangechange");
        return HttpRequestWrapper.HttpRet.OK.desc();
    }

    private static final Logger LOGGER = Logger.getLogger(TestHttpScript.class);
}
