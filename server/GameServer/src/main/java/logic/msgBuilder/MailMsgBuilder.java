package logic.msgBuilder;

import gm.db.mail.bean.BaseMailDBBean;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.game.protobuf.s2c.S2CMailMsg.MailInfo;
import org.game.protobuf.s2c.S2CMailMsg.MailInfoList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import utils.GsonUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


/**
 * 
 * @Description 邮件消息构建器
 * @author LiuJiang
 * @date 2018年7月2日 下午6:03:16
 *
 */
public class MailMsgBuilder {

    public static MailInfo createMailInfo(ChangeType changeType, BaseMailDBBean baseMail) {
        MailInfo.Builder builder = MailInfo.newBuilder();
        builder.setCt(changeType);
        builder.setId(String.valueOf(baseMail.getId()));
        builder.setSenderId((int) baseMail.getSender_id());
        builder.setSenderName("");
        builder.setCreateTime((int) (baseMail.getCreate_date().getTime() / DateUtils.MILLIS_PER_SECOND));
        builder.setModifiedTime((int) (baseMail.getModify_date().getTime() / DateUtils.MILLIS_PER_SECOND));
        builder.setTitle(baseMail.getTitle());
        builder.setBody(baseMail.getBody());
        builder.setStatus(baseMail.getStatus());// 邮件状态
        // 附件
        if (baseMail.getItems() != null) {
            JsonObject json = GsonUtils.toJsonObject(baseMail.getItems());
            for (Map.Entry<String, JsonElement> e : json.entrySet()) {
                RewardsMsg.Builder b = RewardsMsg.newBuilder();
                b.setId(Integer.valueOf(e.getKey()));
                b.setNum(Integer.valueOf(e.getValue().getAsInt()));
                builder.addRewards(b.build());
            }
        }
        return builder.build();
    }

    public static MailInfoList.Builder createMailInfoList(ChangeType type,
            Collection<BaseMailDBBean> mails) {
        MailInfoList.Builder builder = MailInfoList.newBuilder();
        if (mails != null && mails.size() > 0) {
            for (BaseMailDBBean mail : mails) {
                builder.addMails(createMailInfo(type, mail));
            }
        }
        return builder;
    }
}
