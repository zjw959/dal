package javascript.logic;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.AsyncAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.EScriptIdDefine;
import logic.constant.EServerStatus;
import logic.gloabl.GlobalService;
import logic.login.service.LoginCheckService;
import logic.login.service.LoginService;
import logic.pay.PayService;
import script.ILifeCycle;
import server.GameServer;
import server.IShutDownScript;
import thread.log.LogProcessor;
import thread.player.PlayerDBProcessorManager;
import thread.player.PlayerProcessorManager;
import thread.sys.base.SysFunProcessorManager;
import thread.timer.TimeEventProcessor;
import utils.ExceptionEx;

public class ShutdownHookScript implements IShutDownScript, ILifeCycle, Runnable {
    private static final Logger LOGGER = Logger.getLogger(ShutdownHookScript.class);

    @Override
    public void init() {}

    @Override
    public void destroy() {}

    @Override
    public int getScriptId() {
        return EScriptIdDefine.SHUTDOWN_SCRIPT.Value();
    }

    @Override
    public void rest() {
        // 移除之前的hook
        GameServer.getInstance().removeShutdownHook();
        // 添加新的hook
        GameServer.getInstance().addShutdownHook(new Thread(this));
    }

    @Override
    public void stop() {
        run();
    }

    @Override
    public void run() {
        LOGGER.info("ShutdownHookScript---停服脚本-----begin-------------");
        LOGGER.info("JVM exit, call GameServer.stop");
        // 先改变服务器状态为维护状态并踢玩家下线,防止玩家再次登录
        // 设置服务器状态
        GameServer.getInstance().setStatus(EServerStatus.STOPPING);
        _closeServerNotify(true);
        try {
            // 等待netty线程多跑一段时间.等待断开逻辑处理
            long begin = System.currentTimeMillis();
            LOGGER.info("JVM exit, call GameServer.stop threadSleepStart, now:" + begin);
            if (GameServer.getInstance().isIDEMode()) {
                Thread.sleep(2000);
            } else if (GameServer.getInstance().isTestServer()) {
                Thread.sleep(5000);
            } else {
                Thread.sleep(60 * 1000);
            }
            LOGGER.info("JVM exit, call GameServer.stop threadSleepStop, stop:"
                    + (System.currentTimeMillis() - begin) / 1000);

            // {
            // // 关闭tcp监听服务
            // LOGGER.info("stop TcpServer...");
            // GameServer.getInstance().get_externalTcpServer().stop();
            // }

            {
                // 停止所有的定时器
                LOGGER.info("stop timer ...");
                TimeEventProcessor.getInstance().stop();
            }

            {
                // http服务器停止
                LOGGER.info("stop and await GameHttpServer...");
                GameServer.getInstance().httpServerStop();
            }

            // {
            // // 停止消息分发线程
            // LOGGER.info("stop and await DispatchProcessor...");
            // MessageDispatchService.getInstance().getProcess().stop();
            // }

            {
                // 登陆验证线程
                LOGGER.info("stop and await LoginCheckProcessor...");
                LoginCheckService.getInstance().getProcess().stop();
            }

            {
                // 登陆线程
                LOGGER.info("stop and await LoginProcessor...");
                LoginService.getInstance().getProcess().stop();
            }


            {
                // 充值线程
                PayService.getInstance().getProcess().stop();
            }

            {
                // 全局表回存
                GlobalService.getInstance().getProcess().stop();
                LOGGER.info("save GlobalService...");
            }

            LOGGER.info("save all Sys....");
            SysFunProcessorManager.getInstance().save();

            {
                // 停止玩家逻辑线程
                LOGGER.info("stop and await GameLineManager...");
                PlayerProcessorManager.getInstance().stop();
            }

        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        } finally {
            // 关闭玩家数据回存线程
            LOGGER.info("stop and await PlayerRestoreProcessor ...");
            PlayerDBProcessorManager.getInstance().stop();

            {
                // 注意：日志处理线程需要最后关闭
                LOGGER.info("stop and await BackLogProcessor...");
                LogProcessor.getInstance().stop();

                LOGGER.info("stop and await LogManager...");
                LogManager.shutdown();
                LOGGER.info("stop LogManager...");
            }

            try {
                // 确保停服日志写入
                Logger logger = Logger.getRootLogger();
                // 关闭日志.写入磁盘缓存中
                AsyncAppender appender =
                        ((org.apache.log4j.AsyncAppender) logger.getAppender("AsyncFileAppender"));
                DailyRollingFileAppender fileAppender =
                        (org.apache.log4j.DailyRollingFileAppender) appender
                                .getAppender("FileAppender");

                fileAppender.setImmediateFlush(true);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }

            LOGGER.info("ShutdownHookScript---------end------------");
        }
    }

    /**
     * 停服通知 (此方法主要目的是希望在关服前能尽量保证通知到所有玩家)
     * 
     * @param stopImi 是否是立即停服
     */
    public void _closeServerNotify(boolean stopImi) {
        try {
            // 先通知停服
            List<Player> playerList = PlayerManager.getAllPlayers();

            AtomicInteger closeNum_stop = new AtomicInteger(0);
            AtomicInteger closeNum_change = new AtomicInteger(0);

            if (playerList != null && !playerList.isEmpty()) {
                int totalNum = 0;
                for (Player player : playerList) {
                    if (player.isOnline() && player.getCtx() != null
                            && player.getCtx().channel().isOpen()
                            && player.getCtx().channel().isActive()) {
                        ++totalNum;
                    }
                }

                if (stopImi)
                    // 设置计数总数(关服)
                    closeNum_stop.set(totalNum);
                else
                    // 设置计数总数(改变服务器状态)
                    closeNum_change.set(totalNum);

                for (Player player : playerList) {
                    if (!player.isOnline()) {
                        continue;
                    }

                    if (player.getCtx() != null && player.getCtx().channel().isOpen()
                            && player.getCtx().channel().isActive()) {
                        LoginService.sendForceOffline(player.getCtx());
                        ChannelFuture future = player.getCtx().close();
                        future.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (stopImi) {
                                    closeNum_stop.decrementAndGet();
                                } else {
                                    closeNum_change.decrementAndGet();
                                }
                                future.removeListener(this);
                            }
                        });
                    }
                }
            }
            // 再定时检测，以确保消息送达
            int timeout = 1 * 60 * 1000;
            long time = System.currentTimeMillis();
            if (GameServer.getInstance().isIDEMode()) {
                timeout = 5 * 1000;
            } else if (GameServer.getInstance().isTestServer()
                    && !GameServer.getInstance().isRootDrangServer()) {
                timeout = 2 * 1000;
            }
            while (true) {
                // 每隔指定时间检查一次
                Thread.sleep(1000);
                boolean isTimeOut = System.currentTimeMillis() - time > timeout;
                boolean isStopOver = closeNum_stop.intValue() <= 0;
                boolean isChangeOver = closeNum_change.intValue() <= 0;
                boolean isAllSessionClose = stopImi ? isStopOver : isChangeOver;
                // 所有玩家连接关闭或者超时就跳出
                if (isTimeOut || isAllSessionClose) {
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }
}
