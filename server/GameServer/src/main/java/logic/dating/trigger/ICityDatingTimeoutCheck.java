package logic.dating.trigger;

import java.util.Date;

import logic.character.bean.Player;
import logic.dating.bean.CityDatingBean;

/**
 * 城市约会过期检测
 * @author Alan
 *
 */
public interface ICityDatingTimeoutCheck {
    /**
     * 当前约会是否过期
     * @param dating
     * @param player
     * @return
     */
    boolean timeout(CityDatingBean dating, Player player, Date now);
}
