package thread.sys;

import org.apache.log4j.Logger;

import server.MessageDispatchService;
import thread.sys.base.SysFunctionProcessor;

/**
 * 消息分发
 */
public class MessageDispathProcessor extends SysFunctionProcessor {
    private static final Logger LOGGER = Logger.getLogger(MessageDispathProcessor.class);

    int defalutPrintSize = 2000;

    private MessageDispathProcessor() {
        super(MessageDispathProcessor.class.getSimpleName());
    }

    @Override
    protected void registerService() {
        services.add(MessageDispatchService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
    }
}
