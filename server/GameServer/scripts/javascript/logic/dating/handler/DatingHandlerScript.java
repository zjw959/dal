package javascript.logic.dating.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DatingTypeConstant;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EEventType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.constant.RoleConstant;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.DatingBaseBean;
import logic.dating.bean.dto.DatingDialogDTO;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.IDatingHandlerScript;
import logic.item.ItemPackageHelper;
import logic.item.bean.BasicItem;
import logic.item.bean.Item;
import logic.msgBuilder.DatingMsgBuilder;
import logic.msgBuilder.ShareMsgBuilder;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

public class DatingHandlerScript implements IDatingHandlerScript {

    /** 获取匹配的约会剧本 */
    @Override
    public List<DatingRuleCfgBean> getDatingRuleByRoleIdDatingType(int roleCid, int handlerType) {
        List<DatingRuleCfgBean> allCfgList = GameDataManager.getDatingRuleCfgBeans().stream()
                .filter(cfg -> cfg.getRoleId() == roleCid && cfg.getType() == DatingService
                        .getInstance().getLogicHandler(handlerType).getHandlerIdentification())
                .collect(Collectors.toList());
        return allCfgList;
    }

    /**
     * 创建剧本记录,这是流程的最后一步,可以处理日志
     */
    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
        DatingManager dm = player.getDatingManager();
        CurrentDatingBean record = new DatingBaseBean(dm.newDatingId(),
                // 以类型为标识的处理器
                DatingService.getInstance().getLogicHandler(dto.getScriptType())
                        .getHandlerIdentification(),
                // 起始得分60
                60, 0, startNodeId, branchNodes, cfg.getId(), roleIds);
        dm.putDatingBean(record);
        // 需要日志
        DatingService.getInstance().getLogicHandler(dto.getScriptType())
                .handleAfterCreatedScriptRecord(player, record);
    }

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
        DatingManager dm = player.getDatingManager();
        // 此逻辑可以直接用id做索引查找,用组合条件的原因？——客户端设计当前约会对象唯一（玩家使用客户端只能同时进行一场约会）,未发送id，可以后续改写
        CurrentDatingBean record = dm.getByDatingTypeRoleId(datingType, roleId, datingId);
        return record;
    }

    /**
     * 对话
     */
    @Override
    public void dialog(Player player, DatingDialogDTO msg, Logger LOGGER) {
        int branchNodeId = msg.getBranchNodeId();
        int selectedNodeId = msg.getSelectedNodeId();
        int datingType = msg.getDatingType();
        int roleId = msg.getRoleId();
        boolean isLastNode = msg.isLastNode();

        CurrentDatingBean record = DatingService.getInstance().getLogicHandler(datingType)
                .getByPlayerIdDatingTypeRoleId(datingType, roleId, player, msg.getDatingId());
        // 对玩家接口逻辑的判定拦截
        if (record == null)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "没有找到剧本");
        checkSelectedNode(record, branchNodeId, selectedNodeId, isLastNode);
        int previousCid = record.getCurrentCid();
        // 获取当前节点和前一个节点以及分支节点
        BaseDating previousCfg = DatingService.getInstance().getLogicHandler(datingType)
                .getDatingCfgBeanByType(previousCid, record.getScriptId());
        BaseDating selectedCfg = DatingService.getInstance().getLogicHandler(datingType)
                .getDatingCfgBeanByType(selectedNodeId, record.getScriptId());
        BaseDating branchCfg = DatingService.getInstance().getLogicHandler(datingType)
                .getDatingCfgBeanByType(branchNodeId, record.getScriptId());

        if (previousCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                    "DatingHandler:" + previousCid + "-----+record.getScriptId():"
                            + record.getScriptId() + "--DatingId:" + msg.getDatingId());
        if (selectedCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                    "DatingHandler:" + selectedNodeId + "-----+record.getScriptId():"
                            + record.getScriptId() + "--DatingId:" + msg.getDatingId());

        // 对玩家接口逻辑的判定拦截
        // 校验节点
        if (previousCfg.getScriptId() != selectedCfg.getScriptId())
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR);

        BaseDating datingCfg = selectedCfg;
        BagManager bm = player.getBagManager();
        Map cost = datingCfg.getNodeCost();
        if (cost != null && !cost.isEmpty())
            bm.addItems(cost, true, EReason.DATING_NODE);
        Map reward = datingCfg.getNodeReward();
        if (reward != null && !reward.isEmpty())
            bm.removeItemsByTemplateIdWithCheck(reward, true, EReason.DATING_NODE);
        // 处理中间节点
        dealAmongNode(player, record, previousCfg, branchCfg, selectedCfg);
    }

    @Override
    public boolean isFail(Player player, int roleCid, boolean isLastNode) {
        return false;
    }

    /**
     * 约会结束数据记录
     */
    @Override
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record) {
        DatingManager dm = player.getDatingManager();
        dm.deleteDating(record);
    }

    /**
     * 校验选择的节点id
     *
     * @param record 当前剧本记录
     * @param selectedNodeId 选择的节点id
     */
    @Override
    public void checkSelectedNode(CurrentDatingBean record, int branchNodeId, int selectedNodeId,
            boolean isLastNode) {
        if (isLastNode)
            return;
        Map<Integer, List<Integer>> map = record.getCurrentScript();
        List<Integer> ids = map.get(branchNodeId);
    }

    /**
     * 处理中间节点
     *
     * @param record 当前约会记录
     * @param previousCfg 前一个节点
     * @param branchNodeCfg 分支节点
     * @param selectedNodeCfg 选择节点
     */
    @Override
    public void dealAmongNode(Player player, CurrentDatingBean record, BaseDating previousCfg,
            BaseDating branchNodeCfg, BaseDating selectedNodeCfg) {
        BaseDating previous = previousCfg;
        List<BaseDating> nextNodes = getNextNodes(record, previous);
        List<Integer> idList = Lists.newArrayList();

        int loop = 0;
        while (nextNodes.size() > 0) {
            // 对玩家接口逻辑的判定拦截
            if (loop >= 1000)
                MessageUtils.throwCondtionError(GameErrorCode.UNLIMITED_LOOP, "报警了死循环了");
            // ToolError.isAndTrue(GameErrorCode.UNLIMITED_LOOP, "报警了死循环了", loop
            // >= 1000);
            // 根据规则,中间节点必定是单线,因此如果出现多线则直接判定玩家操作非法
            if (nextNodes.size() > 1)
                MessageUtils.throwCondtionError(GameErrorCode.UNLIMITED_LOOP, "非法跳过中间节点操作");
            // ToolError.isAndTrue(GameErrorCode.UNLIMITED_LOOP,
            // "非法跳过中间节点操作",nextNodes.size() > 1);
            recordCurrentDating(player, nextNodes.get(0), record);
            previous = nextNodes.get(0);
            // 对玩家接口逻辑的判定拦截
            if (idList.contains(previous.getId()))
                MessageUtils.throwCondtionError(GameErrorCode.UNLIMITED_LOOP, "配置异常造成死循环");
            // ToolError.isAndTrue(GameErrorCode.UNLIMITED_LOOP,
            // "配置异常造成死循环",idList.contains(previous.getId()));
            idList.add(previous.getId());
            if (branchNodeCfg != null && previous.getId() == branchNodeCfg.getId()) {
                boolean haveSelectId = false;
                for (Integer nextId : previous.getJump()) {
                    if (nextId == selectedNodeCfg.getId()) {
                        haveSelectId = true;
                    }
                }
                // 对玩家接口逻辑的判定拦截
                if (!haveSelectId)
                    MessageUtils.throwCondtionError(GameErrorCode.BRANCH_NODE_ERR,
                            "分支节点中没有被选中的下级节点");
                // ToolError.isAndTrue(GameErrorCode.BRANCH_NODE_ERR,
                // "分支节点中没有被选中的下级节点",
                // !haveSelectId);
                record.setCurrentCid(selectedNodeCfg.getId());
                record.getSelectedNode().add(selectedNodeCfg.getId());
                recordCurrentDating(player, selectedNodeCfg, record);
                break;
            }
            nextNodes = getNextNodes(record, previous);
            loop++;
        }
    }

    /**
     * 获取下一个节点
     */
    @Override
    public List<BaseDating> getNextNodes(CurrentDatingBean record, BaseDating preNode) {
        List<BaseDating> result = Lists.newArrayList();
        int[] nextNodeIds = preNode.getJump();
        if (nextNodeIds != null && nextNodeIds.length > 0) {
            for (int nextNodeId : nextNodeIds) {
                result.add(DatingService.getInstance().getLogicHandler(record.getDatingType())
                        .getDatingCfgBeanByType(nextNodeId, record.getScriptId()));
            }
        }
        return result;
    }

    /**
     * 记录约会数据
     *
     * @param player 玩家对象
     * @param cfg 约会配置
     */
    @Override
    public void recordCurrentDating(Player player, BaseDating cfg, CurrentDatingBean record) {

        Map reward = getReward(player, cfg, record);
        // 记录可完成次数
        if (cfg.getCompletionTimes() > 0) {
            int completionTime =
                    (int) record.getDialogueCount().computeIfAbsent(cfg.getId(), k -> 0);
            record.getDialogueCount().put(cfg.getId(), completionTime + 1);
        }
        // 记录奖励
        if (reward != null && reward.size() > 0) {
            ItemPackageHelper.unpack(reward, null, record.getReward());
        }
        // 记录积分
        record.setScore(record.getScore() + cfg.getScore());
    }

    /**
     * 获取剧本对话奖励
     */
    @Override
    public Map getReward(Player player, BaseDating cfg, CurrentDatingBean record) {
        DatingManager dating = player.getDatingManager();
        boolean repeat = dating.getCompleteEndings().contains(cfg.getId());
        return repeat ? cfg.getRepeatReward() : cfg.getReward();
    }

    /**
     * 结算
     */
    @Override
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record, Logger LOGGER) {
        DatingManager dm = player.getDatingManager();
        // CurrentDatingBean record = getByPlayerIdDatingTypeRoleId(datingType, roleCid, player);

        Map<Integer, Integer> rewardMap = record.getReward();
        // 获取看板娘
        // 因为特殊约会的存在,这里看板娘可能为空,需要对后续的操作做判定处理
        
        Role role = player.getRoleManager().getRole(roleCid);
        
        // 计算好感度
        int favor = calcFavor(record, rewardMap, role);
        // 计算心情值
        calcMood(rewardMap, role);
        calcState(datingType, rewardMap, player, role);
        // 记录对话次数
        record.getDialogueCount().forEach((k, v) -> {
            int before = dm.getDialogueCount().getOrDefault(k, 0);
            dm.getDialogueCount().put(k, before + v);
        });

        BaseDating cfg = DatingService.getInstance().getLogicHandler(record.getDatingType())
                .getDatingCfgBeanByType(selectedNodeCid, record.getScriptId());

        // 完成剧本记录
        if (!dm.getCompleteDatings().contains(cfg.getScriptId())) {
            dm.getCompleteDatings().add(cfg.getScriptId());
        }
        // 结局
        if (!dm.getCompleteEndings().contains(cfg.getId())) {
            dm.getCompleteEndings().add(cfg.getId());
        }

        // 各子类结束需要记录的数据都写到这里面
        DatingService.getInstance().getLogicHandler(datingType).endDatingRecord(player, cfg,
                roleCid, record);

        BagManager bm = player.getBagManager();
        List<Item> itemInfos = new ArrayList<Item>();
        if (rewardMap != null && !rewardMap.isEmpty()) {
            try {
                player.getRoleManager().setPreRoleId(roleCid);
                itemInfos = bm.addItems(rewardMap, true, EReason.DATING_SETTLE);
                player.getRoleManager().setPreRoleId(0);
            } catch (Exception ex) {
                player.getRoleManager().setPreRoleId(0);
                LOGGER.error(ExceptionEx.e2s(ex));
            }
        }
        DatingService.getInstance().getLogicHandler(datingType).sendSettlementMsg(player, record,
                favor, cfg, itemInfos);

        // 约会事件全局触发
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.FINISH_DATING_COUNT);
        in.put(EventConditionKey.ROLE_CID, player.getRoleManager().getNowRoleCid());
        in.put(EventConditionKey.DATING_TYPE, datingType);
        in.put(EventConditionKey.SCRIPT_ID, record.getScriptId());
        player._fireEvent(in, EEventType.ROLE_CHANGE.value());

        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createDatingLog(player, roleCid,
                    selectedNodeCid, 1, EReason.DATING_SETTLE.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }

    }

    /**
     * 发送结算消息
     */
    @Override
    public void sendSettlementMsg(Player player, CurrentDatingBean record, int favor,
            BaseDating cfg, List<Item> itemInfos) {
        // 理论上,业务逻辑模块应该与通讯模块完全解耦,通过中间层进行网络消息转发。此处耦合相对不高,后续优化
        MessageUtils.send(player, DatingMsgBuilder.getDatingSettlementMsg(record.getScore(), favor,
                cfg.getScriptId(), ShareMsgBuilder.createReward(itemInfos), false));
    }

    /**
     * 计算心情值
     */
    @Override
    public int calcMood(Map rewardMap, Role role) {
        if (role == null)
            return 0;
        int mood = (int) rewardMap.computeIfAbsent(ItemConstantId.ROLE_MOOD, k -> 0);
        // 获取看板娘增益
        if (mood != 0) {
            mood *= (100 + role.getEffectByType(RoleConstant.EFFECT_TYPE_4)) / 100;
        }
        rewardMap.put(ItemConstantId.ROLE_MOOD, mood);
        return mood;
    }


    /** 计算无聊的加成值 **/
    @Override
    public void calcState(int datingType, Map rewardMap, Player player, Role role) {
        if (role == null)
            return;
        if (datingType == DatingTypeConstant.DATING_TYPE_DAILY)
            player.getRoleManager().addPercentByRoleState(rewardMap, role, RoleConstant.BORING,
                    RoleConstant.DAILY_DATING);
    }

    /**
     * 计算好感度
     * 
     * @param record 当前约会记录
     * @param rewardMap 约会奖励
     * @param role 精灵
     */
    @Override
    public int calcFavor(CurrentDatingBean record, Map rewardMap, Role role) {
        if (role == null)
            return 0;
        // 1.判断本次约会是否奖励好感度
        int favor = (int) rewardMap.computeIfAbsent(ItemConstantId.ROLE_FAVOR, k -> 0);
        if (favor == 0)
            return 0;

        // 2.计算约会得分对好感度的加成
        int finalFavor = calcFavor(favor, record.getScore());
        // 3. 计算房间对好感度加成
        // 获取看板娘
        finalFavor *= (100 + role.getEffectByType(RoleConstant.EFFECT_TYPE_5)) / 100;
        rewardMap.put(ItemConstantId.ROLE_FAVOR, finalFavor);
        return finalFavor;
    }

    /**
     * 计算约会得分对好感度的加成
     * 
     * @param favor 好感度
     * @param score 约会得分
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public int calcFavor(int favor, int score) {
        Map<Integer, Integer> ruleMap = (Map<Integer, Integer>) GameDataManager
                .getDiscreteDataCfgBean(DiscreteDataID.DATING).getData()
                .get(DiscreteDataKey.DATING_REWARD_FAVOR_RULE);
        int percent = ruleMap.keySet().stream().filter(k -> score >= k || (score < 0 && k == 0))
                .max(Integer::compareTo).map(k -> ruleMap.get(k)).get();
        return favor * percent / 100;
    }

    /**
     * 获取初始节点
     */
    @Override
    public int getStartNodeId(DatingRuleCfgBean cfg) {
        return cfg.getStartNodeId();
    }

    /**
     * 获取分支节点
     */
    @Override
    public Map<Integer, List<Integer>> getScriptBranchNode(Player player, int scriptId,
            DatingScriptDTO dto) {
        // 配置类关联索引优化
        List<BaseDating> cfgList = DatingService.getInstance().getLogicHandler(dto.getScriptType())
                .getDatingCfgBeansByTypeAndScript(scriptId);// DatingCfgCache.me().getByScriptId(scriptId);
        Map<Integer, List<Integer>> result = Maps.newHashMap();
        for (BaseDating datingCfg : cfgList) {
            int[] jumpArr = datingCfg.getJump();
            if (jumpArr != null && jumpArr.length > 1) {
                // 符合条件的待选节点
                List<BaseDating> waitingSelectNodes = Lists.newArrayList();
                for (int jumpCid : jumpArr) {
                    BaseDating subNode =
                            DatingService.getInstance().getLogicHandler(dto.getScriptType())
                                    .getDatingCfgBeanByType(jumpCid, scriptId);
                    if (checkItem(player, subNode, dto)) {
                        waitingSelectNodes.add(subNode);
                    }
                }
                List<Integer> idList = new ArrayList<Integer>(DatingConstant.MAX_DIALOGUE_NUM);
                int maxWeight = 0;
                for (BaseDating dating : waitingSelectNodes) {
                    // 必定出现
                    if (dating.getWeighting() == 0)
                        idList.add(dating.getId());
                    // 这里必须保证权重大于零,否则无法正常随机
                    else if (dating.getWeighting() < 0)
                        MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                                "config [weighting] of optional dating [",
                                String.valueOf(dating.getId()), "] in ", dating.toString(),
                                " is less than 0");
                    else
                        maxWeight += dating.getWeighting();
                    if (idList.size() >= DatingConstant.MAX_DIALOGUE_NUM)
                        break;
                }
                // 对随机选项的整合
                int count = Math.min(DatingConstant.MAX_DIALOGUE_NUM - idList.size(),
                        waitingSelectNodes.size() - idList.size());
                while (count-- > 0) {
                    int tempWeight = 0;
                    int range = (int) (Math.random() * maxWeight);
                    for (BaseDating dating : waitingSelectNodes) {
                        tempWeight += dating.getWeighting();
                        // 跳过已包含id
                        if (idList.contains(dating.getId()) || tempWeight <= range)
                            continue;
                        idList.add(dating.getId());
                        // 从原来权重中删除
                        maxWeight -= dating.getWeighting();
                        break;
                    }
                }
                Collections.shuffle(idList);
                result.put(datingCfg.getId(), idList);
            }
        }
        return result;
    }

    /**
     * 检查节点 检查节点是否达成条件，未达成条件则不给玩家
     *
     * @param player 玩家
     * @param datingCfg 配置
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean checkItem(Player player, BaseDating datingCfg, DatingScriptDTO dto) {
        AtomicBoolean result = new AtomicBoolean(true);
        datingCfg.getCondition().forEach((k, v) -> {
            String condition = (String) k;
            // 约会剧本、触发条件判定规则复杂且未封装不利于扩展,规划后可以使用策略模式改写
            switch (condition) {
                case DatingConstant.DATING_CONDITION_BUILD_LEVEL:
                    int buildCid = dto.getBuildingId();
                    //
                    Object building = null;// Building building =
                                           // BuildingCache.me().getByPlayerIdCid(player.getId(),buildCid);
                    if (building == null) {
                        result.set(false);
                        return;
                    }
                    break;
                case DatingConstant.DATING_CONDITION_MOOD:
                    // 需要看板娘系统支持
                    if (player.getRoleManager().getRole(dto.getRoleId()).getMood() < (int) v) {
                        result.set(false);
                        return;
                    }
                    break;
                case DatingConstant.DATING_CONDITION_TIME:
                    if ((int) v != player.getNewBuildingManager().getDayType()) {
                        result.set(false);
                        return;
                    }
                    break;
                case DatingConstant.DATING_CONDITION_OWN_ROLE:
                    List idList = (List) v;
                    for (Object cid : idList) {
                        // 需要看板娘系统支持
                        if (!player.getRoleManager().getRoles().containsKey((Integer) cid)) {
                            result.set(false);
                            return;
                        }
                    }
                    break;
            }
        });
        return result.get();
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DATING_HANDLER_SCRIPT.Value();
    }

}
