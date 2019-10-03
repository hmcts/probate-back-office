package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Data
public class OCRMappingException extends RuntimeException {
    private final String error;
    private final String warning;

    public OCRMappingException(String error, String warning, Throwable exception) {
        log.error(exception.getMessage());
        this.warning = warning;
        this.error = error;
    }
}
