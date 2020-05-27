package logic.city.script;

import java.util.ArrayList;
import java.util.Set;
import data.bean.CityRoleCfgBean;
import logic.character.bean.Player;
import logic.city.build.bean.CityRoleRecord;
import logic.role.bean.Role;
import script.IScript;

/***
 * 
 * @author 城市精灵
 *
 */

public interface ICityRoleManagerScript extends IScript {

	/** 刷新房间精灵 **/
	public void Refresh(Player player);

	/** 检查房间精灵是否刷新 **/
	public boolean checkRefresh(Player player);

	/** 系统强制刷新精灵系统 **/
	public void RefreshCityRole(Player player);

	/** 刷新单个精灵 **/
	public CityRoleRecord RefreshRole(int roleId, Player player);

	/** 获取当前的时装id **/
	public int getDressId(CityRoleCfgBean cityRole);

	/** 获取配置中的城市精灵 **/
	public Set<Integer> getSystemCityRole(Player player);

	/** 计算role出现在什么地方 **/
	public int getRoomByProb(ArrayList<Integer> list, int parm, int index);

	/** 获取当前精灵出现的时间列表 **/
	public ArrayList<CityRoleCfgBean> getCityRoleList(int roleId);

	/** 获取当前精灵出现的时间点 **/
	public CityRoleCfgBean rightTimeRole(ArrayList<CityRoleCfgBean> cityRoles);

	/** 添加精灵占用房间(由约会触发) **/
	public void addOccupyBuilding(int roleId, int buildingId, int lineId, Player player);

	/** 减去占用房间 **/
	public void removeOccupyBuilding(int roleId, Player player);

	/** 精灵添加 **/
	public void addRoleInroom(int roleId, Player player);

	/** 解锁 **/
	public void unlock(Object object, Player player);

	/** 获取模型id **/
	public int getRoleModel(Role role, Player player);

	/** 更换模型 **/
	public void changeRoleModel(Role role, Player player);

	/** 获取当前精灵房间的概率 **/
	public ArrayList<Integer> getProbabilitys(int[] probabilitys, Player player);

	/** 刷新房间精灵 **/
	public void tick(Player player);

}
