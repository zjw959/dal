package logic.chat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import kafka.service.ChatPrivateProductService;
import kafka.team.param.g2g.InviteTeamSystemHandlerParam;
import kafka.team.param.g2g.PrivateChatHandlerParam;
import kafka.team.param.g2g.PublicChatHandlerParam;
import kafka.team.param.g2g.TeamChatHandlerParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chasm.TeamRedisService;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;
import logic.city.build.BuildingConstant;
import logic.constant.ConstDefine;
import logic.constant.DungeonConstant;
import logic.constant.DungeonTypeConstant;
import logic.constant.EEventType;
import logic.constant.EReason;
import logic.constant.EventConditionKey;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonGroupBean;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.mail.MailService;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.role.bean.Role;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import message.SMessage;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CChatMsg.ChatInfo;
import org.game.protobuf.s2c.S2CChatMsg.RespChangeRoom;
import org.game.protobuf.s2c.S2CChatMsg.RespInitChatInfo;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;

import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import server.GameServer;
import server.ServerConfig;
import thread.sys.base.AbsSysFuctionStore;
import utils.ExceptionEx;
import cn.hutool.core.util.StrUtil;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;

/**
 * 
 * @Description 聊天服务类
 * @author LiuJiang
 * @date 2018年6月7日 下午8:39:50
 *
 */
public class ChatService extends AbsSysFuctionStore {
    private static final Logger LOGGER = Logger.getLogger(ChatService.class);

    private final static String CHAT_ROOM = "chatroom";

    // 记录当前服务器的玩家分别在哪个房间
    private Map<Integer, Integer> playerIdRoomIdMap = new ConcurrentHashMap<Integer, Integer>();
    // 房间最大人数
    private int maxPlayerForRoom = 200;

    private int autoEnterPercent = 80;

    private final int advisePlayerForRoom = maxPlayerForRoom * autoEnterPercent / 100;

    private final int randomRoom = 10;

    private final int maxRoomNum = 9999;

    private final int chatRoomMsgNum = 10;

    private Map<Integer, ArrayBlockingQueue<ChatInfo.Builder>> chatRoomMsgMap =
            new ConcurrentHashMap<Integer, ArrayBlockingQueue<ChatInfo.Builder>>();

    public Map<Integer, Integer> getPlayerIdRoomIdMap() {
        return playerIdRoomIdMap;
    }

    class RoomNum {
        int roomId;
        int num;

        public RoomNum(int roomId, int num) {
            super();
            this.roomId = roomId;
            this.num = num;
        }

        public int getRoomId() {
            return roomId;
        }

