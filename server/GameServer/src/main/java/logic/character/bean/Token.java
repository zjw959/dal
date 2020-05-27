package logic.character.bean;

import java.util.UUID;

import logic.login.struct.ChannelInfo;
import utils.UUIDUtils;

/**
 * 令牌类
 * 
 * 玩家成功登录后为其首次创建一个令牌, 如果客户端与服务端断开连接, 当客户端再次发起消息请求之前, 需要重新连接服务器.
 * 
 * 如果玩家信息还在缓存中, 本次重连不用验证账号且不进入登录流程, 直接根据玩家持有的令牌进行验证即可.
 */
public final class Token {

    private final static int TOKEN_TIMES = 5 * 60000; // 令牌有效期（单位：毫秒）

    private UUID value; // 令牌值

    private long times; // 过期时间

    private ChannelInfo info; // 连接信息

    public Token(ChannelInfo info) {
        value = UUID.randomUUID();
        times = System.currentTimeMillis() + TOKEN_TIMES;
        this.info = info;
    }

    /**
     * 获取令牌值.
     * 
     * @return
     */
    public String getValue() {
        return UUIDUtils.toCompactString(value);
    }

    public UUID getUUID() {
        return value;
    }


    public boolean refresh() {
        long nowTime = System.currentTimeMillis();
        value = UUID.randomUUID();
        times = nowTime + TOKEN_TIMES;

        return true;
    }


    public ChannelInfo getInfo() {
        return info;
    }

    public void setInfo(ChannelInfo info) {
        this.info = info;
    }

    /**
     * 判断令牌是否超时
     * 
     * @return
     */
    public boolean isTimeout() {
        return System.currentTimeMillis() > times;
    }

    @Override
    public String toString() {
        return "token=" + value + ", times=" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date(times));
    }

}
