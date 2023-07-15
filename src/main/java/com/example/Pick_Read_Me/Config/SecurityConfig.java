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
import org.springframework.web.filter.CorsFilter;


import java.util.Arrays;
import java.util.logging.Filter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final JwtProvider tokenService;
    private String[] permitList={
            "/v2/**",
            "/v3/**",
            "/configuration",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/api/home/**",
            "/test",
            "/api/login", //로그인시 Jwt Filter를 거쳐버림 안 거치게 수정
            "/home?accessToken=*", //리다이렉트
            "/home\\?accessToken=.*",
            "/test/**",
            "/api/get/accessToken", //새로운 토큰 발급
            "/api/logout", //로그아웃
            "/test/**",
            "/api/get/all/posts", //모든글 보기
            "/frontend",
            "/frontend/**", //프론트
            "/api/get/infinity/posts", //무한스크롤
    };
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    private final RefreshRepository refreshRepository;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    JwtAuthenticationFilter jwtAuthenticationFilter(JwtProvider jwtProvider, RefreshRepository refreshRepository) {
        return new JwtAuthenticationFilter(jwtProvider,  refreshRepository);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {  //해당 URL은 필터 거치지 않겠다
        return (web -> web.ignoring().antMatchers(permitList)); //여기는 모든 필터를 거치지 않는 곳
        //return (web -> web.ignoring().antMatchers("/test"));
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://s3-ap-northeast-2.amazonaws.com", "https://github.com", "http://52.78.80.150:9000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Origin", "X-Requested-With", "Content-Type", "Accept", "Key", "Authorization", "access-control-allow-origin", "Authorizationsecret", "accessToken", "refreshToken"));
        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsFilter(source);
    }


    @Bean
    protected SecurityFilterChain config(HttpSecurity http, JwtProvider jwtProvider) throws Exception {
        http
                .addFilterBefore(corsFilter(), ChannelProcessingFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers("/api/**").permitAll()
                //여기는 권한 즉 user,admin 등등 모든 처리를 하겠다는 걸
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter(jwtProvider,  refreshRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/api/login")
                .and()
                .redirectionEndpoint()
                .baseUri("/api/auth/code")
                .and()
                .successHandler(successHandler)
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

        return http.build();

    }


}