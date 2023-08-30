package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OCRException extends RuntimeException {

    List<String> warnings = new ArrayList<>();

    public OCRException(String message) {
        super(message);
    }

    public OCRException(String message, List<String> warnings) {
        super(message);
        this.warnings = warnings;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}
