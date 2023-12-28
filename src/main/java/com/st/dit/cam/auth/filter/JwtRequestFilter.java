package com.st.dit.cam.auth.filter;

import com.st.dit.cam.auth.utility.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final String REQUEST_TOKEN_KEY         = "Authorization";
    private static final String PREFIX_TOKEN_KEY          = "Token ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

             final String requestTokenHeader = request.getHeader(REQUEST_TOKEN_KEY);

             String jwtToken    = null;
             String userEntity   = null;

             if (StringUtils.isNotEmpty(requestTokenHeader) && requestTokenHeader.startsWith(PREFIX_TOKEN_KEY)) {
                     jwtToken = requestTokenHeader.substring(6);
                     try{
                          userEntity = jwtTokenUtil.getUsernameFromToken(jwtToken);
                     }catch (IllegalArgumentException ex){
                          LOGGER.error("error::jwt request filter::unable to get JWT Token from request header.", ex);
                     }catch (ExpiredJwtException ex){
                          response.setStatus(SC_FORBIDDEN);
                          LOGGER.error("error::jwt request filter::JWT Token has been expired.");
                     }catch (Exception ex){
                          LOGGER.error("error::jwt request filter::JWT Token validation:: ", ex);
                     }
             }else{
                     LOGGER.warn("caution::JWT Token does not start with {} or not provided.", PREFIX_TOKEN_KEY);
             }

             if (StringUtils.isNotEmpty(userEntity)
                                  && null == SecurityContextHolder.getContext().getAuthentication()){
                     try{
                         UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity);
                         if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                             UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                                    userDetails, StringUtils.EMPTY, userDetails.getAuthorities());
                             usernamePasswordAuthenticationToken
                                     .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                             SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                         }
                     }catch (Exception ex){
                         LOGGER.error("error::JTW Token validation:: ", ex);
                     }
             }
             filterChain.doFilter(request, response);
    }
}
