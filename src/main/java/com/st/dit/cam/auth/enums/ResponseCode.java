package com.st.dit.cam.auth.enums;

public enum ResponseCode {

      OK(200, "OK", "success"),
      KO(200, "KO", "success"),
      CONNECTION_TIME_OUT(522,"CONNECTION_TIME_OUT", "error"),
      NO_CONTENT( 204, "No Content", "warning"),
      NO_DATA_FOUND(204, "No Data Found"),
      VALIDATION_ERROR(422, "Data Validation Error"),

      UNAUTHORIZED(401, "Unauthorized"),

      MESSAGE_NOT_FOUND(204, "MESSAGE_NOT_FOUND"),
      MISSING_PARAMETER(400, "MISSING_PARAMETER"),
      MISSING_REQ_FIELD(400, "MISSING_REQUIRED_FIELD");

      private Integer code;
      private String  message;
      private String  status;

      ResponseCode(final Integer code, final String message){
            this(code, message, "success");
      }

      ResponseCode(final Integer code, final String message, final String status){
             this.code    = code;
             this.message = message;
             this.status  = status;
      }

      public Integer getCode(){return code;}
      public String  getMessage(){return message;}
      public String  getStatus(){return status;}
}
