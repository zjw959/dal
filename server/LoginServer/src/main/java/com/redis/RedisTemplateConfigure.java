package com.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@EnableCaching
public class RedisTemplateConfigure extends CachingConfigurerSupport{
	@Bean
	@ConfigurationProperties(prefix = "spring.redis.standalone")
	protected RedisStandaloneConfiguration standaloneConfiguration() {
		RedisStandaloneConfiguration configure = new RedisStandaloneConfiguration();
		return configure;
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		RedisStandaloneConfiguration standaloneConfig = standaloneConfiguration();
		JedisConnectionFactory factory = new JedisConnectionFactory(standaloneConfig);
		return factory;
	}
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		JedisConnectionFactory connectionFactory = jedisConnectionFactory();
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setHashKeySerializer(hashKeySerializer());
		template.setHashValueSerializer(hashValueSerializer());
		return template;
	}
	
	@Bean
	public RedisSerializer<?> hashKeySerializer() {
		return new StringRedisSerializer();	
	}
	
	@Bean
	public RedisSerializer<?> hashValueSerializer(){
		return new FastJsonRedisSerializer<>(Object.class);	
	}
	
    public RedisCacheConfiguration redisCacheConfiguration(int seconds){
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer));
        if(seconds>0){
        	configuration = configuration.entryTtl(Duration.ofSeconds(seconds));
        }
        return configuration;
    }
	
	@Bean  
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(
            RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
            this.redisCacheConfiguration(0)
            , this.getRedisCacheConfigurationMap()
        );
    }
	
	private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("token", this.redisCacheConfiguration(60 * 60 * 24));// token保存1天
        redisCacheConfigurationMap.put("ban", this.redisCacheConfiguration(60*60*24));//ban保存1天
        redisCacheConfigurationMap.put("account", this.redisCacheConfiguration(60*60*24));//ban保存1天
        return redisCacheConfigurationMap;
    }
}
