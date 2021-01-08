package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Service
@Slf4j
@AllArgsConstructor
public class SolicitorExecutorTransformer {

    public void mainApplicantTransformation(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder builder) {
        if (isSolicitorExecutor(caseData)) {
            if (isSolicitorMainApplicant(caseData)) {
                builder
                        .primaryApplicantForenames(caseData.getSolsSOTForenames())
                        .primaryApplicantSurname(caseData.getSolsSOTSurname())
                        .primaryApplicantPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
                        .primaryApplicantEmailAddress(caseData.getSolsSolicitorEmail())
                        .primaryApplicantAddress(caseData.getSolsSolicitorAddress())
                        .primaryApplicantAlias(null)
                        .primaryApplicantHasAlias(NO)
                        .primaryApplicantIsApplying(YES)
                        .solsSolicitorIsApplying(YES)
                        .solsSolicitorNotApplyingReason(null)
                        .solsPrimaryExecutorNotApplyingReason(null);
            } else {
                if (getSolsSOTName(caseData.getSolsSOTForenames(), caseData.getSolsSOTSurname()).equals(caseData.getPrimaryApplicantFullName())) {
                    removeSolicitorAsPrimaryApplicant(builder);
                }

                if (caseData.getSolsSolicitorIsApplying() == null || YES.equals(caseData.getSolsSolicitorIsApplying())) {
                    builder
                            .solsPrimaryExecutorNotApplyingReason(null);
                }
            }
        } else {
            builder
                    .solsSolicitorIsMainApplicant(null)
                    .solsSolicitorIsApplying(null)
                    .solsSolicitorNotApplyingReason(null);
        }

    }

    private void removeSolicitorAsPrimaryApplicant(ResponseCaseData.ResponseCaseDataBuilder builder) {
        builder
                .primaryApplicantForenames(null)
                .primaryApplicantSurname(null)
                .primaryApplicantPhoneNumber(null)
                .primaryApplicantEmailAddress(null)
                .primaryApplicantAddress(null)
                .primaryApplicantAlias(null)
                .primaryApplicantHasAlias(null)
                .primaryApplicantIsApplying(null)
                .solsPrimaryExecutorNotApplyingReason(null);
    }

    private boolean isSolicitorExecutor(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsExec());
    }

    private boolean isSolicitorMainApplicant(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsMainApplicant());
    }

    private String getSolsSOTName(String firstNames, String surname) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstNames);
        sb.append(" " + surname);
        return sb.toString();
    }
}
