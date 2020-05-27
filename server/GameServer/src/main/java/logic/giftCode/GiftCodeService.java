package logic.giftCode;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import message.SMessage;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CLoginMsg.GiftCodeRps;

import thread.sys.base.SysService;
import utils.ChannelUtils;


/**
 * 
 * @Description 礼包码兑换
 * @author LiuJiang
 * @date 2018年8月1日 上午10:50:57
 *
 */
public class GiftCodeService extends SysService {
    private static final Logger LOGGER = Logger.getLogger(GiftCodeService.class);

    protected GiftCodeService() {
        requestConfig =
                RequestConfig.custom().setSocketTimeout(10 * 1000).setConnectTimeout(10 * 1000)
                        .build();
        client = HttpAsyncClients.createDefault();
        client.start();

    }

    private CloseableHttpAsyncClient client;
    private final RequestConfig requestConfig;

    public CloseableHttpAsyncClient getClient() {
        return client;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    /**
     * 请求兑换礼包码
     */
    public void reqGiftCode(Player player, String code) {
        IGiftCodeScript script = LogicScriptsUtils.getGiftCodeScript();
        script.reqGiftCode(player, code);
    }

    /**
     * 处理兑换礼包码回调
     */
    public void giftCodeCallBack(int playerId, String code, HttpResponse res) {
        IGiftCodeScript script = LogicScriptsUtils.getGiftCodeScript();
        script.giftCodeCallBack(playerId, code, res);
    }

    /**
     * 处理兑换礼包码失败的情况
     */
    public void giftCodeFailed(int playerId, int errCode, String info) {
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        LOGGER.warn(ConstDefine.LOG_GIFT_CODE_PREFIX + " playerId:" + playerId + " errCode:"
                + errCode + info == null ? "" : "note:" + info + " ctx:"
                + ChannelUtils.logInfo(player != null ? player.getCtx() : null));
        if (player != null && player.isOnline()) {
            SMessage msg = new SMessage(GiftCodeRps.MsgID.eMsgID_VALUE, errCode);
            MessageUtils.send(player.getCtx(), msg);
        }
    }

    public static GiftCodeService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        GiftCodeService instance;

        private Singleton() {
            instance = new GiftCodeService();
        }

        GiftCodeService getInstance() {
            return instance;
        }
    }
}
