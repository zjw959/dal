//package logic.dating.handler.logic;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import logic.character.bean.Player;
//import logic.constant.DatingConstant;
//import logic.constant.DatingTypeConstant;
//import logic.constant.GameErrorCode;
//import logic.dating.DatingManager;
//import logic.dating.DatingService;
//import logic.dating.bean.CurrentDatingBean;
//import logic.dating.bean.dto.DatingScriptDTO;
//import logic.role.bean.Role;
//import logic.support.MessageUtils;
//
//import org.game.protobuf.s2c.S2CRoleMsg.RoleInfo;
//import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
//
//import data.GameDataManager;
//import data.bean.BaseDating;
//import data.bean.DatingRuleCfgBean;
//
///**
// * 主线约会逻辑处理器
// * 
// * @author Alan
// *
// */
//public class MainDatingHandler extends DatingHandler {
//
//    @Override
//    public int getHandlerIdentification() {
//        return DatingTypeConstant.DATING_TYPE_MAIN;
//    }
//
//    @Override
//    public void checkDating(Player player, DatingScriptDTO dto) {
//
//    }
//
//    @Override
//    public void handleDatingResource(Player player, DatingScriptDTO dto) {
//
//    }
//
//    @Override
//    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto) {
//        // 需要看板娘模块支持
//        Role role = player.getRoleManager().getRole(dto.getRoleId());
//        return getMainScriptId(player, role);
//    }
//
//    @Override
//    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto) {
//        return Arrays.asList(dto.getRoleId());
//    }
//
//    /**
//     * 获取主线剧本id
//     */
//    private DatingRuleCfgBean getMainScriptId(Player player, Role role) {
//        // 需要看板娘模块支持
//        if (role.getFavor() != 0 && !role.isCriticalPoint()) {
//            return null;
//        }
//        // 需要看板娘模块支持
//        List<DatingRuleCfgBean> ruleCfgList = getDatingRuleByRoleIdDatingType(role.getCid());
//        DatingManager dating = player.getDatingManager();
//        DatingRuleCfgBean script = null;
//        for (DatingRuleCfgBean datingRuleCfg : ruleCfgList) {
//            // 主线剧情只能完成1次，后续通过回顾进行追溯
//            if (dating.getCompleteDatings().contains(datingRuleCfg.getId())) {
//                continue;
//            }
//            // 需要看板娘模块支持
//            if (DatingService.getInstance().getDatingRuleEnterCondtionByType(datingRuleCfg,
//                    DatingConstant.ENTER_CDTION_KEY_FAVOR) == role.getFavor()) {
//                return datingRuleCfg;
//            }
//
//        }
//        return null;
//    }
//
//    @Override
//    protected void endDatingRecord(Player player, BaseDating cfg, int roleCid,
//            CurrentDatingBean record) {
//        super.endDatingRecord(player, cfg, roleCid, record);
//        // 需要看板娘模块支持
//         Role role = player.getRoleManager().getRole(roleCid);//RoleCache.me().getByCidPlayerId(roleCid,player.getId());
////         取消临界状态
//         role.setCriticalPoint(false);
//         // 刷新看板娘信息
//         RoleInfo.Builder builder = RoleInfo.newBuilder();
//         builder.setCt(ChangeType.UPDATE);
//         builder.setId(Long.toString(role.getCid()));
//         builder.setCid(role.getCid());
//         builder.setFavorCriticalPoint(role.isCriticalPoint());
//         MessageUtils.send(player, builder);
//    }
//
//    @Override
//    public boolean isFail(Player player, int roleCid, boolean isLastNode) {
//        DatingManager dm = player.getDatingManager();
//        // 获取当前同类约会(唯一)
//        CurrentDatingBean record =
//                getByPlayerIdDatingTypeRoleId(getHandlerIdentification(), roleCid, player);
//        if (isLastNode) {
//            DatingRuleCfgBean cfg = GameDataManager.getDatingRuleCfgBean(record.getScriptId());
//            for (Object o : cfg.getFailCondition().entrySet()) {
//                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) o;
//                // 失败条件判定
//                switch (entry.getKey()) {
//                    case DatingConstant.FAIL_CDTION_SCORE: {
//                        // 未达到配置分数即失败
//                        if (record.getScore() < entry.getValue()) {
//                            return true;
//                        }
//                        break;
//                    }
//                }
//            }
//        } else {
//            if (record.getScore() <= 0) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {
//        // 对玩家接口逻辑的判定拦截
//        if(ruleCfg == null)
//            MessageUtils.throwCondtionError(GameErrorCode.MAIN_DATING_IS_NULL);
//        // ToolError.isAndTrue(GameErrorCode.MAIN_DATING_IS_NULL, scriptId == 0);
//    }
//
//    @Override
//    protected void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record) {
//
//    }
//
//    @Override
//    protected BaseDating getDatingCfgBeanByType(int datingCid,int datingRuleId) {
//        // 分表获取
//        return GameDataManager.getDatingCfgBean(datingCid);
//    }
//
//    @Override
//    protected List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
//        // 分表获取
//        List<BaseDating> allCfgList = GameDataManager.getDatingCfgBeans()
//                .stream().filter(cfg -> cfg.getScriptId() == scriptCid)
//                .collect(Collectors.toList());
//        return allCfgList;
//    }
//
//}
