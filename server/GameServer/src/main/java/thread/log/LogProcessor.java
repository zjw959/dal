package thread.log;

import log.ILogInfo;
import thread.BaseHandler;
import thread.BaseProcessor;
import utils.ExceptionEx;

/**
 * 日志线程
 */
public class LogProcessor extends BaseProcessor {
    static int coreSize = Runtime.getRuntime().availableProcessors() < 2 ? 1 : 2;

    static {
        coreSize = Runtime.getRuntime().availableProcessors() / 4;
        if (coreSize < 2) {
            coreSize = 1;
        }
    }

    public LogProcessor() {
        super(LogProcessor.class.getSimpleName(), coreSize, coreSize);
    }

    @Override
    public void executeHandler(BaseHandler handler) {
        if (handler instanceof LogHandler) {
            super.executeHandler(handler);
            return;
        }
        LOGGER.error("hanldler is not LogHandler" + "\n Look up : " + handler.getClass().getName()
                + "\n" + ExceptionEx.currentThreadTraces());
    }

    public void sendLog(ILogInfo logInfo) {
        if (logInfo == null) {
            return;
        }
        executeHandler(new LogHandler(logInfo));
    }

    public static LogProcessor getInstance() {
        return LogProcessor.Singleton.INSTANCE.getProcessor();
    }

    private enum Singleton {
        INSTANCE;
        LogProcessor processor;

        Singleton() {
            processor = new LogProcessor();
        }

        LogProcessor getProcessor() {
            return processor;
        }
    }
}
