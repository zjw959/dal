package logic.log.bean;

import logic.character.bean.Player;

public class ItemGenLog extends ItemBaseLog {

    private long uid;
    private int templateId;
    private int reason;
    private int gainNum;
    private int totalNum;
    private boolean fromMail;
    private String ext;

    public ItemGenLog(Player player) {
        super(player, "itemGen");
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getGainNum() {
        return gainNum;
    }

    public void setGainNum(int gainNum) {
        this.gainNum = gainNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public boolean isFromMail() {
        return fromMail;
    }

    public void setFromMail(boolean fromMail) {
        this.fromMail = fromMail;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
