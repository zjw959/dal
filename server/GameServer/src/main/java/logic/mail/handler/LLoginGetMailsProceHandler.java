package logic.mail.handler;

import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import thread.base.GameBaseProcessor;
import thread.base.LProcessCBHandler;


/**
 * 玩家登录时获取邮件线程handler(邮件线程)
 * 
 * @Description 功能描述
 * @author LiuJiang
 * @date 2018年7月4日 下午2:32:09
 *
 */
public class LLoginGetMailsProceHandler extends LProcessCBHandler {
    public LLoginGetMailsProceHandler(Player player, GameBaseProcessor cp,
            LLoginGetMailsCBHandler cbHandler) {
        super(player, cp, cbHandler);
    }

    @Override
    public void action() {
        if (player == null || !player.isOnline()) {
            return;// 玩家已离线
        }
        LLoginGetMailsCBHandler handler = (LLoginGetMailsCBHandler) getHandler();
        boolean isSuccess =
                LogicScriptsUtils.getMailScript().loginGetMailsProce(player.getPlayerId(), handler);
        _doCallBack(isSuccess);
    }

}
