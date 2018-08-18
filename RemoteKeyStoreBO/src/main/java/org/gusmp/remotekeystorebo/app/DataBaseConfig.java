package org.gusmp.remotekeystorebo.app;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories(basePackages = "org.gusmp.remotekeystorebo.repository")
@PropertySource("classpath:dataBaseConfig.properties")
public class DataBaseConfig {
	
	@Value( "${dataSource.driverClassName}" )
	private String driverClassName;
	
	@Value( "${dataSource.url}" )
	private String url;
	
	@Value( "${dataSource.dialect}" )
	private String dialect;
	
	@Value( "${dataSource.username}" )
	private String username;
	
	@Value( "${dataSource.password}" )
	private String password;
	
	@Value( "${dataSource.generateDDL}" )
	private Boolean generateDDL;
	
	@Value( "${dataSource.showSQL}" )
	private Boolean showSQL;
	
	@Bean
	public DataSource getDataSource() {
		
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setDriverClassName(driverClassName);

		return new HikariDataSource(config);
	}
	
	@Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(showSQL);
        hibernateJpaVendorAdapter.setGenerateDdl(generateDDL);
        return hibernateJpaVendorAdapter;
    }
	
	@Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setDataSource(dataSource);
        lef.setJpaVendorAdapter(jpaVendorAdapter);
        lef.setPackagesToScan("org.gusmp.remotekeystorebo");
        lef.getJpaPropertyMap().put("hibernate.dialect", dialect);
        return lef;
    }
	
	@Bean(name="transactionManager")
	public PlatformTransactionManager dbTransactionManager(LocalContainerEntityManagerFactoryBean lef) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(lef.getObject());
        return transactionManager;
    }

}
