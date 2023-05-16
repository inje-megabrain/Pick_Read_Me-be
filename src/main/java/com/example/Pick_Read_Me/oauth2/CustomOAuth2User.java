package com.example.Pick_Read_Me.oauth2;

import com.example.Pick_Read_Me.Domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String id;
    private Role role;


    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String id, Role role) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
        this.role = role;
    }

}