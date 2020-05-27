package utils.pool;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import utils.lang.StrBuilderEx;


public abstract class AbstractQueueObjPool<E> {
    protected final Queue<E> queues = new ConcurrentLinkedQueue<E>();

    protected abstract E createObj();

    protected abstract E resetObj(E obj);

    protected abstract E destoryObj(E obj);

    protected int MAX = Short.MAX_VALUE;

    protected final AtomicInteger numIdle = new AtomicInteger();
    protected final AtomicInteger numActive = new AtomicInteger();

    public E borrow() {
        E e;
        synchronized (queues) {
            if (numIdle.intValue() > 0) {
                numIdle.decrementAndGet();
                e = queues.poll();
            } else {
                e = createObj();
            }
            numActive.incrementAndGet();
            return e;
        }
    }

    public void returnObj(E obj) {
        if (obj == null)
            return;

        synchronized (queues) {
            if (numIdle.intValue() > MAX) {
                destoryObj(obj);
                return;
            }
            resetObj(obj);
            queues.add(obj);
            numIdle.incrementAndGet();
            numActive.decrementAndGet();
        }
    }

    public void clear() {
        synchronized (queues) {
            queues.clear();
        }
    }

    public String getSize() {
        return StrBuilderEx.str("active:${2}, Idle:${1}", numIdle.get(), numActive.get());
    }
}
