package uk.gov.hmcts.probate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.Errors;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends RuntimeException {

    private final List<FieldErrorResponse> errors;

    public BadRequestException(final String message, final Errors errors) {
        super(message);
        this.errors = Optional.ofNullable(errors)
                .map(Errors::getFieldErrors)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(FieldErrorResponse::of)
                .collect(Collectors.toList());
    }
}
