package javascript.http;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;
import logic.chasm.InnerHandler.LDismissTeamHandler;
import logic.chasm.bean.TeamInfo;
import logic.chasm.handler.HttpDestroyFightRoomHandler;
import logic.constant.EScriptIdDefine;
import net.http.HttpRequestWrapper;
import redis.FightRedisOper;
import room.FightRoom;
import room.FightRoomManager;
import script.IHttpScript;
import script.ScriptManager;
import server.FightServer;
import server.ServerConfig;
import thread.FightRoomPrepareProcessor;
import thread.TeamProcessor;
import thread.TeamProcessorManager;
import utils.CsvUtils;
import utils.DateEx;
import utils.ExceptionEx;
import utils.GsonUtils;

public class HttpScript implements IHttpScript {

    private static final Logger LOGGER = Logger.getLogger(HttpScript.class);

    static final String SCRIPTS_LOCAL_DIR = HttpScript.class.getClassLoader().getResource("")
            .getPath()
            + "../";
    @Override
    public int getScriptId() {
        return EScriptIdDefine.HTTPEXECUTE_SCRIPTID.Value();
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        String url = httpRequest.getUrl();
        LOGGER.debug("---url=" + url);
        String ret = getError(HttpRequestWrapper.HttpRet.INVALID.desc());
        String command = null;
        if (url.startsWith("/gm/")) {
            // 去掉前面的字符
            command = url.substring("/gm/".length());
            String paramData = httpRequest.getData();
            httpRequest.getParams().put("param", paramData);
        } else if(url.startsWith("/custom")) {
            command = httpRequest.getParams().get("command");
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
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
        }
        
        if (command == null || command.equalsIgnoreCase("")) {
            LOGGER.error("http server received an invalid command.");
            return getError(HttpRequestWrapper.HttpRet.INVALID.desc());
        }

        LOGGER.info("#########################################################################");
        LOGGER.info("Http Request: Command:" + command + "  IP: "
                + httpRequest.getIp());
        Iterator<Map.Entry<String, String>> it = httpRequest.getParams().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            LOGGER.info("Key: [" + entry.getKey() + "] Value: ["
                    + entry.getValue() + "]");
        }
        LOGGER.info("#########################################################################");
        
