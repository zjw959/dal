package logic.elementCollection;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import script.IScript;

public abstract class IElementCollectionScript implements IScript {

  protected abstract void reqGetElementCollection(Player player, Map<Integer, List<Integer>> elementDatas);
  protected abstract void recordElement(Player player,int type, int cid,Map<Integer, List<Integer>> elementDatas);
}
