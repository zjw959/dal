package logic.dating.req;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.game.protobuf.c2s.C2SDatingMsg;
import org.game.protobuf.c2s.C2SDatingMsg.ScriptType;
import org.game.protobuf.s2c.S2CDatingMsg;
import org.game.protobuf.s2c.S2CDatingMsg.CityDatingInfo;
import org.game.protobuf.s2c.S2CDatingMsg.NotFinishDating;
import org.game.protobuf.s2c.S2CRoleMsg.RoleInfo;
import cn.hutool.core.util.RandomUtil;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DatingRuleCfgBean;
import logic.dating.ReqDatingOrder;
import logic.robot.entity.RobotPlayer;

/***
 * 
 * @author lihongji 获取剧本
 *
 */
@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DATING,
        order = ReqDatingOrder.REQ_GETSCRIPTTYPE)
public class ReqGetScriptMsgEvent extends AbstractEvent {

    public ReqGetScriptMsgEvent(RobotThread robot) {
        super(robot, S2CDatingMsg.GetScriptMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        if (robot.getPlayer().getDatingRuleCid() != 0) {
            robotSkipRun();
        } else {
            RobotPlayer player = robot.getPlayer();
            C2SDatingMsg.GetScriptMsg.Builder builder = choiseDating(player);
            if (builder == null) {
                robotSkipRun();
            } else {
                SMessage msg = new SMessage(C2SDatingMsg.GetScriptMsg.MsgID.eMsgID_VALUE,
                        builder.build().toByteArray(), resOrder);
                sendMsg(msg);
            }
        }
    }


    /** 获取未完成的约会 **/
    public NotFinishDating getNotFinish(RobotPlayer player) {
        return player.getNotFinishDating().get(0);
    }



    public C2SDatingMsg.GetScriptMsg.Builder choiseDating(RobotPlayer player) {
        C2SDatingMsg.GetScriptMsg.Builder builder = C2SDatingMsg.GetScriptMsg.newBuilder();

        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "寻找剧本--------");

        DatingRuleCfgBean bean = null;
        // 未完成的约会
        if (player.getNotFinishDating() != null && player.getNotFinishDating().size() > 0) {
            NotFinishDating dating = player.getNotFinishDating().get(0);
            // 剧本类型
            // builder.setScriptType(getScriptTypeByType(dating.getDatingType()));
            // bean = GameDataManager.getDatingRuleCfgBean(dating.getDatingRuleCid());
            // builder.setRoleId(bean.getRoleId());
            // builder.setBuildId(bean.getBuildingId());
            // builder.setScriptId(bean.getId());
            // builder.setCityDatingId(dating.getDatingType() + "");
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取未完成的剧本");

            player.setDatingNodeId(dating.getCurrentNodeId());
            player.setDatingRuleCid(dating.getDatingRuleCid());
            return null;
        }

        CityDatingInfo cityInfo = getCityDatingBean(6, player);

        // 出游约会
        if (cityInfo != null) {
            DatingRuleCfgBean datingRulebean =
                    GameDataManager.getDatingRuleCfgBean(cityInfo.getDatingRuleCid());
            builder.setScriptType(getScriptTypeByType(datingRulebean.getType()));
            builder.setRoleId(datingRulebean.getRoleId());
            builder.setBuildId(getDatingRuleEnterCondtionByType(datingRulebean, "buildingCid"));
            builder.setScriptId(datingRulebean.getId());
            builder.setCityDatingId(cityInfo.getCityDatingId() + "");
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取出游约会");
            player.setDatingId(Long.parseLong(cityInfo.getCityDatingId()));
            return builder;
        }

        cityInfo = getCityDatingBean(3, player);
        // 预定约会
        if (cityInfo != null && cityInfo.getState() == 3) {
            DatingRuleCfgBean datingRulebean =
                    GameDataManager.getDatingRuleCfgBean(cityInfo.getDatingRuleCid());
            builder.setScriptType(getScriptTypeByType(datingRulebean.getType()));
            builder.setRoleId(datingRulebean.getRoleId());
            builder.setBuildId(getDatingRuleEnterCondtionByType(datingRulebean, "buildingCid"));
            builder.setScriptId(datingRulebean.getId());
            builder.setCityDatingId(cityInfo.getCityDatingId() + "");
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取预定约会");
            player.setDatingId(Long.parseLong(cityInfo.getCityDatingId()));
            return builder;
        }
        // 手机约会
        if (cityInfo != null && cityInfo.getState() == 1) {
            DatingRuleCfgBean datingRulebean =
                    GameDataManager.getDatingRuleCfgBean(cityInfo.getDatingRuleCid());
            // 剧本类型
            builder.setScriptType(getScriptTypeByType(datingRulebean.getType()));
            builder.setRoleId(datingRulebean.getRoleId());
            builder.setBuildId(getDatingRuleEnterCondtionByType(datingRulebean, "buildingCid"));
            builder.setScriptId(datingRulebean.getId());
            builder.setCityDatingId(0 + "");
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取手机约会剧本");
            return builder;
        }


        if (player.getItemCount(500011) > 0 && player.getItemCount(500024) > 20) {
            // 日常约会
            bean = getDailyDating(player);
            if (bean == null)
                return null;
            // 剧本类型
            builder.setScriptType(getScriptTypeByType(bean.getType()));
            builder.setRoleId(bean.getRoleId());
            builder.setBuildId(getDatingRuleEnterCondtionByType(bean, "buildingCid"));
            builder.setScriptId(bean.getId());
            builder.setCityDatingId(0 + "");
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取日常剧本");
            return builder;
        }

        return null;

    }



