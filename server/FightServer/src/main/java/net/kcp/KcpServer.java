package net.kcp;

import java.util.List;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import logic.chasm.InnerHandler.LChannelInactiveHandler;
import message.MessageHandler;
import net.Session;
import net.kcp.constant.KcpConstant;
import room.FightRoom;
import room.FightRoomManager;
import server.FightServer;
import thread.FightRoomProcessorManager;
import utils.ExceptionEx;

public class KcpServer extends BaseKcpServer {
    private Logger LOGGER = Logger.getLogger(KcpServer.class);
    
    public KcpServer(int port, int workerSize) throws Exception {
        super(port, workerSize);
    }

    @Override
    public void handleReceive(ByteBuf buf, KcpOnUdp kcp) {
        // 解码消息
        List<MessageHandler> messageHandlers =
                KcpCodec.decoder(buf, (int[])kcp.getSessionValue(KcpConstant.DECRYPTION_KEYS));

        // 进入消息路由
        for (MessageHandler messageHandler : messageHandlers) {
            if (FightServer.getInstance().isShutDown()) {
                LOGGER.info("server is shutdown, discard message.");
                return;
            }
            KcpMessageDispatchService.getInstance().dispatchMessage(messageHandler, kcp);
        }
    }

    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        LOGGER.error(kcp.getKcp().getConv() + ExceptionEx.t2s(ex));
    }

    @Override
    public void handleClose(KcpOnUdp kcp) {
        Long fightRoomId = (Long) kcp.getSessionValue(KcpConstant.FIGHT_ROOM);
        if(fightRoomId != null) {
            FightRoom fightRoom = FightRoomManager.getRoomByRoomId(fightRoomId);
            if(fightRoom != null) {
                Session session = new Session();
                session.setKcp(true);
                session.setKcpOnUdp(kcp);
                session.setFightRoom(fightRoom);
                FightRoomProcessorManager.getInstance().addCommand(fightRoom.getProcessorId(), new LChannelInactiveHandler(session));
            }
        }
    }
    
}
