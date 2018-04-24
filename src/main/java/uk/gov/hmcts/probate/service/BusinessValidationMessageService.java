package uk.gov.hmcts.probate.service;

import lombok.Data;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.Locale;

@Data
@Component
public class BusinessValidationMessageService {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public FieldErrorResponse generateError(String param, String code) {
        return FieldErrorResponse.builder()
            .param(param)
            .code(code)
            .message(businessValidationMessageRetriever.getMessage(code, null, Locale.UK))
            .build();
    }
}
