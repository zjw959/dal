package logic.dating.handler.script;

import java.util.List;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import script.IScript;

public interface IDailyDatingHandlerScript extends IScript{
    
    /**
     * 获取处理器识别号
     */
    public abstract int getHandlerIdentification();

    /**
     * 约会前置条件中断检测
     */
    public abstract void checkDating(Player player, DatingScriptDTO dto);

    /**
     * 处理约会所需资源
     */
    public abstract void handleDatingResource(Player player, DatingScriptDTO dto);

    /**
     * 获取剧本配置
     */
    public abstract DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto);

    /**
     * 获取约会看板娘列表
     */
    public abstract List<Integer> getDatingRoles(Player player, DatingScriptDTO dto);

    /**
     * 对剧本的中断检测
     */
    public abstract void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg);

    /**
     * 剧本创建后的处理,比如日志
     */
    public abstract void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record,
            Logger LOGGER);

    /** 根据不同类型约会获取相应的约会配置 */
    public abstract BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId);

    /** 根据不同类型约会获取相应的约会配置 */
    public abstract List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid);
    
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId);

    /**
     * 结算
     */
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record, Logger LOGGER);

}
