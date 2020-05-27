package utils.pool;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每个线程对应一个对象
 * 
 * @param <T>
 */
public class ThreadPool<T> {

    private final Map<Thread, T> pool_map = new ConcurrentHashMap<Thread, T>();

    public T getMessagePool(Class<T> cls) {

        T pool = pool_map.get(Thread.currentThread());

        if (pool == null) {
            synchronized (ThreadPool.class) {
                pool = pool_map.get(Thread.currentThread());
                if (pool == null) {
                    try {
                        pool = cls.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("反射异常", e);
                    }
                    pool_map.put(Thread.currentThread(), pool);

                    checkDeadThread();// 新开一个pool去检查一次，调用频率适中
                }
            }
        }

        return pool;
    }

    public int getPoolCount() {
        return this.pool_map.size();
    }

    /**
     * 检查是否该清理死线程占用的资源
     */
    private void checkDeadThread() {
        for (Iterator<Thread> ite = pool_map.keySet().iterator(); ite.hasNext();) {
            if (!ite.next().isAlive()) {
                ite.remove();
            }
        }
    }

    /**
     * 所有池化的对象的集合
     * 
     * @return
     */
    public Collection<T> getValueCollection() {
        return pool_map.values();
    }

    /**
     * 所有实体的迭代器
     * 
     * @return
     */
    public Set<Map.Entry<Thread, T>> getEntrySet() {
        return pool_map.entrySet();
    }

    private final StringBuilder sbInfo = new StringBuilder();

    public String getMessagePoolInfo() {
        sbInfo.setLength(0);
        sbInfo.append("MessagePoolInfo（数量 = ").append(pool_map.size()).append("）[");
        for (Map.Entry<Thread, T> entry : pool_map.entrySet()) {
            sbInfo.append(entry.getKey().getName()).append(" : ").append(entry.getValue())
                    .append(", ");
        }
        sbInfo.append("]");

        return sbInfo.toString();
    }

}
