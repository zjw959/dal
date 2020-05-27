package net.http;

import java.util.HashSet;

import org.apache.log4j.Logger;

import logic.support.LogicScriptsUtils;
import script.IHttpScript;
import server.GameServer;
import thread.base.GameInnerHandler;
import thread.base.http.HttpProcessor;

/**
 * 注意 此类的父类为shareable对象
 */
public class HttpServerImpl extends HttpServerHandler {
    @Override
    protected void _handleHttpRequest(final HttpRequestWrapper httpRequestWrapper) {
        if (GameServer.getInstance().isTestServer()) {
            String pw = httpRequestWrapper.getParams().get("j~x4k31l!55#$om43");
            if (pw != null) {
                if (pw.equals("4445!@#3$%^24324fs#$%^*")) {
                    Integer testNum = Integer.valueOf(httpRequestWrapper.getParams().get("num"));
                    if (setTestNums.contains(testNum)) {
                        LOGGER.error("严重错误,重复数字出现:" + testNum);
                    } else {
                        setTestNums.add(testNum);
                    }

                    // 复杂解码测试
                    if (httpRequestWrapper.getParams().get("isD").equals("true")) {
                        httpRequestWrapper.response(HttpRequestWrapper.HttpContentType.Text,
                                "@#%@#%asf23546y6%^#$%af@sfsdfsd2$$$342$3432$$3243424444445324324fs#$%^&*(ghvjB33^&567#2#&efsdsd)pscs./fsg");
                    } else {
                        // 自增重复测试
                        httpRequestWrapper.response(HttpRequestWrapper.HttpContentType.Text,
                                String.valueOf(testNum));
                    }
                    return;
                }
            }
        }

        _doDefault(httpRequestWrapper);
    }

    private static final Logger LOGGER = Logger.getLogger(HttpServerImpl.class);

    private static final HashSet<Integer> setTestNums = new HashSet<Integer>();

    /**
     * Http 线程中执行
     * 
     * @param httpReques
     */
    private void _doDefault(final HttpRequestWrapper httpRequest) {
        HttpProcessor.getInstance()
                .executeHandler((new LHttpHandler().setHttpRequest(httpRequest)));
    }

    /**
     * http 线程执行的逻辑单元
     */
    private class LHttpHandler extends GameInnerHandler {
        private HttpRequestWrapper httpRequest;

        public LHttpHandler setHttpRequest(HttpRequestWrapper httpRequest) {
            this.httpRequest = httpRequest;
            return this;
        }

        @Override
        public void action() {
            IHttpScript script = LogicScriptsUtils.getIHttpScript();
            String ret = script.execute(httpRequest);
            httpRequest.response(HttpRequestWrapper.HttpContentType.Text, ret);
        }
    }
}
