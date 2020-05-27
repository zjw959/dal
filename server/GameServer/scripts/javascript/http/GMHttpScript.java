package javascript.http;

// import gm.GMOperationManager;
import gm.GMHandlerErrorCode;
import gm.bean.onlinePlayersNum;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.functionSwitch.FunctionSwitchService;
import logic.gloabl.GlobalService;
import logic.login.service.LoginService;
import logic.login.struct.ChannelInfo;
import net.AttributeKeys;
import net.http.HttpRequestWrapper;

import org.apache.log4j.Logger;

import redis.service.ESpringConextType;
import script.IHttpScript;
import script.ScriptManager;
import server.GameServer;
import server.ServerConfig;
import utils.CsvUtils;
import utils.DateEx;
import utils.ExceptionEx;
import utils.SpringContextUtils;
import utils.ToolMap;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import db.game.bean.PlayerDBBean;
import db.game.service.PlayerDaoService;

/**
 * GM后台相关指令 http命令逻辑处理脚本
 */
public class GMHttpScript implements IHttpScript {
    static final String SCRIPTS_LOCAL_DIR = GMHttpScript.class.getClassLoader().getResource("")
            .getPath()
            + "../";
    @Override
    public int getScriptId() {
        return EScriptIdDefine.GM_HTTP_SCRIPTID.Value();
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        String ret = getError();

        String paramData = httpRequest.getData();
        String command = httpRequest.getUrl();

        // 去掉前面的字符
        command = command.substring("/gm/".length());

        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX
                + "--------------GMOperationManager recv request ##############");
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "command = " + command);
        LOGGER.info(ConstDefine.LOG_HTTP_PREFIX + "paramData = " + paramData);

        switch (command.toLowerCase()) {

        // ---------------------------游戏服信息相关操作--------------------------------
        // 通知游戏服重新载入配置表csv
            case "reloadcsvfiles":
                ret = reloadCsvFiles(paramData);
                break;

            // 通知游戏服重新载入class
            case "reloadclass":
                ret = reloadClass(paramData);
                break;

            // 查询当前在线玩家数量
            case "getonlineplayers":
                ret = getOnlineNum(paramData);
                break;

            // 获取服务器当前运行状态
            case "getserverruntimestatus":
                ret = getRuntimeStatus(paramData);
                break;
            // 设置服务器最大在线人数
            case "maxonlinenum":
                ret = setMaxOnlineNum(paramData);
                break;

            // ------------------------玩家信息相关操作--------------------------------
            // 查询玩家信息
            case "getplayerinfo":
                ret = getPlayerInfo(paramData);
                break;

            // 踢玩家下线
            case "kickoff":
                ret = kickPlayerOffLine(paramData);
                break;

            // 设置玩家是否可聊天
            case "chat":
                ret = setPlayerChat(paramData);
                break;
            //
            // // 设置玩家是否可登陆
            // case "login":
            // ret = GMPlayerHandler.getInstance().setPlayerLogin(paramData);
            // break;
            //
            // // 设置玩家身份(普通或者管理员)
            // case "playeridentity":
            // ret = GMPlayerHandler.getInstance().setPlayerIdentity(paramData);
            // break;

            default:
                LOGGER.error(String.format("can not find operation : %s", command));
        }
        LOGGER.debug(ConstDefine.LOG_HTTP_PREFIX + "---ret=" + ret);
        return ret;
    }

    /**
     * 获取在线人数
     * 
     * @param paramData
     * @return
     */
    public String getOnlineNum(String paramData) {
        List<onlinePlayersNum> onlineList = new ArrayList<onlinePlayersNum>();
        List<Player> players = PlayerManager.getAllPlayers();
        Map<String, Map<String, Integer>> onlineMaps = Maps.newHashMap();
        for (Player p : players) {
            if (!p.isOnline()) {
                continue;
            }
            String key = p.getChannelId();
            Map<String, Integer> map = onlineMaps.get(key);
            if (map == null) {
                map = Maps.newHashMap();
                onlineMaps.put(key, map);
            }
            int old = ToolMap.getInt(p.getChannelAppId(), map);
            map.put(p.getChannelAppId(), old + 1);
        }

        for (Entry<String, Map<String, Integer>> entry : onlineMaps.entrySet()) {
            String channelId = entry.getKey();
            for (Entry<String, Integer> en : entry.getValue().entrySet()) {
                String channelAppId = en.getKey();
                int onlineNum = en.getValue();
                onlinePlayersNum channelOnline =
                        new onlinePlayersNum(channelId, channelAppId, onlineNum);
                onlineList.add(channelOnline);
            }
        }

        JSONObject response = getSuccessJsonObject();

        response.put("online", onlineList);

        return response.toJSONString();
    }

    public String getRuntimeStatus(String paramData) {

        JSONObject response = getSuccessJsonObject();

        response.put("ServerStatus", getCurrentServerState());

        return response.toJSONString();
    }

    public String setMaxOnlineNum(String paramData) {
        String rst = null;
        try {
            JSONObject request = JSON.parseObject(paramData);
            int num = request.getIntValue("maxonlinenum");
            if (num > 0) {
                GlobalService.getInstance().modifyOnlineMax(num);
            }
            JSONObject response = getSuccessJsonObject();
            rst = response.toJSONString();
        } catch (Exception e) {
            String msg = ExceptionEx.e2s(e);
            LOGGER.error(msg);
            rst = getError(GMHandlerErrorCode.ERROR_MAX_ONLINE_NUM, msg);
        }
        return rst;
    }



    public String reloadCsvFiles(String paramData) {
        JSONObject request = JSON.parseObject(paramData);
        String rst = null;
        // 通知游戏服文件的地址
        String fileURL = request.getString("path");
        try {
            CsvUtils.reloadCsvFiles(fileURL);
            FunctionSwitchService.getInstance().clear();// 清理全服开关，获取时会自动检查重载
            JSONObject response = getSuccessJsonObject();
            response.put("desc", "reloadCsvFiles:" + fileURL);
            rst = response.toJSONString();
        } catch (Exception e) {
            String msg = ExceptionEx.e2s(e);
            LOGGER.error(msg);
            rst = getError(GMHandlerErrorCode.ERROR_LOAD_CSV, msg);
        }
        return rst;
    }


    public String reloadClass(String paramData) {
        String rst = null;
        try {
            JSONObject request = JSON.parseObject(paramData);
            // 通知游戏服文件的地址
            String fileURL = request.getString("path");
            if (fileURL != null) {
                // 下载并解压
                String fileName = "scripts.zip";
                fileURL += fileName;
                HttpUtil.downloadFile(fileURL, SCRIPTS_LOCAL_DIR);
                ZipUtil.unzip(SCRIPTS_LOCAL_DIR + fileName, SCRIPTS_LOCAL_DIR);
            }
            ScriptManager.getInstance().loadAllScript();
            JSONObject response = getSuccessJsonObject();
            response.put("desc", "reloadClass:" + fileURL);
            rst = response.toJSONString();
        } catch (Exception e) {
            String msg = ExceptionEx.e2s(e);
            LOGGER.error(msg);
            rst = getError(GMHandlerErrorCode.ERROR_LOAD_CLASS, msg);
        }
        return rst;
    }


    public String getPlayerInfo(String paramData) {

        JSONObject request = JSON.parseObject(paramData);
        // 玩家id
        int playerId = request.getIntValue("playerId");

        // 查询玩家数据
        if (playerId <= 0) {
            return getError();
        }
        Player player = null;
        try {
            player = PlayerManager.getPlayerByPlayerId(playerId);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        // 找不到玩家
        if (player == null) {
            PlayerDaoService playerDaoService =
                    SpringContextUtils.getBean(ESpringConextType.PlAYER.getType(),
                            PlayerDaoService.class);
            try {
                PlayerDBBean roleBean = playerDaoService.selectByPlayerId(playerId);
                player = new Player(roleBean, false);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
            if (player == null) {
                return getError(GMHandlerErrorCode.ERROR_NO_PLAYER);
            }
        }
        JSONObject jsonRsopnse = getSuccessJsonObject();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("playerid", player.getPlayerId());
        jsonObject.put("playername", player.getPlayerName());
        jsonObject.put("username", player.getUserName());
        jsonObject.put("createtime", player.getCreateTime());
        jsonObject.put("logintime", player.getLoginTime());
        jsonObject.put("isonline", player.isOnline());
        jsonObject.put("offlinetime", player.getOfflineTime());
        jsonObject.put("channelid", player.getChannelId());
        jsonObject.put("channelappid", player.getChannelAppId());
        jsonObject.put("currentserver", player.getCurrentServer());
        // jsonObject.put("createserver", playerBean.getCreateServer());
        jsonObject.put("ip", player.getIP());
        // 账号被封
        jsonObject.put("isforbid", player.getIsforbid());
        jsonObject.put("level", player.getLevel());
        jsonObject.put("viplevel", player.getVipLevel());
        // GM等级
        jsonObject.put("gmlevel", player.getGmLevel());
        // 经验值
        jsonObject.put("exp", player.getExp());
        jsonObject.put("gold", player.getGold());
        // 系统钻石
        jsonObject.put("systemdiamond", player.getSystemDiamond());
        // 充值钻石
        jsonObject.put("rechargediamond", player.getRechargeDiamond());
        // 战斗力
        jsonObject.put("fightpower", player.getHeroManager().getHelpHeroFightPower());
        // 体力
        jsonObject.put("strength", player.getStrength());


        // 体力
        jsonObject.put("data", "");

        jsonRsopnse.put("playerInfo", jsonObject);

        return jsonRsopnse.toJSONString();
    }

    public String kickPlayerOffLine(String paramData) {
        JSONObject request = JSON.parseObject(paramData);
        // 踢玩家下线
        // 玩家id
        int playerId = request.getIntValue("playerId");
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

        return getSuccess();
    }

    public String setPlayerChat(String paramData) {
        JSONObject request = JSON.parseObject(paramData);
        // 玩家id
        int playerId = request.getIntValue("playerId");
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player != null && player.isOnline()) {
            // true 为可以发言， false为不可以发言
            boolean keepSilent = request.getBoolean("state");
            ChannelInfo channelInfo =
                    player.getCtx().channel().attr(AttributeKeys.CHANNEL_INFO).get();
            channelInfo.setKeepSilent(keepSilent);
            LOGGER.info("开启:" + playerId + "        聊天功能：" + keepSilent);
        }

        return getSuccess();
    }

    /**
     * 服务器当前状态
     * 
     * @return
     */
    public static String getCurrentServerState() {
        JSONObject serverState = new JSONObject();
        int specialId = ServerConfig.getInstance().getSpecialId();
        int serverId = ServerConfig.getInstance().getServerId();
        String serverName = ServerConfig.getInstance().getServerName();
        Runtime runtime = Runtime.getRuntime();
        long startTime = GameServer.getInstance().getStartTime();
        long reloadGameDataTime = GameServer.getInstance().getReloadGameDataTime();
        long reloadJavaScriptTime = GameServer.getInstance().getReloadJavaScriptTime();
        long freeMem = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        int totalSize = 0;
        int online = 0;
        int offline = 0;
        List<Player> players = PlayerManager.getAllPlayers();
        totalSize = players.size();
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p.isOnline())
                online++;
            else {
                offline++;
            }
        }
        serverState.put("groupId", specialId);
        serverState.put("serverId", serverId);
        serverState.put("serverName", serverName);
        serverState.put("isTestServer", ServerConfig.getInstance().isTestServer());
        serverState.put("status", GameServer.getInstance().getStatus());
        serverState.put("startTime",
                DateEx.format(new Date(startTime), DateEx.fmt_yyyy_MM_dd_HH_mm_ss));
        serverState.put("reloadGameDataTime",
                DateEx.format(new Date(reloadGameDataTime), DateEx.fmt_yyyy_MM_dd_HH_mm_ss));
        serverState.put("reloadJavaScriptTime",
                DateEx.format(new Date(reloadJavaScriptTime), DateEx.fmt_yyyy_MM_dd_HH_mm_ss));
        serverState.put("freeMem", toShowSize(freeMem));
        serverState.put("totalMem", toShowSize(totalMemory));

        serverState.put("version", GameServer.getInstance().getGameVersion());
        serverState.put("commit", GameServer.getInstance().getGameVersionTime());

        serverState.put("maxOnlineNum", GlobalService.ONLINE_MAX);
        serverState.put("onlineNum", online);
        serverState.put("offlineNum", offline);
        serverState.put("totalNum", totalSize);
        return serverState.toJSONString();
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

    public String getError() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", GMHandlerErrorCode.ERROR_NO_API);
        jsonObject.put("desc", "no api!");

        return jsonObject.toJSONString();
    }

    public String getError(int error) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", error);
        jsonObject.put("desc", "fail");

        return jsonObject.toJSONString();
    }

    public String getError(int error, String desc) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", error);
        jsonObject.put("desc", desc);

        return jsonObject.toJSONString();
    }

    public String getSuccess() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", GMHandlerErrorCode.SUCESS);
        jsonObject.put("desc", "success");

        return jsonObject.toJSONString();
    }

    public JSONObject getSuccessJsonObject() {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("result", GMHandlerErrorCode.SUCESS);
        jsonObject.put("desc", "success");

        return jsonObject;
    }

    private static final Logger LOGGER = Logger.getLogger(GMHttpScript.class);
}
