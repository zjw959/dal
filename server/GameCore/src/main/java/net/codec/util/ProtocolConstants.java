package net.codec.util;

/**
 * 协议常量定义接口
 * @author wk.dai
 */
public interface ProtocolConstants {
	/** 包头长度 */
    public static final short DEFAULT_HEADER_SIZE = 0x0A;

    /** 包分隔符 */
    public static final short FLAG = 0x712b;
    
    /**
     * 标记位和消息长度两个字段的长度
     */
    public static final int FLAG_AND_LENGTH_DATA_SIZE=0x06;
    
    /**
     * 协议头除去标记位和消息长度两个字段的长度，剩余的字段长度
     */
    public static final int HEAD_WITHOUT_FLAG_AND_LENGTH_SIZE=DEFAULT_HEADER_SIZE-FLAG_AND_LENGTH_DATA_SIZE;
    /**
     * 校验和大小
     */
    public static final int CHECKSUM_SIZE=0x02;
    
    /**
     * 全文计算校验和时跳过的头部字节长度
     */
    public static final int CHECKSUM_SKIP_SIZE_FOR_CONTENT = 8;
}
