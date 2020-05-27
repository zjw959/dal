package logic.dungeon;

/**
 * 副本场景服务,用于全局级别的场景缓存
 * 
 * @author Alan
 *
 */
public class DungeonSceneService /*extends SysService */{
//    private Map<String, DungeonScene> cache = new ConcurrentHashMap<String, DungeonScene>();
//
//    /** 放置场景 */
//    public void putScene(DungeonScene scene) {
//        cache.put(scene.getSceneId(), scene);
//    }
//
//    /** 移除场景 */
//    public DungeonScene removeScene(String sceneId) {
//        return cache.remove(sceneId);
//    }
//
//    /** 获取场景 */
//    @SuppressWarnings("unchecked")
//    public <T extends DungeonScene> T getById(String sceneId) {
//        return (T) cache.get(sceneId);
//    }
//
//    public Collection<DungeonScene> getAll() {
//        return cache.values();
//    }
//
//    public static DungeonSceneService getInstance() {
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
//        DungeonSceneService instance;
//
//        private Singleton() {
//            instance = new DungeonSceneService();
//        }
//
//        DungeonSceneService getInstance() {
//            return instance;
//        }
//    }
}
