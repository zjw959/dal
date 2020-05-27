package logic.chasm.bean;

import org.game.protobuf.s2c.S2CChasmMsg;

import logic.chasm.TeamService;

public class ChasmInfo {
    private int id;
    private int fightCount;
    
    public void create(int id) {
        this.id = id;
        this.fightCount = 0;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFightCount() {
        return fightCount;
    }

    public void setFightCount(int fightCount) {
        this.fightCount = fightCount;
    }
    
    public S2CChasmMsg.ChasmInfo buildChasmInfo() {
        S2CChasmMsg.ChasmInfo.Builder chasmInfoBuilder = S2CChasmMsg.ChasmInfo.newBuilder();
        chasmInfoBuilder.setId(id);
        int status = TeamService.getDefault().getChasmStatus(id) ? 1 : 0;
        chasmInfoBuilder.setStatus(status);
        chasmInfoBuilder.setFightCount(fightCount);
        return chasmInfoBuilder.build();
    }
}
