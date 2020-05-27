package server;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import com.IpUtil;
import com.ServerInfo;
import com.ServerListManager;
import data.GameDataManager;
import kafka.service.F2GProductService;
import kafka.service.G2FConsumerTeamService;
import kafka.service.G2FCreateConsumerService;
import kafka.team.action.TeamActionManager;
import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;
import message.MessageHandlerFactory;
import message.MessageResClazz;
import net.http.HttpServer;
import net.kcp.KcpServer;
import net.tcp.ExternalTcpServer;
import redis.FightRedisOper;
import redis.service.ERedisType;
import redis.service.RedisServices;
import room.FightRoomManager;
import script.ScriptManager;
import thread.CreateTeamProcessor;
import thread.FightRoomPrepareProcessor;
import thread.FightRoomProcessorManager;
import thread.G2FConsumerTeamProcessor;
import thread.G2FCreateConsumerProcessor;
import thread.SingleMatchTeamProcessor;
import thread.TeamProcessorManager;
import utils.ClassScanUtils;
import utils.CsvUtils;
import utils.ExceptionEx;
import utils.NumEx;
import utils.javaManagement.GarbageCollectorInfo;
import utils.javaManagement.MemoryInformation;
import utils.snowflake.IDGenerator;

public class FightServer extends BaseServer {
    private final static Logger LOGGER = Logger.getLogger(FightServer.class);
    public final static int MESSAGE_HANDLER_SIZE = 100;

    /** 是否已经在关闭服务器 */
    private volatile boolean isShutDown = false;
    /**
     * 服务器启动时间
     */
    private long startTime;
    /**
     * 重载配置表时间
     */
    private long reloadGameDataTime;
    /**
     * 重载脚本时间
     */
    private long reloadJavaScriptTime;

    /**
     * 停服钩子
     */
    private Thread hook;

    private HttpServer httpServer;
    /**
     * 战斗转发用的kcp
     */
    private KcpServer kcpServer;
    /**
     * 战斗服转发tcp监听
     */
    private ExternalTcpServer externalTcpServer;
    /** 
     * 服务器内网ip
     */
    private String localIP;
    
    /** 计数器 */
    public ScheduledExecutorService printThread;
    /** 收到的帧数据 */
    public static AtomicLong RECEIVE_FRAME_DATA = new AtomicLong(0);
    /** 已同步的帧数据 */
    public static AtomicLong NOTIFY_FRAME_DATA = new AtomicLong(0);
    /** 上次收到的帧数据 */
    public static long LAST_RECEIVE_FRAME_DATA;
    /** 上次已同步的帧数据 */
    public static long LAST_NOTIFY_FRAME_DATA;

    @Override
    public boolean isTestServer() {
        boolean isTestServer = ServerConfig.getInstance().getIsTestServer();
        if (isTestServer) {
            return isTestServer;
        } else {
            if (isIDEMode()) {
                return true;
            }
            return isRootDrangServer();
        }
    }


    public long getStartTime() {
        return startTime;
    }

