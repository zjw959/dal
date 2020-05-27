package logic.summon.resp;

import java.util.ArrayList;
import java.util.List;

import org.game.protobuf.s2c.S2CSummonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.SummonComposeCfgBean;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CSummonMsg.NoticeComposeFinish.MsgID.eMsgID_VALUE)
public class NoticeComposeFinishEvent extends AbstractEvent {

    public NoticeComposeFinishEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CSummonMsg.NoticeComposeFinish noticeComposeFinish = S2CSummonMsg.NoticeComposeFinish.parseFrom(data);
            List<Integer> cids = noticeComposeFinish.getCidList();
            List<Integer> types = new ArrayList<>();
            for(Integer cid : cids) {
                SummonComposeCfgBean summonComposeCfgBean = GameDataManager.getSummonComposeCfgBean(cid);
                int zPointType = summonComposeCfgBean.getZPointType();
                types.add(zPointType);
            }
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + ",质点 " + types.toArray(new Integer[]{}) + " 合成完成");
        }
    }

}
