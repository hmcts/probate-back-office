package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.Locale;

import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.NO;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZeroApplyingExecutorsValidationRule {

    private static final String NO_EXECUTORS = "zeroExecutors";
    private static final String NO_EXECUTORS_WELSH = "zeroExecutorsWelsh";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        CaseData caseData = caseDetails.getData();
        String userMessage = businessValidationMessageRetriever.getMessage(NO_EXECUTORS, args, Locale.UK);
        String userMessageWelsh = businessValidationMessageRetriever.getMessage(NO_EXECUTORS_WELSH, args, Locale.UK);

        //!YES because caseField getters can return null as journey may not have reached there
        if (null != caseData.getOtherExecutorExists()
                && null != caseData.getOtherPartnersApplyingAsExecutors()
                && null != caseData.getAdditionalExecutorsTrustCorpList()
                && !YES.equals(caseData.getPrimaryApplicantIsApplying())
                && caseData.getOtherPartnersApplyingAsExecutors().isEmpty()
                && caseData.getAdditionalExecutorsTrustCorpList().isEmpty()
                && !YES.equals(caseData.getSolsSolicitorIsExec())
                && !YES.equals(caseData.getSolsSolicitorIsApplying())
                && NO.equals(caseData.getOtherExecutorExists())
                && (YES.equals(caseData.getAppointExec()) || YES.equals(caseData.getAppointExecNo()))) {
            throw new BusinessValidationException(userMessage,
                "There must be at least one executor applying for case id "
                        + caseDetails.getId(), userMessageWelsh);
        }
    }
}
