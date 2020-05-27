package logic.store;

import java.util.Date;

public class StoreService {
    // private Table<Integer, Integer, StoreGrid> maping = HashBasedTable.create();

    public static StoreService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        StoreService instance;

        private Singleton() {
            instance = new StoreService();
        }

        StoreService getInstance() {
            return instance;
        }
    }

    // public void initStore() {
    // // if (maping == null || maping.isEmpty()) {
    // List<CommodityCfgBean> beanList = GameDataManager.getCommodityCfgBeans();
    // for (CommodityCfgBean bean : beanList) {
    // loadCacheCallBack(bean);
    // }
    // // }
    // }

    // public Table<Integer, Integer, StoreGrid> getMaping() {
    // initStore();
    // return maping;
    // }

    // public void loadCacheCallBack(CommodityCfgBean t) {
    // StoreGrid shopGrid = maping.get(t.getStoreId(), t.getGrid());
    // if (shopGrid == null) {
    // List<CommodityCfgBean> cfgs = Lists.newArrayList();
    // cfgs.add(t);
    // shopGrid = new StoreGrid(t.getGrid(), t.getWeight(), cfgs);
    // maping.put(t.getStoreId(), t.getGrid(), shopGrid);
    // } else {
    // shopGrid.getCfgs().add(t);
    // shopGrid.setTotalWeight(shopGrid.getTotalWeight() + t.getWeight());
    // }
    // }

    /**
     * 检查是否刷新
     *
     * @param nowDate 当前时间
     * @param lastRefreshDate 上次刷新时间
     * @param cronSequenceGenerator cron表达式解析器
     * @param refresher 刷新对象
     */
    public Date getNextfreshDate(Date nowDate, Date lastRefreshDate,
            utils.CronSequenceGenerator cronSequenceGenerator) {
        if (lastRefreshDate == null) {
            lastRefreshDate = nowDate;
        }
        // 正常的下一次执行时间
        Date nextRefreshDate = cronSequenceGenerator.next(lastRefreshDate);

        return nextRefreshDate;
    }
}
