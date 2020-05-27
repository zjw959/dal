package javascript.logic.friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;
import org.game.protobuf.s2c.S2CFriendMsg.RespFriends;
import org.springframework.util.StringUtils;

import com.ServerListManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import com.jarvis.cache.to.CacheWrapper;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import db.game.bean.PlayerDBBean;
import db.game.daoimp.PlayerDaoImp;
import db.game.service.PlayerDaoService;
import exception.AbstractLogicModelException;
import kafka.service.FriendProducerService;
import kafka.team.param.g2g.NotifyFriendEx;
import kafka.team.param.g2g.NotifyObj;
import logic.character.PlayerManager;
import logic.character.PlayerSnapService;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.DiscreteDataID;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.FriendConstant;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.dungeon.DungeonManager;
import logic.friend.FriendManager;
import logic.friend.FriendService;
import logic.friend.IFriendScript;
import logic.friend.bean.FriendInfoBean;
import logic.friend.bean.FriendView;
import logic.friend.handler.LGetFriendsInfoFinishHandler;
import logic.gloabl.GlobalService;
import logic.msgBuilder.FriendMsgBuilder;
import logic.support.MessageUtils;
import redis.ExternalRedisSerializer;
import redis.base.PlayerSnap;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;
import redis.service.ERedisType;
import redis.service.ESpringConextType;
import redis.service.RedisServices;
import server.GameServer;
import server.ServerConfig;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;
import utils.DateEx;
import utils.ExceptionEx;
import utils.GsonUtils;
import utils.MapEx;
import utils.SpringContextUtils;
import utils.TimeUtil;
import utils.ToolMap;
import utils.pool.LRUCache;

public class FriendScript extends IFriendScript {

    /** 已加载过的等级set */
    private static Set<Integer> LOAD_LEVEL_FROM_DB = new HashSet<>();
    @Override
    public int getScriptId() {
        return EScriptIdDefine.FRIEND_SCRIPT.Value();
    }

    private transient static final Logger LOGGER = Logger.getLogger(FriendScript.class);
    public transient static String preApply = "friend::apply_";
    public transient static String preSendApply = "friend::sendApply_";
    public transient static String preBlack = "friend::black_";
    public transient static String preRelationIndex = "friend::relIndex_";
    public transient static String preRelation = "friend::relation_";

    // 效率评估打印值
    public transient static AtomicLong totalFindTime = new AtomicLong();
    public transient static AtomicLong localBingoTime = new AtomicLong();
    public transient static AtomicLong redisBingoTime = new AtomicLong();
    public transient static AtomicLong dbFindTime = new AtomicLong();
    /** 找齐次数 **/
    public transient static AtomicLong findAllTime = new AtomicLong();

    /**
     * 创建好友信息
     * 
     * @param friend
     * @return
     */
    private static FriendInfoBean createFriendInfoBean(PlayerSnap p, int ct, int status,
            int friendAction, long createTime, long lastSendTime, boolean canRecv) {
        FriendInfoBean friendInfo = new FriendInfoBean();
        // 封装玩家信息
        friendInfo.setPid(p.playerId);
        friendInfo.setName(p.name);
        int heroId = p.heroId;
        if (heroId != 0) {
            friendInfo.setLeaderCid(p.heroId);// 助战英雄
        } else {
            LOGGER.warn("找不到玩家的助战英雄, 使用默认id: 110101. playerId:" + p.playerId);
            friendInfo.setLeaderCid(110101);// 助战英雄
        }
        friendInfo.setFightPower(p.fightpower);
        friendInfo.setLvl(p.level);
        friendInfo.setLastLoginTime(p.lastLoginTime);
        // 封装好友特有信息
        friendInfo.setLastHandselTime(lastSendTime);// 上次赠送友情点的时间
        friendInfo.setReceive(canRecv);// 是否能领取友情点
        friendInfo.setStatus(status);
        // 封装在线信息
        friendInfo.setOnline(p.isOnline);
        friendInfo.setCreateTime(createTime);
        friendInfo.setCt(ct);
        friendInfo.setFriendAction(friendAction);
        return friendInfo;
    }

    /**
     * 通知客户端好友信息变化
     * 
     */
    private static void notifyFriendInfos(Player player, List<FriendInfo.Builder> infos)
            throws AbstractLogicModelException {
        if (player != null && player.isOnline()) {
            RespFriends.Builder resp = FriendMsgBuilder.createRespFriendsMsg(player, infos);
            MessageUtils.send(player, resp);
        }
    }

    /** 返回获取好友列表信息 */
    public void resGetFriendsInfoMsg(Player player, List<FriendInfoBean> infos) {
        S2CFriendMsg.RespFriends.Builder builder = S2CFriendMsg.RespFriends.newBuilder();
        List<FriendInfo.Builder> msgs = FriendMsgBuilder.createFriendInfo(infos);
        // 设置助战好友cd
        DungeonManager dm = player.getDungeonManager();
        for (FriendInfo.Builder msg : msgs) {
            msg.setHelpCDtime((int) (dm.getHelpFightCD(msg.getPid()) / 1000));
            builder.addFriends(msg);
        }
        FriendManager manager = player.getFriendManager();
        builder.setReceiveCount(manager.getRecvGiftCount());
        builder.setLastReceiveTime((int) (manager.getLastRecvGiftTime() / 1000));
        MessageUtils.send(player, builder);
    }

    /** 是否是好友 */
    public boolean isFriend(Player player, int pid) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String relIndexKey = preRelationIndex + player.getPlayerId();
            // 判断是否是好友
            return jedis.hexists(relIndexKey, String.valueOf(pid));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取推荐好友
     * 
     * 
     * @throws Exception
     */
    public void getRecommendFriends(Player player, boolean isCheckCD, boolean isSend)
            throws Exception {

        FriendManager _friendManager = player.getFriendManager();
        if (_friendManager == null) {
            return;
        }
        DiscreteDataCfgBean cfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FRIEND_CONFIG);

        // 刷新CD
        if (isCheckCD) {
            int cd = ToolMap.getInt("refreshCd", cfg.getData(), 2000);
            if (GameServer.getInstance().isIDEMode()) {
                cd = 100;
            }
            long freshtime = System.currentTimeMillis() - _friendManager.getLastFreshTime();
            if (freshtime < cd) {
                MessageUtils.throwCondtionError(GameErrorCode.THE_OPERATION_IS_TOO_FREQUENT,
                        "freeTime:" + freshtime);
            }
        }
        // 需要过滤的玩家
        List<Integer> filterPids = Lists.newArrayList();
        filterPids.add(player.getPlayerId());
        // 排除好友
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String friendKey = preRelationIndex + player.getPlayerId();
            Set<String> friends = jedis.hkeys(friendKey);
            for (String string : friends) {
                filterPids.add(Integer.valueOf(string));
            }

            String applyKey = preApply + player.getPlayerId();
            Set<String> applys = jedis.zrange(applyKey, 0, -1);
            for (String string : applys) {
                filterPids.add(Integer.valueOf(string));
            }

            String sendApplyKey = preSendApply + player.getPlayerId();
            Set<String> sendApplys = jedis.zrange(sendApplyKey, 0, -1);
            for (String string : sendApplys) {
                filterPids.add(Integer.valueOf(string));
            }

