package gm.utils;

import gm.db.DBFactory;
import gm.db.global.bean.ActivityConfigure;
import gm.db.global.bean.ActivityShopItem;
import gm.db.global.bean.ActivityTaskItem;
import gm.db.global.dao.ActivityConfigureDao;
import gm.db.global.dao.ActivityShopItemDao;
import gm.db.global.dao.ActivityTaskItemDao;

import java.util.List;


/**
 * @Description 活动管理
 */
public class ActivityUtils{

    /**
     * 重构数据库连接工厂
     * */
    public static boolean rebuildDBFactory(String dbUrl, String dbUser, String dbPwd) {
        return DBFactory.GLOBAL_DB.rebuildSessionFactory(dbUrl, dbUser, dbPwd);
    }

	public static List<ActivityConfigure> queryAllActivityConfigure() {
		return ActivityConfigureDao.selectActivityConfigureList();
	}

	public static ActivityConfigure queryActivityConfigureById(Integer id) {
		return ActivityConfigureDao.selectActivityConfigureById(id);
	}

	/**
	 * 活动不管是新增还是修改  统一都设置为未确认状态   
	 * @param config
	 * @return
	 */
	public static boolean addActivityConfigure(ActivityConfigure config) {
		config.setUpdateStatus(1);
		Long now = System.currentTimeMillis();
		config.setCreateTime(now);
		config.setChangeTime(now);
		return ActivityConfigureDao.insertActivityConfigure(config);
	}

	/**
	 * 活动不管是新增还是修改  统一都设置为未确认状态   
	 * @param config
	 * @return
	 */
	public static boolean updateActivityConfigure(ActivityConfigure config) {
		config.setUpdateStatus(1);
		Long now = System.currentTimeMillis();
		config.setChangeTime(now);
		return ActivityConfigureDao.updateActivityConfigure(config);
	}

	public static boolean deleteActivityConfigure(Integer id) {
		return ActivityConfigureDao.deleteActivityById(id);
	}

	
	/**
	 * 确认活动编辑完成   修改活动的编辑状态   让游戏服能读取到该活动
	 * @param id
	 * @return
	 */
	public static boolean confirmActivity(Integer id) {
		Long changeTime = System.currentTimeMillis();
		return ActivityConfigureDao.updateActivityByIdAndChangeTime(id, changeTime);
	}

	public static List<ActivityShopItem> queryAllShopItem() {
		return ActivityShopItemDao.selectActivityShopItemList();
	}

	public static ActivityShopItem queryShopItemById(Integer id) {
		return ActivityShopItemDao.selectActivityShopItemById(id);
	}

	public static boolean addShopItem(ActivityShopItem config) {
		return ActivityShopItemDao.insertActivityShopItem(config);
	}

	public static boolean updateShopItem(ActivityShopItem config) {
		return ActivityShopItemDao.updateActivityShopItem(config);
	}

	public static boolean deleteShopItem(Integer id) {
		return ActivityShopItemDao.deleteActivityById(id);
	}
	
	
	public static List<ActivityTaskItem> queryAllTaskItem() {
        return ActivityTaskItemDao.selectActivityTaskItemList();
    }

    public static ActivityTaskItem queryTaskItemById(Integer id) {
        return ActivityTaskItemDao.selectActivityTaskItemById(id);
    }

    public static boolean addTaskItem(ActivityTaskItem config) {
        return ActivityTaskItemDao.insertActivityTaskItem(config);
    }

    public static boolean updateTaskItem(ActivityTaskItem config) {
        return ActivityTaskItemDao.updateActivityTaskItem(config);
    }

    public static boolean deleteTaskItem(Integer id) {
        return ActivityTaskItemDao.deleteActivityById(id);
    }
	
	
}
