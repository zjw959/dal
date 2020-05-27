package thread.sys;

import org.apache.log4j.Logger;
import kafka.service.AllConsumerService;
import thread.sys.base.SysFunctionProcessor;



/**
 * kafka消费者接收线程
 */
public class AllConsumerProcessor extends SysFunctionProcessor {
    private static final Logger LOGGER = Logger.getLogger(AllConsumerProcessor.class);

    public AllConsumerProcessor() {
        super(AllConsumerProcessor.class.getSimpleName());
        _initTimer(1000, 1000, true);
    }

    @Override
    protected void registerService() {
        services.add(AllConsumerService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        AllConsumerService.getInstance().queueProcess();
    }
}
