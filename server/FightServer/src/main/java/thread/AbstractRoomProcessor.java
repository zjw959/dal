package thread;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import message.SMessageFactory;

public abstract class AbstractRoomProcessor<T> extends BaseProcessor {
    protected int processorId;

    // roomId -> room
    // 不做双向持有 房间线程处理器不引用房间. 房间的tick处理由其他方法驱动.
    // 准备线程与本线程增删该字段
    // 使用跳表 保证每次便利顺序的一致性
    protected final Map<Long, T> rooms = new ConcurrentSkipListMap<>();

    // 消息工厂
    private final SMessageFactory sMsgFactory = new SMessageFactory(100);

    protected AbstractRoomProcessor(String threadName, int processorId) {
        super(threadName);
        this.processorId = processorId;
    }

    public int getProcessorId() {
        return processorId;
    }

    public void addRoom(T room) {
        this.rooms.put(getId(room), room);
    }

    public T getRoom(Long key) {
        return this.rooms.get(key);
    }

    public Collection<T> getRooms() {
        return this.rooms.values();
    }

    public void removeRoom(T room) {
        this.rooms.remove(getId(room));
    }

    public abstract Long getId(T t);

    protected abstract void tick();

    public SMessageFactory getsMsgFactory() {
        return sMsgFactory;
    }

    public void shutdown() {

    }
}
