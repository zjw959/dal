package javascript.logic.city;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RespGetFoodBaseAward.Builder;

import data.GameDataManager;
import data.bean.CookQteCfgBean;
import data.bean.FoodbaseCfgBean;
import logic.character.bean.Player;
import logic.city.BuildingGeneral;
import logic.city.build.BuildingConstant;
import logic.city.build.bean.BuildEvent;
import logic.city.script.ICuisineManagerScript;
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
 * 料理脚本
 * 
 * @author lihongji
 *
 */

public class CuisineManagerScript implements ICuisineManagerScript {

    /** 返回脚本的Id **/
    @Override
    public int getScriptId() {
        return EScriptIdDefine.CITY_CUISINE_SCRIPT.Value();
    }

    /** 检测当前是否能料理 **/
    @SuppressWarnings("unchecked")
    @Override
    public void checkCuisine(FoodbaseCfgBean foodbaseCfgBean, Player player) {
        if (!BuildingGeneral.checkAbility(foodbaseCfgBean.getAbility(), player)) {
            MessageUtils.throwCondtionError(GameErrorCode.CUISINE_ABILITY_IS_NOT_ENOUGH, "材料能力不足");
        }
        if (foodbaseCfgBean.getMaterials() != null
                && !player.getBagManager().enoughByTemplateId(foodbaseCfgBean.getMaterials())) {
            MessageUtils.throwCondtionError(GameErrorCode.CUISINE_MATERIALS_IS_NOT_ENOUGH,
                    "料理材料不足");
        }
        if (player.getCuisineManager().getEvent().getEtime() != 0) {
            MessageUtils.throwCondtionError(GameErrorCode.CUISINE_MATERIALS_IS_NOT_COMPLETE,
                    "料理未完成,请领取奖励");
        }
        if (foodbaseCfgBean.getMaterials() != null)
            player.getBagManager().removeItemsByTemplateIdNoCheck(foodbaseCfgBean.getMaterials(),
                    true, EReason.CUISINE_COST);
    }

