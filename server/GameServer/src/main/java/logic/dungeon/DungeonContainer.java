package logic.dungeon;

/**
 * 副本容器,用于对限时副本等的统一管理
 * 
 * @author Alan
 *
 */
public class DungeonContainer/* extends SysService implements ITick */{
//    /** 当前容器运行状态 */
//    AtomicBoolean running = new AtomicBoolean(false);
//    /** 容器启动时间,用于第一次运行时的检测 */
//    Date defaultRefreshTime = new Date();
//    /** 对各副本更新成功的时间缓存 */
//    Map<Integer, Date> dungeonRefreshTime = new HashMap<Integer, Date>();
//
//    @Override
//    public void tick() {
//        collateDungeons(new Date());
//    }
//
//    /** 整理副本 */
//    public void collateDungeons(Date now) {
//        if (!running.compareAndSet(false, true))
//            return;
//        // 应该使用业务线程做逻辑
//        getProcess().executeInnerHandler(new GameInnerHandler() {
//
//            @Override
//            public void action() throws AbstractLogicModelException {
//                try {
//                    // 仅做跨天事件点缓存,若执行过程出现异常终止不影响玩家具体数据对象内的标记
//                    // 玩家进行其他途径可继续激活事件,如登录
//                    // boolean isNewDay = TimeUtil.isSameDay(lastCheck, time);
//                    List<Player> onlinePlayers = PlayerManager.getAllPlayers();
//                    // 个人刷新已交由player自己处理
//                    // refreshDungeon(onlinePlayers, isNewDay, time);
//                    noticeActivityDungeonTimePoint(onlinePlayers, now);
//                    // setLastCheck(time);
//                } finally {
//                    running.set(false);
//                }
//
//            }
//        });
//    }
//
//    // private void refreshDungeon(Collection<Player> players, boolean isNewDay, Date now) {
//    // if (!isNewDay) return;
//    // players.forEach(player -> {
//    // DungeonManager dm = player.getDungeonManager();
//    // dm.updateGroupList(now, true);
//    // dm.updateDungeonList(now, true);
//    // });
//    // }
//
//    /**
//     * 通知活动副本开启及结束
//     */
//    void noticeActivityDungeonTimePoint(Collection<Player> sourcePlayers, Date date) {
//        Collection<Player> players = new HashSet<Player>(sourcePlayers);
//        Map<Integer, List<Integer>> timePointGroups = getTimePointGroup(date);
//        if (timePointGroups.size() > 0) {
//            S2CDungeonMsg.UpdateActivityDungeon.Builder builder =
//                    DungeonMsgBuilder.getUpdateActivityDungeonMsg(timePointGroups);
//            players.forEach(player -> {
//                MessageUtils.send(player, builder);
//            });
//        }
//    }
//
//    /**
//     * 获取开始和结束的活动副本
//     */
//    private Map<Integer, List<Integer>> getTimePointGroup(Date date) {
//        Map<Integer, List<Integer>> result = Maps.newHashMap();
//        GameDataManager.getDungeonLevelGroupCfgBeans().forEach(
//                group -> {
//                    Date groupLastCheck = dungeonRefreshTime.put(group.getId(), date);
//                    int timePoint =
//                            ConfigDateTimeUtil.getTimePoint(group.getOpenTimeType(), group
//                                    .getTimeFrame(), groupLastCheck == null ? defaultRefreshTime
//                                    : groupLastCheck, date);
//                    if (timePoint != ConfigDateTimeUtil.TIME_POINT_NONE) {
//                        List<Integer> list =
//                                result.computeIfAbsent(timePoint, k -> Lists.newArrayList());
//                        list.add(group.getId());
//                    }
//                });
//        return result;
//    }
//
//    public static DungeonContainer getInstance() {
//        return Singleton.INSTANCE.getInstance();
//    }
//
//    /**
//     * <p>
//     * enum单例
//     * <li>在安全性上,enum特殊类不能进行反射获得,不能通过使用相同类加载器链的情况下反序列化生成多实例
//     * <li>书写上更优雅
//     * <p>
//     * 枚举成员列表的初始化规则与饿汉模式相同,都不存在并发问题
//     */
//    private enum Singleton {
//        INSTANCE;
//
//        DungeonContainer instance;
//
//        private Singleton() {
//            instance = new DungeonContainer();
//        }
//
//        DungeonContainer getInstance() {
//            return instance;
//        }
//    }
}
