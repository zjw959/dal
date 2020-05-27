package logic.favor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorReward;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices;
import org.game.protobuf.s2c.S2CExtraDatingMsg.DatingInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.SettleInfo;

import data.GameDataManager;
import data.bean.DatingVariableCfgBean;
import data.bean.FavorMessageCfgBean;
import data.bean.FavorScriptCfgBean;
import data.bean.FavorStepCfgBean;
import event.Event;
import event.IEventListener;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.BagType;
import logic.constant.EEventType;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.favor.structs.FavorDatingData;
import logic.msgBuilder.FavorDatingBuilder;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import utils.TimeUtil;

/**
 * 
 * @Description 主线约会管理器
 * @author zhaojianbo
 *
 */
public class FavorDatingManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, IEventListener {
    private static final Logger LOGGER = Logger.getLogger(FavorDatingManager.class);
    private Map<Integer, Map<Integer, FavorDatingData>> dataMap =
            new HashMap<Integer, Map<Integer, FavorDatingData>>();
    private List<Integer> completedDating = new ArrayList<Integer>();
    private List<Integer> rewardFavorId = new ArrayList<>();
    private Map<Integer, Integer> noticedFavorId = new HashMap<Integer, Integer>();

    /**
     * 请求主线约会界面相关信息
     * 
     * @param player
     * @param roleId
     * @param favorDatingId
     */
    public void reqFavorDatingInfo(Player player, int roleId, int favorDatingId) {
        LogicScriptsUtils.getIFavorDatingScript().reqFavorDatingInfo(player, roleId,
                favorDatingId);
    }

    /**
     * 开始主线约会
     * 
     * @param player
     * @param roleId
     * @param favorDatingId
     * @param entranceId
     */
    public void reqStartEntrance(Player player, int roleId, int favorDatingId, int entranceId){
        LogicScriptsUtils.getIFavorDatingScript().reqStartEntrance(player, roleId, favorDatingId,
                entranceId);
    };

    /**
     * 请求选项
     * 
     * @param player
     * @param msg
     * 
     */
    public void reqGetEventChoices(Player player, ReqGetEventChoices msg) {
        LogicScriptsUtils.getIFavorDatingScript().reqGetEventChoices(player, msg);
    };

    /**
     * 选项选取
     * 
     * @param player
     * @param msg
     */
    public void reqChooseEntrance(Player player, ReqChooseEntranceEvent msg) {
        LogicScriptsUtils.getIFavorDatingScript().reqChooseEntrance(player, msg);
    }
    
    /**
     * 请求札记界面主线 米面板相关信息
     * 
     * @param player
     * @param msg
     */
    public void reqFavorDatingPanel(Player player, int roleId) {
        LogicScriptsUtils.getIFavorDatingScript().reqFavorDatingPanel(player, roleId);
    }
    
    /**
     * 请求领取奖励
     * 
     * @param player
     * @param msg
     */
    public void reqFavorReward(Player player, ReqFavorReward msg) {
        LogicScriptsUtils.getIFavorDatingScript().reqFavorReward(player, msg);
    }
    
    /**
     * 请求获取精灵章节提示状态
     * 
     * @param player
     */
    public void reqRoleStatue(Player player) {
        LogicScriptsUtils.getIFavorDatingScript().reqRoleStatue(player);
    }

    public void dealNoticeStatue(Player player, int roleId) {
        LogicScriptsUtils.getIFavorDatingScript().dealNoticeStatue(player, roleId);
    }

    /**
     * 触发章节开启通知精灵
     * 
     */
    @Override
    public void eventPerformed(Event event) {
        LogicScriptsUtils.getIFavorDatingScript().eventPerformed(player, event);
    }

    /**
     * 获取主线约会玩家数据
     * 
     */
    public FavorDatingData getFavorDatingData(Player player, int roleId, int favorDatingId,
            boolean initial) {
        Map<Integer, FavorDatingData> roleFavorData = dataMap.get(roleId);
        if (roleFavorData == null
                || (initial && need2Init(roleFavorData.get(favorDatingId), new Date()))) {
            Map<Integer, FavorDatingData> roleFavorData2 = new HashMap<Integer, FavorDatingData>();
            roleFavorData2.put(favorDatingId, initFavorDatingData(player, favorDatingId,
                    roleFavorData == null ? null : roleFavorData.get(favorDatingId)));
            dataMap.put(roleId, roleFavorData2);
        } else
            checkScriptDate(roleFavorData.get(favorDatingId), new Date());
        return dataMap.get(roleId).get(favorDatingId);

    }

