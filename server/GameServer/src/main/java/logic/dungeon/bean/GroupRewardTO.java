package logic.dungeon.bean;

/**
 * 关卡组奖励数据对象
 * 
 * @author Alan
 *
 */
public class GroupRewardTO {
    int levelGroupCid;
    int difficulty;
    String rewardInfo;
    String starInfo;
    int rewardId;
    int starNums;
    int heartNums;

    public GroupRewardTO(int levelGroupCid, int difficulty, String rewardInfo, String starInfo,
            int rewardId, int starNums, int heartNums) {
        super();
        this.levelGroupCid = levelGroupCid;
        this.difficulty = difficulty;
        this.rewardInfo = rewardInfo;
        this.starInfo = starInfo;
        this.rewardId = rewardId;
        this.starNums = starNums;
        this.heartNums = heartNums;
    }

    public int getLevelGroupCid() {
        return levelGroupCid;
    }

    public void setLevelGroupCid(int levelGroupCid) {
        this.levelGroupCid = levelGroupCid;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getRewardInfo() {
        return rewardInfo;
    }

    public void setRewardInfo(String rewardInfo) {
        this.rewardInfo = rewardInfo;
    }

    public String getStarInfo() {
        return starInfo;
    }

    public void setStarInfo(String starInfo) {
        this.starInfo = starInfo;
    }

    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }

    public int getStarNums() {
        return starNums;
    }

    public void setStarNums(int starNums) {
        this.starNums = starNums;
    }

    public int getHeartNums() {
        return heartNums;
    }

    public void setHeartNums(int heartNums) {
        this.heartNums = heartNums;
    }

}
