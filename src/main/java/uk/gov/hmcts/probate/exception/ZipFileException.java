package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ZipFileException extends RuntimeException {
    public ZipFileException(String message) {
        super(message);
    }
}
