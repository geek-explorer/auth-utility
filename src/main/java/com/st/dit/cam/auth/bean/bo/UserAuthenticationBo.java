package com.st.dit.cam.auth.bean.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class UserAuthenticationBo implements Serializable {

        private static final long serialVersionUID = 7253238398926358613L;

        private ApplicationUserBo applicationUser;

        private Set<RoleBo> role;

        private Set<ApplicationServiceBo> services;
}
