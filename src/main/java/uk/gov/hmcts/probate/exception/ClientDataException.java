package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientDataException extends RuntimeException {

    public ClientDataException(final String message) {
        super(message);
    }
}
