package logic.msgBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.game.protobuf.s2c.S2CCommentMsg;
import org.game.protobuf.s2c.S2CCommentMsg.CommentInfo;

import logic.comment.bean.CommentBean;
import logic.comment.bean.CommentHotBean;
import redis.clients.jedis.Tuple;
import utils.GsonUtils;

/**
 * 
 * @Description 评价信息构建器
 * @author hongfu.wang
 * @date 2018-08-08
 *
 */
public class CommentMsgBuilder {
    /**
     * 创建单个最新评价信息
     */
    public static CommentInfo.Builder createCommentInfo(CommentBean info) {
        S2CCommentMsg.CommentInfo.Builder builder = S2CCommentMsg.CommentInfo.newBuilder();
        builder.setComment(info.getComment()).setCommentDate(info.getDate())
                .setHeroId(info.getIcon()).setItemId(info.getItemId()).setName(info.getName())
                .setPlayerId(info.getPlayerId()).setPrise(info.getLike());
        return builder;
    }

    /**
     * 创建多个最新评价信息
     */
    public static List<CommentInfo.Builder> createCommentInfo(List<CommentBean> infos) {
        List<CommentInfo.Builder> list = new ArrayList<>(infos.size());
        for (CommentBean friendInfo : infos) {
            list.add(createCommentInfo(friendInfo));
        }
        return list;
    }

    /**
     * 创建单个热门评价信息
     */
    public static CommentInfo.Builder createHotCommentInfo(CommentHotBean info, int like) {
        S2CCommentMsg.CommentInfo.Builder builder = S2CCommentMsg.CommentInfo.newBuilder();
        builder.setComment(info.getComment()).setCommentDate(info.getDate())
                .setHeroId(info.getIcon()).setItemId(info.getItemId()).setName(info.getName())
                .setPlayerId(info.getPlayerId()).setPrise(like);
        return builder;
    }

    /**
     * 创建多个热门评价信息
     */
    public static List<CommentInfo.Builder> createHotCommentInfo(Set<Tuple> hotComment) {
        List<CommentInfo.Builder> _result = new ArrayList<>();
        for (Tuple _comment : hotComment) {
            int _score = (int) _comment.getScore();
            CommentHotBean _bean = GsonUtils.fromJson(_comment.getElement(), CommentHotBean.class);
            _result.add(createHotCommentInfo(_bean, _score));
        }
        return _result;
    }
}
