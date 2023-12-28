package com.st.dit.cam.auth.service.impl;

import com.st.dit.cam.auth.bean.param.AuthenticationParameter;
import com.st.dit.cam.auth.bean.response.JwtResponse;
import com.st.dit.cam.auth.remote.AuthRemoteService;
import com.st.dit.cam.auth.service.AuthJwtDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthJwtDetailServiceImpl implements AuthJwtDetailService {

        private static final Logger LOGGER = LoggerFactory.getLogger(AuthJwtDetailServiceImpl.class);

        @Autowired
        private AuthRemoteService authRemoteService;

        @Override
        public JwtResponse getJwtDetail(AuthenticationParameter authParameter) throws Exception {
                LOGGER.debug("start::fetch jwt detail::parameter: {}", authParameter);

                final JwtResponse jwtResponse = authRemoteService.getJwtAuthenticationInfo(authParameter);
                LOGGER.debug("end::fetch jwt detail::parameter: {}", authParameter);
                return jwtResponse;
        }
}
