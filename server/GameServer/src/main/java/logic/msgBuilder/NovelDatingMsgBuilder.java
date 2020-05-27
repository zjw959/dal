package logic.msgBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.game.protobuf.s2c.S2CExtraDatingMsg.DatingInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.EntranceInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoosed;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import data.GameDataManager;
import data.bean.NovelScriptCfgBean;
import logic.character.bean.Player;
import logic.novelDating.structs.NovelDatingData;

public class NovelDatingMsgBuilder {
    public static DatingInfo createNovelDatingInfo(Player player, int novelId,
            NovelDatingData data) {
        // FavorDatingManager mng = player.getFavorDatingManager();
        DatingInfo.Builder info = DatingInfo.newBuilder();
        info.setDatingType(3);
        info.setDatingValue(novelId);
        // 距离当天零点的描述
        info.setStepTime(data.getCurrentTime());
        info.addAllEntrances(getEntrances(data));
        // info.addAllQuality(getQuality(roleId, player));
        // 背包组装
        // if (favorData.getBag() != null && favorData.getBag().size() > 0) {
        // info.addAllBag(getBag(favorData));
        // }
        // 结局
        // if (favorData.getEndings() != null && favorData.getEndings().size() > 0) {
        // for (Integer ending : (List<Integer>) favorData.getEndings())
        // info.addEndings(ending);
        // }
        return info.build();
    }

    public static RespEntranceEventChoosed.Builder createRespEntranceEventChoosed(
            NovelDatingData osd, Map<Integer, Integer> normalItems, boolean hasEnding,
            Map<Integer, Integer> endingItems, Map<Integer, Integer> costItems, int favorDatingId) {
        RespEntranceEventChoosed.Builder resp = RespEntranceEventChoosed.newBuilder();
        resp.setDatingType(3);
        resp.setDatingValue(favorDatingId);
        if (normalItems != null)
            resp.addAllItems(createRewardsMsgs(normalItems));
        if (costItems != null)
            resp.addAllCostItems(createRewardsMsgs(costItems));
        if (hasEnding) {
            resp.addAllEndItems(createRewardsMsgs(endingItems));
            resp.setStepEnd(1);
        }
        return resp;
    }

    public static List<EntranceInfo> getEntrances(NovelDatingData data) {
        List<EntranceInfo> entrances = new ArrayList<EntranceInfo>(data.getEntrances().size());
        Set<Integer> guides = data.getGuideScript();
        for (Integer entrance : (Set<Integer>) data.getEntrances().keySet()) {
            NovelScriptCfgBean scriptBean = GameDataManager.getNovelScriptCfgBean(entrance);
            if (scriptBean == null) {
                continue;
            }

            EntranceInfo.Builder info = EntranceInfo.newBuilder();
            info.setEntranceId(entrance);
            if (!guides.contains(entrance)) {
                info.setGuide(scriptBean.getIsGuide() == 1);
            } else {
                info.setGuide(false);
            }
            entrances.add(info.build());
        }
        return entrances;
    }

    public static List<RewardsMsg> createRewardsMsgs(Map<Integer, Integer> items) {
        if (items == null)
            return null;
        List<RewardsMsg> msgs = new ArrayList<RewardsMsg>(items.size());
        RewardsMsg.Builder bag;
        for (Entry<Integer, Integer> entry : items.entrySet()) {
            bag = RewardsMsg.newBuilder();
            bag.setId(entry.getKey());
            bag.setNum(entry.getValue());
            msgs.add(bag.build());
        }
        return msgs;
    }
}
