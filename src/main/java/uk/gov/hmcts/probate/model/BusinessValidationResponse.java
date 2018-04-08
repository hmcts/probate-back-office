package uk.gov.hmcts.probate.model;

import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BusinessValidationResponse implements Serializable {

    private final BusinessValidationStatus status;
    private final List<String> errors;

    public BusinessValidationResponse(final BusinessValidationStatus status) {
        this.status = status;
        this.errors = new ArrayList<>();
    }

    public BusinessValidationResponse(final BusinessValidationStatus status,
                                      final List<FieldError> errors,
                                      final List<BusinessValidationError> businessErrors) {
        this.status = status;
        this.errors = errors.parallelStream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        this.errors.addAll(businessErrors.parallelStream()
            .map(BusinessValidationError::getMsg)
            .collect(Collectors.toList()));
    }

    public BusinessValidationStatus getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }
}
