package logic.city.build.bean;

/***
 * 建筑事件
 * 
 * @author lihongji
 *
 */
public class BuildEvent {

    /** 建筑id **/
    private int buildingId;
    /** 功能id **/
    private int funId;
    /** 事件类型 **/
    private int eventType;
    /** 创建时间 **/
    private long createTime;
    /** 执行的id **/
    private int exeId;

    /** 创建事件 **/
    public BuildEvent() {


    }

    /** 创建事件 **/
    public BuildEvent(int buildingId, int funId, int exeId, int eventType) {
        this.buildingId = buildingId;
        this.funId = funId;
        this.eventType = eventType;
        this.exeId = exeId;
        this.createTime = System.currentTimeMillis();
    }


    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public int getFunId() {
        return funId;
    }

    public void setFunId(int funId) {
        this.funId = funId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public int getExeId() {
        return exeId;
    }


    public void setExeId(int exeId) {
        this.exeId = exeId;
    }


}
