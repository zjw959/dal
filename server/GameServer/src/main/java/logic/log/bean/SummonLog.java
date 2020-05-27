package logic.log.bean;

import logic.character.bean.Player;

public class SummonLog extends ActionBaseLog {
    private String ext;
    
    public SummonLog(Player player) {
        super(player, "summon");
    }
    
    public String getExt() {
        return ext;
    }
    
    public void setExt(String ext) {
        this.ext = ext;
    }
}
