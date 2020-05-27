package logic.summon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CSummonMsg;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;

public class HistoryRecord {
    private int type;
    private List<Record> records = new ArrayList<>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @SuppressWarnings("rawtypes")
    public void addRecord(int itemId, int itemNum) {
        if(itemId == 500001) {
            return;
        }
        
        Record record = new Record();
        record.setTime(System.currentTimeMillis());
        record.setItemId(itemId);
        record.setItemNum(itemNum);
        records.add(record);
        
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(14001);
        Map data = discreteDataCfgBean.getData();
        int size = (int) data.get("summonHistory");
        if(records.size() > size) {
            int removeSize = records.size() - size;
            for(int i = 0; i < removeSize; i++) {
                records.remove(0);
            }
        }
    }
    
    public S2CSummonMsg.HistoryRecord.Builder buildHistoryRecord() {
        S2CSummonMsg.HistoryRecord.Builder historyRecordBuilder = S2CSummonMsg.HistoryRecord.newBuilder();
        historyRecordBuilder.setType(type);
        for(int i = records.size() - 1; i >= 0; i--) {
            Record record = records.get(i);
            S2CSummonMsg.Record.Builder recordBuilder = S2CSummonMsg.Record.newBuilder();
            recordBuilder.setTime(record.getTime() / 1000);
            recordBuilder.setItemId(record.getItemId());
            recordBuilder.setItemNum(record.getItemNum());
            historyRecordBuilder.addRecords(recordBuilder);
        }
        return historyRecordBuilder;
    }
}
