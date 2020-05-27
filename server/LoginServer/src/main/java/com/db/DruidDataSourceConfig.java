package com.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
public class DruidDataSourceConfig {
	@Autowired  
    private Environment env;  
  
    @Bean  
    @Primary
    public DataSource getDataSource() {  
        DruidDataSource dataSource = new DruidDataSource();  
        dataSource.setUrl(env.getProperty("spring.datasource.url"));  
        dataSource.setUsername(env.getProperty("spring.datasource.username"));  
        dataSource.setPassword(env.getProperty("spring.datasource.password"));  
        return dataSource;  
    } 
    
//    @Bean(name = "userDataSource")
//    @Qualifier("userDataSource")
//    @ConfigurationProperties(prefix="spring.datasource.user")
//    public DataSource userDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//	@Bean(name="userJdbcTemplate")
//    public JdbcTemplate userJdbcTemplate (
//        @Qualifier("userDataSource")  DataSource dataSource ) {
//        return new JdbcTemplate(dataSource);
//    }
}
