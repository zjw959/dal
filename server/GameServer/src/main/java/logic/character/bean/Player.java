package logic.character.bean;

import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import logic.TriggerEvent.TriggerEventManager;
import logic.activity.ActivityManager;
import logic.bag.BagManager;
import logic.basecore.IAcrossDay;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.ILoginInit;
import logic.basecore.IOffline;
import logic.basecore.IRoleBeanConverter;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITick;
import logic.basecore.IView;
import logic.basecore.PlayerBaseFunctionManager;
import logic.buyResources.BuyResMgr;
import logic.character.PlayerManager;
import logic.character.PlayerViewService;
import logic.chasm.TeamDungeonManager;
import logic.city.CityInfoManager;
import logic.city.CityRoleManager;
import logic.city.CuisineManager;
import logic.city.ManualManager;
import logic.city.NewBuildingManager;
import logic.city.PartTimeManager;
import logic.city.PrizeClawGameManager;
import logic.city.build.BuildingConstant;
import logic.comment.CommentManager;
import logic.constant.ConstDefine;
import logic.constant.DiscreteDataID;
import logic.constant.EEventType;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.dating.DatingManager;
import logic.dungeon.DungeonManager;
import logic.elementCollection.ElementCollectionManager;
import logic.endless.EndlessCloisterManager;
import logic.favor.FavorDatingManager;
import logic.friend.FriendManager;
import logic.hero.HeroManager;
import logic.hero.bean.Hero;
import logic.info.InfoManager;
import logic.login.struct.ChannelInfo;
import logic.mail.MailMgr;
import logic.medal.MedalManager;
import logic.novelDating.NovelDatingManager;
import logic.pay.PayManager;
import logic.playerGuide.PlayerGuideManager;
import logic.role.RoleManager;
import logic.sign.ApSupplyManager;
import logic.sign.MonthSignManager;
import logic.sign.SevenDaySignManager;
import logic.sign.TomorrowSignManager;
import logic.store.StoreManager;
import logic.summon.SummonManager;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import logic.task.TaskManager;
import message.SMessageFactory;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;

import security.MD5;
import server.ServerConfig;
import thread.base.GameInnerHandler;
import thread.db.DBUpdateRoleHandler;
import thread.player.PlayerDBProcessorManager;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;
import utils.ChannelUtils;
import utils.ClassScanUtils;
import utils.ExceptionEx;
import utils.GsonUtils;
import utils.StrEx;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.LevelUpCfgBean;
import db.game.bean.PlayerDBBean;
import event.IEventListener;
import event.SimpleEventDispatcher;

/**
 * 角色实体类
 */
public class Player {

    private static final Logger LOGGER = Logger.getLogger(Player.class);

    /** player' network connect session */
    private ChannelHandlerContext ctx;
    /** logic processor index */
    private int lineIndex;

    /* ————————————————user attribute—————————————————— */
    /** player user name (account) */
    private String userName;
    /** is forbid */
    private int isforbid;
    private String IP;
    /** 渠道id */
    private String channelId;
    /** 渠道appid */
    private String channelAppId;

    /* ————————————————user attribute—————————————————— */


    /* ————————————————role attribute—————————————————— */
    /** role id */
    private int playerId;
    /** role name */
    private String playerName;
    /** role level */
    private int level;
    /** role vip level */
    private int vip;
    /** role experience */
    private long exp;

    /** gm level( > 1 ) */
    private int gm;
    /** 金币 */
    private long gold;
    /** 系统钻石 */
    private long systemDiamond;
    /** 充值钻石 */
    private long rechargeDiamond;
    // /** 战斗力 */
    // private long fightpower;

    /** current login time (login success recorded) */
    private long loginTime;
    /** last offline time */
    private long offlineTime;
    /** total online time */
    private long onlineTime;
    /** last login time */
    private long lastLoginTime;

    /** role createTime */
    private long createTime;
    /** 当前登录服务器 */
    private int currentServer;

    private long updateLevelTime;

    /* ——————————————————role attribute————————————————— */


    /* —————————————————————other————————————————————— */
    /** 上次回存时间 */
    private long lastSavebackTime;
    /** 断线重连使用的token */
    private Token token;
    /** 数据MD5标示 */
    private String dirtyKey;
    /**
     * 计算dirtyKey的时间
     */
    private long dirtyCalculateTime;
    /** 玩家在线状态 */
    private volatile PlayerState state;

