package com.example.Pick_Read_Me.Jwt;


import com.example.Pick_Read_Me.Domain.Refresh;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import com.example.Pick_Read_Me.Util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
//처츰으로 지나가는 Filter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final RefreshRepository refreshRepository;

    //1번 필터
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = null;
        String refreshToken = null;
        Authentication authenticate;
        //사용자의 principal과 credential 정보를 Authentication에 담는다
        log.info("Jwt");
        accessToken =  req.getHeader("accessToken");
        log.info(accessToken);
        if(accessToken==null || jwtProvider.isTokenExpired(accessToken)) {
            refreshToken = req.getHeader("refreshToken");
            log.info(refreshToken);
            if (refreshToken != null || !jwtProvider.RefreshisTokenExpired(refreshToken)) {
                Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(refreshToken));
                log.info(String.valueOf(github_id));

                Refresh refresh =  refreshRepository.findById(github_id).orElseGet(Refresh::new);
                log.info(refresh.getRefreshToken());
                log.info(refresh.getIp());
                if (refresh.getRefreshToken().equals(refreshToken) && refresh.getIp().equals(req.getRemoteAddr())) {
                    try {
                        authenticate = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(github_id, ""));
                        SecurityContextHolder.getContext().setAuthentication(authenticate);
                    }catch(Exception e){
                        throw new RuntimeException("다시 로그인 하세요.!");
                    }

                    HashMap<String, String> m = new HashMap<>();
                    m.put("githubId", String.valueOf(github_id));

                    accessToken = jwtProvider.generateToken(m);
                    refreshToken = jwtProvider.generateRefreshToken(m);

                    refresh.setRefreshToken(refreshToken);
                    refresh.setId(refresh.getId());
                    refreshRepository.save(refresh);

                } else {
                    throw new RuntimeException("등록한 IP가 다릅니다 다시 로그인해주세요!");
                }
            } else {
                throw new RuntimeException("다시로그인 하세요");
            }
        }
        else if(accessToken!=null && !jwtProvider.isTokenExpired(accessToken)){
            try {
                String github_id = jwtProvider.getGithubIdFromToken(accessToken);
                authenticate = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(github_id, ""));
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            }catch(Exception e)
            {
                throw new RuntimeException("다시 로그인하세요");
            }
        }
        filterChain.doFilter(req, res);

    }

}

