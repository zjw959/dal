package logic.endless.req;

import logic.endless.EndlessCloisterOrder;

import org.game.protobuf.c2s.C2SEndlessCloisterMsg;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.EndlessCloisterLevelCfgBean;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ENDLESS,
        order = EndlessCloisterOrder.ENDLESS_PASS)
public class ReqPassEndlessCloister extends AbstractEvent {

    public ReqPassEndlessCloister(RobotThread robot) {
        super(robot, S2CEndlessCloisterMsg.RspPassStageEndless.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // 开启状态
        if (robot.getPlayer().getEndlessInfo().getStep() == 1) {
            EndlessCloisterLevelCfgBean cfg =
                    GameDataManager.getEndlessCloisterLevelCfgBean(robot.getPlayer()
                            .getEndlessNowStage());
            if (cfg == null) {
                Log4jManager.getInstance().warn(
                        "无尽副本配置不存在:" + robot.getPlayer().getEndlessNowStage());
                super.robotSkipRun();
                return;
            }
            int costTime = (int) (Math.random() * cfg.getTime()) + 1;
            C2SEndlessCloisterMsg.ReqPassStageEndless.Builder builder =
                    C2SEndlessCloisterMsg.ReqPassStageEndless.newBuilder();
            builder.setLevelCid(cfg.getId());
            builder.setCostTime(costTime);
            SMessage msg =
                    new SMessage(C2SEndlessCloisterMsg.ReqPassStageEndless.MsgID.eMsgID_VALUE,
                            builder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(), "无尽副本当前未开启");
            super.robotSkipRun();
            return;
        }
    }
}
