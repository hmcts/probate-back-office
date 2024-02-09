package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SocketException extends RuntimeException {
    public SocketException(String message) {
        super(message);
    }
}
