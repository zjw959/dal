package net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import logic.login.service.LoginService;
import logic.login.struct.ChannelInfo;

public class AttributeKeys {
    /**
     * 连接信息
     * 
     * 详见 ChannelInfo
     */
    public static final AttributeKey<ChannelInfo> CHANNEL_INFO =
            AttributeKey.valueOf("channel_info");

    /**
     * 连接时间
     */
    public static final AttributeKey<Long> CONNECT_TIME = AttributeKey.valueOf("connect_time");

    /**
     * 连接状态
     */
    public static AttributeKey<LoginService.CTXState> CHANNEL_STATUS =
            AttributeKey.valueOf("channel_status");


    public static void clear(ChannelHandlerContext ctx) {
        if (ctx != null && ctx.channel() != null) {
            Attribute<Long> attr1 = ctx.channel().attr(CONNECT_TIME);
            if (attr1.get() != null)
                attr1.remove();

            Attribute<ChannelInfo> attr2 = ctx.channel().attr(CHANNEL_INFO);
            if (attr2.get() != null)
                attr2.remove();

            Attribute<LoginService.CTXState> attr6 =
                    ctx.channel().attr(AttributeKeys.CHANNEL_STATUS);
            if (attr6.get() != null)
                attr6.remove();
        }
    }
}
