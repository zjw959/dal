package logic.friend;

import java.util.List;

import org.game.protobuf.c2s.C2SFriendMsg.ReqOperate;

import data.GameDataManager;
import logic.basecore.IAcrossDay;
import logic.basecore.IOffline;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.PlayerViewService;
import logic.constant.EAcrossDayType;
import logic.constant.ItemConstantId;
import logic.friend.bean.FriendInfoBean;
import logic.friend.bean.FriendView;
import logic.support.LogicScriptsUtils;
import utils.TimeUtil;

/**
 * 
 * @Description 好友管理器
 * @author LiuJiang
 * @date 2018年6月10日 下午9:25:33
 *
 */
public class FriendManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, IAcrossDay, IOffline {

    /**
     * 上次刷新好友时间
     */
    private transient long lastFreshTime;

    /**
     * 刷新好友次数
     */
    private transient int freshCount = 1;

    /**
     * 推荐好友临时缓存,好友助战用.
     */
    private transient List<FriendView> recommends = null;

    /** 友情点 */
    private int friendPoint = 0;
    /** 最后领取好友奖励时间 */
    private long lastRecvGiftTime;
    /** 今日领取好友奖励次数 */
    private int recvGiftCount = 0;
    /** 上次处理过期好友申请时间 */
    private long lastDealExpireFriendApplyTime;
    /** 检查好友申请过期时间间隔  */
    public static final long CHECK_APPLY_FRIEND_APPLY_INTERVAL =  30 * TimeUtil.ONE_DAY;


    @Override
    public void loginInit() {}

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            // 跨天重置已领取次数
            this.recvGiftCount = 0;
        }
    }

    @Override
    public void offline() {
        this.recommends = null;
    }

    /** 友情点 */
    public int getFriendPoint() {
        return friendPoint;
    }

    /** 友情点 */
    public void setFriendPoint(int friendPoint) {
        this.friendPoint = friendPoint;
    }

    /** 最后领取好友奖励时间 */
    public long getLastRecvGiftTime() {
        return lastRecvGiftTime;
    }

    /** 最后领取好友奖励时间 */
    public void setLastRecvGiftTime(long lastRecvGiftTime) {
        this.lastRecvGiftTime = lastRecvGiftTime;
    }

    /** 领取好友奖励次数 */
    public int getRecvGiftCount() {
        return recvGiftCount;
    }

    /** 领取好友奖励次数 */
    public void setRecvGiftCount(int recvGiftCount) {
        this.recvGiftCount = recvGiftCount;
    }

    public long getLastFreshTime() {
        return lastFreshTime;
    }

    public List<FriendView> getRecommends() {
        return recommends;
    }


    public int getFreshCount() {
        return freshCount;
    }

    public void setFreshCount(int freshCount) {
        this.freshCount = freshCount;
    }

    public void setLastFreshTime(long lastFreshTime) {
        this.lastFreshTime = lastFreshTime;
    }

    public void setRecommends(List<FriendView> recommends) {
        this.recommends = recommends;
    }

    /**
     * 改变友情点
     * 
     * @param num
     * @return
     */
    public boolean changeFriendPoint(int num) {
        // 上限
        int max = GameDataManager.getBaseGoods(ItemConstantId.FRIEND_SHIP_POINT).getTotalMax();
        int total = this.friendPoint + num;
        if (total < 0) {
            if (num > 0) {// 超上限
                total = max;
            } else {
                total = 0;
            }
        }
        if (total > max) {
            total = max;
        }
        if (total != this.friendPoint) {
            this.friendPoint = total;
            return true;
        }
        return false;
    }

    public static void notifyFriendsSelfUpdate(int playerId) {
        LogicScriptsUtils.getIFriendScript().notifyFriendsSelfUpdate(playerId);
    }

    public void getRecommendFriends(boolean isCheckCD, boolean isSend) throws Exception {
        LogicScriptsUtils.getIFriendScript().getRecommendFriends(player, isCheckCD, isSend);
    }

    public static void process(String json) {
        LogicScriptsUtils.getIFriendScript().process(json);
    }

    public boolean isFriend(int pid) {
        return LogicScriptsUtils.getIFriendScript().isFriend(player.getPlayerId(),pid);
    }

    public void resGetFriendsInfoMsg(List<FriendInfoBean> infos) {
        LogicScriptsUtils.getIFriendScript().resGetFriendsInfoMsg(player, infos);
    }

    public void operate(ReqOperate msg) throws Exception {
        LogicScriptsUtils.getIFriendScript().operate(player, msg);        
    }

    public void getFriendsInfo(int rmsgId) {
        LogicScriptsUtils.getIFriendScript().getFriendsInfo(player,rmsgId);
    }

    @Override
    public void tick() {
        long _now = System.currentTimeMillis();
        if (_now - lastDealExpireFriendApplyTime > CHECK_APPLY_FRIEND_APPLY_INTERVAL) {
            ((PlayerViewService) PlayerViewService.getInstance())
                    .clearExpireFriendApplyAysnc(player.getPlayerId());
            lastDealExpireFriendApplyTime = _now;
        }
    }
}
