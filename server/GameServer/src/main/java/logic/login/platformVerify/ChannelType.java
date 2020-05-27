package logic.login.platformVerify;

import exception.LogicModelException;
import logic.constant.ConstDefine;
import logic.constant.LoginErrorCode;

/**
 * 
 * @Description ChannelType
 * @author LiuJiang
 * @date 2018年6月3日 上午11:53:20
 *
 */
public enum ChannelType {
    /**
     * 本地测试
     */
    LOCAL_TEST(0, "LOCAL_TEST"),
    /**
     * 黑桃互动
     */
    HEI_TAO(1, "HEI_TAO");

    private final int id;

    private final String tag;

    ChannelType(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public static ChannelType getChannelType(String tag) {
        for (ChannelType it : ChannelType.values()) {
            if (it.getTag().equals(tag)) {
                return it;
            }
        }
        throw new LogicModelException(ConstDefine.LOG_ERROR_CONDITION_PREFIX, LoginErrorCode.NOT_FOND_CHANNEL, "channel is invalid:" + tag);
    }

}
