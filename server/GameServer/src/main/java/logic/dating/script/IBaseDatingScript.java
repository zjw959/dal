package logic.dating.script;

import org.apache.log4j.Logger;
import logic.character.bean.Player;
import logic.role.bean.Role;
import script.IScript;

public interface IBaseDatingScript extends IScript {

    /**
     * 获取约会类型
     */
    public abstract int getDatingType();

    /** 当前类型约会中,精灵是否可用 */
    public abstract boolean isRoleValid(Role role, Player player);

    /** 当前类型约会是否已经过期 */
    public abstract ICityDatingTimeOutCheckScript getTimeoutChecker(Player player,Logger LOGGER);

}
