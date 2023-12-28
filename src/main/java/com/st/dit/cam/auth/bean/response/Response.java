package com.st.dit.cam.auth.bean.response;


import com.st.dit.cam.auth.enums.ResponseCode;

public class Response extends BaseResponse<Object>{

      public static final String SUCCESS_MESSAGE = "OK";

      public static final String NO_DATA_FOUND = "No Content";

      public Response(){}

      public Response(final ResponseCode responseCode){
              this.setStatusCode(responseCode.getCode());
              this.setStatusMessage(responseCode.getMessage());
      }
}
