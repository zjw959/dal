package javascript.logic.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.hutool.core.util.RandomUtil;
import data.GameDataManager;
import data.bean.CityRoleCfgBean;
import data.bean.RoleCfgBean;
import logic.character.bean.Player;
import logic.city.BuildingGeneral;
import logic.city.build.BuildingConstant;
import logic.city.build.bean.BuildEvent;
import logic.city.build.bean.CityRoleRecord;
import logic.city.build.bean.OccupyRoleInfo;
import logic.city.script.ICityRoleManagerScript;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.item.bean.DressItem;
import logic.item.bean.Item;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.role.bean.Role;
import logic.support.MessageUtils;
import utils.TimeUtil;

/****
 * 
 * 城市精灵脚本
 * 
 * @author lihongji
 *
 */

public class CityRoleManagerScript implements ICityRoleManagerScript {

	/** 返回脚本的Id **/
	@Override
	public int getScriptId() {
		return EScriptIdDefine.CITY_ROLE_SCRIPT.Value();
	}

	/** 刷新房间精灵 **/
	@Override
	public void Refresh(Player player) {
		if (!checkRefresh(player))
			return;
		RefreshCityRole(player);
	}

	/** 检查房间精灵是否刷新 **/
	@Override
	public boolean checkRefresh(Player player) {
		return System.currentTimeMillis() > player.getCityRoleManager().getRoleRefreshTime();
	}

	/** 系统强制刷新精灵系统 **/
	@Override
	public void RefreshCityRole(Player player) {
		// 获取所有的房间（如果一个房间智能有一个精灵的话那么每次都会去获取当前房间数量）
		player.getCityRoleManager().setCityRoles(new HashMap<Integer, ArrayList<CityRoleRecord>>());
		getSystemCityRole(player).forEach((roleId) -> {
			RefreshRole(roleId, player);
		});
		MessageUtils.send(player, NewBuildingMsgBuilder.refreshCityRole(player.getCityRoleManager(), null)
				.setDayType(player.getNewBuildingManager().getDayType()));
		player.getCityRoleManager()
				.setRoleRefreshTime(TimeUtil.getTodayOfhonur() + BuildingConstant.REFRESH_ROLE_ROOM_TIME);
	}

	/** 刷新单个精灵 **/
	@Override
	public CityRoleRecord RefreshRole(int roleId, Player player) {
		CityRoleRecord cityRole = null;
		long dressId = 0;
		ArrayList<CityRoleCfgBean> cityRoleList = getCityRoleList(roleId);
		CityRoleCfgBean cityRolebean = rightTimeRole(cityRoleList);
		if (player.getRoleManager().getRole(roleId) != null) {
			dressId = getRoleModel(player.getRoleManager().getRole(roleId), player);
		}
		if (dressId <= 0)
			dressId = cityRolebean.getDress()[0];
		if (player.getCityRoleManager().getOccupyBuilding().get(roleId) != null) {
			cityRole = new CityRoleRecord(roleId,
					player.getCityRoleManager().getOccupyBuilding().get(roleId).getBuildingId(), dressId,
					player.getCityRoleManager().getOccupyBuilding().get(roleId).getLineId());
		} else {
			if (cityRolebean.getShowCondition() < BuildingConstant.ROLE_ALL_PROB) {
				if (cityRolebean.getShowCondition() > RandomUtil.randomInt(BuildingConstant.ROLE_ALL_PROB))
					return null;
			}
			int buildingId = getRoomByProb(getProbabilitys(cityRolebean.getCondition(), player), 2, 0);
			if (buildingId == 0) {
				return null;
			}
			if (cityRolebean.getDialog() == null)
				return null;

			int line = cityRolebean.getDialog()[RandomUtil.randomInt(cityRolebean.getDialog().length)];
			// 如果建筑为空那么全部弄在路面去
			cityRole = new CityRoleRecord(roleId, buildingId, dressId, line);
		}
		Map<Integer, ArrayList<CityRoleRecord>> cityRoles = player.getCityRoleManager().getCityRoles();
		ArrayList<CityRoleRecord> param = cityRoles.get(cityRole.getBuildingId());
		if (param == null) {
			param = new ArrayList<CityRoleRecord>();
			cityRoles.put(cityRole.getBuildingId(), param);
		}
		param.add(cityRole);
		return cityRole;
	}

	/** 获取当前的时装id **/
	@Override
	public int getDressId(CityRoleCfgBean cityRole) {
		int random = (int) (Math.random() * cityRole.getDress().length);
		return cityRole.getDress()[random];
	}

	/** 获取配置中的城市精灵 **/
	@Override
	public Set<Integer> getSystemCityRole(Player player) {
		Map<Integer, Role> roles = player.getRoleManager().getRoles();
		Set<Integer> cityRoles = new HashSet<>();
		GameDataManager.getCityRoleCfgBeans().forEach((cityrole) -> {
			int roleId = cityrole.getRoleId();
			if (cityrole.getType() == BuildingConstant.ROLE_TYPE && roles.get(cityrole.getRoleId()) != null) {
				cityRoles.add(roleId);
			} else if (cityrole.getType() == BuildingConstant.NPC_TYPE && !cityRoles.contains(roleId)) {
				cityRoles.add(roleId);
			}
		});
		return cityRoles;
	}

	/** 计算role出现在什么地方 **/
	@Override
	public int getRoomByProb(ArrayList<Integer> list, int parm, int index) {
		if (list == null || list.size() == 0)
			return 0;
		int probability = (int) (Math.random() * list.get(list.size() - 1));
		int startrPobability = 0;
		for (int i = 0; i < list.size(); i += parm) {
			if (startrPobability <= probability && probability < list.get(i + 1)) {
				return list.get(i);
			}
			startrPobability = list.get(i + 1);
		}
		// 如果没有随机到 则默认写第一个
		return list.get(index);
	}

