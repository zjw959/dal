package logic.TriggerEvent;

import event.Event;
import logic.character.bean.Player;
import logic.event.EventResult;
import script.IScript;

public abstract class ITriggerEventScrip extends EventResult implements IScript{

	protected abstract void eventPerformed(Player player, Event event);
}
