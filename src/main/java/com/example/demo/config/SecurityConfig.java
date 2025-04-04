package com.example.demo.config;

import com.example.demo.config.filter.JwtFilter;
import com.example.demo.config.filter.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final AuthenticationConfiguration authConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configureChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests
                    .requestMatchers("/user/logout", "/user/signup", "/login", "/logout").permitAll()
//                    .requestMatchers("/user/**").hasRole("USER")
                    .anyRequest().permitAll();
        });
        http.logout(logout -> {
            logout.logoutUrl("/logout").permitAll().clearAuthentication(true).logoutSuccessUrl("/user/logout").deleteCookies("ATOKEN", "JSESSIONID").invalidateHttpSession(true);
        });

        http.sessionManagement(AbstractHttpConfigurer::disable);
        http.addFilterAt(new LoginFilter(authConfiguration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}