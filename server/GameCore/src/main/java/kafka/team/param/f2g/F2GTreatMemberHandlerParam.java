package kafka.team.param.f2g;

public class F2GTreatMemberHandlerParam {
    // 接受消息的玩家
    int playerId;
    // 如果是更换队长时 这个字段表示为新队长;如果是踢人 这个字段表示被踢的人
    int operatedPid;

    long teamId;

    int type;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOperatedPid() {
        return operatedPid;
    }

    public void setOperatedPid(int operatedPid) {
        this.operatedPid = operatedPid;
    }


}
