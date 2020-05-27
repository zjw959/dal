package logic.log.bean;

import logic.character.bean.Player;

public class HeroAngelLog extends ActionBaseLog {
    /** 精灵id */
    private int heroId;
    /** 精灵等级 */
    private int heroLevel;
    /** 天使觉醒变化 */
    private int angelAwakeChange;
    /** 天使觉醒等级 */
    private int angelAwake;
    /** 天使技能页 */
    private int angelStrategy;
    /** 天使技能 */
    private int angelSkill;
    /** 额外信息 */
    private String ext;
    
    public HeroAngelLog(Player player) {
        super(player, "heroangel");
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getHeroLevel() {
        return heroLevel;
    }

    public void setHeroLevel(int heroLevel) {
        this.heroLevel = heroLevel;
    }

    public int getAngelAwakeChange() {
        return angelAwakeChange;
    }

    public void setAngelAwakeChange(int angelAwakeChange) {
        this.angelAwakeChange = angelAwakeChange;
    }

    public int getAngelAwake() {
        return angelAwake;
    }

    public void setAngelAwake(int angelAwake) {
        this.angelAwake = angelAwake;
    }

    public int getAngelStrategy() {
        return angelStrategy;
    }

    public void setAngelStrategy(int angelStrategy) {
        this.angelStrategy = angelStrategy;
    }

    public int getAngelSkill() {
        return angelSkill;
    }

    public void setAngelSkill(int angelSkill) {
        this.angelSkill = angelSkill;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

}