    /** 创建料理事件 **/
    @Override
    public void createCuisineEvent(int id, Player player, Logger LOGGER) {
        FoodbaseCfgBean foodbaseCfgBean = GameDataManager.getFoodbaseCfgBean(id);
        if (foodbaseCfgBean == null)
            MessageUtils.throwCondtionError(GameErrorCode.CUISINE_IS_ERROR, "料理id不存在:" + id);
        checkCuisine(foodbaseCfgBean, player);
        long etime = (foodbaseCfgBean.getCooktime() * TimeUtil.SECOND) + System.currentTimeMillis();
        player.getCuisineManager().getEvent().initCuisineEvent(id, 0, etime);

        // 料理开始时间
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createCityGameLog(player,
                    EReason.CUISINE_STARTTIME.value(), id));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }


    }

    /** 获取当前奖励 **/
    @SuppressWarnings("unchecked")
    @Override
    public Builder getAward(int cuisineId, Player player, Logger LOGGER) {
        if (player.getCuisineManager().getEvent().getCuisineId() != cuisineId
                || player.getCuisineManager().getEvent().getEtime() == 0) {
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR,
                    "参数错误,当前料理id:" + player.getCuisineManager().getEvent().getCuisineId()
                            + ",客户端发送的id:" + cuisineId + ",当前结束时间"
                            + player.getCuisineManager().getEvent().getEtime());
        }
        FoodbaseCfgBean foodbaseCfgBean = GameDataManager.getFoodbaseCfgBean(cuisineId);
        long timeNow = System.currentTimeMillis();
        if (timeNow < player.getCuisineManager().getEvent().getEtime()) {
            int addIntegral = (int) ((timeNow - (player.getCuisineManager().getEvent().getEtime()
                    - foodbaseCfgBean.getCooktime() * TimeUtil.SECOND) / TimeUtil.SECOND)
                    * foodbaseCfgBean.getNatureTime());
            player.getCuisineManager().getEvent().addIntegral(addIntegral);
        } else {
            int addIntegral =
                    (int) (foodbaseCfgBean.getCooktime() * foodbaseCfgBean.getNatureTime());
            player.getCuisineManager().getEvent().addIntegral(addIntegral);
        }
        List<Map<Integer, Map<Integer, Integer>>> list = foodbaseCfgBean.getIntegral();
        Map<Integer, Integer> award = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            Set<Integer> set = list.get(i).keySet();
            Object[] object = set.toArray();
            if (player.getCuisineManager().getEvent().getIntegral() >= (Integer) (object[0])) {
                award = list.get(i).get((Integer) (object[0]));
                break;
            }
        }
        if (award == null || award.size() <= 0)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR, "积分不足领取奖励");
        // 重置当前记录
        player.getCuisineManager().getEvent().clear();
        player.getBagManager().addItems(award, true, EReason.GIVE_BY_COOK);


        // 料理结束时间
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createCityGameLog(player,
                    EReason.CUISINE_ENDTIME.value(), cuisineId));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }

        return NewBuildingMsgBuilder.packingCuiSineReward(cuisineId, award);
    }

    /** 获取QTE操作 **/
    @Override
    public List<CookQteCfgBean> getCookQteCfgBeans() {
        return GameDataManager.getCookQteCfgBeans();
    }

    /** 检测客户端传递的QTE操作积分 **/
    @Override
    public org.game.protobuf.s2c.S2CNewBuildingMsg.RespUploadQteIntegral.Builder checkQteIntegral(
            int cuisineId, int qteId, int integral, Player player) {
        FoodbaseCfgBean foodbaseCfgBean = GameDataManager.getFoodbaseCfgBean(cuisineId);
        if (foodbaseCfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CUISINE_IS_ERROR, "料理id不存在:" + cuisineId);
        }
        if (player.getCuisineManager().getEvent().getCuisineId() != cuisineId)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR,
                    "参数不一致,服务器ID:" + player.getCuisineManager().getEvent().getCuisineId()
                            + ",客户端传的ID:" + cuisineId);
        long timeOut = 10 * TimeUtil.SECOND;
        if (System.currentTimeMillis() > player.getCuisineManager().getEvent().getEtime() + timeOut)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_PARAMETER_IS_ERROR,
                    "上传料理积分超时：" + (player.getCuisineManager().getEvent().getEtime() + timeOut)
                            + ",系统时间：" + System.currentTimeMillis());

        CookQteCfgBean cookQteCfgBean = GameDataManager.getCookQteCfgBean(qteId);
        if (cookQteCfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CUISINE_IS_ERROR, "料理qteId不存在:" + qteId);
        }

        if (cookQteCfgBean.getMax() != 0) {
            int maxIntegral = cookQteCfgBean.getTime() * cookQteCfgBean.getMax();
            if (maxIntegral < integral) {
                integral = 0;
            }
        }
        player.getCuisineManager().getEvent().addQteTime();
        player.getCuisineManager().getEvent().addIntegral(integral);
        return NewBuildingMsgBuilder.packingQTEInfo(player.getCuisineManager().getEvent(), qteId);
    }

    /** 检测 **/
    @SuppressWarnings("unchecked")
    @Override
    public void checkUnlock(int buildingId, Player player, Set<Integer> validCuisine) {
        Set<Integer> addCuisine = new HashSet<Integer>();
        List<BuildEvent> addEvent = new ArrayList<BuildEvent>();
        List<FoodbaseCfgBean> list = GameDataManager.getFoodbaseCfgBeans();
        list.forEach(foodBean -> {
            if (!validCuisine.contains(foodBean.getId())
                    && BuildingGeneral.checkAbility(foodBean.getAbility(), player)) {
                validCuisine.add(foodBean.getId());
                addCuisine.add(foodBean.getId());
                BuildEvent event = BuildingGeneral.createBuildingEvent(buildingId,
                        BuildingConstant.EVENT_TYPE_CUISINE_ADD, 0, 0, player);
                addEvent.add(event);
            }
        });
        MessageUtils.send(player, NewBuildingMsgBuilder.addEvent(addEvent)
                .setDayType(player.getNewBuildingManager().getDayType()));
    }

    /** 解锁料理 **/
    @SuppressWarnings("unchecked")
    @Override
    public void unlock(Object object, Player player, Set<Integer> validCuisine) {
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
                    if (list.contains(BuildingConstant.UNLOCK_CUISINE_TYPE)) {
                        checkUnlock(entry.getKey(), player, validCuisine);
                        break;
                    }
                }
                break;
            case BuildingConstant.ATTRIBUTE_CHANGE:
                Set<Integer> buildings = BuildingGeneral
                        .getBuildingByFunId(BuildingConstant.UNLOCK_CUISINE_TYPE, player);
                if (buildings != null) {
                    buildings.forEach((building) -> {
                        checkUnlock(building, player, validCuisine);
                    });
                }
                break;

            default:
                break;
        }
    }

    @Override
    public int getNatureTime(FoodbaseCfgBean foodbaseCfgBean, Player player) {
        long timeNow = System.currentTimeMillis();
        int time = (int) ((timeNow - (player.getCuisineManager().getEvent().getEtime()
                - foodbaseCfgBean.getCooktime() * TimeUtil.SECOND)) / TimeUtil.SECOND);
        if (time > foodbaseCfgBean.getCooktime())
            time = foodbaseCfgBean.getCooktime();
        int addIntegral = time * foodbaseCfgBean.getNatureTime();
        return addIntegral;
    }

}
