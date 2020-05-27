package javascript.http.fix;


import java.lang.reflect.Field;
import java.util.Map;

import org.apache.log4j.Logger;

import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.support.LogicScriptsUtils;
import message.HandlerFactoryElement;
import message.MessageHandler;
import message.MessageHandlerFactory;
import net.http.HttpRequestWrapper;
import script.IDynamicScript;
import script.IHttpScript;
import script.ILifeCycle;
import script.ScriptManager;
import server.GameServer;
import utils.ExceptionEx;

/**
 * 用户修复大部分的bug的脚本工具类
 */
public class FixBug implements IHttpScript, ILifeCycle {

    @Override
    public void init() {
        LOGGER.info("init fixbug");
    }

    /**
     * map 容器中临时变量需要自己去维护销毁相关
     */
    @Override
    public void destroy() {
        LOGGER.info("destroy fixbug");
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.FIX_BUG.Value();
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        String ret = HttpRequestWrapper.HttpRet.FAILED.desc();
        try {
            String methodName = httpRequest.getParams().get("method");

            if (methodName == null) {
                LOGGER.error("method param is null");
                return ret;
            }

            switch (methodName) {
                case "replacemhandler":
                    // 替换消息Handler
                    ret = _replaceMessageHandlerInstance(httpRequest);
                    break;
                case "callclass":
                    ret = _callClass(httpRequest);
                    break;
                case "callobj":
                    ret = _callObj(httpRequest);
                    break;
                // 临时修复,查询代码在这里
                // 避免频繁修改该类,将基础类改错
                case "dologic":
                    ret = _doLogic(httpRequest);
                    break;
                case "fixplayer":
                    ret = _doFixPlayer(httpRequest);
                    break;
                default:
                    LOGGER.error("no match methodName :" + methodName);
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }

        return ret;
    }

    /**
     * 直接调用缓存对象的call方法
     * 
     * @param httpRequest
     * @return
     */
    private String _callObj(HttpRequestWrapper httpRequest) {
        String fileName = httpRequest.getParams().get("filename");
        if (fileName == null || fileName.isEmpty()) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "filename is null");
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
        LOGGER.info("file name is:" + fileName);
        IDynamicScript dynamicScript = ScriptManager.getInstance().getScriptDy(fileName);
        if (dynamicScript == null) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "dynamicScript is null");
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
        return dynamicScript.execute(httpRequest);
    }

    /**
     * 利用缓存对应的class new一个新的对象调用call方法
     * 
     * @param httpRequest
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private String _callClass(HttpRequestWrapper httpRequest)
            throws InstantiationException, IllegalAccessException {
        String fileName = httpRequest.getParams().get("filename");
        if (fileName == null || fileName.isEmpty()) {
            LOGGER.error(
                    ConstDefine.LOG_HTTP_PREFIX + ConstDefine.LOG_HTTP_PREFIX + "filename is null");
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
        LOGGER.info("file name is:" + fileName);
        IDynamicScript dynamicScript = ScriptManager.getInstance().getScriptDy(fileName);
        if (dynamicScript == null) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "dynamicScript is null");
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
        LOGGER.debug(ConstDefine.LOG_HTTP_PREFIX + dynamicScript.hashCode());
        dynamicScript = dynamicScript.getClass().newInstance();
        LOGGER.debug(ConstDefine.LOG_HTTP_PREFIX + dynamicScript.hashCode());
        return dynamicScript.execute(httpRequest);
    }

    private String _doLogic(HttpRequestWrapper httpRequest) {
        // IHttpScript iScript = (IHttpScript) ScriptManager.getInstance()
        // .getScript(EScriptIdDefine.FIX_BUG_LOGIC.Value());
        IHttpScript iScript = LogicScriptsUtils.getFIX_BUG_LOGIC();
        return iScript.execute(httpRequest);
    }

    private String _doFixPlayer(HttpRequestWrapper httpRequest) {
        // IHttpScript iScript = (IHttpScript) ScriptManager.getInstance()
        // .getScript(EScriptIdDefine.FIX_BUG_LOGIC.Value());
        IHttpScript iScript = LogicScriptsUtils.getFIX_BUG_PLAYER();
        return iScript.execute(httpRequest);
    }

    /**
     * 热修复Messagehandler 脚本 替换handler缓存实例 通过调用此方法更新的handler实例, 在服务器重启后失效
     * 
     * @return
     */
    private String _replaceMessageHandlerInstance(HttpRequestWrapper httpRequest) {
        String ret = HttpRequestWrapper.HttpRet.FAILED.desc();
        try {
            String filename = httpRequest.getParams().get("filename");
            if (filename == null || filename.isEmpty()) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "messagename is null");
                return ret;
            }
            // "org.game.protobuf.c2s.C2SLoginMsg$Ping";
            String messageName = httpRequest.getParams().get("messagename");
            if (messageName == null || messageName.isEmpty()) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "messagename is null");
                return ret;
            }
            // String filename = "javascript.dynamic.handler.MPingHandlerForTest";
            IDynamicScript dynamicScript = ScriptManager.getInstance().getScriptDy(filename);
            if (dynamicScript == null) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "dynamicScript is null");
                return HttpRequestWrapper.HttpRet.FAILED.desc();
            }

            if (!(dynamicScript instanceof MessageHandler)) {
                LOGGER.error(
                        ConstDefine.LOG_HTTP_PREFIX + "dynamicScript is not MessageHandler. name:"
                                + dynamicScript.getClass().getName());
                return HttpRequestWrapper.HttpRet.FAILED.desc();
            }

            MessageHandler md = (MessageHandler) dynamicScript;

            Class handlerClass = md.getClass();
            if (handlerClass == null) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX
                        + String.format("加载handler Class 出错,ClassName:%s", filename));
                return ret;
            }

            Class messageClass = Class.forName(messageName);
            Class<?> clazz = Class.forName(messageClass.getName() + "$MsgID");

            short msgId = (short) clazz.getField("eMsgID_VALUE").getInt(clazz);

            HandlerFactoryElement handlerElements = new HandlerFactoryElement(handlerClass,
                    messageClass, GameServer.MESSAGE_HANDLER_SIZE);

            Class<? extends MessageHandlerFactory> dmClass =
                    MessageHandlerFactory.getInstance().getClass();
            Field field = dmClass.getDeclaredField("elements");
            field.setAccessible(true);
            Map<Short, HandlerFactoryElement> map =
                    (Map) field.get(MessageHandlerFactory.getInstance());
            map.put(msgId, handlerElements);

            LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + String.format(
                    " replace handlerInstance success for handler name %s , MessageName: %s , messageId: %s",
                    filename, messageName, msgId));
            ret = HttpRequestWrapper.HttpRet.OK.desc();
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "replace handlerInstance failed   ", e);
        }
        return ret;
    }

    /**
     * 主要用于存放临时变量
     */
    // private Map<String, Object> params;
    private static Logger LOGGER = Logger.getLogger(FixBug.class);
}
