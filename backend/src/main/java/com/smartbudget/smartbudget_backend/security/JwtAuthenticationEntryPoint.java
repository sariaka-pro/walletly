package com.smartbudget.smartbudget_backend.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
            
        response.setContentType("application/json");// specifie : je t'envoie un JSON 
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // error 401 non authorized
        
        final Map<String, Object> body = new HashMap<>(); 
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthoriezd");
        body.put("Message", "Authentication token was either missing or invalid");
        body.put("path", request.getServletPath());


        // convertit le requete Java en JSON 
        final ObjectMapper mapper = new ObjectMapper(); 
        mapper.writeValue(response.getOutputStream(), body);
    }

}