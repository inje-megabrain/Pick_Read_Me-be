package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.Domain.Dto.OAuthDto.GetMemberDto;
import com.example.Pick_Read_Me.Domain.Entity.Member;
import com.example.Pick_Read_Me.Exception.MemberNotFoundException;
import com.example.Pick_Read_Me.Jwt.JwtProvider;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Service
@Slf4j
public class MemberService {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;
    public ResponseEntity<GetMemberDto> getMembers(HttpServletRequest request) {
        log.info("a"+request.getHeader("accessToken"));
        log.info("r"+request.getHeader("refreshToken"));
        String token = request.getHeader("accessToken");
        String github_id = jwtProvider.getGithubIdFromToken(token);

        Member member = memberRepository.findById(Long.valueOf(github_id))
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + github_id));

        GetMemberDto getMemberDto = new GetMemberDto(
                member.getName(),member.getProfile(),member.getRepo(), member.getEmail()
        );

        return ResponseEntity.ok(getMemberDto);
    }

    public String getAccessToken(HttpServletRequest request) {
        String token = request.getHeader("refreshToken");
        String github_id = jwtProvider.getRefreshGithubIdFromToken(token);

        HashMap<String, String> m = new HashMap<>();
        m.put("githubId", github_id);
        String accessToken = jwtProvider.generateToken(m);
        return accessToken;
    }
}
