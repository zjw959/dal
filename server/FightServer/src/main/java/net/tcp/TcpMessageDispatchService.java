package net.tcp;

import message.MessageHandler;
import message.RMessage;
import message.SMessage;
import net.Session;
import room.FightRoom;
import room.FightRoomManager;
import thread.FightRoomProcessorManager;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.ReqEnterFight;
import org.game.protobuf.c2s.C2SFightMsg.ReqFightPing;
import org.game.protobuf.c2s.C2SLoginMsg;
import org.game.protobuf.s2c.S2CFightMsg;
import org.game.protobuf.s2c.S2CFightMsg.RespFightPong;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import logic.constant.IChannelConstants;
import logic.support.MessageUtils;

/**
 * 消息分发service
 */
public class TcpMessageDispatchService {
    private static final Logger LOGGER = Logger.getLogger(TcpMessageDispatchService.class);
    
    public void dispatchMessage(MessageHandler messageHandler) {
         if (messageHandler == null) {
             return;
         }
         
         RMessage message = messageHandler.getMessage();
         if (message != null) {
             short msgId = message.getId();
             if (msgId == C2SLoginMsg.Ping.MsgID.eMsgID_VALUE) {// 跳过心跳功能
                 return;
             }
             if(msgId == C2SFightMsg.ReqFightPing.MsgID.eMsgID_VALUE) {
                 C2SFightMsg.ReqFightPing reqFightPing = (ReqFightPing) message.getData();
                 RespFightPong.Builder respFightPongBuilder = RespFightPong.newBuilder();
                 respFightPongBuilder.setTime(reqFightPing.getTime());
                 SMessage msg = new SMessage(S2CFightMsg.RespFightPong.MsgID.eMsgID_VALUE, respFightPongBuilder.build().toByteArray());
                 MessageUtils.send(messageHandler.getCtx(), msg);
             }
             long fightRoomId = 0;
             if (msgId == C2SFightMsg.ReqEnterFight.MsgID.eMsgID_VALUE) {
                 C2SFightMsg.ReqEnterFight reqEnterFight = (ReqEnterFight) message.getData();
                 String fightIdStr = reqEnterFight.getFightId();
                 fightRoomId = Long.parseLong(fightIdStr);
             } else {
                 ChannelHandlerContext ctx = messageHandler.getCtx();
                 Channel channel = ctx.channel();
                 Attribute<Long> attributes = channel.attr(IChannelConstants.FIGHT_ROOM);
                 fightRoomId = attributes.get();
             }
             FightRoom fightRoom = FightRoomManager.getRoomByRoomId(fightRoomId);
             if (fightRoom == null || fightRoom.isDestroy()) {
                 return;
             }
             
             Session session = new Session();
             session.setKcp(false);
             session.setCtx(messageHandler.getCtx());
             session.setFightRoom(fightRoom);
             messageHandler.setGameData(session);
             FightRoomProcessorManager.getInstance().addCommand(fightRoom.getProcessorId(),
                     messageHandler);   
         } else {
             LOGGER.error("未分发处理的Handler: " +  messageHandler);
         }
     }

    public static TcpMessageDispatchService getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;
        TcpMessageDispatchService processor;

        Singleton() {
            this.processor = new TcpMessageDispatchService();
        }

        TcpMessageDispatchService getProcessor() {
            return processor;
        }
    }
}
