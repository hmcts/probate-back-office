package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttorneyAppointedExecutorValidationRule {

    private static final String ATTORNEY_APPOINTED_EXECUTOR = "AttorneyAppointedExec";
    private static final String ATTORNEY_APPOINTED_EXECUTOR_WELSH = "AttorneyAppointedExecWelsh";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        CaseData caseData = caseDetails.getData();
        String userMessage = businessValidationMessageRetriever.getMessage(
                ATTORNEY_APPOINTED_EXECUTOR, args, Locale.UK);
        String userMessageWelsh = businessValidationMessageRetriever.getMessage(
                ATTORNEY_APPOINTED_EXECUTOR_WELSH, args, Locale.UK);

        if (caseData.getApplyingAsAnAttorney() == "YES" && !caseData.getAdditionalExecutorsApplying().isEmpty()) {
            throw new BusinessValidationException(userMessage,
                "There cannot be an attorney appointed and executors for case id " + caseDetails.getId(),
                    userMessageWelsh);
        }
    }
}
