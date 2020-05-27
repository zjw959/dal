package logic.favor;

import logic.character.bean.Player;
import logic.favor.structs.FavorDatingData;

public interface ConditionValidator {
    boolean validate(Player player, FavorDatingData osd, Object condition, int favorDatingId);
}
