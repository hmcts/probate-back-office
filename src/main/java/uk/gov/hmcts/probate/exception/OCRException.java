package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OCRException extends RuntimeException {

    public OCRException(String message) {
        super(message);
    }
}
