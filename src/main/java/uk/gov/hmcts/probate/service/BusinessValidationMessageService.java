package uk.gov.hmcts.probate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class BusinessValidationMessageService {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public FieldErrorResponse generateError(String param, String code) {
        return FieldErrorResponse.builder()
            .param(param)
            .code(code)
            .message(businessValidationMessageRetriever.getMessage(code, null, Locale.UK))
            .build();
    }

    public FieldErrorResponse generateError(String param, String code, String[] args) {
        return FieldErrorResponse.builder()
            .param(param)
            .code(code)
            .message(businessValidationMessageRetriever.getMessage(code, args, Locale.UK))
            .build();
    }
}
