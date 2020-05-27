package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.ServerInfo;
import com.ServerListManager;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import db.DBFactory;
import io.netty.util.internal.SystemPropertyUtil;
import kafka.service.G2FCreateTeamProducerService;
import kafka.service.G2FProductService;
import kafka.service.G2GInviteTeamProductService;
import kafka.team.action.TeamActionManager;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.EServerStatus;
import logic.gloabl.GlobalService;
import logic.support.LogicScriptsUtils;
import logic.support.SensitiveWordFilter;
import message.MHandler;
import message.MessageHandler;
import message.MessageHandlerFactory;
import message.MessageResClazz;
import net.ExternalTcpServer;
import net.http.HttpServer;
import redis.service.ERedisType;
import redis.service.RedisServices;
import script.ScriptManager;
import thread.HandlerThreadFactory;
import thread.player.PlayerDBProcessorManager;
import thread.player.PlayerProcessorManager;
import thread.sys.CMessageNumInfoProcessor;
import thread.sys.FixBugTimerProcessor;
import thread.sys.base.SysFunProcessorManager;
import thread.timer.TimerEvent;
import utils.ClassScanUtils;
import utils.CommonUtil;
import utils.CsvUtils;
import utils.ExceptionEx;
import utils.NumEx;
import utils.javaManagement.GarbageCollectorInfo;
import utils.javaManagement.MemoryInformation;
import utils.snowflake.SnowflakeTestThread;

public class GameServer extends BaseServer {
    private final static Logger LOGGER = Logger.getLogger(GameServer.class);
    public final static int MESSAGE_HANDLER_SIZE = 100;

    /**
     * 服务器当前天时间, 用于处理跨天/时限等操作
     */
    private volatile long dayTime;

    /**
     * 服务器启动时间
     */
    private long _startTime;
    /**
     * 重载配置表时间
     */
    private long _reloadGameDataTime;
    /**
     * 重载脚本时间
     */
    private long _reloadJavaScriptTime;

    /**
     * 停服钩子
     */
    private Thread hook;

    private ExternalTcpServer _externalTcpServer;

    private HttpServer httpServer;
    private String[] args;
    private String localIP;
    /** 服务器状态 */
    private EServerStatus status;
    private GarbageCollectorInfo garbageCollectorInfo;


