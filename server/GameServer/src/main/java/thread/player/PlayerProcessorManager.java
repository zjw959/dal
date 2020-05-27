package thread.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.gloabl.GlobalService;
import logic.login.struct.ChannelInfo;
import message.MessageHandler;
import net.AttributeKeys;
import server.GameServer;
import thread.base.GameInnerHandler;
import thread.player.hanlder.LPlayerProcessSecdTickHandler;
import thread.timer.TimeEventProcessor;
import thread.timer.TimerEvent;
import utils.ChannelUtils;
import utils.DateEx;
import utils.ExceptionEx;

/**
 * 玩家线程管理器
 */
public class PlayerProcessorManager {
    private static final Logger LOGGER = Logger.getLogger(PlayerProcessorManager.class);

    /** 逻辑线处理器集合 */
    private final Map<Integer, PlayerProcessor> playerProcessors = new HashMap<>();

    /** 这里需要根据项目的实际情况进行估算 在线人数,线程数,回存线程数量 **/
    /**
     * 根据在线人数 数据库更新次数 估算5分钟内能保存所有的在线玩家
     * 
     * 当前按照4k人, 16核进行设置. 详见文档
     **/

    /** 根据实际线程数控制 **/
    /** 每次回存在线玩家数量 */
    private volatile int maxSaveNumOnline = 10;
    /** 每次离线回存最大数量(每次回存在线玩家数量/3) */
    private volatile int maxSaveNumOffline = 3;

    private int processNum;

    private long singleDBTime = 50;

    /**
     * 初始化玩家logic处理线程数量 cpu处理器核心数-2
     * 
     * @throws Exception
     */
    private void _init() throws Exception {
        this.processNum = Runtime.getRuntime().availableProcessors() - 2;
        if (this.processNum <= 0) {
            this.processNum = 1;
        }

        this.initSaveNum();

        PlayerProcessor processor;
        LOGGER.info(
                String.format("init logic processor ,created %s LogicProcessor", this.processNum));
        for (int i = 0; i < this.processNum; i++) {
            processor = new PlayerProcessor(i);
            playerProcessors.put(i, processor);
        }

        long now = System.currentTimeMillis();
        initProcess(now);
    }

    public void initProcess(long now) {
        // 每个线程的启动时间间隔
        int delta = (int) (Math
                .ceil(GlobalService.getInstance().getSavebackCheckInterval() / DateEx.TIME_MINUTE)
                * DateEx.TIME_SECOND);
        if (delta <= 0) {
            int _num = this.processNum - 2;
            if (_num <= 0) {
                _num = 1;
            }
            delta = (int) (DateEx.TIME_SECOND / _num);
        }
        for (int i = 0; i < this.processNum; i++) {
            PlayerProcessor processor = this.playerProcessors.get(i);
            // (在非每秒执行的情况下) 错时运行玩家检测,尽量平滑运行
            // 不能整除的间隔时间 逻辑上让回存间隔时间拆为每N个线程在同一时间点运行
            processor.setLastSaveTime(now + (i - 1) * delta);
        }
    }

    public void initSaveNum() throws Exception {
        // 按迭代时间计算保底数量
        int onlineMax = GlobalService.ONLINE_MAX;

        // 每次迭代最少取出的在线玩家个数
        int _s = (int) (onlineMax
                / (GlobalService.getInstance().getSaveBackInterval() / DateEx.TIME_SECOND));
        if (_s > 0) {
            int _de = (int) (DateEx.TIME_SECOND / _s);
            if (this.singleDBTime < _de) {
                this.singleDBTime = _de;
            }
        }
        // 每个线程需要分担的数量
        int _sNum = (int) Math.ceil(_s / (double) this.processNum);

        if (_sNum <= 0 && (!GameServer.getInstance().isTestServer()
                || GameServer.getInstance().isRootDrangServer())) {
            throw new Exception("maxSaveNumOfOnline is zero. onlineMax:" + onlineMax + "," + _sNum
                    + ",this.processNum:" + this.processNum);
        }

        if (_sNum > 10 && (!GameServer.getInstance().isTestServer()
                || GameServer.getInstance().isRootDrangServer())) {
            throw new Exception("回存线程每次取出设置压力过高. onlineMax:" + onlineMax + "," + _sNum
                    + ",this.processNum:" + this.processNum);
        }

        this.maxSaveNumOnline =
                _sNum * (int) (GlobalService.getInstance().getSavebackCheckInterval()
                        / DateEx.TIME_SECOND);

        // 增加一定的冗余数
        this.maxSaveNumOnline *= 1.2;
        this.maxSaveNumOnline = 1;
        if (this.maxSaveNumOnline <= 0) {
            this.printParamsInfo(true);
            System.exit(-1);
        }

        this.maxSaveNumOffline = (int) Math.ceil((this.maxSaveNumOnline / 2.0));
    }

