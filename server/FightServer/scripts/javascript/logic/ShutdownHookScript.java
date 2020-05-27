package javascript.logic;

import org.apache.log4j.Logger;
import logic.constant.EScriptIdDefine;
import logic.support.IShutDownScript;
import script.ILifeCycle;
import server.FightServer;
import thread.TeamProcessorManager;

public class ShutdownHookScript implements IShutDownScript, ILifeCycle, Runnable {
    private static final Logger LOGGER = Logger.getLogger(ShutdownHookScript.class);

    @Override
    public void init() {}

    @Override
    public void destroy() {}

    @Override
    public int getScriptId() {
        return EScriptIdDefine.SHUTDOWN_SCRIPT.Value();
    }


    @Override
    public void rest() {
        // 移除之前的hook
        FightServer.getInstance().removeShutdownHook();
        // 添加新的hook
        FightServer.getInstance().addShutdownHook(new Thread(this));
    }

    @Override
    public void stop() {
        run();
    }

    @Override
    public void run() {
        LOGGER.info("ShutdownHookScript---停服脚本-----begin-------------");
        LOGGER.info("JVM exit, call GameServer.stop");
        FightServer.getInstance().setShutDown(true);
        
        TeamProcessorManager.getInstance().shutdown();
    }
}
