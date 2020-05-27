package core.net.codec;

import core.net.kcp.KcpClient;
import core.robot.GRobotManager;
import core.robot.RobotThread;
import core.robot.RobotThreadFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import logic.chasmfight.unlimited.SendMsgService;
import net.codec.util.EncryptionAndDecryptionUtil;
import net.codec.util.IChannelConstants;

public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // 取得解密钥匙
        Attribute<int[]> decryptionKeysAttribute =
                ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS_ATTRIBUTE_KEY);
        int[] decryptionKeys = null;
        if ((decryptionKeys = decryptionKeysAttribute.get()) == null) {
            // 设置默认解密密钥
            decryptionKeys = EncryptionAndDecryptionUtil.getDefaultCustomDecryptionKeys();
            decryptionKeysAttribute.set(decryptionKeys);
        }
        // 取得加密钥匙
        int[] encryptionKeys = EncryptionAndDecryptionUtil.getDefaultCustomEncryptionKeys();
        ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS).set(decryptionKeys);
        ctx.channel().attr(IChannelConstants.ENCRYPTION_KEYS).set(encryptionKeys);
    }


    @Override
    protected void channelRead0(io.netty.channel.ChannelHandlerContext ctx, ByteBuf msg)
            throws Exception {}

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        closeChannel(ctx);
        super.channelInactive(ctx);
    }

    private void closeChannel(ChannelHandlerContext ctx) {
        RobotThread robot = RobotThreadFactory.removeFightRobot(ctx.channel().id().asLongText());
        if(robot == null) {
            robot = RobotThreadFactory.removeRobot(ctx.channel().id().asLongText());
            if (robot != null) {
                robot.getWindow().closeConnection(robot);
                GRobotManager.instance().remove(ctx.channel().id().asLongText());
                KcpClient kcpClient = robot.getKcpClient();
                if(kcpClient != null && kcpClient.isRunning()) {
                    kcpClient.close();
                    RobotThreadFactory.removeFightRobot(String.valueOf(kcpClient.getConv()));
                    SendMsgService.unlimitedMsg.remove(String.valueOf(kcpClient.getConv()));
                }
            }
        } else {
            robot.setFightChannel(null);
            SendMsgService.unlimitedMsg.remove(ctx.channel().id().asLongText());
        }
    }
}
