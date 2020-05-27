package thread.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import logic.character.bean.Player;
import thread.db.DBBaseHandler;
import utils.ExceptionEx;

/**
 * 玩家线程管理器
 */
public class PlayerDBProcessorManager {
    private static final Logger LOGGER = Logger.getLogger(PlayerDBProcessorManager.class);

    /** 逻辑线处理器集合 */
    private final Map<Integer, PlayerDBProcessor> playerDBProcessors = new HashMap<>();

    private int processNum;

    // 数据统计
    private AtomicLong delayUpdateCount = new AtomicLong();

    private AtomicLong doUpdateCount = new AtomicLong();
    private AtomicLong doSelectCount = new AtomicLong();
    private AtomicLong doInsertCount = new AtomicLong();

    private AtomicLong updateSize = new AtomicLong();
    private AtomicLong selectSize = new AtomicLong();
    private AtomicLong insertSize = new AtomicLong();

    /**
     * 初始化处理线程数量
     * 
     * @throws Exception
     */
    private void _init() throws Exception {
        int coreSize = Runtime.getRuntime().availableProcessors() / 4;
        if (coreSize < 2) {
            coreSize = 1;
        }

        this.processNum = coreSize;

        PlayerDBProcessor processor;
        LOGGER.info(
                String.format("init playerDBProcessor, created %s DBProcessor", this.processNum));
        for (int i = 0; i < this.processNum; i++) {
            processor = new PlayerDBProcessor(i);
            this.playerDBProcessors.put(i, processor);
        }
    }

    /**
     * 向指定的玩家线程抛送handler
     * 
     * @param player
     * @param gameInnerHandler
     */
    public void addPlayerHandler(Player player, DBBaseHandler dbHandler) {
        int index = _calculateIndex(player.getUserName());
        addPlayerHandler(index, dbHandler);
    }

    /**
     * 向指定的玩家线程抛送handler
     * 
     * @param line
     * @param isReconnect
     * @param gameInnerHandler
     */
    public void addPlayerHandler(int line, DBBaseHandler dbHandler) {
        PlayerDBProcessor processor = getProcessor(line);
        if (processor == null) {
            LOGGER.error("Add Command to DBProcessor . Can Not Find Processor. index :" + line
                    + ExceptionEx.currentThreadTraces());
            return;
        }
        processor.doExecute(dbHandler);
    }

    // /**
    // * 根据playerId 计算逻辑处理线程并且返回
    // *
    // * @param playerId
    // * @return
    // */
    // public PlayerDBProcessor getProcessorByPlayerId(long playerId) {
    // int index = _calculateIndex(playerId);
    // return playerDBProcessors.get(index);
    // }

    /**
     * 根据userName 计算逻辑处理线程并且返回
     * 
     * @param userName
     * @return
     */
    public PlayerDBProcessor getProcessorByUserName(String userName) {
        int index = _calculateIndex(userName);
        return playerDBProcessors.get(index);
    }

    /**
     * 根据逻辑线程Id 获取处理线程
     * 
     * @param lineId
     * @return
     */
    public PlayerDBProcessor getProcessor(int lineId) {
        return playerDBProcessors.get(lineId);
    }

    /**
     * 获取线程数
     * 
     * @return
     */
    public int processorSize() {
        return playerDBProcessors.size();
    }

    // private int _calculateIndex(long playerId) {
    // return (int) (playerId % playerDBProcessors.size()) + 1;
    // }

    private int _calculateIndex(String userName) {
        return Math.abs(userName.toLowerCase().hashCode()) % playerDBProcessors.size();
    }


    /**
     * 私有化构造函数
     * 
     * @throws Exception
     */
    private PlayerDBProcessorManager() {
        try {
            _init();
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            System.exit(-1);
        }
    }

    /**
     * 停止并等待停止
     */
    public void stop() {
        _stop();
    }

    private void _stop() {
        List<PlayerDBProcessor> lines = new ArrayList<>();
        lines.addAll(this.playerDBProcessors.values());
        // for (PlayerDBProcessor playerProcessor : lines) {
        // playerProcessor.stop();
        // }
        ExecutorService service = Executors.newFixedThreadPool(lines.size());
        for (PlayerDBProcessor playerDBProcessor : lines) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    playerDBProcessor.stop();
                }
            });
        }
        service.shutdown();
        try {
            while (!service.awaitTermination(1000L, TimeUnit.MILLISECONDS)) {
                LOGGER.info("wait playerDBProcessor stop ...");
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    public int[] totalLineSize() {
        int[] total = new int[processNum];
        for (int i = 0; i < processNum; i++) {
            PlayerDBProcessor processor = playerDBProcessors.get(i);
            total[i] = processor.getQueueSize();
        }
        return total;
    }

    public long[] totalAloneCount() {
        long[] total = new long[processNum];
        for (int i = 0; i < processNum; i++) {
            PlayerDBProcessor processor = playerDBProcessors.get(i);
            total[i] = processor.getAloneNum();
        }
        return total;
    }

    public long getDelayUpdateCount() {
        return delayUpdateCount.get();
    }

    public void addDelayUpdateCount() {
        delayUpdateCount.incrementAndGet();
    }

    public long getDoUpdateCount() {
        return doUpdateCount.get();
    }

    public void addDoUpdateCount() {
        doUpdateCount.incrementAndGet();
    }

    public long getDoSelectCount() {
        return doSelectCount.get();
    }

    public void addDoSelectCount() {
        doSelectCount.incrementAndGet();
    }

    public long getDoInsertCount() {
        return doInsertCount.get();
    }

    public void addDoInsertCount() {
        doInsertCount.incrementAndGet();
    }

    public long getUpdateSize() {
        return updateSize.get();
    }

    public void addUpdateSize() {
        updateSize.incrementAndGet();
    }

    public void decUpdateSize() {
        updateSize.decrementAndGet();
    }

    public long getSelectSize() {
        return selectSize.get();
    }

    public void addSelectSize() {
        selectSize.incrementAndGet();
    }

    public void decSelectSize() {
        selectSize.decrementAndGet();
    }

    public long getInsertSize() {
        return insertSize.get();
    }

    public void addInsertSize() {
        insertSize.incrementAndGet();
    }


    public void decInsertSize() {
        insertSize.decrementAndGet();
    }


    public void addUpdateSize(int delta) {
        this.updateSize.addAndGet(delta);
    }


    public static PlayerDBProcessorManager getInstance() {
        return Singleton.INSTANCE.getManager();
    }


    private enum Singleton {
        INSTANCE;
        PlayerDBProcessorManager manager;

        Singleton() {
            this.manager = new PlayerDBProcessorManager();
        }

        PlayerDBProcessorManager getManager() {
            return manager;
        }
    }
}
