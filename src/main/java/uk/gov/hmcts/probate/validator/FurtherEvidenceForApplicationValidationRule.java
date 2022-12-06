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
public class FurtherEvidenceForApplicationValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String EMPTY_FURTHER_EVIDENCE = "emptyFurtherEvidence";

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (caseData.getFurtherEvidenceForApplication() == null
                || caseData.getFurtherEvidenceForApplication().trim().isBlank()) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage(EMPTY_FURTHER_EVIDENCE, null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "The further evidence for application cannot be empty " + caseDetails.getId());
        }
    }
}
