package redis;


import com.jarvis.cache.DBLoadHanler;

import db.game.bean.PlayerDBBean;

public class DBViewLoadHandler implements DBLoadHanler {
    @Override
    public boolean isDBLoad(Object result) {
        if (result instanceof PlayerDBBean) {
            return !((PlayerDBBean) result).getIsOnline();
        }
        return DBLoadHanler.super.isDBLoad(result);
    }
}
