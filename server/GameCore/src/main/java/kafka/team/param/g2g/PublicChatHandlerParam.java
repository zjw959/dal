package kafka.team.param.g2g;

public class PublicChatHandlerParam {

    private int senderId;

    private String senderName;

    private int senderLevel;

    private int senderHeroCid;

    private String content;

    private int roomId;

    public PublicChatHandlerParam(int senderId, String senderName, int senderLevel,
            int senderHeroCid, String content, int roomId) {
        super();
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderLevel = senderLevel;
        this.senderHeroCid = senderHeroCid;
        this.content = content;
        this.roomId = roomId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

}
