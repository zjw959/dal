package logic.novelDating;

import logic.character.bean.Player;
import logic.novelDating.structs.NovelDatingData;

public interface ConditionValidator {
    boolean validate(Player player, NovelDatingData ndd, Object condition);
}
