package UI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import UI.window.frame.FunctionWindow;
import UI.window.frame.MainWindow;
import UI.window.frame.MessageWindow;
import conf.RunConf;
import core.Log4jManager;
import core.net.connect.GClient;
import core.net.kcp.KcpClient;
import core.net.kcp.KcpCodec;
import core.net.message.SMessage;
import core.robot.GRobotManager;
import core.robot.RobotThread;
import core.robot.RobotThreadFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.kcp.KcpOnUdp;
import net.kcp.constant.KcpConstant;
import utils.MiscUtils;

/**
 * @function 运行功能测试
 */
public class RunFunctionListener implements ActionListener {

    public RunFunctionListener(FunctionWindow window) {
        this.window = window;
    }

    public RunFunctionListener(MessageWindow window) {
        this.window = window;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // if (event.getSource() == window.getRunButton()) {
        window.getRunButton().setEnabled(false);
        backPool.execute(new Runnable() {
            public void run() {
                try {
                    if (isShutDown()) {
                        synchronized (stopLock) {
                            window.getConsolePanel().addConsoleAreaInfo("开始运行...", false);
                            GClient.init();
                            window.getRunButton().setText("停止运行");
                            window.setAlltoZero();
                            window.getRunButton().setEnabled(true);
                            Log4jManager.logExecute(window);
                        }
                        new RunT().run();
                    } else {
                        synchronized (stopLock) {
                            window.getConsolePanel().addConsoleAreaInfo("开始停止运行...", false);
                            if (sendMsgPool != null) {
                                sendMsgPool.shutdown();
                                if (!sendMsgPool.isTerminated()) {
                                    Log4jManager.getInstance().info(window, "等待停止运行机器人发送线程...");
                                    Thread.sleep(1000);
                                }
                                sendMsgPool = null;
                            } else {
                                Log4jManager.getInstance().error(window, "sendMsgPool is null !");
                            }

                            if (runRobotPool != null) {
                                runRobotPool.shutdown();
                                if (!runRobotPool.isTerminated()) {
                                    Log4jManager.getInstance().info(window, "等待停止运行机器人逻辑线程...");
                                    Thread.sleep(1000);
                                }
                                runRobotPool = null;
                            } else {
                                Log4jManager.getInstance().error(window, "runRobotPool is null !");
                            }

                            Log4jManager.getInstance().info(window, "socket开始停止...");
                            GClient.group.shutdownGracefully().sync();
                            GClient.bootstrap = null;
                            Log4jManager.getInstance().info(window, "socket已停止...");
                            window.getRunButton().setText("开始运行");
                            Log4jManager.stopLogExecute();
                            Log4jManager.getInstance().info(window, "成功停止");
                            window.getRunButton().setEnabled(true);
                        }
                    }
                } catch (Exception e) {
                    Log4jManager.getInstance().error(e);
                    window.getRunButton().setEnabled(true);
                }
            }
        });
    }

    public static void runBackPool(Runnable r) {
        backPool.execute(r);
    }

    private boolean isShutDown() {
        if (window.getRunButton().getText().equals("开始运行")) {
            return true;
        }
        return false;
    }

    /**
     * 创建并启动机器人
     */
    private void robotRun() {
        try {
            window.getRobotPanel().reloadRobotConf();
            int proNum = Runtime.getRuntime().availableProcessors();
            proNum = (proNum / 2);
            if (proNum < 1) {
                proNum = 1;
            }

            sendMsgPool = Executors.newScheduledThreadPool(proNum, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("scheduledSendPool");
                    return thread;
                }
            });

