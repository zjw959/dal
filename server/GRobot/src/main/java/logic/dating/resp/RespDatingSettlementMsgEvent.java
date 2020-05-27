package logic.dating.resp;

import java.util.ArrayList;
import java.util.List;
import org.game.protobuf.s2c.S2CDatingMsg;
import org.game.protobuf.s2c.S2CDatingMsg.CityDatingInfo;
import org.game.protobuf.s2c.S2CDatingMsg.NotFinishDating;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DatingRuleCfgBean;
import logic.robot.entity.RobotPlayer;


/****
 * 结算剧本信息
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DATING,
        order = S2CDatingMsg.DatingSettlementMsg.MsgID.eMsgID_VALUE)
public class RespDatingSettlementMsgEvent extends AbstractEvent {

    public RespDatingSettlementMsgEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            //移除约会
            deleteDating(player);
            player.setDatingRuleCid(0);
            player.setDatingNodeId(0);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "结算剧本信息功");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "结算剧本信息失败");
        }
    }

    /** 删除约会 **/
    private void deleteDating(RobotPlayer player) {
        DatingRuleCfgBean bean = GameDataManager.getDatingRuleCfgBean(player.getDatingRuleCid());
        if (bean == null)
            return;
        removeNotFinishDating(bean, player);
        removeCityDatingInfo(bean, player);
    }


    /** 移除未完成的约会 **/
    public void removeNotFinishDating(DatingRuleCfgBean bean, RobotPlayer player) {
        if (player.getNotFinishDating() == null || player.getNotFinishDating().size() <= 0)
            return;
        List<NotFinishDating> removeList = new ArrayList<NotFinishDating>();
        for (NotFinishDating notFinishDating : player.getNotFinishDating()) {
            if (notFinishDating.getRoleCidList().contains(bean.getRoleId())
                    && bean.getId() == notFinishDating.getDatingRuleCid()) {

            }
            removeList.add(notFinishDating);
        }
        player.setNotFinishDating(removeList);
    }

    /** 移除城市约会 **/
    public void removeCityDatingInfo(DatingRuleCfgBean bean, RobotPlayer player) {
        if (player.getCityDatingInfoList() == null || player.getCityDatingInfoList().size() <= 0)
            return;
        List<CityDatingInfo> removeList = new ArrayList<CityDatingInfo>();
        for (CityDatingInfo cityDating : player.getCityDatingInfoList()) {
            DatingRuleCfgBean cityBean =
                    GameDataManager.getDatingRuleCfgBean(cityDating.getDatingRuleCid());
            if (bean.getType() != cityBean.getType())
                removeList.add(cityDating);
        }
        player.setCityDatingInfoList(removeList);
    }



}
