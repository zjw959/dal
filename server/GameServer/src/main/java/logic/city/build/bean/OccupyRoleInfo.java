package logic.city.build.bean;

/***
 * 
 * 约会占用房间记录
 * @author lihongji
 *
 */
public class OccupyRoleInfo {

    /** 精灵ID **/
    private int roleId;
    /** 建筑id **/
    private int buildingId;
    /** 台词id **/
    private int lineId;

    
    public OccupyRoleInfo(){
        
    }
    
    public OccupyRoleInfo(int roleId,int buildingId,int lineId){
        this.roleId=roleId;
        this.buildingId=buildingId;
        this.lineId=lineId;
        
    }
    
    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

}
