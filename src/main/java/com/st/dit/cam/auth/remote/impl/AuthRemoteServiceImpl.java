package com.st.dit.cam.auth.remote.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.st.dit.cam.auth.bean.bo.UserAuthenticationBo;
import com.st.dit.cam.auth.bean.param.AuthenticationParameter;
import com.st.dit.cam.auth.bean.response.JwtResponse;
import com.st.dit.cam.auth.bean.response.Response;
import com.st.dit.cam.auth.config.ApplicationProps;
import com.st.dit.cam.auth.enums.ResponseCode;
import com.st.dit.cam.auth.remote.AuthRemoteService;
import com.st.dit.cam.auth.utility.CodeEncryptorUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthRemoteServiceImpl implements AuthRemoteService {

        private static final Logger LOGGER = LoggerFactory.getLogger(AuthRemoteServiceImpl.class);

        private static final String CONTENT_TYPE_APPLICATION_JSON  = "application/json";

        private static final String URL_AUTHENTICATION_SERVICE      = "/api/v1/cam/authentication/jwt-auth/authenticate";

        private static final String URL_AUTHENTICATION_JWT_SERVICE  = "/api/v1/cam/authentication/jwt-auth/apps=";

        private WebClient webClient;

        private static ObjectMapper objectMapper = new ObjectMapper();

        @Autowired
        private CircuitBreaker authCircuitBreaker;

        @Autowired
        private ApplicationProps applicationProps;

        @Autowired
        private Environment env;

        @Override
        public JwtResponse getJwtAuthenticationInfo(AuthenticationParameter authParameter) {
                if (null == authParameter) {
                     LOGGER.warn("caution:get jwt authentication info::missing required parameter:: Authentication Parameter");
                     return null;
                }
                final Response response = this.callRemoteServiceWithoutParameter(URL_AUTHENTICATION_JWT_SERVICE + authParameter.getApplicationCode().toUpperCase(), authParameter);
                if (null != response && ResponseCode.OK.getCode() == response.getStatusCode()){
                      final JwtResponse jwtResponse = getObjectMapper().convertValue(response.getData(), new TypeReference<JwtResponse>() {});
                      if (null != jwtResponse) {
                          return jwtResponse;
                      }
                }
                return null;
        }

        @Override
        public UserAuthenticationBo getUserToAuthenticateAndRoles(final String applicationCode, final String userName, final String applicationInstance) {
                    if (StringUtils.isEmpty(applicationCode)){
                            LOGGER.warn("caution::fetching user and roles::missing parameter: Application Code");
                            return null;
                    }
                    if (StringUtils.isEmpty(userName)){
                            LOGGER.warn("caution::fetching user and roles::missing parameter: User Name");
                            return null;
                    }
                    if (StringUtils.isEmpty(applicationInstance)) {
                            LOGGER.warn("caution::fetching user and roles::missing parameter: Application Instance");
                            return null;
                    }
                    String authBaseUrl = env.getProperty("remote-api.authentication.url." + applicationInstance.toLowerCase());
                    if (StringUtils.isEmpty(authBaseUrl)) {
                          LOGGER.warn("caution::fetching user and roles::missing parameter: Authentication Base URL");
                          return null;
                    }

                    final AuthenticationParameter authenticationParameter = AuthenticationParameter
                                                                                           .builder()
                                                                                           .authUrl(authBaseUrl)
                                                                                           .applicationCode(applicationCode)
                                                                                           .userName(userName)
                                                                                           .password("T/3DcZFlAmUN61aRqXey3g==")
                                                                                           .build();


                   final Response response = this.callRemoteServiceWithParameter(URL_AUTHENTICATION_SERVICE, authenticationParameter);
                   if (null != response && ResponseCode.OK.getCode() == response.getStatusCode()){
                        final UserAuthenticationBo userAuthenticationBo = getObjectMapper().convertValue(response.getData(), new TypeReference<UserAuthenticationBo>() {});
                        if (null != userAuthenticationBo){
                               return userAuthenticationBo;
                        }
                   }
                   return null;
        }

        private Response callRemoteServiceWithoutParameter(String uri, AuthenticationParameter parameter) {
                 initializeWebClient(parameter.getAuthUrl());
                 final String username = parameter.getUserName();
                 final String password = CodeEncryptorUtil.decrypt(parameter.getPassword());
                 return this.authCircuitBreaker.run(() -> webClient
                            .get()
                            .uri(uri)
                            .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .headers(headers -> headers.setBasicAuth(username, password))
                            .retrieve()
                            .bodyToMono(Response.class)
                            .block(), throwable -> { return this.getRemoteServiceFallback(parameter.getAuthUrl() + uri);});
        }

        private Response callRemoteServiceWithParameter(String uri, AuthenticationParameter parameter){
                initializeWebClient(parameter.getAuthUrl());
                return this.authCircuitBreaker.run(() -> webClient
                           .post()
                           .uri(uri)
                           .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                           .body(BodyInserters.fromValue(parameter))
                           .retrieve()
                           .bodyToMono(Response.class)
                           .block(), throwable -> { return this.getRemoteServiceFallback(parameter.getAuthUrl() + uri);});
        }

        private Response getRemoteServiceFallback(final String serviceUrl){
                LOGGER.warn("caution:remote service fallback::failed connecting to {}.", serviceUrl);
                return getEmptyDataResponse();
        }

        private Response getEmptyDataResponse(){
                Response response = new Response();
                response.setStatusCode(ResponseCode.NO_DATA_FOUND.getCode());
                response.setStatusMessage(ResponseCode.NO_DATA_FOUND.getMessage());
                response.setData(null);

                return response;
        }

        private ObjectMapper getObjectMapper() {
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
                return objectMapper;
        }

        private void initializeWebClient(String authenticationUrl){
                final int size = 16 * 1024 * 1024;
                final ExchangeStrategies strategies = ExchangeStrategies.builder()
                                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                                .build();

                webClient =  WebClient.builder()
                                .baseUrl(authenticationUrl)
                                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .exchangeStrategies(strategies)
                                .build();
        }
}
