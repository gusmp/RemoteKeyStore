package org.gusmp.remotekeystorebo.app;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
@EnableWebSecurity
@PropertySource("classpath:securityConfig.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value( "#{${backOfficeUsersMap}}" )
	private HashMap<String, String> backOfficeUsers;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {

		for (Map.Entry<String, String> entry : backOfficeUsers.entrySet())
		{
			auth
			.inMemoryAuthentication().passwordEncoder(passwordEncoder())
				.withUser(entry.getKey()).password(entry.getValue()).roles("ADMIN");
		}
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
			.antMatchers("/get/**").permitAll()
			.antMatchers("/login/**").permitAll()
			.antMatchers("/resources/**").permitAll()
			.anyRequest().authenticated()
			.and()
		.formLogin()
			.loginPage("/login")
			.and()
		.logout().permitAll()
			.and()
		.csrf()
			.disable();
	}
}
