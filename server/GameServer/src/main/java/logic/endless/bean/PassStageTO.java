package logic.endless.bean;

import java.util.Map;

/**
 * 通关数据对象
 * 
 * @author Alan
 *
 */
public class PassStageTO {
    int levelCid;
    int costTime;
    Map<Integer, Integer> passRewards;

    public PassStageTO(int levelCid, int costTime) {
        super();
        this.levelCid = levelCid;
        this.costTime = costTime;
    }

    public int getLevelCid() {
        return levelCid;
    }

    public void setLevelCid(int levelCid) {
        this.levelCid = levelCid;
    }

    public int getCostTime() {
        return costTime;
    }

    public void setCostTime(int costTime) {
        this.costTime = costTime;
    }

    public Map<Integer, Integer> getPassRewards() {
        return passRewards;
    }

    public void setPassRewards(Map<Integer, Integer> passRewards) {
        this.passRewards = passRewards;
    }

}
