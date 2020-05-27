package logic.dating.bean.dto;

/**
 * 约会剧本数据传输对象
 * 
 * @author Alan
 *
 */
public class DatingScriptDTO {
    int buildingId;
    int roleId;
    int scriptType;
    int cityId;
    int scriptId;
    String cityDatingId;

    public DatingScriptDTO(int buildingId, int roleId, int scriptType, int cityId, int scriptId,
            String cityDatingId) {
        super();
        this.buildingId = buildingId;
        this.roleId = roleId;
        this.scriptType = scriptType;
        this.cityId = cityId;
        this.scriptId = scriptId;
        this.cityDatingId = cityDatingId;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public DatingScriptDTO setBuildingId(int buildingId) {
        this.buildingId = buildingId;
        return this;
    }

    public int getRoleId() {
        return roleId;
    }

    public DatingScriptDTO setRoleId(int roleId) {
        this.roleId = roleId;
        return this;
    }

    public int getScriptType() {
        return scriptType;
    }

    public DatingScriptDTO setScriptType(int scriptType) {
        this.scriptType = scriptType;
        return this;
    }

    public int getCityId() {
        return cityId;
    }

    public DatingScriptDTO setCityId(int cityId) {
        this.cityId = cityId;
        return this;
    }

    public int getScriptId() {
        return scriptId;
    }

    public DatingScriptDTO setScriptId(int scriptId) {
        this.scriptId = scriptId;
        return this;
    }

    public String getCityDatingId() {
        return cityDatingId;
    }

    public DatingScriptDTO setCityDatingId(String cityDatingId) {
        this.cityDatingId = cityDatingId;
        return this;
    }


}
