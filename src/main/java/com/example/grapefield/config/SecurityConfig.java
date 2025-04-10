package com.example.grapefield.config;

import com.example.grapefield.config.filter.JwtFilter;
import com.example.grapefield.config.filter.LoginFilter;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

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
//      CorsConfiguration configuration = new CorsConfiguration();
//      configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://www.grapefield.kro.kr"));
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.logout(logout -> logout.logoutUrl("/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .deleteCookies("ATOKEN"));
        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests
                    .requestMatchers("/user/logout", "/user/signup", "/login", "/logout", "/user/email_verify", "/user/email_verify/**").permitAll()
                    .requestMatchers("/admin/**", "/events/register", "/user/**", "/post/register","/post/update/**", "/post/delete/**","/comment/register","/comment/update/**","/comment/delete/**").hasRole("ADMIN")
                    .requestMatchers("/post/register","/post/update/**", "/post/delete/**","/comment/register","/comment/update/**","/comment/delete/**", "/user/**").hasRole("USER")
//                    .requestMatchers("/user/**").hasRole("USER")
//                    .anyRequest().permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**","/v3/api-docs", "/swagger-resources/**", "/webjars/**").permitAll()
                    .anyRequest().authenticated();

        });
//        http.oauth2Login(config-> {
//            config.successHandler(new OAuth2SuccessHandler());
//            config.userInfoEndpoint(endpoint->
//                    endpoint.userService(customOAuth2UserService));
//        });

        http.addFilterAt(new LoginFilter(authConfiguration.getAuthenticationManager()), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}