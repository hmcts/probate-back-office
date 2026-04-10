package uk.gov.hmcts.probate.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.exception.BusinessValidationException;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseDetails;
import uk.gov.hmcts.probate.service.BusinessValidationMessageRetriever;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class AttorneyAppointedExecutorValidationRule {

    private static final String ATTORNEY_APPOINTED_EXECUTOR = "AttorneyAppointedExec";
    private static final String ATTORNEY_APPOINTED_EXECUTOR_WELSH = "AttorneyAppointedExecWelsh";
    private static final String POWER_OF_ATTORNEY = "PowerOfAttorney";
    private static final String ATTORNEY_APPOINTED_ERROR_MESSAGE =
            "Cannot have an executor and then appoint another as an attorney for case id ";
    private final BusinessValidationMessageRetriever businessValidationMessageRetriever;

    public void validate(CaseDetails caseDetails) {
        String[] args = {caseDetails.getId().toString()};
        CaseData caseData = caseDetails.getData();
        String userMessage = businessValidationMessageRetriever.getMessage(
                ATTORNEY_APPOINTED_EXECUTOR, args, Locale.UK);
        String userMessageWelsh = businessValidationMessageRetriever.getMessage(
                ATTORNEY_APPOINTED_EXECUTOR_WELSH, args, Locale.UK);

        if ("YES".equals(caseData.getAppointExec())
                && hasPowerOfAttorneyReason(caseData.getAdditionalExecutorsNotApplying())) {
            throw new BusinessValidationException(userMessage, ATTORNEY_APPOINTED_ERROR_MESSAGE + caseDetails.getId(),
                    userMessageWelsh);
        }
    }

    private boolean hasPowerOfAttorneyReason(List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying) {

        if (!CollectionUtils.isEmpty(execsNotApplying)) {
            for (CollectionMember<AdditionalExecutorNotApplying> eachExecNotApplying : execsNotApplying) {
                if (eachExecNotApplying != null && eachExecNotApplying.getValue() != null) {
                    AdditionalExecutorNotApplying exec = eachExecNotApplying.getValue();
                    if (POWER_OF_ATTORNEY.equalsIgnoreCase(exec.getNotApplyingExecutorReason())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
