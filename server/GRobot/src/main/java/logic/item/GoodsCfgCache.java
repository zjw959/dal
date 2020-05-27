package logic.item;

import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.log4j.Logger;

import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.DressCfgBean;
import data.bean.EquipmentCfgBean;
import data.bean.HeroCfgBean;
import data.bean.HeroSkinCfgBean;
import data.bean.ItemCfgBean;
import data.bean.ItemTimeCfgBean;
import data.bean.RoomCfgBean;

/**
 * 
 * @Description 物品缓存（主要是为了兼容之前的配置表结构）
 * @author LiuJiang
 * @date 2018年6月5日 下午7:23:43
 *
 */
public class GoodsCfgCache {
    private static final Logger LOGGER = Logger.getLogger(GoodsCfgCache.class);
    /** 同步锁对象（服务器运行中可能重载配置表） */
    private Object lockObj = new Object();

    public static GoodsCfgCache getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        GoodsCfgCache instance;

        private Singleton() {
            instance = new GoodsCfgCache();
        }

        GoodsCfgCache getInstance() {
            return instance;
        }
    }

    final ConcurrentSkipListMap<Integer, BaseGoods> cache =
            new ConcurrentSkipListMap<Integer, BaseGoods>();

    /** 初始化 */
    public void init() {
        synchronized (lockObj) {
            cache.clear();
            // 属于物品类的container
            // 时装
            for (DressCfgBean bean : GameDataManager.getDressCfgContainer().getList()) {
                add(bean);
            }
            // 灵装
            for (EquipmentCfgBean bean : GameDataManager.getEquipmentCfgContainer().getList()) {
                add(bean);
            }
            // 精灵
            for (HeroCfgBean bean : GameDataManager.getHeroCfgContainer().getList()) {
                add(bean);
            }
            // 精灵皮肤
            for (HeroSkinCfgBean bean : GameDataManager.getHeroSkinCfgContainer().getList()) {
                add(bean);
            }
            // 道具
            for (ItemCfgBean bean : GameDataManager.getItemCfgContainer().getList()) {
                add(bean);
            }
            // 限时道具
            for (ItemTimeCfgBean bean : GameDataManager.getItemTimeCfgContainer().getList()) {
                add(bean);
            }
            // 房间
            for (RoomCfgBean bean : GameDataManager.getRoomCfgContainer().getList()) {
                add(bean);
            }
        }
    }

    private void add(BaseGoods baseGoods) {
        if (cache.containsKey(baseGoods.getId())) {
            BaseGoods old = cache.get(baseGoods.getId());
            LOGGER.error("道具ID重复 " + baseGoods.getClass().getSimpleName() + " "
                    + baseGoods.getId() + ",在 " + old.getClass().getSimpleName()
                    + " 已经加载 ！");
        }
        cache.put(baseGoods.getId(), baseGoods);
    }

    /**
     * 获取物品缓存
     * @param id
     * @return
     */
    public BaseGoods get(int id) {
        synchronized (lockObj) {
            BaseGoods goods = cache.get(id);
            if (goods == null) {
                LOGGER.warn("道具不存在  id:" + id);
            }
            return goods;
        }
    }
}
