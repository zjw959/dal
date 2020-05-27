package logic.login.struct;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
/**
 *
 */
public class TokenCache {
    
    public static TokenCache getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        TokenCache instance;

        private Singleton() {
            instance = new TokenCache();
        }

        TokenCache getInstance() {
            return instance;
        }
    }
    /**
     * KEY = UID
     * VAL = Token
     */
    private BiMap<String,String> cache = HashBiMap.create();

    public void addCache(String uid,String token){
        cache.put(uid,token);
    }

    public void updateCache(String uid,String token){
        cache.put(uid,token);
    }


    public String removeToken(String token){
        return cache.inverse().remove(token);
    }

    public void checkTimeOut(){
//        Date nowDate = new Date();
//        List<String> timeoutUids = new ArrayList<>();
//        for (Map.Entry<String,String> e : cache.entrySet()) {
//            Account account = AccountCache.me().getById(e.getKey());
//            if (ToolDate.addSecond(30, (Date) account.getModifiedDate().clone()).before(nowDate)) {
////				removeCache(e.getKey());
//                timeoutUids.add(e.getKey());
//                LogKit.info("remove token = " + e.getKey());
//            }
//        }
//        for (String uid : timeoutUids) {
//            cache.remove(uid);
//        }
    }
    
    public String getToken(String token){
    	return cache.inverse().get(token);
    }
    
    public static void main(String[] args) {
        String token = "de2b3b0a-c2bf-407c-b6f9-355c51cd6f49";
        String uid = "test001";
        TokenCache.getInstance().addCache(uid, token);
        String r = TokenCache.getInstance().getToken(token);
        System.out.println("--r="+r);
    }
}
