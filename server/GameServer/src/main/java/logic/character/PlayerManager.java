package logic.character;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.login.struct.ChannelInfo;
import net.AttributeKeys;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;

public class PlayerManager {
    private static final Logger LOGGER = Logger.getLogger(PlayerManager.class);
    /** key -> playerId value -> players */
    private static final Map<Integer, Player> pIdPlayersRegistry = new ConcurrentHashMap<>();
    /** key -> playerId value -> players */
    private static final Map<String, Player> userNamePlayersRegistry = new ConcurrentHashMap<>();

    public static Player getPlayerByPlayerId(int playerId) {
        return pIdPlayersRegistry.get(playerId);
    }

    public static Player getPlayerByUserName(String userName) {
        return userNamePlayersRegistry.get(userName);
    }

    /**
     * get player by ctx
     * 
     * @param ctx
     * @return player or null
     */
    public static Player getPlayerByCtx(ChannelHandlerContext ctx) {
        ChannelInfo channelInfo = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
        if (channelInfo == null) {
            return null;
        }
        return userNamePlayersRegistry.get(channelInfo.getFullUserName());
    }


    /**
     * 添加玩家到内存中
     * 
     * @param ctx
     * @param player
     */
    public static synchronized boolean register(Player player) {
        // 分线
        PlayerProcessor processor =
                PlayerProcessorManager.getInstance().getProcessorByUserName(player.getUserName());
        if (processor == null) {
            LOGGER.error(ConstDefine.LOG_ERROR_PROGRAMMER_PREFIX + "register failed."
                    + player.logInfo());
            return false;
        }

        pIdPlayersRegistry.put(player.getPlayerId(), player);
        userNamePlayersRegistry.put(player.getUserName(), player);

        processor.registerPlayer(player);
        
        return true;
    }

    /**
     * 分离玩家出内存
     * 
     * @param player
     */
    public static synchronized void detachPlayer(Player player) {
        pIdPlayersRegistry.remove(player.getPlayerId());
        userNamePlayersRegistry.remove(player.getUserName());
    }

    /**
     * 获取数据仍在内存中的所有玩家
     *
     * @return
     */
    public static List<Player> getAllPlayers() {
        return new ArrayList<>(pIdPlayersRegistry.values());
    }

    /**
     * 根据等级获取数据仍在内存中的所有玩家
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Player> getAllPlayerBylevel(int min, int max) {
        ArrayList lists = new ArrayList<>();
        Collection<Player> collection = pIdPlayersRegistry.values();
        for (Player player : collection) {
            if (player.getLevel() >= min && player.getLevel() <= max) {
                lists.add(player);
            }
        }
        Collections.shuffle(lists);
        return lists;
    }

    /**
     * 根据等级获取数据仍在内存中的所有玩家
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<Player> getAllPlayerBylevel(int level) {
        ArrayList lists = new ArrayList<>();
        if (level <= 0) {
            LOGGER.warn("getAllPlayerBylevel, level == 0");
            return lists;
        }
        Collection<Player> collection = pIdPlayersRegistry.values();
        for (Player player : collection) {
            if (player.getLevel() == level) {
                lists.add(player);
            }
        }
        return lists;
    }

    /** 获取当前人数 */
    public static int[] getPlayerNum() {
        int num = 0;
        List<Player> players = getAllPlayers();
        for (Player p : players) {
            if (p.isOnline()) {
                num++;
            }
        }
        int[] size = new int[2];
        size[0] = num;
        size[1] = players.size();
        return size;
    }

    /** 获取当前在线人数 */
    public static int getOnlineNum() {
        return getPlayerNum()[0];
    }

    public static int getAllNum() {
        return getPlayerNum()[1];
    }
}