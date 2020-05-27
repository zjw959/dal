package logic.basecore;

import event.Event;
import event.IEventListener;
import logic.character.bean.Player;
import logic.constant.EEventType;

/**
 * 跨天
 */
public interface ILevelUp extends IEventListener, ILoginInit {

    @Override
    public default void registerPerformed(Player player) {
        player.registerEventListener(EEventType.LEVEL_UP.value(), this);
    }

    @Override
    public default void eventPerformed(Event event) {
        final int eventId = event.getEventId();
        if (eventId == EEventType.LEVEL_UP.value()) {
            levelUp();
        }
    }

    abstract void levelUp();
}
