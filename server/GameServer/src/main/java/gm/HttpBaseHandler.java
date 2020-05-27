//package gm;
//
//import com.alibaba.fastjson.JSONObject;
//
//import db.game.bean.PlayerDBBean;
//import logic.character.PlayerManager;
//import logic.character.bean.Player;
//
//
//public class HttpBaseHandler {
//
////  public void setHttpKey(String key);
////  public String handler(HttpRequestWrapper httpRequest);
//    public String getError() {
//        JSONObject jsonObject = new JSONObject();
//        
//        jsonObject.put("result", -1);
//        jsonObject.put("desc", "no api!");
//        
//        return jsonObject.toJSONString();
//    }
//    
//    public String getError(int error) {
//        JSONObject jsonObject = new JSONObject();
//        
//        jsonObject.put("result", error);
//        jsonObject.put("desc", "fail");
//        
//        return jsonObject.toJSONString();
//    }
//    
//    public String getError(int error, String desc) {
//        JSONObject jsonObject = new JSONObject();
//        
//        jsonObject.put("result", error);
//        jsonObject.put("desc",   desc);
//        
//        return jsonObject.toJSONString();
//    }
//    
//    public String getSuccess() {
//        JSONObject jsonObject = new JSONObject();
//        
//        jsonObject.put("result", 0);
//        jsonObject.put("desc", "success");
//        
//        return jsonObject.toJSONString();
//    }
//    
//    public JSONObject getSuccessJsonObject(){
//        
//        JSONObject jsonObject = new JSONObject();
//        
//        jsonObject.put("result", 0);
//        jsonObject.put("desc", "success");
//        
//        return jsonObject;
//    }
//    
//    public PlayerDBBean getPlayerBeanWithId(int playerId) {
//        //先从缓存中查找角色     
//        Player viewPlayer = PlayerManager.getViewPlayerByPlayerId(playerId);
//        PlayerDBBean playerBean = null;
//        
//        if (viewPlayer == null){
//            //查找不到，则在redis中查找
//            
//            
//            //最后直接从数据库查找
//            // playerBean = PlayerDao.selectByPlayerId(playerId);
//            // if (playerBean == null){
//            // return null;
//            // }
//            
//
//            return playerBean;
//        }
//        
//        return viewPlayer.toPlayerBean();
//    }
//    
//    
//    public Player getPlayerWithId(int playerId){
//        //先从缓存中查找角色     
//        Player viewPlayer = PlayerManager.getViewPlayerByPlayerId(playerId);
//        PlayerDBBean playerBean = null;
//        
//        if (viewPlayer == null){
//            //查找不到，则在redis中查找
//            
//            
//            //最后直接从数据库查找
//            // playerBean = PlayerDao.selectByPlayerId(playerId);
//            // if (playerBean == null){
//            // return null;
//            // }
//            //
//            // try {
//            // viewPlayer = new Player(playerBean);
//            // } catch (Exception e) {
//            // return null;
//            // }
//        }
//        
//        return viewPlayer;
//    }
//}
