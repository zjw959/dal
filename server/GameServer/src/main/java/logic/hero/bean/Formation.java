package logic.hero.bean;

import java.util.ArrayList;
import java.util.List;

import org.game.protobuf.s2c.S2CPlayerMsg.FormationInfo;
import org.game.protobuf.s2c.S2CShareMsg;

/**
 * 阵型
 * 
 * @author ouyangcheng
 *
 */
public class Formation {
    private int type;
    private int status;
    private List<Integer> heroIds = new ArrayList<>();
    
    public void create(int type) {
        this.type = type;
    }
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Integer> getHeroIds() {
        return heroIds;
    }

    public FormationInfo.Builder buildFormationInfo(S2CShareMsg.ChangeType changeType) {
        FormationInfo.Builder formationInfoBuilder = FormationInfo.newBuilder();
        formationInfoBuilder.setCt(changeType);
        formationInfoBuilder.setType(type);
        formationInfoBuilder.setStatus(status);
        List<String> heroIdStrs = new ArrayList<>();
        for(Integer heroId : heroIds) {
            heroIdStrs.add(String.valueOf(heroId));
        }
        formationInfoBuilder.addAllStance(heroIdStrs);
        return formationInfoBuilder;
    }
}