    /** after client initialize Over ,request those Managers to sending data */
    private final List<ILoginInit> dataLazyPullManagers = new ArrayList<>();
    /** after create a new role ,those managers need to initialize */
    private final List<ICreateRoleInitialize> createRoleInitManagers = new ArrayList<>();
    private final List<ICreatePlayerInitialize> createPlayerInitManagers = new ArrayList<>();
    private final List<IEventListener> eventListernerManagers = new ArrayList<>();
    private final List<ITick> tickManagers = new ArrayList<>();
    private final List<IView> viewManagers = new ArrayList<>();
    private final List<IOffline> offlineManagers = new ArrayList<>();
    /** allManager */
    private static final Map<String, Class<?>> funClassManagers = new HashMap<>();
    private final Map<String, PlayerBaseFunctionManager> funInstanceManagers = new HashMap<>();
    /** even dispatcher */
    private final SimpleEventDispatcher eventDispatcher = new SimpleEventDispatcher();

    /** 检查清理关闭连接时间 */
    public long isClosingTime;


    public Iterator<ITick> getTickManagers() {
        return tickManagers.iterator();
    }

    public static void initFunManager() {
        Collection<Class<?>> classes = ClassScanUtils.scanPackages("logic");
        for (Class<?> _class : classes) {
            if (PlayerBaseFunctionManager.class.isAssignableFrom(_class)
                    && PlayerBaseFunctionManager.class.hashCode() != _class.hashCode()) {
                funClassManagers.put(_class.getSimpleName(), _class);
            }
        }
    }

