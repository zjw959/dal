package javascript.logic.favordating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;
import org.game.protobuf.s2c.S2CExtraDatingMsg.DatingInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.ResFavorDatingNotices;
import org.game.protobuf.s2c.S2CExtraDatingMsg.ResFavorDatingPanel;
import org.game.protobuf.s2c.S2CExtraDatingMsg.ResFavorReward;
import org.game.protobuf.s2c.S2CExtraDatingMsg.ResTiggerRoleNotice;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespChoices;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoices;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoosed;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespMainInfo;

import data.GameDataManager;
import data.bean.BaseFavorDating;
import data.bean.DiscreteDataCfgBean;
import data.bean.FavorCfgBean;
import data.bean.FavorMessageCfgBean;
import data.bean.FavorScriptCfgBean;
import data.bean.RoleCfgBean;
import event.Event;
import logic.character.bean.Player;
import logic.constant.BagType;
import logic.constant.DiscreteDataID;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EItemType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.favor.ConditionValidator;
import logic.favor.ConditionValidatorFactory;
import logic.favor.FavorDatingManager;
import logic.favor.IFavorDatingScript;
import logic.favor.structs.FavorDatingConst;
import logic.favor.structs.FavorDatingData;
import logic.functionSwitch.FunctionSwitchService;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.msgBuilder.FavorDatingBuilder;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

public class FavorDatingScript implements IFavorDatingScript {
    private static Logger LOGGER = Logger.getLogger(FavorDatingScript.class);
    @Override
    public int getScriptId() {
        return EScriptIdDefine.FAVOR_DATING_SCRIPT.Value();
    }

    @Override
    public void reqRoleStatue(Player player) {
        // 获取之前，先进行触发
        player._fireEvent(null, EEventType.MAINDATING_ACTIVE.value());
        Map<Integer, Integer> noticedMap = player.getFavorDatingManager().getNoticedFavorId();
        ResFavorDatingNotices.Builder builder = ResFavorDatingNotices.newBuilder();
        for (Integer favorDatingId : noticedMap.keySet()) {
            if (noticedMap.get(favorDatingId) == 0) {
                ResTiggerRoleNotice.Builder resp = ResTiggerRoleNotice.newBuilder();
                resp.setFavorDatingId(favorDatingId);
                resp.setStatue(1);
                builder.addFavorRoleStatue(resp);
            }
        }
        MessageUtils.send(player, builder);
    }

    @Override
    public void reqFavorDatingInfo(Player player, int roleId, int favorDatingId) {
        if (GameDataManager.getFavorCfgBean(favorDatingId) == null) {
            MessageUtils.throwCondtionError(GameErrorCode.MAIN_DATING_IS_NULL, "主线不可用 ");
            LOGGER.info("主线不可用  player:" + player.getPlayerName() + "  主线id：" + favorDatingId);
            return;
        }
        FavorDatingManager mng = player.getFavorDatingManager();
        FavorDatingData favorData = mng.getFavorDatingData(player, roleId, favorDatingId, true);
        if (!checkCanEnter(player, favorData)) {
            LOGGER.info("精力不足,无法进入 player:" + player.getPlayerName());
            return;
        }
        favorData.getTempBag().clear();
        favorData.getTempFlag().clear();
        favorData.getTempQuality().clear();
        RespMainInfo.Builder mainbuilder = RespMainInfo.newBuilder();
        DatingInfo datingInfo =
                FavorDatingBuilder.createFavorDateInfo(favorData, player, roleId, favorDatingId);
        mainbuilder.setInfo(datingInfo);
        MessageUtils.send(player, mainbuilder);
    }