        public void setRoomId(int roomId) {
            this.roomId = roomId;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void save() {
        Map<Integer, Integer> onlineNumMap = getRoomOnlineNum();
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.CHAT.getType()).getJedis()) {
            for (Entry<Integer, Integer> entry : onlineNumMap.entrySet()) {
                Integer roomId = entry.getKey();
                Integer num = entry.getValue();
                jedis.hincrBy(CHAT_ROOM, "" + roomId, -num);
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    public int getRoomId(Player player) {
        if (!player.isOnline()) {
            return 0;
        }
        Integer roomId = playerIdRoomIdMap.get(player.getPlayerId());
        if (roomId == null) {
            LOGGER.error("player is not in chatroom!playerId=" + player.getPlayerId());
            return 0;
        }
        return roomId;
    }

    public void enterChatRoom(Player player, int roomId, boolean isLogin) {
        maxPlayerForRoom = (int) GameDataManager.getDiscreteDataCfgBean(3004).getData().get("max");
        autoEnterPercent = (int) GameDataManager.getDiscreteDataCfgBean(3004).getData()
                .get("autoEnterPercent");
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.CHAT.getType()).getJedis()) {
            // 获得房间人数
            boolean isExist = jedis.exists(CHAT_ROOM);
            Map<String, String> numInRoomsStr = new HashMap<String, String>();
            if (isExist) {
                numInRoomsStr = jedis.hgetAll(CHAT_ROOM);
            }
            Map<Integer, Integer> numInRooms = new HashMap<Integer, Integer>();
            List<RoomNum> roomNumList = new ArrayList<RoomNum>();
            for (Entry<String, String> entry : numInRoomsStr.entrySet()) {
                int _roomId = Integer.valueOf(entry.getKey());
                int _num = Integer.valueOf(entry.getValue());
                numInRooms.put(_roomId, _num);
                if (_num < advisePlayerForRoom) {
                    roomNumList.add(new RoomNum(_roomId, _num));
                }
            }
            Integer oldRoom = playerIdRoomIdMap.get(player.getPlayerId());
            // 自己选择房间
            if (roomId > 0) {
                Integer numInRoom = numInRooms.get(roomId);
                if (oldRoom != null && numInRoom != null && numInRoom >= maxPlayerForRoom) {
                    // RespInitChatInfo.Builder info =
                    // RespInitChatInfo.newBuilder().setRoomId(oldRoom);
                    // RespChangeRoom.Builder builder =
                    // RespChangeRoom.newBuilder().setRoomInfo(info);
                    // MessageUtils.send(player, builder);
                    SMessage msg = new SMessage(RespChangeRoom.MsgID.eMsgID_VALUE,
                            GameErrorCode.CHAT_ROOM_IS_FULL);
                    MessageUtils.send(player.getCtx(), msg);
                    return;
                }
            } else {
                // 推荐房间
                roomId = getAdviseRoom(numInRooms, roomNumList);
            }
            if (oldRoom != null && oldRoom > 0) {
                jedis.hincrBy(CHAT_ROOM, "" + oldRoom, -1);
            }
            jedis.hincrBy(CHAT_ROOM, "" + roomId, 1);
            playerIdRoomIdMap.put(player.getPlayerId(), roomId);
            RespInitChatInfo.Builder info = RespInitChatInfo.newBuilder().setRoomId(roomId);
            Collection<ChatInfo.Builder> chatList = getChatRoomMsg(roomId);
            for (ChatInfo.Builder msgBuilder : chatList) {
                info.addMsgs(msgBuilder);
            }
            if (isLogin) {
                MessageUtils.send(player, info);
            } else {
                RespChangeRoom.Builder builder = RespChangeRoom.newBuilder().setRoomInfo(info);
                MessageUtils.send(player, builder);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private int getAdviseRoom(Map<Integer, Integer> numInRooms, List<RoomNum> roomNumList) {
        /**
         * 玩家将被分配到当前房间人数未超过80%的房间中，人数最多的10个非空房间中的任意一个。 如果非空房间不足10个，将按房间id顺序凑齐10个房间。 
         * 人数超过80%的房间，玩家只能选择手动收入房间号进入房间。  房间人数达到100%时，其他房间玩家无法进入此房间。
         */
        /*
         * 1，从redis上取服务器人数 2，插10个空房间 3，排序 超80%的排后面，否则人多的排前面 4，随机出1个
         */
        for (int i = 0, j = 1; i < randomRoom && j <= maxRoomNum; j++) {
            if (numInRooms.get(j) == null) {
                roomNumList.add(new RoomNum(j, 0));
                i++;
                continue;
            }
        }
        roomNumList.sort((roomA, roomB) -> {
            if (roomA.getNum() == roomB.getNum())
                return 0;
            if (roomB.getNum() < advisePlayerForRoom && roomA.getNum() >= advisePlayerForRoom)
                return 1;
            if (roomA.getNum() < advisePlayerForRoom && roomB.getNum() >= advisePlayerForRoom)
                return -1;
            if (roomA.getNum() >= advisePlayerForRoom && roomB.getNum() >= advisePlayerForRoom) {
                return (roomA.getNum() > roomB.getNum()) ? 1 : -1;
            }
            return (roomA.getNum() > roomB.getNum()) ? -1 : 1;
        });
        roomNumList = roomNumList.subList(0, randomRoom);
        Random ran = new Random();
        int index = ran.nextInt(roomNumList.size());
        RoomNum ranRoom = roomNumList.get(index);
        return ranRoom.getRoomId();
    }

    // 下线的时候调用
    public void exitChatRoom(int playerId) {
        Integer roomId = playerIdRoomIdMap.remove(playerId);
        if (roomId == null)
            return;
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.CHAT.getType()).getJedis()) {
            jedis.hincrBy(CHAT_ROOM, "" + roomId, -1);
        } catch (Exception e) {
            throw e;
        }
    }

    private void putChatRoomMsg(int roomId, ChatInfo.Builder builder) {
        ArrayBlockingQueue<ChatInfo.Builder> queue = chatRoomMsgMap.get(roomId);
        if (queue == null) {
            queue = new ArrayBlockingQueue<ChatInfo.Builder>(chatRoomMsgNum);
            chatRoomMsgMap.put(roomId, queue);
        }
        while (!queue.offer(builder)) {
            queue.poll();
        }
    }

    private Collection<ChatInfo.Builder> getChatRoomMsg(int roomId) {
        ArrayBlockingQueue<ChatInfo.Builder> queue = chatRoomMsgMap.get(roomId);
        if (queue == null || queue.size() == 0)
            return new ArrayList<ChatInfo.Builder>();
        return Arrays.asList(queue.toArray(new ChatInfo.Builder[queue.size()]));
    }

    public void notifyChatRoom(PublicChatHandlerParam chatObject) {
        ChatInfo.Builder builder = ChatInfo.newBuilder().setChannel(ChatChannelType.PUBLIC)
                .setFun(ChatFunctionType.CHAT).setHelpFightHeroCid(chatObject.getSenderHeroCid())
                .setContent(chatObject.getContent()).setPid(chatObject.getSenderId())
                .setPname(chatObject.getSenderName()).setLvl(chatObject.getSenderLevel());
        putChatRoomMsg(chatObject.getRoomId(), builder);
        Map<Integer, Integer> playerIdRoomIdMap = ChatService.getInstance().getPlayerIdRoomIdMap();
        for (Entry<Integer, Integer> entry : playerIdRoomIdMap.entrySet()) {
            if (entry.getValue() != chatObject.getRoomId())
                continue;
            Player player = PlayerManager.getPlayerByPlayerId(entry.getKey());
            if (player == null || !player.isOnline())
                continue;
            MessageUtils.send(player, builder);
        }
    }

    public void notifyChatRoom(InviteTeamSystemHandlerParam chatObject) {
        ChatInfo.Builder builder = ChatInfo.newBuilder().setChannel(ChatChannelType.PUBLIC)
                .setFun(ChatFunctionType.CHASM).setHelpFightHeroCid(chatObject.getSenderHeroCid())
                .setContent(chatObject.getContent()).setPid(chatObject.getPlayerId())
                .setPname(chatObject.getSenderName()).setLvl(chatObject.getSenderLevel());
        Map<Integer, Integer> playerIdRoomIdMap = ChatService.getInstance().getPlayerIdRoomIdMap();
        for (Entry<Integer, Integer> entry : playerIdRoomIdMap.entrySet()) {
            if (entry.getValue() != chatObject.getRoomId())
                continue;
            Player player = PlayerManager.getPlayerByPlayerId(entry.getKey());
            if (player == null || !player.isOnline())
                continue;
            MessageUtils.send(player, builder);
        }
    }

    public void sendPrivateMsg(Player player, int targetId, String content) {
        PrivateChatHandlerParam chatObject = new PrivateChatHandlerParam(player.getPlayerId(),
                player.getPlayerName(), player.getLevel(),
                player.getHeroManager().getHelpFightHeroCid(), targetId, content);
        try {
            ChatPrivateProductService.getDefault().sendChatPrivate(player, chatObject);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.error("send private msg error", e);
        }
    }

    public void sendTeamChatMsg(Player player, String content) {
        long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
        if (teamId == 0L) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_TEAM,
                    player.getPlayerId() + "不存在队伍id");
            return;
        }
        TeamInfo teamInfo = LogicScriptsUtils.getTeamScript().getTeamInfo(teamId);
        if (teamInfo == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_TEAM,
                    player.getPlayerId() + "不存在队伍" + teamId);
            return;
        }
        if (teamInfo.getMembers() != null && teamInfo.getMembers().size() > 0) {
            for (TeamMember member : teamInfo.getMembers().values()) {
                TeamChatHandlerParam chatObject = new TeamChatHandlerParam(player.getPlayerId(),
                        player.getPlayerName(), player.getLevel(),
                        player.getHeroManager().getHelpFightHeroCid(), member.getPid(), content);
                try {
                    ChatPrivateProductService.getDefault().sendChatTeam(player, chatObject);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error("send team chat msg error", e);
                }
            }
        }
    }

    public void notifyTeamMsg(int receiverId, TeamChatHandlerParam chatObject) {
        Player player = PlayerManager.getPlayerByPlayerId(receiverId);
        if (player == null || !player.isOnline())
            return;
        ChatInfo.Builder builder = ChatInfo.newBuilder().setChannel(ChatChannelType.TEAM)
                .setFun(ChatFunctionType.CHAT).setHelpFightHeroCid(chatObject.getSenderHeroCid())
                .setContent(chatObject.getContent()).setPid(chatObject.getSenderId())
                .setPname(chatObject.getSenderName()).setLvl(chatObject.getSenderLevel());
        MessageUtils.send(player, builder);
    }

    public void notifyPrivateMsg(int receiverId, PrivateChatHandlerParam chatObject) {
        Player player = PlayerManager.getPlayerByPlayerId(receiverId);
        if (player == null || !player.isOnline())
            return;
        ChatInfo.Builder builder = ChatInfo.newBuilder().setChannel(ChatChannelType.PRIVATE)
                .setFun(ChatFunctionType.CHAT).setHelpFightHeroCid(chatObject.getSenderHeroCid())
                .setContent(chatObject.getContent()).setPid(chatObject.getSenderId())
                .setPname(chatObject.getSenderName()).setLvl(chatObject.getSenderLevel());
        MessageUtils.send(player, builder);
    }

    /** 发送系统消息（跑马灯） */
    public void sendSystemMsg(String content) {
        List<Player> players = PlayerManager.getAllPlayers();
        for (Player p : players) {
            if (!p.isOnline()) {
                continue;
            }
            sendSystemMsg(p, content);
        }
    }

    /** 发送系统消息（跑马灯） */
    public void sendSystemMsg(Player p, String content) {
        ChatInfo.Builder builder =
                ChatInfo.newBuilder().setChannel(ChatChannelType.SYSTEM)
                        .setFun(ChatFunctionType.CHAT).setHelpFightHeroCid(0).setContent(content)
                        .setPid(0).setPname(GameDataManager.getStringCfgBean(600003).getText())
                        .setLvl(0);
        MessageUtils.send(p, builder);
    }

    private Map<Integer, Integer> getRoomOnlineNum() {
        Map<Integer, Integer> map = new HashMap<>();
        for (Entry<Integer, Integer> entry : playerIdRoomIdMap.entrySet()) {
            int roomId = entry.getValue();
            Integer num = map.get(roomId);
            if (num == null) {
                map.put(roomId, 1);
            } else {
                map.put(roomId, num + 1);
            }
        }
        return map;
    }


    public boolean isGM(String content) {
        // 确定只有 测试服才能用?
        if (!GameServer.getInstance().isTestServer()) {
            return false;
        }
        content = content.trim();
        if (content.startsWith("./")) {
            content = content.substring(2);
            List<String> cmds = Splitter.on(StrUtil.SPACE).omitEmptyStrings().splitToList(content);
            if (cmds.isEmpty()) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 执行GM命令
     * 
     * @param player
     * @param content
     * @return
     */
    public boolean executeGmCommand(Player player, String content) {
        if (isGM(content)) {
            content = content.substring(2);
            List<String> cmds = Splitter.on(StrUtil.SPACE).omitEmptyStrings().splitToList(content);

            String key = cmds.get(0);
            switch (key) {
                case "setDate": {
                    // String date = ToolList.getString(1, cmds);
                    // String time = ToolList.getString(2, cmds);
                    // if (setSystemDate(date, time)) {
                    // // 设置成功了发送公告
                    // IMessage msg = notifySystemDate(String.format("<%s> Update System Date ->
                    // %s", player.getName(),
                    // DateUtil.date().toString()));
                    // OnlinePlayerCache.sendMsgByOnlinePlayer(msg);
                    //
                    // // 更新客户端服务器时间
                    // RespServerTime.Builder builder = RespServerTime.newBuilder();
                    // builder.setServerTime((int) (System.currentTimeMillis() / 1000));
                    // msg = MessageManager.me().create(Core.getServerTime,
                    // builder.build().toByteArray());
                    // OnlinePlayerCache.sendMsgByOnlinePlayer(msg);
                    // }
                    break;
                }
                case "nowDate": {
                    // IMessage msg = notifySystemDate("System Date : " +
                    // DateUtil.date().toString());
                    // player.getPlayerProxy().sendMsg(msg);
                    break;
                }
                case "stopserver": {// 停服
                    System.exit(0);
                    break;
                }
                case "addItems": {
                    int goodsId = Integer.parseInt(cmds.get(1));
                    int num = Integer.parseInt(cmds.get(2));
                    player.getBagManager().addItem(goodsId, num, true, EReason.ITEM_GM);
                    break;
                }
                case "addItemList": {
                    if (cmds.size() > 1) {
                        String itemStr = cmds.get(1);
                        List<String> itemList =
                                Splitter.on(StrUtil.COMMA).omitEmptyStrings().splitToList(itemStr);
                        Map<Integer, Integer> itemMap = new HashMap<>();
                        for (String temp : itemList) {
                            List<String> item =
                                    Splitter.on(StrUtil.COLON).omitEmptyStrings().splitToList(temp);
                            int itemId = Integer.parseInt(item.get(0));
                            int itemNum = Integer.parseInt(item.get(1));
                            itemMap.put(itemId, itemNum);
                        }
                        player.getBagManager().addItems(itemMap, true, EReason.ITEM_GM);
                    }
                    break;
                }
                case "delItems": {
                    int goodsId = Integer.parseInt(cmds.get(1));
                    int num = Integer.parseInt(cmds.get(2));
                    player.getBagManager().removeItemByTempId(goodsId, num, true, EReason.ITEM_GM);
                    break;
                }
                case "delItemList": {
                    String itemStr = cmds.get(1);
                    List<String> itemList =
                            Splitter.on(StrUtil.COMMA).omitEmptyStrings().splitToList(itemStr);
                    Map<Integer, Integer> itemMap = new HashMap<>();
                    for (String temp : itemList) {
                        List<String> item =
                                Splitter.on(StrUtil.COLON).omitEmptyStrings().splitToList(temp);
                        int itemId = Integer.parseInt(item.get(0));
                        int itemNum = Integer.parseInt(item.get(1));
                        itemMap.put(itemId, itemNum);
                    }
                    player.getBagManager().removeItemsByTemplateIdWithCheck(itemMap, true,
                            EReason.ITEM_GM);
                    break;
                }
                case "delItemByIds": {
                    String itemStr = cmds.get(1);
                    String reason = cmds.get(2);
                    List<String> itemList =
                            Splitter.on(StrUtil.COMMA).omitEmptyStrings().splitToList(itemStr);
                    Map<Long, Integer> itemMap = new HashMap<>();
                    for (String temp : itemList) {
                        List<String> item =
                                Splitter.on(StrUtil.COLON).omitEmptyStrings().splitToList(temp);
                        long itemId = Long.parseLong(item.get(0));
                        Item _Item = player.getBagManager().getItemCopy(itemId, reason);
                        if (_Item != null && _Item instanceof EquipItem) {
                            EquipItem equipItem = (EquipItem) _Item;
                            if (equipItem.getPosition() != 0 && equipItem.getHeroId() != 0) {
                                continue;
                            }
                        }
                        int itemNum = Integer.parseInt(item.get(1));
                        itemMap.put(itemId, itemNum);
                    }
                    player.getBagManager().removeItemsByIds(itemMap, true, EReason.ITEM_GM);
                    break;
                }
                case "cleanUnUsedItems": {
                    player.getBagManager().removeUnUsedItems(EReason.ITEM_GM);
                    break;
                }
                case "setPlv": {
                    // int newPlv = ToolList.getInt(1, cmds);
                    // int upLvl = player.getLevel();
                    // long totalExp =
                    // (LevelUpCfgCache.me().getById(player.getLevel()).getPlayerExp() -
                    // player.getExp());
                    // for (int i = upLvl + 1; i < newPlv; i++) {
                    // LevelUpCfg cfg = LevelUpCfgCache.me().getById(i);
                    // if (cfg == null) {
                    // break;
                    // }
                    // totalExp += cfg.getPlayerExp();
                    // }
                    // PlayerManager.me().addExp(player, totalExp);
                    break;
                }
                case "addExp": {
                    // int exp = ToolList.getInt(1, cmds);
                    // PlayerManager.me().addExp(player, exp);
                    break;
                }
                case "addHeroExp": {
                    // int exp = ToolList.getInt(1, cmds);
                    // Long heroId = ToolList.getLong(2, cmds);
                    // Hero hero = player.getPlayerProxy().getHerosByDbId().get(heroId);
                    // ToolError.isAndTrue(GameErrorCode.NOT_FOND_HERO, hero == null);
                    // HeroManager.me().addExp(exp, hero);
                    break;
                }
                case "addTeamExp": {
                    // int exp = ToolList.getInt(1, cmds);
                    // HeroManager.me().addTeamExp(player, exp);
                    break;
                }
                case "changeName": {
                    String name = cmds.get(1);
                    player.setPlayerName(name);
                    PlayerInfo.Builder builder = PlayerInfo.newBuilder();
                    builder.setName(player.getPlayerName());
                    MessageUtils.send(player, builder);
                    break;
                }
                case "reloadgamedata": {
                    try {
                        GameDataManager.Ainit();
                    } catch (Exception e) {
                        LOGGER.error(ConstDefine.LOG_ERROR_CONFIG_PREFIX + ExceptionEx.e2s(e));
                    }
                    break;
                }
                case "reloadScript": {
                    // Map<String, String> parameters = new HashMap<String, String>();
                    // if (cmds.size() > 1) {
                    // String scriptName = cmds.get(1);
                    // if (!"".equals(scriptName)) {
                    // parameters.put("file", scriptName);// 默认重载所有脚本，可指定重载单个脚本
                    // }
                    // }
                    // ScriptJavaLoader.getInstance().loadScript(parameters);
                    break;
                }
                case "mc": {
                    // int goodsId = ToolList.getInt(1, cmds);
                    // GameRechargeManager.testDeliveryOrderGoods(player.getId(),
                    // UUID.randomUUID().toString(), goodsId);
                    break;
                }
                case "room": {
                    // RoomTest.dispatcher(player, cmds);
                    break;
                }
                case "allec": {
                    // byte b = 19;
                    // ElementCollectBO elementCollectBO = IServer.getModules().getModule(b,
                    // ElementCollectBO.class);
                    // elementCollectBO.getAllElement(player.getPlayerProxy().getSession(),
                    // C2SElementCollectMsg.GetAllElement.newBuilder().build());
                    break;
                }
                case "dt": {// 约会
                    // DatingTest.dispatcher(player, cmds);
                    break;
                }
                case "ro": {// 看板娘
                    player.getRoleManager().donate("101", 530111, 1);
                    break;
                }
                case "addRoleFavor": {// 看板娘
                    int roleId = Integer.parseInt(cmds.get(1));
                    int num = Integer.parseInt(cmds.get(2));
                    Role role = player.getRoleManager().getRole(roleId);
                    player.getRoleManager().changeFavor(role, num, EReason.ITEM_GM);
                    break;
                }
                case "build": {
                    // BuildingTest.dispatcher(player, cmds);
                    break;
                }
                case "endlessLevel": {
                    // int level = Integer.parseInt(cmds.get(1));
                    // PlayerData playerData = player.getPlayerDatasFkPlayerId();
                    // PlayerDataProxy pdProxy = playerData.getPlayerDataProxy();
                    // EndlessVO endlessVO = pdProxy.getEndlessVO();
                    // int initLevelId = EndlessCloisterManager.me().getInitStageId();
                    // int newLevelId = initLevelId / 1000 * 1000 + level;
                    // EndlessCloisterLevelCfg cfg =
                    // EndlessCloisterLevelCfgCache.me().getById(newLevelId);
                    // if (cfg == null) {
                    // return false;
                    // }
                    // endlessVO.setLastPassTime(System.currentTimeMillis());
                    // // 计算下一关卡
                    // int nextStageId = newLevelId + 1;
                    // cfg = EndlessCloisterLevelCfgCache.me().getById(newLevelId);
                    // if (cfg == null) {
                    // nextStageId = -1;
                    // }
                    // endlessVO.setNowStage(nextStageId);
                    // int bestStage = newLevelId;// 今日最高通关关卡id
                    // endlessVO.setTodayBest(bestStage);// 因为后续要根据关卡id发奖，所以这里保存的是关卡id
                    // int best = EndlessCloisterLevelCfgCache.me().getById(bestStage).getOrder();
                    // if (best > endlessVO.getHistoryBest()) {
                    // endlessVO.setHistoryBest(best);
                    // }
                    // pdProxy.save();
                    // RspEndlessCloisterInfo info =
                    // EndlessCloisterMsgBuilder.createEndlessCloisterInfo(player);
                    // player.getPlayerProxy()
                    // .sendMsg(MessageManager.me().create(EndlessCloisterBO.getInfo,
                    // ProtoUnit.toByte(info)));
                    //
                    // List<RewardsMsg> rewards = new ArrayList<RewardsMsg>();
                    // RspPassStageEndless pInfo =
                    // EndlessCloisterMsgBuilder.createRspPassStageEndless(endlessVO, rewards,
                    // nextStageId);
                    // player.getPlayerProxy()
                    // .sendMsg(MessageManager.me().create(EndlessCloisterBO.passStage,
                    // ProtoUnit.toByte(pInfo)));
                    break;
                }
                case "pass": {
                    int cid = Integer.parseInt(cmds.get(1));
                    DungeonLevelCfgBean levelCfg = GameDataManager.getDungeonLevelCfgBean(cid);
                    if (levelCfg == null)
                        MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, String.valueOf(cid), " not exists");
                    pass(player, levelCfg);
                    break;
                }
                case "passGroup": {
                    int cid = Integer.parseInt(cmds.get(1));
                    DungeonLevelGroupCfgBean group =
                            GameDataManager.getDungeonLevelGroupCfgBean(cid);
                    if (group == null)
                        MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, String.valueOf(cid), " not exists");
                    passGroup(player, group);
                    break;
                }
                case "passAll": {
                    List<DungeonLevelGroupCfgBean> groups =
                            GameDataManager.getDungeonLevelGroupCfgBeans();
                    for (DungeonLevelGroupCfgBean dungeonLevelGroupCfg : groups) {
                        if (dungeonLevelGroupCfg
                                .getDungeonType() == DungeonTypeConstant.DUNGEON_TYPE_MAIN_LINE
                                || dungeonLevelGroupCfg
                                        .getDungeonType() == DungeonTypeConstant.DUNGEON_TYPE_GENERAL) {
                            passGroup(player, dungeonLevelGroupCfg);
                        }
                    }
                    break;
                }
                case "createMail": {
                    // 创建测试邮件
                    int count = 1;
                    if (cmds.size() > 1) {
                        count = Integer.parseInt(cmds.get(1));
                    }
                    if (player.getGold() > GameDataManager.getBaseGoods(ItemConstantId.GOLD)
                            .getTotalMax()) {
                        player.setGold(0);// 重新清零
                    }
                    Map<Integer, Integer> items = new HashMap<Integer, Integer>();
                    items.put(ItemConstantId.GOLD, 1);
                    for (int i = 0; i < count; i++) {
                        MailService.getInstance().sendPlayerMail(false, player.getPlayerId(),
                                "测试邮件", "测试邮件-有附件", items, EReason.MAIL_GM);
                    }
                    break;
                }
                case "addFavor": {
                    int roleId = Integer.parseInt(cmds.get(1));
                    int num = Integer.parseInt(cmds.get(2));
                    player.getRoleManager().changeFavor(player.getRoleManager().getRole(roleId),
                            num, null);
                    break;
                }
                case "serverInfo": {
                    String info =
                            "serverId:" + ServerConfig.getInstance().getServerId() + " serverName:"
                                    + ServerConfig.getInstance().getServerName();
                    sendSystemMsg(player, info);
                    break;
                }
                default:
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private void passGroup(Player player, DungeonLevelGroupCfgBean group) {
        List<DungeonLevelCfgBean> levelCfgs = GameDataManager.getDungeonLevelCfgBeans().stream()
                .filter(levelCfg -> levelCfg.getLevelGroupServerID() == group.getId())
                .collect(Collectors.toList());
        for (DungeonLevelCfgBean dungeonLevelCfg : levelCfgs) {
            pass(player, dungeonLevelCfg);
        }
    }

    private void pass(Player player, DungeonLevelCfgBean levelCfg) {
        // 跳过卡巴拉
        if(levelCfg.getDungeonType() == 11)
            return;
        int star = levelCfg.getStarParam() == null ? 1 : levelCfg.getStarParam().length;
        List<Integer> achieveGoals = Lists.newArrayList();
        for (int i = 1; i <= star; i++) {
            achieveGoals.add(i);
        }

        // 关卡记录
        int oldStar = 0;
        int nowStar = star;
        DungeonBean dungeonLevel =
                player.getDungeonManager().getOrInitDungeonBean(levelCfg.getId());
        oldStar = dungeonLevel.getStar();
        dungeonLevel.setAchieveGoals(achieveGoals);
        dungeonLevel.setStar(star);
        dungeonLevel.setWin(true);
        DungeonGroupBean groupBean =
                player.getDungeonManager().getOrInitDungeonLevelGroup(player,
                        levelCfg.getLevelGroupServerID());
        if (nowStar > oldStar) {
            int preStars = groupBean.getStars().getOrDefault(levelCfg.getDifficulty(), 0);
            groupBean.getStars().put(levelCfg.getDifficulty(), preStars + (nowStar - oldStar));
        }

        // 暂时没有外部可用的关卡事件入口，本地代码支持
        sceneEvent(player, dungeonLevel, oldStar, nowStar);

        MessageUtils.send(player,
                DungeonMsgBuilder.getSceneWinMsg(dungeonLevel, Lists.newArrayList()));
    }

    /** 触发关卡事件 */
    public void sceneEvent(Player player, DungeonBean dungeonLevel, int oldStar, int nowStar) {
        DungeonLevelCfgBean dungeonLevelCfgBean =
                GameDataManager.getDungeonLevelCfgBean(dungeonLevel.getCid());
        if (dungeonLevelCfgBean == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(dungeonLevel.getCid()), "] not exists");
        DungeonLevelGroupCfgBean groupCfgBean =
                GameDataManager.getDungeonLevelGroupCfgBean(dungeonLevelCfgBean
                        .getLevelGroupServerID());
        if (groupCfgBean == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelGroupCfgBean [",
                    String.valueOf(dungeonLevelCfgBean.getLevelGroupServerID()), "] not exists");

        // 触发事件
        Map<String, Object> in = Maps.newHashMap();
        // 关卡ID
        in.put(EventConditionKey.DUNGEON_CID, dungeonLevel.getCid());
        // 本次星数
        in.put(EventConditionKey.STAR, dungeonLevel.getStar());
        // 关卡难度
        in.put(EventConditionKey.DIFFICULTY, dungeonLevelCfgBean.getDifficulty());

        // 新增3星关卡通关
        if (oldStar != nowStar && nowStar == DungeonConstant.CHAPTER_MAX_STAR) {
            in.put(EventConditionKey.FIRST_3_STAR, 1);
        }
        // 新增星数
        int addStar = nowStar - oldStar;
        if (addStar > 0) {
            in.put(EventConditionKey.ADD_STAR, addStar);
        }
        // 参与战斗英雄数
        // Formation formation = player.getPlayerProxy().getCurrentFormation();
        // in.put(EventConditionKey.FIGHT_HERO_COUNT, formation.getStance().size());
        // 战斗持续时间
        in.put(EventConditionKey.FIGHT_TIME, 0);
        // if (msg != null) {
        // 本次最大连击数
        in.put(EventConditionKey.BATTER, Integer.MAX_VALUE);
        // 拾取数量
        in.put(EventConditionKey.PICK_UP_COUNT, Integer.MAX_VALUE);
        // 拾取类型数量
        in.put(EventConditionKey.PICK_UP_TYPE_COUNT, Integer.MAX_VALUE);
        // }

        // 副本类型
        int dungeonType = groupCfgBean == null ? 0 : groupCfgBean.getDungeonType();
        in.put(EventConditionKey.DUNGOEN_TYPE, dungeonType);
        player._fireEvent(in, EEventType.PASS_DUP.value());
        player._fireEvent(in, EEventType.MEDIA_EVENT.value());
        // 触发主线章节激活通知
        player._fireEvent(null, EEventType.MAINDATING_ACTIVE.value());
        // 关卡检测
        Map<String, Object> info = Maps.newHashMap();
        info.put(BuildingConstant.EVENT_CONDITION_ID, BuildingConstant.PASS_DUP);
        info.put(BuildingConstant.EVENT_RESULT_DATA, dungeonLevel.getCid());
        player._fireEvent(info, EEventType.CHECK_UNLOCK_BUILDING.value());
    }

    public static ChatService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        ChatService instance;

        private Singleton() {
            instance = new ChatService();
        }

        ChatService getInstance() {
            return instance;
        }
    }
    //
    // public static void main(String[] args) throws IOException {
    // Runtime.getRuntime().exec("cmd /c time 03:00:00");
    // System.out.println("ok");
    // }
    //
    // // 设置系统时间
    // private boolean setSystemDate(String date, String time) {
    // boolean bool = false;
    // try {
    // DateTime dateTime = DateUtil.parse((date + " " + time).trim());
    //
    // ToolError.isAndTrue(GameErrorCode.CLIENT_PARAM_IS_ERR, dateTime.before(DateUtil.date()));
    // if (SystemUtil.getOsInfo().isWindows()) {
    // Runtime.getRuntime().exec("cmd /c date " + date);
    // Runtime.getRuntime().exec("cmd /c time " + time);
    // bool = true;
    // } else if (SystemUtil.getOsInfo().isLinux()) {
    // Runtime.getRuntime().exec("date -s " + date);
    // Runtime.getRuntime().exec("date -s " + time);
    // bool = true;
    // }
    //
    // if (bool) {
    // Thread.sleep(100);
    // }
    // } catch (Exception e) {
    // log.error(e);
    // }
    // return bool;
    // }
    //
    // /** 公告系统时间 */
    // private IMessage notifySystemDate(String content) {
    // ChatInfo.Builder builder = ChatInfo.newBuilder();
    // builder.setChannel(ChatConstant.CHANNEL_SYSTEM);
    // builder.setFun(ChatConstant.FUN_CHAT);
    // builder.setContent(content);
    // builder.setPid(0);
    // builder.setPname("System");
    // builder.setLvl(0);
    // builder.setHelpFightHeroCid(0);
    // IMessage msg = MessageManager.me().create(ChatBO.pushChatMsg, builder.build().toByteArray());
    // return msg;
    // }
}
