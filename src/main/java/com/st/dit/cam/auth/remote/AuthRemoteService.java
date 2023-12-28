package com.st.dit.cam.auth.remote;

import com.st.dit.cam.auth.bean.bo.UserAuthenticationBo;
import com.st.dit.cam.auth.bean.param.AuthenticationParameter;
import com.st.dit.cam.auth.bean.response.JwtResponse;

public interface AuthRemoteService {

       JwtResponse getJwtAuthenticationInfo(final AuthenticationParameter authParameter);

       UserAuthenticationBo getUserToAuthenticateAndRoles(final String applicationCode, final String userName, final String applicationInstance);
}
