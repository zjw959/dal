package logic.log.bean;

import logic.character.bean.Player;

/**
 * 精灵日志
 */
public class HeroLog extends ActionBaseLog {

    private int templateId;
    private int expChange;
    private long exp;
    private int levelChange;
    private int level;
    private int starChange;
    private int star;
    private String ext;

    public HeroLog(Player player) {
        super(player, "hero");
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public int getExpChange() {
        return expChange;
    }

    public void setExpChange(int expChange) {
        this.expChange = expChange;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public int getLevelChange() {
        return levelChange;
    }

    public void setLevelChange(int levelChange) {
        this.levelChange = levelChange;
    }

    public int getStarChange() {
        return starChange;
    }

    public void setStarChange(int starChange) {
        this.starChange = starChange;
    }

}
