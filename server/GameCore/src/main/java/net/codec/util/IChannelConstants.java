package net.codec.util;

import io.netty.util.AttributeKey;

/**
 * 通信通道属性绑定常量键值定义接口
 * @author wk.dai
 */
public interface IChannelConstants {
	
	/**
	 * 解密密钥属性键值，为了兼容长连接第一次通信和短连接每次连接之后进行重连接时使用指令方式进行重连接而添加
	 */
	public static final AttributeKey<int[]> DECRYPTION_KEYS_ATTRIBUTE_KEY = AttributeKey.valueOf("decryptionKeys");
	/** 加密钥密 */
	public static final AttributeKey<int[]> ENCRYPTION_KEYS = AttributeKey.valueOf("encryption_keys");
	/** 解密钥密 */
	public static final AttributeKey<int[]> DECRYPTION_KEYS = AttributeKey.valueOf("decryption_keys");
}
