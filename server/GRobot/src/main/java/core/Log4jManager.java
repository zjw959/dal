package core;


import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import UI.window.frame.MainWindow;
import core.robot.GRobotManager;
import core.robot.RobotThread;
import utils.ExceptionEx;
import utils.javaManagement.GarbageCollectorInfo;

public class Log4jManager {
    public static boolean netDetail = true;
    // TODO 消息是否出错了,有没有错误返回码
    public static boolean netSucc = true;
    public static boolean netClose = true;


    static long lastsendMsgs = -2;
    static long llastsendMsgs = -1;

    public static Log4jManager getInstance() {
        return _instance;
    }

    public void init() {
        try {
            CLIENT = Logger.getLogger("Client");
        } catch (Exception e) {
            System.out.println(ExceptionEx.e2s(e));
            System.exit(1);
        }
    }

    public boolean isInit() {
        return (CLIENT == null ? false : true);
    }

    public void setLevelInfo() {
        if (CLIENT == null) {
            return;
        }
        CLIENT.setLevel(Level.INFO);
    }

    public void setLevelWar() {
        if (CLIENT == null) {
            return;
        }
        CLIENT.setLevel(Level.WARN);
    }

    public void setLevelDebug() {
        if (CLIENT == null) {
            return;
        }
        CLIENT.setLevel(Level.DEBUG);
    }

    public boolean isDebugEnabled() {
        if (CLIENT == null) {
            return false;
        }
        return CLIENT.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        if (CLIENT == null) {
            return false;
        }
        return CLIENT.isInfoEnabled();
    }

    public void debug(MainWindow window, String str) {
        if (CLIENT == null) {
            System.out.println(str);
        } else {
            CLIENT.debug(str);
        }
        window.getConsolePanel().addConsoleAreaInfo("debug:" + str, true);
    }

    public void info(MainWindow window, String str) {
        if (CLIENT == null) {
            System.out.println(str);
        } else {
            CLIENT.info(str);
        }
        window.getConsolePanel().addConsoleAreaInfo(str, false);
    }

    public void warn(String str) {
        warn(null, str);
    }

    public void warn(MainWindow window, String str) {
        if (CLIENT == null) {
            System.out.println(str);
        } else {
            CLIENT.error(str);
        }
        if (window != null) {
            window.getConsolePanel().addConsoleAreaInfo("警告:" + str, false);
        }
    }

    public void error(Exception e) {
        error(null, ExceptionEx.e2s(e));
    }

    public void error(String str) {
        error(null, str);
    }

    public void error(MainWindow window, Exception e) {
        error(window, ExceptionEx.e2s(e));
    }

    public void error(MainWindow window, String str) {
        if (CLIENT == null) {
            System.out.println(str);
        } else {
            CLIENT.error(str);
        }
        if (window != null) {
            window.getConsolePanel().addConsoleAreaInfo("错误:" + str, false);
        }
    }

    static class PrintUtil {
        static long sendLogicMsgs = 0;
        static long sendMsgs = 0;
        static long recvMsgs = 0;
        
        static long chasmSendMsgs = 0;
        static long chasmCompleteSendMsgs = 0;
        static long chasmRecvMsgs = 0;
    }

