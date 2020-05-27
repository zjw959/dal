package redis;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


public abstract class AbsRedisPropertyConfigurer extends PropertyPlaceholderConfigurer {
    private static final Logger LOGGER = Logger.getLogger(AbsRedisPropertyConfigurer.class);
    
    /**
     * 存取properties配置文件key-value结果
     */
    private Properties props; 

    protected abstract void _init(ConfigurableListableBeanFactory beanFactoryToProcess,
            Properties props);

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
            Properties props) throws BeansException {

        this._init(beanFactoryToProcess, props);

        LOGGER.info(props.toString());
        
        super.processProperties(beanFactoryToProcess, props);
        this.props = props;
    }

    public String getProperty(String key) {
        return this.props.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return this.props.getProperty(key, defaultValue);
    }

    public Object setProperty(String key, String value) {
        return this.props.setProperty(key, value);
    }

    protected String _url(String pw, String host, String port, String db) {
        return "redis://:" + pw + "@" + host + ":" + port + "/" + db;
    }
}
