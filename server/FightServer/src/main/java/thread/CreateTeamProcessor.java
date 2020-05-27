package thread;

import logic.support.LogicScriptsUtils;
import thread.timer.TimeEventProcessor;
import thread.timer.TimerEvent;

public class CreateTeamProcessor extends BaseProcessor {

    public CreateTeamProcessor(String name) {
        super(name);
    }

    private static class DEFAULT {
        private static final CreateTeamProcessor provider =
                new CreateTeamProcessor(CreateTeamProcessor.class.getSimpleName());
    }

    public static CreateTeamProcessor getDefault() {
        return DEFAULT.provider;
    }

    protected void tick() {
        LogicScriptsUtils.getChasmScript().createTeamTick();
    }

    public class TickHandler extends BaseHandler {
        CreateTeamProcessor processor;

        public TickHandler(CreateTeamProcessor processor) {
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
                executeHandler(new TickHandler(CreateTeamProcessor.getDefault()));
            }
        });
    }
}
