package com.st.dit.cam.auth.bean.param;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AuthenticationParameter implements Serializable {

        private String authUrl;

        private String applicationCode;

        private String userName;

        private String password;

}
