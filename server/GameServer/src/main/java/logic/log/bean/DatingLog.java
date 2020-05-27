package logic.log.bean;

import logic.character.bean.Player;

/**
 * 约会日志
 *
 */
public class DatingLog extends ActionBaseLog {

    private int templateId;
    private int heroTmplId;
    private int itemCG;
    private String ext;
    private long time;

    public DatingLog(Player player) {
        super(player, "dating");
    }


    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getHeroTmplId() {
        return heroTmplId;
    }

    public void setHeroTmplId(int heroTmplId) {
        this.heroTmplId = heroTmplId;
    }

    public int getItemCG() {
        return itemCG;
    }

    public void setItemCG(int itemCG) {
        this.itemCG = itemCG;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


}
