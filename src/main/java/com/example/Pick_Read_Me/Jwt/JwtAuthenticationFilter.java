package com.example.Pick_Read_Me.Jwt;


import com.example.Pick_Read_Me.Domain.Entity.Refresh;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import com.example.Pick_Read_Me.Util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;

@RequiredArgsConstructor
@Slf4j
//처츰으로 지나가는 Filter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshRepository refreshRepository;
    //1번 필터
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT FIlter");

        String accessToken = null;
        Authentication authenticate;

        //사용자의 principal과 credential 정보를 Authentication에 담는다
        accessToken =  req.getHeader("accessToken");

        if(accessToken==null) {       //토큰이 둘다 없다면

            response401(res, "error : accessToken 토큰 만료");
            log.info("토큰은 안 넣었는데?");
            return;
        }
        else {

            try {
                try {
                    Long github_id = Long.valueOf(jwtProvider.getGithubIdFromToken(accessToken));
                    authenticate = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(github_id, ""));
                    SecurityContextHolder.getContext().setAuthentication(authenticate);
                } catch (Exception e) {
                    response401(res, "error : accessToken Expired");
                    return;
                }
            } catch (Exception e) {
                response401(res, "error : Security Context에 저장 실패");
                return;
            }
        }


        filterChain.doFilter(req, res);

    }

    public void response401(HttpServletResponse res, String print) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write(print);
    }
}

