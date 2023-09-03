package com.example.Pick_Read_Me;

import com.example.Pick_Read_Me.Repository.MemberRepository;
import com.example.Pick_Read_Me.Repository.RefreshRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.security.Security;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ExtendWith(SpringExtension.class) // Spring 테스트와 통합
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//Mock환경에서 실행, 즉 실제 서버가 아닌 가상의서 서버를 사용
@AutoConfigureMockMvc //MovkMvc객체 자동 구성 HTTP요청 Mock으로 보내고 응답을 확인함
@Transactional
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;    //GET,POST,PATCH,DELETE 등의 요청을 만들어 보낼 수 있디.

    @Autowired
    private WebApplicationContext context; //servlet에 접근가

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshRepository refreshRepository;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity()) //Spring Security 구성 적용
                .build();
    }


}
