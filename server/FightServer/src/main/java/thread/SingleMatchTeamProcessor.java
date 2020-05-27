package thread;

import java.util.concurrent.atomic.AtomicInteger;
import logic.support.LogicScriptsUtils;
import thread.timer.TimeEventProcessor;
import thread.timer.TimerEvent;

public class SingleMatchTeamProcessor extends BaseProcessor {

    public SingleMatchTeamProcessor(String name) {
        super(name);
    }

    private static class DEFAULT {
        private static final SingleMatchTeamProcessor provider =
                new SingleMatchTeamProcessor(SingleMatchTeamProcessor.class.getSimpleName());
    }

    public static SingleMatchTeamProcessor getDefault() {
        return DEFAULT.provider;
    }

    private AtomicInteger interval = new AtomicInteger(1);

    protected void tick() {
        LogicScriptsUtils.getChasmScript().singleMatchTick(interval);
    }

    public class TickHandler extends BaseHandler {
        SingleMatchTeamProcessor processor;

        public TickHandler(SingleMatchTeamProcessor processor) {
            this.processor = processor;
        }

        @Override
        public void action() {
            this.processor.tick();
        }
    }

    public void registerTickTimer(long firstDelay, long delay, boolean loopFixed) {
        TimeEventProcessor.getInstance().addEvent(new TimerEvent(firstDelay, delay, loopFixed) {
            @Override
            public void run() {
                executeHandler(new TickHandler(SingleMatchTeamProcessor.getDefault()));
            }
        });
    }
}
