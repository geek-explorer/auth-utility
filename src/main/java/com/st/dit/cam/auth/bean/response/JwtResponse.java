package com.st.dit.cam.auth.bean.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    @Getter
    private String username;

    @Getter
    private String lastName;

    @Getter
    private String firstName;

    @Getter
    private String applicationName;

    @Getter
    private String jwToken;

    @Getter
    private List<String> authorities;
}
