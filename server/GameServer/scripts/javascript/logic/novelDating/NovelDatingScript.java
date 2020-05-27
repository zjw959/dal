package javascript.logic.novelDating;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;
import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CExtraDatingMsg.DatingInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespChoices;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoices;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoosed;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespMainInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.SettleInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.DungeonLevelCfgBean;
import data.bean.NovelCfgBean;
import data.bean.NovelDatingCfgBean;
import data.bean.NovelScriptCfgBean;
import data.bean.NovelStepCfgBean;
import logic.character.bean.Player;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.dungeon.bean.DungeonBean;
import logic.favor.FavorDatingManager;
import logic.favor.structs.FavorDatingConst;
import logic.item.ItemPackageHelper;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.msgBuilder.NovelDatingMsgBuilder;
import logic.novelDating.ConditionValidator;
import logic.novelDating.ConditionValidatorFactory;
import logic.novelDating.INovelDatingScript;
import logic.novelDating.NovelDatingManager;
import logic.novelDating.structs.NovelDatingConst;
import logic.novelDating.structs.NovelDatingData;
import logic.role.bean.Role;
import logic.support.MessageUtils;

public class NovelDatingScript implements INovelDatingScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.NOVEL_DATING_SCRIPT.Value();
    }

    @Override
    public void reqNovelDatingInfo(Player player, int novelDatingId) {
        NovelDatingData data = player.getNovelDatingManager().getDataMap().get(novelDatingId);
        if (need2Init(data)) {
            data = createData(player, data, novelDatingId);
            player.getNovelDatingManager().getDataMap().put(novelDatingId, data);
        }
        RespMainInfo.Builder mainbuilder = RespMainInfo.newBuilder();

        DatingInfo datingInfo =
                NovelDatingMsgBuilder.createNovelDatingInfo(player, novelDatingId, data);
        mainbuilder.setInfo(datingInfo);
        MessageUtils.send(player, mainbuilder);
    }

    @Override
    public void reqStartNovelEntrance(Player player, int novelDatingId, int entranceId) {
        NovelDatingData data = player.getNovelDatingManager().getDataMap().get(novelDatingId);
        if (data == null) {
            return;
        }
        data.getTempBag().clear();
        data.getTempFlag().clear();
        NovelScriptCfgBean novelScriptBean = GameDataManager.getNovelScriptCfgBean(entranceId);
        if (novelScriptBean == null) {
            return;
        }
        if (data.getEntrances().containsKey(novelScriptBean.getId())) {
            data.getEntrances().put(novelScriptBean.getId(), novelScriptBean.getStartId());
        }
        RespEntranceEventChoices.Builder resp = RespEntranceEventChoices.newBuilder();
        resp.setFirst(false);
        if (!data.getCompleteScript().contains(novelScriptBean.getId())) {
            resp.setFirst(true);
        }
        resp.setDatingType(3);

        MessageUtils.send(player, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reqGetEventChoices(Player player,
            org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices msg) {
        int novelDatingId = msg.getDatingValue();// 约会ID
        NovelCfgBean novelBean = GameDataManager.getNovelCfgBean(novelDatingId);
        if (novelBean == null) {
            return;
        }
        int eventId = msg.getEventId();
        NovelDatingManager mng = player.getNovelDatingManager();
        NovelDatingData novelDatingData = mng.getDataMap().get(novelDatingId);
        if (novelDatingData == null) {
            return;
        }
        List<Integer> ids = new ArrayList<Integer>();
        NovelDatingCfgBean fdc = GameDataManager.getNovelDatingCfgBean(eventId);
        NovelDatingCfgBean temp;
        Map<Integer, Integer> costItem;
        List<Integer> costMark;
        for (int key : fdc.getJump()) {
            temp = GameDataManager.getNovelDatingCfgBean(key);
            if (temp.getNodeCost() != null) {
                costItem = (Map<Integer, Integer>) temp.getNodeCost()
                        .get(NovelDatingConst.SCRIPT_CONDITION_ITEM);
                costMark = (List<Integer>) temp.getNodeCost()
                        .get(NovelDatingConst.SCRIPT_CONDITION_MARK);
                if ((costItem == null || mng.isItemsEnough(player, costItem, novelDatingData))
                        && (costMark == null || mng.isMarksEnough(costMark, novelDatingData)))
                    ids.add(key);
            } else
                ids.add(key);
        }
        RespChoices.Builder info = RespChoices.newBuilder();
        info.setDatingType(3);
        info.setDatingValue(novelDatingId);
        info.addAllEventId(ids);
        MessageUtils.send(player, info);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reqChooseEntrance(Player player, ReqChooseEntranceEvent msg) {
        int novelDatingId = msg.getDatingValue();
        int eventId = msg.getEventId();
        int entranceId = msg.getEntranceId();
        int choiceType = msg.getChoiceType();
        int choice = msg.getChoice();
        NovelCfgBean novelBean = GameDataManager.getNovelCfgBean(novelDatingId);
        if (novelBean == null) {
            return;
        }
        NovelDatingData novelDatingData =
                player.getNovelDatingManager().getDataMap().get(novelDatingId);

        if (novelDatingData == null) {
            MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_NOT_EXIST, "约会数据不存在");
            return;
        }
        if (!novelDatingData.getEntrances().containsKey(entranceId)) {
            MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_BUILDING_INVALID, "建筑不可用");
            // return;
        }
        NovelScriptCfgBean fsc = GameDataManager.getNovelScriptCfgBean(entranceId);
        boolean hasEnding = false;
        // 移除入口
        boolean need2Push = false;
        Map<Integer, Integer> endingItems = new HashMap<Integer, Integer>();
        Map<Integer, Integer> normalItems = new HashMap<Integer, Integer>();
        Map<Integer, Integer> costItems = new HashMap<Integer, Integer>();
        // 下一剧本
        int nextId = 0;
        NovelDatingManager mng = player.getNovelDatingManager();
        if (choiceType == FavorDatingConst.BUILDING_EVENT_TYPE_SCRIPT) {
            // 验证剧本
            int lastDatingId = getLastScriptEvent(novelDatingData, entranceId);
            // 如果是初始剧本
            lastDatingId = lastDatingId > 0 ? lastDatingId : fsc.getStartId();
            NovelDatingCfgBean odc = GameDataManager.getNovelDatingCfgBean(lastDatingId);
            // 下一剧本或结算
            odc = GameDataManager.getNovelDatingCfgBean(eventId);
            if (odc == null) {
                MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_SCRIPT_INVALID,
                        "事件不可用");
                return;
            }
            // 有道具
            if (odc.getNodeCost() != null) {
                Map<Integer, Integer> nodeItem = (Map<Integer, Integer>) odc.getNodeCost()
                        .get(NovelDatingConst.SCRIPT_CONDITION_ITEM);
                if (nodeItem != null) {
                    if (!mng.isItemsEnough(player, nodeItem, novelDatingData)) {
                        MessageUtils.throwCondtionError(
                                GameErrorCode.OUTSIDE_DATING_ITEM_NOT_ENOUGH, "需要道具不足");
                    }
                    removeItems(nodeItem, novelDatingData);
                    combineValueCount(costItems, nodeItem, true);
                }
                List<Integer> nodeMark = (List<Integer>) odc.getNodeCost()
                        .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                if (nodeMark != null) {
                    if (!mng.isMarksEnough(nodeMark, novelDatingData)) {
                        MessageUtils.throwCondtionError(
                                GameErrorCode.OUTSIDE_DATING_ITEM_NOT_ENOUGH, "需要道具不足");
                        return;
                    }
                    for (Integer mark : nodeMark) {
                        removeMark(novelDatingData, mark);
                    }
                }
            }
            if (odc.getNodeReward() != null) {
                Map<Integer, Integer> nodeItem = (Map<Integer, Integer>) odc.getNodeReward()
                        .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                if (nodeItem != null) {
                    addItems(novelDatingData, nodeItem);
                    combineValueCount(normalItems, nodeItem, true);
                }
                List<Integer> nodeMark = (List<Integer>) odc.getNodeReward()
                        .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                if (nodeMark != null) {
                    for (Integer mark : nodeMark) {
                        addMark(novelDatingData, mark);
                    }
                }
            }
            if (odc.getJump() != null && odc.getJump().length > 0) {
                if (choice - 1 < 0 || choice - 1 > odc.getJump().length) {
                    MessageUtils.throwCondtionError(
                            GameErrorCode.OUTSIDE_DATING_NEXT_SCRIPT_INVALID, "后置剧本不可用");
                    return;
                }
                nextId = odc.getJump()[choice - 1];
                recordCurrentScriptEvent(novelDatingData, entranceId, nextId);
            } else {
                // 有结算
                // 标记处理
                need2Push = true;
                if (odc.getAddSign() != null) {
                    for (int sign : odc.getAddSign()) {
                        addMark(novelDatingData, sign);
                    }
                }
                if (odc.getDelSign() != null) {
                    for (int sign : odc.getDelSign()) {
                        removeMark(novelDatingData, sign);
                    }
                }
                // 结局
                if (!odc.getOutsideType()) {
                    // 计算时间
                    if (fsc.getAddTime() != null && fsc.getAddTime().length() > 0) {
                        String addTime = fsc.getAddTime();
                        novelDatingData
                                .setCurrentTime(player.getFavorDatingManager().formatTime(addTime));
                    } else if (fsc.getCostTime() > 0) {
                        int beginTime = novelDatingData.getCurrentTime();
                        novelDatingData.setCurrentTime(fsc.getCostTime() + beginTime);
                    }
                    Map<Integer, Integer> items = (Map<Integer, Integer>) odc.getOutsideReward()
                            .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                    if (items != null) {
                        addItems(novelDatingData, items);
                        mng.combineValueCount(normalItems, items, true);
                    }
                    List<Integer> marks = (List<Integer>) odc.getOutsideReward()
                            .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                    if (marks != null) {
                        for (Integer mark : marks) {
                            addMark(novelDatingData, mark);
                        }
                    }
                } else {
                    hasEnding = true;
                    Map<Integer, Integer> items;
                    List<Integer> marks;
                    // 首次
                    if (odc.getOutsideUnlock() > 0
                            && !novelDatingData.getDoneDatings().contains(novelDatingId)) {
                        items = (Map<Integer, Integer>) odc.getOutsideEnd()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        marks = (List<Integer>) odc.getOutsideEnd()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                    }
                    // 重复
                    else {
                        items = (Map<Integer, Integer>) odc.getOutsideRepeat()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        marks = (List<Integer>) odc.getOutsideRepeat()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                    }
                    if (items != null) {
                        addItems(novelDatingData, items);
                        combineValueCount(endingItems, items, true);
                    }
                    if (marks != null) {
                        for (Integer mark : marks) {
                            addMark(novelDatingData, mark);
                        }
                    }
                }
                if (fsc.getDelete())
                    need2Push = true;
                removeEntrance(player, novelDatingData, fsc.getId());
                handleTempData(player, novelDatingData);
                recordCurrentScriptEvent(novelDatingData, entranceId, -1);
                // 阶段转换
                int nextPara = fsc.getStepJump();

                if (nextPara > novelDatingData.getCurrentParagraphId()) {
                    go2NextParagragh(player, novelDatingData, nextPara, novelDatingId);
                    need2Push = true;
                }
                if (hasEnding) {
                    novelDatingData.addDoneNovelDating(novelDatingId);
                    mng.clearNovelData(novelDatingData);
                    // 处理副本结束
                    normalItems = dealDungeonData(player, novelBean);
                    // 副本城市约会新增指定精灵奖励
                    addResource(player, normalItems, odc.getRoleReward());
                    need2Push = true;
                }
                if (need2Push) {
                    if (fsc.getIsGuide() == 1) {
                        novelDatingData.getGuideScript().add(fsc.getId());
                    }
                    // sendParagraghChange(player, novelDatingId, novelDatingData);
                }
                if (!novelDatingData.getCompleteEndings().contains(odc.getScriptId())) {
                    novelDatingData.getCompleteEndings().add(odc.getScriptId());
                }
            }
        }
        // 建筑入口的刷新
        if (need2Push && !hasEnding) {
            refreshEntrances(player, novelDatingData, novelDatingId);
            sendParagraghChange(player, novelDatingId, novelDatingData);
        }
        // 刷新
        RespEntranceEventChoosed.Builder builder =
                NovelDatingMsgBuilder.createRespEntranceEventChoosed(novelDatingData, normalItems,
                        hasEnding, endingItems, costItems, novelDatingId);
        MessageUtils.send(player, builder);
    }

    public Map<Integer, Integer> getOpeningEntrances(Player player, NovelDatingData fdd,
            List<NovelScriptCfgBean> wholeBuildings) {
        // 入口开放判定
        return wholeBuildings.stream()
                .filter(entrance -> validateDisplayConditions(player, fdd, entrance))
                .collect(Collectors.toMap(NovelScriptCfgBean::getId, value -> {
                    return -1;
                }));
    }

    @SuppressWarnings("unchecked")
    public boolean validateDisplayConditions(Player player, NovelDatingData fdd,
            NovelScriptCfgBean wholeBuilding) {
        String limitTime = wholeBuilding.getLimitTime();
        if (limitTime == null || limitTime.length() <= 0) {
            return true;
        }
        String[] spTime = limitTime.split("-");
        if (spTime.length != 2) {
            return true;
        }
        // 时间验证
        FavorDatingManager mng = player.getFavorDatingManager();
        String beginStr = spTime[0];
        String endStr = spTime[1];
        int beginTime = mng.formatTime(beginStr);
        int endTime = mng.formatTime(endStr);
        if (fdd != null) {
            int currentTime = fdd.getCurrentTime();
            if (currentTime > endTime || endTime < beginTime) {
                return false;
            }
        }
        // 默认条件为是否隐藏,返回是否显示时取反
        if (wholeBuilding.getHideCondition1().isEmpty()
                && wholeBuilding.getHideCondition2().isEmpty())
            return true;
        return !validateNormalConditions(player, ConditionValidatorFactory.entranceValidators, fdd,
                (Map<Object, Object>) wholeBuilding.getHideCondition1(),
                (Map<Object, Object>) wholeBuilding.getHideCondition2(), wholeBuilding.getHide());
    }

    private boolean validateNormalConditions(Player player,
            Map<String, ConditionValidator> validators, NovelDatingData fdd,
            Map<Object, Object> existConditions, Map<Object, Object> notExistConditions,
            boolean defaultStaut) {
        ConditionValidator validator;
        for (Entry<Object, Object> entry : existConditions.entrySet()) {
            validator = validators.get(entry.getKey().toString());
            // 条件未达成,保持默认显示状态
            if (!validator.validate(player, fdd, entry.getValue()))
                return defaultStaut;
        }
        boolean totalExistStaut = true;
        boolean existConditionStaut = false;
        if (notExistConditions != null) {
            for (Entry<Object, Object> entry : notExistConditions.entrySet()) {
                validator = validators.get(entry.getKey().toString());
                // 条件达成,保持默认显示状态
                totalExistStaut =
                        totalExistStaut && validator.validate(player, fdd, entry.getValue());
                existConditionStaut = true;
            }
        }
        // 条件未中断,达成默认显示反转
        return ((!existConditionStaut) || (!totalExistStaut)) ? !defaultStaut : defaultStaut;
    }

    public NovelDatingData createData(Player player, NovelDatingData ndd, int novelDatingId) {
        NovelStepCfgBean stepBean = GameDataManager.getNovelStepCfgBeans().stream()
                .filter(e -> e.getMainId() == novelDatingId)
                .sorted(Comparator.comparing(NovelStepCfgBean::getId)).findFirst().get();
        List<Integer> steps = new ArrayList<Integer>();
        if (stepBean != null) {
            steps.add(stepBean.getId());
        }
        // 获取所有事件
        List<NovelScriptCfgBean> wholeBuildings = new ArrayList<NovelScriptCfgBean>();
        int[] events = stepBean.getEvent();
        for (Integer novelScriptId : events) {
            wholeBuildings.add(GameDataManager.getNovelScriptCfgBean(novelScriptId));
        }
        Map<Integer, Integer> entrances = getOpeningEntrances(player, null, wholeBuildings);
        int beginTime = player.getFavorDatingManager().formatTime(stepBean.getStepTime());
        // 重新初始数据
        if (ndd == null) {
            ndd = new NovelDatingData(steps, entrances,
                    beginTime);
        } else {
            ndd.setParagraghs(steps);
            ndd.setEntrances(entrances);
            ndd.setCurrentTime(beginTime);
        }
        return ndd;
    }

    /** 当前约会是否需要初始化 */
    private boolean need2Init(NovelDatingData ndd) {
        // 根据阶段记录来判定当前外传是否可用
        return ndd == null || ndd.getParagraghs() == null || ndd.getParagraghs().size() <= 0;
    }

    /** 节点上下文是否包含本次剧本 */
    public int getLastScriptEvent(NovelDatingData osd, int building) {
        if (osd.getEntrances() == null)
            return -1;
        Integer context = (Integer) osd.getEntrances().get(building);
        if (context == null)
            return -1;
        // 节点上下文是否包含本次剧本
        return context;
    }

    public void combineValueCount(Map<Integer, Integer> base, Map<Integer, Integer> addition,
            boolean addable) {
        for (Entry<Integer, Integer> entry : addition.entrySet()) {
            int baseCount = base.getOrDefault(entry.getKey(), 0);
            int result = addable ? baseCount + entry.getValue() : baseCount - entry.getValue();
            base.put(entry.getKey(), result);
        }
    }

    public void removeItems(Map<Integer, Integer> items, NovelDatingData osd) {
        combineValueCount(osd.getTempBag(), items, false);
    }

    public boolean removeMark(NovelDatingData osd, Integer mark) {
        // 临时标记
        boolean result = osd.getTempFlag().remove(mark);
        // 临时标记没有则反标记
        if (!result)
            osd.getTempFlag().add(-mark);
        return result;
    }

    public void addItems(NovelDatingData osd, Map<Integer, Integer> items) {
        combineValueCount(osd.getTempBag(), items, true);
    }

    public void addMark(NovelDatingData osd, Integer mark) {
        // 判定是否存在临时反标记
        if (!osd.getTempFlag().remove(-mark))
            osd.getTempFlag().add(mark);
    }

    /** 节点上下文记录下次剧本 */
    public void recordCurrentScriptEvent(NovelDatingData osd, int building, int event) {
        if (osd.getEntrances().containsKey(building))
            osd.getEntrances().put(building, event);
    }

    public void removeEntrance(Player player, NovelDatingData osd, int entrance) {
        if (osd.getEntrances().remove(entrance) == null)
            return;
        if (osd.getRmEntrance() == null)
            osd.setRmEntrance(new LinkedList<Integer>());
        if (!osd.getRmEntrance().contains(entrance))
            osd.getRmEntrance().add(entrance);
    }

    public void handleTempData(Player player, NovelDatingData osd) {
        handleTempItems(player, osd);
        osd.getTempBag().clear();
        // 标记
        if (osd.getEventFlag() == null)
            osd.setEventFlag(new ArrayList<Integer>());
        for (Integer mark : osd.getTempFlag()) {
            if (mark > 0)
                osd.getEventFlag().add(mark);
            else
                osd.getEventFlag().remove((Integer) (-mark));
        }
        osd.getTempFlag().clear();
    }

    private void handleTempItems(Player player, NovelDatingData osd) {
        // 正常道具
        Map<Integer, Integer> normalAdd = new HashMap<Integer, Integer>();
        Map<Integer, Integer> normalCost = new HashMap<Integer, Integer>();

        for (Map.Entry<Integer, Integer> item : osd.getTempBag().entrySet()) {
            if (item.getValue() > 0)
                normalAdd.put(item.getKey(), item.getValue());
            else
                normalCost.put(item.getKey(), item.getValue());
        }
        if (!normalAdd.isEmpty()) {
            player.getBagManager().addItems(normalAdd, true, EReason.FAVOR_DATING_ADD);
        }
        if (!normalCost.isEmpty()) {
            player.getBagManager().removeItemsByTemplateIdNoCheck(normalCost, true,
                    EReason.FAVOR_DATING_REMOVE);
        }
    }
    public void go2NextParagragh(Player player, NovelDatingData osd, int nextParagragh,
            int outsideId) {
        // 获取新阶段入口
        List<NovelScriptCfgBean> wholeBuildings = getWholeBuildings(nextParagragh);
        // 开放入口获取
        Map<Integer, Integer> entrances = getOpeningEntrances(player, osd, wholeBuildings);
        // 阶段刷新
        osd.setEntrances(entrances);
        osd.addParagraghId(nextParagragh);
    }
    public List<NovelScriptCfgBean> getWholeBuildings(int step) {
        List<NovelScriptCfgBean> wholeEvents = new ArrayList<NovelScriptCfgBean>();
        NovelStepCfgBean stepBean = GameDataManager.getNovelStepCfgBean(step);
        if (stepBean == null) {
            return wholeEvents;
        }
        int[] events = stepBean.getEvent();
        for (Integer favorScriptId : events) {
            wholeEvents.add(GameDataManager.getNovelScriptCfgBean(favorScriptId));
        }
        return wholeEvents;
    }

    public void sendParagraghChange(Player player, int novelId, NovelDatingData osd) {
        SettleInfo.Builder resp = SettleInfo.newBuilder();
        DatingInfo info = NovelDatingMsgBuilder.createNovelDatingInfo(player, novelId, osd);
        resp.setInfo(info);
        MessageUtils.send(player, resp);
    }

    public void refreshEntrances(Player player, NovelDatingData osd, int novelId) {
        int currentParagraph = osd.getCurrentParagraphId();
        // 获取阶段入口
        List<NovelScriptCfgBean> wholeBuildings = getWholeBuildings(currentParagraph);
        for (NovelScriptCfgBean favorScriptCfg : wholeBuildings) {
            // 已经被移除了,不再检测
            if (osd.getRmEntrance() != null && osd.getRmEntrance().contains(favorScriptCfg.getId()))
                continue;
            // 进行检测,可用的添加,不可用的移除
            boolean visual = validateDisplayConditions(player, osd, favorScriptCfg);
            if (visual)
                osd.getEntrances().putIfAbsent(favorScriptCfg.getId(), -1);
            else
                removeEntrance(player, osd, favorScriptCfg.getId());
        }
    }

    // 增加指定精灵的资源，目前只有好感度需求
    private void addResource(Player player, Map<Integer, Integer> rewards, int[] roleReward) {

        if (roleReward == null || roleReward.length != 3) {
            return;
        }
        if (roleReward[1] == ItemConstantId.ROLE_FAVOR) {
            Role role = player.getRoleManager().getRole(roleReward[0]);
            if (role == null) {
                return;
            }
            player.getRoleManager().changeFavor(role, roleReward[2], EReason.NOVEL_DATING_REWARD);
        }
        rewards.put(roleReward[1], roleReward[2]);
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> dealDungeonData(Player player, NovelCfgBean novelBean) {
        // 保存副本通关相关数据
        Map<Integer, Integer> rewards = Maps.newHashMap();
        int dgLevelId = novelBean.getDgLevelId();
        DungeonLevelCfgBean dgLevelBean = GameDataManager.getDungeonLevelCfgBean(dgLevelId);
        if (dgLevelBean == null) {
            MessageUtils.throwConfigError(GameErrorCode.OUTSIDE_DATING_NOT_EXIST, String.valueOf(dgLevelId), "数据不存在");
        }
        DungeonBean dungeonLevel = player.getDungeonManager().getOrInitDungeonBean(dgLevelId);
        dungeonLevel.setStar(3);
        List<Integer> achieveGoals = Lists.newArrayList();
        achieveGoals.add(1);
        dungeonLevel.setAchieveGoals(achieveGoals);
        dungeonLevel.setWin(true);
        int rewardId = 0;
        if (dgLevelBean.getFirstReward() != 0 && dungeonLevel.getTotalSceneCount() == 0) {
            rewardId = dgLevelBean.getFirstReward();
        } else {
            rewardId = dgLevelBean.getReward();
        }
        if (rewardId != 0) {
            BaseGoods cfg = GameDataManager.getBaseGoods(rewardId);
            ItemPackageHelper.unpack(cfg.getUseProfit(), null, rewards);
        }
        if (!rewards.isEmpty()) {
            player.getBagManager().addItems(rewards, true, EReason.DUNGEON_DUNGEON_PASS);
        }
        dungeonLevel.setTotalSceneCount(dungeonLevel.getTotalSceneCount() + 1);
        // 更新一次副本信息
        S2CDungeonMsg.GetLevelInfo.Builder builder = DungeonMsgBuilder.getDungeonRecordMsg(player);
        MessageUtils.send(player, builder);
        return rewards;
    }
}
