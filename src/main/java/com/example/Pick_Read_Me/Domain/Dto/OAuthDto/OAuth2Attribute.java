package com.example.Pick_Read_Me.Domain.Dto.OAuthDto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String repo;

    private String profile;
    private Long id;

    public static OAuth2Attribute of(String provider, String attributeKey,  //
                                     Map<String, Object> attributes) {
        switch (provider) {
            case "github":
                return ofGithub("id", attributes);
            default:
                throw new RuntimeException();
        }
    }


    private static OAuth2Attribute ofGithub(String attributeKey,
                                           Map<String, Object> attributes) {

        Integer id = (Integer) attributes.get("id");
        return OAuth2Attribute.builder()
                .id(Long.valueOf(id))
                .name((String) attributes.get("login"))
                .email((String) attributes.get("email"))
                .repo((String) attributes.get("html_url"))
                .profile((String) attributes.get("avatar_url"))
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("name", name);
        map.put("email", email);
        map.put("id", id);
        map.put("repo", repo);
        map.put("profile", profile);

        return map;
    }
}