    /** 根据类型获取 **/
    public ScriptType getScriptTypeByType(int type) {
        switch (type) {
            case 1:
                return ScriptType.MAIN_SCRIPT;
            case 2:
                return ScriptType.DAY_SCRIPT;
            case 3:
                return ScriptType.RESERVE_SCRIPT;
            case 4:
                return ScriptType.TRIGGER_SCRIPT;
            case 5:
                return ScriptType.WORK_SCRIPT;
            case 6:
                return ScriptType.OUT_SCRIPT;
            case 7:
                return ScriptType.DUNGEON_SCRIPT;
            case 10:
                return ScriptType.PHONE_SCRIPT;
            default:
                break;
        }
        return null;
    }


    /** 获取日常列表 **/
    public DatingRuleCfgBean getDailyDating(RobotPlayer player) {
        if (player.getRoles() == null || player.getRoles().size() <= 0)
            return null;
        List<RoleInfo> roles = getAbleList(player);
        if (roles == null || roles.size() <= 0)
            return null;
        RoleInfo role = player.getRoles().get(RandomUtil.randomInt(roles.size()));
        return getDatingRuleId(role, 0, player, 2);
    }


    /** 获取匹配的约会剧本 */
    protected List<DatingRuleCfgBean> getDatingRuleByRoleIdDatingType(int roleCid, int datingType) {
        List<DatingRuleCfgBean> allCfgList = GameDataManager.getDatingRuleCfgBeans().stream()
                .filter(cfg -> cfg.getRoleId() == roleCid && cfg.getType() == datingType)
                .collect(Collectors.toList());
        return allCfgList;
    }


    /** 获取可以用的精灵 **/
    protected List<RoleInfo> getAbleList(RobotPlayer player) {
        return player.getRoles().stream().filter(role -> role.getFavor() > 0)
                .collect(Collectors.toList());
    }


    /**
     * 根据区域id获取剧本id
     * 
     * @param buildingCid 区域id dayType 白天还是黑夜
     * @return
     */
    public DatingRuleCfgBean getDatingRuleId(RoleInfo role, int buildingCid, RobotPlayer player,
            int datingType) {
        // 需要看板娘模块支持
        List<DatingRuleCfgBean> allCfgList =
                getDatingRuleByRoleIdDatingType(role.getCid(), datingType);
        List<DatingRuleCfgBean> tempList = new ArrayList<DatingRuleCfgBean>();
        AtomicInteger maxWeight = new AtomicInteger();

        allCfgList.forEach(ruleCfg -> {
            // 条件1：建筑id
            Integer condition = getDatingRuleEnterCondtionByType(ruleCfg, "buildingCid");
            // if (condition != 0 && condition != buildingCid)
            // return;
            // 条件2：好感度
            condition = getDatingRuleEnterCondtionByType(ruleCfg, "favor");
            // 获取看板娘好感度
            if (condition > role.getFavor())
                return;

            // 判断当前是白天还是黑夜满足条件
//            Integer time = getDatingRuleEnterCondtionByType(ruleCfg, "time");
//            if (time != player.getDayType())
//                return;

            // 是否通过当前关卡
            // Integer pass = getDatingRuleEnterCondtionByType(ruleCfg, "pass");
            // if (pass <= 0)
            // return;
            // if (!player.getDungeons().containsKey(pass) ||
            // !player.getDungeons().get(pass).getWin())
            // return;

            maxWeight.addAndGet(getDatingRuleEnterCondtionByType(ruleCfg, "weight"));
            tempList.add(ruleCfg);
        });
        int max = maxWeight.get();
        if (max > 0) {
            int randomWeight = RandomUtil.randomInt(max);
            int weight = 0;
            for (DatingRuleCfgBean ruleCfg : tempList) {
                weight += getDatingRuleEnterCondtionByType(ruleCfg, "weight");
                if (randomWeight <= weight)
                    return ruleCfg;
            }
        }
        return null;
    }


    /** 旧代码遗留,仅支持值为整型的类型 */
    public int getDatingRuleEnterCondtionByType(DatingRuleCfgBean ruleCfg, String type) {
        if (ruleCfg.getEnterCondition() == null)
            return 0;
        Integer typeValue = (Integer) ruleCfg.getEnterCondition().get(type);
        if (typeValue != null) {
            return typeValue;
        }
        return 0;
    }


    /** 根据类型获取 **/
    private CityDatingInfo getCityDatingBean(int datingType, RobotPlayer player) {
        if (player.getCityDatingInfoList() == null || player.getCityDatingInfoList().size() <= 0)
            return null;
        for (CityDatingInfo cityInfo : player.getCityDatingInfoList()) {
            DatingRuleCfgBean bean =
                    GameDataManager.getDatingRuleCfgBean(cityInfo.getDatingRuleCid());
            if (bean.getType() == datingType)
                return cityInfo;
        }
        return null;
    }



}
