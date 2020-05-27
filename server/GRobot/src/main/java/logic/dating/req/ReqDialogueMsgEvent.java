package logic.dating.req;

import java.util.ArrayList;
import java.util.List;
import org.game.protobuf.c2s.C2SDatingMsg;
import org.game.protobuf.s2c.S2CDatingMsg;
import cn.hutool.core.util.RandomUtil;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.dating.ReqDatingOrder;
import logic.robot.entity.RobotPlayer;

/***
 * 进入对话
 * 
 * @author
 *
 */


@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DATING,
        order = ReqDatingOrder.REQ_DIALOGUEMSG)
public class ReqDialogueMsgEvent extends AbstractEvent {

    public ReqDialogueMsgEvent(RobotThread robot) {
        super(robot, S2CDatingMsg.DialogueMsg.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        if (robot.getPlayer().getDatingRuleCid() != 0) {

            C2SDatingMsg.DialogueMsg.Builder builder = C2SDatingMsg.DialogueMsg.newBuilder();
            // BranchNode node =
            // robot.getPlayer().getBranchNodeList().get(robot.getPlayer().getBranchNodeList().size()-1);
            DatingRuleCfgBean bean =
                    GameDataManager.getDatingRuleCfgBean(robot.getPlayer().getDatingRuleCid());
            BaseDating baseDating = getDating(robot.getPlayer(), bean.getStartNodeId(),bean);

            int next = 0;
            if(baseDating.getJump()==null)
            {
                builder.setIsLastNode(true);
                builder.setBranchNodeId(0);
                builder.setSelectedNodeId(baseDating.getId());
            }
            else
            {
                builder.setIsLastNode(false);
                builder.setBranchNodeId(baseDating.getId());
                next=baseDating.getJump()[RandomUtil.randomInt(baseDating.getJump().length)];
                builder.setSelectedNodeId(next);
            }
            builder.setDatingType(bean.getType());
            builder.setRoleId(bean.getRoleId());
            builder.setDatingId(robot.getPlayer().getDatingId()+"");
            robot.getPlayer().setDatingNodeId(next);
            
//            builder.setBranchNodeId(baseDating.getId());
//            builder.setDatingType(bean.getType());
//            if (baseDating.getJump()==null)
//                builder.setIsLastNode(true);
//            else
//                builder.setIsLastNode(false);
//            builder.setRoleId(bean.getRoleId());
            SMessage msg = new SMessage(C2SDatingMsg.DialogueMsg.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "进入对话");
        } else {
            robotSkipRun();
        }
    }

    /** 获取当前节点的跳转 **/
    public BaseDating getDating(RobotPlayer player, int nodeId,DatingRuleCfgBean datingRuleBean) {
        int startNodeId = player.getDatingNodeId();
        if (startNodeId == 0)
            startNodeId = nodeId;
//        List<BaseDating> list = getDatingCfgBeansByTypeAndScript(player.getDatingRuleCid());
//        boolean flag = false;
//        for (BaseDating dating : list) {
//            if (dating.getId() == startNodeId) {
//                flag = true;
//            }
//            if (flag && (dating.get) {
//                return dating;
//            }
//        }
//        return null;
        int le=0;
        BaseDating baseDating=GameDataManager.getBaseDating(datingRuleBean.getCallTableName()).get(startNodeId);
        while(le<1000) {
            le++;
            if(baseDating.getJump()==null || baseDating.getJump().length>1)
                return baseDating;
            baseDating=GameDataManager.getBaseDating(datingRuleBean.getCallTableName()).get(baseDating.getJump()[0]);
        }
        return null;
    }


    protected List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
        DatingRuleCfgBean bean = GameDataManager.getDatingRuleCfgBean(scriptCid);
        List<BaseDating> list = new ArrayList<BaseDating>();
        for (BaseDating dating : GameDataManager.getBaseDating(bean.getCallTableName()).values()) {
            if (dating.getScriptId() == scriptCid)
                list.add(dating);
        }
        return list;
    }

}
