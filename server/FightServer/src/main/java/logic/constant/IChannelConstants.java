package logic.constant;

import io.netty.util.AttributeKey;

public interface IChannelConstants {
    /** 房间 */
    public static final AttributeKey<Long> FIGHT_ROOM = AttributeKey.valueOf("fight_room");
    /** 加密钥密 */
    public static final AttributeKey<int[]> ENCRYPTION_KEYS = AttributeKey.valueOf("encryption_keys");
    /** 解密钥密 */
    public static final AttributeKey<int[]> DECRYPTION_KEYS = AttributeKey.valueOf("decryption_keys");
}