    private void _initTimer() {
        TimeEventProcessor.getInstance().addEvent(new PlayerProcessorTimerEvent(1000, 1000, true));
    }

    public void printParamsInfo(boolean isError) {
        String printStr = "player processor init params: onlineMax:" + GlobalService.ONLINE_MAX
                + ", saveInterval:" + GlobalService.getInstance().getSaveBackInterval()
                + ", checkInterval:" + GlobalService.getInstance().getSavebackCheckInterval()
                + ", maxSaveNumOnline:" + this.maxSaveNumOnline + ", maxSaveNumOffline:"
                + maxSaveNumOffline;
        if (isError) {
            LOGGER.error(printStr);
        } else {
            LOGGER.info(printStr);
        }
    }

    /**
     * 此处逻辑将会在定时器线程执行
     */
    public void checkPerSecond() {
        for (Map.Entry<Integer, PlayerProcessor> entry : playerProcessors.entrySet()) {
            PlayerProcessor processor = entry.getValue();
            processor.executeInnerHandler(new LPlayerProcessSecdTickHandler(processor));
        }
    }

    public void addMsgHandler(MessageHandler messageHandler) {
        if (ChannelUtils.isDisconnectChannel(messageHandler.getCtx())) {
            LOGGER.info("distribute messageHandler to LogicProcess, but channel is invalid. ctx:"
                    + ChannelUtils.logInfo(messageHandler.getCtx())
                    + " msgid:" + messageHandler.getMessage().getId());
            return;
        }
        Player player = PlayerManager.getPlayerByCtx(messageHandler.getCtx());
        if (player == null) {
            LOGGER.warn(
                    "distribute messageHandler to LogicProcess, can not find player ,server close channel context"
                            + ChannelUtils.logInfo(messageHandler.getCtx()) + " msgid:"
                            + messageHandler.getMessage().getId());
            ChannelUtils.closeChannel(messageHandler.getCtx(),
                    "distribute messageHandler,can not find player, server close.");
            return;
        }
        PlayerProcessor processor = getProcessor(player.getLineIndex());
        if (processor == null) {
            LOGGER.error("Add Command to LogicProcessor . Can Not Find Processor. index :"
                    + player.getLineIndex());
            return;
        }
        if (processor.isStop(false)) {
            LOGGER.warn("Add Command to LogicProcessor .Processor is shoudown. index :"
                    + player.getLineIndex());
            return;
        }
        processor.executeMessageHandler(messageHandler);
    }

    /**
     * 向指定的玩家线程抛送handler
     * 
     * @param player
     * @param gameInnerHandler
     */
    public void addPlayerHandler(Player player, GameInnerHandler gameInnerHandler) {
        addPlayerHandler(player.getLineIndex(), gameInnerHandler);
    }

    /**
     * 向指定的玩家线程抛送handler
     * 
     * @param line
     * @param gameInnerHandler
     */
    public void addPlayerHandler(int line, GameInnerHandler gameInnerHandler) {
        PlayerProcessor processor = getProcessor(line);
        if (processor == null) {
            LOGGER.error("Add Command to LogicProcessor . Can Not Find Processor. index :" + line
                    + ExceptionEx.currentThreadTraces());
            return;
        }
        processor.executeInnerHandler(gameInnerHandler);
    }

    /**
     * 获取lineId
     * 
     * @param context
     * @return
     */
    public int getLineIdByCTX(ChannelHandlerContext context) {
        ChannelInfo info = context.channel().attr(AttributeKeys.CHANNEL_INFO).get();
        if (info == null) {
            return -1;
        }
        return _calculateIndex(info.getFullUserName());
    }

