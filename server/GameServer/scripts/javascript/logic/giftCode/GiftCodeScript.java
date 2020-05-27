package javascript.logic.giftCode;

import java.util.Map;
import java.util.Map.Entry;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.constant.GiftConstant;
import logic.functionSwitch.FunctionSwitchService;
import logic.giftCode.GiftCodeService;
import logic.giftCode.IGiftCodeScript;
import logic.giftCode.handler.GiftCodeCheckHttpCallBack;
import logic.mail.MailService;
import logic.support.MessageUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CLoginMsg.GiftCodeRps;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import server.ServerConfig;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;
import thread.player.hanlder.LPlayerProceBagHandler;
import utils.ExceptionEx;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import data.GameDataManager;

public class GiftCodeScript extends IGiftCodeScript {
    private static final Logger LOGGER = Logger.getLogger(GiftCodeScript.class);
    @Override
    public int getScriptId() {
        return EScriptIdDefine.GIFT_CODE_SCRIPT.Value();
    }

    @Override
    protected void reqGiftCode(Player player, String code) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.GIFT_CODE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:giftCode");
        }
        // 异步
        String url =
                ServerConfig.getInstance().getGiftCodeVerifyUrl() + "?id=" + code + "&roleId="
                        + player.getPlayerId() + "&channelId=" + player.getChannelId();
        LOGGER.info(ConstDefine.LOG_GIFT_CODE_PREFIX + " url=" + url);
        HttpGet get = new HttpGet(url);
        get.setConfig(GiftCodeService.getInstance().getRequestConfig());
        GiftCodeService.getInstance().getClient()
                .execute(get, new GiftCodeCheckHttpCallBack(player.getPlayerId(), code));
    }

    @Override
    protected void giftCodeCallBack(int playerId, String code, HttpResponse res) {
        try {
            Player player = PlayerManager.getPlayerByPlayerId(playerId);
            String result = EntityUtils.toString(res.getEntity());
            LOGGER.info(ConstDefine.LOG_GIFT_CODE_PREFIX + " playerId:" + playerId + " code:"
                    + code + " result:" + result);
            JSONObject json = JSONObject.parseObject(result);
            // 结果返回
            String status = json.getString("status");
            if (!GiftConstant.OK.equals(status)) {
                int errCode = GameErrorCode.GIFT_CODE_ERR;
                if (GiftConstant.ALREADY_USE.equals(status)) {
                    errCode = GameErrorCode.GIFT_CODE_GAIN_OVER;
                } else if (GiftConstant.INVALID_CHANNEL.equals(status)) {
                    errCode = GameErrorCode.GIFT_CODE_CHANNEL_ERR;
                }
                GiftCodeService.getInstance().giftCodeFailed(playerId, errCode,
                        "giftCode failed. code=" + code + " status="
                        + status);
                return;
            }
            String goodsInfo = json.getString("goodsInfo");
            String[] infos = StringUtils.split(goodsInfo, ",");
            Map<Integer, Integer> items = Maps.newHashMap();
            for (String info : infos) {
                String[] kv = StringUtils.split(info, ":");
                items.put(Integer.parseInt(kv[0]), Integer.parseInt(kv[1]));
            }
            if (player != null && player.isOnline()) {// 玩家在线
                // 直接发奖
                LPlayerProceBagHandler handler =
                        new LPlayerProceBagHandler(player, items, EReason.ITEM_GIFT_CODE);
                PlayerProcessor processor =
                        PlayerProcessorManager.getInstance().getProcessor(player.getLineIndex());
                processor.executeInnerHandler(handler);
                GiftCodeRps.Builder builder = GiftCodeRps.newBuilder();
                for (Entry<Integer, Integer> entry : items.entrySet()) {
                    RewardsMsg.Builder rewards = RewardsMsg.newBuilder();
                    rewards.setId(entry.getKey());
                    rewards.setNum(entry.getValue());
                    builder.addRewards(rewards);
                }
                MessageUtils.send(player, builder);
            } else {// 玩家离线
                // 邮件发奖
                String title = GameDataManager.getStringCfgBean(212011).getText();
                String body = GameDataManager.getStringCfgBean(212012).getText();
                ((MailService) (MailService.getInstance())).sendPlayerMail(false,
                        player.getPlayerId(), title, body, items, EReason.ITEM_GIFT_CODE);
            }
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }
    }
}
