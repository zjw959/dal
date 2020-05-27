package javascript.logic.elementCollection;

import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CElementCollectMsg;

import com.google.common.collect.Lists;

import logic.elementCollection.IElementCollectionScript;
import logic.character.bean.Player;
import logic.constant.EScriptIdDefine;
import logic.support.MessageUtils;

public class ElementCollectionScript extends IElementCollectionScript{

  @Override
  public int getScriptId() {
    return EScriptIdDefine.ELEMENT_COLLECTION_SCRIPT.Value();
  }

  @Override
  protected void reqGetElementCollection(Player player, Map<Integer, List<Integer>> elementDatas) {
    S2CElementCollectMsg.GetAllElement.Builder builder = S2CElementCollectMsg.GetAllElement.newBuilder();
     elementDatas.forEach((k, v) -> builder.addElment(buildElementsMsg((int)k,(List<Integer>)v)));
     MessageUtils.send(player, builder);
  }
  private S2CElementCollectMsg.Elements buildElementsMsg(int type,List<Integer> cidList){
    S2CElementCollectMsg.Elements.Builder builder = S2CElementCollectMsg.Elements.newBuilder();
    builder.setType(type)
            .addAllCids(cidList);
    return builder.build();
}

  @Override
  protected void recordElement(Player player, int type, int cid,Map<Integer, List<Integer>> elementDatas) {
    List<Integer> list = (List<Integer>) elementDatas.computeIfAbsent(type, key -> Lists.newArrayList());
    if (list.contains(cid)){
        return;
    }
    list.add(cid);
    S2CElementCollectMsg.AddNewElement.Builder builder = S2CElementCollectMsg.AddNewElement.newBuilder();
    builder.setElment(S2CElementCollectMsg.Element.newBuilder().setType(type).setCid(cid)).build();
    MessageUtils.send(player, builder);
  }
}
