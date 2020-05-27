package utils.snowflake;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by raymond on 2017/7/28.
 */
public class SystemClock {

    private final long period;
    private final AtomicLong atomicTimestamp;

    private SystemClock(long period) {
        this.period = period;
        this.atomicTimestamp = new AtomicLong(System.currentTimeMillis());
        this.updateSystemTimestamp();
    }

    public static long currentTimestamp() {

        return SYSTEM_CLOCK_INSTANCE.atomicTimestamp.get();
    }

    private void updateSystemTimestamp() {
        ScheduledExecutorService timestampScheduledService =
                Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread thread = new Thread(r, "SystemClock > ScheduledService");
                    thread.setDaemon(true);// 后台守护进程
                        return thread;
                    });

        timestampScheduledService.scheduleAtFixedRate(
                () -> atomicTimestamp.set(System.currentTimeMillis()), period, period,
                TimeUnit.MILLISECONDS);
    }

    private static final SystemClock SYSTEM_CLOCK_INSTANCE = new SystemClock(1);
}
