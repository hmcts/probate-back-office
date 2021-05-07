package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Component
@RequiredArgsConstructor
public class PartnersAllRenouncingValidator implements TitleAndClearingPageValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String ALL_RENOUNCING = "allRenouncing";

    @Override
    public void validate(CaseDetails caseDetails) {

        var caseData = caseDetails.getData();
        var args = new String[]{caseDetails.getId().toString()};
        String userMessage = businessValidationMessageRetriever.getMessage(ALL_RENOUNCING, args, Locale.UK);

        if (NO.equals(caseData.getSolsSolicitorIsExec())
            && YES.equals(caseData.getSolsSolicitorIsApplying())
            && (caseData.getTitleAndClearingType().matches("TCTPartSuccAllRenouncing")
            || caseData.getTitleAndClearingType().matches("TCTPartAllRenouncing"))) {

            throw new BusinessValidationException(userMessage,
                "Probate practitioner cannot be applying if part of a group which is "
                    + "all renouncing for case id " + caseDetails.getId());
        }
    }
}
