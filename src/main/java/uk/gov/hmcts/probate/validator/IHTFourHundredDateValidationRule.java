package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.time.LocalDate;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class IHTFourHundredDateValidationRule implements IHTFourHundredDateRule {

    public static final String IHT_DATE_IS_INVALID = "iht400DateInvalid";
    public static final String IHT_DATE_IS_IN_FUTURE = "iht400DateIsInFuture";

    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    @Override
    public void validate(CaseDetails caseDetails) {

        LocalDate iht400Date = caseDetails.getData().getSolsIHT400Date();
        LocalDate twentyDaysBeforeToday = LocalDate.now().minusDays(20);
        String[] args = {caseDetails.getData().convertDate(twentyDaysBeforeToday)};

        String userMessage;

        if (!iht400Date.isAfter(twentyDaysBeforeToday)) {
            userMessage = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_INVALID, args, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "Case ID " + caseDetails.getId() + ": IHT400421 date (" + iht400Date + ") needs to be before 20 days before current date (" + twentyDaysBeforeToday + ")");
        }

        if (iht400Date.isAfter(LocalDate.now())) {
            userMessage = businessValidationMessageRetriever.getMessage(IHT_DATE_IS_IN_FUTURE, args, Locale.UK);
            throw new BusinessValidationException(userMessage,
                    "Case ID " + caseDetails.getId() + ": IHT400421 date (" + iht400Date + ") needs to be in the past");
        }
    }
}
