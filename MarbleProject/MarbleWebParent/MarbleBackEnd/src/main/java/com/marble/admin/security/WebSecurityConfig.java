package com.marble.admin.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.transaction.TransactionDefinition.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new MarbleUserDetailsService();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/users/**", "/settings/**", "/countries/**", "/states/**").hasAuthority("Admin")
                        .requestMatchers("/categories/**","/brands/**","/menus/**", "/articles/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/products/edit/**", "/products/save", "/products/check_unique")
                        .hasAnyAuthority("Admin", "Editor", "Salesperson")
                        .requestMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
                        .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                        .requestMatchers("/products/**","/articles/**", "/menus/**", "/sections/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/customers/**","/shipping/**","/articles/**", "/get_shipping_cost", "/reports/**").hasAnyAuthority("Admin", "Salesperson")
                        .requestMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                        .requestMatchers("/states/list_by_country/**").hasAnyAuthority("Admin", "Salesperson")
                        .requestMatchers("/orders_shipper/update/**").hasAuthority("Shipper")
                        .requestMatchers("/products/detail/**", "/customers/detail/**").hasAnyAuthority("Admin", "Editor", "Salesperson", "Assistant")
                        .requestMatchers("/reviews/**", "/questions/**").hasAnyAuthority("Admin", "Assistant")
                        .requestMatchers("/images/**", "/js/**", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")   // specify your logout URL here, if different from default
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("dfsafhfjhlkjdsjfkdasjf_123132131231123898")// specify your secret key
                        .tokenValiditySeconds(7*24*60*60)  // specify token validity time in seconds
                        .userDetailsService(userDetailsService()) // specify your UserDetailsService here
                );

        return http.build();
    }

}
