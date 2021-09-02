package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.EMPTY_STRING;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.getTrustCorpTitleClearingTypes;

@Component
@RequiredArgsConstructor
public class PractitionersPositionInTrustValidationRule implements TitleAndClearingPageValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String POS_TRUST_NEEDED = "practitionerPosTrustNeeded";

    @Override
    public void validate(CaseDetails caseDetails) {

        final var caseData = caseDetails.getData();
        final var posInTrust = caseData.getProbatePractitionersPositionInTrust();

        if (NO.equals(caseData.getSolsSolicitorIsExec())
            && YES.equals(caseData.getSolsSolicitorIsApplying())
            && (posInTrust == null || posInTrust.equals(EMPTY_STRING))
            && getTrustCorpTitleClearingTypes().contains(caseData.getTitleAndClearingType())) {

            final String userMessage = businessValidationMessageRetriever.getMessage(POS_TRUST_NEEDED, null, Locale.UK);

            throw new BusinessValidationException(userMessage,
                    "Position in Trust must be specified for question probatePractitionersPositionInTrust for case id "
                            + caseDetails.getId() + " if practitioner is not named in the will and is applying.");
        }
    }
}

