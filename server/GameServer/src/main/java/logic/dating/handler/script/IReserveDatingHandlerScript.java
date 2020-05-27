package logic.dating.handler.script;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CShareMsg;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DatingTypeConstant;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.logic.ReserveDatingHandler;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DatingMsgBuilder;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import script.IScript;
import thread.log.LogProcessor;
import utils.ExceptionEx;

public interface IReserveDatingHandlerScript extends IScript {


    public int getHandlerIdentification();


    public void checkDating(Player player, DatingScriptDTO dto);

    public void handleDatingResource(Player player, DatingScriptDTO dto);

    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto);

    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto);

    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg);

    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto);

    public Map<Integer, List<Integer>> getScriptBranchNode(Player player, int scriptId,
            DatingScriptDTO dto);

    public boolean CityDatingBeanExists(Player player, DatingScriptDTO dto);

    public CityDatingBean getCityDatingRecord(Player player, DatingScriptDTO dto);

    /** 可能已经删除了约会记录,后续的操作都需要对记录判定以从缓存获取被删除的剧本id */
    public int getCurrentDatingRuleCfgId(Player player, DatingScriptDTO dto);

    /**
     * 获取预约约会的时间状态
     *
     * @return 0：正常约会时间 1：迟到，但未跨天 2：迟到且跨天
     */
    public int getReverseDatingTimeState(CityDatingBean record);

    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record);

    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record);

    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record,Logger LOGGER);


    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId);

    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId);

    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid);

}