    public static void loadFromDB(PlayerDBBean roleBean)
            throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Set<Entry<String, Class<?>>> entries = funClassManagers.entrySet();
        for (Entry<String, Class<?>> entry : entries) {
            Class<?> _class = entry.getValue();

            if (IRoleBeanConverter.class.isAssignableFrom(_class)) {
                // IRoleBeanConverter.loadFromDB是接口静态方法,只存在接口的方法表,不能子类调用和获取
                Method method = _class.getMethod("loadFromDB", roleBean.getClass());
                method.invoke(null, roleBean);
            }
        }
    }

    /**
     * 独立表数据回存 暂无具体使用
     * 
     * @param roleBean
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Deprecated
    public static void saveToDB(PlayerDBBean roleBean)
            throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Set<Entry<String, Class<?>>> entries = funClassManagers.entrySet();
        for (Entry<String, Class<?>> entry : entries) {
            Class<?> _class = entry.getValue();

            if (IRoleBeanConverter.class.isAssignableFrom(_class)) {
                Method method = _class.getMethod("saveToDB", roleBean.getClass());
                method.invoke(null, roleBean);
            }
        }
    }

    public static List<String> getIViewImpls() {
        List<String> ms = Lists.newArrayList();
        for (Class<?> _class : funClassManagers.values()) {
            if (IView.class.isAssignableFrom(_class)) {
                ms.add(_class.getSimpleName());
            }
        }
        return ms;
    }

    public static List<Class<?>> getIViewClass() {
        List<Class<?>> ms = Lists.newArrayList();
        for (Class<?> _class : funClassManagers.values()) {
            if (IView.class.isAssignableFrom(_class)) {
                ms.add(_class);
            }
        }
        return ms;
    }

    protected Player() {}

    public Player(ChannelInfo cInfo) throws Exception {
        _initPlayer(null, cInfo);
    }

    /**
     * 
     * @param roleBean
     * @param isView 是否是通过redis的playerview在进行初始化
     * @throws Exception
     */
    public Player(PlayerDBBean roleBean, boolean isView) throws Exception {
        _initPlayer(roleBean, null);
        if (isView) {
            this.state = roleBean.getIsOnline() ? PlayerState.ONLINE : PlayerState.OFFLINE;
        }
    }

    private void _initPlayer(PlayerDBBean roleBean, ChannelInfo cInfo) throws Exception {
        if ((roleBean != null && cInfo != null) || (roleBean == null && cInfo == null)) {
            throw new Exception("init player params error" + ExceptionEx.currentThreadTraces());
        }


        _initializePlayerInfo(roleBean, cInfo);

        if ((roleBean != null) && StrEx.isEmpty(roleBean.getData())) {
            LOGGER.warn("roleBean data is empty." + roleBean.toString());
            roleBean = null;
        }

        JsonObject jsonObjet = new JsonObject();

        if ((roleBean != null) && !roleBean.getData().isEmpty()) {
            // byte[] data = Compress.decodeBase64(roleBean.getData().getBytes());
            // roleJsonData = new String(Compress.uncompressWithGZip(data));
            // jsonObjet = GsonUtils.toJsonObject(roleJsonData);
            String json = roleBean.getData();
            jsonObjet = GsonUtils.toJsonObject(json);
        }

        Set<Entry<String, Class<?>>> entries = funClassManagers.entrySet();
        for (Entry<String, Class<?>> entry : entries) {
            Class<?> _class = entry.getValue();

            String jsonKey = _class.getSimpleName();
            PlayerBaseFunctionManager baseFunMan = null;
            if (IRoleJsonConverter.class.isAssignableFrom(_class)) {
                JsonElement element = jsonObjet.get(jsonKey);
                // 为空则添加默认JSON构造函数
                String _jsonData = "{}";
                if (element != null) {
                    _jsonData = element.toString();
                }
                baseFunMan = (PlayerBaseFunctionManager) GsonUtils.fromJson(_jsonData, _class);
            } else {
                Constructor<?> constructor = _class.getDeclaredConstructor();
                baseFunMan = (PlayerBaseFunctionManager) constructor.newInstance();
            }

            if ((roleBean != null) && IRoleBeanConverter.class.isAssignableFrom(_class)) {
                ((IRoleBeanConverter) baseFunMan).fromData(roleBean);
            }

            // 通过反射注册player
            Field fieldPlayer = PlayerBaseFunctionManager.class.getDeclaredField("player");
            fieldPlayer.setAccessible(true);
            fieldPlayer.set(baseFunMan, this);
            fieldPlayer.setAccessible(false);

            this.registerManager(baseFunMan);
        }
        // 事件提前注册,避免事件没被注册,有事件触发产生
        for (IEventListener iEventListener : eventListernerManagers) {
            // 注册事件
            iEventListener.registerPerformed(this);
        }

        // 新注册账号的逻辑
        if (roleBean == null) {
            // init function system for create initialize
            for (ICreateRoleInitialize manager : createRoleInitManagers) {
                manager.createRoleInitialize();
                // ((PlayerBaseFunctionManager) manager).setAcrossDay(new Date().getTime());
            }
        }

        // 初始化player
        for (ICreatePlayerInitialize iCreatePlayerInitialize : createPlayerInitManagers) {
            iCreatePlayerInitialize.createPlayerInitialize();
        }

    }

    @SuppressWarnings("rawtypes")
    private void _initializePlayerInfo(PlayerDBBean playerBean, ChannelInfo cInfo)
            throws Exception {
        if (cInfo != null) {
            DiscreteDataCfgBean discreteDataCfgBean =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.INIT_PLAYER);
            Map dataMap = discreteDataCfgBean.getData();
            this.playerId = cInfo.getPlayerId();
            this.level = (int) dataMap.get("playerLevel");
            this.exp = (int) dataMap.get("playerExp");
            this.vip = (int) dataMap.get("playerVipLevel");

            this.userName = cInfo.getFullUserName();
            this.channelId = cInfo.getChannelId();
            this.channelAppId = cInfo.getChannelAppId();
            this.isforbid = 0;
            this.currentServer = ServerConfig.getInstance().getServerId();
            this.playerName = String.valueOf(this.playerId);
            this.gm = 1;
            this.gold = 0;
            this.systemDiamond = 0;
            this.rechargeDiamond = 0;
            long cur = System.currentTimeMillis();
            this.createTime = cur;
            this.loginTime = cur;
            this.onlineTime = 0;
            this.offlineTime = 0;
            this.lastLoginTime = cur;
        } else {
            this.userName = playerBean.getUsername();
            this.channelId = playerBean.getChannelId();
            this.channelAppId = playerBean.getChannelAppId();
            this.isforbid = playerBean.getIsForbid();
            this.currentServer = playerBean.getCurrentServer();

            this.playerId = playerBean.getPlayerId();
            this.playerName = playerBean.getPlayername();
            this.level = playerBean.getLevel();
            this.vip = playerBean.getViplevel();
            this.exp = playerBean.getExp();
            this.gm = playerBean.getGmlevel();

            this.gold = playerBean.getGold();
            this.systemDiamond = playerBean.getSystemDiamond();
            this.rechargeDiamond = playerBean.getRechargeDiamond();
            // this.fightpower = playerBean.getFightpower();

            this.createTime = playerBean.getCreatetime();
            this.loginTime = playerBean.getLogintime();
            this.onlineTime = playerBean.getOnlinetime();
            this.offlineTime = playerBean.getOfflinetime();
            this.lastLoginTime = playerBean.getLastlogintime();
        }
        PlayerProcessor processor =
                PlayerProcessorManager.getInstance().getProcessorByUserName(this.userName);
        if (processor == null) {
            throw new Exception("init player. player choose logicProcess failed" + this.userName
                    + " " + this.playerName + " " + this.playerId + ChannelUtils.logInfo(ctx));
        }

        this.lineIndex = processor.getLineIndex();
        // 避免insert 和 selectfromdb 就又要马上又会回存一次
        this.lastSavebackTime = System.currentTimeMillis();
    }

    /**
     * Get SMessageFactory{@link SMessageFactory} To Fetch SMessage Object From SMessageObjectPools
     * 
     * @return SMessageFactory
     */
    public SMessageFactory getSMsgFatory() {
        PlayerProcessor processor =
                PlayerProcessorManager.getInstance().getProcessor(this.getLineIndex());
        if (processor != null) {
            return processor.getSMsgFactory();
        }
        return null;
    }

    /**
     * register manager for save data, lazy data send ,and create role initialize
     * 
     * @param manager
     */
    public void registerManager(PlayerBaseFunctionManager manager) {
        if (manager instanceof ILoginInit) {
            dataLazyPullManagers.add((ILoginInit) manager);
        }
        if (manager instanceof ICreateRoleInitialize) {
            createRoleInitManagers.add((ICreateRoleInitialize) manager);
        }
        if (manager instanceof ITick) {
            tickManagers.add((ITick) manager);
        }
        if (manager instanceof IOffline) {
            offlineManagers.add((IOffline) manager);
        }
        if (manager instanceof ICreatePlayerInitialize) {
            createPlayerInitManagers.add((ICreatePlayerInitialize) manager);
        }
        // 注册事件
        if (manager instanceof IEventListener) {
            eventListernerManagers.add((IEventListener) manager);
        }
        if (manager instanceof IView) {
            viewManagers.add((IView) manager);
        }
        funInstanceManagers.put(manager.getKey(), manager);
    }

    /***
     * Get player function manager
     * 
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends PlayerBaseFunctionManager> T getFunManager(String className) {
        return (T) this.funInstanceManagers.get(className);
    }

    /**
     * 登陆后服务器抛送的
     * 
     * 注意,由于客户端不支持推送.因此修改为此种方式.此处不能推送变化,等待客户端登陆后主动获取服务拉取.
     * 
     * after client initialize over
     */
    public void loginInit() {
        long now = System.currentTimeMillis();
        for (ILoginInit manager : this.dataLazyPullManagers) {
            try {
                // 玩家登陆首先判断跨天的数据内容
                if (manager instanceof IAcrossDay) {
                    PlayerBaseFunctionManager baseFunctionManager =
                            (PlayerBaseFunctionManager) manager;
                    // 不推送跨天内容
                    ((IAcrossDay) manager).tickAcrossDay(baseFunctionManager, now, false);
                }
                manager.loginInit();
            } catch (Exception e) {
                ExceptionEx.e2s(e);
            }
        }
        try {
            LogicScriptsUtils.getPlayerScript().initFixBug(this);
        } catch (Exception e) {
            ExceptionEx.e2s(e);
        }
    }

    /**
     * 只能在玩家自己的PlayerProcess线程调用
     * 
     * 不能在其他线程调用
     * 
     * @return
     */
    public PlayerDBBean toPlayerBean() {
        PlayerDBBean bean = new PlayerDBBean();
        bean.setPlayerId(this.playerId);
        bean.setPlayername(this.playerName);
        bean.setUserName(this.userName);
        bean.setChannelId(this.channelId);
        bean.setChannelAppId(this.channelAppId);
        bean.setLevel(this.level);
        bean.setViplevel(this.vip);
        bean.setExp(this.exp);
        bean.setGmlevel(this.gm);
        bean.setLogintime(this.loginTime);
        bean.setOfflinetime(this.offlineTime);
        bean.setOnlinetime(this.onlineTime);
        bean.setLastlogintime(this.lastLoginTime);
        bean.setGold(this.gold);
        bean.setSystemDiamond(this.systemDiamond);
        bean.setRechargeDiamond(this.rechargeDiamond);
        // bean.setFightpower(this.fightpower);
        bean.setCreateTime(this.createTime);

        bean.setIp(this.IP);
        bean.setIsForbid(this.isforbid);

        bean.setCurrentServer(this.currentServer);

        bean.setHeroId(this.getHeroManager().getHelpFightHeroCid());
        bean.setSkinCid(this.getSkinCid());
        bean.setFightpower(this.getHeroManager().getHelpHeroFightPower());
        bean.setDescribe(this.getInfoManager().getRemark());
        bean.setIsOnline(this.isOnline());

        // 数据存储
        JsonObject jsonObject = new JsonObject();

        Set<Entry<String, PlayerBaseFunctionManager>> entries = funInstanceManagers.entrySet();
        for (Entry<String, PlayerBaseFunctionManager> entry : entries) {
            PlayerBaseFunctionManager _manager = entry.getValue();
            if (_manager instanceof IRoleJsonConverter) {

                // String _json = GsonUtils.toJson(_manager);
                // 字符串 双引号转移 使用TREE
                // System.out.println(_json);
                // jsonObject.addProperty(_manager.getKey(), _json);

                JsonElement _json = GsonUtils.toJsonTree(_manager);
                jsonObject.add(_manager.getKey(), _json);
            }

            if (_manager instanceof IRoleBeanConverter) {
                ((IRoleBeanConverter) _manager).toData(bean);
            }
        }

        String allStr = jsonObject.toString();
        // byte[] data = Compress.compressWithGZip(allStr.getBytes());
        // bean.setData(Compress.encodeBase64(data));

        bean.setData(allStr);

        return bean;
    }

    /**
     * 增加角色经验
     * 
     * @param addExp
     * @return 实际获得经验
     * 
     */
    public long addExp(long addExp, boolean isNotify) {
        DiscreteDataCfgBean discreteData =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAX_LVL_CONFIG);
        // 最大等级
        int maxLvl = Integer.parseInt(discreteData.getData().get("pmaxlvl").toString());
        // 实际获得经验
        long actualAddExp = 0;
        long totalExp = this.exp + addExp;
        // 升级恢复体力
        int addStrength = 0;
        int addEnergy = 0;
        int oldLev = this.level;
        while (this.level < maxLvl) {
            LevelUpCfgBean bean = GameDataManager.getLevelUpCfgBean(this.level);
            long nextLevelNeed = bean.getPlayerExp();
            if (totalExp < nextLevelNeed) {
                actualAddExp += totalExp;
                this.exp = totalExp;
                break;
            }
            this.level++;
            addStrength = bean.getRecovery();
            addEnergy = bean.getRecoveryVigour();
            actualAddExp += nextLevelNeed;
            totalExp -= nextLevelNeed;
            if (this.level == maxLvl) {
                this.exp = 0;// 满级后经验置为0
            }
        }
        // 等级变化
        if (this.level != oldLev) {
            // 向redis推送View
            ((PlayerViewService) PlayerViewService.getInstance())
                    .updatePlayerView(this.toPlayerBean(), true, oldLev);
            // 触发任务激活事件

            _fireEvent(null, EEventType.OPEN_ACTIVITY_EVENT.value());

            Map<String, Object> in = Maps.newHashMap();
            in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.PLAYER_LVL);
            in.put(EventConditionKey.OLD_LEVEL, oldLev);
            in.put(EventConditionKey.NOW_LEVEL, this.level);
            _fireEvent(in, EEventType.PLAYER_CHANGE.value());
            Map<String, Object> info = Maps.newHashMap();
            info.put(BuildingConstant.EVENT_CONDITION_ID, BuildingConstant.LEVEL_UP);
            info.put(BuildingConstant.EVENT_RESULT_DATA, this.level);
            _fireEvent(info, EEventType.CHECK_UNLOCK_BUILDING.value());

            boolean b = changeStrength(addStrength, true);
            if (b && isNotify) {
                getInfoManager().sendStrengthUpdate();
            }

            boolean bool = changeEnergy(addEnergy, true);
            if (bool && isNotify) {
                getCityInfoManager().sendCityEnergyUpdate();
            }
            // 刷新商店
            this.getStortManager().randomCommodity(true);
            // PlayerDaoService playerDaoService =
            // SpringContextUtils.getBean(ESpringConextType.PlAYER.getType(),
            // PlayerDaoService.class);
        }
        // 是否通知客户端
        if (isNotify && actualAddExp > 0) {
            PlayerInfo.Builder playerBuilder = PlayerInfo.newBuilder();
            playerBuilder.setLvl(getLevel());
            playerBuilder.setExp(getExp());
            MessageUtils.send(this, playerBuilder);
        }
        return actualAddExp;
    }

    /**
     * 改变角色体力
     * 
     * @param num
     * @param isForce 是否可以强制超过当前等级的体力上限
     * @param isNotify
     * @return
     */
    public boolean changeStrength(int num, boolean isForce) {
        return getInfoManager().changeStrength(num, isForce);
    }

    /**
     * 改变角色精力
     * 
     * @param num
     * @param isForce 是否可以强制超过当前等级的精力上限
     * @param isNotify
     * @return
     */
    public boolean changeEnergy(int num, boolean isForce) {
        return getCityInfoManager().changeCityEnergy(num, isForce);
    }


    /** 专注变化 **/
    public void changeAbsorbed(int change) {
        getInfoManager().changeAbsorbed(change);
    }

    /** 魅力变化 **/
    public void changeGlamour(int change) {
        getInfoManager().changeGlamour(change);
    }

    /** 温柔变化 **/
    public void changeTender(int change) {
        getInfoManager().changeTender(change);
    }

    /** 知识变化 **/
    public void changeKnowledge(int change) {
        getInfoManager().changeKnowledge(change);
    }

    /** 运气变化 **/
    public void changefortune(int change) {
        getInfoManager().changefortune(change);
    }

    public void tick(long currentTime) {
        try {
            LogicScriptsUtils.getPlayerScript().tick(this, currentTime);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e) + this.logInfo());
        }
        try {
            LogicScriptsUtils.getPlayerScript().clearClosedSession(this);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e) + this.logInfo());
        }
        try {
            LogicScriptsUtils.getPlayerScript().fixTickBug(this, currentTime);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e) + this.logInfo());
        }
    }

    /**
     * 玩家离线 离线hanlder调用
     */
    public void logout() {
        LogicScriptsUtils.getLoginHttpScript().logout(this);
    }

    public void managerOffline() {
        for (IOffline manager : this.offlineManagers) {
            long off = System.currentTimeMillis();
            manager.offline();
            long offtime = (System.currentTimeMillis() - off);
            if (offtime > 50) {
                LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " logout."
                        + manager.getClass().getSimpleName() + " _managerOffline:" + offtime);
            }
        }
    }

    /**
     * 回存Player 数据到数据库
     */
    public void save() {
        save(false);
    }

    /**
     * 回存Player 数据到数据库
     * 
     * @param isLogout 是否是离线写入. 并非代表玩家当时的离线状态
     */
    public PlayerDBBean save(boolean isLogout) {
        PlayerDBBean dbBean = toPlayerBean();
        if (isLogout) {
            PlayerDBProcessorManager.getInstance().addPlayerHandler(this,
                    new DBUpdateRoleHandler(dbBean,
                            PlayerProcessorManager.getInstance().getProcessorByUserName(userName),
                            new LDBDoneOfflineCBHandler()));
        } else {
            PlayerDBProcessorManager.getInstance().addPlayerHandler(this,
                    new DBUpdateRoleHandler(dbBean));
        }
        return dbBean;
    }

    public void registerEventListener(int eventId, IEventListener listener) {
        eventDispatcher.registerEventListener(eventId, listener);
    }

    public void _fireEvent(Object params, int eventId) {
        eventDispatcher.fireEvent(params, eventId);
    }


    public enum PlayerState {
        LOGIN, ONLINE, OFFLINE;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    /**
     * 玩家是否在线
     * 
     * @return
     */
    public boolean isOnline() {
        return state == PlayerState.ONLINE ? true : false;
    }

    /**
     * 玩家基本信息
     * 
     * @return
     */
    public String logInfo() {
        return "{\"playerInfo\": playerId:" + playerId + ",playerName:" + playerName + ",accountId:"
                + this.userName + ",state:" + this.state + ",lineId:" + lineIndex
                + ChannelUtils.logInfo(ctx) + "}";
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public String getUserName() {
        return userName;
    }

    // public void setAccountId(String accountId) {
    // this.userName = accountId;
    // }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getGmLevel() {
        return gm;
    }

    public void setGmLevel(int gmLevel) {
        this.gm = gmLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getVipLevel() {
        return vip;
    }

    public void setVipLevel(int vipLevel) {
        this.vip = vipLevel;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getGold() {
        return gold;
    }

    /**
     * 禁止调用该结构进行元宝数量修改 通过背包进行变更
     * 
     * @param gold
     */
    public void setGold(long gold) {
        this.gold = gold;
    }

    public long getSystemDiamond() {
        return systemDiamond;
    }

    public void setSystemDiamond(long systemDiamond) {
        this.systemDiamond = systemDiamond;
    }

    public long getRechargeDiamond() {
        return rechargeDiamond;
    }

    public void setRechargeDiamond(long rechargeDiamond) {
        this.rechargeDiamond = rechargeDiamond;
    }

    public long getToltalDiamond() {
        return systemDiamond + rechargeDiamond;
    }

    public int getStrength() {
        return getInfoManager().getStrength();
    }

    public int getAbsorbed() {
        return getInfoManager().getAbsorbed();
    }

    public int getGlamour() {
        return getInfoManager().getGlamour();
    }

    public int getTender() {
        return getInfoManager().getTender();
    }

    public int getKnowledge() {
        return getInfoManager().getKnowledge();
    }

    public int getFortune() {
        return getInfoManager().getFortune();
    }
    // public long getFightpower() {
    // return fightpower;
    // }
    //
    // public void setFightpower(long fightpower) {
    // this.fightpower = fightpower;
    // }

    public long getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(long offlineTime) {
        this.offlineTime = offlineTime;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public int getIsforbid() {
        return this.isforbid;
    }

    public void setIsforbid(short isforbid) {
        this.isforbid = isforbid;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelAppId() {
        return channelAppId;
    }

    public int getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(int currentServer) {
        this.currentServer = currentServer;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public long getLastSavebackTime() {
        return lastSavebackTime;
    }

    public void setLastSavebackTime(long lastSavebackTime) {
        this.lastSavebackTime = lastSavebackTime;
    }

    public String getDirtyKey() {
        return dirtyKey;
    }

    public void setDirtyKey(PlayerDBBean roleBean) {
        String str = roleBean.toString();
        int idx = str.indexOf("[");
        if (idx >= 0 && idx < str.length()) {
            str = str.substring(idx);
            dirtyKey = MD5.MD5Encode(str);
        }
    }

    /**
     * 计算dirty的时间
     * 
     * @param now
     */
    public void setDirtyCalculateTime(long now) {
        this.dirtyCalculateTime = now;
    }

    /**
     * 计算dirty的时间
     * 
     * @param now
     */
    public long getDirtyCalculateTime() {
        return this.dirtyCalculateTime;
    }


    public void setDirtyKey(String key) {
        this.dirtyKey = key;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public long getUpdateLevelTime() {
        return updateLevelTime;
    }

    public void setUpdateLevelTime(long updateLevelTime) {
        this.updateLevelTime = updateLevelTime;
    }


    public SimpleEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public BagManager getBagManager() {
        return (BagManager) this.getFunManager(BagManager.class.getSimpleName());
    }

    public SummonManager getSummonManager() {
        return this.getFunManager(SummonManager.class.getSimpleName());
    }

    public InfoManager getInfoManager() {
        return this.getFunManager(InfoManager.class.getSimpleName());
    }

    public FriendManager getFriendManager() {
        return this.getFunManager(FriendManager.class.getSimpleName());
    }

    public DungeonManager getDungeonManager() {
        return (DungeonManager) this.getFunManager(DungeonManager.class.getSimpleName());
    }

    public MonthSignManager getMonthSignManager() {
        return (MonthSignManager) this.getFunManager(MonthSignManager.class.getSimpleName());
    }

    public DatingManager getDatingManager() {
        return (DatingManager) this.getFunManager(DatingManager.class.getSimpleName());
    }


    public TomorrowSignManager getTomorrowSignManager() {
        return (TomorrowSignManager) this.getFunManager(TomorrowSignManager.class.getSimpleName());
    }

    public ApSupplyManager getApSupplyManager() {
        return (ApSupplyManager) this.getFunManager(ApSupplyManager.class.getSimpleName());
    }

    public RoleManager getRoleManager() {
        return (RoleManager) this.getFunManager(RoleManager.class.getSimpleName());
    }

    public HeroManager getHeroManager() {
        return this.getFunManager(HeroManager.class.getSimpleName());
    }

    public PrizeClawGameManager getPrizeClawGameManager() {
        return this.getFunManager(PrizeClawGameManager.class.getSimpleName());
    }

    public CuisineManager getCuisineManager() {
        return this.getFunManager(CuisineManager.class.getSimpleName());
    }

    public NewBuildingManager getNewBuildingManager() {
        return this.getFunManager(NewBuildingManager.class.getSimpleName());
    }

    public PartTimeManager getPartTimeManager() {
        return this.getFunManager(PartTimeManager.class.getSimpleName());
    }

    public CityRoleManager getCityRoleManager() {
        return this.getFunManager(CityRoleManager.class.getSimpleName());
    }

    public ManualManager getManualManager() {
        return this.getFunManager(ManualManager.class.getSimpleName());
    }

    public CommentManager getCommentManager() {
        return this.getFunManager(CommentManager.class.getSimpleName());
    }

    public int getSkinCid() {
        int skinCid = 0;
        Hero hero = this.getHeroManager().getHero(this.getHeroManager().getHelpFightHeroCid());
        if (hero != null) {
            skinCid = hero.getSkin().getSkinItem().getTemplateId();
        } else {
            LOGGER.error("hero not found! heroCid=" + this.getHeroManager().getHelpFightHeroCid());
        }
        return skinCid;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public ElementCollectionManager getElementCollectionManager() {
        return this.getFunManager(ElementCollectionManager.class.getSimpleName());
    }

    public TriggerEventManager getTriggerEventManager() {
        return this.getFunManager(TriggerEventManager.class.getSimpleName());
    }

    public MailMgr getMailManager() {
        return this.getFunManager(MailMgr.class.getSimpleName());
    }

    public PayManager getPayManager() {
        return this.getFunManager(PayManager.class.getSimpleName());
    }

    public CityInfoManager getCityInfoManager() {
        return this.getFunManager(CityInfoManager.class.getSimpleName());
    }

    public SevenDaySignManager getSevenDaySignManager() {
        return this.getFunManager(SevenDaySignManager.class.getSimpleName());
    }

    public FavorDatingManager getFavorDatingManager() {
        return this.getFunManager(FavorDatingManager.class.getSimpleName());
    }

    public NovelDatingManager getNovelDatingManager() {
        return this.getFunManager(NovelDatingManager.class.getSimpleName());
    }

    public StoreManager getStortManager() {
        return (StoreManager) this.getFunManager(StoreManager.class.getSimpleName());
    }

    public TaskManager getTaskManager() {
        return (TaskManager) this.getFunManager(TaskManager.class.getSimpleName());
    }

    public PlayerGuideManager getPlayerGuideManager() {
        return (PlayerGuideManager) this.getFunManager(PlayerGuideManager.class.getSimpleName());
    }

    public ActivityManager getActivityManager() {
        return (ActivityManager) this.getFunManager(ActivityManager.class.getSimpleName());
    }

    public MedalManager getMedalManager() {
        return (MedalManager) this.getFunManager(MedalManager.class.getSimpleName());
    }

    public BuyResMgr getBuyResourcesManager() {
        return (BuyResMgr) this.getFunManager(BuyResMgr.class.getSimpleName());
    }

    public TeamDungeonManager getTeamDungeonManager() {
        return this.getFunManager(TeamDungeonManager.class.getSimpleName());
    }

    public EndlessCloisterManager getEndlessCloisterManager() {
        return this.getFunManager(EndlessCloisterManager.class.getSimpleName());
    }

    // ----------内部hanlder--------/
    /**
     * 离线写入后的回调逻辑
     */
    public class LDBDoneOfflineCBHandler extends GameInnerHandler {
        @Override
        public void action() {
            Player _player = PlayerManager.getPlayerByPlayerId(playerId);
            if (_player == null || !_player.isOnline()) {
                FriendManager.notifyFriendsSelfUpdate(playerId);
            }
        }
    }
}
