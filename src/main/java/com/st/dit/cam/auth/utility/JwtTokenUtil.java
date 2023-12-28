package com.st.dit.cam.auth.utility;

import com.st.dit.cam.auth.constant.CommonAuthConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -4621563297531049199L;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    public static final long JWT_TOKEN_VALIDITY = 10 * 60;

    @Value("${jwt.secret}")
    private String secret;

    public String getUsernameFromToken(String token) throws Exception {
        return getClaimFromToken(token, Claims::getSubject);
    }


    public String getSystemUserFromToken(String token){
        try {
            final String userEntity = getUsernameFromToken(token);
            if (StringUtils.isNotEmpty(userEntity)){
                String[] userEntities = userEntity.split(CommonAuthConstant.JWT_USER_ENTITY_SEPARATOR);
                return userEntities[2];
            }
        }catch (Exception ex){
             LOGGER.error("Error on getting System User from jwtToken.", ex);
        }
        return StringUtils.EMPTY;
    }

    public String getApplicationFromToken(String token) {
        try {
            final String userEntity = getUsernameFromToken(token);
            if (StringUtils.isNotEmpty(userEntity)){
                String[] userEntities = userEntity.split(CommonAuthConstant.JWT_USER_ENTITY_SEPARATOR);
                return userEntities[0];
            }
        }catch (Exception ex){
            LOGGER.error("Error on getting Application from jwtToken.", ex);
        }
        return StringUtils.EMPTY;
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
            Map<String, Object> claims = new HashMap<>();
            return doGenerateToken(claims, userDetails.getUsername());
    }


    public Boolean validateToken(String token, UserDetails userDetails) throws Exception {
        final String username = getUsernameFromToken(token);

        String[] userEntities    = username.split(CommonAuthConstant.JWT_USER_ENTITY_SEPARATOR);
        if (StringUtils.isEmpty(userEntities[2])){
            return false;
        }
        final String user        = userEntities[2];
        return (user.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

}
