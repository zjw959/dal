package redis.base;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import com.jarvis.cache.to.HashRedisObject;
import db.game.bean.PlayerDBBean;
import utils.ExceptionEx;

public class PlayerSnap implements HashRedisObject {
    private transient static final Logger LOGGER = Logger.getLogger(PlayerSnap.class);

    public int playerId;
    public String name;
    public int level;
    public int heroId;
    public int skinCid;
    public String describe;
    public long lastLoginTime;
    public boolean isOnline;
    public int fightpower;
    public long teamId;
    // 创建队伍 同意加入队伍 申请单人匹配的时间
    public long teamTime;

    public PlayerSnap(PlayerDBBean obj) {
        if (obj == null) {
            LOGGER.error("obj is null:" + ExceptionEx.currentThreadTraces());
            return;
        }

        this.playerId = obj.getPlayerId();
        this.name = obj.getPlayername();
        if(this.name == null) {
            this.name = "";
            LOGGER.error("name is null" + ExceptionEx.currentThreadTraces());
        }
        this.level = obj.getLevel();
        this.heroId = obj.getHeroId();
        this.skinCid = obj.getSkinCid();
        this.describe = obj.getDescribe();
        if (this.describe == null) {
            this.describe = "";
            LOGGER.error("describe is null" + ExceptionEx.currentThreadTraces());
        }
        this.lastLoginTime = obj.getLastlogintime();
        this.isOnline = obj.getIsOnline();
        this.fightpower = obj.getFightpower();
    }

    public PlayerSnap(Map<String, String> map) {
        try {
            this.playerId = Integer.valueOf(map.get("playerid"));
            this.name = map.get("name");
            this.level = Integer.valueOf(map.get("level"));
            this.heroId = Integer.valueOf(map.get("heroid"));
            this.skinCid = Integer.valueOf(map.get("skinCid"));
            this.describe = map.get("describe");
            this.lastLoginTime = Long.valueOf(map.get("lastlogintime"));
            this.isOnline = Boolean.valueOf(map.get("isonline"));
            this.fightpower = Integer.valueOf(map.get("fightpower"));
        } catch (Exception e) {
            LOGGER.error("mapdata:" + map + ExceptionEx.e2s(e));
            throw e;
        }
    }

    @Override
    public HashMap<String, String> toHash() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("playerid", String.valueOf(this.playerId));
        map.put("name", this.name);
        map.put("level", String.valueOf(this.level));
        map.put("heroid", String.valueOf(this.heroId));
        map.put("skinCid", String.valueOf(this.skinCid));
        map.put("describe", this.describe);
        map.put("lastlogintime", String.valueOf(this.lastLoginTime));
        map.put("isonline", String.valueOf(this.isOnline));
        map.put("fightpower", String.valueOf(this.fightpower));
        return map;
    }
}
