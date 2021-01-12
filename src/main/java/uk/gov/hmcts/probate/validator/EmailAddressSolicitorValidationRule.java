package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.exception.model.FieldErrorResponse;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;
import uk.gov.hmcts.probate.service.BusinessValidationMessageService;

import java.util.*;

import static uk.gov.hmcts.probate.model.Constants.BUSINESS_ERROR;

@Component
@RequiredArgsConstructor
public class EmailAddressSolicitorValidationRule implements CaseDetailsEmailValidationRule {
    private static final String EMAIL_NOT_FOUND_PA = "solsSolicitorEmailInvalid";
    private final BusinessValidationMessageService businessValidationMessageService;
    private static final String REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]{1,30}(?:\\.[^.\\n]{1,30}){0,30}@[a-z0-9](?:[a-z0-9-.]{0,30}[a-z0-9])?\\.[a-z0-9](?:[a-z0-9-]{0,10}[a-z0-9])?";

    @Override
    public List<FieldErrorResponse> validate(CaseDetails caseDetails) {
        Set<FieldErrorResponse> errors = new HashSet<>();

        if(caseDetails.getData().getApplicationType() != null){
            if (caseDetails.getData().getApplicationType().getCode().matches("sol")
                    && caseDetails.getData().getSolsSolicitorEmail() != null) {
                if(!caseDetails.getData().getSolsSolicitorEmail().matches(REGEX)){
                    errors.add(businessValidationMessageService.generateError(BUSINESS_ERROR, EMAIL_NOT_FOUND_PA));
                }
            }
        }
        return new ArrayList<>(errors);
    }
}
