// package com.cache;
//
// import java.net.URL;
//
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.cache.ehcache.EhCacheCacheManager;
// import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.FileSystemResource;
//
/// *** 缓存配置. **/
//
// @Configuration
// @EnableCaching // 标注启动缓存.
// public class CacheConfiguration {
//
// /**
// * ehcache 主要的管理器
// *
// * @param bean
// * @return
// */
// @Bean
// public EhCacheCacheManager ehCacheCacheManager() {
// return new EhCacheCacheManager(ehCacheManagerFactoryBean().getObject());
// }
//
// /*
// * 据shared与否的设置, Spring分别通过CacheManager.create() 或new CacheManager()方式来创建一个ehcache基地.
// *
// * 也说是说通过这个来设置cache的基地是这里的Spring独用,还是跟别的(如hibernate的Ehcache共享)
// *
// */
// @Bean
// public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
// EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
// URL url = getClass().getClassLoader().getResource(".");
// FileSystemResource res = new FileSystemResource(url.getPath() + "ehcache.xml");
// // new ClassPathResource("config/ehcache.xml");
// cacheManagerFactoryBean.setConfigLocation(res);
// cacheManagerFactoryBean.setShared(true);
// return cacheManagerFactoryBean;
// }
//
// }
