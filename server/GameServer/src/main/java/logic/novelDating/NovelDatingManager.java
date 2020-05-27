package logic.novelDating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices;

import data.GameDataManager;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.BagType;
import logic.novelDating.structs.NovelDatingData;
import logic.support.LogicScriptsUtils;

public class NovelDatingManager extends PlayerBaseFunctionManager implements IRoleJsonConverter {
    private Map<Integer, NovelDatingData> dataMap = new HashMap<Integer, NovelDatingData>();

    public void reqNovelDatingInfo(Player player, int novelDatingId) {
        LogicScriptsUtils.getINovelDatingScript().reqNovelDatingInfo(player, novelDatingId);
    }

    public void reqStartEntrance(Player player, int novelDatingId, int entranceId) {
        LogicScriptsUtils.getINovelDatingScript().reqStartNovelEntrance(player, novelDatingId,
                entranceId);
    };

    public void ReqGetEventChoices(Player player, ReqGetEventChoices msg) {
        LogicScriptsUtils.getINovelDatingScript().reqGetEventChoices(player, msg);
    };

    public void reqChooseEntrance(Player player, ReqChooseEntranceEvent msg) {
        LogicScriptsUtils.getINovelDatingScript().reqChooseEntrance(player, msg);
    }

    public boolean isMarksEnough(List<Integer> needMarks, NovelDatingData osd) {
        // 临时标记
        List<Integer> flags = new ArrayList<Integer>(osd.getTempFlag());
        if (osd.getEventFlag() != null)
            flags.addAll(osd.getEventFlag());
        return isMarksEnough(needMarks, flags);
    }

    private boolean isMarksEnough(List<Integer> needMarks, List<Integer> existMarks) {
        if (existMarks == null)
            return false;
        // 是否存在标记
        for (Integer mark : needMarks) {
            if (!existMarks.contains(mark))
                return false;
        }
        return true;
    }

    public boolean isItemsEnough(Player player, Map<Integer, Integer> items, NovelDatingData osd) {
        // 临时道具
        Map<Integer, Integer> exists = osd.getTempBag();
        if (osd.getBag() != null)
            combineValueCount(exists, osd.getBag(), true);
        return isItemsEnough(player, items, exists);
    }

    private boolean isItemsEnough(Player player, Map<Integer, Integer> items,
            Map<Integer, Integer> bag) {
        // 正常道具
        Map<Integer, Integer> normal = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> item : items.entrySet()) {
            // 外传道具
            if (GameDataManager.getItemCfgBean(item.getKey()).getBagType() == BagType.FAVOR_DATING
                    && item.getValue() > bag.getOrDefault(item.getKey(), 0))
                return false;
            else {
                // 临时背包
                int needCount = item.getValue() - bag.getOrDefault(item.getKey(), 0);
                if (needCount > 0)
                    normal.put(item.getKey(), needCount);
            }
            int needCount = item.getValue() - bag.getOrDefault(item.getKey(), 0);
            if (needCount > 0) {
                normal.put(item.getKey(), needCount);
            }
        }
        // 是否存在足够道具
        for (Integer templateId : normal.keySet()) {
            if (player.getBagManager().getItemCount(templateId) < normal.get(templateId)) {
                return false;
            }
        }
        return true;
    }

    public void combineValueCount(Map<Integer, Integer> base, Map<Integer, Integer> addition,
            boolean addable) {
        for (Entry<Integer, Integer> entry : addition.entrySet()) {
            int baseCount = base.getOrDefault(entry.getKey(), 0);
            int result = addable ? baseCount + entry.getValue() : baseCount - entry.getValue();
            base.put(entry.getKey(), result);
        }
    }

    public void clearNovelData(NovelDatingData osd) {
        osd.setRmEntrance(null);
        osd.getEntrances().clear();
        osd.getParagraghs().clear();
        osd.setEventFlag(null);
        osd.setCurrentTime(0);
    }
    public Map<Integer, NovelDatingData> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<Integer, NovelDatingData> dataMap) {
        this.dataMap = dataMap;
    }

}
