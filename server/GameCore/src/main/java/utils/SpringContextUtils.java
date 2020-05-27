package utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextUtils implements ApplicationContextAware {

    /**
     * Spring应用上下文环境
     */
    private static Map<Integer, ApplicationContext> applicationContexts = new HashMap<>();

    public static SpringContextUtils getInstance() {
        return Singleton.INSTANCE.getProcesser();
    }

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     * 
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtils.applicationContexts
                .put(Integer.valueOf(applicationContext.getDisplayName()),
                        applicationContext);
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext(Integer applicationId) {
        return applicationContexts.get(applicationId);
    }


    /**
     * 获取对象 这里重写了bean方法，起主要作用
     * 
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     */
    public static Object getBeanByName(Integer applicationId, String name) throws BeansException {
        return applicationContexts.get(applicationId).getBean(name);
    }

    /**
     * 根据bean的名称获取相应类型的对象，使用泛型，获得结果后，不需要强制转换为相应的类型
     * 
     * @param clazz bean的类型，使用泛型
     * @return T类型的对象
     */
    public static <T> T getBean(Integer applicationId, Class<T> clazz) {
        T bean = applicationContexts.get(applicationId).getBean(clazz);

        return bean;
    }

    private enum Singleton {
        INSTANCE;

        SpringContextUtils processer;

        private Singleton() {
            processer = new SpringContextUtils();
        }

        SpringContextUtils getProcesser() {
            return processer;
        }
    }

    public static void main() {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"dao_context.xml"});
        context.setDisplayName("1");
        new SpringContextUtils().setApplicationContext(context);
    }
}
