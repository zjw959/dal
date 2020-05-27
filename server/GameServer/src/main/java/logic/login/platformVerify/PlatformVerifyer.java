package logic.login.platformVerify;

import io.netty.channel.ChannelHandlerContext;
import logic.login.struct.ChannelInfo;
import exception.AbstractLogicModelException;

/**
 * 
 * @Description 平台账号校验者
 * @author LiuJiang
 * @date 2018年6月3日 上午11:39:11
 *
 */
public interface PlatformVerifyer {
    void verify(ChannelHandlerContext ctx, ChannelInfo channelInfo)
            throws AbstractLogicModelException;
}
