package com.st.dit.cam.auth.remote;

import com.st.dit.cam.auth.bean.param.MailParameter;
import com.st.dit.cam.auth.bean.response.Response;
import com.st.dit.cam.auth.constant.CommonAuthConstant;
import com.st.dit.cam.auth.enums.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MailNotificationService {

        private static final Logger LOGGER = LoggerFactory.getLogger(MailNotificationService.class);

        @Value("${remote-api.mail-notification.base-url}")
        private String mailNotificationBaseUrl;

        private WebClient webClient;

        private static final String CONTENT_TYPE_APPLICATION_JSON  = "application/json";

        private static final String URL_MAIL_SERVICE      = "/api/cam/mail-service/mailer-with-template";

        @Autowired
        private CircuitBreaker authCircuitBreaker;

        public void sendUnreachableServiceNotification(final MailParameter parameter, final String jwtToken) {
                     if (null == parameter){
                         LOGGER.warn("error sending email for unreachable service::missing parameter:: Mail Parameter");
                         return;
                     }
                     final Response response = this.callRemoteServiceWithParameter(URL_MAIL_SERVICE, parameter, jwtToken);
                     if (null != response && ResponseCode.OK.getCode() == response.getStatusCode()){
                         LOGGER.debug("Mail was send successfully. Parameters::{}", parameter.toString());
                     }
        }

        private Response callRemoteServiceWithParameter(final String uri, final MailParameter parameter, final String jwtToken){
                initializeWebClient();
                return this.authCircuitBreaker.run(() -> webClient
                            .post()
                            .uri(uri)
                            .header(HttpHeaders.AUTHORIZATION, CommonAuthConstant.TOKEN_BEARER + jwtToken)
                            .header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON)
                            .body(BodyInserters.fromValue(parameter))
                            .retrieve()
                            .bodyToMono(Response.class)
                            .block(), throwable -> { return this.getRemoteServiceFallback(uri);});
        }

        private Response getRemoteServiceFallback(final String serviceUrl){
                LOGGER.warn("remote service connection error::failed connecting to {}.", serviceUrl);
                return getEmptyDataResponse();
        }

        private Response getEmptyDataResponse(){
                Response response = new Response();
                response.setStatusCode(ResponseCode.NO_DATA_FOUND.getCode());
                response.setStatusMessage(ResponseCode.NO_DATA_FOUND.getMessage());
                response.setData(null);
                return response;
        }

        private void initializeWebClient(){
                    final int size = 16 * 1024 * 1024;
                    final ExchangeStrategies strategies = ExchangeStrategies.builder()
                            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                            .build();

                    webClient =  WebClient.builder()
                            .baseUrl(mailNotificationBaseUrl)
                            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .exchangeStrategies(strategies)
                            .build();
        }
}
