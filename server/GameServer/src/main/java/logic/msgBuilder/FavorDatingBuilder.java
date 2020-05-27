package logic.msgBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.game.protobuf.s2c.S2CExtraDatingMsg.DatingInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.EntranceInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.FavorStatueInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.QualityInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.RespEntranceEventChoosed;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.favor.structs.FavorDatingData;

public class FavorDatingBuilder {

    public static DatingInfo createFavorDateInfo(FavorDatingData favorData, Player player,
            int roleId, int favorDatingId) {
        DatingInfo.Builder info = DatingInfo.newBuilder();
        info.setDatingType(2);
        info.setDatingValue(favorDatingId);
        // 距离当天零点的描述
        info.setStepTime(favorData.getCurrentTime());
        info.addAllEntrances(getEntrances(favorData));
        info.addAllQuality(getQuality(roleId, player));
        // 背包组装
        if (favorData.getBag() != null && favorData.getBag().size() > 0) {
            info.addAllBag(getBag(favorData));
        }
        // 结局
        if (favorData.getEndings() != null && favorData.getEndings().size() > 0) {
            for (Integer ending : (List<Integer>) favorData.getEndings())
                info.addEndings(ending);
        }
        return info.build();
    }

    @SuppressWarnings("unchecked")
    public static FavorStatueInfo creteFavorStatueInfo(FavorDatingData osd, int fcbId, int statue,
            List<Integer> completeDating) {
        FavorStatueInfo.Builder info = FavorStatueInfo.newBuilder();
        info.setFavorId(fcbId);
        info.setStatue(statue);
        int needEnergy = 0;
        if (completeDating != null) {
            DiscreteDataCfgBean cfg =
                    GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FAVOR_DATING_COST);
            if (cfg != null && cfg.getData() != null) {
                List<Integer> valuelist = new ArrayList<>(cfg.getData().values());
                valuelist.stream().sorted();
                needEnergy = osd == null ? (Integer) cfg.getData().get(1)
                        : cfg.getData().get(osd.getRepeat()) == null
                                ? (Integer) valuelist.get(valuelist.size() - 1)
                                : (Integer) cfg.getData().get(osd.getRepeat());
            }
            info.setEnergy(needEnergy);
        }
        if (completeDating == null || !completeDating.contains(fcbId)) {
            info.setFirstPass(0);
        } else {
            info.setFirstPass(1);
        }
        return info.build();
    }
    
    public static RespEntranceEventChoosed.Builder createRespEntranceEventChoosed(
            FavorDatingData osd,
            Map<Integer, Integer> normalItems, boolean hasEnding, Map<Integer, Integer> endingItems,
            Map<Integer, Integer> costItems, int favorDatingId, Player player, int roleId) {
        RespEntranceEventChoosed.Builder resp = RespEntranceEventChoosed.newBuilder();
        resp.setDatingType(2);
        resp.setDatingValue(favorDatingId);
        resp.addAllQuality(getQuality(roleId, player));
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
    public static List<RewardsMsg> getBag(FavorDatingData osd) {
        List<RewardsMsg> items = new ArrayList<RewardsMsg>(osd.getBag().size());
        RewardsMsg.Builder bag;
        for (Entry<Integer, Integer> entry : osd.getBag().entrySet()) {
            bag = RewardsMsg.newBuilder();
            bag.setId(entry.getKey());
            bag.setNum(entry.getValue());
            items.add(bag.build());
        }
        return items;
    }

    public static List<EntranceInfo> getEntrances(FavorDatingData osd) {
        List<EntranceInfo> entrances = new ArrayList<EntranceInfo>(osd.getEntrances().size());
        for (Integer entrance : (Set<Integer>) osd.getEntrances().keySet()) {
            EntranceInfo.Builder info = EntranceInfo.newBuilder();
            info.setEntranceId(entrance);
            info.setGuide(false);
            entrances.add(info.build());
        }
        return entrances;
    }

    public static List<QualityInfo> getQuality(int roleId,Player player) {
        List<QualityInfo> qualityInfos = new ArrayList<QualityInfo>();
        QualityInfo.Builder quality;
        Map<Integer, FavorDatingData> dataMap =
                player.getFavorDatingManager().getDataMap().get(roleId);
        if (dataMap == null) {
            return qualityInfos;
        }
        Map<Integer, Integer> qualityMap = new HashMap<Integer, Integer>();
        for (FavorDatingData fdd : dataMap.values()) {
            // 已经拥有的属性
            for (Entry<Integer, Integer> entry : fdd.getQualityMap().entrySet()) {
                qualityMap.putIfAbsent(entry.getKey(), 0);
                if (qualityMap.containsKey(entry.getKey())) {
                    qualityMap.put(entry.getKey(),
                            qualityMap.get(entry.getKey()) + entry.getValue());
                }
            }
            // 重新玩的临时属性
            for (Entry<Integer, Integer> entry : fdd.getTempQuality().entrySet()) {
                qualityMap.putIfAbsent(entry.getKey(), 0);
                if (qualityMap.containsKey(entry.getKey())) {
                    qualityMap.put(entry.getKey(),
                            qualityMap.get(entry.getKey()) + entry.getValue());
                }
            }
        }
        for (Entry<Integer, Integer> entry : qualityMap.entrySet()) {
            quality = QualityInfo.newBuilder();
            quality.setQualityId(entry.getKey());
            quality.setValue(entry.getValue());
            qualityInfos.add(quality.build());
        }
        return qualityInfos;
    }
}
