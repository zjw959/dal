package logic.chasmfight.unlimited;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.game.protobuf.c2s.C2SFightMsg;

import core.net.kcp.KcpClient;
import core.net.kcp.KcpCodec;
import core.net.message.SMessage;
import core.robot.RobotThread;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.kcp.KcpOnUdp;
import net.kcp.constant.KcpConstant;
import thread.HandlerThreadFactory;

public class SendMsgService {
    public static Map<String, RobotThread> unlimitedMsg = new ConcurrentHashMap<>();
    private static ScheduledExecutorService tickService =
            Executors.newSingleThreadScheduledExecutor(new HandlerThreadFactory("MsgTickThread"));
    private static ExecutorService sendSevicel;

    static {
        int proNum = Runtime.getRuntime().availableProcessors();
        proNum = (proNum / 2);
        if (proNum < 1) {
            proNum = 1;
        }
        sendSevicel =
                Executors.newFixedThreadPool(proNum, new HandlerThreadFactory("MsgSendThread"));
        tickService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                sendSevicel.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (Map.Entry<String, RobotThread> entry : unlimitedMsg.entrySet()) {
                            RobotThread robot = entry.getValue();
                            C2SFightMsg.ReqOperateFight.Builder reqOperateFightBuilder =
                                    C2SFightMsg.ReqOperateFight.newBuilder();
                            reqOperateFightBuilder.setKeyCode(20);
                            reqOperateFightBuilder.setKeyEvent(5);
                            SMessage msg =
                                    new SMessage(C2SFightMsg.ReqOperateFight.MsgID.eMsgID_VALUE,
                                            reqOperateFightBuilder.build().toByteArray());
                            if (robot.isKcp()) {
                                KcpClient kcpClient = robot.getKcpClient();
                                KcpOnUdp kcpOnUdp = kcpClient.getKcpOnUdp();
                                ByteBuf buf = KcpCodec.encoder(msg, (int[]) kcpOnUdp
                                        .getSessionValue(KcpConstant.ENCRYPTION_KEYS));
                                robot.getWindow().chasmSendMsgs.incrementAndGet();
                                kcpClient.send(buf);
                                robot.getWindow().chasmCompleteSendMsgs.incrementAndGet();
                            } else {
                                if(robot.getFightChannel() != null && robot.getFightChannel().isOpen()) {
                                    robot.getWindow().chasmSendMsgs.incrementAndGet();
                                    ChannelFuture future = robot.getFightChannel().writeAndFlush(msg);
                                    future.addListener(new ChannelFutureListener() {
                                        @Override
                                        public void operationComplete(ChannelFuture future)
                                                throws Exception {
                                            robot.getWindow().chasmCompleteSendMsgs.incrementAndGet();
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        }, 33, 33, TimeUnit.MILLISECONDS);
    }
}
