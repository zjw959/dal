package thread;

import kafka.service.G2FCreateConsumerService;
import thread.timer.TimeEventProcessor;
import thread.timer.TimerEvent;

public class G2FCreateConsumerProcessor extends BaseProcessor {
    public G2FCreateConsumerProcessor() {
        super(G2FCreateConsumerProcessor.class.getSimpleName());
        init();
    }

    private static class DEFAULT {
        private static final G2FCreateConsumerProcessor provider = new G2FCreateConsumerProcessor();
    }

    public static G2FCreateConsumerProcessor getDefault() {
        return DEFAULT.provider;
    }

    private void init() {
        TimeEventProcessor.getInstance().addEvent(new TimerEvent(66, 66, true) {
            @Override
            public void run() {
                executeHandler(new BaseHandler() {
                    @Override
                    public void action() throws Exception {
                        G2FCreateConsumerService.getDefault().queueProcess();
                    }
                });
            }
        });
    }
}
