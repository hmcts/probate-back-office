package uk.gov.hmcts.probate.exception.model;

import lombok.Data;

@Data
public class AuthError {
    private final int code;
    private final String message;
}
