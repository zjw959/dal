package logic.msgBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import logic.item.bean.Item;

import org.game.protobuf.s2c.S2CShareMsg;

/**
 * Created by fxf on 2017/11/29.
 */
public class ShareMsgBuilder {

    public static S2CShareMsg.ListMap createListMap(Integer key,List<Integer> list){
        S2CShareMsg.ListMap.Builder builder = S2CShareMsg.ListMap.newBuilder();
        builder.addAllList(list)
                .setKey(key);
        return builder.build();
    }

    public static S2CShareMsg.Pair createPair(int key,int value){
        S2CShareMsg.Pair.Builder builder = S2CShareMsg.Pair.newBuilder();
        builder.setKey(key).setValue(value);
        return builder.build();
    }

    public static S2CShareMsg.RewardsMsg createReward(int id,int num){
        S2CShareMsg.RewardsMsg.Builder builder = S2CShareMsg.RewardsMsg.newBuilder();
        builder.setId(id).setNum(num);
        return builder.build();
    }

    public static List<S2CShareMsg.RewardsMsg> createReward(Map<Integer, Integer> items){
    	if(items == null) return null;
    	List<S2CShareMsg.RewardsMsg> itemList = new ArrayList<S2CShareMsg.RewardsMsg>(items.size());
    	items.forEach((id, num) -> {
    		S2CShareMsg.RewardsMsg.Builder builder = S2CShareMsg.RewardsMsg.newBuilder();
            builder.setId(id).setNum(num);
            itemList.add(builder.build());
    	});
        return itemList;
    }
    
    public static List<S2CShareMsg.RewardsMsg> createReward(List<Item> items){
    	if(items == null) return null;
    	List<S2CShareMsg.RewardsMsg> itemList = new ArrayList<S2CShareMsg.RewardsMsg>(items.size());
    	items.forEach(item -> {
    		S2CShareMsg.RewardsMsg.Builder builder = S2CShareMsg.RewardsMsg.newBuilder();
            builder.setId(item.getTemplateId()).setNum(item.getNum());
            itemList.add(builder.build());
    	});
        return itemList;
    }
    
//    public static S2CLoginMsg.FunctionSwitch buildFunctionSwitchMsg(){
//        S2CLoginMsg.FunctionSwitch.Builder builder = S2CLoginMsg.FunctionSwitch.newBuilder();
//
//    }
}
