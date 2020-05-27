package logic.login.resp;

import org.game.protobuf.s2c.S2CLoginMsg;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CLoginMsg.EnterSuc.MsgID.eMsgID_VALUE)
public class ResLoginEvent extends AbstractEvent {

    public ResLoginEvent(RobotThread robot) {
        super(robot);
        this.resOrder = S2CLoginMsg.EnterSuc.MsgID.eMsgID_VALUE;
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int succ = (int) obj[1];
        int len = data.length;
        if (len > 0) {
            S2CLoginMsg.EnterSuc enterSuc = S2CLoginMsg.EnterSuc.parseFrom(data);

            robot.init = false;

            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robotName:" + robot.getName() + "登陆状态:" + succ);
            if (succ != 0) {
                Log4jManager.getInstance()
                        .error("robotName:" + robot.getName() + "登陆失败 " + succ);
                robot.getChannel().close();
                return;
            } else {
                if (enterSuc.getQueue() != 0) {
                    Log4jManager.getInstance().debug(robot.getWindow(),
                            "robotName:" + robot.getName() + "登陆排队中,当前等待人数:" + enterSuc.getQueue());
                    return;
                }
            }

            int serverTime = enterSuc.getServerTime();
            PlayerInfo info = enterSuc.getPlayerinfo();
            RobotPlayer robotPlayer = robot.getPlayer();
            robotPlayer.setPlayerInfo(info);
            robotPlayer.setServerTime(serverTime);
            robotPlayer.setIsLogin(true);
            robot.getWindow().addLogined();
        }
    }

}
