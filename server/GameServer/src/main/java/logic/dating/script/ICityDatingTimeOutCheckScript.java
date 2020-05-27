package logic.dating.script;

import java.util.Date;
import logic.character.bean.Player;
import logic.dating.bean.CityDatingBean;

public interface ICityDatingTimeOutCheckScript {

    /**
     * 当前约会是否过期
     * 
     * @param dating
     * @param player
     * @return
     */
    boolean timeout(CityDatingBean dating, Player player, Date now);

}
