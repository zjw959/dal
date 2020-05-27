package logic.city.build.bean;


/***
 * 
 * 城建精灵信息
 * 
 * @author lihongji
 *
 */
public class CityRoleRecord {

    /** npc 精灵 id **/
    private int id;
    /** 建筑id **/
    private int buildingId;
    /** 时装id **/
    private long dressId;
    /** 获取台词的id **/
    private int lineId;

    public CityRoleRecord() {

    }

    public CityRoleRecord(int id, int buildingId, long dressId, int lineId) {
        this.id = id;
        this.buildingId = buildingId;
        this.dressId = dressId;
        this.lineId = lineId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public long getDressId() {
        return dressId;
    }

    public void setDressId(long dressId) {
        this.dressId = dressId;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

}
