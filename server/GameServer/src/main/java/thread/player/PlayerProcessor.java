package thread.player;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.support.LogicScriptsUtils;
import net.AttributeKeys;
import security.MD5;
import thread.base.GameBaseProcessor;
import thread.base.GameInnerHandler;
import thread.db.DBUpdateRoleHandler;
import utils.ChannelUtils;
import utils.DateEx;

/**
 * 玩家逻辑处理线程
 */
public class PlayerProcessor extends GameBaseProcessor {
    private static final Logger LOGGER = Logger.getLogger(GameBaseProcessor.class);
    /** 逻辑线index */
    private final int lineIndex;

    /** key-> userId value -> player */
    private final Map<Integer, Player> playerRegistry = new ConcurrentHashMap<>();
    /** 上次检测回存时间 */
    private long lastSaveTime = 0;

    public PlayerProcessor(int index) {
        super(PlayerProcessor.class.getSimpleName() + "-" + index);
        this.lineIndex = index;
    }

    public int getLineIndex() {
        return this.lineIndex;
    }

    public void registerPlayer(Player player) {
        this.playerRegistry.put(player.getPlayerId(), player);
    }

    public Player getPlayerByPlayerId(int playerId) {
        return this.playerRegistry.get(playerId);
    }

    private void _removeByPlayerId(int playerId) {
        this.playerRegistry.remove(playerId);
    }

    public void tick() {
        long currentTime = System.currentTimeMillis();
        int tickSize = 0;
        for (Player player : this.playerRegistry.values()) {
            if (!player.isOnline()) {
                // 跳过离线玩家
                continue;
            }
            tickSize++;
            player.tick(currentTime);
        }
        long tick = System.currentTimeMillis() - currentTime;
        if (tick > 100) {
            LOGGER.warn(
                    ConstDefine.LOG_DO_OVER_TIME + "player tick :" + tick + ",size:" + tickSize);
        }
        _savebackTick();
    }

    private void _savebackTick() {
        LogicScriptsUtils.getPlayerScript().saveBackTick(this);
    }

    /**
     * 回写内存中所有玩家
     * 
     * @param isDisconnected 是否做停服处理
     */
    public void savebackAllPlayer(boolean isDisconnected) {
        executeInnerHandler(new LSaveBackAllHandler(isDisconnected));
    }

    private class LSaveBackAllHandler extends GameInnerHandler {
        private boolean isConnected;

        public LSaveBackAllHandler(boolean isConnected) {
            this.isConnected = isConnected;
        }

        @Override
        public void action() {
            PlayerProcessor.this.savebackAll(isConnected);
        }
    }

    private void savebackAll(boolean disconnect) {
        Iterator<Map.Entry<Integer, Player>> it = this.playerRegistry.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Player> entry = it.next();
            Player player = entry.getValue();
            if (player != null) {
                // 根据id分别整合更新
                PlayerDBProcessorManager.getInstance().addPlayerHandler(player,
                        new DBUpdateRoleHandler(player.toPlayerBean()));

                if (disconnect && player.getCtx() != null && player.getCtx().channel() != null) {
                    ChannelUtils.closeChannel(player.getCtx(), ". 停服, 将玩家踢下线.");
                    AttributeKeys.clear(player.getCtx());// 避免断开连接后再次进入回存队列
                    it.remove();
                }
            }
        }

        // if (!roleBeanLists.isEmpty()) {
        // DBUpdateBatchCharacterHandler batchUpdates =
        // new DBUpdateBatchCharacterHandler(roleBeanLists, false);
        // PlayerDBProcessor.getInstance().doExecute(batchUpdates);
        // }

