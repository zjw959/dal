package thread;

import java.util.concurrent.ThreadFactory;

/**
 * 命令线程工厂
 */
public class HandlerThreadFactory implements ThreadFactory {
    public HandlerThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.threadName = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, threadName, 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

    /** 线程名字 **/
    private String threadName;
    /** 线程所属的组 **/
    private ThreadGroup group;
}
