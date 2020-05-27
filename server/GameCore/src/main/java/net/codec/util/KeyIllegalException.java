/**
 * 
 */
package net.codec.util;

/**
 * 非法键值异常
 * @author wk.dai
 */
public class KeyIllegalException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7698313359368593965L;

	public KeyIllegalException() {
		super();
	}

	public KeyIllegalException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public KeyIllegalException(String message, Throwable cause) {
		super(message, cause);
	}

	public KeyIllegalException(String message) {
		super(message);
	}

	public KeyIllegalException(Throwable cause) {
		super(cause);
	}

}
