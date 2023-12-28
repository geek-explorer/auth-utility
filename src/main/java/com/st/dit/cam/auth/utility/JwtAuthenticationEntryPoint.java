package com.st.dit.cam.auth.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = 704663786884235584L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
           if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
               response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token Expired");
           } else {
               response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
           }
    }
}
