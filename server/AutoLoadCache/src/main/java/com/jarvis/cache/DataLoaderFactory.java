package com.jarvis.cache;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jiayu.qiu
 */
public class DataLoaderFactory extends BasePooledObjectFactory<DataLoader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoaderFactory.class);


    private static volatile DataLoaderFactory instance;

    private final GenericObjectPool<DataLoader> factory;

    private DataLoaderFactory() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(1024);
        config.setMaxIdle(50);
        config.setMinIdle(8);
        config.setBlockWhenExhausted(false);// 当Pool中没有对象时不等待，而是直接new个新的

        AbandonedConfig abandonConfig = new AbandonedConfig();
        abandonConfig.setRemoveAbandonedTimeout(300);
        abandonConfig.setRemoveAbandonedOnBorrow(true);
        abandonConfig.setRemoveAbandonedOnMaintenance(true);
        factory = new GenericObjectPool<DataLoader>(this, config, abandonConfig);
    }

    public static DataLoaderFactory getInstance() {
        if (null == instance) {
            synchronized (DataLoaderFactory.class) {
                if (null == instance) {
                    instance = new DataLoaderFactory();
                }
            }
        }
        return instance;
    }

    public DataLoader getDataLoader() {
        try {
            return factory.borrowObject();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new DataLoader();
    }

    public void returnObject(DataLoader loader) {
        loader.reset();
        factory.returnObject(loader);
    }

    @Override
    public DataLoader create() throws Exception {
        return new DataLoader();
    }

    @Override
    public PooledObject<DataLoader> wrap(DataLoader obj) {
        return new DefaultPooledObject<DataLoader>(obj);
    }
}
