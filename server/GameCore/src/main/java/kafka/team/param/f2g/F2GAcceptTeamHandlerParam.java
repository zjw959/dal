package kafka.team.param.f2g;

public class F2GAcceptTeamHandlerParam {

    public int playerId;

    public boolean isEntrant;

    public long teamId;

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

    public boolean isEntrant() {
        return isEntrant;
    }

    public void setEntrant(boolean isEntrant) {
        this.isEntrant = isEntrant;
    }

}