    public static void logExecute(MainWindow window) {
        scheduledSinglePoolLog = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("scheduledSinglePoolMin1");
                return thread;
            }
        });

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    if (window.getConsolePanel().isChasmCheckBox()) {
                        // 打印深渊关卡(组队副本日志)
                        long chasmSendMsgs = window.chasmSendMsgs.get();
                        long chasmCompleteSendMsgs = window.chasmCompleteSendMsgs.get();
                        long chasmReceiveMsgs = window.chasmReceiveMsgs.get();
                        
                        long _temp = chasmSendMsgs;
                        long _temp2 = chasmCompleteSendMsgs;
                        long _temp3 = chasmReceiveMsgs;
                        chasmSendMsgs = chasmSendMsgs - PrintUtil.chasmSendMsgs;
                        chasmCompleteSendMsgs = chasmCompleteSendMsgs - PrintUtil.chasmCompleteSendMsgs;
                        chasmReceiveMsgs = chasmReceiveMsgs - PrintUtil.chasmRecvMsgs;

                        PrintUtil.chasmSendMsgs = _temp;
                        PrintUtil.chasmCompleteSendMsgs = _temp2;
                        PrintUtil.chasmRecvMsgs = _temp3;
                        
                        String info = "组队副本压测,累积创建:" + window.getConnectionTots() + ",当前连接:"
                                + window.getNowConnections() + ",当前登陆:" + window.getLogined() + ";队列发送帧数据:"
                                + chasmSendMsgs + ",实际发送帧数据:"
                                + chasmCompleteSendMsgs + ",接收帧数据:" + chasmReceiveMsgs;
                        
                        Log4jManager.getInstance().info(window, info);
                    } else {
                        long sendLogicMsgs = window.getSendLogicMsgs();
                        long sendMsgs = window.getSendMsgs();
                        long recvMsgs = window.getReceiveMsgs();
                        
                        if (window.getConsolePanel().isShowDelta()) {
                            long _temp = sendLogicMsgs;
                            long _temp2 = sendMsgs;
                            long _temp3 = recvMsgs;
                            sendLogicMsgs = sendLogicMsgs - PrintUtil.sendLogicMsgs;
                            sendMsgs = sendMsgs - PrintUtil.sendMsgs;
                            recvMsgs = recvMsgs - PrintUtil.recvMsgs;

                            PrintUtil.sendLogicMsgs = _temp;
                            PrintUtil.sendMsgs = _temp2;
                            PrintUtil.recvMsgs = _temp3;
                        }

                        Runtime runtime = Runtime.getRuntime();
                        long freeMem = runtime.freeMemory();
                        long totalMemory = runtime.totalMemory();
                        long tMemory = ((totalMemory) / (1024 * 1024));
                        long fMemory = ((freeMem) / (1024 * 1024));
                        long uMemory = tMemory - fMemory;

                        GarbageCollectorInfo collectorInfo = new GarbageCollectorInfo();
                        collectorInfo.collectGC();
                        long fgctime = collectorInfo.getM_lastFullgcCount();

                        String info = "totalMemory:[" + tMemory + "]MB"
                                + " freeMemory:[" + fMemory + "]MB" + " usedMemory:["
                                + uMemory + "]MB cms(full)GC:" + fgctime + "\n累积创建:"
                                + window.getConnectionTots() + ",当前连接:"
                                + window.getNowConnections() + ",当前登陆:" + window.getLogined() + ";队列发送:"
                                + sendLogicMsgs + ",实际发送:"
                                + sendMsgs + ",接收数据:" + recvMsgs;

                        boolean isStop = false;
                        if (window.getConsolePanel().isShowDelta()) {
                            if (lastsendMsgs == 0 && (lastsendMsgs == llastsendMsgs)
                                    && (llastsendMsgs == sendMsgs)) {
                                isStop = true;
                            }
                        } else {
                            if ((lastsendMsgs == llastsendMsgs) && (llastsendMsgs == sendMsgs)) {
                                isStop = true;
                            }
                        }

                        if (isStop) {
                            List<RobotThread> list = GRobotManager.instance().getRobots();
                            String funs = "";
                            for (RobotThread robotThread : list) {
                                funs += robotThread.currentEvent.getFunctionInfo() + ",";
                            }

//                            Log4jManager.getInstance().error(window,
//                                    "客户端流程卡住,连续3秒发送量没有变化,当前机器人功能:" + funs + "...");
                        }

                        llastsendMsgs = lastsendMsgs;
                        lastsendMsgs = sendMsgs;

                        Log4jManager.getInstance().info(window, info);
                    }
                } catch (Exception e) {
                    Log4jManager.getInstance().error(window, e);
                }
            }
        };

        scheduledSinglePoolLog.scheduleWithFixedDelay(r, 0, 1000 * 1, TimeUnit.MILLISECONDS);
    }

    public static void stopLogExecute() {
        scheduledSinglePoolLog.shutdown();
        while (!scheduledSinglePoolLog.isTerminated()) {

        }
    }

    // ----------------------------------------------------------------

    static ScheduledExecutorService scheduledSinglePoolLog;

    private static Log4jManager _instance = new Log4jManager();
    private static Logger CLIENT;

    private Log4jManager() {

    }


}
