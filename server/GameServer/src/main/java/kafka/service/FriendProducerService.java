package kafka.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import kafka.team.action.TeamActionManager;
import kafka.team.param.g2g.FriendHandlerParam;
import kafka.team.param.g2g.NotifyObj;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.friend.FriendManager;
import server.ServerConfig;
import utils.ExceptionEx;
import utils.GsonUtils;
import utils.LocationServerUtil;

public class FriendProducerService extends AbstractProductService {
    private static final Logger LOGGER = Logger.getLogger(ChatRoomProducerService.class);


    private static class DEFAULT {
        private static final FriendProducerService provider = new FriendProducerService();
    }

    public static FriendProducerService getInstance() {
        return DEFAULT.provider;
    }

    @Override
    public String getPropPath() {
        return "./config/properties/kafka-producer-game.properties";
    }

    /** 发送通知 **/
    public void sendNotify(Map<Integer, List<NotifyObj>> notifyObjs) {
        if (notifyObjs.size() == 0) {
            return;
        }

        Set<Entry<Integer, List<NotifyObj>>> entries = notifyObjs.entrySet();
        Iterator<Entry<Integer, List<NotifyObj>>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Entry<Integer, List<NotifyObj>> _entry = iterator.next();
            int _playerId = _entry.getKey();
            int serverId = LocationServerUtil.getServerId(_playerId);
            // 本服玩家
            if (serverId == ServerConfig.getInstance().getServerId()) {
                iterator.remove();
                Player player = PlayerManager.getPlayerByPlayerId(_playerId);
                if (player != null && player.isOnline()) {
                    List<NotifyObj> objs = _entry.getValue();
                    String json = GsonUtils.toJson(objs);
                    // this.process(json);
                    FriendManager.process(json);
                }
            }
            if (serverId == -1) {
                iterator.remove();
            }
        }

        // 其他服务玩家
        for (Entry<Integer, List<NotifyObj>> entry : notifyObjs.entrySet()) {
            int _playerId = entry.getKey();
            int serverId = LocationServerUtil.getServerId(_playerId);
            if (serverId != -1) {
                String data = GsonUtils.toJson(entry.getValue());
                // LOGGER.info("kafka send!topic=" + getTopic(serverId) + " data=" + data);
                FriendHandlerParam param = new FriendHandlerParam();
                param.setNotifyList(data);
                String dataStr;
                try {
                    dataStr = TeamActionManager.getDefault().makeParam(param);
                    getProducer().send(this.getTopic(serverId), "", dataStr);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
        }
    }

    public String getTopic(int serverId) {
        return "g" + getServerGroup() + "game" + serverId;
    }

}
