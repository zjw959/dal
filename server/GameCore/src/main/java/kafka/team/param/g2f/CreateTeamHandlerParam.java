package kafka.team.param.g2f;

public class CreateTeamHandlerParam {

    private int playerId;

    private String name;

    private int level;

    private int gameServerId;

    private int dungeonCid;

    private int heroCid;

    private int skinCid;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGameServerId() {
        return gameServerId;
    }

    public void setGameServerId(int gameServerId) {
        this.gameServerId = gameServerId;
    }

    public int getDungeonCid() {
        return dungeonCid;
    }

    public void setDungeonCid(int dungeonCid) {
        this.dungeonCid = dungeonCid;
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