    /**
     * 初始化主线约会玩家数据
     * 
     */
    private FavorDatingData initFavorDatingData(Player player, int favorDatingId,
            FavorDatingData fdd) {
        int step = -1;
        boolean beforeDay = GameDataManager.getFavorStepCfgBeans().stream()
                .filter(e -> e.getDay() < 0).findAny().isPresent();
        if (!completedDating.contains(favorDatingId) && beforeDay) {
            FavorStepCfgBean stepBean = GameDataManager.getFavorStepCfgBeans().stream()
                    .filter(e -> e.getDay() < 0)
                    .sorted(Comparator.comparing(FavorStepCfgBean::getId)).findFirst().get();
            if (stepBean != null) {
                step = stepBean.getId();
            }
        } else {
            // 完成过主线约会，从建筑事件开始
            try {
                FavorStepCfgBean stepBean = GameDataManager.getFavorStepCfgBeans().stream()
                        .filter(e -> e.getDay() > 0 && e.getOutsideId() == favorDatingId)
                        .sorted(Comparator.comparing(FavorStepCfgBean::getId)).findFirst().get();

                if (stepBean != null) {
                    step = stepBean.getId();
                }
            } catch (Exception e2) {
                MessageUtils.throwCondtionError(GameErrorCode.MAIN_DATING_IS_NULL,
                        "没有找到配置  favordating:" + favorDatingId);

            }

        }
        List<Integer> steps = new ArrayList<Integer>();
        steps.add(step);
        // 获取阶段step
        FavorStepCfgBean stepBean = GameDataManager.getFavorStepCfgBean(step);
        // 获取所有事件
        List<FavorScriptCfgBean> wholeBuildings = new ArrayList<FavorScriptCfgBean>();
        int[] events = stepBean.getEvent();
        for (Integer favorScriptId : events) {
            wholeBuildings.add(GameDataManager.getFavorScriptCfgBean(favorScriptId));
        }
        Map<Integer, Integer> entrances =
                getOpeningEntrances(player, null, wholeBuildings, favorDatingId);
        int defaultSchedule = 1;
        int beginTime = formatTime(stepBean.getStepTime());
        // 重新初始数据
        if (fdd == null) {
            fdd = new FavorDatingData(steps, defaultSchedule, entrances, defaultSchedule,
                    beginTime);
        } else {
            fdd.setParagraghs(steps);
            fdd.setEntranceSchedule(defaultSchedule);
            fdd.setEntrances(entrances);
            fdd.setDateSchedule(defaultSchedule);
            fdd.setCurrentTime(beginTime);
            fdd.setLastDateSchedule(new Date());
            fdd.getQualityMap().clear();
        }
        // 初始化,清属性map标识
        fdd.setRePlay(true);
        return fdd;
    }

    /**
     * 获取可用的建筑入口
     * 
     * @param player 玩家
     * @param fdd 玩家主线数据
     * @param wholeBuildings 建筑入口配置对象
     * @return 可用的建筑入口id
     */
    public Map<Integer, Integer> getOpeningEntrances(Player player, FavorDatingData fdd,
            List<FavorScriptCfgBean> wholeBuildings, int favorDatingId) {
        // 入口开放判定
        return wholeBuildings.stream()
                .filter(entrance -> validateDisplayConditions(player, fdd, entrance, favorDatingId))
                .collect(Collectors.toMap(FavorScriptCfgBean::getId, value -> {
                    return -1;
                }));
    }

    /** 检测入口是否显示 */
    @SuppressWarnings("unchecked")
    public boolean validateDisplayConditions(Player player, FavorDatingData fdd,
            FavorScriptCfgBean wholeBuilding, int favorDatingId) {
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
            if (currentTime > endTime || currentTime < beginTime) {
                return false;
            }
        }
        // 默认条件为是否隐藏,返回是否显示时取反
        if ((wholeBuilding.getHideCondition1() == null
                || wholeBuilding.getHideCondition1().isEmpty()))
            // && wholeBuilding.getHideCondition2().isEmpty())
            return true;

