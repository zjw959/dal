package thread.sys;

import org.apache.log4j.Logger;
import kafka.service.AssignConsumerService;
import thread.sys.base.SysFunctionProcessor;


/**
 * kafka消费者接收线程
 */
public class AssignConsumerProcessor extends SysFunctionProcessor {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger.getLogger(AssignConsumerProcessor.class);

    private AssignConsumerProcessor() {
        super(AssignConsumerProcessor.class.getSimpleName());
        _initTimer(66, 66, false);
    }

    @Override
    protected void registerService() {
        services.add(AssignConsumerService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        AssignConsumerService.getInstance().queueProcess();
    }
}