    /**
     * 根据playerId 计算逻辑处理线程并且返回
     * 
     * @param userName 渠道名
     * @return
     */
    public PlayerProcessor getProcessorByUserName(String userName) {
        int index = _calculateIndex(userName);
        return playerProcessors.get(index);
    }

    /**
     * 根据逻辑线程Id 获取处理线程
     * 
     * @param lineId
     * @return
     */
    public PlayerProcessor getProcessor(int lineId) {
        return playerProcessors.get(lineId);
    }

    /**
     * 获取玩家线程数
     * 
     * @return
     */
    public int processorSize() {
        return playerProcessors.size();
    }

    // private int _calculateIndex(long playerId) {
    // return (int) (playerId % playerProcessors.size()) + 1;
    // }

    private int _calculateIndex(String userName) {
        return Math.abs(userName.toLowerCase().hashCode()) % playerProcessors.size();
    }

    /**
     * 私有化构造函数
     * 
     * @throws Exception
     */
    private PlayerProcessorManager() {
        try {
            _init();
            _initTimer();
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            System.exit(-1);
        }
    }

    public int getMaxSaveNumOffline() {
        return maxSaveNumOffline;
    }

    public void setMaxSaveNumOffline(int maxSaveNumOffline) {
        this.maxSaveNumOffline = maxSaveNumOffline;
    }

    public int getMaxSaveNumOnline() {
        return maxSaveNumOnline;
    }

    public void setMaxSaveNumOnline(int maxSaveNumOfOnline) {
        this.maxSaveNumOnline = maxSaveNumOfOnline;
    }

    public static PlayerProcessorManager getInstance() {
        return Singleton.INSTANCE.getManager();
    }


    private enum Singleton {
        INSTANCE;
        PlayerProcessorManager manager;

        Singleton() {
            this.manager = new PlayerProcessorManager();
        }

        PlayerProcessorManager getManager() {
            return manager;
        }
    }


    /**
     * 停止并等待停止
     */
    public void stop() {
        // 请求各线回存玩家数据
        requestSaveAllPlayer();
        _stop();
    }

    /**
     * 请求各线回存玩家数据
     */
    private void requestSaveAllPlayer() {
        List<PlayerProcessor> lines = new ArrayList<>();
        lines.addAll(this.playerProcessors.values());
        for (PlayerProcessor playerProcessor : lines) {
            playerProcessor.savebackAllPlayer(true);
        }
    }

    /**
     * http线程调用
     */
    public void savebackAllOfflinePlayer() {
        List<PlayerProcessor> lines = new ArrayList<>();
        lines.addAll(this.playerProcessors.values());
        for (PlayerProcessor playerProcessor : lines) {
            playerProcessor.savebackAllOfflinePlayer();
        }
    }

    private void _stop() {
        List<PlayerProcessor> lines = new ArrayList<>();
        lines.addAll(this.playerProcessors.values());
        // 并发等待回存
        // for (PlayerProcessor playerProcessor : lines) {
        // playerProcessor.stop();
        // }
        ExecutorService service = Executors.newFixedThreadPool(lines.size());
        for (PlayerProcessor playerProcessor : lines) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    playerProcessor.stop();
                }
            });
        }
        service.shutdown();
        try {
            while (!service.awaitTermination(1000L, TimeUnit.MILLISECONDS)) {
                LOGGER.info("wait playerProcessor stop ...");
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    public long getSingleDBTime() {
        return singleDBTime;
    }

    public void setSingleDBTime(long singleDBTime) {
        this.singleDBTime = singleDBTime;
    }

    /**
     * 玩家处理线程 Tick 定时器
     */
    private class PlayerProcessorTimerEvent extends TimerEvent {
        public PlayerProcessorTimerEvent(long firstDelay, long delay, boolean loopFixed) {
            super(firstDelay, delay, loopFixed);
        }

        @Override
        public void run() {
            PlayerProcessorManager.getInstance().checkPerSecond();
        }
    }

    public int[] totalLineSize() {
        int[] total = new int[processNum];
        for (int i = 0; i < processNum; i++) {
            PlayerProcessor processor = playerProcessors.get(i);
            total[i] = processor.getQueueSize();
        }
        return total;
    }
}
