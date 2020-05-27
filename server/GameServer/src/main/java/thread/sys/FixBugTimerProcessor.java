package thread.sys;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import thread.HandlerThreadFactory;
import thread.timer.TimerEvent;

public class FixBugTimerProcessor {
    private final ScheduledExecutorService executor;

    public FixBugTimerProcessor() {
        executor = Executors.newSingleThreadScheduledExecutor(
                new HandlerThreadFactory("FixBugTimerProcessor"));
    }

    /**
     * 单件
     *
     * @return
     */
    public static FixBugTimerProcessor getInstance() {
        return FixBugTimerProcessor.Singleton.INSTANCE.getInstance();
    }

    public void stop() {
        executor.shutdownNow();
    }

    /**
     * 添加一个Timer
     *
     * @param event
     * @return
     */
    public final ScheduledFuture<?> addTimer(TimerEvent event) {
        if (event.getDelay() == 0)
            return executor.schedule(event, event.getFirstDelay(), TimeUnit.MILLISECONDS);
        else if (event.isLoopFixed())
            return executor.scheduleAtFixedRate(event, event.getFirstDelay(), event.getDelay(),
                    TimeUnit.MILLISECONDS);
        else
            return executor.scheduleWithFixedDelay(event, event.getFirstDelay(), event.getDelay(),
                    TimeUnit.MILLISECONDS);
    }

    /**
     * 单件枚举
     */
    private enum Singleton {
        INSTANCE;

        FixBugTimerProcessor instance;

        Singleton() {
            this.instance = new FixBugTimerProcessor();
        }

        FixBugTimerProcessor getInstance() {
            return instance;
        }
    }
}
