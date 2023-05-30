package com.example.Pick_Read_Me.Config;

import com.example.Pick_Read_Me.Jwt.JwtAuthenticationFilter;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import com.example.Pick_Read_Me.Service.CustomOAuth2UserService;
import com.example.Pick_Read_Me.Service.OAuth2SuccessHandler;
import com.example.Pick_Read_Me.Util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.logging.Filter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final JwtProvider tokenService;

    private final CorsFilter corsFilter;
    private String[] permitList={
            "/v2/**",
            "/v3/**",
            "/configuration",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/home/**",
            "/test",
            "/api/**",
            "/login",
            "/home?accessToken=*",
            "/home\\?accessToken=.*",
            "/api/**",
            "/test/**",
    };
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    private final RefreshRepository refreshRepository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    JwtAuthenticationFilter jwtAuthenticationFilter(JwtProvider jwtProvider, CookieUtil cookieUtil, RefreshRepository refreshRepository) {
        return new JwtAuthenticationFilter(jwtProvider, cookieUtil, refreshRepository);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {  //해당 URL은 필터 거치지 않겠다
        return (web -> web.ignoring().antMatchers(permitList));
        //return (web -> web.ignoring().antMatchers("/test"));
    }


    @Bean
    protected SecurityFilterChain config(HttpSecurity http, JwtProvider jwtProvider,
                                         CookieUtil cookieUtil) throws Exception {
        http
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()

                .addFilterBefore(jwtAuthenticationFilter(jwtProvider, cookieUtil, refreshRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/login")
                .and()
                .redirectionEndpoint()
                .baseUri("/auth/code")
                .and()
                .successHandler(successHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        return http.build();

    }


}