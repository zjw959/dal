package thread.player.hanlder;

import thread.base.GameInnerHandler;
import thread.player.PlayerProcessor;

/**
 * 玩家线程定时器hanlder
 */
public class LPlayerProcessSecdTickHandler extends GameInnerHandler {
    private PlayerProcessor processor;

    public LPlayerProcessSecdTickHandler(PlayerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void action() {
        processor.tick();
    }
}
