package net.kcp;

import message.MessageHandler;
import message.RMessage;
import message.SMessage;
import net.Session;
import net.kcp.KcpOnUdp;
import net.kcp.constant.KcpConstant;
import room.FightRoom;
import room.FightRoomManager;
import thread.FightRoomProcessorManager;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.ReqEnterFight;
import org.game.protobuf.c2s.C2SFightMsg.ReqFightPing;
import org.game.protobuf.s2c.S2CFightMsg.RespFightPong;

import org.game.protobuf.c2s.C2SLoginMsg;

/**
 * 消息分发service
 */
public class KcpMessageDispatchService {
    private static final Logger LOGGER = Logger.getLogger(KcpMessageDispatchService.class);

    /**
     * 服务器功能枚举
     */
    public static class FunEnum {
        /** 核心功能 */
        public static final int CORE_FUNCTION = 1;
    }


    public void dispatchMessage(MessageHandler messageHandler, KcpOnUdp kcp) {
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
                Transfer.send(kcp, null, respFightPongBuilder);
            }
            long fightRoomId = 0;
            if (msgId == C2SFightMsg.ReqEnterFight.MsgID.eMsgID_VALUE) {
                C2SFightMsg.ReqEnterFight reqEnterFight = (ReqEnterFight) message.getData();
                String fightIdStr = reqEnterFight.getFightId();
                fightRoomId = Long.parseLong(fightIdStr);
            } else {
                Object value = kcp.getSessionValue(KcpConstant.FIGHT_ROOM);
                if(value != null) {
                    fightRoomId = (long) value;
                }
            }

            FightRoom fightRoom = FightRoomManager.getRoomByRoomId(fightRoomId);
            if (fightRoom == null || fightRoom.isDestroy()) {
                return;
            }
            
            Session session = new Session();
            session.setKcp(true);
            session.setKcpOnUdp(kcp);
            session.setFightRoom(fightRoom);
            messageHandler.setGameData(session);
            FightRoomProcessorManager.getInstance().addCommand(fightRoom.getProcessorId(),
                    messageHandler);   
        } else {
            LOGGER.error("未分发处理的Handler: " + messageHandler);
        }
    }

    public static KcpMessageDispatchService getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;
        KcpMessageDispatchService processor;

        Singleton() {
            this.processor = new KcpMessageDispatchService();
        }

        KcpMessageDispatchService getProcessor() {
            return processor;
        }
    }
}