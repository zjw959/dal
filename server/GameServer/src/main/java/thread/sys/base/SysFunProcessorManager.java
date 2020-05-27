package thread.sys.base;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import utils.ClassScanUtils;

/**
 * 系统线程管理器
 */
public class SysFunProcessorManager {
    private static final Logger LOGGER = Logger.getLogger(SysFunProcessorManager.class);

    Set<SysFunctionProcessor> processors = new HashSet<>();

    public void initialize()
            throws Exception {
        Collection<Class<?>> classes = ClassScanUtils.scanPackages("thread.sys");
        for (Class<?> _class : classes) {
            if (SysFunctionProcessor.class.isAssignableFrom(_class)
                    && SysFunctionProcessor.class.hashCode() != _class.hashCode()) {

                // Method method = _class.getMethod("getInstance", null);
                // SysFunctionProcessor sysFunctionProcessor =
                // (SysFunctionProcessor) method.invoke(null, null);

                Constructor<?> constructor = _class.getDeclaredConstructor();
                constructor.setAccessible(true);
                SysFunctionProcessor sysFunctionProcessor =
                        (SysFunctionProcessor) constructor.newInstance();
                constructor.setAccessible(false);
                processors.add(sysFunctionProcessor);
                LOGGER.info("add processor in sysProcessManager, name:" + _class.getSimpleName());
            }
        }

        for (SysFunctionProcessor sysFunctionProcessor : processors) {
            sysFunctionProcessor.initialize();
        }
    }

    public void save() {
        for (SysFunctionProcessor sysFunctionProcessor : processors) {
            sysFunctionProcessor.stopAndSave();
        }
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;
        SysFunProcessorManager processor;

        Singleton() {
            this.processor = new SysFunProcessorManager();
        }

        SysFunProcessorManager getProcessor() {
            return processor;
        }
    }

    /**
     * 获取单例对象
     * 
     * @return
     */
    public static SysFunProcessorManager getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }
}
