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
public class NoneOfTheseSelectedValidationRule implements TitleAndClearingPageValidationRule {

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;
    private static final String NONE_OF_THESE_SELECTED = "noneOfTheseSelected";

    @Override
    public void validate(CaseDetails caseDetails) {

        var caseData = caseDetails.getData();
        var args = new String[]{caseDetails.getId().toString()};
        var userMessage = businessValidationMessageRetriever.getMessage(NONE_OF_THESE_SELECTED, args, Locale.UK);

        if (NO.equals(caseData.getSolsSolicitorIsExec())
                && YES.equals(caseData.getSolsSolicitorIsApplying())
                && caseData.getTitleAndClearingType().matches("TCTNoT")) {

            throw new BusinessValidationException(userMessage,
                    "None of these selected, you need to make a paper application for case id " + caseDetails.getId());
        }
    }
}