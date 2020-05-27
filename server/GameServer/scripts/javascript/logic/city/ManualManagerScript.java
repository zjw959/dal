package javascript.logic.city;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RespGetHandWorkAward.Builder;
import data.GameDataManager;
import data.bean.HandworkbaseCfgBean;
import logic.character.bean.Player;
import logic.city.BuildingGeneral;
import logic.city.build.BuildingConstant;
import logic.city.build.bean.BuildEvent;
import logic.city.script.IManualManagerScript;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;
import utils.TimeUtil;

/***
 * 
 * 手工小游戏脚本
 * 
 * @author lihongji
 *
 */
public class ManualManagerScript implements IManualManagerScript {

    /** 返回脚本的Id **/
    @Override
    public int getScriptId() {
        return EScriptIdDefine.CITY_MANUAL_SCRIPT.Value();
    }

    /** 检测当前是否能进行手工制作 **/
    @SuppressWarnings("unchecked")
    @Override
    public void checkManual(HandworkbaseCfgBean handworkbaseCfgBean, Player player) {
        if (handworkbaseCfgBean == null)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_MANUAL_ID_ERROR, "要制作的手工ID不存在");

        if (!BuildingGeneral.checkAbility(handworkbaseCfgBean.getAbility(), player)) {
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_MANUAL_ABILITY_IS_NOT_ENOUGH,
                    "能力不足");
        }
        if (handworkbaseCfgBean.getMaterials() != null
                && !player.getBagManager().enoughByTemplateId(handworkbaseCfgBean.getMaterials())) {
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_MANUAL_MATERIALS_IS_NOT_ENOUGH,
                    "要制作的手工ID的材料不足");
        }
        if (player.getManualManager().getEvent().getEtime() != 0) {
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_MANUAL_IS_NOT_COMPLETE, "手工未完成");
        }
        if (handworkbaseCfgBean.getMaterials() != null)
            player.getBagManager().removeItemsByTemplateIdNoCheck(
                    handworkbaseCfgBean.getMaterials(), true, EReason.MANUAL_COST);

    }

    /** 创建手工事件 **/
    @Override
    public void createManualEvent(int id, Player player, Logger LOGGER) {

        HandworkbaseCfgBean handworkbaseCfgBean = GameDataManager.getHandworkbaseCfgBean(id);
        checkManual(handworkbaseCfgBean, player);
        long etime =
                (handworkbaseCfgBean.getWorktime() * TimeUtil.SECOND) + System.currentTimeMillis();
        player.getManualManager().getEvent().initManualEvent(id, 0, etime);

        // 手工开始时间
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createCityGameLog(player, EReason.MANUAL_STARTTIME.value(), id));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder getAward(int manualId, Player player, Logger LOGGER) {
        if (player.getManualManager().getEvent().getManualId() != manualId
                || player.getManualManager().getEvent().getEtime() == 0) {
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR, "参数不一致");
        }
        HandworkbaseCfgBean handworkbaseCfgBean = GameDataManager.getHandworkbaseCfgBean(manualId);
        long timeNow = System.currentTimeMillis();
        if (timeNow < player.getManualManager().getEvent().getEtime()) {
            int addIntegral = (int) ((timeNow - (player.getManualManager().getEvent().getEtime()
                    - handworkbaseCfgBean.getWorktime() * TimeUtil.SECOND) / TimeUtil.SECOND)
                    * handworkbaseCfgBean.getNatureTime());
            player.getManualManager().getEvent().addIntegral(addIntegral);
        } else {
            int addIntegral =
                    (int) (handworkbaseCfgBean.getWorktime() * handworkbaseCfgBean.getNatureTime());
            player.getManualManager().getEvent().addIntegral(addIntegral);
        }
        List<Map<Integer, Map<Integer, Integer>>> list = handworkbaseCfgBean.getIntegral();
        Map<Integer, Integer> award = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            Set<Integer> set = list.get(i).keySet();
            Object[] object = set.toArray();
            if (player.getManualManager().getEvent().getIntegral() >= (Integer) (object[0])) {
                award = list.get(i).get((Integer) (object[0]));
                break;
            }
        }
        if (award == null || award.size() <= 0)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR, "积分不足领取奖励");
        // 重置当前记录
        player.getManualManager().getEvent().clear();
        player.getBagManager().addItems(award, true, EReason.MANUAL_AWARD);

        // 手工领奖时间
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createCityGameLog(player,
                    EReason.MANUAL_ENDTIME.value(), manualId));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }


        return NewBuildingMsgBuilder.packingHandWorkReward(manualId, award);
    }

	/** 检测客户端传递的手工操作积分 **/
	@Override
	public org.game.protobuf.s2c.S2CNewBuildingMsg.RespGetHandWorkInfo.Builder checkOperateIntegral(int manualId,
			int integral, Player player) {
        if (player.getManualManager().getEvent().getManualId() != manualId) {
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR, "参数不一致");
        }
		long timeOut = 10 * TimeUtil.SECOND;
		if (System.currentTimeMillis() > player.getManualManager().getEvent().getEtime() + timeOut)
			MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR, "超时");
		HandworkbaseCfgBean handworkbaseCfgBean = GameDataManager.getHandworkbaseCfgBean(manualId);
		if (handworkbaseCfgBean == null) {
			MessageUtils.throwCondtionError(GameErrorCode.BUILDING_MANUAL_ID_ERROR, "要制作的手工ID不存在");
		}
		player.getManualManager().getEvent().addTimes();
		player.getManualManager().getEvent().addIntegral(integral);
        return NewBuildingMsgBuilder.packageIngHandWorkInfo(player, false);
    }

    /** 检测 **/
    @SuppressWarnings("unchecked")
    @Override
    public void checkUnlock(int buildingId, Player player, Set<Integer> validManual) {
        Set<Integer> addCuisine = new HashSet<Integer>();
        List<BuildEvent> addEvent = new ArrayList<BuildEvent>();
        List<HandworkbaseCfgBean> list = GameDataManager.getHandworkbaseCfgBeans();
        list.forEach(handworkBean -> {
            if (!validManual.contains(handworkBean.getId())
                    && BuildingGeneral.checkAbility(handworkBean.getAbility(), player)) {
                validManual.add(handworkBean.getId());
                addCuisine.add(handworkBean.getId());
                BuildEvent event = BuildingGeneral.createBuildingEvent(buildingId,
                        BuildingConstant.EVENT_TYPE_CUISINE_ADD, 0, 0, player);
                addEvent.add(event);
            }
        });
        MessageUtils.send(player, NewBuildingMsgBuilder.addEvent(addEvent)
                .setDayType(player.getNewBuildingManager().getDayType()));

    }

    /** 获取自然增长量 **/
    @Override
    public int getNatureTime(HandworkbaseCfgBean handworkbaseCfgBean, Player player) {
        long timeNow = System.currentTimeMillis();
        int time = (int) ((timeNow - (player.getManualManager().getEvent().getEtime()
                - handworkbaseCfgBean.getWorktime() * TimeUtil.SECOND)) / TimeUtil.SECOND);
        if (time > handworkbaseCfgBean.getWorktime())
            time = handworkbaseCfgBean.getWorktime();
        int addIntegral = time * handworkbaseCfgBean.getNatureTime();
        return addIntegral;
    }

    /** 解锁手工 **/
    @SuppressWarnings("unchecked")
    @Override
    public void unlock(Object object, Player player, Set<Integer> validManual) {
        if (object == null)
            return;
        Map<String, Object> param = (Map<String, Object>) object;
        if (param.get(BuildingConstant.EVENT_CONDITION_ID) == null)
            return;
        int id = (int) param.get(BuildingConstant.EVENT_CONDITION_ID);
        switch (id) {
            case BuildingConstant.REFRESH_ALL:
                Map<Integer, ArrayList<Integer>> map = (Map<Integer, ArrayList<Integer>>) param
                        .get(BuildingConstant.EVENT_RESULT_DATA);
                for (Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
                    ArrayList<Integer> list = entry.getValue();
                    if (list == null)
                        continue;
                    if (list.contains(BuildingConstant.UNLOCK_HAND_WORK_TYPE)) {
                        checkUnlock(entry.getKey(), player, validManual);
                        break;
                    }
                }
                break;
            case BuildingConstant.ATTRIBUTE_CHANGE:
                Set<Integer> buildings = BuildingGeneral
                        .getBuildingByFunId(BuildingConstant.UNLOCK_HAND_WORK_TYPE, player);
                if (buildings != null) {
                    buildings.forEach((building) -> {
                        checkUnlock(building, player, validManual);
                    });
                }
                break;

            default:
                break;
        }

    }

}
