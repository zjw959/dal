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
import data.bean.FoodbaseCfgBean;
import logic.city.ReqCityOrder;
import logic.robot.entity.RobotPlayer;

/***
 * 请求料理制作
 * 
 * @author
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_COOKFOODBASE)
public class ReqCookFoodbaseEvent extends AbstractEvent {

    public ReqCookFoodbaseEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespCookFoodbase.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqCookFoodbase.Builder builder =
                C2SNewBuildingMsg.ReqCookFoodbase.newBuilder();
        if (robot.getPlayer().getFoodId() != 0 && robot.getPlayer().getEndTime() != 0) {
            robotSkipRun();
        } else {
            List<FoodbaseCfgBean> list = getAbleCookList(robot.getPlayer());
            if (list == null) {
                robotSkipRun();
            } else {
                int randomCookId = list.get(RandomUtil.randomInt(list.size())).getId();
                builder.setFoodId(randomCookId);
                SMessage msg = new SMessage(C2SNewBuildingMsg.ReqCookFoodbase.MsgID.eMsgID_VALUE,
                        builder.build().toByteArray(), resOrder);
                sendMsg(msg);
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "请求料理制作");
            }

        }
    }

    /** 获取所有可cook的集合 **/
    public List getAbleCookList(RobotPlayer player) {
        return GameDataManager.getFoodbaseCfgBeans().stream()
                .filter(bean -> checkAbility(player, bean))
                .filter(bean -> checkMaterials(player, bean)).collect(Collectors.toList());
    }

    /** 检测下能力 **/
    @SuppressWarnings("unchecked")
    public boolean checkAbility(RobotPlayer player, FoodbaseCfgBean bean) {
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
    public boolean checkMaterials(RobotPlayer player, FoodbaseCfgBean bean) {
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
