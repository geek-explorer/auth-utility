package com.st.dit.cam.auth.bean.bo;


import lombok.Data;

import java.io.Serializable;

@Data
public class ApplicationUserBo implements Serializable {

        private static final long serialVersionUID = 1122032141821520584L;

        private String userId;

        private String userName;

        private String firstName;

        private String lastName;

        private String middleName;

        private String emailAddress;

        private Boolean active;

}