    @Override
    public void reqStartEntrance(Player player, int roleId, int favorDatingId, int entranceId) {
        FavorDatingData favorDating = player.getFavorDatingManager().getFavorDatingData(player,
                roleId, favorDatingId, false);
        favorDating.getTempBag().clear();
        favorDating.getTempFlag().clear();
        favorDating.getTempQuality().clear();
        FavorScriptCfgBean favorScriptBean = GameDataManager.getFavorScriptCfgBean(entranceId);
        if (favorScriptBean == null) {
            LOGGER.error("favorScriptBean == null  id:" + entranceId);
            return;
        }
        if (favorDating.getTempEndItemMap().containsKey(favorScriptBean.getId())) {
            favorDating.getTempEndItemMap().remove(favorScriptBean.getId());
        }
        if (favorDating.getEntrances().containsKey(favorScriptBean.getId())) {
            favorDating.getEntrances().put(favorScriptBean.getId(), favorScriptBean.getStartId());
            // 开始剧本号，记录日志
            try {
                LogProcessor.getInstance()
                        .sendLog(LogBeanFactory.createDatingLog(player, favorScriptBean.getId(),
                                roleId, 0, EReason.FAVOR_DATING_NODE.value(), null));
            } catch (Exception ex) {
                LOGGER.error(ExceptionEx.e2s(ex));
            }
        }
        // 记录有选项的约会不ID
        if (!favorDating.getChoosed().contains(entranceId)) {
            favorDating.getChoosed().add(entranceId);
        }
        RespEntranceEventChoices.Builder resp = RespEntranceEventChoices.newBuilder();
        resp.setFirst(false);
        if (!favorDating.getCompleteDatingScript().contains(favorScriptBean.getId())) {
            resp.setFirst(true);
        }
        resp.setDatingType(2);
        MessageUtils.send(player, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reqGetEventChoices(Player player,
            org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices msg) {
        int favorDatingId = msg.getDatingValue();// 主线约会ID
        FavorCfgBean favorBean = GameDataManager.getFavorCfgBean(favorDatingId);
        if(favorBean == null){
            LOGGER.info("favorBean == null  id:" + favorDatingId);
            return;
        }
        int roleId = favorBean.getRole();
        int eventId = msg.getEventId();// 剧本或者信息id
        int choiceType = msg.getChoiceType();// 选择项类型,1:剧本/2:信息
        FavorDatingManager mng = player.getFavorDatingManager();
        FavorDatingData favorDatingData =
                mng.getFavorDatingData(player, roleId, favorDatingId, false);
        List<Integer> ids = new ArrayList<Integer>();

        // 剧本
        if (choiceType == FavorDatingConst.BUILDING_EVENT_TYPE_SCRIPT) {
            BaseFavorDating fdc =
                    GameDataManager.getBaseFavorDating(favorBean.getCallTableNameF()).get(eventId);
            // FavorDatingCfgBean fdc = GameDataManager.getFavorDatingCfgBean(eventId);
            BaseFavorDating temp;
            for (int key : fdc.getJump()) {
                temp = GameDataManager.getBaseFavorDating(favorBean.getCallTableNameF()).get(key);
                if (temp.getCondition() != null && temp.getCondition().size() > 0) {
                    if (choiceShow(player, favorDatingData, favorDatingId, temp.getCondition())) {
                        if (!ids.contains(key)) {
                            ids.add(key);
                        }
                    }
                } else {
                    ids.add(key);
                }

            }
            // 信息
        } else {
            FavorMessageCfgBean omc = GameDataManager.getFavorMessageCfgBean(eventId);
            Map<Integer, Integer> costItem;
            List<Integer> costMark;
            if (omc.getOptionCost1() != null) {
                costItem = (Map<Integer, Integer>) omc.getOptionCost1()
                        .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                costMark = (List<Integer>) omc.getOptionCost1()
                        .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                if ((costItem == null || mng.isItemsEnough(player, costItem, favorDatingData))
                        && (costMark == null || mng.isMarksEnough(costMark, favorDatingData)))
                    ids.add(1);
            } else
                ids.add(1);
            if (omc.getOptionCost2() != null) {
                costItem = (Map<Integer, Integer>) omc.getOptionCost2()
                        .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                costMark = (List<Integer>) omc.getOptionCost2()
                        .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                if ((costItem == null || mng.isItemsEnough(player, costItem, favorDatingData))
                        && (costMark == null || mng.isMarksEnough(costMark, favorDatingData)))
                    ids.add(2);
            } else
                ids.add(2);
        }
        RespChoices.Builder info = RespChoices.newBuilder();
        info.setDatingType(2);
        info.setDatingValue(favorDatingId);
        info.addAllEventId(ids);
        MessageUtils.send(player, info);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reqChooseEntrance(Player player, ReqChooseEntranceEvent msg) {
        int favorDatingId = msg.getDatingValue();
        int eventId = msg.getEventId();
        int entranceId = msg.getEntranceId();
        int choiceType = msg.getChoiceType();
        int choice = msg.getChoice();
        FavorCfgBean favorBean = GameDataManager.getFavorCfgBean(favorDatingId);
        if(favorBean == null){
            LOGGER.error("favorBean == null  favorId:" + favorDatingId);
            return;
        }
        int roleId = favorBean.getRole();
        FavorDatingData favorDatingData = player.getFavorDatingManager().getFavorDatingData(player,
                roleId, favorDatingId, false);
        if (favorDatingData == null) {
            MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_NOT_EXIST, "约会数据不存在");
            LOGGER.error("favorDatingData == null  player:" + player.getPlayerName() + "favorId:"
                    + favorDatingId);
            return;
        }
        if (!favorDatingData.getEntrances().containsKey(entranceId)) {
            MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_BUILDING_INVALID, "建筑不可用");
            LOGGER.info(
                    "传递的事件ID不可用 :" + entranceId + "可用的事件ID有 ：" + favorDatingData.getEntrances()+"player:"+player.getPlayerName());
            return;
        }
        FavorScriptCfgBean fsc = GameDataManager.getFavorScriptCfgBean(entranceId);
        boolean hasEnding = false;
        boolean needPush = false;
        Map<Integer, Integer> endingItems = new HashMap<Integer, Integer>();
        Map<Integer, Integer> normalItems = new HashMap<Integer, Integer>();
        Map<Integer, Integer> costItems = new HashMap<Integer, Integer>();
        // 下一剧本
        int nextId = 0;
        FavorDatingManager mng = player.getFavorDatingManager();
        if (choiceType == FavorDatingConst.BUILDING_EVENT_TYPE_SCRIPT) {
            // 验证剧本
            int lastDatingId = mng.getLastScriptEvent(favorDatingData, entranceId);
            // 如果是初始剧本
            lastDatingId = lastDatingId > 0 ? lastDatingId : fsc.getStartId();
            BaseFavorDating odc = GameDataManager.getBaseFavorDating(favorBean.getCallTableNameF())
                    .get(lastDatingId);
            // 下一剧本或结算
            odc = GameDataManager.getBaseFavorDating(favorBean.getCallTableNameF()).get(eventId);
            if (odc == null) {
                MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_SCRIPT_INVALID,
                        "剧本不可用");
                LOGGER.error("剧本对话不可用  剧本对话Id:" + eventId + "剧本表:" + favorBean.getCallTableNameF()
                        + "player:" + player.getPlayerName());
                return;
            }
            // 有道具
            if(odc.getNodeCost() != null){
                Map<Integer, Integer> nodeItem = (Map<Integer, Integer>) odc.getNodeCost()
                        .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                if(nodeItem != null) {
                    if (mng.isItemsEnough(player, nodeItem, favorDatingData)) {
                        mng.removeItems(nodeItem, favorDatingData);
                        mng.combineValueCount(costItems, nodeItem, true);
                    }
                }
                List<Integer> nodeMark = (List<Integer>) odc.getNodeCost()
                        .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                if(nodeMark != null) {
                    if (mng.isMarksEnough(nodeMark, favorDatingData)) {
                        for (Integer mark : nodeMark) {
                            mng.removeMark(favorDatingData, mark);
                        }
                    }
                }
            }
            if (odc.getNodeReward() != null && odc.getNodeReward().size() > 0) {
                Map<Integer, Integer> nodeItem = (Map<Integer, Integer>) odc.getNodeReward()
                        .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                if(nodeItem != null) {
                    mng.addItems(favorDatingData, nodeItem);
                    mng.combineValueCount(normalItems, nodeItem, true);
                }
                List<Integer> nodeMark = (List<Integer>) odc.getNodeReward()
                        .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                if(nodeMark != null) {
                    for (Integer mark : nodeMark) {
                        mng.addMark(favorDatingData, mark);
                    }
                }
                Map<Integer, Integer> qualityMap = (Map<Integer, Integer>) odc.getNodeReward()
                        .get(FavorDatingConst.SCRIPT_CONDITION_QUALITY);
                if (qualityMap != null) {
                    mng.addQuality(favorDatingData, qualityMap);
                }
            }
            // 新增一个只在结局的时候才结算的奖励字段
            if (odc.getExtraBonus() != null && odc.getExtraBonus().size() > 0) {
                Map<Integer, Integer> endItem = (Map<Integer, Integer>) odc.getExtraBonus()
                        .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                addEndTempItem(favorDatingData, fsc, endItem);
            }
            if (odc.getJump() !=null && odc.getJump().length > 0) {
                if (choice - 1 < 0 || choice - 1 > odc.getJump().length) {
                    MessageUtils.throwCondtionError(
                            GameErrorCode.OUTSIDE_DATING_NEXT_SCRIPT_INVALID, "后置剧本不可用");
                    return;
                }
                nextId = odc.getJump()[choice - 1];
                mng.recordCurrentScriptEvent(favorDatingData, entranceId, nextId);
            } else {
                // 有结算
                needPush = true;
                // 标记处理
                if(odc.getAddSign() != null) {
                    for (int sign : odc.getAddSign()) {
                        mng.addMark(favorDatingData, sign);
                    }
                }
                if(odc.getDelSign() != null) {
                    for (int sign : odc.getDelSign()) {
                        mng.removeMark(favorDatingData, sign);
                    }
                }
                // 建筑事件
                if (!odc.getOutsideType()) {
                    // 计算时间
                    if (fsc.getAddTime() != null && fsc.getAddTime().length() > 0) {
                        String addTime = fsc.getAddTime();
                        favorDatingData.setCurrentTime(mng.formatTime(addTime));
                    } else if (fsc.getCostTime() > 0) {
                        int beginTime = favorDatingData.getCurrentTime();
                        favorDatingData.setCurrentTime(fsc.getCostTime() * 60 + beginTime);
                    }
                    Map<Integer, Integer> items = (Map<Integer, Integer>) odc.getOutsideReward()
                            .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                    if(items != null) {
                        mng.addItems(favorDatingData, items);
                        mng.combineValueCount(normalItems, items, true);
                    }
                    List<Integer> marks = (List<Integer>) odc.getOutsideReward()
                            .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                    if(marks != null) {
                        for (Integer mark : marks) {
                            mng.addMark(favorDatingData, mark);
                        }
                    }
                    Map<Integer, Integer> qualityMap =
                            (Map<Integer, Integer>) odc.getOutsideReward()
                            .get(FavorDatingConst.SCRIPT_CONDITION_QUALITY);
                    if (qualityMap != null) {
                        mng.addQuality(favorDatingData, qualityMap);
                    }
                } else {
                    hasEnding = true;
                    Map<Integer, Integer> items;
                    Map<Integer, Integer> bonusItems;
                    List<Integer> marks;
                    // 首次
                    if (!mng.getCompletedDating().contains(favorDatingId)) {
                        items = (Map<Integer, Integer>) odc.getOutsideEnd()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        marks = (List<Integer>) odc.getOutsideEnd()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                        mng.getCompletedDating().add(favorDatingId);
                        // 触发主线章节激活通知
                        player._fireEvent(null, EEventType.MAINDATING_ACTIVE.value());
                    }else{
                        items = (Map<Integer, Integer>) odc.getOutsideRepeat()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        marks = (List<Integer>) odc.getOutsideRepeat()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                    }
                    if(!mng.endingExist(favorDatingData.getEndings(),
                            odc.getOutsideUnlock())){
                        favorDatingData.setEndings(mng.addEnding(favorDatingData.getEndings(),
                                odc.getOutsideUnlock()));
                    }
                    mng.combineValueCount(endingItems, resultEndTempItem(favorDatingData, player),
                            true);
                    if(items != null) {
                        mng.addItems(favorDatingData, items);
                        mng.combineValueCount(endingItems, items, true);
                    }
                    if(marks != null) {
                        for (Integer mark : marks) {
                            mng.addMark(favorDatingData, mark);
                        }
                    }
                    Map<Integer, Integer> qualityMap = (Map<Integer, Integer>) odc
                            .getOutsideRepeat().get(FavorDatingConst.SCRIPT_CONDITION_QUALITY);
                    if (qualityMap != null) {
                        mng.addQuality(favorDatingData, qualityMap);
                    }
                    Map<Integer, Boolean> repeatTimeMap = favorDatingData.getRepeatTimeMap();
                    favorDatingData.setRepeat(favorDatingData.getRepeat() + 1);
                    repeatTimeMap.clear();
                    repeatTimeMap.put(favorDatingData.getRepeat(), false);
                }
                if (fsc.getDelete())
                    mng.removeEntrance(player, favorDatingData, fsc.getId());
                boolean needClearQuality = false;
                if (mng.endingExist(favorDatingData.getEndings(), odc.getOutsideUnlock())) {
                    if (favorDatingData.isRePlay()) {
                        favorDatingData.setRePlay(false);
                        needClearQuality = true;
                    }
                }
                mng.handleTempData(player, favorDatingData, needClearQuality);
                mng.recordCurrentScriptEvent(favorDatingData, entranceId, -1);
                // 阶段转换
                int nextPara = fsc.getStepJump();
                
                if (nextPara > favorDatingData.getCurrentParagraphId()) {
                    mng.go2NextParagragh(player, favorDatingData, nextPara, favorDatingId);
                }
                if(hasEnding) {
                    mng.clearDatingData(favorDatingData);

                }
                // 记录剧本
                int datingScriptId = odc.getScriptId();
                if (!favorDatingData.getCompleteDatingScript().contains(datingScriptId)) {
                    favorDatingData.getCompleteDatingScript().add(datingScriptId);
                }
                // 剧本结束，记录日志
                favorDatintgLog(player, odc, roleId, normalItems);
            }
        } else {
            FavorMessageCfgBean omc = GameDataManager.getFavorMessageCfgBean(fsc.getMessage());
            // 执行信息
            if (!mng.handleEventMessage(costItems, normalItems, player, favorDatingData, omc, fsc,
                    choice,roleId,favorDatingId)) {
                MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_MESSAGE_INVALID,
                        " 信息不可用");
            }
            boolean needClearQuality = false;
            if (favorDatingData.getMessages().contains(omc.getId())) {
                needClearQuality = true;
            } else {
                favorDatingData.getMessages().add(omc.getId());
            }
            mng.handleTempData(player, favorDatingData, needClearQuality);
            mng.recordCurrentScriptEvent(favorDatingData, entranceId, -1);
        }

