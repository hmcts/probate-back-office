package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.DocumentCaseType;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;


@Component
@RequiredArgsConstructor
public class AdColligendaBonaCaseTypeValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (DocumentCaseType.AD_COLLIGENDA_BONA.getCaseType().equals(caseData.getCaseType())) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage("invalidCaseTypeSelection", null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "Ad Colligenda Bona selection is invalid: " + caseDetails.getId());
        }
    }
}
