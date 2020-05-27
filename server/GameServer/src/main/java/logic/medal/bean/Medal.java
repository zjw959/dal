package logic.medal.bean;

/**
 * 勋章
 */
public class Medal {
    /**
     * 配置模板
     */
    private int cid;
    /**
     * 星级
     */
    private int star;
    /**
     * 品质
     */
    private int quailty;
    /**
     * 到期时间
     */
    private long effectTime;
    /**
     * 
     */
    private boolean isEquip;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getQuailty() {
        return quailty;
    }

    public void setQuailty(int quailty) {
        this.quailty = quailty;
    }

    public long getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(long effectTime) {
        this.effectTime = effectTime;
    }

    public boolean isEquip() {
        return isEquip;
    }

    public void setEquip(boolean isEquip) {
        this.isEquip = isEquip;
    }

    public Medal(int cid, int star, int quailty, long effectTime, boolean isEquip) {
        super();
        this.cid = cid;
        this.star = star;
        this.quailty = quailty;
        this.effectTime = effectTime;
        this.isEquip = isEquip;
    }

    public void takeOff() {
        this.isEquip = false;
    }

    public void equip() {
        this.isEquip = true;
    }
}
