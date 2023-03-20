package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class ChangeToSameStateValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String CHANGE_TO_SAME_STATE = "changeToSameState";

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (caseData.getTransferToState().equals(caseDetails.getState())) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage(CHANGE_TO_SAME_STATE, null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "The change case state cannot be the same: " + caseDetails.getId());
        }
    }
}