        switch (command.toLowerCase()) {
            // custom
            // 获得队伍信息
            case "teaminfo":
                try {
                    JSONArray array = TeamProcessorManager.getInstance().getAllTeamInfoForJson();
                    ret = getSuccess(GsonUtils.toJson(array));
                } catch (Exception e) {
                    ret = getError("acquire teaminfo error " + ExceptionEx.e2s(e));
                }
                break;
            case "fightroom":
                try {
                    JSONArray array = FightRoomManager.getAllFightRoomForJson();
                    ret = getSuccess(GsonUtils.toJson(array));
                } catch (Exception e) {
                    ret = getError("acquire fightroom error " + ExceptionEx.e2s(e));
                }
                break;
            case "delteam":
                ret = delTeam(httpRequest);
                break;
            case "delfight":
                ret = delFightRoom(httpRequest);
                break;
             // 脚本操作
            case "loadallscripts":
                ret = loadAllScripts();
                break;
            case "loadscript":
                ret = loadSingleScript(httpRequest);
                break;
            case "loadluascript":
                ret = loadLuaScript(httpRequest);
                break;
            case "setnettype":
                ret = setNetType(httpRequest);
                break;
            case "setmaxfightnum":
                ret = setMaxFightNum(httpRequest);
                break;
            case "loadallgamedata":
                ret = loadAllGameData();
                break;
            // gm
            case "reloadcsvfiles":
                ret = reloadCsvFiles(httpRequest);
                break;
            case "reloadclass":
                ret = reloadClass(httpRequest);
                break;
            case "getserverruntimestatus":
                ret = getServerRuntimeStatus();
                break;
        }
        LOGGER.debug("---ret=" + ret);
        return ret;
    }

    /**
     * 重载所有脚本
     * @return
     */
    private String loadAllScripts() {
        try {
            ScriptManager.getInstance().loadAllScript();
            FightServer.getInstance().setReloadJavaScriptTime(System.currentTimeMillis());
            return getSuccess();
        } catch (Exception ex) {
            return getError("[http info] --->reloadAllJavaScript: " + ExceptionEx.e2s(ex));
        }
    }
    
    private String loadSingleScript(HttpRequestWrapper httpRequest) {
        try {
            String scriptName = httpRequest.getParams().get("scriptname");
            if (ScriptManager.getInstance().loadScript(scriptName)) {
                FightServer.getInstance().setReloadJavaScriptTime(System.currentTimeMillis());
                return getSuccess();
            } else {
                return getError("[http info] --->reloadJavaScript: " + HttpRequestWrapper.HttpRet.FAILED.desc());
            }
        } catch (Exception ex) {
            return getError("[http info] --->reloadJavaScript: " + ExceptionEx.e2s(ex));
        }
    }
    
    private String loadLuaScript(HttpRequestWrapper httpRequest) {
        try {
            if (ScriptManager.getInstance().loadScript("javascript.logic.RedisOperScript")) {
                FightServer.getInstance().setReloadJavaScriptTime(System.currentTimeMillis());
                FightRedisOper.clearSha();
                return getSuccess();
            } else {
                return getError("[http info] --->reloadLuaScript: " + HttpRequestWrapper.HttpRet.FAILED.desc());
            }
        } catch (Exception ex) {
            return getError("[http info] --->reloadLuaScript: " + ExceptionEx.e2s(ex));
        }
    }
    
    private String setNetType(HttpRequestWrapper httpRequest) {
        try {
            String netTypeStr = httpRequest.getParams().get("nettype");
            int netType = Integer.parseInt(netTypeStr);
            if(netType == 1 || netType == 2) {
                ServerConfig.getInstance().setNetType(netType);
                return getSuccess();
            } else {
                return getError("[http info] --->setNetType: " + HttpRequestWrapper.HttpRet.FAILED.desc());
            }
        } catch (Exception ex) {
            return getError("[http info] --->setNetType: " + ExceptionEx.e2s(ex));
        }
    }
    
    private String setMaxFightNum(HttpRequestWrapper httpRequest) {
        try {
            String maxFightNumStr = httpRequest.getParams().get("maxfightnum");
            int maxFightNum = Integer.parseInt(maxFightNumStr);
            ServerConfig.getInstance().setMaxFightNum(maxFightNum);
            return getSuccess();
        } catch (Exception ex) {
            return getError("[http info] --->setMaxFightNum: " + ExceptionEx.e2s(ex));
        }
    }
    
    private String loadAllGameData() {
        String rst = null;
        try {
            CsvUtils.reloadCsvFiles(null);
            rst = getSuccess("reloadCsvFiles: local");
        } catch (Exception e) {
            rst = getError("reloadCsvFiles local failed " + ExceptionEx.e2s(e));
        }
        return rst;
    }
    
    public String getError(String desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", -2);
        jsonObject.put("desc", desc);

        return jsonObject.toJSONString();
    }
    
    public String getSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", 0);
        jsonObject.put("desc", "success");

        return jsonObject.toJSONString();
    }
    
    public String getSuccess(String desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", 0);
        jsonObject.put("desc", desc);

        return jsonObject.toJSONString();
    }
    
    public JSONObject getSuccessJsonObject() {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", 0);
        jsonObject.put("desc", "success");

        return jsonObject;
    }
    
    public String reloadCsvFiles(HttpRequestWrapper httpRequest) {
        String paramData = httpRequest.getParams().get("param");
        JSONObject request = JSON.parseObject(paramData);
        String rst = null;
        // 通知游戏服文件的地址
        String fileURL = request.getString("path");
        try {
            CsvUtils.reloadCsvFiles(fileURL);
            rst = getSuccess("reloadCsvFiles:" + fileURL);
        } catch (Exception e) {
            rst = getError("reloadCsvFiles failed " + ExceptionEx.e2s(e));
        }
        return rst;
    }
    
    public String reloadClass(HttpRequestWrapper httpRequest) {
        String rst = null;
        try {
            String paramData = httpRequest.getParams().get("param");
            JSONObject request = JSON.parseObject(paramData);
            // 通知游戏服文件的地址
            String fileURL = request.getString("path");
            if (fileURL != null) {
                // 下载并解压
                String fileName = "fightscripts.zip";
                fileURL += fileName;
                HttpUtil.downloadFile(fileURL, SCRIPTS_LOCAL_DIR);
                ZipUtil.unzip(SCRIPTS_LOCAL_DIR + fileName, SCRIPTS_LOCAL_DIR);
            }
            ScriptManager.getInstance().loadAllScript();
            rst = getSuccess("reloadClass:" + fileURL);
        } catch (Exception e) {
            rst = getError("reloadClass failed " + ExceptionEx.e2s(e));
        }
        return rst;
    }
    
    private String getServerRuntimeStatus() {
        Runtime runtime = Runtime.getRuntime();
        long startTime = FightServer.getInstance().getStartTime();
        long reloadGameDataTime = FightServer.getInstance().getReloadGameDataTime();
        long reloadJavaScriptTime = FightServer.getInstance().getReloadJavaScriptTime();
        long freeMem = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();

        int teamNum = TeamProcessorManager.getInstance().getTeamNum();

        JSONObject jsonObject = getSuccessJsonObject();
        JSONObject json = new JSONObject();
        json.put("isTestServer", ServerConfig.getInstance().getIsTestServer());
        json.put("startTime", DateEx.format(new Date(startTime), DateEx.fmt_yyyy_MM_dd_HH_mm_ss));
        json.put("reloadGameDataTime",
                DateEx.format(new Date(reloadGameDataTime), DateEx.fmt_yyyy_MM_dd_HH_mm_ss));
        json.put("reloadJavaScriptTime",
                DateEx.format(new Date(reloadJavaScriptTime), DateEx.fmt_yyyy_MM_dd_HH_mm_ss));
        json.put("freeMem", toShowSize(freeMem));
        json.put("totalMem", toShowSize(totalMemory));
        json.put("TeamNum", teamNum);
        json.put("RoomNum", FightRoomManager.getRoomSize());
        json.put("RoleNum", FightRoomManager.getRoleSize());
        jsonObject.put("ServerStatus", json.toJSONString());
        return jsonObject.toJSONString();
    }
    
    private String delTeam(HttpRequestWrapper httpRequest) {
        String rst = null;
        try {
            String teamIdStr = httpRequest.getParams().get("teamid");
            long teamId = Long.valueOf(teamIdStr);
            TeamInfo teamInfo =
                    TeamProcessorManager.getInstance().getTeamInfo(teamId);
            TeamProcessor teamProcessor = (TeamProcessor) TeamProcessorManager.getInstance()
                    .getRoomProcessor(teamInfo.getProcessorId());
            teamProcessor.executeHandler(new LDismissTeamHandler(teamInfo));
            rst = getSuccess();
        } catch (Exception e) {
            rst = getError("delteam failed " + ExceptionEx.e2s(e));
        }
        return rst;
    }
    
    private String delFightRoom(HttpRequestWrapper httpRequest) {
        String rst = null;
        try {
            String roomIdStr = httpRequest.getParams().get("roomid");
            long roomId = Long.valueOf(roomIdStr);
            FightRoom fightRoom = FightRoomManager.getRoomByRoomId(roomId);
            HttpDestroyFightRoomHandler handler = new HttpDestroyFightRoomHandler(fightRoom);
            FightRoomPrepareProcessor.getInstance().executeHandler(handler);
            rst = getSuccess();
        } catch (Exception e) {
            rst = getError("delfightRoom failed " + ExceptionEx.e2s(e));
        }
        return rst;
        
    }
    
    /***
     * 转换成直观的显示长度
     * 
     * @param size 单位（Byte）
     * @return
     */
    public static String toShowSize(long size) {
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
}
