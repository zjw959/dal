package utils.pool;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = -4952783105001241782L;
    private int cacheSize;

    public LRUCache(int cacheSize) {
        // 访问顺序
        super(16, 0.75f, true);
        this.cacheSize = cacheSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        int size = size();
        return size > cacheSize;
    }
}