            String blackKey = preBlack + player.getPlayerId();
            Set<String> blacks = jedis.zrange(blackKey, 0, -1);
            for (String string : blacks) {
                filterPids.add(Integer.valueOf(string));
            }
        } catch (Exception e) {
            throw e;
        }

        boolean isFindAll = false;
        // List<Player> recommend = Lists.newArrayList();

        Map<Integer, FriendView> recommendKV = new HashMap<>();

        int _freshCount = _friendManager.getFreshCount();
        boolean _isSearchFromLocal =
                _freshCount % GlobalService.getInstance().getRecommandFriendHitLocalRate() != 0;
        // 每请求5次则跳过从本服查找. 避免本服虽然都满足查找条件,但样本过小并且都不是玩家想要的情况则必须跨服查找.
        int count = ToolMap.getInt("friendRecommendation", cfg.getData(), 10);
        if (_isSearchFromLocal) {
            isFindAll = searchFromLocal(player, count, filterPids, recommendKV, _freshCount);
        }


        int _playerLvl = player.getLevel();
        if (!isFindAll) {
            // 随机区间 否则每次刷新后用户一致
            Map<Integer, FriendView> _lvlPlayer = FriendService.PLAYER_LEVEL_MAP.get(_playerLvl);
            if (_lvlPlayer != null && !_lvlPlayer.isEmpty()) {
                recommendKV.putAll(filterRecommandFriends(_lvlPlayer, filterPids));
            }
            // 如果id不足 去上下五个等级取
            if (recommendKV.size() < count) {
                for (int _index = 1; _index <= 5; _index++) {
                    Map<Integer, FriendView> _lvlMap = filterRecommandFriends(
                            FriendService.PLAYER_LEVEL_MAP.get(_playerLvl + _index), filterPids);
                    if (_lvlMap != null && !_lvlMap.isEmpty()) {
                        recommendKV.putAll(_lvlMap);
                    }
                    if (recommendKV.size() >= count) {
                        break;
                    }
                    _lvlMap = filterRecommandFriends(
                            FriendService.PLAYER_LEVEL_MAP.get(_playerLvl - _index), filterPids);
                    if (_lvlMap != null && !_lvlMap.isEmpty()) {
                        recommendKV.putAll(_lvlMap);
                    }
                    if (recommendKV.size() >= count) {
                        break;
                    }
                }
            }

            if (!recommendKV.isEmpty()) {
                if (recommendKV.size() >= count) {
                    isFindAll = true;
                    redisBingoTime.incrementAndGet();
                }
            }
            // 如果redis查不够并且此没有从local查询过，则这里从local取一遍
            if (!isFindAll && !_isSearchFromLocal) {
                isFindAll = searchFromLocal(player, count, filterPids, recommendKV, _freshCount);
            }
        }
        // 从交叉好友开始找

        // 从数据库取玩家凑齐
        if (!isFindAll) {
            Map<Integer, FriendView> _lvlPlayer = FriendService.PLAYER_LEVEL_MAP_FROM_DB.get(_playerLvl);
            if (_lvlPlayer != null && !_lvlPlayer.isEmpty()) {
                recommendKV.putAll(filterRecommandFriends(_lvlPlayer, filterPids));
            }
            // 如果id不足 去上下五个等级取
            if (recommendKV.size() < count) {
                for (int _index = 1; _index <= 5; _index++) {
                    Map<Integer, FriendView> _lvlMap = filterRecommandFriends(
                            FriendService.PLAYER_LEVEL_MAP_FROM_DB.get(_playerLvl + _index), filterPids);
                    if (_lvlMap != null && !_lvlMap.isEmpty()) {
                        recommendKV.putAll(_lvlMap);
                    }
                    if (recommendKV.size() >= count) {
                        break;
                    }
                    _lvlMap = filterRecommandFriends(
                            FriendService.PLAYER_LEVEL_MAP_FROM_DB.get(_playerLvl - _index), filterPids);
                    if (_lvlMap != null && !_lvlMap.isEmpty()) {
                        recommendKV.putAll(_lvlMap);
                    }
                    if (recommendKV.size() >= count) {
                        break;
                    }
                }
            }
            
            dbFindTime.incrementAndGet();
        }
        totalFindTime.incrementAndGet();

        LOGGER.info("recommend info: totalFindTime:" + totalFindTime.get() + ",本地命中次数:"
                + localBingoTime.get() + ",缓存命中次数:" + redisBingoTime.get() + ",数据库查找次数:"
                + dbFindTime.get());

        List<FriendView> recommends = new ArrayList<FriendView>(recommendKV.values());

        // 排序 并 保留10人
        Collections.sort(recommends, new Comparator<FriendView>() {
            @Override
            public int compare(FriendView o1, FriendView o2) {
                if ((o1.isOnline() && o2.isOnline()) || (!o1.isOnline() && !o2.isOnline())) {
                    if (o1.getLevel() == o2.getLevel()) {
                        if (o1.getLastLoginTime() == o2.getLastLoginTime()) {
                            return 0;
                        } else if (o1.getLastLoginTime() > o2.getLastLoginTime()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    } else if (o1.getLevel() > o2.getLevel()) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else if (o1.isOnline() && !o2.isOnline()) {
                    return -1;
                } else if (!o1.isOnline() && o2.isOnline()) {
                    return 1;
                } else {
                    LOGGER.error("unknown compare" + o1.isOnline() + o2.isOnline());
                    return -1;
                }
            }
        });

        if (recommends.size() > count) {
            recommends = (List<FriendView>) recommends.subList(0, count);
        }

        _friendManager.setFreshCount(_freshCount + 1);
        _friendManager.setLastFreshTime(System.currentTimeMillis());
        _friendManager.setRecommends(recommends);

        if (isSend) {
            S2CFriendMsg.RespRecommendFriends.Builder builder =
                    S2CFriendMsg.RespRecommendFriends.newBuilder();
            List<FriendInfo.Builder> list = FriendMsgBuilder.createRecommendFriendInfo(recommends);
            for (FriendInfo.Builder b : list) {
                builder.addFriends(b);
            }
            MessageUtils.send(player, builder);
        }
    }


    private boolean searchFromLocal(Player player, int count, List<Integer> filterPids,
            Map<Integer, FriendView> recommendKV, int freshCount) {
        boolean isFindAll = false;
        // 每请求N次则跳过从等级差1开始查找
        int maxdelta = 5;
        int deltaLevel = freshCount % maxdelta;

        int min = player.getLevel() - deltaLevel;
        int max = player.getLevel() + deltaLevel;

        List<Player> players = PlayerManager.getAllPlayerBylevel(min, max);
        // 按照最近登录时间排序
        for (Player p : players) {
            if (filterPids.contains(p.getPlayerId())) {
                continue;
            }
            if (recommendKV.containsKey(p.getPlayerId())) {
                continue;
            }
            recommendKV.put(p.getPlayerId(), new FriendView(p));
            if (recommendKV.size() >= count) {
                isFindAll = true;
                break;
            }
        }

        // 人数不够,则扩大范围
        while (!isFindAll && deltaLevel < maxdelta) {
            deltaLevel++;
            min = player.getLevel() - deltaLevel;
            max = player.getLevel() + deltaLevel;

            if (min > 0) {
                players = PlayerManager.getAllPlayerBylevel(min);
            }

            players.addAll(PlayerManager.getAllPlayerBylevel(max));


            // 按照最近登录时间排序
            for (Player p : players) {

                if (filterPids.contains(p.getPlayerId())) {
                    continue;
                }
                if (recommendKV.containsKey(p.getPlayerId())) {
                    continue;
                }
                HashMap<Integer, FriendView> _temp = new HashMap<>();
                _temp.put(p.getPlayerId(), new FriendView(p));
                recommendKV.putAll(filterRecommandFriends(_temp, filterPids));
                if (recommendKV.size() >= count) {
                    isFindAll = true;
                    break;
                }
            }
        }
        if (isFindAll) {
            localBingoTime.incrementAndGet();
        }
        return isFindAll;
    }

    private Map<Integer, FriendView> filterRecommandFriends(Map<Integer, FriendView> map,
            List<Integer> filterPids) {
        Map<Integer, FriendView> _temp = new HashMap<>();
        if (map == null || map.isEmpty()) {
            return _temp;
        }
        List<FriendView> _players = new ArrayList<>(map.values());
        for (int _index = _players.size() - 1; _index >= 0; _index--) {
            FriendView _player = _players.get(_index);
            if (_player == null) {
                continue;
            }
            if (filterPids.contains(_player.getPlayerId())) {
                _players.remove(_index);
            }
        }
        Collections.shuffle(_players);
        Map<Integer, FriendView> _result = new HashMap<>();
        // 过滤掉目标申请列表满的情况
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            DiscreteDataCfgBean cfg =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FRIEND_CONFIG);
            // 推荐上限
            int _recommendMaxCount = ToolMap.getInt("friendRecommendation", cfg.getData(), 10);
            int _count = 0;
            // 好友上限
            int _maxFriendsNum = ToolMap.getInt("maxRequest", cfg.getData(), 100);
            for (FriendView _player : _players) {
                // 对方被申请次数上限
                Long _targetBeApplyCount =
                        jedis.zcount(preApply + _player.getPlayerId(), 0, Long.MAX_VALUE);
                if (_targetBeApplyCount < _maxFriendsNum) {
                    _result.put(_player.getPlayerId(), _player);
                    _count++;
                    if (_count >= _recommendMaxCount) {
                        break;
                    }
                } else {
                    map.remove(_player.getPlayerId());
                }
            }
        }
        return _result;
    }

    /**
     * 当玩家等级数据过期后，需要重新加载redis数据到内存
     * 
     * 只允许启服调用一次,被调用次数不能超过等级数量
     * 
     * @return
     * @throws Exception
     */
    public void loadRecommendsFromDb(int level) throws Exception {
        if (LOAD_LEVEL_FROM_DB.contains(level)) {
            LOGGER.error(String
                    .format("FriendScript.loadRecommendsFromDb(): lvl = %s load once more", level));
            return;
        }
        LOAD_LEVEL_FROM_DB.add(level);
        String ip = GameServer.getInstance().getLocalIP();
        // 内网测试环境在数据库中直接查找测试
        if (!(GameServer.getInstance().isTestServer()
                && !GameServer.getInstance().isRootDrangServer()) && (ip.startsWith("192.168."))) {
            return;
        }

        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
        // 从redis取完后再在数据库中取出100个玩家数据 作为备用
        int dbCount = 100;
        // 最大等级
        List<PlayerDBBean> beans = new ArrayList<>();
        beans = playerDaoService.selectPlayerViewByNum(0, dbCount, level);
        LRUCache<Integer, FriendView> _playerFromDB = new LRUCache<Integer, FriendView>(dbCount);
        for (PlayerDBBean _data : beans) {
            FriendView _player = new FriendView(_data, true);
            _playerFromDB.put(_data.getPlayerId(), _player);
        }

        FriendService.PLAYER_LEVEL_MAP_FROM_DB.put(level, _playerFromDB);
        LOGGER.info(String.format("FriendScript.loadRecommendsFromDb(): load lvl = %s count = %s",
                level, _playerFromDB.size()));
    }

    /**
     * 进行好友相关操作
     * 
     * @throws Exception
     */
    public void operate(Player player, C2SFriendMsg.ReqOperate req) throws Exception {
        // 各类好友操作
        int type = req.getType();
        switch (type) {
            case FriendConstant.OPERATE_APPLY_FRIEND:
                _apply_friend(player, req.getTargetsList(),
                        C2SFriendMsg.ReqOperate.MsgID.eMsgID_VALUE);
                break;
            case FriendConstant.OPERATE_DELETE_FRIEND:
                _delete_friend(player, req.getTargetsList());
                break;
            case FriendConstant.OPERATE_SHIELD_PLAYER:
                _black_player(player, req.getTargetsList());
                break;
            case FriendConstant.OPERATE_LIFTED_SHIELD:
                _lifted_black_player(player, req.getTargetsList());
                break;
            case FriendConstant.OPERATE_AGREE_APPLY:
                _argee_apply(player, req.getTargetsList());
                break;
            case FriendConstant.OPERATE_REFUSE_APPLY:
                _refuse_apply(player, req.getTargetsList());
                break;
            case FriendConstant.OPERATE_GIVE_GIFT:
                _give_gift_friend(player, req.getTargetsList());
                break;
            case FriendConstant.OPERATE_RECEIVE_GIFT:
                _receive_gift_friend(player, req.getTargetsList());
                break;
            default:
                break;
        }
    }

    /**
     * 申请添加好友
     * 
     * @param list
     * @throws Exception
     */
    private void _apply_friend(Player player, List<Integer> targetsList, int rmsgId)
            throws Exception {

        boolean isFriends = false;// 是否已经是好友
        boolean isBlack = false;// 是否已经是屏蔽对象
        boolean isNotFind = false;// 是否未找到好友信息

        Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();

        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            DiscreteDataCfgBean cfg =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FRIEND_CONFIG);
            int maxFriendsNum = ToolMap.getInt("maxFriendsNum", cfg.getData(), 10);
            String relIndexKey = preRelationIndex + player.getPlayerId();
            Set<String> relIndex = jedis.hkeys(relIndexKey);
            if (relIndex.size() >= maxFriendsNum) {
                MessageUtils.throwCondtionError(GameErrorCode.HAS_REACHED_FRIENDS_LIMIT);
            }
            String blackKey = preBlack + player.getPlayerId();
            Set<String> blacks = jedis.zrange(blackKey, 0, -1);
            // 成功申请的列表
            Set<Integer> resultApply = new HashSet<Integer>();

            for (Integer _playerId : targetsList) {
                // 判断是否是自己
                if (_playerId == player.getPlayerId()) {
                    continue;
                }
                // 判断是否已经是好友
                if (jedis.hexists(relIndexKey, String.valueOf(_playerId))) {
                    isFriends = true;
                    LOGGER.warn(ConstDefine.LOG_ERROR_CONDITION_PREFIX + ",friend exists. targetId:"
                            + _playerId + player.logInfo());
                    continue;
                }
                // 是否是自己的屏蔽对象
                if (blacks.contains(String.valueOf(_playerId))) {
                    isBlack = true;
                    continue;
                }
                // 是否是对方的屏蔽对象
                String _blackKey = preBlack + _playerId;
                Set<String> _blacks = jedis.zrange(_blackKey, 0, -1);
                if (_blacks.contains(String.valueOf(player.getPlayerId()))) {
                    isBlack = true;
                    continue;
                }
                // 判断用户是否存在
                // 首先判断Redis有全数据,则直接redis判断
                if (!PlayerSnapService.getPlayerIsExist(player.getPlayerId())) {
                    PlayerDaoService daoService = SpringContextUtils
                            .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
                    int size = 0;
                    try {
                        size = daoService.selectPlayerNumByPlayerId(_playerId);
                    } catch (Exception e) {
                        LOGGER.error(ExceptionEx.e2s(e));
                    }
                    if (size == 0) {
                        isNotFind = true;
                        continue;
                    }
                }
                // 添加到成功队列
                resultApply.add(_playerId);
            }

            // 写入到redis中
            Iterator<Integer> iterator = resultApply.iterator();
           
            // 好友被申请上限
            int _beApplyCount = ToolMap.getInt("maxRequest", cfg.getData(), 100);
            
            while (iterator.hasNext()) {
                int _playerId = iterator.next();
                // 对方被申请次数
                Long _targetBeApplyCount = jedis.zcount(preApply + _playerId, 0, Long.MAX_VALUE);
                if (_targetBeApplyCount >= _beApplyCount) {
                    // 添加单个玩家 如果对方好友申请达到上限，提示并返回
                    if (targetsList.size() <= 1) {
                        MessageUtils.throwCondtionError(GameErrorCode.TARGET_APPLY_FULL);
                        return;
                    } else {
                        // 批量添加不弹提示 继续添加后续
                        continue;
                    }
                }
                jedis.zadd(preApply + _playerId, System.currentTimeMillis(),
                        String.valueOf(player.getPlayerId()));
                jedis.zadd(preSendApply + player.getPlayerId(), System.currentTimeMillis(),
                        String.valueOf(_playerId));
                // 通知被操作玩家对象
                NotifyFriendEx friendEx = new NotifyFriendEx(player.getPlayerId(),
                        FriendConstant.STATUS_APPLY, System.currentTimeMillis(), 0, false);
                List<NotifyFriendEx> exlist = Lists.newArrayList();
                exlist.add(friendEx);
                NotifyObj obj =
                        new NotifyObj(FriendConstant.CT_ADD, FriendConstant.FRIEND_ADD_EVENT,
                                _playerId, exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }

            // Integer[] integers = resultApply.toArray(new Integer[resultApply.size()]);
            // List<User> listUser= Arrays.asList(DbUtils.list("from User").toArray(new User[0]));

        } catch (Exception e) {
            throw e;
        }

        // 推送到订阅中心
        FriendProducerService.getInstance().sendNotify(notifyObjs);

        // 一键添加 自动返回列表
        if (targetsList.size() > 1) {
            ((PlayerViewService) PlayerViewService.getInstance()).getRecommands(player, rmsgId,
                    false);
        }

        if (isFriends) {
            MessageUtils.throwCondtionError(GameErrorCode.YOU_ARE_ALREADY_FRIENDS,
                    " you are already friends.");
        }
        if (isBlack) {
            MessageUtils.throwCondtionError(GameErrorCode.YOU_HAVE_BEEN_SHIELDED,
                    " you are in black list.");
        }
        if (isNotFind) {
            MessageUtils.throwCondtionError(GameErrorCode.NOR_FONT_PLAYERINFO,
                    " palyer not exists.");
        }

    }

    /**
     * 同意好友申请
     * 
     * @param targetsList
     */
    private void _argee_apply(Player player, List<Integer> targetsList) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {

            DiscreteDataCfgBean cfg =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FRIEND_CONFIG);
            // 好友上限
            int maxFriendsNum = ToolMap.getInt("maxFriendsNum", cfg.getData(), 10);
            String relIndexKey = preRelationIndex + player.getPlayerId();
            Set<String> relIndex = jedis.hkeys(relIndexKey);
            // 自己好友数量是否已达上限
            boolean isSelfMax = false;
            if (relIndex.size() >= maxFriendsNum) {
                isSelfMax = true;
            } else {
                String applyKey = preApply + player.getPlayerId();
                Set<String> applys = jedis.zrange(applyKey, 0, -1);
                // 成功同意的列表
                Set<Integer> resultApply = new HashSet<Integer>();
                Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
                boolean isTarMax = false;
                for (Integer _playerId : targetsList) {
                    // 是否在申请列表中
                    if (!applys.contains(String.valueOf(_playerId))) {
                        LOGGER.warn(ConstDefine.LOG_ERROR_CONDITION_PREFIX
                                + ",apply not exists. targetId:" + _playerId + player.logInfo());
                        continue;
                    }
                    // 判断是否已经好友
                    if (jedis.hexists(relIndexKey, String.valueOf(_playerId))) {
                        LOGGER.warn(ConstDefine.LOG_ERROR_CONDITION_PREFIX
                                + ",friend exists.targetId:" + _playerId + player.logInfo());
                        continue;
                    }
                    // 判断对方好友上限
                    long targetFriendCount = jedis.hkeys(preRelationIndex + _playerId).size();
                    if (targetFriendCount >= maxFriendsNum) {
                        isTarMax = true;
                        continue;
                    }
                    resultApply.add(_playerId);
                }
                List<NotifyFriendEx> exlist = Lists.newArrayList();
                // 添加好友
                Iterator<Integer> iterator = resultApply.iterator();
                while (iterator.hasNext()) {
                    long nowFriendCount = jedis.hkeys(relIndexKey).size();
                    if (nowFriendCount >= maxFriendsNum) {
                        isSelfMax = true;
                        break;
                    }
                    int _playerId = iterator.next();
                    // 注意,对方是否也已经同时同意了

                    // 删除申请列表
                    jedis.zrem(applyKey, String.valueOf(_playerId));
                    // 如果在对方的申请列表里，也一并移除
                    String _applyKey = preApply + _playerId;
                    jedis.zrem(_applyKey, String.valueOf(player.getPlayerId()));
                    // 从已申请列表里删除
                    jedis.zrem(preSendApply + player.getPlayerId(), String.valueOf(_playerId));
                    jedis.zrem(preSendApply + _playerId, String.valueOf(player.getPlayerId()));
                    // 添加好友关系数据
                    // 先检查之前是否存在好友关系数据，如果存在，则继续使用之前的key及赠送相关数据,防止每天重复赠送
                    String relKey = preRelation + _playerId + "_" + player.getPlayerId();
                    Map<String, String> rstMap = jedis.hgetAll(relKey);
                    if (MapEx.isEmpty(rstMap)) {
                        relKey = preRelation + player.getPlayerId() + "_" + _playerId;
                        rstMap = jedis.hgetAll(relKey);
                    }
                    if (MapEx.isEmpty(rstMap)) {
                        HashMap<String, Long> dataMap = new HashMap<String, Long>();
                        dataMap.put("send", 0L);// 上次赠送友情点时间
                        dataMap.put("recv", 0L);// 上次领取友情点时间
                        jedis.hset(relKey, String.valueOf(_playerId), GsonUtils.toJson(dataMap));
                        jedis.hset(relKey, String.valueOf(player.getPlayerId()),
                                GsonUtils.toJson(dataMap));
                    }
                    jedis.hset(relKey, "create", String.valueOf(System.currentTimeMillis()));// 重置关系创建时间

                    // 将好友关系key添加到各自好友索引
                    jedis.hset(preRelationIndex + player.getPlayerId(), String.valueOf(_playerId),
                            relKey);
                    jedis.hset(preRelationIndex + _playerId, String.valueOf(player.getPlayerId()),
                            relKey);

                    // 通知被操作玩家对象
                    long[] _rst = parseFriendData(_playerId, player.getPlayerId(), jedis);
                    long _createTime = _rst[0];
                    long _lastSendTime = _rst[1];
                    boolean _canRecv = _rst[3] == 1;
                    NotifyFriendEx _friendEx = new NotifyFriendEx(player.getPlayerId(),
                            FriendConstant.STATUS_FRIEND, _createTime, _lastSendTime, _canRecv);
                    List<NotifyFriendEx> _exlist = Lists.newArrayList();
                    _exlist.add(_friendEx);
                    NotifyObj obj =
                            new NotifyObj(FriendConstant.CT_ADD, FriendConstant.FRIEND_ADD_EVENT,
                                    _playerId, _exlist, System.currentTimeMillis());
                    _addNotifyFriend(notifyObjs, obj);

                    // 通知操作玩家
                    long[] rst = parseFriendData(player.getPlayerId(), _playerId, jedis);
                    long createTime = rst[0];
                    long lastSendTime = rst[1];
                    boolean canRecv = rst[3] == 1;
                    NotifyFriendEx friendEx = new NotifyFriendEx(_playerId,
                            FriendConstant.STATUS_FRIEND, createTime, lastSendTime, canRecv);

                    exlist.add(friendEx);
                }
                if (exlist.size() > 0) {
                    // 通知操作玩家对象
                    NotifyObj obj =
                            new NotifyObj(FriendConstant.CT_UPDATE, FriendConstant.FRIEND_ADD_EVENT,
                                    player.getPlayerId(), exlist, System.currentTimeMillis());
                    _addNotifyFriend(notifyObjs, obj);
                }

                // 推送到订阅中心
                FriendProducerService.getInstance().sendNotify(notifyObjs);
                // 对方好友数量达到上限
                if (isTarMax) {
                    MessageUtils.throwCondtionError(GameErrorCode.TARGET_HAS_REACHED_FRIENDS_LIMIT,
                            "target friends num is max.");
                }
            }
            // 自己好友数量达到上限
            if (isSelfMax) {
                MessageUtils.throwCondtionError(GameErrorCode.HAS_REACHED_FRIENDS_LIMIT,
                        " friends num is max.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 拒绝好友申请
     * 
     * @param targetsList
     */
    private void _refuse_apply(Player player, List<Integer> targetsList) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {

            String applyKey = preApply + player.getPlayerId();
            Set<String> applys = jedis.zrange(applyKey, 0, -1);
            Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
            List<NotifyFriendEx> exlist = Lists.newArrayList();
            for (Integer _playerId : targetsList) {
                // 是否在申请列表中
                if (!applys.contains(String.valueOf(_playerId))) {
                    continue;
                }
                // 删除申请列表
                jedis.zrem(applyKey, String.valueOf(_playerId));
                jedis.zrem(preSendApply + _playerId, String.valueOf(player.getPlayerId()));
                // 通知操作玩家对象
                NotifyFriendEx friendEx = new NotifyFriendEx(_playerId, FriendConstant.STATUS_APPLY,
                        System.currentTimeMillis(), 0, false);
                exlist.add(friendEx);
            }

            // 推送到客户端
            if (exlist.size() > 0) {
                // 通知操作玩家对象
                NotifyObj obj =
                        new NotifyObj(FriendConstant.CT_DELETE, FriendConstant.FRIEND_DEL_EVENT,
                                player.getPlayerId(), exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }

            // 推送到订阅中心
            FriendProducerService.getInstance().sendNotify(notifyObjs);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 删除好友
     * 
     * @param targetsList
     */
    private void _delete_friend(Player player, List<Integer> targetsList) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String relIndexKey = preRelationIndex + player.getPlayerId();
            // 成功删除列表
            Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
            List<NotifyFriendEx> exlist = Lists.newArrayList();
            boolean exist = true;
            for (Integer _playerId : targetsList) {
                // 判断是否已经不是好友
                if (!jedis.hexists(relIndexKey, String.valueOf(_playerId))) {
                    exist = false;
                    continue;
                }
                // 只删除双方索引,保留关系数据状态
                jedis.hdel(relIndexKey, String.valueOf(_playerId));
                String _relIndexKey = preRelationIndex + _playerId;
                jedis.hdel(_relIndexKey, String.valueOf(player.getPlayerId()));
                // 通知被操作玩家对象
                NotifyFriendEx _friendEx = new NotifyFriendEx(player.getPlayerId(),
                        FriendConstant.STATUS_FRIEND, System.currentTimeMillis(), 0, false);
                List<NotifyFriendEx> _exlist = Lists.newArrayList();
                _exlist.add(_friendEx);
                NotifyObj obj =
                        new NotifyObj(FriendConstant.CT_DELETE, FriendConstant.FRIEND_DEL_EVENT,
                                _playerId, _exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
                // 通知操作玩家对象
                NotifyFriendEx friendEx = new NotifyFriendEx(_playerId,
                        FriendConstant.STATUS_FRIEND, System.currentTimeMillis(), 0, false);
                exlist.add(friendEx);
            }
            // 推送到客户端
            if (exlist.size() > 0) {
                // 通知操作玩家对象
                NotifyObj obj =
                        new NotifyObj(FriendConstant.CT_DELETE, FriendConstant.FRIEND_DEL_EVENT,
                                player.getPlayerId(), exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }
            // 推送到订阅中心
            FriendProducerService.getInstance().sendNotify(notifyObjs);
            if (!exist) {
                MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_FRIEND,
                        "friend is not exist.");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 赠送友情点
     * 
     * @param targetsList
     */
    private void _give_gift_friend(Player player, List<Integer> targetsList) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String relIndexKey = preRelationIndex + player.getPlayerId();
            Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
            List<NotifyFriendEx> exlist = Lists.newArrayList();
            java.lang.reflect.Type type = new TypeToken<HashMap<String, Long>>() {}.getType();
            for (Integer _playerId : targetsList) {
                // 判断是否是好友
                if (!jedis.hexists(relIndexKey, String.valueOf(_playerId))) {
                    continue;
                }
                String relKey = jedis.hget(relIndexKey, String.valueOf(_playerId));
                if (StringUtils.isEmpty(relKey)) {
                    continue;
                }
                // 今天是否已经赠送
                String json = jedis.hget(relKey, String.valueOf(player.getPlayerId()));
                Map<String, Long> data = GsonUtils.fromJson(json, type);
                Long sendTime = data.get("send");
                if (DateEx.isToday(sendTime)) {
                    LOGGER.warn(ConstDefine.LOG_ERROR_CONDITION_PREFIX + ",player has been send."
                            + player.logInfo());
                    continue;
                }
                data.put("send", System.currentTimeMillis());
                jedis.hset(relKey, String.valueOf(player.getPlayerId()), GsonUtils.toJson(data));
                // 通知被操作玩家对象
                long[] _rst = parseFriendData(_playerId, player.getPlayerId(), jedis);
                long _createTime = _rst[0];
                long _lastSendTime = _rst[1];
                boolean _canRecv = _rst[3] == 1;
                NotifyFriendEx _friendEx = new NotifyFriendEx(player.getPlayerId(),
                        FriendConstant.STATUS_FRIEND, _createTime, _lastSendTime, _canRecv);
                List<NotifyFriendEx> _exlist = Lists.newArrayList();
                _exlist.add(_friendEx);
                NotifyObj obj =
                        new NotifyObj(FriendConstant.CT_UPDATE, FriendConstant.FRIEND_ADD_EVENT,
                                _playerId, _exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);

                // 通知操作玩家
                long[] rst = parseFriendData(player.getPlayerId(), _playerId, jedis);
                long createTime = rst[0];
                long lastSendTime = rst[1];
                boolean canRecv = rst[3] == 1;
                NotifyFriendEx friendEx = new NotifyFriendEx(_playerId,
                        FriendConstant.STATUS_FRIEND, createTime, lastSendTime, canRecv);
                exlist.add(friendEx);
            }
            if (exlist.size() > 0) {
                // 通知操作玩家对象
                NotifyObj obj =
                        new NotifyObj(FriendConstant.CT_UPDATE, FriendConstant.FRIEND_ADD_EVENT,
                                player.getPlayerId(), exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }

            // 推送到订阅中心
            FriendProducerService.getInstance().sendNotify(notifyObjs);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 领取赠送的友情点
     * 
     * @param targetsList
     */
    private void _receive_gift_friend(Player player, List<Integer> targetsList) {
        FriendManager _friendManager = player.getFriendManager();
        if (_friendManager == null) {
            return;
        }
        int recvGiftCount = _friendManager.getRecvGiftCount();
        long lastRecvGiftTime = _friendManager.getLastRecvGiftTime();
        // 跨天重置已领取次数
        if (recvGiftCount != 0 && !DateEx.isToday(lastRecvGiftTime)) {
            recvGiftCount = 0;
        }
        // 是否已经达到领取上限了
        DiscreteDataCfgBean cfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FRIEND_CONFIG);
        int receiveMax = ToolMap.getInt("receiveFriendshipTime", cfg.getData(), 60);
        if (recvGiftCount >= receiveMax) {
            MessageUtils.throwCondtionError(GameErrorCode.MAX_RECEIVE_COUNT,
                    " this.receiveGiftCount over max.count=" + recvGiftCount);
            return;
        }
        int friendship = ToolMap.getInt("friendship", cfg.getData(), 10);

        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String relIndexKey = preRelationIndex + player.getPlayerId();
            Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
            List<NotifyFriendEx> exlist = Lists.newArrayList();
            boolean isRecvMax = false;
            int friendPoint = 0;
            for (Integer _playerId : targetsList) {
                if (recvGiftCount >= receiveMax) {
                    isRecvMax = true;
                    break;
                }
                // 判断是否是好友
                if (!jedis.hexists(relIndexKey, String.valueOf(_playerId))) {
                    continue;
                }
                long[] rst = parseFriendData(player.getPlayerId(), _playerId, jedis);
                long createTime = rst[0];
                long lastSendTime = rst[1];
                boolean canRecv = rst[3] == 1;
                if (!canRecv) {// 不能领取
                    continue;
                }
                // 修改redis数据
                String relKey = jedis.hget(relIndexKey, String.valueOf(_playerId));
                if (StringUtils.isEmpty(relKey)) {
                    continue;
                }
                String _json = jedis.hget(relKey, String.valueOf(player.getPlayerId()));
                java.lang.reflect.Type type = new TypeToken<HashMap<String, Long>>() {}.getType();
                Map<String, Long> _data = GsonUtils.fromJson(_json, type);
                _data.put("recv", System.currentTimeMillis());
                jedis.hset(relKey, String.valueOf(player.getPlayerId()), GsonUtils.toJson(_data));
                // _friendManager.setRecvGiftCount(recvGiftCount + 1);
                recvGiftCount = recvGiftCount + 1;
                _friendManager.setRecvGiftCount(recvGiftCount);
                _friendManager.setLastRecvGiftTime(System.currentTimeMillis());
                friendPoint += friendship;
                // 通知被操作玩家对象
                long[] _rst = parseFriendData(_playerId, player.getPlayerId(), jedis);
                long _createTime = _rst[0];
                long _lastSendTime = _rst[1];
                boolean _canRecv = _rst[3] == 1;
                NotifyFriendEx _friendEx = new NotifyFriendEx(player.getPlayerId(),
                        FriendConstant.STATUS_FRIEND, _createTime, _lastSendTime, _canRecv);
                List<NotifyFriendEx> _exlist = Lists.newArrayList();
                _exlist.add(_friendEx);
                NotifyObj obj = new NotifyObj(FriendConstant.CT_UPDATE, 0, _playerId, _exlist,
                        System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);

                // 通知操作玩家
                NotifyFriendEx friendEx = new NotifyFriendEx(_playerId,
                        FriendConstant.STATUS_FRIEND, createTime, lastSendTime, false);
                exlist.add(friendEx);
            }
            // 领取友情点
            if (friendPoint > 0) {
                player.getBagManager().addItem(ItemConstantId.FRIEND_SHIP_POINT, friendPoint, true,
                        EReason.FRIEND_POINT_RECV);
            }
            if (exlist.size() > 0) {
                // 通知操作玩家对象
                NotifyObj obj = new NotifyObj(FriendConstant.CT_UPDATE, 0, player.getPlayerId(),
                        exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }
            // 推送到订阅中心
            FriendProducerService.getInstance().sendNotify(notifyObjs);
            if (isRecvMax) {
                MessageUtils.throwCondtionError(GameErrorCode.MAX_RECEIVE_COUNT,
                        " this.receiveGiftCount over max.count=" + recvGiftCount);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 拉黑玩家
     * 
     * @param pid
     * @param targetIds
     */
    private void _black_player(Player player, List<Integer> targetIds) {
        // 好友离散配置
        DiscreteDataCfgBean cfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FRIEND_CONFIG);
        // 黑名单最大数量
        int maxBlacklistNum = ToolMap.getInt("maxBlacklistNum", cfg.getData(), 100);
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String blackKey = preBlack + player.getPlayerId();
            long size = jedis.zcount(preBlack, 0, -1);
            if (size >= maxBlacklistNum) {
                MessageUtils.throwCondtionError(GameErrorCode.HAS_REACHED_BLACK_LIMIT,
                        " blacklist is over max.count=" + size);
                return;
            }
            // 删除好友
            _delete_friend(player, targetIds);
            // 删除申请
            _refuse_apply(player, targetIds);

            Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
            List<NotifyFriendEx> exlist = Lists.newArrayList();
            for (Integer _playerId : targetIds) {
                size = jedis.zcount(preBlack, 0, -1);
                if (size >= maxBlacklistNum) {
                    break;
                }
                // 添加屏蔽
                jedis.zadd(blackKey, System.currentTimeMillis(), String.valueOf(_playerId));
                NotifyFriendEx friendEx =
                        new NotifyFriendEx(_playerId, FriendConstant.STATUS_SHIELD, 0, 0, false);
                exlist.add(friendEx);
            }

            if (exlist.size() > 0) {
                // 通知操作玩家对象
                NotifyObj obj = new NotifyObj(FriendConstant.CT_ADD, 0, player.getPlayerId(),
                        exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }
            // 推送到订阅中心
            FriendProducerService.getInstance().sendNotify(notifyObjs);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 解除黑名单玩家
     * 
     * @param pid
     * @param targetIds
     */
    private void _lifted_black_player(Player player, List<Integer> targetIds) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String blackKey = preBlack + player.getPlayerId();
            Set<String> blacks = jedis.zrange(blackKey, 0, -1);
            Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
            List<NotifyFriendEx> exlist = Lists.newArrayList();
            for (Integer _playerId : targetIds) {
                if (!blacks.contains(String.valueOf(_playerId))) {
                    continue;// 不在黑名单内
                }
                // 取消屏蔽
                jedis.zrem(blackKey, String.valueOf(_playerId));
                NotifyFriendEx friendEx =
                        new NotifyFriendEx(_playerId, FriendConstant.STATUS_SHIELD, 0, 0, false);
                exlist.add(friendEx);
            }

            if (exlist.size() > 0) {
                // 通知操作玩家对象
                NotifyObj obj = new NotifyObj(FriendConstant.CT_DELETE, 0, player.getPlayerId(),
                        exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }
            // 推送到订阅中心
            FriendProducerService.getInstance().sendNotify(notifyObjs);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 添加好友推送信息
     * 
     */
    private static void _addNotifyFriend(Map<Integer, List<NotifyObj>> notifyObjs, NotifyObj obj) {
        // 把同一个玩家的封装到一起
        List<NotifyObj> changeList = (List<NotifyObj>) notifyObjs.computeIfAbsent(obj.playerId,
                k -> Lists.newArrayList());
        changeList.add(obj);
    }

    /**
     * 通知好友自身信息变更
     */
    public void notifyFriendsSelfUpdate(int selfPid) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String relIndexKey = preRelationIndex + selfPid;
            Map<String, String> friends = jedis.hgetAll(relIndexKey);
            Map<Integer, List<NotifyObj>> notifyObjs = Maps.newHashMap();
            // 好友
            for (Entry<String, String> entry : friends.entrySet()) {
                Integer _playerId = Integer.parseInt(entry.getKey());
                // 通知好友
                long[] rst = parseFriendData(_playerId, selfPid, jedis);
                long createTime = rst[0];
                long lastSendTime = rst[1];
                boolean canRecv = rst[3] == 1;
                NotifyFriendEx _friendEx = new NotifyFriendEx(selfPid, FriendConstant.STATUS_FRIEND,
                        createTime, lastSendTime, canRecv);
                List<NotifyFriendEx> _exlist = Lists.newArrayList();
                _exlist.add(_friendEx);
                NotifyObj obj =
                        new NotifyObj(FriendConstant.CT_UPDATE, FriendConstant.FRIEND_GET_EVENT,
                                _playerId, _exlist, System.currentTimeMillis());
                _addNotifyFriend(notifyObjs, obj);
            }
            // 推送到订阅中心
            FriendProducerService.getInstance().sendNotify(notifyObjs);
        } catch (Exception e) {
            throw e;
        }
    }

    public void process(String json) {
        try {
            java.lang.reflect.Type type = new TypeToken<List<NotifyObj>>() {}.getType();
            List<NotifyObj> notifyObjs = GsonUtils.fromJson(json, type);
            for (NotifyObj obj : notifyObjs) {
                int playerId = obj.playerId;
                Player player = PlayerManager.getPlayerByPlayerId(playerId);
                if (player != null && player.isOnline()) {
                    List<NotifyFriendEx> targets = obj.targetPlayers;
                    List<FriendInfo.Builder> infos = Lists.newArrayList();
                    for (NotifyFriendEx tar : targets) {
                        PlayerSnap playerSnap = PlayerSnapService.getPlayerShap(obj, tar);

                        if (playerSnap != null) {
                            FriendInfoBean bean = createFriendInfoBean(playerSnap, obj.ct,
                                    tar.status, obj.friendAction, tar.createTime, tar.lastSendTime,
                                    tar.canRecv);
                            FriendInfo.Builder info = FriendMsgBuilder.createFriendInfo(bean);
                            infos.add(info);
                        }
                    }
                    // 通知客户端好友信息
                    notifyFriendInfos(player, infos);
                }
            }
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }
    }

    /**
     * 解析好友相关数据
     * 
     * @param srcPlayerId
     * @param tarFriendId
     * @param jedis
     * @return result [0]创建时间 [1]上次赠送时间 [2]上次领取时间 [3]是否可领取
     */
    private static long[] parseFriendData(int srcPlayerId, int tarFriendId, Jedis jedis) {
        long[] result = new long[4];
        // 判定关系是否还存在
        String relIndexKey = preRelationIndex + srcPlayerId;
        if (jedis.hexists(relIndexKey, String.valueOf(tarFriendId))) {
            boolean canRecv = false;
            // 好友关系key
            String relKey = jedis.hget(relIndexKey, String.valueOf(tarFriendId));
            if (StringUtils.isEmpty(relKey)) {
                return result;
            }
            // 好友关系创建时间
            result[0] = Long.valueOf(jedis.hget(relKey, "create"));
            // srcPlayer数据
            String json = jedis.hget(relKey, String.valueOf(srcPlayerId));
            java.lang.reflect.Type type = new TypeToken<HashMap<String, Long>>() {}.getType();
            Map<String, Long> data = GsonUtils.fromJson(json, type);
            result[1] = data.get("send");
            long recv = data.get("recv");
            result[2] = recv;
            if (!DateEx.isToday(recv)) {
                // 确认对方今日是否赠送过友情点
                String _json = jedis.hget(relKey, String.valueOf(tarFriendId));
                Map<String, Long> _data = GsonUtils.fromJson(_json, type);
                long _send = _data.get("send");
                if (DateEx.isToday(_send)) {
                    canRecv = true;
                }
            }
            result[3] = canRecv ? 1 : 0;
        }
        return result;
    }

    /**
     * 当玩家等级数据过期后，需要重新加载redis数据到内存
     * 
     * @throws Exception
     */
    public synchronized void reloadPlayerLvlWhenExpired() throws Exception {
        int _lvlJedisExpireCheckInterVal =
                GlobalService.getInstance().getRecommandFriendLoadRedisInterval();
        // 刷新时间 如每3分钟执行一次 则此值为每三分钟的时间点
        long _refreshBaseTime =
                TimeUtil.getMinute() / _lvlJedisExpireCheckInterVal * _lvlJedisExpireCheckInterVal;
        // 每个服有各自的刷新时间点 保证每台游戏服不同时加载redis数据
        long _delay = 0;
        if (ServerListManager.gameServerCount > 0) {
            int _curServerId = ServerConfig.getInstance().getServerId();
            int _serverIndex = _curServerId % ServerListManager.gameServerCount;
            _delay = _lvlJedisExpireCheckInterVal * TimeUtil.MINUTE * _serverIndex
                    / ServerListManager.gameServerCount;
        }
        long _now = System.currentTimeMillis();
        if (FriendService.LAST_DEL_EXPIRE_LEVEL_JEDIS_TIME
                + _lvlJedisExpireCheckInterVal * TimeUtil.MINUTE + _delay > _now) {
            return;
        }
        int delta = GlobalService.getInstance().getRecommandFriendLoadCount();
        DiscreteDataCfgBean discreteData =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAX_LVL_CONFIG);
        // 最大等级
        int maxLvl = Integer.parseInt(discreteData.getData().get("pmaxlvl").toString());
        // 删除过期
        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
        playerDaoService.removePlayerLvlRedisExpire(maxLvl);
        FriendService.LAST_DEL_EXPIRE_LEVEL_JEDIS_TIME = _refreshBaseTime * TimeUtil.MINUTE;
        // 按等级加载在线玩家等级数据
        Map<Integer, Set<String>> _lvlPlayerIds = new HashMap<>();
        for (int _level = 1; _level <= maxLvl; _level++) {
            _lvlPlayerIds.put(_level, playerDaoService.selectTopLvlPlayerIds(_level, delta));
        }
        ExternalRedisSerializer externalSerializer = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), ExternalRedisSerializer.class);

        // try (ShardedJedis shardedJedis =
        // ((ShardedRedisServices) RedisServices.getRedisService(ERedisType.VIEW.getType()))
        // .getShardedJedis()) {
        // Collection<Jedis> _allJedis = shardedJedis.getAllShards();
        // for (Entry<Integer, Set<String>> _entry : _lvlPlayerIds.entrySet()) {
        // List<byte[]> values = new ArrayList<>();
        // Map<Integer, FriendView> _playerMap = new ConcurrentHashMap<>();
        // Set<String> _playerIds = _entry.getValue();
        // if (_playerIds.isEmpty()) {
        // FriendService.PLAYER_LEVEL_MAP.put(_entry.getKey(), _playerMap);
        // continue;
        // }
        // // 由于redis存储时使用了hession压缩,这里必须使用byte[]参数类型，返回值同样为byte[]
        // // 若使用String类型，返回值String将压缩部分直接转换成了字符串会造成解析错误
        // byte[][] serchKeys = new byte[_playerIds.size()][];
        // int _index = 0;
        // for (String _id : _playerIds) {
        // serchKeys[_index] =
        // FriendService.KEY_SERIALIZER.serialize(PlayerDaoImp.viewPrefix + _id);
        // _index++;
        // }
        //
        // for (Jedis _jedis : _allJedis) {
        // if (_jedis == null) {
        // continue;
        // }
        // _jedis.mget(serchKeys);
        // List<byte[]> searchs = _jedis.mget(serchKeys);
        // if (searchs == null || searchs.isEmpty()) {
        // continue;
        // }
        // for (byte[] _search : searchs) {
        // if (_search == null || _search.length <= 0) {
        // continue;
        // }
        // values.add(_search);
        // }
        // if (values.size() >= delta) {
        // break;
        // }
        // }
        //
        // LOGGER.info(String.format(
        // "FriendScript.reloadPlayerLvlWhenExpired(): search redis view count=%s",
        // values.size()));
        //
        // // 转化为player对象
        // for (byte[] _data : values) {
        // CacheWrapper _cacheWrapper = (CacheWrapper) externalSerializer
        // .deserialize(_data, PlayerDBBean.class);
        // PlayerDBBean _playerDBBean = (PlayerDBBean) _cacheWrapper.getCacheObject();
        // // 因为player保存了null的数据
        // if (_playerDBBean == null) {
        // continue;
        // }
        // FriendView _player = new FriendView(_playerDBBean, true);
        // _playerMap.put(_player.getPlayerId(), _player);
        // }
        // FriendService.PLAYER_LEVEL_MAP.put(_entry.getKey(), _playerMap);
        // }
        // } catch (Exception e) {
        // throw e;
        // }

        try (Jedis jedis = RedisServices.getRedisService(ERedisType.VIEW.getType()).getJedis()) {
            for (Entry<Integer, Set<String>> _entry : _lvlPlayerIds.entrySet()) {
                List<byte[]> values = new ArrayList<>();
                Map<Integer, FriendView> _playerMap = new ConcurrentHashMap<>();
                Set<String> _playerIds = _entry.getValue();
                if (_playerIds.isEmpty()) {
                    FriendService.PLAYER_LEVEL_MAP.put(_entry.getKey(), _playerMap);
                    continue;
                }
                
                Pipeline pipeline = jedis.pipelined();
                // 由于redis存储时使用了hession压缩,这里必须使用byte[]参数类型，返回值同样为byte[]
                // 若使用String类型，返回值String将压缩部分直接转换成了字符串会造成解析错误
                byte[][] serchKeys = new byte[_playerIds.size()][];
                int _index = 0;
                for (String _id : _playerIds) {
                    serchKeys[_index] =
                            FriendService.KEY_SERIALIZER.serialize(PlayerDaoImp.viewPrefix + _id);
                    pipeline.get(FriendService.KEY_SERIALIZER.serialize(PlayerDaoImp.viewPrefix + _id));
                    _index++;
                }

                // jedis.mget(serchKeys);
                // List<byte[]> searchs = jedis.mget(serchKeys);

                List<Object> searchs = pipeline.syncAndReturnAll();

                if (searchs == null || searchs.isEmpty()) {
                    continue;
                }
                for (Object obj : searchs) {
                    byte[] _search = (byte[]) obj;
                    if (_search == null || _search.length <= 0) {
                        continue;
                    }
                    values.add(_search);
                    if (values.size() >= delta) {
                        break;
                    }
                }
                LOGGER.info(String.format(
                        "FriendScript.reloadPlayerLvlWhenExpired(): search redis view count=%s with level=%s",
                        values.size(), _entry.getKey()));


                // 转化为player对象
                for (byte[] _data : values) {
                    CacheWrapper _cacheWrapper = (CacheWrapper) externalSerializer
                            .deserialize(_data, PlayerDBBean.class);
                    PlayerDBBean _playerDBBean = (PlayerDBBean) _cacheWrapper.getCacheObject();
                    // 因为player保存了null的数据
                    if (_playerDBBean == null) {
                        continue;
                    }
                    FriendView _player = new FriendView(_playerDBBean, true);
                    _playerMap.put(_player.getPlayerId(), _player);
                }
                FriendService.PLAYER_LEVEL_MAP.put(_entry.getKey(), _playerMap);
            }
        } catch (

        Exception e) {
            throw e;
        }
    }


    @Override
    public boolean isFriend(int playerId, int pid) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String relIndexKey = preRelationIndex + playerId;
            // 判断是否是好友
            return jedis.hexists(relIndexKey, String.valueOf(pid));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void getFriendsInfo(Player player, int rmsgId) {
        int playerId = player.getPlayerId();
        // 获取好友信息
        List<FriendInfoBean> infos = Lists.newArrayList();
        Set<Tuple> applys;
        Set<String> blacks;
        Map<String, Long> applyMap = new HashMap<String, Long>();
        long t0 = System.currentTimeMillis();
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String applyKey = preApply + playerId;
            applys = jedis.zrangeWithScores(applyKey, 0, -1);
            String relIndexKey = preRelationIndex + playerId;
            String blackKey = preBlack + playerId;
            blacks = jedis.zrange(blackKey, 0, -1);
            for (Tuple _tp : applys) {
                applyMap.put(_tp.getElement(), Math.round(_tp.getScore()));
            }
            // 好友
            Set<String> indexKeys = jedis.hkeys(relIndexKey);
            for (String key : indexKeys) {
                Integer _playerId = Integer.parseInt(key);
                // Player _player = PlayerViewService.getPlayerView(_playerId);
                PlayerSnap playerSnap = PlayerSnapService.getPlayerShap(_playerId);
                if (playerSnap == null) {
                    LOGGER.warn("没有找到对应的好友信息:" + _playerId);
                    continue;
                }
                long[] rst = parseFriendData(playerId, _playerId, jedis);
                long createTime = rst[0];
                long lastSendTime = rst[1];
                boolean canRecv = rst[3] == 1;
                infos.add(createFriendInfoBean(playerSnap, FriendConstant.CT_GET,
                        FriendConstant.STATUS_FRIEND, FriendConstant.FRIEND_GET_EVENT, createTime,
                        lastSendTime, canRecv));
            }
        }
        long doTime = System.currentTimeMillis() - t0;
        if (doTime > 100) {
            LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " get friend info:" + doTime);
        }
        long t1 = System.currentTimeMillis();
        // 申请列表
        for (Entry<String, Long> entry : applyMap.entrySet()) {
            String _playerId = entry.getKey();
            long createTime = entry.getValue();
            // Player _player = PlayerViewService.getPlayerView(Integer.valueOf(_playerId));
            PlayerSnap _player = PlayerSnapService.getPlayerShap(Integer.valueOf(_playerId));
            if (_player == null) {
                LOGGER.warn("没有找到对应的好友信息：" + _playerId);
                continue;
            }
            infos.add(createFriendInfoBean(_player, FriendConstant.CT_GET,
                    FriendConstant.STATUS_APPLY, FriendConstant.FRIEND_GET_EVENT, createTime, 0,
                    false));
        }
        long t2 = System.currentTimeMillis();
        doTime = (t2 - t1);
        if (doTime > 200) {
            LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " get friend apply:" + doTime);
        }
        // 屏蔽列表
        for (String _playerId : blacks) {
            // Player _player = PlayerViewService.getPlayerView(Integer.valueOf(_playerId));
            PlayerSnap _player = PlayerSnapService.getPlayerShap(Integer.valueOf(_playerId));
            if (_player == null) {
                LOGGER.warn("没有找到对应的好友屏蔽信息：" + _playerId);
                continue;
            }
            long createTime = System.currentTimeMillis();
            infos.add(createFriendInfoBean(_player, FriendConstant.CT_GET,
                    FriendConstant.STATUS_SHIELD, FriendConstant.FRIEND_GET_EVENT, createTime, 0,
                    false));
        }
        doTime = System.currentTimeMillis() - t2;
        if (doTime > 100) {
            LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " get friend black:" + doTime);
        }
        PlayerProcessor pro =
                PlayerProcessorManager.getInstance().getProcessor(player.getLineIndex());
        pro.executeInnerHandler(new LGetFriendsInfoFinishHandler(playerId, rmsgId, infos));
    }

    @Override
    public void clearExpireFriendApply(int playerId) {
        DiscreteDataCfgBean cfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FRIEND_CONFIG);
        long applyTimeout =
                ToolMap.getInt("applyTimeout", cfg.getData(), 7) * DateUtils.MILLIS_PER_DAY;
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.FRIEND.getType()).getJedis()) {
            String applyKey = preApply + playerId;
            Set<Tuple> applys = jedis.zrangeWithScores(applyKey, 0, -1);
            for (Tuple _tp : applys) {
                // 检查过期申请
                long createTime = Math.round(_tp.getScore());
                if (DateEx.isTimeout(createTime, applyTimeout)) {
                    // 移除过期的申请
                    jedis.zrem(applyKey, _tp.getElement());
                    jedis.zrem(preSendApply + _tp.getElement(), String.valueOf(playerId));
                    continue;
                }
            }
        }
    }

}