    @Override
    public void stop() {
        LOGGER.info("end server stop, begin.");

        LogicScriptsUtils.getShutDown().stop();

        LOGGER.info("end server stop, succeed.");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public boolean isTestServer() {
        boolean isTestServer = ServerConfig.getInstance().isTestServer();
        if (isTestServer) {
            return isTestServer;
        } else {
            if (isIDEMode()) {
                return true;
            }
            return isRootDrangServer();
        }
    }



    public GarbageCollectorInfo getGarbageCollectorInfo() {
        return this.garbageCollectorInfo;
    }


    public long getStartTime() {
        return _startTime;
    }

    public long getReloadGameDataTime() {
        return _reloadGameDataTime;
    }

    public void setReloadGameDataTime(long _reloadGameDataTime) {
        this._reloadGameDataTime = _reloadGameDataTime;
    }

    public long getReloadJavaScriptTime() {
        return _reloadJavaScriptTime;
    }

    public void setReloadJavaScriptTime(long _reloadJavaScriptTime) {
        this._reloadJavaScriptTime = _reloadJavaScriptTime;
    }

    public long getDayTime() {
        return dayTime;
    }

    public EServerStatus getStatus() {
        return status;
    }

    public void setStatus(EServerStatus status) {
        this.status = status;
    }

    public Date getDayDate() {
        return new Date(dayTime);
    }

    public void setDayTime(long dayTime) {
        this.dayTime = dayTime;
    }

    public ExternalTcpServer get_externalTcpServer() {
        return _externalTcpServer;
    }

    /**
     * 游戏的所有初始化都必须放在这里 异常不能在函数内部自行处理,必须抛出终止启动
     * 
     * @param args 第一个参数为redisview,fiend的ip地址.
     */
    public void start(String[] args) {
        // System.out.println("begin server start console");
        try {
            setStatus(EServerStatus.STARTING);
            // 初始化日志配置
            super.start();

            // System.setProperty("Log4jContextSelector",
            // "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

            // 大数据LOG4J2配置
            String path = getConfigPath() + "log4j2.xml";
            ConfigurationSource source = new ConfigurationSource(new FileInputStream(path),
                    new File(path).toURI().toURL());
            Configurator.initialize(null, source);

            if (GameServer.getInstance().isIDEMode()) {
                this.args = args;
            }

            // 初始化服务器配置
            _loadServerConfig(args);

            // 从登录服获取当前分配给本游戏服的serverId和游戏服总数量
            LOGGER.info("begin init server");
            // 获取本服serverId
            localIP = _getLocalIP();// 本服内网ip
            if (localIP == null || localIP.isEmpty()) {
                throw new Exception("can not find local ip");
            }
            int port = ServerConfig.getInstance().getClientListenPort();
            boolean b = ServerListManager.initServerListManager(
                    ServerConfig.getInstance().getInitServerUrl(), localIP, port);
            if (!b) {
                LOGGER.error("init server error. request LoginServer failed");
                System.exit(-1);
                return;
            }
            ServerInfo info = ServerListManager.getMyServerInfo();
            int serverId = info.getId();
            // 分组id
            int specialId = info.getServerGroup();
            // 服务器名
            String serverName = info.getName();
            boolean isTest = info.getIsTest() == 1;
            ServerConfig.getInstance().setIsTestServer(isTest);
            ServerConfig.getInstance().setServerId(serverId);
            ServerConfig.getInstance().setSpecialId(specialId);
            ServerConfig.getInstance().setServerName(serverName);
            ServerConfig.getInstance().setCsvUrl(info.getCsvUrl());
            ServerConfig.getInstance().setGiftCodeVerifyUrl(info.getGiftCodeVerifyUrl());
            LOGGER.info("end init server. serverId:" + serverId + " serverName:" + serverName
                    + " ip:" + localIP + " port:" + port + " groupId:" + specialId
                    + " isTestServer:" + isTest);

            // 初始化脚本系统
            ScriptManager.getInstance().initialize("./scripts", "./scriptBin", isIDEMode());
            GameServer.getInstance().setReloadJavaScriptTime(System.currentTimeMillis());

            // 初始化数据源
            {
                DBFactory.GAME_DB.getLogger();
                // DBFactory.GAME_LOG_DB.getLogger();
                gm.db.DBFactory.MAIL_DB.getLogger();
                gm.db.DBFactory.PAY_DB.getLogger();
                gm.db.DBFactory.GLOBAL_DB.getLogger();
                // 初始化redis
                ERedisType[] eRedisTypes = ERedisType.values();
                for (ERedisType eRedisType : eRedisTypes) {
                    new RedisServices(eRedisType.getType(), eRedisType.getPoolName(),
                            eRedisType.getSpringConextType().getType(),
                            eRedisType.getSpringConextType().getPath());
                }
            }

            this.dayTime = System.currentTimeMillis();
            this._startTime = System.currentTimeMillis();


            GlobalService.getInstance().init();
            // 初始化handler工厂
            _loadAndInitMessage();
            _loadResMessage();
            // 初始化用户manager
            Player.initFunManager();

            LOGGER.info("begin init kafka");

            // TODO 现在为手动初始化,修改为自动扫描
            TeamActionManager.getDefault().init();
            G2FCreateTeamProducerService.getDefault();
            G2GInviteTeamProductService.getDefault();
            G2FProductService.getDefault();

            LOGGER.info("end init kafka");

            // 自动检测下载csv文件并加载数据
            CsvUtils.init();


            // 独立系统线程 包括全局线程(全局timer)
            SysFunProcessorManager.getInstance().initialize();

            // 初始化玩家线程
            PlayerProcessorManager.getInstance();
            PlayerDBProcessorManager.getInstance();

            // 初始化服务
            SensitiveWordFilter.getInstance().initSensitiveWords();

            // 初始化 全局内容(全局表,全局数据,全局Timer)

            // 初始化各种活动

            // 异步HTTP服务

            // 停服hook 支持脚本替换
            Thread hook = new Thread(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            });
            // 脚本
            addShutdownHook(hook);

            int _time = 1000;
            if (!isTestServer()) {
                _time = 30000;
            }

            // 详细定时任务打印
            CMessageNumInfoProcessor.getInstance().addTimer(new TimerEvent(_time, _time, true) {
                @Override
                public void run() {
                    LogicScriptsUtils.getICommonScript().printSysInfo();
                }
            });
            
            FixBugTimerProcessor.getInstance().addTimer(new TimerEvent(1000, 1000, true) {
                @Override
                public void run() {
                    LogicScriptsUtils.getIFixScript().second();
                }
            });
            
            FixBugTimerProcessor.getInstance().addTimer(new TimerEvent(6000, 6000, true) {
                @Override
                public void run() {
                    LogicScriptsUtils.getIFixScript().minute();
                }
            });

            FixBugTimerProcessor.getInstance().addTimer(new TimerEvent(6000 * 60, 6000 * 60, true) {
                @Override
                public void run() {
                    LogicScriptsUtils.getIFixScript().hour();
                }
            });

            LOGGER.info("GameServer load player from DB ------------BEGIN------------");
            // 从数据库异步加载每个等级玩家数据
            DiscreteDataCfgBean discreteData =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAX_LVL_CONFIG);
            int maxLvl = Integer.parseInt(discreteData.getData().get("pmaxlvl").toString());
            int _coreSize = Runtime.getRuntime().availableProcessors();
            _coreSize = Math.max(1, _coreSize);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(_coreSize, _coreSize, 0,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(maxLvl),
                    new HandlerThreadFactory("LoadPlayerFromDB"));
            CountDownLatch _lantch = new CountDownLatch(maxLvl);
            for (int _level = 1; _level <= maxLvl; _level++) {
                final int _lvl = _level;
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogicScriptsUtils.getIFriendScript().loadRecommendsFromDb(_lvl);
                        } catch (Exception e) {
                            LOGGER.error(ExceptionEx.e2s(e));
                        }
                        _lantch.countDown();
                    }
                });
            }
            _lantch.await();
            LOGGER.info("GameServer load player from DB ------------END------------");

            System.gc();
            MemoryInformation memoryInformation = new MemoryInformation();
            this.garbageCollectorInfo = new GarbageCollectorInfo();
            this.garbageCollectorInfo.collectGC();
            Runtime runtime = Runtime.getRuntime();
            long freeMem = runtime.freeMemory();
            long totalMemory = runtime.totalMemory();
            long tMemory = ((totalMemory) / (1024 * 1024));
            long fMemory = ((freeMem) / (1024 * 1024));
            long uMemory = tMemory - fMemory;
            long directBuff = memoryInformation.getUsedDirectBufferSize() / (1024 * 1024);
            String name = ManagementFactory.getRuntimeMXBean().getName();
            String pid = name.split("@")[0];
            LOGGER.info("pid:" + pid + " TotalMemory:" + tMemory + " MB" + " FreeMemory:" + fMemory
                    + " MB" + " UsedMemory:" + uMemory + " MB" + " DirectBuff:" + directBuff
                    + " MB");
            // 获取JVM名字和类型
            LOGGER.info("vm name:" + System.getProperty("java.vm.name"));
            // 获取JVM的工作模式
            LOGGER.info("vm Info:" + System.getProperty("java.vm.info"));
            // 操作系统名字
            String osName = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
            LOGGER.info("os:" + osName);
            // 服务器最大在线人数
            LOGGER.info("maxOnlineNum:" + GlobalService.ONLINE_MAX + " maxOffLineNum:"
                    + GlobalService.getOffLineMax());

            PlayerProcessorManager.getInstance().printParamsInfo(false);

            LOGGER.info("-------------------------");
            LOGGER.info("-------------------------");
            LOGGER.info("-------------------------");


            LOGGER.info("start socket-------");

            // 启动HTTP服务
            _httpServerStart();
            // 游戏客户端监听
            _startClientServerListener();

            LOGGER.info("server client port:" + ServerConfig.getInstance().getClientListenPort()
                    + " currentThread: " + Thread.currentThread().getName());

            setStatus(EServerStatus.RUNNING);

            // 该打印内容不能修改 运维依赖 并且必须放在这里
            super.startEnd();
            LOGGER.info("game main start over");
            // 检查是否开启控制台必须放在最后面,开启后主线程进入循环等待
            _checkConsole();
        } catch (Exception e) {
            LOGGER.error("start server exception.");
            LOGGER.error(STARTFIELD + ExceptionEx.e2s(e));
            System.exit(-1);
        }
    }

    /** 是否正在关服 */
    public boolean isShutDown() {
        return status == EServerStatus.STOPPING;
    }

    /** 是否维护中(对客户端来说，非正常运行状态都视为维护中) */
    public boolean isMaintaining() {
        return status != EServerStatus.RUNNING;
    }

    public void httpServerStop() throws Exception {
        httpServer.stop();
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

    public String[] getArgs() {
        return this.args;
    }

    /**
     * 检查是否开启控制台
     */
    private void _checkConsole() {
        if (isIDEMode()) {
            LOGGER.info("IDE : Console Opened");
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
        if (input.equalsIgnoreCase("stop")) {
            stop();
        } else if (input.equalsIgnoreCase("ras")) {
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
            System.exit(0);
            // stop();
        } else if (input.equalsIgnoreCase("print")) {
            if (isIDEMode()) {
                ServerConfig.getInstance()
                        .setIsPrintQueueSizeIde(!ServerConfig.getInstance().isPrintQueueSizeIde());
            }
        } else if (input.equalsIgnoreCase("test")) {
            SnowflakeTestThread.main(null);

            // int num = 3;
            // for (int i = 0; i < num; i++) {
            // int rst =
            // MarqueeUtils.addMarquee("【跑马灯" + (i + 1)
            // + "】 老司机，带带我，我是中学生。哈哈哈哈哈哈暗杀华东师大。。。", num - i, 5, 10, new Date(),
            // new Date(System.currentTimeMillis() + 5 * 60 * 1000));
            // System.out.println("---rst=" + rst);
            // }
            // String tt = GMGameServerHandler.getInstance().getOnlineNum(null);
            // System.out.println("--onlineNum=" + tt);
            // 订单号生成规则：“日期+玩家id” eg:20180714172435+pid
            // int player_id = 10000011;
            // int item_id = 10;
            // Player player = PlayerViewService.getPlayerView(player_id);
            // RechargeCfgBean cfg = GameDataManager.getRechargeCfgBean(item_id);
            // int sell_amount = (int) (cfg.getPrice() * 100);
            // String order_id = DateEx.format(new Date(), DateEx.fmt_num_yyyy_MM_dd_HH_mm_ss)
            // + player_id;
            // String channel_order_id = "channel_" + order_id;
            // PayUtils.createPay(order_id,player.getChannelId(),player.getChannelAppId(),
            // player.getAccountId(),player_id, item_id, sell_amount);
            // int pay_amount = sell_amount;
            // PayUtils.updatePayByCallBack(channel_order_id, order_id, player_id, pay_amount,
            // "extinfo");

            // // 邮件测试
            // int id = (int) (System.currentTimeMillis() / 1000);
            // Map<Integer, Integer> items = new HashMap<Integer, Integer>();
            // items.put(500001, 2);
            // ((MailService) (MailService.getInstance())).sendServerMail("全服邮件_" + id, "全服邮件:" +
            // id,
            // items);
            // items.clear();
            // items.put(500002, 3);
            // items.put(500003, 4);
            // int receiver_id = 10000011;
            // ((MailService) (MailService.getInstance())).sendPlayerMail(true, receiver_id,
            // "单人邮件_" + id, "单人邮件:" + id,
            // items, EReason.MAIL_GM, "xx");
            // id++;
            // ((MailService) (MailService.getInstance())).sendPlayerMail(true, receiver_id,
            // "单人邮件_" + id, "单人邮件:" + id,
            // null, EReason.MAIL_GM, "无附件");
        } else if (input.equalsIgnoreCase("drop")) {
        } else if (input.equalsIgnoreCase("getview")) {
            ((PlayerViewService) PlayerViewService.getInstance()).getPlayerView(10000003);
        } else {
            LOGGER.info("不能识别的命令: " + input);
        }
    }

    /**
     * 启动监听
     * 
     * @throws Exception
     */
    private void _startClientServerListener() throws Exception {
        _externalTcpServer = new ExternalTcpServer();
        _externalTcpServer.start(ServerConfig.getInstance().getClientListenPort(),
                ServerConfig.getInstance().getClientChannelIdleTime());
    }

    private void _httpServerStart() throws Exception {
        httpServer = new HttpServer();
        httpServer.start(ServerConfig.getInstance().getHttpListenPort());
    }

    /**
     * 加载服务器配置
     * 
     * @throws Exception
     */
    private void _loadServerConfig(String[] args) throws Exception {
        String serverCfgPath = getConfigPath() + "server_config.xml";
        ServerConfig.getInstance().load(serverCfgPath, args);
    }

    /**
     * 加载消息文件并且初始化Handler factory
     * 
     * @throws Exception
     */
    private void _loadAndInitMessage() throws Exception {
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
    private void _loadResMessage() throws Exception {
        LOGGER.info("begin load respone msg.");
        Collection<Class<?>> classes = ClassScanUtils.scanPackages("org.game.protobuf");
        int count = 0;
        for (Class<?> clazz : classes) {
            MessageResClazz.getInstance().init(clazz);
            count++;
        }
        LOGGER.info("end load respone msg. count:" + count);
    }

    private String _getLocalIP() {
        String ip = "";
        try {
            // 通过网卡获取
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                Enumeration<?> e2 = ni.getInetAddresses();
                while (e2.hasMoreElements()) {
                    InetAddress ia = (InetAddress) e2.nextElement();
                    if (ia instanceof Inet6Address)
                        continue;
                    String address = ia.getHostAddress();
                    if (address.startsWith("192.168.") || address.startsWith("10.")) {
                        ip = address;
                        return ip;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        return ip;
    }

    // 获取游戏版本号
    public String getGameVersion() {
        return getGameVersionInfo(1);
    }

    // 获取版本最后提交时间
    public String getGameVersionTime() {
        return getGameVersionInfo(2);
    }

    public String getGameVersionInfo(int i) {
        InputStream is =
                CommonUtil.class.getClassLoader().getResourceAsStream("build_version.properties");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String version = null;
        int k = 1;
        try {
            while ((version = br.readLine()) != null) {
                LOGGER.info(version);
                // 读第一行
                if (i == k) {
                    break;
                }
                // 读第二行
                if (i == k) {
                    break;
                }
                k++;
            }
        } catch (IOException e) {
            LOGGER.error("read build_version.properties error", e);
        }
        if (version == null || version.isEmpty()) {
            LOGGER.error("not found buildVersion");
            return "";
        }
        return version;
    }

    public String getLocalIP() {
        return this.localIP;
    }

    public boolean isPrintQueueInfo() {
        return (isTestServer()
                        && (isIDEMode() ? ServerConfig.getInstance().isPrintQueueSizeIde() : true))
                || (!isTestServer() && ServerConfig.getInstance().isPrintQueueSize());
    }


    /**
     * 获取GameServer的实例对象.
     * 
     * @return
     */
    public static GameServer getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {

        INSTANCE;

        GameServer processor;

        Singleton() {
            this.processor = new GameServer();
        }

        GameServer getProcessor() {
            return processor;
        }
    }

}
