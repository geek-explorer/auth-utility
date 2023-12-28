package com.st.dit.cam.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties("jwt")
public class ApplicationProps {

       private String secret;

       private Authentication authentication;

       @Data
       public static class Authentication {

              private List<Map<String, Object>> url;

       }
}
