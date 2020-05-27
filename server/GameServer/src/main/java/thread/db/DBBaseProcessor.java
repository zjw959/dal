package thread.db;

import server.GameServer;
import thread.BaseProcessor;

/**
 * 数据库操作线程基类
 */
public abstract class DBBaseProcessor extends BaseProcessor {
    public DBBaseProcessor(String name) {
        super(name);
    }

    public void doExecute(DBBaseHandler handler) {
        if (GameServer.getInstance().isIDEMode()) {
            if (!handler.getClass().getName().startsWith("test")
                    && !handler.getClass().getSimpleName().startsWith("DB")) {
                LOGGER.error(
                        "Breached DBHandler Naming Rules Will Be Ignored . For The Correct Naming Rules: Name StartsWith DB."
                                + "\n Look up : " + handler.getClass().getName());
                return;
            }
        }
        super.executeHandler(handler);
    }
}
