package com.aoher.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.aoher.util.Constants.ROLE_ADMIN;
import static com.aoher.util.Constants.ROLE_USER;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}password").roles(ROLE_USER)
                .and()
                .withUser("admin").password("{noop}password").roles(ROLE_USER, ROLE_ADMIN);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/books/**").hasRole(ROLE_USER)
                .antMatchers(HttpMethod.POST, "/books").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.PUT, "/books/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.PATCH, "/books/**").hasRole(ROLE_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/books/**").hasRole(ROLE_ADMIN)
                .and()
                .csrf().disable()
                .formLogin().disable();
    }
}
