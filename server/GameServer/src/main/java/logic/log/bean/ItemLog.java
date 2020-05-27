package logic.log.bean;

import logic.character.bean.Player;

public class ItemLog extends ItemBaseLog {

    private long uid;
    private int templateId;
    private int reason;
    private int usenum;
    private int surplusnum;
    private String ext;


    public ItemLog(Player player) {
        super(player, "item");
    }


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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

    public int getUsenum() {
        return usenum;
    }

    public void setUsenum(int usenum) {
        this.usenum = usenum;
    }

    public int getSurplusnum() {
        return surplusnum;
    }

    public void setSurplusnum(int surplusnum) {
        this.surplusnum = surplusnum;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
