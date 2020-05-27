package logic.city.req;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import cn.hutool.core.util.RandomUtil;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.HandworkbaseCfgBean;
import logic.city.ReqCityOrder;
import logic.robot.entity.RobotPlayer;

/***
 * 请求手工制作
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_DOHANDWORK)
public class ReqDoHandWorkEvent extends AbstractEvent {

    public ReqDoHandWorkEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespDoHandWork.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqDoHandWork.Builder builder =
                C2SNewBuildingMsg.ReqDoHandWork.newBuilder();
        if (robot.getPlayer().getHandWorkInfo() != null
                && robot.getPlayer().getHandWorkInfo().getEndTime() != 0
                && robot.getPlayer().getHandWorkInfo().getManualId() != 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "手工制作正在进行中");
            robotSkipRun();
        } else {
            @SuppressWarnings("unchecked")
            List<HandworkbaseCfgBean> list = getAbleCookList(robot.getPlayer());
            if (list == null) {
                robotSkipRun();
            } else {
                int randomManualId = list.get(RandomUtil.randomInt(list.size())).getId();
                builder.setManualId(randomManualId);
                SMessage msg = new SMessage(C2SNewBuildingMsg.ReqDoHandWork.MsgID.eMsgID_VALUE,
                        builder.build().toByteArray(), resOrder);
                sendMsg(msg);
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "请求手工制作");
            }

        }
    }

    /** 获取所有可cook的集合 **/
    public List getAbleCookList(RobotPlayer player) {
        return GameDataManager.getHandworkbaseCfgBeans().stream()
                .filter(bean -> checkAbility(player, bean))
                .filter(bean -> checkMaterials(player, bean)).collect(Collectors.toList());
    }

    /** 检测下能力 **/
    @SuppressWarnings("unchecked")
    public boolean checkAbility(RobotPlayer player, HandworkbaseCfgBean bean) {
        if (bean.getAbility() == null || bean.getAbility().size() <= 0)
            return true;
        Map<Integer, Integer> map = bean.getAbility();
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            if (player.getItemCount(entry.getKey()) < entry.getValue())
                return false;
        }
        return true;
    }

    /** 检测下能力 **/
    @SuppressWarnings("unchecked")
    public boolean checkMaterials(RobotPlayer player, HandworkbaseCfgBean bean) {
        if (bean.getAbility() == null || bean.getMaterials().size() <= 0)
            return true;
        Map<Integer, Integer> map = bean.getMaterials();
        for (Entry<Integer, Integer> entry : map.entrySet()) {
            if (player.getItemCount(entry.getKey()) < entry.getValue())
                return false;
        }
        return true;
    }

}
