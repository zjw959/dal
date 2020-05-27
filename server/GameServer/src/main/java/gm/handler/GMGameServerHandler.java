//package gm.handler;
//
//import gm.GMHandlerErrorCode;
//import gm.bean.GameServerSwitch;
//import gm.bean.onlinePlayersNum;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import logic.character.PlayerManager;
//import logic.character.bean.Player;
//import net.http.HttpRequestWrapper;
//
//import org.apache.log4j.Logger;
//
//import server.GameServer;
//import utils.CsvUtils;
//import utils.ExceptionEx;
//import utils.ToolMap;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.google.common.collect.Maps;
//
///***
// *** @author: King
// *** @date : 2018年6月27日 下午12:13:35
// ***/
//
//public class GMGameServerHandler extends HttpBaseHandler {
//	private static final GMGameServerHandler DEFAULT = new GMGameServerHandler();
//
//	Logger log = Logger.getLogger(GMGameServerHandler.class);
//
//	public static GMGameServerHandler getInstance() {
//		return DEFAULT;
//	}
//	
//	public String handler(HttpRequestWrapper httpRequest) {
//		return "";
//	}
//	
//    /**
//     * 获取在线人数
//     * 
//     * @param paramData
//     * @return
//     */
//	public String getOnlineNum(String paramData) {
//		List<onlinePlayersNum> onlineList = new ArrayList<onlinePlayersNum>();
//        List<Player> players = PlayerManager.getAllPlayers();
//        Map<String, Map<String, Integer>> onlineMaps = Maps.newHashMap();
//        for (Player p : players) {
//            if (!p.isOnline()) {
//                continue;
//            }
//            String key = p.getChannelId();
//            Map<String, Integer> map = onlineMaps.get(key);
//            if (map == null) {
//                map = Maps.newHashMap();
//                onlineMaps.put(key, map);
//            }
//            int old = ToolMap.getInt(p.getChannelAppId(), map);
//            map.put(p.getChannelAppId(), old + 1);
//        }
//		
//        for (Entry<String, Map<String, Integer>> entry : onlineMaps.entrySet()) {
//            String channelId = entry.getKey();
//            for (Entry<String, Integer> en : entry.getValue().entrySet()) {
//                String channelAppId = en.getKey();
//                int onlineNum = en.getValue();
//                onlinePlayersNum channelOnline =
//                        new onlinePlayersNum(channelId, channelAppId, onlineNum);
//                onlineList.add(channelOnline);
//            }
//		}
//		
//		JSONObject response = getSuccessJsonObject();
//		
//		response.put("online", onlineList);
//		
//		return response.toJSONString();
//	}
//	
//	public String getRuntimeStatus(String paramData){
//		
//		JSONObject response = getSuccessJsonObject();
//		
//        response.put("ServerStatus", GameServer.getInstance().getStatus().toString());
//		
//		return response.toJSONString();
//	}
//	
//
//	public String setServerSwith(String paramData){
//		int result = 0;
//		
//		JSONObject request = JSON.parseObject(paramData);
//		//switch id
//		int switchId = request.getInteger("switch");
//		//
//		int status = request.getInteger("status");
//		
//		log.info("设置服务器开关:"+ switchId + "        状态："+ status);
//		
//		//踢玩家下线
//		return getSuccess();
//	}
//	
//	public String setSingleServerSwith(String paramData){
//		int result = 0;
//		
//		JSONObject request = JSON.parseObject(paramData);
//		//switch id
//		int switchId = request.getInteger("switch");
//		//
//		int status = request.getInteger("status");
//		
//		log.info("设置服务器开关:"+ switchId + "        状态："+ status);
//		
//		//踢玩家下线
//		return getSuccess();
//	}
//	
//	public String setBathServerSwith(String paramData){
//		int result = 0;
//		
//		JSONObject request = JSON.parseObject(paramData);
//		//switch id
////		int switchId = request.getInteger("switchList");
//		
//		List<Integer> switchList = JSON.parseArray(request.getString("switchList"), Integer.class);
//		//
//		int status = request.getInteger("status");
//		
////		log.info("设置服务器开关:"+ switchId + "        状态："+ status);
//		for (Integer tmp : switchList){
//			log.info("设置服务器开关:"+ tmp + "        状态："+ status);
//		}
//		
//		//踢玩家下线
//		return getSuccess();
//	}
//	
//	
//	public String getSwithList(String paramData){
//		List<GameServerSwitch> switchList = new ArrayList<GameServerSwitch>();
//		
////		for test
//		for (int i = 1; i < 5; i++){
//			GameServerSwitch switchInfo = new GameServerSwitch(i, 500+i);
//			
//			switchList.add(switchInfo);
//		}
////		end test
//		
//		JSONObject response = getSuccessJsonObject();
//		
//		response.put("switchList", switchList);
//		
//		return response.toJSONString();
//	}
//
//	
//	
//	
//	
//	public String reloadCsvFiles(String paramData){
//		JSONObject request = JSON.parseObject(paramData);
//        String rst = null;
//		//通知游戏服文件的地址		
//		String fileURL = request.getString("path");
//        try {
//            CsvUtils.reloadCsvFiles(fileURL);
//            JSONObject response = getSuccessJsonObject();
//            response.put("desc", "reloadCsvFiles:" + fileURL);
//            rst = response.toJSONString();
//        } catch (Exception e) {
//            String msg = ExceptionEx.e2s(e);
//            log.error(msg);
//            rst = getError(GMHandlerErrorCode.ERROR_LOAD_CSV, msg);
//        }
//        return rst;
//	}
//	
//	
//	public String reloadClass(String paramData){
//		
//		JSONObject request = JSON.parseObject(paramData);
//		
//		//通知游戏服文件的地址		
//		String fileURL = request.getString("path");
//		
//		
//		JSONObject response = getSuccessJsonObject();
//
//		response.put("desc", "reloadClass:" + fileURL);
//		
//		return response.toJSONString();
//	}
//
//}
