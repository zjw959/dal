package thread;

import kafka.service.G2FConsumerTeamService;
import thread.timer.TimeEventProcessor;
import thread.timer.TimerEvent;

public class G2FConsumerTeamProcessor extends BaseProcessor {

    public G2FConsumerTeamProcessor() {
        super(G2FConsumerTeamProcessor.class.getSimpleName());
        init();
    }

    private static class DEFAULT {
        private static final G2FConsumerTeamProcessor provider = new G2FConsumerTeamProcessor();
    }

    public static G2FConsumerTeamProcessor getDefault() {
        return DEFAULT.provider;
    }

    private void init() {
        TimeEventProcessor.getInstance().addEvent(new TimerEvent(66, 66, true) {
            @Override
            public void run() {
                executeHandler(new BaseHandler() {
                    @Override
                    public void action() throws Exception {
                        G2FConsumerTeamService.getDefault().queueProcess();
                    }
                });
            }
        });
    }
}
