package javascript.logic.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.game.protobuf.s2c.S2CCommentMsg;
import org.game.protobuf.s2c.S2CCommentMsg.CommentInfo;

import cn.hutool.core.util.StrUtil;
import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import logic.character.bean.Player;
import logic.comment.ICommentScript;
import logic.comment.bean.CommentBean;
import logic.comment.bean.CommentHotBean;
import logic.constant.DiscreteDataID;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.msgBuilder.CommentMsgBuilder;
import logic.support.MessageUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.service.ERedisType;
import redis.service.RedisServices;
import utils.GsonUtils;
import utils.SensitiveWordsUtil;
import utils.TimeUtil;
import utils.ToolMap;

public class CommentScript extends ICommentScript {

    /** 精灵评价key 结构 sortedset key heroId value：comment,charid,icon,like score:recordtime */
    public transient static String HERO_COMMENT_KEY = "hcmt::";
    /** 质点评价key 结构 sortedset key heroId value：comment,charid,icon,like score:recordtime */
    public transient static String EQUIP_COMMENT_KEY = "ecmt::";
    /** 精灵点赞key 结构 sortedset key heroId value：comment,charid,icon score:like */
    public transient static String LIKE_HERO_COMMENT_KEY = "lhcmt::";
    /** 质点点赞key 结构 sortedset key heroId value：comment,charid,icon score:like */
    public transient static String LIKE_EQUIP_COMMENT_KEY = "lecmt::";
    /** 当日评价数据精灵 key heroId:playerId expire 到0点的剩余时长 */
    public transient static String HERO_COMMENT_DAILY = "hcmtd::";
    /** 当日评价数据质点 key equip:playerId expire 到0点的剩余时长 */
    public transient static String EQUIP_COMMENT_DAILY = "ecmtd::";

    /** 最近一次删除评价排行时间 */
    public static long LAST_REM_COMMENT_TIME;
    /** 删除评价排行时间间隔 */
    public static long REM_COMMENT_INTERVAL = 10 * TimeUtil.MINUTE;

    public static final int COMMENT_TYPE_ITEM = 1;
    public static final int COMMENT_TYPE_HERO = 2;

    /** 评论排名额外记录条数 */
    private static final int COMMENT_RANK_EXTRA = 10;

    @Override
    public int getScriptId() {
        return EScriptIdDefine.COMMENT_SCRIPT.Value();
    }

    @Override
    public void addComment(Player player, int id, String comment, int type,long crossDayTime) {
        String _key = COMMENT_TYPE_HERO == type ? HERO_COMMENT_KEY : EQUIP_COMMENT_KEY;
        _key = _key + id;
        String _dailyKey = COMMENT_TYPE_HERO == type ? HERO_COMMENT_DAILY : EQUIP_COMMENT_DAILY;
        _dailyKey = _dailyKey + id + ":" + player.getPlayerId();

        // 屏蔽字判定
        boolean isDirtyWords = SensitiveWordsUtil.filter(comment) != null;
        if (StrUtil.isBlank(comment) || isDirtyWords) {
            MessageUtils.throwCondtionError(GameErrorCode.COMMENT_ILLEGAL);
        }
        DiscreteDataCfgBean _cfg =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.COMMENT_CONFIG);
        int _minTextLength = ToolMap.getInt("minText", _cfg.getData(), 4);
        int _maxTextLength = ToolMap.getInt("maxText", _cfg.getData(), 40);
        if (comment.length() < _minTextLength) {
            MessageUtils.throwCondtionError(GameErrorCode.COMMENT_TOO_SHORT); 
        }
        if (comment.length() > _maxTextLength) {
            MessageUtils.throwCondtionError(GameErrorCode.COMMENT_TOO_LANG);
        }

