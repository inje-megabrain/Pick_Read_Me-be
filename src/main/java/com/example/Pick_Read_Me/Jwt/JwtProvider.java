package com.example.Pick_Read_Me.Jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Pick_Read_Me.Service.Details;
import com.example.Pick_Read_Me.Service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider implements AuthenticationProvider {

    private final MyUserDetailsService userDetailsService;

    public static final long TOKEN_VALIDATION_SECOND = 1800L;   // 30분
    public static final long REFRESH_TOKEN_VALIDATION_TIME = 60*1440L; // 14일



    @Value("${spring:jwt:secret}")
    private String SECRET_KEY;

    @Value("${group:name}")
    private String ISSUER;

    private Algorithm getSigningKey(String secretKey) {
        return Algorithm.HMAC256(secretKey);
    }

    private Map<String, Claim> getAllClaims(DecodedJWT token) {
        return token.getClaims();
    }

    public String getGithubIdFromToken(String token) {
        DecodedJWT verifiedToken = validateToken(token);
        return verifiedToken.getClaim("githubId").asString();
    }
    public String getRefreshGithubIdFromToken(String token) {
        DecodedJWT verifiedToken = RefreshvalidateToken(token);
        return verifiedToken.getClaim("githubId").asString();
    }
    private JWTVerifier getTokenValidator() {
        return JWT.require(getSigningKey(SECRET_KEY))
                .withIssuer(ISSUER)
                .acceptExpiresAt(TOKEN_VALIDATION_SECOND)
                .build();
    }
    private JWTVerifier RefreshgetTokenValidator() {
        return JWT.require(getSigningKey(SECRET_KEY))
                .withIssuer(ISSUER)
                .acceptExpiresAt(REFRESH_TOKEN_VALIDATION_TIME)
                .build();
    }

    public String generateToken(Map<String, String> payload) {

        return doGenerateToken(TOKEN_VALIDATION_SECOND, payload);
    }

    public String generateRefreshToken(Map<String, String> payload) {
        return doGenerateToken(REFRESH_TOKEN_VALIDATION_TIME, payload);
    }

    private String doGenerateToken(long expireTime, Map<String, String> payload) {

        return JWT.create()
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + expireTime))
                .withPayload(payload)
                .withIssuer(ISSUER)
                .sign(getSigningKey(SECRET_KEY));
    }

    private DecodedJWT validateToken(String token) throws JWTVerificationException {
        JWTVerifier validator = getTokenValidator();
        return validator.verify(token);
    }

    private DecodedJWT RefreshvalidateToken(String token) throws JWTVerificationException {
        JWTVerifier validator = RefreshgetTokenValidator();
        return validator.verify(token);
    }
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = validateToken(token);
            return false;
        } catch (JWTVerificationException e) {
            log.info("accessToken 만료");
            return true;
        }
    }

    public boolean RefreshisTokenExpired(String token) {
        try {
            DecodedJWT decodedJWT = RefreshvalidateToken(token);
            return false;
        } catch (JWTVerificationException e) {
            log.info("리프래시 토큰 만료");
            return true;
        }
    }

    //UsernamePassword찾는 곳
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info(String.valueOf(authentication));
        log.info("Authentication username: " + authentication.getPrincipal());
        Details userDetails = (Details) userDetailsService.loadUserByUsername(String.valueOf(authentication.getPrincipal()));
        log.info("User details: " + userDetails);
        log.info(String.valueOf(userDetails));
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()); //권한
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }

}