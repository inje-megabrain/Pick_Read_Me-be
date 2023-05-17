package com.example.Pick_Read_Me.Service;

import com.example.Pick_Read_Me.Domain.Member;
import com.example.Pick_Read_Me.Repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String Id) throws UsernameNotFoundException {
        Long github_id = Long.parseLong(Id);
        log.info(String.valueOf(github_id));
        Member member = memberRepository.findById(github_id).orElseGet(Member::new);
        log.info(String.valueOf(member));
        if(member==null){
            throw new UsernameNotFoundException(github_id  + " : 사용자 존재하지 않음");
        }
        return new Details(member);
    }
}

