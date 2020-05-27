package logic.city.req;

import java.util.List;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import cn.hutool.core.util.RandomUtil;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.CookQteCfgBean;
import logic.city.ReqCityOrder;
import logic.robot.entity.RobotPlayer;

/***
 * 请求QTE完成上传积分
 * 
 * @author
 *
 */



@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_UPLOADQTEINTEGRAL)
public class ReqUploadQteIntegralEvent extends AbstractEvent {

    public ReqUploadQteIntegralEvent(RobotThread robot) {
        // super(robot, S2CNewBuildingMsg.RespUploadQteIntegral.MsgID.eMsgID_VALUE);
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqUploadQteIntegral.Builder builder =
                C2SNewBuildingMsg.ReqUploadQteIntegral.newBuilder();
        RobotPlayer player = robot.getPlayer();
        if (player.getFoodId() != 0 && player.getEndTime() != 0
                && player.getEndTime() > (System.currentTimeMillis() / 1000)) {
            builder.setFoodId(player.getFoodId());
            builder.setIntegral(100);

            List<CookQteCfgBean> beans = GameDataManager.getCookQteCfgBeans();
            CookQteCfgBean bean = beans.get(RandomUtil.randomInt(beans.size()));
            builder.setQteId(bean.getId());
            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqUploadQteIntegral.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg, true);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求QTE完成上传积分");
        } else {
            robotSkipRun();
        }


    }

}
