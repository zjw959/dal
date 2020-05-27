package kafka.team.param.f2g;

public class F2GChasmFightReviveParam {
    private int playerId;
    private boolean isSuccess;
    private int reliveCount;
    private int dungeonCid;

    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    
    public int getReliveCount() {
        return reliveCount;
    }
    
    public void setReliveCount(int reliveCount) {
        this.reliveCount = reliveCount;
    }
    
    public int getDungeonCid() {
        return dungeonCid;
    }
    
    public void setDungeonCid(int dungeonCid) {
        this.dungeonCid = dungeonCid;
    }
}
