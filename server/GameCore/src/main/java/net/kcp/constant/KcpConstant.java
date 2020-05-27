package net.kcp.constant;

public class KcpConstant {
	/** 战斗房间 */
	public static final String FIGHT_ROOM = "FIGHT_ROOM";
	/** 解密钥密 */
	public static final String DECRYPTION_KEYS = "DECRYPTION_KEYS";
	/** 加密钥密 */
	public static final String ENCRYPTION_KEYS = "ENCRYPTION_KEYS";

	/** 包分割符号 */
    public static final short PACKET_TAG = 0x712b;
    /** 包分割符占用字节数 */
    public static final int PACKET_TAG_LENGTH = 2;
    /** 包长度占用字节数 */
    public static final int PACKET_LENGTH = 4;
    /** 包校验和占用字节数 */
    public static final int PACKET_CHECK_SUM = 2;
    /** 包消息号占用字节数 */
    public static final int PACKET_COMMAND_ID = 2;
    /** 包的状态码长度 */
    public static final int PACKET_STATUS_CODE = 4;
}
