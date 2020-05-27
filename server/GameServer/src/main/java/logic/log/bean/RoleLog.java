package logic.log.bean;

import logic.character.bean.Player;

/**
 * 看板娘日志
 *
 */
public class RoleLog extends ActionBaseLog {

    private int heroTmplId;
    private int favorChange;
    private int favor;
    private String ext;

    public RoleLog(Player player) {
        super(player,"role");
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }



    public int getHeroTmplId() {
        return heroTmplId;
    }

    public void setHeroTmplId(int heroTmplId) {
        this.heroTmplId = heroTmplId;
    }

    public int getFavorChange() {
        return favorChange;
    }

    public void setFavorChange(int favorChange) {
        this.favorChange = favorChange;
    }

    public int getFavor() {
        return favor;
    }

    public void setFavor(int favor) {
        this.favor = favor;
    }



}
