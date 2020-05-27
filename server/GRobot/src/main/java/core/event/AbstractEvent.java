package core.event;

import org.game.protobuf.s2c.S2CLoginMsg;

import UI.Listener.RunFunctionListener;
import conf.RunConf;
import core.Log4jManager;
import core.net.message.SMessage;
import core.robot.RobotThread;
import utils.MiscUtils;

/**
 * @function 抽象事件类
 */
public abstract class AbstractEvent implements IEventAction {

    /**
     * 机器人
     */
    public final RobotThread robot;

    public AbstractEvent(RobotThread robot) {
        this.robot = robot;
    }

    public AbstractEvent(RobotThread robot, int resOrder) {
        this.robot = robot;
        this.resOrder = resOrder;
    }

    public String getFunctionInfo() {
        IsEvent isEventClazz = this.getClass().getAnnotation(IsEvent.class);
        if (isEventClazz != null) {
            FunctionType functionType = isEventClazz.functionT();
            return functionType.fName;
        } else {
            return "";
        }
    }

    public FunctionType getFunctionType() {
        IsEvent isEventClazz = this.getClass().getAnnotation(IsEvent.class);
        if (isEventClazz != null) {
            FunctionType functionType = isEventClazz.functionT();
            return functionType;
        } else {
            return null;
        }
    }

    /**
     * 执行事件
     */
    public final void doAction(Object... objParm) {
        try {
            if (this.robot == null) {
                Log4jManager.getInstance().error("############Robot Null#############");
            }
            if (this.robot.getPlayer().getIsLogin()
                    || (!this.robot.getPlayer().getIsLogin()
                            && this.resOrder == S2CLoginMsg.EnterSuc.MsgID.eMsgID_VALUE)) {
                action(objParm);
            }
        } catch (Exception e) {
            Log4jManager.getInstance().error(robot.getWindow(), e);
        }
        String actionResult = actionOver();
        if (actionResult != null && !actionResult.isEmpty()) {
            if (MiscUtils.isIDEEnvironment() && RunConf.robotConf.getRobotHoldNum() <= 5
                    && RunConf.robotConf.getSendDelayTime() >= 500) {
                Log4jManager.getInstance().info(robot.getWindow(), actionResult);
            }
        }
    }

    /**
     * 慎用
     * 
     * 仅适用于很特殊的逻辑
     * 
     * 跳过等待执行间隔,立即执行下一条指令
     */
    public void robotSkipRun() {
        robot.run(true);
    }

    /**
     * 事件执行结束
     */
    public String actionOver() {
        return "robot:" + robot.getName() + "," + this.getClass().getName()
                + "'s event action over !" + "Thread:" + Thread.currentThread().getId();
    }

    /**
     * 慎用
     * 
     * 发送消息
     *
     * 该接口会跳过消息等待,直接进行下一次逻辑执行.
     *
     * @param msg
     * @param isContiue 是否调过等待逻辑直接执行下一调
     */
    protected final void sendMsg(SMessage msg, boolean isContiue) {
        RunFunctionListener.addSendMsgPool(robot, msg, isContiue);
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    protected final void sendMsg(SMessage msg) {
        sendMsg(msg, false);
    }
    
    protected int resOrder = -1;
}
