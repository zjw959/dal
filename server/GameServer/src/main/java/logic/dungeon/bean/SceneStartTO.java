package logic.dungeon.bean;

import java.util.List;

/**
 * 副本开始数据对象
 * 
 * @author Alan
 *
 */
public class SceneStartTO {
    int cid;
    int quickBattleCount = 0;
    boolean isDuelMod;
    int helpPlayerId;
    int helpHeroCid;
    List<DungeonLimitHeroTO> limitedHeros;

    public SceneStartTO(int cid, int quickBattleCount, boolean isDuelMod, int helpPlayerId,
            int helpHeroCid, List<DungeonLimitHeroTO> limitedHeros) {
        super();
        this.cid = cid;
        this.quickBattleCount = quickBattleCount;
        this.isDuelMod = isDuelMod;
        this.helpPlayerId = helpPlayerId;
        this.helpHeroCid = helpHeroCid;
        this.limitedHeros = limitedHeros;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getQuickBattleCount() {
        return quickBattleCount;
    }

    public void setQuickBattleCount(int quickBattleCount) {
        this.quickBattleCount = quickBattleCount;
    }

    public boolean isDuelMod() {
        return isDuelMod;
    }

    public void setDuelMod(boolean isDuelMod) {
        this.isDuelMod = isDuelMod;
    }

    public int getHelpPlayerId() {
        return helpPlayerId;
    }

    public void setHelpPlayerId(int helpPlayerId) {
        this.helpPlayerId = helpPlayerId;
    }

    public int getHelpHeroCid() {
        return helpHeroCid;
    }

    public void setHelpHeroCid(int helpHeroCid) {
        this.helpHeroCid = helpHeroCid;
    }

    public List<DungeonLimitHeroTO> getLimitedHeros() {
        return limitedHeros;
    }

    public void setLimitedHeros(List<DungeonLimitHeroTO> limitedHeros) {
        this.limitedHeros = limitedHeros;
    }

}
