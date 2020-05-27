package kafka.team.param.g2g;

public class InviteTeamHandlerParam {

    public int playerId;

    private String senderName;

    private int senderLevel;

    private int senderHeroCid;

    public int targetPlayerId;

    public String content;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTargetPlayerId() {
        return targetPlayerId;
    }

    public void setTargetPlayerId(int targetPlayerId) {
        this.targetPlayerId = targetPlayerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public int getSenderLevel() {
        return senderLevel;
    }

    public void setSenderLevel(int senderLevel) {
        this.senderLevel = senderLevel;
    }

    public int getSenderHeroCid() {
        return senderHeroCid;
    }

    public void setSenderHeroCid(int senderHeroCid) {
        this.senderHeroCid = senderHeroCid;
    }



}
