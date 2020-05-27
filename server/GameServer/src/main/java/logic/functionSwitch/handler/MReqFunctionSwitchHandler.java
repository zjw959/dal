package logic.functionSwitch.handler;

import java.util.Map;

import logic.character.bean.Player;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.s2c.S2CLoginMsg.FunctionSwitch;
import org.game.protobuf.s2c.S2CLoginMsg.RespFunctionSwitch;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import exception.AbstractLogicModelException;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SLoginMsg.ReqFunctionSwitch.class)
public class MReqFunctionSwitchHandler extends MessageHandler {
    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) this.getGameData();
        // 获取服务器功能开关
        RespFunctionSwitch.Builder builder = RespFunctionSwitch.newBuilder();
        Map<Integer, Boolean> map = FunctionSwitchService.getInstance().getSwitches();
        if (map.size() > 0) {
            for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
                FunctionSwitch.Builder fbuilder = FunctionSwitch.newBuilder();
                fbuilder.setCt(ChangeType.DEFAULT);
                fbuilder.setSwitchType(entry.getKey());
                fbuilder.setOpen(entry.getValue());
                builder.addSwitchs(fbuilder);
            }
        }
        MessageUtils.send(player, builder);
    }
}
