package thread.log;

import log.ILogInfo;
import thread.BaseHandler;

/**
 * 日志handler
 */
public class LogHandler extends BaseHandler {
    private ILogInfo logInfo;

    public LogHandler(ILogInfo logInfo) {
        this.logInfo = logInfo;
    }

    @Override
    public void action() throws Exception {
        if (logInfo == null) {
            return;
        }
        logInfo.sendLog();
    }
}
