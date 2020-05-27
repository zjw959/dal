package exception;

/**
 * 逻辑异常
 *
 */
public class LogicModelException extends AbstractLogicModelException {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4465677405631244005L;

    public LogicModelException(String logPrefix, int code, String msg) {
        super(msg);
        this.logPrefix = logPrefix;
        this.code = code;
    }

    public LogicModelException(String logPrefix, int code) {
        this.logPrefix = logPrefix;
        this.code = code;
    }
}
