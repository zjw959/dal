package javascript.http;

// import gm.GMOperationManager;
import gm.GMOperationManager;

import java.util.Map;

import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.login.struct.WhiteListManager;
import logic.support.LogicScriptsUtils;
import net.http.HttpRequestWrapper;

import org.apache.log4j.Logger;

import script.IHttpScript;
import server.GameServer;

/**
 * http命令基类脚本 负责分发 以及 逻辑处理脚本
 * 
 * 注意 HTTP线程为多线程. 刷表等操作需要同步锁.
 */
public class HttpScript implements IHttpScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.HTTPEXECUTE_SCRIPTID.Value();
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        // if (httpRequest.invalid()) {
        // if (httpRequest.getCtx() != null) {
        // HttpServerHandler.close(httpRequest.getCtx(), HttpResponseStatus.NOT_FOUND,
        // "not invalid");
        // }
        // return HttpRequestWrapper.HttpRet.FAILED.desc();
        // }
        String url = httpRequest.getUrl();
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "---url=" + url);
        String ret = HttpRequestWrapper.HttpRet.INVALID.desc();

        if (!(GameServer.getInstance().isTestServer())) {
            // 验证合法性
            if (!WhiteListManager.GetInstance().addressInWhiteList(httpRequest.getIp())
                    && !_isLegalClient(httpRequest.getParams())) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "http is not legal");
                return HttpRequestWrapper.HttpRet.ILLEGAL.desc();
            }
        }

        if (url.startsWith("/gm/")) {
            // GM后台相关指令
            ret = GMOperationManager.getInstance().Handler(httpRequest);
        } else if (url.startsWith("/program") || url.startsWith("/custom")) {
            // 程序相关指令,不在GM后台指令中的
            IHttpScript script = LogicScriptsUtils.getProgramHttpScript();
            ret = script.execute(httpRequest);
        } else if (url.startsWith("/stopserver")) {
            // 停服
            System.exit(0);
            ret = HttpRequestWrapper.HttpRet.OK.desc();
        }
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "---ret=" + ret);
        return ret;
    }


    /**
     * 验证http请求的合法性 算法： 1.首先检查发送请求的时间是否在TIMEOUT以内 2.做md5验证
     * 
     * @param parameters
     * @return
     */
    private boolean _isLegalClient(Map<String, String> parameters) {
        // String timestamp = parameters.get("timestamp");
        // String sign = parameters.get("sign");
        //
        // if (!StringUtils.isEmpty(timestamp) && !StringUtils.isEmpty(sign)) {
        // if (Math.abs(System.currentTimeMillis() - Long.parseLong(timestamp)) < TIMEOUT) {
        // String text = timestamp + VERIFIEDSEQUENCE;
        // String md5 = MD5.MD5Encode(text);
        // if (sign.equalsIgnoreCase(md5)) {
        // return true;
        // }
        // }
        // }
        //
        // return false;
        return true;
    }

    private static final Logger LOGGER = Logger.getLogger(HttpScript.class);

    private final long TIMEOUT = 10 * 60 * 1000;
    private final String VERIFIEDSEQUENCE = "1@/.`$~*as0O/Sf?:a^&fd\"23sdf)_4|}]-5+=5667788";
}
