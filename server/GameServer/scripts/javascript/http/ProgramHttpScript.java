package javascript.http;

import gm.GMHandlerErrorCode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.activity.helper.list.ActivityStoreHelper;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.constant.EServerStatus;
import logic.functionSwitch.FunctionSwitchService;
import logic.gloabl.GlobalService;
import logic.login.service.LoginService;
import logic.support.LogicScriptsUtils;
import net.http.HttpRequestWrapper;

import org.apache.log4j.Logger;

import redis.service.ESpringConextType;
import script.IHttpScript;
import script.IScript;
import script.ScriptManager;
import server.GameServer;
import server.ServerConfig;
import thread.player.PlayerProcessorManager;
import utils.CsvUtils;
import utils.ExceptionEx;
import utils.GsonUtils;
import utils.SpringContextUtils;
import utils.javaManagement.GarbageCollectorInfo;
import utils.javaManagement.JavaInfo;
import utils.javaManagement.MemoryInformation;
import cn.hutool.core.util.RandomUtil;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jarvis.cache.AutoLoadHandler;
import com.jarvis.cache.CacheHandler;

import db.game.bean.PlayerDBBean;

/**
 * GM后台相关指令 http命令逻辑处理脚本
 */
public class ProgramHttpScript implements IHttpScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.PROGRAM_HTTP_SCRIPTID.Value();
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        String ret = HttpRequestWrapper.HttpRet.FAILED.desc();

        String command = httpRequest.getParams().get("command");
        if (command == null || command.equalsIgnoreCase("")) {
            try {
                // GM后台是走的POST请求
                String data = httpRequest.getData();
                JSONObject json = JSONObject.parseObject(data);
                JSONObject jsonObj = json.getJSONObject("gmcommand");
                String com = jsonObj.getString("command");
                if (com != null) {
                    command = com;
                    // 将参数解析到Params
                    for (Entry<String, Object> entry : jsonObj.entrySet()) {
                        httpRequest.getParams().put(entry.getKey(), entry.getValue().toString());
                    }
                }
            } catch (Exception e) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + ExceptionEx.e2s(e));
            }
        }
        if (command == null || command.equalsIgnoreCase("")) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "http server received an invalid command.");
            // httpRequest.response(HttpRequestWrapper.HttpContentType.Text,
            // HttpRequestWrapper.HttpRet.INVALID.desc());
            return HttpRequestWrapper.HttpRet.INVALID.desc();
        }

        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "Http Request: Command:" + command + "  IP: "
                + httpRequest.getIp());
        Iterator<Map.Entry<String, String>> it = httpRequest.getParams().entrySet().iterator();
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX
                + "#########################################################################");
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "Key: [" + entry.getKey() + "] Value: ["
                    + entry.getValue() + "]");
        }
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX
                + "#########################################################################");


        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX
                + "--------------ProgramOperationManager recv request ##############");
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "command = " + command);
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "params = " + httpRequest.getParams());


        switch (command.toLowerCase()) {
            // 程序基础性能面功能
            case "printinfo":
                boolean isPrint = ServerConfig.getInstance().isPrintQueueSize();
                ServerConfig.getInstance().setIsPrintQueueSize(!isPrint);
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "threadinfo":
                Map<String, Number> threads = JavaInfo.collectThreadInfo();
                String threadStr = GsonUtils.toJson(threads);
                LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + threadStr);
                ret = threadStr;
                break;
            case "classloadinfo":
                Map<String, Number> classloads = JavaInfo.collectClassLoadingInfo();
                String classloadStr = GsonUtils.toJson(classloads);
                ret = classloadStr;
                break;
            case "meminfo":
                String meminfo = new MemoryInformation().toString();
                LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + meminfo);
                ret = meminfo;
                break;
            case "garbageinfo":
                Map<String, Number> garbages = new GarbageCollectorInfo().collectGC();
                String garbageStr = GsonUtils.toJson(garbages);
                ret = garbageStr;
                break;
            case "garbageinfodelta":
                Map<String, Number> garbagedeltas =
                        GameServer.getInstance().getGarbageCollectorInfo().collectGC();
                String garbagesStr = GsonUtils.toJson(garbagedeltas);
                ret = garbagesStr;
                break;
            case "getplayermemsize":
                ret = _getPlayerMemSize(httpRequest);
                break;
            case "getplayerids":
                ret = _getPlayerIds();
                break;
            case "gc":
                System.gc();
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            // 性能维护操作:
            case "maxsavenumoffline":
                int num = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyOffLineMax(num);
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "modifymemory_max":
                int modifymemory_max = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyMEMORY_MAX(modifymemory_max);
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "modifylogin_queue_max":
                int modifylogin_queue_max = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyLOGIN_QUEUE_MAX(modifylogin_queue_max);
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "modifymemory_busy":
                int modifymemory_busy = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyMEMORY_BUSY(modifymemory_busy);
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "maxofflinetimes":
                int num2 = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyMaxOfflineTimes(num2);;
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "savebackcheckinterval":
                int num3 = Integer.valueOf(httpRequest.getParams().get("num"));
                boolean is = GlobalService.getInstance().modifySaveBackCheckInterval(num3);
                if (is) {
                    ret = HttpRequestWrapper.HttpRet.OK.desc();
                } else {
                    ret = HttpRequestWrapper.HttpRet.FAILED.desc();
                }
                break;
            case "savebackinterval":
                int num4 = Integer.valueOf(httpRequest.getParams().get("num"));
                boolean is1 = GlobalService.getInstance().modifySaveBackInterval(num4);
                if (is1) {
                    ret = HttpRequestWrapper.HttpRet.OK.desc();
                } else {
                    ret = HttpRequestWrapper.HttpRet.FAILED.desc();
                }
                break;
            case "savebackallofflineplayer":
                PlayerProcessorManager.getInstance().savebackAllOfflinePlayer();
                System.gc();
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "recommandfriendhitlocalrate":
                int num5 = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyRecommandFriendHitLocalRate(num5);;
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "recommandfriendloadcount":
                int num6 = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyRecommandFriendLoadCount(num6);;
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "recommandfriendloadredisinterval":
                int num7 = Integer.valueOf(httpRequest.getParams().get("num"));
                GlobalService.getInstance().modifyRecommandFriendLoadRedisInterval(num7);;
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            case "cachepeerhitrate":
                int num8 = Integer.valueOf(httpRequest.getParams().get("num"));
                CacheHandler cacheHandler = SpringContextUtils
                        .getBean(ESpringConextType.PlAYER.getType(), CacheHandler.class);
                if (cacheHandler != null) {
                    AutoLoadHandler _autoLoadHander = cacheHandler.getAutoLoadHandler();
                    if (_autoLoadHander != null) {
                        _autoLoadHander.setCachePeerHitRate(num8);
                    }
                }
                System.gc();
                ret = HttpRequestWrapper.HttpRet.OK.desc();
                break;
            // 设置服务器最大在线人数
            case "maxonlinenum":
                ret = setMaxOnlineNum(httpRequest);
                break;
            // 修改服务器状态
            case "modifyserverstatus":
                int status = Integer.valueOf(httpRequest.getParams().get("status"));
                EServerStatus s = EServerStatus.getServerStatus(status);
                if (s != null) {
                    GameServer.getInstance().setStatus(s);
                    if (s == EServerStatus.MAINTAINING) {// 切维护
                        // 踢所有玩家下线
                        kickPlayerOffLine(0);
                    }
                    ret = HttpRequestWrapper.HttpRet.OK.desc();
                } else {
                    ret = getError(-4, "error status:" + status);
                }
                break;
            // 踢玩家下线
            case "kickoff":
                String pid = httpRequest.getParams().get("playerId");
                // 踢玩家下线
                // 玩家id
                int playerId = 0;
                if (pid != null) {
                    playerId = Integer.parseInt(pid);
                }
                ret = kickPlayerOffLine(playerId);
                break;
            // 重载配置表
            case "loadallgamedata":
                ret = _loadAllGameData();
                break;
            // 脚本操作
            case "loadallscripts":
                ret = _loadAllScripts();
                break;
            case "loadscript":
                ret = _loadSingleScript(httpRequest);
                break;
            case "executeihttpscript":
                ret = _executeIHttpScript(httpRequest);
                break;
            case "fixbug":
                ret = _fixBugScript(httpRequest);
                break;
            case "loadactivitylua":
                ret = loadActivityLuaScript(httpRequest);
                break;
            default:
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX
                        + String.format("can not find operation : %s", command));
        }
        LOGGER.debug(ConstDefine.LOG_HTTP_PREFIX + "---ret=" + ret);
        return ret;
    }

    public String setMaxOnlineNum(HttpRequestWrapper httpRequest) {
        String rst = null;
        try {
            String value = httpRequest.getParams().get("maxonlinenum");
            if (value == null) {
                rst = getError(-4, "maxonlinenum is null");
            } else {
                int num = Integer.parseInt(value);
                if (num > 0) {
                    GlobalService.getInstance().modifyOnlineMax(num);
                }
                JSONObject response = getSuccessJsonObject();
                rst = response.toJSONString();
            }
        } catch (Exception e) {
            String msg = ExceptionEx.e2s(e);
            LOGGER.error(msg);
            rst = getError(GMHandlerErrorCode.ERROR_MAX_ONLINE_NUM, msg);
        }
        return rst;
    }

    /**
     * 获取玩家内存占用大小
     * 
     * @param httpRequest
     */
    private String _getPlayerMemSize(HttpRequestWrapper httpRequest) {
        try {
            Player player = null;
            String pid = httpRequest.getParams().get("playerid");
            if (pid != null) {
                int playerId = Integer.parseInt(pid);
                player = PlayerManager.getPlayerByPlayerId(playerId);
            } else {
                List<Player> ps = PlayerManager.getAllPlayers();
                int s = ps.size();
                if (s == 0) {
                    return "player size is zero";
                }
                player = ps.get(RandomUtil.randomInt(0, s));
            }
            if (player == null) {
                return "managerCache not find player.playerId:" + pid;
            }
            // long size = SizeOfAgent.sizeOf(player);
            // long fullSize = SizeOfAgent.fullSizeOf(player);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject json = new JsonObject();
            json.addProperty("playerId", player.getPlayerId());
            // json.addProperty("memSize", _toShowSize(size));
            // json.addProperty("memFullSize", _toShowSize(fullSize));
            // json.addProperty("playerToString", player.toString());
            PlayerDBBean playerDb = player.toPlayerBean();
            String playerData = playerDb.getData();
            int byteBytes = playerDb.toString().getBytes().length;
            json.addProperty("dbStringByteSize", byteBytes / 1024);
            playerDb.setData("");
            JsonElement dbJson = GsonUtils.toJsonTree(playerDb);
            json.add("dbJson", dbJson);

            JsonElement dataStr = new JsonParser().parse(playerData);
            json.add("data", dataStr);
            
            return gson.toJson(json) + "\n strOriginValue:" + playerData;
        } catch (Exception ex) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "_getPlayerMemSize: " + ExceptionEx.e2s(ex));
            return ExceptionEx.e2s(ex);
        }
    }

    /**
     * 获取玩家内存占用大小
     * 
     * @param httpRequest
     */
    private String _getPlayerIds() {
        try {
            List<Integer> as = new ArrayList<Integer>();
            List<Player> ps = PlayerManager.getAllPlayers();
            int s = ps.size();
            if (s == 0) {
                return "player size is zero";
            }
            for (Player player : ps) {
                as.add(player.getPlayerId());
            }
            return GsonUtils.toJson(as);
        } catch (Exception ex) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "_getPlayerMemSize: " + ExceptionEx.e2s(ex));
            return ExceptionEx.e2s(ex);
        }
    }

    /**
     * 指定的bug修复
     * 
     * @param httpRequest
     * @return
     */
    private String _fixBugScript(HttpRequestWrapper httpRequest) {
        IHttpScript iScript = LogicScriptsUtils.getFixBug();
        return iScript.execute(httpRequest);
    }

    /**
     * 调用HttpScript的execute方法
     *
     * 没有指定id(危险) 或者 指定了id的HTTP
     * 
     * @param remoteAddress
     * @param parameters
     * @return
     */
    private String _executeIHttpScript(HttpRequestWrapper httpRequest) {
        try {
            int scriptId = Integer.valueOf(httpRequest.getParams().get("scriptId"));
            LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "_executeIHttpScript, scriptId=" + scriptId);
            IScript iScript = ScriptManager.getInstance().getScript(scriptId);
            if (iScript == null) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "找不到对应的IScript脚本. id:" + scriptId);
                return HttpRequestWrapper.HttpRet.FAILED.desc();
            }

            if (!IHttpScript.class.isAssignableFrom(iScript.getClass())) {
                LOGGER.error(
                        ConstDefine.LOG_HTTP_PREFIX + "该脚本并没有继承自IHttpScript接口. id:" + scriptId);
                return HttpRequestWrapper.HttpRet.FAILED.desc();
            }

            if (EScriptIdDefine.HTTPEXECUTE_SCRIPTID.Value() == scriptId) {
                LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "scriptId 不能等于 "
                        + EScriptIdDefine.HTTPEXECUTE_SCRIPTID.name() + " ,id:" + scriptId);
                return HttpRequestWrapper.HttpRet.FAILED.desc();
            }

            boolean isDefine = false;
            EScriptIdDefine[] defines = EScriptIdDefine.values();
            for (EScriptIdDefine eScriptIdDefine : defines) {
                if (eScriptIdDefine.Value() == scriptId) {
                    isDefine = true;
                }
            }
            if (!isDefine) {
                LOGGER.warn("该Http接口ID没有HTTP定义.id:" + scriptId + " ,name:"
                        + iScript.getClass().getName());
            }

            return ((IHttpScript) iScript).execute(httpRequest);

        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + ExceptionEx.e2s(e));
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
    }

    private String _loadAllScripts() {
        try {
            ScriptManager.getInstance().loadAllScript();
            GameServer.getInstance().setReloadJavaScriptTime(System.currentTimeMillis());
            return HttpRequestWrapper.HttpRet.OK.desc();
        } catch (Exception ex) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "reloadJavaScript: " + ExceptionEx.e2s(ex));
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
    }

    private String _loadAllGameData() {
        String rst = null;
        try {
            CsvUtils.reloadCsvFiles(null);
            FunctionSwitchService.getInstance().clear();// 清理全服开关，获取时会自动检查重载
            JSONObject response = getSuccessJsonObject();
            response.put("desc", "reloadCsvFiles: local");
            rst = response.toJSONString();
        } catch (Exception e) {
            String msg = ExceptionEx.e2s(e);
            LOGGER.error(msg);
            rst = getError(GMHandlerErrorCode.ERROR_LOAD_CSV, msg);
        }
        return rst;
    }

    private String _loadSingleScript(HttpRequestWrapper httpRequest) {
        try {
            String scriptName = httpRequest.getParams().get("scriptname");
            if (ScriptManager.getInstance().loadScript(scriptName)) {
                GameServer.getInstance().setReloadJavaScriptTime(System.currentTimeMillis());
                return HttpRequestWrapper.HttpRet.OK.desc();
            } else {
                return HttpRequestWrapper.HttpRet.FAILED.desc();
            }
        } catch (Exception ex) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "reloadJavaScript: " + ExceptionEx.e2s(ex));
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
    }

    public String kickPlayerOffLine(int playerId) {
        try {
            if (playerId > 0) {
                Player player = PlayerManager.getPlayerByPlayerId(playerId);
                if (player != null && player.isOnline()) {
                    LoginService.sendForceOffline(player.getCtx());
                    LOGGER.info("把玩家踢下线， 玩家id: " + playerId + " 当前在线");
                } else {
                    LOGGER.info("把玩家踢下线， 玩家id: " + playerId + " 当前不在线");
                }
            } else {// 踢所有人下线
                LOGGER.info("把玩家踢下线， 踢所有人");
                List<Player> playerList = PlayerManager.getAllPlayers();
                if (playerList != null && !playerList.isEmpty()) {
                    for (Player player : playerList) {
                        if (player.isOnline()) {
                            LoginService.sendForceOffline(player.getCtx());
                        }
                    }
                }
            }
            return HttpRequestWrapper.HttpRet.OK.desc();
        } catch (Exception ex) {
            LOGGER.error(ConstDefine.LOG_HTTP_PREFIX + "reloadJavaScript: " + ExceptionEx.e2s(ex));
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
    }
    

    private String loadActivityLuaScript(HttpRequestWrapper httpRequest) {
        try {
            if (ScriptManager.getInstance().loadScript("javascript.logic.activity.ActivityReloadLuaScript")) {
                ActivityStoreHelper.reloadScripts();
                return HttpRequestWrapper.HttpRet.OK.desc();
            } else {
                return HttpRequestWrapper.HttpRet.FAILED.desc();
            }
        } catch (Exception ex) {
            LOGGER.error("[http info] --->loadActivityLuaScript: " + ExceptionEx.e2s(ex));
            return HttpRequestWrapper.HttpRet.FAILED.desc();
        }
    }


    /***
     * 转换成直观的显示长度
     * 
     * @param size 单位（Byte）
     * @return
     */
    private static String _toShowSize(long size) {
        String str = size + "B";
        if (size >= 1024 * 1024 * 1024) {
            str = size / (1024d * 1024d * 1024d) + "G";
        } else if (size >= 1024 * 1024) {
            str = size / (1024d * 1024d) + "M";
        } else if (size >= 1024) {
            str = size / (1024d) + "K";
        }
        return str;
    }

    public String getSuccess(String desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", GMHandlerErrorCode.SUCESS);
        jsonObject.put("desc", desc);

        return jsonObject.toJSONString();
    }

    public String getError() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", GMHandlerErrorCode.ERROR_NO_API);
        jsonObject.put("desc", "no api!");

        return jsonObject.toJSONString();
    }

    public String getError(String desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", -2);
        jsonObject.put("desc", desc);

        return jsonObject.toJSONString();
    }

    public String getError(int error, String desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", error);
        jsonObject.put("desc", desc);

        return jsonObject.toJSONString();
    }

    public JSONObject getSuccessJsonObject() {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", GMHandlerErrorCode.SUCESS);
        jsonObject.put("desc", "success");

        return jsonObject;
    }

    private static final Logger LOGGER = Logger.getLogger(ProgramHttpScript.class);
}
