package javascript.logic.dating.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cn.hutool.core.util.RandomUtil;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import data.bean.DiscreteDataCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DatingTypeConstant;
import logic.constant.DiscreteDataID;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.IDailyDatingHandlerScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.CommonUtil;
import utils.ExceptionEx;

public class DailyHandlerScript implements IDailyDatingHandlerScript {

    @Override
    public int getHandlerIdentification() {
        return DatingTypeConstant.DATING_TYPE_DAILY;
    }


    @Override
    public void handleDatingResource(Player player, DatingScriptDTO dto) {
    
    }

    @Override
    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto) {
        // 需要看板娘模块支持
        Role role = player.getRoleManager().getRole(dto.getRoleId());
        return getDatingRuleId(role, dto.getBuildingId(), player);
    }

    @Override
    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto) {
        return Arrays.asList(dto.getRoleId());
    }

    /**
     * 根据区域id获取剧本id
     * 
     * @param buildingCid 区域id dayType 白天还是黑夜
     * @return
     */
    public DatingRuleCfgBean getDatingRuleId(Role role, int buildingCid, Player player) {
        // 需要看板娘模块支持
        List<DatingRuleCfgBean> allCfgList = LogicScriptsUtils.getIDatingHandlerScript()
                .getDatingRuleByRoleIdDatingType(role.getCid(), getHandlerIdentification());
        List<DatingRuleCfgBean> tempList = Lists.newArrayList();
        AtomicInteger maxWeight = new AtomicInteger();

        allCfgList.forEach(ruleCfg -> {
            // 条件1：建筑id
            Integer condition = DatingService.getInstance().getDatingRuleEnterCondtionByType(
                    ruleCfg, DatingConstant.ENTER_CDTION_KEY_BUILD_CID);
            if (condition != 0 && condition != buildingCid)
                return;
            // 条件2：好感度
            condition = DatingService.getInstance().getDatingRuleEnterCondtionByType(ruleCfg,
                    DatingConstant.ENTER_CDTION_KEY_FAVOR);
            // 获取看板娘好感度
            if (condition > role.getFavor())
                return;

            // 判断当前是白天还是黑夜满足条件 策划修改了判定条件
            
//            Integer time = DatingService.getInstance().getDatingRuleEnterCondtionByType(ruleCfg,
//                    DatingConstant.ENTER_CDTION_KEY_DAYTYPE);
//            if (time != player.getNewBuildingManager().getDayType())
//                return;

            // 是否通过当前关卡
            Integer pass = DatingService.getInstance().getDatingRuleEnterCondtionByType(ruleCfg,
                    DatingConstant.ENTER_CDTION_KEY_PASS);
            if (pass <= 0)
                return;
            if (!player.getDungeonManager().checkDungeonPass(pass))
                return;

            maxWeight.addAndGet(DatingService.getInstance().getDatingRuleEnterCondtionByType(
                    ruleCfg, DatingConstant.ENTER_CDTION_KEY_WEIGHT));
            tempList.add(ruleCfg);
        });
        int max = maxWeight.get();
        if (max > 0) {
            int randomWeight = RandomUtil.randomInt(max);
            int weight = 0;
            for (DatingRuleCfgBean ruleCfg : tempList) {
                weight += DatingService.getInstance().getDatingRuleEnterCondtionByType(ruleCfg,
                        DatingConstant.ENTER_CDTION_KEY_WEIGHT);
                if (randomWeight <= weight)
                    return ruleCfg;
            }
        }
        return null;
    }

    @Override
    public void checkDating(Player player, DatingScriptDTO dto) {

        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_DAILY)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:date_daily");
        }

        // 对玩家接口逻辑的判定拦截
        if (!player.getBagManager().enoughByTemplateId(ItemConstantId.DAILY_DATING_COUNT, 1))
            MessageUtils.throwCondtionError(GameErrorCode.DAILY_DATING_COUNT_IS_ZERO, "日常约会次数已经用完");

        DiscreteDataCfgBean dataCfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DAILY_DATING_COST);
        if (dataCfg != null) {
            int need = (int) dataCfg.getData().get(1);
            Map<Integer, Integer> cost = Maps.newHashMap();
            cost.put(ItemConstantId.CITY_ENERGY, need);
            boolean isEnough = player.getBagManager().removeItemsByTemplateIdWithCheck(cost, true,
                    EReason.DAILY_DATING);
            if (!isEnough)
                // 精力不足
                MessageUtils.throwCondtionError(GameErrorCode.NOT_ENERGY, "精力不足");
        }
    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record,
            Logger LOGGER) {
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.DAILY_DATING_STIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId) {
        DatingRuleCfgBean bean = GameDataManager.getDatingRuleCfgBean(datingRuleId);
        return GameDataManager.getBaseDating(bean.getCallTableName()).get(datingCid);
    }

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {
        // 对玩家接口逻辑的判定拦截
        if (ruleCfg == null)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR);
        // 扣除约会次数,继续使用原设计,适配客户端协议
        BagManager bm = player.getBagManager();
        bm.removeItemsByTemplateIdWithCheck(
                CommonUtil.packageMap(ItemConstantId.DAILY_DATING_COUNT, 1), true,
                EReason.DATING_BEGIN);

    }

    @Override
    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
        DatingRuleCfgBean bean = GameDataManager.getDatingRuleCfgBean(scriptCid);
        List<BaseDating> list = new ArrayList<BaseDating>();
        for (BaseDating dating : GameDataManager.getBaseDating(bean.getCallTableName()).values()) {
            if (dating.getScriptId() == scriptCid)
                list.add(dating);
        }
        return list;
    }

    @Override
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record, Logger LOGGER) {
        LogicScriptsUtils.getIDatingHandlerScript().settlement(player, selectedNodeCid, datingType,
                roleCid, record, LOGGER);
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.DAILY_DATING_ETIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
        DatingManager dm = player.getDatingManager();
        // 此逻辑可以直接用id做索引查找,用组合条件的原因？——客户端设计当前约会对象唯一（玩家使用客户端只能同时进行一场约会）,未发送id，可以后续改写
        CurrentDatingBean record = dm.getByDatingTypeRoleId(datingType, roleId);
        return record;
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DAILLY_HANDLERHANDLER_SCRIPT.Value();
    }


}
