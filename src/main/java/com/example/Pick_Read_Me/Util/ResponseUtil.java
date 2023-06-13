package com.example.Pick_Read_Me.Util;


import io.jsonwebtoken.io.IOException;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class ResponseUtil {

    public HttpServletResponse sendResponse(HttpServletResponse response, HttpStatus status, String contentType, String content) throws IOException {
        response.setStatus(status.value());
        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");
        return response;
    }

}