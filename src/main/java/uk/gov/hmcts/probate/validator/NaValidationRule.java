package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;


@Component
@RequiredArgsConstructor
public class NaValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (YES.equalsIgnoreCase(caseData.getIhtFormEstateValuesCompleted())
                && NOT_APPLICABLE_VALUE.equalsIgnoreCase(caseData.getIhtFormEstate())) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage("ihtFormEstateNa", null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "NA selection is invalid: " + caseDetails.getId());
        }
    }
}
