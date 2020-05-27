package javascript.logic.dating.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CDatingMsg.DatingSettlementMsg;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import data.bean.DungeonLevelCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingTypeConstant;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.DungeonDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.dungeon.SettledLogicHandler;
import logic.dating.handler.script.IDungeonDatingHandlerScript;
import logic.dungeon.scene.DatingDungeonScene;
import logic.dungeon.scene.DungeonScene;
import logic.item.bean.Item;
import logic.msgBuilder.DatingMsgBuilder;
import logic.msgBuilder.ShareMsgBuilder;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;

public class DungeonDatingHandlerScript implements IDungeonDatingHandlerScript {

    @Override
    public int getHandlerIdentification() {
        return DatingTypeConstant.DATING_TYPE_DUNGEON;
    }

    @Override
    public void handleDatingResource(Player player, DatingScriptDTO dto) {

    }

    @Override
    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto) {
        // 获取当前关卡对象
        DatingDungeonScene scene = getDatingDungeonScene(player);
        DungeonLevelCfgBean levelCfg =
                GameDataManager.getDungeonLevelCfgBean(scene.getSceneCfgId());
        if (levelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(scene.getSceneCfgId()), "] not exists");
        // 如果已经完成过
        if (scene.getDatingBeans().containsKey(dto.getScriptId()))
            MessageUtils.throwCondtionError(GameErrorCode.COMPLETED_DATING);
        DatingRuleCfgBean datingRule = null;
        // 从dungeonlevel获取datingrule
        // 获取下阶段剧本
        for (int datingRuleId : levelCfg.getDatingID()) {
            if (dto.getScriptId() != datingRuleId)
                continue;
            datingRule = GameDataManager.getDatingRuleCfgBean(datingRuleId);
            break;
        }
        return datingRule;
    }

    @Override
    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto) {
        // 配置的精灵在结算时需要做相应处理
        DatingRuleCfgBean datingRule = GameDataManager.getDatingRuleCfgBean(dto.getScriptId());
        return datingRule.getDungeonRoleId() > 0 ? Arrays.asList(datingRule.getDungeonRoleId())
                : null;
    }

    @Override
    public void checkDating(Player player, DatingScriptDTO dto) {}

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {
        // 对玩家接口逻辑的判定拦截
        if (ruleCfg == null)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR);
    }

    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
        DatingManager dm = player.getDatingManager();
        DungeonDatingBean record = new DungeonDatingBean(dm.newDatingId(),
                // 以类型为标识的处理器
                getHandlerIdentification(),
                // 起始得分60
                60, 0, startNodeId, branchNodes, cfg.getId(), roleIds);
        // 将当前关卡约会对象设置到当前约会关卡
        DatingDungeonScene scene = getDatingDungeonScene(player);
        scene.setCurrentDatingBean(record);
        handleAfterCreatedScriptRecord(player, record);
    }

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long dataingId) {
        // 从当前关卡获取约会对象
        DatingDungeonScene scene = getDatingDungeonScene(player);
        return scene.getCurrentDatingBean();
    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record) {

    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId) {
        // 分表获取
        return GameDataManager.getMainDialogueCfgBean(datingCid);
    }

    @Override
    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
        // 分表获取
        List<BaseDating> allCfgList = GameDataManager.getMainDialogueCfgBeans().stream()
                .filter(cfg -> cfg.getScriptId() == scriptCid).collect(Collectors.toList());
        return allCfgList;
    }

    @Override
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record) {
        DatingDungeonScene datingScene = getDatingDungeonScene(player);
        // 结束本阶段阶段
        datingScene.completeCurrentDating();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void sendSettlementMsg(Player player, CurrentDatingBean record, int favor,
            BaseDating cfg, List<Item> itemInfos) {
        // 对整体进度的判定
        DatingDungeonScene datingScene = getDatingDungeonScene(player);
        DungeonLevelCfgBean levelCfg =
                GameDataManager.getDungeonLevelCfgBean(datingScene.getSceneCfgId());
        if (levelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(datingScene.getSceneCfgId()), "] not exists");
        // 判断当前约会关卡是否完成
        int lastDating = levelCfg.getDatingID()[levelCfg.getDatingID().length - 1];
        List<Boolean> hearts = null;
        // 这里暂时按顺序判断是否到最后阶段,扩展模式确定后再做修改
        if (datingScene.getDatingBeans().containsKey(lastDating)) {
            // 累计约会结果
            int totalScore = 0;
            Map<Integer, Integer> datingItems = new HashMap<Integer, Integer>();
            for (DungeonDatingBean datingBean : datingScene.getDatingBeans().values()) {
                totalScore += datingBean.getScore();
                datingBean.outputDatingItems(datingItems);
            }
            // 设置最终结果
            datingScene.setTotalScore(totalScore);
            datingScene.setTotalDatingItems(datingItems);
            DatingRuleCfgBean ruleCfg = GameDataManager.getDatingRuleCfgBean(lastDating);
            // 使用规范的复合条件解析SettledLogicHandler
            List conditions = ruleCfg.getDungeonDateHeart();
            hearts = new ArrayList<Boolean>(conditions.size());
            for (int i = 0; i < conditions.size(); i++) {
                // 判定结果
                boolean result = SettledLogicHandler.getInstance()
                        .isConditionComplete((Map) conditions.get(i), datingScene);
                hearts.add(result);
                // 心数记录
                if (result)
                    datingScene.getGoals().add((i + 1));
            }
        }
        DatingSettlementMsg.Builder builder =
                DatingMsgBuilder.getDatingSettlementMsg(record.getScore(), favor, cfg.getScriptId(),
                        ShareMsgBuilder.createReward(itemInfos), false);
        if (hearts != null)
            // 添加目标达成数即心级消息
            builder.addAllStarList(hearts);
        // 理论上,业务逻辑模块应该与通讯模块完全解耦,通过中间层进行网络消息转发。此处耦合相对不高,后续优化
        MessageUtils.send(player, builder);
    }

    @Override
    public DatingDungeonScene getDatingDungeonScene(Player player) {
        DungeonScene scene = player.getDungeonManager().getCurrentScene();
        if (scene == null || !(scene instanceof DatingDungeonScene))
            MessageUtils.throwCondtionError(GameErrorCode.CURRENT_DUNGEON_NOT_MATCH);
        return (DatingDungeonScene) scene;
    }

    @Override
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record, Logger LOGGER) {
        DatingRuleCfgBean ruleCfg = GameDataManager.getDatingRuleCfgBean(record.getScriptId());
        if (ruleCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "CurrentDatingBean [",
                    String.valueOf(record.getScriptId()), "] does not exist");
        // 这里需要对受益精灵做修正
        roleCid = ruleCfg.getDungeonRoleId();
        LogicScriptsUtils.getIDatingHandlerScript().settlement(player, selectedNodeCid, datingType,
                roleCid, record, LOGGER);
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DUNGEONG_HANDLER_SCRIPT.Value();
    }

}
