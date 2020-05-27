package thread.player;

import server.GameServer;
import thread.BaseHandler;
import thread.db.DBBaseProcessor;

/**
 * 玩家数据库操作线程
 */
public class PlayerDBProcessor extends DBBaseProcessor {
    private int lineIndex;

    public PlayerDBProcessor(int index) {
        super(PlayerDBProcessor.class.getSimpleName() + "-" + index);
        this.lineIndex = index;
    }

    // 打印时间重写
    int defalutPrintSize = 300;
    int defaultLogWaitTime = 3000;

    public int getLineIndex() {
        return lineIndex;
    }

    @Override
    public void sizePrintAction(BaseHandler handler, long now, int size) {
        if (!GameServer.getInstance().isShutDown()) {
            super.sizePrintAction(handler, now, size);
        }
    }

    @Override
    public void waitPrintAction(BaseHandler handler, long waitTime) {
        if (!GameServer.getInstance().isShutDown()) {
            super.waitPrintAction(handler, waitTime);
        }
    }
}
