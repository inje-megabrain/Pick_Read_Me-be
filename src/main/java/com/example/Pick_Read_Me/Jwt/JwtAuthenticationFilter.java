package com.example.Pick_Read_Me.Jwt;


import com.example.Pick_Read_Me.Domain.Entity.Refresh;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import com.example.Pick_Read_Me.Util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
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


        log.info("JWT FIlter");
        String accessToken = null;
        String refreshToken = null;
        Authentication authenticate;

        //사용자의 principal과 credential 정보를 Authentication에 담는다
        accessToken =  req.getHeader("accessToken");
        refreshToken = req.getHeader("refreshToken");
        if(accessToken==null && refreshToken==null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write("{\"error\": \"토큰이 둘다 없습니다\"}");

            return;
        }
        if(accessToken==null || jwtProvider.isTokenExpired(accessToken)) {
            refreshToken = req.getHeader("refreshToken");
            log.info("리프레시::"+refreshToken);
            if (refreshToken != null && !jwtProvider.RefreshisTokenExpired(refreshToken)) {
                log.info("githubID검사");

                Long github_id = Long.valueOf(jwtProvider.getRefreshGithubIdFromToken(refreshToken));
                log.info(String.valueOf(github_id));

                Refresh refresh =  refreshRepository.findById(github_id).orElseGet(Refresh::new);
                log.info("refersh :: "+refresh.getRefreshToken());
                log.info(refresh.getIp());
                log.info(req.getRemoteAddr());
                if (refresh.getRefreshToken().equals(refreshToken) && refresh.getIp().equals(req.getRemoteAddr())) {
                    try {
                        authenticate = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(github_id, ""));
                        SecurityContextHolder.getContext().setAuthentication(authenticate);
                    }catch(Exception e){

                        throw new RuntimeException("authenticate 오류!");

                    }

                    HashMap<String, String> m = new HashMap<>();
                    m.put("githubId", String.valueOf(github_id));

                    accessToken = jwtProvider.generateToken(m);
                    refreshToken = jwtProvider.generateRefreshToken(m);

                    refresh.setRefreshToken(refreshToken);
                    refresh.setId(refresh.getId());
                    refreshRepository.save(refresh);
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"error\": \"리프레시 토큰 통과\"}");

                } else {
                    log.info("리프래시 토큰 IP 다름");
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write("{\"error\": \"리프레시 토큰 IP 다름\"}");
                    return ;

                }
            } else if(refreshToken==null){
                log.info("리프래시 없음");
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write("{\"error\": \"리프레시 토큰이 없어요\"}");
                return ;

            } else if(jwtProvider.RefreshisTokenExpired(refreshToken)){
                log.info("리프래시 유효기간 지남");
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write("{\"error\": \"리프레시 토큰 만료\"}");


                return ;
            }
        }
        else if(accessToken!=null && !jwtProvider.isTokenExpired(accessToken)){
            try {
                String github_id = jwtProvider.getGithubIdFromToken(accessToken);
                authenticate = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(github_id, ""));
                SecurityContextHolder.getContext().setAuthentication(authenticate);
            }catch(Exception e)
            {
                return ;

            }
        }else if(accessToken==null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write("{\"error\": \"액세스 토큰 없음\"}");
            return ;

        }else if(jwtProvider.isTokenExpired(accessToken)){
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write("{\"error\": \"액세스 토큰 만료\"}");
            return ;

        }
        filterChain.doFilter(req, res);

    }

}

