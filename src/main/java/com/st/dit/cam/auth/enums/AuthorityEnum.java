package com.st.dit.cam.auth.enums;

public enum AuthorityEnum {

        GUEST("guest", "Guest");

        private String code;

        private String description;

        AuthorityEnum(final String code, final String descriptionn){
            this.code = code;
            this.description = descriptionn;
        }

        public String getCode(){return this.code;}
        public String getDescription(){return this.description;}
}
