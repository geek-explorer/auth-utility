package com.st.dit.cam.auth.bean.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TemplateParameterBo implements Serializable {

       List<KeyValuePair> parameters;
}