	/** 获取当前精灵出现的时间列表 **/
	@Override
	public ArrayList<CityRoleCfgBean> getCityRoleList(int roleId) {
		ArrayList<CityRoleCfgBean> cityRoles = new ArrayList<CityRoleCfgBean>();
		for (CityRoleCfgBean cityRole : GameDataManager.getCityRoleCfgBeans()) {
			if (cityRole.getRoleId() == roleId)
				cityRoles.add(cityRole);
		}
		return cityRoles;
	}

	/** 获取当前精灵出现的时间点 **/
	@Override
	public CityRoleCfgBean rightTimeRole(ArrayList<CityRoleCfgBean> cityRoles) {
		long timeNow = System.currentTimeMillis();
		long timeStart = TimeUtil.getNextZeroClock();
		for (int i = cityRoles.size() - 1; i >= 0; i--) {
			CityRoleCfgBean bean = cityRoles.get(i);
			int[] time = bean.getTime();
			long stime = timeStart - time[0] * TimeUtil.SECOND;
			long etime = timeStart - time[1] * TimeUtil.SECOND;
			if (stime > timeNow && timeNow < etime)
				return bean;
		}
		// 默认选择第一个
		return cityRoles.get(0);
	}

	/** 添加精灵占用房间(由约会触发) **/
	@Override
	public void addOccupyBuilding(int roleId, int buildingId, int lineId, Player player) {
		player.getCityRoleManager().getOccupyBuilding().put(roleId, new OccupyRoleInfo(roleId, buildingId, lineId));
		/** 刷新精灵 **/
		RefreshRole(roleId, player);
		MessageUtils.send(player, NewBuildingMsgBuilder.refreshCityRole(player.getCityRoleManager(), null)
				.setDayType(player.getNewBuildingManager().getDayType()));
	}

	/** 减去占用房间 **/
	@Override
	public void removeOccupyBuilding(int roleId, Player player) {
		player.getCityRoleManager().getOccupyBuilding().remove(roleId);
	}

	/** 精灵添加 **/
	@Override
	public void addRoleInroom(int roleId, Player player) {
		CityRoleRecord cityRole = RefreshRole(roleId, player);
		if (cityRole == null)
			return;
		List<BuildEvent> addEvent = new ArrayList<BuildEvent>();
		addEvent.add(BuildingGeneral.createBuildingEvent(cityRole.getBuildingId(),
				BuildingConstant.EVENT_TYPE_ROLE_UNSEAL, roleId, 0, player));
		// 刷新新增精灵
		MessageUtils.send(player, NewBuildingMsgBuilder.refreshCityRole(player.getCityRoleManager(), addEvent)
				.setDayType(player.getNewBuildingManager().getDayType()));
	}

	/** 解锁判定 **/
	@SuppressWarnings("unchecked")
    @Override
	public void unlock(Object object, Player player) {
		if (object == null)
			return;
		Map<String, Object> param = (Map<String, Object>) object;
		if (param.get(BuildingConstant.EVENT_CONDITION_ID) == null)
			return;
		int id = (int) param.get(BuildingConstant.EVENT_CONDITION_ID);
		switch (id) {
		case BuildingConstant.REFRESH_ALL:
			break;
		case BuildingConstant.ADD_ROLE:
			int roleId = (int) param.get(EventConditionKey.EVENT_RESULT_DATA);
			addRoleInroom(roleId, player);
			break;
		case BuildingConstant.CHANGE_MODEL:
			int rId = (int) param.get(EventConditionKey.EVENT_RESULT_DATA);
			changeRoleModel(player.getRoleManager().getRole(rId), player);
			break;

		default:
			break;
		}
	}

	/** 获取模型id **/
	@Override
	public int getRoleModel(Role role, Player player) {
		Item item = player.getBagManager().getItemOrigin(role.getDressId());
		if (item == null) {
			return 0;
		}
		DressItem dressItem = (DressItem) item;
		RoleCfgBean cfg = GameDataManager.getRoleCfgBean(role.getCid());
		if (cfg == null || cfg.getDress().length <= 0) {
			return 0;
		}
		return dressItem.getTemplateId();
	}

	/** 更换模型 **/
	@Override
	public void changeRoleModel(Role role, Player player) {
		player.getCityRoleManager().getCityRoles().forEach((buildingId, recordList) -> {
			recordList.forEach((record) -> {
				if (record.getId() == role.getCid()) {
					record.setDressId(getRoleModel(role, player));
					MessageUtils.send(player, NewBuildingMsgBuilder.refreshCityRole(player.getCityRoleManager(), null)
							.setDayType(player.getNewBuildingManager().getDayType()));
					return;
				}
			});

		});

	}

	/** 获取当前精灵房间的概率 **/
	@Override
	public ArrayList<Integer> getProbabilitys(int[] probabilitys, Player player) {
		if (probabilitys == null)
			return null;
		Set<Integer> validbuilding = player.getNewBuildingManager().getValidbuilding();
		ArrayList<Integer> list = new ArrayList<Integer>();
		int pro = 0;
		for (int i = 0; i < probabilitys.length; i += 2) {
			if (probabilitys[i] == 2 || probabilitys[i] == 3 || validbuilding.contains(probabilitys[i])) {
				pro += probabilitys[i + 1];
				list.add(probabilitys[i]);
				list.add(pro);
			}
		}
		return list;
	}

	/** 刷新房间精灵 **/
	@Override
	public void tick(Player player) {
		Refresh(player);
	}

}