        try (Jedis jedis = RedisServices.getRedisService(ERedisType.COMMENT.getType()).getJedis()) {
            // 今日已评价过该精灵/质点
            if (jedis.exists(_dailyKey)) {
                if (COMMENT_TYPE_HERO == type) {
                    MessageUtils.throwCondtionError(GameErrorCode.HOER_COMMENT_EXISI_TODAY);
                } else {
                    MessageUtils.throwCondtionError(GameErrorCode.EQUIP_COMMENT_EXISI_TODAY);
                }
            }
            int _today = TimeUtil.getToday();
            CommentBean _newComment = new CommentBean();
            _newComment.setComment(comment);
            _newComment.setDate(_today);
            _newComment.setIcon(player.getSkinCid());
            _newComment.setLike(0);
            _newComment.setPlayerId(player.getPlayerId());
            _newComment.setName(player.getPlayerName());
            _newComment.setItemId(id);

            jedis.zadd(_key, TimeUtil.getSeconds(), GsonUtils.toJson(_newComment));
            jedis.setex(_dailyKey,
                    (int) ((crossDayTime - System.currentTimeMillis()) / TimeUtil.SECOND), "");

            S2CCommentMsg.RespSingleComment.Builder builder =
                    S2CCommentMsg.RespSingleComment.newBuilder();
            builder.setSuccess(true);
            MessageUtils.send(player, builder);

        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void likeComment(Player player, int commentPlayerId, int id, int date, int type) {
        if (commentPlayerId == player.getPlayerId()) {
            MessageUtils.throwCondtionError(GameErrorCode.COMMENT_CAN_NOT_LIKE_SELF);
        }
        String _hotKey = COMMENT_TYPE_HERO == type ? LIKE_HERO_COMMENT_KEY : LIKE_EQUIP_COMMENT_KEY;
        _hotKey = _hotKey + id;

        String _commentKey = COMMENT_TYPE_HERO == type ? HERO_COMMENT_KEY : EQUIP_COMMENT_KEY;
        _commentKey = _commentKey + id;

        try (Jedis jedis = RedisServices.getRedisService(ERedisType.COMMENT.getType()).getJedis()) {
            DiscreteDataCfgBean cfg =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.COMMENT);
            // 新评论上限
            int _newRankCount = ToolMap.getInt("newRankCount", cfg.getData(), 20);
            List<CommentBean> _newCommentTuple = getComment(jedis, _commentKey,
                    _newRankCount + COMMENT_RANK_EXTRA, CommentBean.class);
            int _newCommentScore = 0;
            CommentBean _newCommentBean = null;
            for (CommentBean _coment : _newCommentTuple) {
                if (_coment.getPlayerId() == commentPlayerId && _coment.getDate() == date) {
                    // 判断有没有这个playerid和date的评论，有的话加点赞加1
                    Double _score = jedis.zscore(_commentKey, GsonUtils.toJson(_coment));
                    Long _second = TimeUtil.getSeconds();
                    if (_score != null) {
                        jedis.zrem(_commentKey, GsonUtils.toJson(_coment));
                        _second = Math.round(_score);
                    }
                    _newCommentScore = _coment.getLike() + 1;
                    _coment.setLike(_newCommentScore);
                    jedis.zadd(_commentKey, _second, GsonUtils.toJson(_coment));
                    _newCommentBean = _coment;
                    break;
                }
            }

            // 如果是新评价，设置点赞值为新评价中的值，
            if (_newCommentScore != 0 && _newCommentBean != null) {
                CommentHotBean _hotBean = newCommentBean2Hot(_newCommentBean);
                jedis.zadd(_hotKey, _newCommentScore, GsonUtils.toJson(_hotBean));
            } else {
                int _hotRankCount = ToolMap.getInt("hotRankCount", cfg.getData(), 10);
                List<CommentHotBean> _hotCommentTuple = getComment(jedis, _hotKey,
                        _hotRankCount + COMMENT_RANK_EXTRA, CommentHotBean.class);
                for (CommentHotBean _coment : _hotCommentTuple) {
                    if (_coment.getPlayerId() == player.getPlayerId()
                            && _coment.getDate() == date) {
                        jedis.zincrby(_hotKey, 1, GsonUtils.toJson(_coment));
                        break;
                    }
                }
            }
            S2CCommentMsg.RespPrise.Builder builder = S2CCommentMsg.RespPrise.newBuilder();
            builder.setSuccess(true);
            MessageUtils.send(player, builder);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void getComment(Player player, int id, int type) {
        String _newCommentKey = COMMENT_TYPE_HERO == type ? HERO_COMMENT_KEY : EQUIP_COMMENT_KEY;
        _newCommentKey = _newCommentKey + id;
        String _hotKey = COMMENT_TYPE_HERO == type ? LIKE_HERO_COMMENT_KEY : LIKE_EQUIP_COMMENT_KEY;
        _hotKey = _hotKey + id;
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.COMMENT.getType()).getJedis()) {
            DiscreteDataCfgBean cfg =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.COMMENT);
            // 新评论上限
            int _newRankCount = ToolMap.getInt("newRankCount", cfg.getData(), 20);
            int _hotRankCount = ToolMap.getInt("hotRankCount", cfg.getData(), 10);

            // 取最新评价的前30名
            List<CommentBean> _newComment =
                    getComment(jedis, _newCommentKey, _newRankCount, CommentBean.class);
            Set<Tuple> _hotComment = getCommentWithScore(jedis, _hotKey, _hotRankCount);

            S2CCommentMsg.RespComment.Builder builder = S2CCommentMsg.RespComment.newBuilder();
            List<CommentInfo.Builder> _nList = CommentMsgBuilder.createCommentInfo(_newComment);
            for (CommentInfo.Builder b : _nList) {
                builder.addNewInfo(b);
            }

            List<CommentInfo.Builder> _hList = CommentMsgBuilder.createHotCommentInfo(_hotComment);
            for (CommentInfo.Builder b : _hList) {
                builder.addHotInfo(b);
            }

            MessageUtils.send(player, builder);

            // 定时删除不在榜单上的数据
            jedis.zremrangeByRank(_newCommentKey, 0, -(_newRankCount + COMMENT_RANK_EXTRA));
            jedis.zremrangeByRank(_hotKey, 0, -(_hotRankCount + COMMENT_RANK_EXTRA));
           /* long _now = System.currentTimeMillis();
            if (LAST_REM_COMMENT_TIME + REM_COMMENT_INTERVAL < _now) {
                LAST_REM_COMMENT_TIME = _now;
            }*/
        } catch (Exception e) {
            throw e;
        }
    }

    private <T> List<T> getComment(Jedis jedis, String key, int rankCount, Class<T> clazz) {
        Set<String> _comments = jedis.zrevrange(key, 0, rankCount);
        ArrayList<T> _result = new ArrayList<T>();
        for (String _comment : _comments) {
            _result.add((T) GsonUtils.fromJson(_comment, clazz));
        }
        return _result;
    }


    private Set<Tuple> getCommentWithScore(Jedis jedis, String key, int rankCount) {
        Set<Tuple> _result = jedis.zrevrangeWithScores(key, 0, rankCount);
        return _result;
    }

    /**
     * 新评论转换成热门评论bean
     * 
     * @param bean
     * @return
     */
    public static CommentHotBean newCommentBean2Hot(CommentBean bean) {
        CommentHotBean _result = new CommentHotBean();
        _result.setComment(bean.getComment());
        _result.setDate(bean.getDate());
        _result.setIcon(bean.getIcon());
        _result.setPlayerId(bean.getPlayerId());
        _result.setName(bean.getName());
        _result.setItemId(bean.getItemId());
        return _result;
    }
}
