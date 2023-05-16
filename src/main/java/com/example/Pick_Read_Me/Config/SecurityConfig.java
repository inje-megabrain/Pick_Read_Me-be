package com.example.Pick_Read_Me.Config;

import com.example.Pick_Read_Me.Jwt.JwtAuthenticationFilter;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Util.CookieUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    JwtAuthenticationFilter jwtAuthenticationFilter(JwtProvider jwtProvider, CookieUtil cookieUtil) {
        return new JwtAuthenticationFilter(jwtProvider, cookieUtil);
    }


    @Bean
    protected SecurityFilterChain config(HttpSecurity http,
                                         JwtProvider jwtProvider,
                                         CookieUtil cookieUtil) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/oauth2/**").permitAll()
                .antMatchers("/api/**").hasAuthority("[USER]")
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/login");

        http.addFilterBefore(jwtAuthenticationFilter(jwtProvider, cookieUtil),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}