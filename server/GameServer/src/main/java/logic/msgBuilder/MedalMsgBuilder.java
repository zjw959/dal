package logic.msgBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.game.protobuf.s2c.S2CMedalMsg.MedalInfo;
import org.game.protobuf.s2c.S2CMedalMsg.RespActivateMedals;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import logic.character.bean.Player;
import logic.medal.bean.Medal;
import logic.support.MessageUtils;

public class MedalMsgBuilder {

    public static MedalInfo createMedalInfo(Medal medal, ChangeType changeType) {
        MedalInfo.Builder info = MedalInfo.newBuilder();
        info.setCid(medal.getCid());
        info.setStar(medal.getStar());
        info.setQuality(medal.getQuailty());
        info.setEffectTime((int) (medal.getEffectTime() / DateUtils.MILLIS_PER_SECOND));
        info.setIsEquip(medal.isEquip());
        info.setCt(changeType);
        return info.build();
    }

    public static List<MedalInfo> createMedalInfoList(Map<Integer, Medal> medals,
            ChangeType changeType) {
        List<MedalInfo> medalInfoList = new ArrayList<>();
        for (Medal medal : medals.values()) {
            medalInfoList.add(createMedalInfo(medal, changeType));
        }
        return medalInfoList;
    }

    public static List<MedalInfo> createMedalInfoList(List<Medal> medals, ChangeType changeType) {
        List<MedalInfo> medalInfoList = new ArrayList<>();
        for (Medal medal : medals) {
            medalInfoList.add(createMedalInfo(medal, changeType));
        }
        return medalInfoList;
    }

    public static List<MedalInfo> createMedalInfoList(ChangeType changeType, Medal... medals) {
        List<MedalInfo> medalInfoList = new ArrayList<>();
        for (Medal medal : medals) {
            if (medal == null) {
                continue;
            }
            medalInfoList.add(createMedalInfo(medal, changeType));
        }
        return medalInfoList;
    }

    public static RespActivateMedals.Builder getMedalInfos(Map<Integer, Medal> medals,
            ChangeType changeType) {
        RespActivateMedals.Builder builder = RespActivateMedals.newBuilder();
        builder.addAllMedal(createMedalInfoList(medals, changeType));
        return builder;
    }


    public static void notifyMedals(Player player, List<Medal> medals, ChangeType changeType) {
        if (medals == null || medals.isEmpty()) {
            return;
        }
        RespActivateMedals.Builder builder = RespActivateMedals.newBuilder();
        builder.addAllMedal(createMedalInfoList(medals, changeType));
        MessageUtils.send(player, builder);
    }

    public static void notifyMedals(Player player, ChangeType changeType, Medal... medals) {
        if (medals == null || medals.length <= 0) {
            return;
        }
        RespActivateMedals.Builder builder = RespActivateMedals.newBuilder();
        builder.addAllMedal(createMedalInfoList(changeType, medals));
        MessageUtils.send(player, builder);
    }

    public static void notifyMedals(Player player, List<MedalInfo> medals) {
        if (medals == null || medals.isEmpty()) {
            return;
        }
        RespActivateMedals.Builder builder = RespActivateMedals.newBuilder();
        builder.addAllMedal(medals);
        MessageUtils.send(player, builder);
    }
}
