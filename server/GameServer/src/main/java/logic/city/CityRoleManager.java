package logic.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import data.bean.CityRoleCfgBean;
import logic.basecore.IAcrossDay;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.IUnlockBuilding;
import logic.basecore.PlayerBaseFunctionManager;
import logic.city.build.bean.CityRoleRecord;
import logic.city.build.bean.OccupyRoleInfo;
import logic.city.script.ICityRoleManagerScript;
import logic.constant.EAcrossDayType;
import logic.role.bean.Role;
import logic.support.LogicScriptsUtils;

/***
 * 
 * 精灵系统(城市建设中出现精灵)
 * 
 * @author lihongji
 *
 */
public class CityRoleManager extends PlayerBaseFunctionManager
        implements IAcrossDay,IRoleJsonConverter, IUnlockBuilding {

    /**
     * 房间里的精灵：房间对精灵的映射
     */
    private Map<Integer, ArrayList<CityRoleRecord>> cityRoles =
            new HashMap<Integer, ArrayList<CityRoleRecord>>();

    /** 房间精灵刷新时间 **/
    private long roleRefreshTime;

    private Map<Integer, OccupyRoleInfo> occupyBuilding = new HashMap<Integer, OccupyRoleInfo>();


    private ICityRoleManagerScript getManagerScript() {
        return LogicScriptsUtils.getICityRoleManagerScript();
    }


    /** 刷新房间精灵 **/
    public void Refresh() {
        getManagerScript().Refresh(player);
    }

    /** 检查房间精灵是否刷新 **/
    public boolean checkRefresh() {
        return getManagerScript().checkRefresh(player);
    }


    /** 系统强制刷新精灵系统 **/
    public void RefreshCityRole() {
        getManagerScript().RefreshCityRole(player);
    }


    /** 刷新单个精灵 **/
    public CityRoleRecord RefreshRole(int roleId) {
        return getManagerScript().RefreshRole(roleId, player);
    }

    /** 获取当前的时装id **/
    public int getDressId(CityRoleCfgBean cityRole) {
        return getManagerScript().getDressId(cityRole);
    }

    /** 获取当前精灵房间的概率 **/
    public ArrayList<Integer> getProbabilitys(int[] probabilitys) {
        return getManagerScript().getProbabilitys(probabilitys, player);
    }

    /** 获取配置中的城市精灵 **/
    public Set<Integer> getSystemCityRole() {
        return getManagerScript().getSystemCityRole(player);
    }


    /** 计算role出现在什么地方 **/
    public int getRoomByProb(ArrayList<Integer> list, int parm, int index) {
        return getRoomByProb(list, parm, index);
    }

    /** 获取当前精灵出现的时间列表 **/
    public ArrayList<CityRoleCfgBean> getCityRoleList(int roleId) {
        return getManagerScript().getCityRoleList(roleId);
    }



    /** 获取当前精灵出现的时间点 **/
    public CityRoleCfgBean rightTimeRole(ArrayList<CityRoleCfgBean> cityRoles) {
        return getManagerScript().rightTimeRole(cityRoles);
    }



    /** 添加精灵占用房间(由约会触发) **/
    public void addOccupyBuilding(int roleId, int buildingId, int lineId) {
        getManagerScript().addOccupyBuilding(roleId, buildingId, lineId, player);

    }

    /** 减去占用房间 **/
    public void removeOccupyBuilding(int roleId) {
        getManagerScript().removeOccupyBuilding(roleId, player);
    }

    /** 刷新房间精灵 **/
    @Override
    public void tick() {
        getManagerScript().Refresh(player);
    }

    /** 精灵添加 **/
    public void addRoleInroom(int roleId) {
        getManagerScript().addRoleInroom(roleId, player);
    }


    /** 解锁 **/
    @Override
    public void unlock(Object object) {
        getManagerScript().unlock(object, player);
    }

    /** 获取模型id **/
    public int getRoleModel(Role role) {
        return getManagerScript().getRoleModel(role, player);
    }


    /** 更换模型 **/
    public void changeRoleModel(Role role) {
        getManagerScript().changeRoleModel(role, player);
    }

    public Map<Integer, ArrayList<CityRoleRecord>> getCityRoles() {
        return cityRoles;
    }

    public void setCityRoles(Map<Integer, ArrayList<CityRoleRecord>> cityRoles) {
        this.cityRoles = cityRoles;
    }

    public Map<Integer, OccupyRoleInfo> getOccupyBuilding() {
        return occupyBuilding;
    }

    public void setOccupyBuilding(Map<Integer, OccupyRoleInfo> occupyBuilding) {
        this.occupyBuilding = occupyBuilding;
    }

    public long getRoleRefreshTime() {
        return roleRefreshTime;
    }

    public void setRoleRefreshTime(long roleRefreshTime) {
        this.roleRefreshTime = roleRefreshTime;
    }


    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        
    }


}
