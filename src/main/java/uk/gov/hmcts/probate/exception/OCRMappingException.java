package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OCRMappingException extends RuntimeException {

    public OCRMappingException(String message) {
        super(message);
    }

    public OCRMappingException(String message, Throwable t) {
        super(message, t);
    }
}