            runRobotPool = Executors.newScheduledThreadPool(proNum, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("scheduledRobotRunPool");
                    return thread;
                }
            });

            // 根据当前连接数(机器人数量)动态创建机器人
            while (true) {
                synchronized (this.stopLock) {
                    if (isShutDown()) {
                        break;
                    }

                    // 创建一个机器人
                    int maxConnectedRobot = RunConf.robotConf.getRobotHoldNum();

                    if (RunConf.robotConf.getRobotMaxNum() != -1) {
                        if(maxConnectedRobot > RunConf.robotConf.getRobotMaxNum()) {
                            maxConnectedRobot = RunConf.robotConf.getRobotMaxNum();
                        }
                    }

                    if (GRobotManager.instance().connectedRobotCount() < maxConnectedRobot) {
                        boolean isSingle = window.getRobotPanel().getIsSingle();
                        RobotThread crt = RobotThreadFactory.createNewRobot(window, isSingle);
                        crt.initChannel();
                        // if (crt.initChannel()) {
                        // crt.run(false);
                        // }
                    } else if (GRobotManager.instance()
                            .connectedRobotCount() > maxConnectedRobot) {
                        int deltaCount = GRobotManager.instance().connectedRobotCount()
                                - RunConf.robotConf.getRobotHoldNum();
                        if (deltaCount > 0) {
                            // 选择最近最久没有访问的robot移除
                            GRobotManager.instance().removeRobot(deltaCount);
                        }
                    }
                    // Thread.sleep(5);
                }
            }
            Log4jManager.getInstance().info(window, "机器人创建线程已停止");
        } catch (Exception e) {
            Log4jManager.getInstance().error(e);
        }
    }


    public static void addRobotRunPool(Runnable r, long delay) {
        if (runRobotPool != null && !runRobotPool.isShutdown()) {
            if (delay < RunConf.robotConf.getSendDelayTime()) {
                delay = RunConf.robotConf.getSendDelayTime();
            }
            runRobotPool.schedule(r, delay, TimeUnit.MILLISECONDS);
        }
    }

    public static synchronized void addSendMsgPool(RobotThread robot, SMessage msg,
            Boolean isContiue) {
        if (isContiue) {
            // 依赖调用是否已经设置了跳过状态
            if (!robot.isNowSkip) {
                robot.isNowSkip = true;
                if (robot.requestMultipleEvents.size() != 1) {
                    robot.isSkipContiueCount.incrementAndGet();
                }
            }
        } else {
            if (!robot.isNowSkip) {
                robot.isSkipContiueCount.set(0);
            }
        }

        if (robot.isSkipContiueCount.get() > 3
                && !robot.currentEvent.getFunctionInfo().equals("邮件")) {
            Log4jManager.getInstance().warn(robot.getWindow(),
                    "当前功能:" + robot.currentEvent.getFunctionInfo() + " 已经连续:"
                            + robot.isSkipContiueCount.get() + "次手动跳过测试逻辑,可能存在测试漏洞.");
        }

        if (sendMsgPool != null && !sendMsgPool.isShutdown()) {
            robot.getWindow().addSendLogicMsgs();

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        robot.setResOrder(msg.getResOrder());
                        long now = System.currentTimeMillis();
                        long _c = now - robot.sendingTime;
                        if (robot.isNowSkip) {
                            // 现在是,则不检查
                            robot.sendingTime = 0;
                        }
                        // 压力测试没有严格执行发送间隔.
                        if (robot.sendingTime != 0
                                && _c < (RunConf.robotConf.getSendDelayTime() / 2)) {

                            robot.shortTimeCount++;

                            if (robot.shortTimeCount > 2) {
                                // 待定是否要移动
                            }

                            if (MiscUtils.isIDEEnvironment()) {
                            Log4jManager.getInstance().error(robot.getWindow(),
                                    "当前功能:" + robot.currentEvent.getFunctionInfo()
                                            + " 压力测试没有严格执行发送间隔. robot.sendingmsg:"
                                            + robot.sendingmsg
                                            + ",now:" + msg.getId() + ",tid:"
                                            + Thread.currentThread().getId() + ",robot"
                                            + robot.getName() + ",time:" + _c + "ms,resOrder:"
                                            + robot.getResOrder());
                            }
                        }
                        if (robot.isNowSkip) {
                            // 下次不是, 同样不检查
                            robot.sendingTime = 0;
                        } else {
                            robot.sendingTime = now;
                        }
                        robot.isNowSkip = false;
                        robot.sendingmsg = msg.getId();
                        if(robot.isKcp() && robot.getKcpClient() != null && robot.getKcpClient().isRunning()) {
                            KcpClient kcpClient = robot.getKcpClient();
                            KcpOnUdp kcp = kcpClient.getKcpOnUdp();
                            ByteBuf buf = KcpCodec.encoder(msg, (int[])kcp.getSessionValue(KcpConstant.ENCRYPTION_KEYS));
                            kcpClient.send(buf);
                            robot.getWindow().addCompleteSendMsgs();
                            if (isContiue) {
                                robot.sendingTime = 0;
                                robot.run(true);
                            }
                        } else {
                            ChannelFuture future;
                            int msgId = msg.getId();
                            if(msgId / 100 == 256) {
//                                if(!robot.isKcp() && robot.getFightChannel() != null && robot.getFightChannel().isOpen()) {
                                future = robot.getFightChannel().writeAndFlush(msg);
                            } else {
                                future = robot.getChannel().writeAndFlush(msg);
                            }
                            // Log4jManager.getInstance().info(String.valueOf(System.currentTimeMillis()));
                            future.addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    robot.getWindow().addCompleteSendMsgs();
                                    if (isContiue) {
                                        robot.sendingTime = 0;
                                        robot.run(true);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log4jManager.getInstance().error(e);
                    }
                }
            };

            // long now = System.currentTimeMillis();
            // long _c = now - robot.addTime;
            // if (robot.addTime != 0 && _c < (RunConf.robotConf.getSendDelayTime() / 2)) {
            // Log4jManager.getInstance().error(robot.getWindow(),
            // "压力测试没有严格执行发送间隔. robotaddTimeaddTime.sendingmsg:" + robot.sendingmsg
            // + ",now:" + msg.getId() + ",tid:" + Thread.currentThread().getId()
            // + ",robot" + robot.getName() + ",time:" + _c + ",resOrder:"
            // + robot.getResOrder() + ",enterTimes:" + robot.enterTimes
            // + ",sendList:" + robot.sendList.toString());
            // }
            // if (robot.isNowSkip) {
            // robot.addTime = 0;
            // } else {
            // robot.addTime = now;
            // }
            robot.sendList.add(msg.getId());
            sendMsgPool.schedule(r, RunConf.robotConf.getSendDelayTime(), TimeUnit.MILLISECONDS);
        }
    }

    public static boolean isNull() {
        return (sendMsgPool == null);
    }

    /**
     * @function 运行线程
     */
    private class RunT implements Runnable {
        @Override
        public void run() {
            robotRun();
        }
    }

    private MainWindow window;

    /**
     * 关闭按钮同步锁
     **/
    private Object stopLock = new Object();

    /**
     * 后台线程(创建机器人 以及 UI)
     **/
    private static ExecutorService backPool = Executors.newFixedThreadPool(2);

    /**
     * 机器人线程
     * 
     * 用于处理一些特殊的定时逻辑
     **/
    private static ScheduledExecutorService runRobotPool;

    /**
     * 发送线程
     **/
    private static ScheduledExecutorService sendMsgPool;

}
