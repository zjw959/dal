package logic.playerGuide;

import org.game.protobuf.s2c.S2CPlayerMsg;

import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.support.MessageUtils;

public class PlayerGuideManager extends PlayerBaseFunctionManager implements IRoleJsonConverter {
    private String data;

    public void reqNewPlayerGuide(String data) {
        this.data = data;
        S2CPlayerMsg.RespNewPlayerGuide.Builder builder =
                S2CPlayerMsg.RespNewPlayerGuide.newBuilder();
        builder.setData(this.data);

        MessageUtils.send(player, builder);
    }

    public String getData() {
        return data;
    }

}
