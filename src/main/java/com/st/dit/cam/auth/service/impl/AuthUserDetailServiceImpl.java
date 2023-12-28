package com.st.dit.cam.auth.service.impl;

import com.st.dit.cam.auth.bean.bo.ApplicationServiceBo;
import com.st.dit.cam.auth.bean.bo.RoleBo;
import com.st.dit.cam.auth.bean.bo.UserAuthenticationBo;
import com.st.dit.cam.auth.constant.CommonAuthConstant;
import com.st.dit.cam.auth.enums.AuthorityEnum;
import com.st.dit.cam.auth.remote.AuthRemoteService;
import com.st.dit.cam.auth.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthUserDetailServiceImpl extends BaseService implements UserDetailsService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserDetailServiceImpl.class);

    @Autowired
    private AuthRemoteService authRemoteService;

    @Value("${spring.profiles.active}")
    private String applicationInstance;

    @Value("${spring.application.name}")
    private String activeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
           String[] userEntities = username.split(CommonAuthConstant.JWT_USER_ENTITY_SEPARATOR);

           final String application         =  userEntities[0];
           final String applicationInstance =  userEntities[1];
           final String user                =  userEntities[2];


           if (StringUtils.isEmpty(applicationInstance) || !applicationInstance.equalsIgnoreCase(this.applicationInstance)){
                     LOGGER.warn("caution::user {} not configured to authorize access in this service {}-{}.", user, application, applicationInstance);
                     throw new UsernameNotFoundException("error::user " + user + " not configured to authorize access::instance: " + this.applicationInstance);
           }

           final UserAuthenticationBo userAuthenticationBo = authRemoteService.getUserToAuthenticateAndRoles(application, user, applicationInstance);

           if (null != userAuthenticationBo){
                 if (CollectionUtils.isEmpty(userAuthenticationBo.getServices()) ||
                                             !userAuthenticationBo.getServices().stream()
                                                                                .map(ApplicationServiceBo::getServiceCode)
                                                                                .anyMatch(srv -> srv.equalsIgnoreCase(activeService))) {
                       LOGGER.warn("caution::application {} not configured to authorize access in this service {}.", application, activeService);
                       throw new UsernameNotFoundException("error::application " + application + " not configured to authorize access this service::service name :" + activeService);
                }

                final List<SimpleGrantedAuthority> authorities = getUserAuthorities(userAuthenticationBo.getRole());
                return new User(userAuthenticationBo.getApplicationUser().getUserName(),StringUtils.EMPTY,authorities);
           }

           throw new UsernameNotFoundException("error::user " + user + " not configured to authorize access::instance: " + this.applicationInstance);
    }


    private List<SimpleGrantedAuthority> getUserAuthorities(Set<RoleBo> userRoles){
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userRoles)){
                    authorities = userRoles.stream()
                                    .map(userRole -> new SimpleGrantedAuthority(userRole.getRoleId()))
                                    .collect(Collectors.toList());
            }else {
                   authorities.add(new SimpleGrantedAuthority(AuthorityEnum.GUEST.getCode()));
           }
           return authorities;
    }


}
