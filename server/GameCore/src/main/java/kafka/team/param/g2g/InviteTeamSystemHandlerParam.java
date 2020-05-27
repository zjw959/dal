package kafka.team.param.g2g;

public class InviteTeamSystemHandlerParam {

    public int playerId;

    private String senderName;

    private int senderLevel;

    private int senderHeroCid;

    public String content;

    private int roomId;

    public InviteTeamSystemHandlerParam(int playerId, String senderName, int senderLevel,
            int senderHeroCid, String content, int roomId) {
        super();
        this.playerId = playerId;
        this.senderName = senderName;
        this.senderLevel = senderLevel;
        this.senderHeroCid = senderHeroCid;
        this.content = content;
        this.roomId = roomId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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
