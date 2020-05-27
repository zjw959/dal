package net.codec.util;

/**
 * 加密类型枚举
 * @author wk.dai
 */
public interface EncryptType {
	/**
	 * 无加密
	 */
	byte NONE=0;
	/**
	 * DES加密
	 */
	byte DES=1;
	/**
	 * 自定义加密
	 */
	byte CUSTOMER=127;
}
