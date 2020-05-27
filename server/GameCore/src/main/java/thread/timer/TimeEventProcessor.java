package thread.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import thread.HandlerThreadFactory;

/**
 * 定时器线程 负责调度每个timeEvent
 */
public class TimeEventProcessor {

    private static final Logger LOGGER = Logger.getLogger(TimeEventProcessor.class);

    private final ScheduledExecutorService service;

    /**
     * 定时器线程基类
     */
    public TimeEventProcessor() {
        service = Executors.newSingleThreadScheduledExecutor(
                new HandlerThreadFactory(TimeEventProcessor.class.getSimpleName()));
    }

    public void stop() {
        service.shutdownNow();
    }

    /**
     * 添加一个定时执行事件
     * 
     * 单线程定时器,只为触发定时任务. 不要在该方法内写逻辑代码.处理延迟,会影响其他所有相关. 具体逻辑触发到其他指定线程去执行.
     * 
     * @param event 事件参数
     * @return 事件执行的future
     */
    public final ScheduledFuture<?> addEvent(TimerEvent event) {
        if (event == null) {
            LOGGER.error("event can not be null");
            return null;
        }
        if (event.getDelay() == 0) {
            return service.schedule(event, event.getFirstDelay(), TimeUnit.MILLISECONDS);
        } else if (event.isLoopFixed()) {
            return service.scheduleAtFixedRate(event, event.getFirstDelay(), event.getDelay(),
                    TimeUnit.MILLISECONDS);
        } else {
            return service.scheduleWithFixedDelay(event, event.getFirstDelay(), event.getDelay(),
                    TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 添加一个包含指定次数的定时事件
     * 
     * @param event 定时执行的事件
     * @param count 指定执行次数
     * @return
     */
    public final ScheduledFuture<?> addEvent(TimerEvent event, int count) {
        if (event == null || count <= 0) {
            LOGGER.error("event can not be null or  count must > 0");
            return null;
        }
        ScheduledFuture<?> future = null;
        TimerEventWrapper wrapper = new TimerEventWrapper(event, count);
        if (event.getDelay() == 0) {
            future = service.schedule(wrapper, wrapper.event.getFirstDelay(),
                    TimeUnit.MILLISECONDS);
        } else if (event.isLoopFixed()) {
            future = service.scheduleAtFixedRate(wrapper, wrapper.event.getFirstDelay(),
                    wrapper.event.getDelay(), TimeUnit.MILLISECONDS);
        } else {
            future = service.scheduleWithFixedDelay(wrapper, wrapper.event.getFirstDelay(),
                    wrapper.event.getDelay(), TimeUnit.MILLISECONDS);
        }
        wrapper.future = future;
        return future;
    }

    public class TimerEventWrapper implements Runnable {

        /**
         * 包装的事件
         */
        private final TimerEvent event;

        /**
         * 事件执行的Future
         */
        private ScheduledFuture future;

        /**
         * 事件执行的次数
         */
        private int count;

        /**
         * 包装定时器为指定次数
         * 
         * @param event
         * @param count
         */
        public TimerEventWrapper(TimerEvent event, int count) {
            this.event = event;
            this.count = count;
        }


        @Override
        public void run() {
            if (count > 0) {
                event.run();
                count--;
            } else {
                future.cancel(false);
            }
        }
    }

    /**
     * 单件枚举
     */
    private enum Singleton {
        INSTANCE;

        TimeEventProcessor instance;

        Singleton() {
            this.instance = new TimeEventProcessor();
        }

        TimeEventProcessor getInstance() {
            return instance;
        }
    }

    public static TimeEventProcessor getInstance() {
        return Singleton.INSTANCE.getInstance();
    }
}
