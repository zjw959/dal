package conf;

/**
 * @function 机器人配置
 */
public final class RobotConf {

    public RobotConf() {}

    public RobotConf(int robotMaxNum, int robotHoldNum, String prefixName, long sendDelayTime) {
        this.robotMaxNum = robotMaxNum;
        this.robotHoldNum = robotHoldNum;
        this.prefixName = prefixName;
        this.sendDelayTime = sendDelayTime;
    }

    /**
     * 机器人数量
     */
    private volatile int robotMaxNum;

    /**
     * 机器人名称前缀
     */
    private volatile String prefixName;

    /**
     * 机器人消息发送延时(单位毫秒)
     */
    private long sendDelayTime;

    private volatile int robotHoldNum;

    public int getRobotMaxNum() {
        return robotMaxNum;
    }

    public String getPrefixName() {
        return prefixName;
    }

    public long getSendDelayTime() {
        return sendDelayTime;
    }

    public int getRobotHoldNum() {
        return robotHoldNum;
    }

    public void setRobotMaxNum(int robotMaxNum) {
        this.robotMaxNum = robotMaxNum;
    }

    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    public void setSendDelayTime(long sendDelayTime) {
        this.sendDelayTime = sendDelayTime;
    }

    public void setRobotHoldNum(int robotHoldNum) {
        this.robotHoldNum = robotHoldNum;
    }
}
