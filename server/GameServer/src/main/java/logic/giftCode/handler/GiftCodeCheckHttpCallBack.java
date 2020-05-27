package logic.giftCode.handler;

import logic.constant.GameErrorCode;
import logic.giftCode.GiftCodeService;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * 
 * @Description 礼包码兑换验证回调
 * @author LiuJiang
 * @date 2018年8月1日 上午11:15:31
 *
 */
public class GiftCodeCheckHttpCallBack implements FutureCallback<HttpResponse> {
    private final int pid;
    private final String code;

    public GiftCodeCheckHttpCallBack(int pid, String code) {
        this.pid = pid;
        this.code = code;
    }

    @Override
    public void completed(HttpResponse t) {
        GiftCodeService.getInstance().giftCodeCallBack(pid, code, t);
    }

    @Override
    public void failed(Exception excptn) {
        GiftCodeService.getInstance().giftCodeFailed(pid, GameErrorCode.GIFT_CODE_PLATFORM_ERR,
                "giftCode failed. code=" + code + " result=" + excptn.toString());
    }

    @Override
    public void cancelled() {
        GiftCodeService.getInstance().giftCodeFailed(pid, GameErrorCode.GIFT_CODE_PLATFORM_ERR,
                "giftCode failed. code=" + code + " result=" + "cancelled");
    }
}
