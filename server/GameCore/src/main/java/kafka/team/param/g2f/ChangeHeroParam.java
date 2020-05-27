package kafka.team.param.g2f;

public class ChangeHeroParam {

    public int playerId;

    public long teamId;

    public int heroCid;

    public int skinCid;

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

    public int getHeroCid() {
        return heroCid;
    }

    public void setHeroCid(int heroCid) {
        this.heroCid = heroCid;
    }

    public int getSkinCid() {
        return skinCid;
    }

    public void setSkinCid(int skinCid) {
        this.skinCid = skinCid;
    }

}