        return !validateNormalConditions(player, ConditionValidatorFactory.entranceValidators, fdd,
                (Map<Object, Object>) wholeBuilding.getHideCondition1(),
                (Map<Object, Object>) wholeBuilding.getHideCondition2(), wholeBuilding.getHide(),
                favorDatingId);
    }

    private boolean validateNormalConditions(Player player,
            Map<String, ConditionValidator> validators, FavorDatingData fdd,
            Map<Object, Object> existConditions, Map<Object, Object> notExistConditions,
            boolean defaultStaut, int favorDatingId) {
        ConditionValidator validator;
        if (existConditions != null) {
            for (Entry<Object, Object> entry : existConditions.entrySet()) {
                validator = validators.get(entry.getKey().toString());
                // 条件未达成,保持默认显示状态
                if (!validator.validate(player, fdd, entry.getValue(), favorDatingId))
                    return defaultStaut;
            }
        }
        boolean totalExistStaut = true;
        boolean existConditionStaut = false;
        if (notExistConditions != null) {
            for (Entry<Object, Object> entry : notExistConditions.entrySet()) {
                validator = validators.get(entry.getKey().toString());
                // 条件达成,保持默认显示状态
                totalExistStaut =
                        totalExistStaut
                                && validator.validate(player, fdd, entry.getValue(), favorDatingId);
                existConditionStaut = true;
            }
        }
        // 条件未中断,达成默认显示反转
        return ((!existConditionStaut) || (!totalExistStaut)) ? !defaultStaut : defaultStaut;
    }
    public boolean isMarksEnough(List<Integer> needMarks, FavorDatingData osd) {
        // 临时标记
        List<Integer> flags =
                new ArrayList<Integer>(osd.getTempFlag());
        if (osd.getEventFlag() != null)
            flags.addAll(osd.getEventFlag());
        return isMarksEnough(needMarks, flags);
    }

    private boolean isMarksEnough(List<Integer> needMarks, List<Integer> existMarks) {
        if (existMarks == null)
            return false;
        // 是否存在标记
        for (Integer mark : needMarks) {
            if (!existMarks.contains(mark))
                return false;
        }
        return true;
    }

    public boolean notMarksEnough(List<Integer> needMarks, FavorDatingData osd) {
        // 临时标记
        List<Integer> flags = new ArrayList<Integer>(osd.getTempFlag());
        if (osd.getEventFlag() != null)
            flags.addAll(osd.getEventFlag());
        return notMarksEnough(needMarks, flags);
    }

    private boolean notMarksEnough(List<Integer> needMarks, List<Integer> existMarks) {
        if (existMarks == null)
            return false;
        // 是否存在标记
        for (Integer mark : needMarks) {
            if (existMarks.contains(mark))
                return false;
        }
        return true;
    }
    public boolean isItemsEnough(Player player, Map<Integer, Integer> items, FavorDatingData osd) {
        // 临时道具
        Map<Integer, Integer> exists = new HashMap<Integer, Integer>(osd.getTempBag());
        if (osd.getBag() != null)
            combineValueCount(exists, osd.getBag(), true);
        return isItemsEnough(player, items, exists);
    }

    public boolean notItemsEnough(Player player, Map<Integer, Integer> items, FavorDatingData osd) {
        // 临时道具
        Map<Integer, Integer> exists = new HashMap<Integer, Integer>(osd.getTempBag());
        if (osd.getBag() != null)
            combineValueCount(exists, osd.getBag(), true);
        return notItemsEnough(player, items, exists);
    }
    public void combineValueCount(Map<Integer, Integer> base, Map<Integer, Integer> addition,
            boolean addable) {
        for (Entry<Integer, Integer> entry : addition.entrySet()) {
            int baseCount = base.getOrDefault(entry.getKey(), 0);
            int result = addable ? baseCount + entry.getValue() : baseCount - entry.getValue();
            base.put(entry.getKey(), result);
        }
    }

    public void removeItems(Map<Integer, Integer> items, FavorDatingData osd) {
        combineValueCount(osd.getTempBag(), items, false);
    }

    public boolean removeMark(FavorDatingData osd, Integer mark) {
        // 临时标记
        boolean result = osd.getTempFlag().remove(mark);
        // 临时标记没有则反标记
        if (!result)
            osd.getTempFlag().add(-mark);
        return result;
    }

    public void addItems(FavorDatingData osd, Map<Integer, Integer> items) {
        combineValueCount(osd.getTempBag(), items, true);
    }

    public void addQuality(FavorDatingData osd, Map<Integer, Integer> qualityMap) {
        Map<Integer, Integer> base = osd.getTempQuality();
        for (Entry<Integer, Integer> entry : qualityMap.entrySet()) {
            int baseCount = base.getOrDefault(entry.getKey(), 0);
            int result = baseCount + entry.getValue();
            DatingVariableCfgBean dvBean = GameDataManager.getDatingVariableCfgBean(entry.getKey());
            // 属性上下限判检查
            int min = 0;
            int max = 100;
            if(dvBean !=null){
                int[] limit = dvBean.getLimit();
                min = limit[0];
                max = limit[1];
            }
            if(result<min){
                result = min;
            }else if(result>max){
                result = max; 
            }
            base.put(entry.getKey(), result);
        }
    }
    public void addMark(FavorDatingData osd, Integer mark) {
        // 判定是否存在临时反标记
        if (!osd.getTempFlag().remove(-mark))
            osd.getTempFlag().add(mark);
    }

    public void removeEntrance(Player player, FavorDatingData osd, int entrance) {
        if (osd.getEntrances().remove(entrance) == null)
            return;
        if (osd.getRmEntrance() == null)
            osd.setRmEntrance(new LinkedList<Integer>());
        if (!osd.getRmEntrance().contains(entrance))
            osd.getRmEntrance().add(entrance);
    }
    private boolean isItemsEnough(Player player, Map<Integer, Integer> items,
            Map<Integer, Integer> bag) {
        // 正常道具
        Map<Integer, Integer> normal = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> item : items.entrySet()) {
            // 道具
            if (GameDataManager.getItemCfgBean(item.getKey()).getBagType() == BagType.FAVOR_DATING
                    && item.getValue() > bag.getOrDefault(item.getKey(), 0))
                return false;
            else {
                // 临时背包
                int needCount = item.getValue() - bag.getOrDefault(item.getKey(), 0);
                if (needCount > 0)
                    normal.put(item.getKey(), needCount);
            }
            int needCount = item.getValue() - bag.getOrDefault(item.getKey(), 0);
            if (needCount > 0) {
                normal.put(item.getKey(), needCount);
            }
        }
        // 是否存在足够道具
        for (Integer templateId : normal.keySet()) {
            if (player.getBagManager().getItemCount(templateId) < normal.get(templateId)) {
                return false;
            }
        }
        return true;
    }

    private boolean notItemsEnough(Player player, Map<Integer, Integer> items,
            Map<Integer, Integer> bag) {
        // 正常道具
        Map<Integer, Integer> normal = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> item : items.entrySet()) {
            // 道具
            if (GameDataManager.getItemCfgBean(item.getKey()).getBagType() == BagType.FAVOR_DATING
                    && item.getValue() > bag.getOrDefault(item.getKey(), 0))
                return false;
            else {
                // 临时背包
                int needCount = item.getValue() - bag.getOrDefault(item.getKey(), 0);
                if (needCount > 0)
                    normal.put(item.getKey(), needCount);
            }
            int needCount = item.getValue() - bag.getOrDefault(item.getKey(), 0);
            if (needCount > 0) {
                normal.put(item.getKey(), needCount);
            }
        }
        for (Integer templateId : normal.keySet()) {
            if (player.getBagManager().getItemCount(templateId) >= normal.get(templateId)) {
                return false;
            }
        }
        return true;
    }

    public void checkScriptDate(FavorDatingData osd, Date now) {
        if (osd == null || osd.getLastDateSchedule() == null)
            return;
        // 天数刷新
        if (now.after(osd.getLastDateSchedule())
                && !TimeUtil.isSameDay(now, osd.getLastDateSchedule())) {
            osd.setDateSchedule(osd.getDateSchedule() + 1);
            osd.setLastDateSchedule(now);
        }
    }

    /** 当前外传是否需要初始化 */
    private boolean need2Init(FavorDatingData osd, Date now) {
        // 根据阶段记录来判定当前外传是否可用
        return osd == null || (osd.getLastDateSchedule() == null && 
                (osd.getParagraghs() == null || osd.getParagraghs().size() <= 0));
    }

    /** 节点上下文是否包含本次剧本 */
    public int getLastScriptEvent(FavorDatingData osd, int building) {
        if (osd.getEntrances() == null)
            return -1;
        Integer context = (Integer) osd.getEntrances().get(building);
        if (context == null)
            return -1;
        // 节点上下文是否包含本次剧本
        return context;
    }

    /** 节点上下文记录下次剧本 */
    public void recordCurrentScriptEvent(FavorDatingData osd, int building, int event) {
        if (osd.getEntrances().containsKey(building))
            osd.getEntrances().put(building, event);
    }

    public boolean endingExist(List<Integer> endings, int ending) {
        if (endings == null)
            return false;
        return endings.contains(ending);
    }
    public List<Integer> addEnding(List<Integer> endings, int ending) {
        if (endings == null)
            endings = new ArrayList<Integer>();
        endings.add(ending);
        return endings;
    }

    /**
     * 约会结束时,处理玩家数据
     * 
     */
    public void handleTempData(Player player, FavorDatingData osd, boolean needClearQuality) {
        handleTempItems(player, osd);
        osd.getTempBag().clear();
        // 标记
        if (osd.getEventFlag() == null)
            osd.setEventFlag(new ArrayList<Integer>());
        for (Integer mark : osd.getTempFlag()) {
            if (mark > 0) {
                if (!osd.getEventFlag().contains(mark)) {
                    osd.getEventFlag().add(mark);
                }
            } else {
                osd.getEventFlag().remove((Integer) (-mark));
            }
        }
        osd.getTempFlag().clear();
        // 属性
        Map<Integer, Integer> qualityMap = osd.getQualityMap();
        if (needClearQuality) {
            qualityMap.clear();
        }
        for (Map.Entry<Integer, Integer> quality : osd.getTempQuality().entrySet()) {
            if (qualityMap.containsKey(quality.getKey())) {
                qualityMap.put(quality.getKey(),
                        quality.getValue() + qualityMap.get(quality.getKey()));
            } else {
                qualityMap.put(quality.getKey(), quality.getValue());
            }
        }
        osd.getTempQuality().clear();
    }

    private void handleTempItems(Player player, FavorDatingData osd) {
        Map<Integer, Integer> bag = osd.getBag();
        // 正常道具
        Map<Integer, Integer> normalAdd = new HashMap<Integer, Integer>();
        Map<Integer, Integer> normalCost = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> item : osd.getTempBag().entrySet()) {
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
                if (item.getValue() > 0)
                    normalAdd.put(item.getKey(), item.getValue());
                else
                    normalCost.put(item.getKey(), item.getValue());
            }
        }
        if(!normalAdd.isEmpty()){
            player.getBagManager().addItems(normalAdd, true, EReason.FAVOR_DATING_ADD);
        }
        if(!normalCost.isEmpty()){
            player.getBagManager().removeItemsByTemplateIdNoCheck(normalCost, true,
                    EReason.FAVOR_DATING_REMOVE);
        }
    }

    /**
     * 跳到下一阶段
     * 
     */
    public void go2NextParagragh(Player player, FavorDatingData osd, int nextParagragh,
            int favorDatingId) {
        // 获取新阶段入口
        List<FavorScriptCfgBean> wholeBuildings = getWholeBuildings(nextParagragh, osd);
        // 开放入口获取
        Map<Integer, Integer> entrances =
                getOpeningEntrances(player, osd, wholeBuildings, favorDatingId);
        // 阶段刷新
        List<FavorScriptCfgBean> showBuildings = new ArrayList<FavorScriptCfgBean>();
        entrances.keySet()
                .forEach(e -> showBuildings.add(GameDataManager.getFavorScriptCfgBean(e)));
        List<FavorScriptCfgBean> showEvents = getShowEvents(showBuildings, osd);
        entrances =
                showEvents.stream().collect(Collectors.toMap(FavorScriptCfgBean::getId, value -> {
                    return -1;
                }));
        osd.setEntrances(entrances);
        osd.addParagraghId(nextParagragh);
    }

    /**
     * 根据阶段获取入口
     * 
     */
    public List<FavorScriptCfgBean> getWholeBuildings(int step, FavorDatingData osd) {
        List<FavorScriptCfgBean> wholeEvents = new ArrayList<FavorScriptCfgBean>();
        FavorStepCfgBean stepBean = GameDataManager.getFavorStepCfgBean(step);
        if (stepBean == null) {
            return wholeEvents;
        }
        int[] events = stepBean.getEvent();
        for (Integer favorScriptId : events) {
            wholeEvents.add(GameDataManager.getFavorScriptCfgBean(favorScriptId));
        }
        return wholeEvents;
    }

    /**
     * 约会完成清除数据
     * 
     */
    public void clearDatingData(FavorDatingData osd) {
        osd.setRmEntrance(null);
        osd.getEntrances().clear();
        osd.getTempEndItemMap().clear();
        osd.setDateSchedule(0);
        osd.getParagraghs().clear();
        osd.setLastDateSchedule(null);
        osd.setEventFlag(null);
        osd.setCurrentTime(0);
        osd.getTempChoose().clear();
        if (osd.getBag() != null) {
            // 判定需要清理的道具
            List<Integer> rmItems = new ArrayList<Integer>(osd.getBag().size());
            for (Integer key : ((Map<Integer, Integer>) osd.getBag()).keySet()) {
                if (GameDataManager.getItemCfgBean(key).getDelete())
                    rmItems.add(key);
            }
            for (Integer key : rmItems) {
                osd.getBag().remove(key);
            }
        }
    }

    public void sendParagraghChange(Player player, FavorDatingData osd, int roleId,
            int favorDatingId) {
        SettleInfo.Builder resp = SettleInfo.newBuilder();
        DatingInfo info =
                FavorDatingBuilder.createFavorDateInfo(osd, player, roleId, favorDatingId);
        resp.setInfo(info);
        MessageUtils.send(player, resp);
    }

    public boolean handleEventMessage(Map<Integer, Integer> costItems, Map<Integer, Integer> items,
            Player player, FavorDatingData osd, FavorMessageCfgBean omc, FavorScriptCfgBean osc,
            int choice,int roleId,int favorDatingId) {
        EventMessageHandler handler =
                ConditionValidatorFactory.eventMessageHandlers.get(omc.getType());
        if (handler == null)
            return false;
        return handler.execute(player, osd, omc, osc, choice, items, costItems, roleId, favorDatingId);
    }

    /**
     * 刷新入口
     * 
     */
    public void refreshEntrances(Player player, FavorDatingData osd, int favorDatingId) {
        int currentParagraph = osd.getCurrentParagraphId();
        // 获取阶段入口
        List<FavorScriptCfgBean> wholeBuildings = getWholeBuildings(currentParagraph, osd);
        List<FavorScriptCfgBean> showBuildings = new ArrayList<FavorScriptCfgBean>();
        for (FavorScriptCfgBean favorScriptCfg : wholeBuildings) {
            // 已经被移除了,不再检测
            if (osd.getRmEntrance() != null
                    && osd.getRmEntrance().contains(favorScriptCfg.getId()))
                continue;
            // 进行检测,可用的添加,不可用的移除
            boolean visual = validateDisplayConditions(player, osd, favorScriptCfg, favorDatingId);
            if (visual) {
                showBuildings.add(favorScriptCfg);
                // osd.getEntrances().putIfAbsent(favorScriptCfg.getId(), -1);
            } else {
                removeEntrance(player, osd, favorScriptCfg.getId());
            }
        }
        List<FavorScriptCfgBean> showEvents = getShowEvents(showBuildings, osd);
        // 保存数据
        for (FavorScriptCfgBean favorScriptCfg : showEvents) {
            // for (Integer entrance : osd.getEntrances().keySet()) {
            // if (GameDataManager.getFavorScriptCfgBean(entrance).getBindBuild() == favorScriptCfg
            // .getBindBuild()) {
            // continue flag;
            // }
            // }
            osd.getEntrances().putIfAbsent(favorScriptCfg.getId(), -1);
        }

    }

    public List<FavorScriptCfgBean> getShowEvents(List<FavorScriptCfgBean> showBuildings,
            FavorDatingData osd) {
        // 多个入口出现在同一建筑上，优先显示没有选择过的入口,其次ID优先
        Map<Integer, List<Integer>> buildEntrances = new HashMap<Integer, List<Integer>>();
        List<FavorScriptCfgBean> showEvents = new ArrayList<FavorScriptCfgBean>();
        Map<Integer, List<Integer>> buildRoles = new HashMap<Integer, List<Integer>>();
        for (FavorScriptCfgBean fsc : showBuildings) {
            // 没有绑定建筑 直接显示
            if (fsc.getBindBuild() == 0) {
                showEvents.add(fsc);
                continue;
            }
            // 过滤绑定在同一角色上
            if (fsc.getBindType() == 1) {
                buildRoles.putIfAbsent(fsc.getBindRole(), new ArrayList<Integer>());
                if (buildRoles.containsKey(fsc.getBindRole())) {
                    buildRoles.get(fsc.getBindRole()).add(fsc.getId());
                }
                continue;
            }
            // 过滤绑定在同一地点上
            buildEntrances.putIfAbsent(fsc.getBindBuild(), new ArrayList<Integer>());
            if (buildEntrances.containsKey(fsc.getBindBuild())) {
                buildEntrances.get(fsc.getBindBuild()).add(fsc.getId());
            }
        }
        filteEntrance(showEvents, buildEntrances, osd);
        filteEntrance(showEvents, buildRoles, osd);
        return showEvents;
    }

    public void filteEntrance(List<FavorScriptCfgBean> showEvents,
            Map<Integer, List<Integer>> entrances, FavorDatingData osd) {
        // 筛选过程
        for (List<Integer> values : entrances.values()) {
            if (values.size() == 1) {
                showEvents.add(GameDataManager.getFavorScriptCfgBean(values.get(0)));
            } else {
                // 优先显示没有选择过的入口
                Collections.sort(values);
                boolean needChoose = true;
                flag: for (Integer fscId : values) {
                    if (!osd.getChoosed().contains(fscId)) {
                        showEvents.add(GameDataManager.getFavorScriptCfgBean(fscId));
                        needChoose = false;
                        break flag;
                    }
                }
                // 其次ID
                if (needChoose) {
                    showEvents.add(
                            GameDataManager.getFavorScriptCfgBean(values.get(values.size() - 1)));
                }
            }
        }
    }

    // 处理配置时间,返回距离当天0点的秒数
    public int formatTime(String timeStr) {
        try {
            StringBuffer str = new StringBuffer();
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            str.append(simpleDateFormat2.format(new Date()));
            str.append(" ");
            str.append(timeStr);
            Date date = simpleDateFormat1.parse(str.toString());
            return (int) (date.getTime() - getZeroTime()) / 1000;
        } catch (ParseException e) {
            LOGGER.error("favorscript 时间格式配置错误");
        }
        return 0;
    }

    private long getZeroTime() {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance.getTimeInMillis();
    }

    public Map<Integer, Map<Integer, FavorDatingData>> getDataMap() {
        return dataMap;
    }

    public List<Integer> getRewardFavorId() {
        return rewardFavorId;
    }

    public void setRewardFavorId(List<Integer> rewardFavorId) {
        this.rewardFavorId = rewardFavorId;
    }

    public List<Integer> getCompletedDating() {
        return completedDating;
    }

    @Override
    public void registerPerformed(Player player) {
        player.registerEventListener(EEventType.MAINDATING_ACTIVE.value(), this);
    }

    public Map<Integer, Integer> getNoticedFavorId() {
        return noticedFavorId;
    }

    public void setNoticedFavorId(Map<Integer, Integer> noticedFavorId) {
        this.noticedFavorId = noticedFavorId;
    }
}
