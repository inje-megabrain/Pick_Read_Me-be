package com.example.Pick_Read_Me.Util;


import com.example.Pick_Read_Me.Jwt.JwtProvider;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Service
public class CookieUtil {   //토큰을 Cookie로 저장



    public Cookie createCookie(String cookieName, String value){
        Cookie token = new Cookie(cookieName,value);
        token.setHttpOnly(true);
        token.setMaxAge((int) JwtProvider.TOKEN_VALIDATION_SECOND);


        token.setPath("/");
        token.setSecure(true);
        token.setDomain("None");

        /*
        
         */
        return token;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

}