    public void start(String[] args) {
        try {
            // 初始化日志配置
            super.start();
            
            // 初始化服务器配置
            loadServerConfig();
            LOGGER.info("begin init server");
            // 获取本服serverId
            localIP = IpUtil.getLocalIP();// 本服内网ip
            if (localIP == null || localIP.isEmpty()) {
                throw new Exception("can not find local ip");
            }
            int tcpPort = ServerConfig.getInstance().getClientListenPort();
            boolean isSucess = ServerListManager.initServerListManager(
                    ServerConfig.getInstance().getInitServerUrl(), localIP, tcpPort);
            if (!isSucess) {
                LOGGER.error("init server error. request LoginServer failed");
                System.exit(-1);
                return;
            }
            ServerInfo info = ServerListManager.getMyServerInfo();
            int serverId = info.getId();
            // 分组id
            boolean isTest = info.getIsTest() == 1;
            ServerConfig.getInstance().setTestServer(isTest);
            ServerConfig.getInstance().setCsvUrl(info.getCsvUrl());
            ServerConfig.getInstance().setServerGroup(info.getServerGroup());
            ServerConfig.getInstance().setServerId(serverId);
            ServerConfig.getInstance().setExternalIp(info.getGameServerExternalIp());
            ServerConfig.getInstance().setMaxFightNum(info.getMaxOnlineNum());
            LOGGER.info("end init server. serverId:" + serverId + " serverName:" + info.getName()
                    + " ip:" + localIP + " port:" + tcpPort + " groupId:" + info.getServerGroup());
            
            // 初始化handler工厂
            loadAndInitMessage();
            loadResMessage();

            // 自动检测下载csv文件
            CsvUtils.init();

            // 初始化脚本系统
            ScriptManager.getInstance().initialize("./scripts", "./scriptBin", isIDEMode());
            setReloadJavaScriptTime(System.currentTimeMillis());
            
            // 游戏客户端监听(暂时不用kcp)
//            int coreSize = Runtime.getRuntime().availableProcessors() / 2;
//            if (coreSize < 1) {
//                coreSize = 1;
//            }
//            kcpServer = new KcpServer(ServerConfig.getInstance().getClientListenPort(), coreSize);
//            kcpServer.noDelay(1, 10, 2, 1);
//            kcpServer.setMinRto(10);
//            kcpServer.wndSize(128, 128);
//            kcpServer.setTimeout(ServerConfig.getInstance().getClientChannelIdleTime());
//            kcpServer.setMtu(512);
//            kcpServer.start();

            startClientServerListener();

            // 启动HTTP服务
            _httpServerStart();

            // 初始化IDGenerator生成器
            IDGenerator.getDefault().initIdWorker(serverId);
            // KAFKA初始化
            TeamActionManager.getDefault().init();
            G2FConsumerTeamService.getDefault();
            G2FCreateConsumerService.getDefault();
            F2GProductService.getDefault();
            G2FConsumerTeamProcessor.getDefault();
            G2FCreateConsumerProcessor.getDefault();

            // 初始化redis
            new RedisServices(ERedisType.Fight.getType(), "jedisPool", 1,
                    "file:./config/redis_fight_context.xml");
            // RedisService redisService = RedisService.getRedisService(ERedisType.Fight.getType());


            // 房间准备线程
            FightRoomPrepareProcessor.getInstance();
            // 房间线程
            FightRoomProcessorManager.getInstance().registerRoomLineTickTimer(ServerConfig.getInstance().getTickInterval(), ServerConfig.getInstance().getTickInterval(), false);
            // 队伍线程
            TeamProcessorManager.getInstance().registerRoomLineTickTimer(2000, 2000, false);
            // 单人匹配线程
            SingleMatchTeamProcessor.getDefault().registerTickTimer(1000, 1000, false);
            // 创建队伍线程
            CreateTeamProcessor.getDefault().registerTickTimer(10, 10, false);

            this.startTime = System.currentTimeMillis();

            LOGGER.info("server client port:" + ServerConfig.getInstance().getClientListenPort()
                    + " currentThread: " + Thread.currentThread().getName());

            // 停服hook 支持脚本替换
            Thread hook = new Thread(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            });
            // 脚本
            addShutdownHook(hook);


            // 详细定时任务打印
            // 打印器
            if(isRootDrangServer()) {
                printThread = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("printThread");
                        return thread;
                    }
                });

                printThread.scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        Runtime runtime = Runtime.getRuntime();
                        long freeMem = runtime.freeMemory();
                        long totalMemory = runtime.totalMemory();
                        long tMemory = ((totalMemory) / (1024 * 1024));
                        long fMemory = ((freeMem) / (1024 * 1024));
                        long uMemory = tMemory - fMemory;

                        String name = ManagementFactory.getRuntimeMXBean().getName();
                        String pid = name.split("@")[0];

                        MemoryInformation memoryInformation = new MemoryInformation();
                        GarbageCollectorInfo collectorInfo = new GarbageCollectorInfo();
                        collectorInfo.collectGC();
                        long fgctime = collectorInfo.getM_lastFullgcCount();
                        long ygctime = collectorInfo.getM_lastYounggcCount();

                        try {
                            LOGGER.info(new StringBuilder()
                                    .append("pid:" + pid + " totalMemory:[" + tMemory + "]MB"
                                            + " freeMemory:[" + fMemory + "]MB" + " usedMemory:["
                                            + uMemory + "]MB")
                                    .append(" directByteSize:["
                                            + Arrays.toString(MemoryInformation.dirMemForBits())
                                            + "]MB")
                                    .append(" directNettySize:["
                                            + (MemoryInformation.nettyDirectMem())
                                            + "]MB")
                                    .append(" nonHeapSize:["
                                            + memoryInformation.getUsedNonHeapMemory() + "]MB")
                                    .append(" ygcTimes:" + ygctime + " cms(full)GC:" + fgctime)
                                    .append(" fightPlayers:" + FightRoomManager.roleIdRooms.size())
                                    .append(" recvSum:" + RECEIVE_FRAME_DATA.get() + ",sendSum:"
                                            + NOTIFY_FRAME_DATA.get() + ",recvCount:"
                                            + (RECEIVE_FRAME_DATA.get() - LAST_RECEIVE_FRAME_DATA)
                                            + ",sendCount:"
                                            + (NOTIFY_FRAME_DATA.get() - LAST_NOTIFY_FRAME_DATA))
                                    .toString());
                            LAST_RECEIVE_FRAME_DATA = RECEIVE_FRAME_DATA.get();
                            LAST_NOTIFY_FRAME_DATA = NOTIFY_FRAME_DATA.get();
                        } catch (Exception e) {
                            LOGGER.error(ExceptionEx.e2s(e));
                        }
                    }
                }, 0, 1000 * 1, TimeUnit.MILLISECONDS);
            }
            System.gc();

            // 该打印内容不能修改 运维依赖 并且必须放在这里
            super.startEnd();

            // 检查是否开启控制台必须放在最后面,开启后主线程进入循环等待
            _checkConsole();
        } catch (Exception e) {
            LOGGER.error("start server exception.");
            LOGGER.error(STARTFIELD + ExceptionEx.e2s(e));
            System.exit(-1);
        }
    }

    /**
     * 启动监听
     * 
     * @throws Exception
     */
    private void startClientServerListener() throws Exception {
        externalTcpServer = new ExternalTcpServer();
        int defaultBossThread = Runtime.getRuntime().availableProcessors() <= 4 ? 1 : 2;
        int defaultWorkThread = Runtime.getRuntime().availableProcessors() <= 4 ? 2
                : Runtime.getRuntime().availableProcessors() / 4;
        externalTcpServer.start(ServerConfig.getInstance().getClientListenPort(),
                ServerConfig.getInstance().getClientChannelIdleTime(), defaultBossThread, defaultWorkThread);
    }

    private void _httpServerStart() throws Exception {
        httpServer = new HttpServer();
        httpServer.start(ServerConfig.getInstance().getHttpListenPort());
    }

    private void loadServerConfig() throws Exception {
        String serverCfgPath = getConfigPath() + "server_config.xml";
        ServerConfig.getInstance().load(serverCfgPath);
    }

    /**
     * 加载消息文件并且初始化Handler factory
     * 
     * @throws Exception
     */
    private void loadAndInitMessage() throws Exception {
        LOGGER.info("begin load recv msg.");
        Collection<Class<?>> classes = ClassScanUtils.scanPackages("logic");
        int count = 0;
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(MHandler.class)) {
                MHandler _protoHandler = clazz.getDeclaredAnnotation(MHandler.class);
                MessageHandlerFactory.getInstance().initMessageHandler(_protoHandler.messageClazz(),
                        (Class<MessageHandler>) clazz, MESSAGE_HANDLER_SIZE);
                count++;
            }
        }
        LOGGER.info("end load recv msg. count:" + count);
    }

    /**
     * 加载respone消息id序列
     * 
     * @throws Exception
     */
    private void loadResMessage() throws Exception {
        LOGGER.info("begin load respone msg.");
        Collection<Class<?>> classes = ClassScanUtils.scanPackages("org.game.protobuf");
        int count = 0;
        for (Class<?> clazz : classes) {
            MessageResClazz.getInstance().init(clazz);
            count++;
        }
        LOGGER.info("end load respone msg. count:" + count);
    }

    /**
     * 注册JVM退出hook
     */
    public void addShutdownHook(Thread thread) {
        hook = thread;
        Runtime.getRuntime().addShutdownHook(hook);
    }

    /**
     * 移除JVM退出hook
     */
    public void removeShutdownHook() {
        if (hook != null) {
            Runtime.getRuntime().removeShutdownHook(hook);
        }
    }

    /** 是否正在关服 */
    public boolean isShutDown() {
        return isShutDown;
    }

    /** 设置关服 */
    public void setShutDown(boolean isShutDown) {
        this.isShutDown = isShutDown;
    }

    @Override
    public void stop() {
        LogicScriptsUtils.getShutDownScript().stop();
    }

    public String[] getArgs() {
        return null;
    }

    /**
     * 检查是否开启控制台
     */
    private void _checkConsole() {
        if (isIDEMode()) {
            LOGGER.info("IDE Mode ：Console Opened");
            Scanner scanner = new Scanner(System.in);
            boolean exit = false;
            while (exit != true) {
                try {
                    String input = scanner.next();
                    _runCommand(input, scanner);
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
            scanner.close();
        }
    }

    /**
     * 执行命令
     * 
     * @param input
     * @param scanner
     * @throws Exception
     */
    private void _runCommand(String input, Scanner scanner) throws Exception {
        if (input.equalsIgnoreCase("ras")) {
            ScriptManager.getInstance().loadAllScript();
        } else if (input.equalsIgnoreCase("rss")) {
            String param = scanner.next();
            if (NumEx.isInt(param)) {
                ScriptManager.getInstance().loadScriptBean(Integer.valueOf(param));
            } else {
                ScriptManager.getInstance().loadScript(param);
            }
        } else if (input.equals("rdata")) {
            GameDataManager.Ainit();
        } else if (input.equalsIgnoreCase("stop")) {
            stop();
        } else if (input.equalsIgnoreCase("test")) {
            List<String> list = FightRedisOper.match("testfight", 999999999l, 3);
            LOGGER.info(list);
        } else if (input.equalsIgnoreCase("drop")) {
        } else {
            LOGGER.info("不能识别的命令: " + input);
        }
    }


    /**
     * 获取FightServer的实例对象.
     * 
     * @return
     */
    public static FightServer getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;

        FightServer instance;

        Singleton() {
            this.instance = new FightServer();
        }

        FightServer getInstance() {
            return instance;
        }
    }

    public long getReloadGameDataTime() {
        return reloadGameDataTime;
    }
    
    public void setReloadGameDataTime(long reloadGameDataTime) {
        this.reloadGameDataTime = reloadGameDataTime;
    }
    
    public long getReloadJavaScriptTime() {
        return reloadJavaScriptTime;
    }

    public void setReloadJavaScriptTime(long reloadJavaScriptTime) {
        this.reloadJavaScriptTime = reloadJavaScriptTime;
    }
}
