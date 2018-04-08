package uk.gov.hmcts.probate.model;

import java.io.Serializable;

public class BusinessValidationError implements Serializable {

    private final String param;
    private final String code;
    private final String msg;

    public BusinessValidationError(String param, String code, String msg) {
        this.param = param;
        this.code = code;
        this.msg = msg;
    }

    public String getParam() {
        return param;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
