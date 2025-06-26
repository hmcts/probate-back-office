package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.NOT_APPLICABLE_VALUE;
import static uk.gov.hmcts.reform.probate.model.IhtFormType.Constants.IHT400421_VALUE;


@Component
@RequiredArgsConstructor
public class IHTFormIDValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        CaseData caseData = caseDetails.getData();
        if (caseData.getIhtFormEstateValuesCompleted() == null
                && (NOT_APPLICABLE_VALUE.equalsIgnoreCase(caseData.getIhtFormId())
                || IHT400421_VALUE.equalsIgnoreCase(caseData.getIhtFormId()))
        ) {
            String userMessage = businessValidationMessageRetriever
                    .getMessage("ihtFormIDInvalid", null, Locale.UK);
            String userMessageWelsh = businessValidationMessageRetriever
                    .getMessage("ihtFormIDInvalidWelsh", null, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "IHTFormID is invalid: " + caseDetails.getId(), userMessageWelsh);
        }
    }
}
