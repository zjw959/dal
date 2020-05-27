package log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日志记录工具
 */
public class DalLog {
	private final Logger logger;

    public Logger getLogger() {
        return this.logger;
    }

	public static DalLog getLogger(String logName) {
		Logger logger = LogManager.getLogger(logName);
		return new DalLog(logger);
	}

	private DalLog(Logger logger) {
		this.logger = logger;
	}

	public boolean isDebugEnabled() {
		return this.logger.isDebugEnabled();
	}
	
	public void error(String info) {
		if (logger.isErrorEnabled()) {
			logger.error(info);
		}
	}

	public void error(String info, Throwable e) {
		if (logger.isErrorEnabled()) {
			logger.error(info, e);
		}
	}

	public void warn(String info) {
		if (logger.isWarnEnabled()) {
			logger.warn(info);
		}
	}

	public void warn(String info, Throwable e) {
		if (logger.isWarnEnabled()) {
			logger.warn(info, e);
		}
	}
	
	public void info(String info) {
		if (logger.isInfoEnabled()) {
			logger.info(info);
		}
	}

	public void debug(String info) {
		if (logger.isDebugEnabled()) {
			logger.debug(info);
		}
	}
}
