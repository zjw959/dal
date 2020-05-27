package logic.giftCode;

import logic.character.bean.Player;

import org.apache.http.HttpResponse;

import script.IScript;

public abstract class IGiftCodeScript implements IScript {

    /** 请求兑换礼包码 */
    protected abstract void reqGiftCode(Player player, String code);

    /** 处理兑换礼包码回调 */
    protected abstract void giftCodeCallBack(int playerId, String code, HttpResponse res);

}