        LOGGER.info("PlayerProcessor Save Back All Player to PlayerDBProcessor");
    }

    /**
     * 后台Http接口调用 回存并且移除内存中所有的离线玩家
     */
    protected void savebackAllOfflinePlayer() {
        executeInnerHandler(new LSaveBackAllOfflineHandler());
    }

    private class LSaveBackAllOfflineHandler extends GameInnerHandler {

        @Override
        public void action() {
            PlayerProcessor.this.savebackAllOffline();
        }
    }

    private void savebackAllOffline() {
        Iterator<Map.Entry<Integer, Player>> it = playerRegistry.entrySet().iterator();
        Player player;
        while (it.hasNext()) {
            player = it.next().getValue();
            if (player.getState().equals(Player.PlayerState.OFFLINE)) {
                PlayerDBProcessorManager.getInstance().addPlayerHandler(player,
                        new DBUpdateRoleHandler(player.toPlayerBean()));
                it.remove();
                PlayerManager.detachPlayer(player);
            }
        }
        LOGGER.info("Line : " + lineIndex + "————Save Back All Offline Player In Memory————");
    }

    /**
     * 根据数据变化情况进行回存 并从内存中移除
     * 
     * @param player
     * @param currentTime
     * @return isSaveDb
     */
    public boolean flushSingleOneOfflinePlayer(Player player, long currentTime) {
        boolean isSave = false;
        if (!isDirty(player)) {
            player.save(false);
            isSave = true;
        }
        player.setLastSavebackTime(currentTime);
        _removeByPlayerId(player.getPlayerId());
        PlayerManager.detachPlayer(player);

        // TODO 根据内测情况,玩家实际的离线频率. 统计平均值 判断是否需要扩大或者减少离线缓存玩家数量
        long inMemTime = currentTime - player.getOfflineTime();
        if (inMemTime != 0) {
            inMemTime = inMemTime / DateEx.TIME_MINUTE;
        }
        LOGGER.info("player detach from memory. in memory time:" + inMemTime + "s");
        return isSave;
    }

    /**
     * 判断数据是否进行了修改
     * 
     * @param player
     * @return
     */
    public boolean isDirty(Player player) {
        long now = System.currentTimeMillis();
        player.setDirtyCalculateTime(now);
        String str = player.toPlayerBean().toString();
        long time = System.currentTimeMillis() - now;
        // if (time > 10) {
        // LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " dirtytime toString:" + time + ",playerId:"
        // + player.getPlayerId());
        // }
        int idx = str.indexOf("[");
        if (idx >= 0 && idx < str.length()) {
            str = str.substring(idx);
            long now2 = System.currentTimeMillis();
            String _dirtyKey = MD5.MD5Encode(str);
            time = System.currentTimeMillis() - now2;
            // if (time > 5) {
            // LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " dirtytime, MD5:" + time + ",playerId:"
            // + player.getPlayerId());
            // }
            long now3 = System.currentTimeMillis();
            if (!_dirtyKey.equalsIgnoreCase(player.getDirtyKey())) {
                time = System.currentTimeMillis() - now3;
                if (time > 5) {
                    LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " dirtytime, equals:" + time
                            + ",playerId:" + player.getPlayerId());
                }
                player.setDirtyKey(_dirtyKey);
                time = System.currentTimeMillis() - now;
                if (time > 10) {
                    LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " dirtytime, all:" + time
                            + ",playerId:" + player.getPlayerId());
                }
                return true;
            }
            // time = System.currentTimeMillis() - now;
            // if (time > 10) {
            // LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " dirtytime, all:" + time + ",playerId:"
            // + player.getPlayerId());
            // }
        }
        return false;
    }

    /**
     * 按照离线时间排序.(升序)
     * 
     * @return
     */
    public TreeSet<Player> getTreeSetByOfflineTime() {
        return new TreeSet<>(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if (o1 == null || o2 == null)
                    return 1;
                return (int) (o1.getOfflineTime() - o2.getOfflineTime());
            }
        });
    }

    /**
     * 按照dirtyTime时间排序.(升序)
     * 
     * @return
     */
    public static class PlayerDirtyTimeComparator implements Comparator<Player> {
        @Override
        public int compare(Player p1, Player p2) {
            return (int) (p1.getDirtyCalculateTime() - p2.getDirtyCalculateTime());
        }
    }

    /**
     * 最后回存时间.(升序)
     * 
     * @return
     */
    public TreeSet<Player> getTreeSetByLastSaveTime() {
        return new TreeSet<>(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if (o1 == null || o2 == null)
                    return 1;
                return (int) (o1.getLastSavebackTime() - o2.getLastSavebackTime());
            }
        });
    }

    public void setLastSaveTime(long lastSaveTime) {
        this.lastSaveTime = lastSaveTime;
    }

    public long getLastSaveTime() {
        return lastSaveTime;
    }

    public Map<Integer, Player> getPlayerRegistry() {
        return playerRegistry;
    }
}
