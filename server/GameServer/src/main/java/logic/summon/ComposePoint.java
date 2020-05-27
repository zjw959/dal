package logic.summon;

public class ComposePoint {
    private int id;
    /** 合成状态 */
    private EComposeState state;
    /** 合成配方 高低中 */
    private int type;
    /** 合成结束时间 */
    private long finishTime;

    public ComposePoint() {}

    public ComposePoint(int id) {
        this.id = id;
        this.state = EComposeState.IDLE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EComposeState getState() {
        return state;
    }

    public void setState(EComposeState state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }
}
