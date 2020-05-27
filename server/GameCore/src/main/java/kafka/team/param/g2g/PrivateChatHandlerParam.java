package kafka.team.param.g2g;

public class PrivateChatHandlerParam {

    private int senderId;

    private String senderName;

    private int senderLevel;

    private int senderHeroCid;

    private int receiverId;

    private String content;

    public PrivateChatHandlerParam(int senderId, String senderName, int senderLevel,
            int senderHeroCid, int receiverId, String content) {
        super();
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderLevel = senderLevel;
        this.senderHeroCid = senderHeroCid;
        this.receiverId = receiverId;
        this.content = content;
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

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
