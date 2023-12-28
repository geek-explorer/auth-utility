package com.st.dit.cam.auth.service;

import com.st.dit.cam.auth.bean.param.AuthenticationParameter;
import com.st.dit.cam.auth.bean.response.JwtResponse;

public interface AuthJwtDetailService {

        JwtResponse getJwtDetail(final AuthenticationParameter authParameter) throws Exception;

}
