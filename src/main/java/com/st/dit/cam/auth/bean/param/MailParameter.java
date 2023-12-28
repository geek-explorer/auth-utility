package com.st.dit.cam.auth.bean.param;

import com.st.dit.cam.auth.bean.bo.TemplateParameterBo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MailParameter implements Serializable {

       private static final long serialVersionUID = -3719750897457195914L;

       private String mailTemplate;

       private String sender;

       private String recipient;

       private String recipientOnCC;

       private TemplateParameterBo templateParameter;
}
