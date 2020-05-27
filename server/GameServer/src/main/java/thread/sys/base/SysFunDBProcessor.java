package thread.sys.base;

import thread.db.DBBaseProcessor;

/**
 * 系统线程数据数据库操作线程
 */
public class SysFunDBProcessor extends DBBaseProcessor {

    protected SysFunDBProcessor() {
        super(SysFunDBProcessor.class.getSimpleName());
    }


    public static SysFunDBProcessor getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    private enum Singleton {
        INSTANCE;
        SysFunDBProcessor processor;

        Singleton() {
            processor = new SysFunDBProcessor();
        }

        SysFunDBProcessor getProcessor() {
            return processor;
        }
    }
}
