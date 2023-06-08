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

        /*
        
         */
        if(accessToken==null && refreshToken==null) {       //토큰이 둘다 없다면
            response401(res, "error : 토큰이 둘다 만료");
            return;
        }

        if(accessToken==null || jwtProvider.isTokenExpired(accessToken)) {  //accessToken 없거나 토큰이 만료되었다면
            log.info("accessToken 만료된 경우");
            if (refreshToken != null && !jwtProvider.RefreshisTokenExpired(refreshToken)) { //refreshToken 만료가 안되었을 경우

                Long github_id = Long.valueOf(jwtProvider.getRefreshGithubIdFromToken(refreshToken));
                log.info(String.valueOf(github_id));

                Refresh refresh =  refreshRepository.findById(github_id).orElseGet(Refresh::new); //IP검증

                if (refresh.getRefreshToken().equals(refreshToken) && refresh.getIp().equals(req.getRemoteAddr())) {    //IP비교 && 리프래시 토큰 존재하는지 검증하고 저장
                    try {
                        authenticate = jwtProvider.authenticate(new UsernamePasswordAuthenticationToken(github_id, ""));
                        SecurityContextHolder.getContext().setAuthentication(authenticate);
                    }catch(Exception e){

                        log.info(e.getMessage());
                    }

                    response401(res, "error : accessToken Expired");
                    return ;
                }
                else {    //IP가 다른 경우 로그아웃 시켜야함
                    response401(res, "error : 등록한 IP가 다릅니다.\n"+"다시 로그인 해주세요");
                    return ;
                }
            }
            else if(refreshToken==null){    //accessToken존재 하지만 refreshToken이 없음
                log.info("리프래시 없음");
                response401(res, "error : 리프래시 토큰이 없음");
                return ;
            } else if(jwtProvider.RefreshisTokenExpired(refreshToken)){ //리프래시 토큰 만료
                log.info("리프래시 토큰 만료");
                response401(res, "error : 리프래시 토큰 만료");
                return ;
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