        // 建筑入口的刷新
        if (needPush && !hasEnding) {
            refreshEntrances(player, favorDatingData, favorDatingId);
            mng.sendParagraghChange(player, favorDatingData, roleId, favorDatingId);
        }
        // 检查入口是否为空
        if (!hasEnding && favorDatingData.getEntrances().isEmpty()) {
            mng.clearDatingData(favorDatingData);
            MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_DATE_INVALID,
                    "没有可用的入口,检查配置  favordating:" + eventId);
            LOGGER.error("没有可用的事件入口,传递的剧本对话id" + eventId);
        }
        // 刷新
        RespEntranceEventChoosed.Builder builder =
                FavorDatingBuilder.createRespEntranceEventChoosed(favorDatingData, normalItems,
                        hasEnding, endingItems, costItems, favorDatingId, player, roleId);
        MessageUtils.send(player, builder);
    }

    @Override
    public void reqFavorDatingPanel(Player player,
            int roleId) {

        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_MAIN_LINE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:mainDating");
        }
        Role role = player.getRoleManager().getRole(roleId);
        if (role == null) {
            return;
        }
        ResFavorDatingPanel.Builder resp = ResFavorDatingPanel.newBuilder();
        resp.setRoleId(roleId);
        getFavorTypeList(role, resp, player);
        getFavorRewardList(player, player.getFavorDatingManager(), role, resp);
        resp.setRoleId(role.getCid());
        MessageUtils.send(player, resp);
    }

    // 获取激活的主线章节
    private void getFavorTypeList(Role role,
            ResFavorDatingPanel.Builder resp, Player player) {
        // 精灵好感度
        int roleFavor = role.getFavor();
        // 计算好感度等级
        RoleCfgBean roleBean = GameDataManager.getRoleCfgBean(role.getCid());
        if(roleBean == null){
            return;
        }
        int level = 1;
        int[] favorLevels = roleBean.getFavorLevels();
        for(int i=0;i<favorLevels.length;i++){
            if (roleFavor >= favorLevels[i]) {
                level = i + 1;
            }
        }
        List<FavorCfgBean> favorsList = GameDataManager.getFavorCfgBeans().stream()
                .filter(e -> e.getType() == FavorDatingConst.FAVOR_TYPE
                        && e.getRole() == role.getCid())
                .collect(Collectors.toList());
        List<Integer> completeDating = player.getFavorDatingManager().getCompletedDating();
        // 条件检查
        for (FavorCfgBean fcb : favorsList) {
            FavorDatingData osd = null;
            Map<Integer, FavorDatingData> favaDataMap =
                    player.getFavorDatingManager().getDataMap().get(role.getCid());
            if (favaDataMap != null) {
                osd = favaDataMap.get(fcb.getId());
            }
            if (fcb.getCondition() != null && fcb.getCondition().size() > 0) {
                if (fcb.getCondition().get(FavorDatingConst.CONDI_TYPE) != null) {
                    int value = (int) fcb.getCondition().get(FavorDatingConst.FAVOR_TYPE);
                    // 好感度不满足条件
                    if (level < value) {
                        resp.addInfo(FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(), 0,
                                completeDating));
                        continue;
                    }

                } else if (fcb.getCondition().get(FavorDatingConst.CONDI_DUNGEON) != null) {
                    int value = (int) fcb.getCondition().get(FavorDatingConst.CONDI_DUNGEON);
                    // 副本不满足条件
                    if (!player.getDungeonManager().checkDungeonPass(value)) {
                        resp.addInfo(FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(), 0,
                                completeDating));
                        continue;
                    } else if (fcb.getPrepose() > 0) {
                        if (!completeDating.contains(fcb.getPrepose())) {
                            resp.addInfo(FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(),
                                    0,
                                    completeDating));
                            continue;
                        }
                    }
                }
                resp.addInfo(
                        FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(), 1,
                                completeDating));
            } else {
                resp.addInfo(
                        FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(), 1,
                                completeDating));
            }
        }
    }

    // 获取领奖状态
    private void getFavorRewardList(Player player, FavorDatingManager mng, Role role,
            ResFavorDatingPanel.Builder resp) {
        // 精灵好感度
        int roleFavor = role.getFavor();
        List<FavorCfgBean> rewardsList = GameDataManager.getFavorCfgBeans().stream()
                .filter(e -> e.getType() == FavorDatingConst.FAVOR_TYPE_REWARD
                        && e.getRole() == role.getCid())
                .collect(Collectors.toList());
        // 奖励分支好多度条件检查
        for (FavorCfgBean fcb : rewardsList) {
            FavorDatingData osd = null;
            Map<Integer, FavorDatingData> favaDataMap =
                    player.getFavorDatingManager().getDataMap().get(role.getCid());
            if (favaDataMap != null) {
                osd = favaDataMap.get(fcb.getId());
            }
            // 不能领取
            if (roleFavor < fcb.getBranchCondition()) {
                resp.addInfo(FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(), 0, null));
            } else {
                // 已经领取
                if (mng.getRewardFavorId().contains(fcb.getId())) {
                    resp.addInfo(
                            FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(), 2, null));
                } else {
                    // 可领
                    resp.addInfo(
                            FavorDatingBuilder.creteFavorStatueInfo(osd, fcb.getId(), 1, null));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reqFavorReward(Player player,
            org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorReward msg) {
        FavorDatingManager mng = player.getFavorDatingManager();
        int favorId = msg.getFavorDatingId();
        if (mng.getRewardFavorId().contains(favorId)) {
            return;
        }
        FavorCfgBean favorBean = GameDataManager.getFavorCfgBean(favorId);
        Map<Integer, Integer> rewards = (Map<Integer, Integer>) favorBean.getBranchReward();
        List<Item> addItem = new ArrayList<Item>();
        for (Entry<Integer, Integer> entry : rewards.entrySet()) {
            addItem = player.getBagManager().addItem(entry.getKey(), entry.getValue(), true,
                    EReason.FAVOR_DATING_ADD);
        }
        ResFavorReward.Builder builder = ResFavorReward.newBuilder();
        builder.setFavorDatingId(favorId);
        if (addItem.size() > 0) {
            mng.getRewardFavorId().add(favorId);
            builder.setStatue(1);
        } else {
            builder.setStatue(0);
        }
        MessageUtils.send(player, builder);
    }

    @Override
    public void eventPerformed(Player player, Event event) {
        List<Integer> completeDating = player.getFavorDatingManager().getCompletedDating();
        Map<Integer, Integer> noticedFavorIdMap =
                player.getFavorDatingManager().getNoticedFavorId();

        Map<Integer, Role> roles = player.getRoleManager().getRoles();
        for (int cid : roles.keySet()) {
            int favorDatingId = getNoticeFavorId(player, cid, getFavorLevel(player, cid),
                    noticedFavorIdMap, completeDating);
            if (favorDatingId == 0) {
                continue;
            }
            noticedFavorIdMap.put(favorDatingId, 0);
            ResTiggerRoleNotice.Builder builder = ResTiggerRoleNotice.newBuilder();
            builder.setFavorDatingId(favorDatingId);
            builder.setStatue(1);
            MessageUtils.send(player, builder);
        }
    }

    // 计算好感度等级
    private int getFavorLevel(Player player, int roleId) {
        int nowFavor = player.getRoleManager().getRole(roleId) == null ? 0
                : player.getRoleManager().getRole(roleId).getFavor();
        RoleCfgBean roleBean = GameDataManager.getRoleCfgBean(roleId);
        if (roleBean == null) {
            return 0;
        }
        int level = 1;
        int[] favorLevels = roleBean.getFavorLevels();
        for (int i = 0; i < favorLevels.length; i++) {
            if (nowFavor >= favorLevels[i]) {
                level = i + 1;
            }
        }
        return level;
    }

    // 精灵检查是否通知
    @SuppressWarnings("unchecked")
    private int getNoticeFavorId(Player player, int roleId, int favorlevel,
            Map<Integer, Integer> noticedFavorIdMap, List<Integer> completeDating) {
        // 精灵获取对应的主线章节
        List<FavorCfgBean> favorsList = GameDataManager.getFavorCfgBeans().stream()
                .filter(e -> e.getType() == FavorDatingConst.FAVOR_TYPE && e.getRole() == roleId)
                .collect(Collectors.toList());
        // 检查条件
        for (FavorCfgBean fcb : favorsList) {
            // 触发过,不在触发
            if (noticedFavorIdMap.containsKey(fcb.getId())) {
                continue;
            }
            Map<Integer, Integer> condition = fcb.getCondition();
            if (condition != null && condition.size() > 0) {
                if (fcb.getPrepose() > 0) {
                    // 前置章节条件
                    if (!completeDating.contains(fcb.getPrepose())) {
                        continue;
                    }
                } else if (condition.get(FavorDatingConst.CONDI_TYPE) != null) {
                    int value = condition.get(FavorDatingConst.FAVOR_TYPE);
                    // 好感度条件
                    if (favorlevel < value) {
                        continue;
                    }
                } else if (condition.get(FavorDatingConst.CONDI_DUNGEON) != null) {
                    int value = condition.get(FavorDatingConst.CONDI_DUNGEON);
                    // 副本条件
                    if (!player.getDungeonManager().checkDungeonPass(value)) {
                        continue;
                    }
                    // 完成过,说明不是首次
                } else if (completeDating.contains(fcb.getId())) {
                    continue;
                }
                return fcb.getId();
            }
            return fcb.getId();
        }
        return 0;
    }

    // 处理精灵章节状态提示
    @Override
    public void dealNoticeStatue(Player player, int roelId) {

        Map<Integer, Integer> noticedMap = player.getFavorDatingManager().getNoticedFavorId();
        List<FavorCfgBean> favorBeans = GameDataManager.getFavorCfgBeans().stream()
                .filter(e -> e.getRole() == roelId).collect(Collectors.toList());
        FavorCfgBean favorBean =
                favorBeans.stream().filter(
                        e -> noticedMap.containsKey(e.getId()) && noticedMap.get(e.getId()) == 0)
                        .findAny()
                        .orElse(null);
        if (favorBean == null) {
            return;
        }
        noticedMap.put(favorBean.getId(), 1);
        ResTiggerRoleNotice.Builder builder = ResTiggerRoleNotice.newBuilder();
        builder.setFavorDatingId(favorBean.getId());
        builder.setStatue(0);
        MessageUtils.send(player, builder);
    }

    public boolean choiceShow(Player player, FavorDatingData fdd, int favorDatingId,
            Map<Object, Object> condition) {

        Map<String, ConditionValidator> validators = ConditionValidatorFactory.entranceValidators;
        ConditionValidator validator;
        if (condition != null) {
            for (Entry<Object, Object> entry : condition.entrySet()) {
                validator = validators.get(entry.getKey().toString());
                // 不满足
                if (!validator.validate(player, fdd, entry.getValue(), favorDatingId))
                    return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean checkCanEnter(Player player, FavorDatingData fdd) {
        Map<Integer, Boolean> repeatTimeMap = fdd.getRepeatTimeMap();
        if (repeatTimeMap.isEmpty()) {
            repeatTimeMap.put(1, false);
        }
        boolean hasCost = repeatTimeMap.get(fdd.getRepeat());
        if (hasCost) {
            return true;
        }
        int cityEnergy = player.getCityInfoManager().getCityEnergy();
        DiscreteDataCfgBean cfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FAVOR_DATING_COST);
        if (cfg == null) {
            repeatTimeMap.put(fdd.getRepeat(), true);
            return true;
        }
        Integer need = 100;
        List<Integer> valuelist = new ArrayList<>(cfg.getData().values());
        valuelist.stream().sorted();
        need = (Integer) cfg.getData().get(fdd.getRepeat());
        if (need == null) {
            // 取最大
            need = (Integer) valuelist.get(valuelist.size() - 1);
        }
        if (need > cityEnergy) {
            // 精力不足
            MessageUtils.throwCondtionError(GameErrorCode.NOT_ENERGY, "精力不足");
            return false;
        }
        // 精力足够扣除精力
        player.getCityInfoManager().changeCityEnergy(-need, true);
        repeatTimeMap.put(fdd.getRepeat(), true);
        player.getCityInfoManager().sendCityEnergyUpdate();
        return true;
    }

    public void favorDatintgLog(Player player, BaseFavorDating odc, int roleId,
            Map<Integer, Integer> reward) {
        int cgId = 0;
        for (int itemId : reward.keySet()) {
            if (EItemType.CG.getValue() == ItemUtils.getItemType(itemId)) {
                cgId = itemId;
                break;
            }
        }
        // 结束剧本，记录日志
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createDatingLog(player,
                    odc.getScriptId(), roleId,  cgId, EReason.FAVOR_DATING_SETTLE.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    /**
     * 添加结局结算奖励
     * 
     */
    private void addEndTempItem(FavorDatingData favorDatingData, FavorScriptCfgBean fsc,
            Map<Integer, Integer> endItem) {
        if (endItem != null) {
            favorDatingData.getTempEndItemMap().putIfAbsent(fsc.getId(),
                    new HashMap<Integer, Integer>());
            Map<Integer, Integer> base = favorDatingData.getTempEndItemMap().get(fsc.getId());
            for (Entry<Integer, Integer> entry : endItem.entrySet()) {
                int baseCount = base.getOrDefault(entry.getKey(), 0);
                int result = baseCount + entry.getValue();
                base.put(entry.getKey(), result);
            }
        }
    }

    private Map<Integer, Integer> resultEndTempItem(FavorDatingData osd, Player player) {
        Map<Integer, Integer> bag = osd.getBag();
        // 正常道具
        Map<Integer, Integer> normalAdd = new HashMap<Integer, Integer>();
        Collection<Map<Integer, Integer>> reultItems = osd.getTempEndItemMap().values();
        for (Map<Integer, Integer> reultItem : reultItems) {
            for (Map.Entry<Integer, Integer> item : reultItem.entrySet()) {
                // 外传道具
                if (GameDataManager.getItemCfgBean(item.getKey())
                        .getSuperType() == BagType.FAVOR_DATING) {
                    int count = bag.getOrDefault(item.getKey(), 0);
                    count = count + item.getValue() > 0 ? count + item.getValue() : 0;
                    if (count > 0)
                        bag.put(item.getKey(), count);
                    else
                        bag.remove(item.getKey());
                } else {
                    if (item.getValue() > 0) {
                        normalAdd.put(item.getKey(), item.getValue());
                    }
                }
            }
        }
        if (!normalAdd.isEmpty()) {
            player.getBagManager().addItems(normalAdd, true, EReason.FAVOR_DATING_ADD);
        }
        return normalAdd;
    }
    private void refreshEntrances(Player player,FavorDatingData osd,int favorDatingId){
        FavorDatingManager mng = player.getFavorDatingManager();
        int currentParagraph = osd.getCurrentParagraphId();
        // 获取阶段入口
        List<FavorScriptCfgBean> wholeBuildings = mng.getWholeBuildings(currentParagraph, osd);
        List<FavorScriptCfgBean> showBuildings = new ArrayList<FavorScriptCfgBean>();
        for (FavorScriptCfgBean favorScriptCfg : wholeBuildings) {
            // 已经被移除了,不再检测
            if (osd.getRmEntrance() != null
                    && osd.getRmEntrance().contains(favorScriptCfg.getId()))
                continue;
            // 进行检测,可用的添加,不可用的移除
            boolean visual = mng.validateDisplayConditions(player, osd, favorScriptCfg, favorDatingId);
            if (visual) {
                showBuildings.add(favorScriptCfg);
            } else {
                mng.removeEntrance(player, osd, favorScriptCfg.getId());
            }
        }
        List<FavorScriptCfgBean> showEvents = mng.getShowEvents(showBuildings, osd);
        osd.getEntrances().clear();
        // 保存数据
        for (FavorScriptCfgBean favorScriptCfg : showEvents) {
            osd.getEntrances().putIfAbsent(favorScriptCfg.getId(), -1);
        }
        
    }
}

