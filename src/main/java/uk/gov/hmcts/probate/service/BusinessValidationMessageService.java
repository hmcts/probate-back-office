package uk.gov.hmcts.probate.service;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.BusinessValidationError;

import javax.annotation.Resource;
import java.util.Locale;

@Component
public class BusinessValidationMessageService {

    @Resource
    private BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public BusinessValidationError generateError(String param, String code) {
        return new BusinessValidationError(param, code, getMessageFromBundle(code));
    }

    public BusinessValidationError generateError(String param, String code, String[] args) {
        return new BusinessValidationError(param, code, getMessageFromBundle(code, args));
    }

    private String getMessageFromBundle(String code) {
        return businessValidationMessageRetriever.getMessage(code, null, Locale.UK);
    }

    private String getMessageFromBundle(String code, String[] args) {
        return businessValidationMessageRetriever.getMessage(code, args, Locale.UK);
    }

}
