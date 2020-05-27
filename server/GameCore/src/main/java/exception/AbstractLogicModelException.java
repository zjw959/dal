package exception;
/**
 * 逻辑异常
 *
 */
public abstract class AbstractLogicModelException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 479488970657331599L;
	
	/**
	 * 错误码
	 */
	protected int code;
	   
    /**
     * 错误日志前缀
     */
    protected String logPrefix;
	
	public AbstractLogicModelException() {
        super();
    }

    public AbstractLogicModelException(String message) {
        super(message);
    }

    public AbstractLogicModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractLogicModelException(Throwable cause) {
        super(cause);
    }

    protected AbstractLogicModelException(String message, Throwable cause,boolean enableSuppression,boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    public int getCode() {
		return code;
	}

    public String getLogPrefix() {
        return logPrefix;
    }
    
}
