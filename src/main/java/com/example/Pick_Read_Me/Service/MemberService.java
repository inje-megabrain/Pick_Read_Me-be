package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.Domain.Dto.OAuthDto.GetMemberDto;
import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Domain.Entity.Refresh;
import com.example.Pick_Read_Me.Exception.MemberNotFoundException;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Service
@Slf4j
public class MemberService {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshRepository refreshRepository;

    @Autowired
    private MemberRepository memberRepository;
    //회원 정보를 조회해서 반환합니다
    public ResponseEntity<GetMemberDto> getMembers(Authentication authentication) {
        Long github_id = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));

        GetMemberDto getMemberDto = new GetMemberDto(
                member.getName(),member.getRepo(),member.getProfile(), member.getEmail(), member.getPosts().size()
        );

        return ResponseEntity.ok(getMemberDto);
    }

    /*

     */
    public HttpServletResponse getAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String accessToken = null;

        String token = request.getHeader("refreshToken");

        try {
            String github_id = jwtProvider.getRefreshGithubIdFromToken(token);

            Refresh r = refreshRepository.findById(Long.valueOf(github_id)).orElseGet(Refresh::new);

            if (r.getRefreshToken().equals(token) && r.getIp().equals(request.getRemoteAddr())) {

                HashMap<String, String> m = new HashMap<>();
                m.put("githubId", github_id);
                accessToken = jwtProvider.generateToken(m);
                response200(response, accessToken);
                return response;
            }
            else {response401(response, "error: IP 다름");
                return response;}
        }catch(Exception e) {
            response401(response, "error : refreshToken 만료");
            return response;
        }




    }
    public void response401(HttpServletResponse res, String print) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter writer = null;
        try {
            writer = res.getWriter();
            writer.write(print);
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close(); // 출력 스트림을 닫습니다.
            }
        }
    }

    public void response200(HttpServletResponse res, String print) throws IOException {
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        PrintWriter writer = null;
        try {
            writer = res.getWriter();
            writer.write(print);
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close(); // 출력 스트림을 닫습니다.
            }
        }
    }

}
