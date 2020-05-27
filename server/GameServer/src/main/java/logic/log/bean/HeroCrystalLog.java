package logic.log.bean;

import logic.character.bean.Player;

public class HeroCrystalLog extends ActionBaseLog {
    /** 精灵id */
    private int heroId;
    /** 精灵品质 */
    private int rarity;
    /** 结晶格子 */
    private int gridId;
    /** 额外信息 */
    private String ext;
    
    public HeroCrystalLog(Player player) {
        super(player, "herocrystal");
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

}
