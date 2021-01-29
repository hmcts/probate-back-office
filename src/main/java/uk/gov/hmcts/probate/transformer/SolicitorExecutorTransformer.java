package uk.gov.hmcts.probate.transformer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.response.ResponseCaseData;
import uk.gov.hmcts.probate.service.SolicitorExecutorService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.ANSWER_NO;
import static uk.gov.hmcts.probate.transformer.CallbackResponseTransformer.ANSWER_YES;

@Service
@Slf4j
@AllArgsConstructor
public class SolicitorExecutorTransformer {

    private static final String SOL_AS_EXEC_ID = "solicitor";

    public void mainApplicantTransformation(CaseData caseData, ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
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

    public void populateAdditionalExecutorList(CaseData caseData,
                                               SolicitorExecutorService solicitorExecutorService,
                                               ResponseCaseData.ResponseCaseDataBuilder builder) {

        // Initialise lists
        List<CollectionMember<AdditionalExecutorApplying>> execsApplying = caseData.getAdditionalExecutorsApplying() == null ?
                new ArrayList<>() : mapApplyingAdditionalExecutors(caseData);

        List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying = caseData.getAdditionalExecutorsNotApplying() == null ?
                new ArrayList<>() : caseData.getAdditionalExecutorsNotApplying();

        // Transform lists
        if (YES.equals(caseData.getSolsSolicitorIsExec()) && !isSolicitorMainApplicant(caseData)) {
            if (YES.equals(caseData.getSolsSolicitorIsApplying())) {
                execsApplying = solicitorExecutorService.updateSolicitorApplyingExecutor(caseData, execsApplying);
                execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
            } else if (NO.equals(caseData.getSolsSolicitorIsApplying())) {
                execsNotApplying = solicitorExecutorService.updateSolicitorNotApplyingExecutor(caseData, execsNotApplying);
                execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
            }
        } else if (NO.equals(caseData.getSolsSolicitorIsExec()) || isSolicitorMainApplicant(caseData)) {
            execsApplying = solicitorExecutorService.removeSolicitorAsApplyingExecutor(execsApplying);
            execsNotApplying = solicitorExecutorService.removeSolicitorAsNotApplyingExecutor(execsNotApplying);
        }

        builder
                .additionalExecutorsApplying(execsApplying)
                .additionalExecutorsNotApplying(execsNotApplying);
    }


    public void solicitorExecutorTransformation(CaseData caseData, SolicitorExecutorService solicitorExecutorService,
                     ResponseCaseData.ResponseCaseDataBuilder builder){
        if (CollectionUtils.isEmpty(caseData.getSolsAdditionalExecutorList())) {
            if (YES.equals(caseData.getSolsSolicitorIsExec())) {
                populateAdditionalExecutorList(caseData, solicitorExecutorService, builder);
            } else {
                builder
                        .additionalExecutorsApplying(caseData.getAdditionalExecutorsApplying())
                        .additionalExecutorsNotApplying(caseData.getAdditionalExecutorsNotApplying());
            }
        } else {
            List<CollectionMember<AdditionalExecutorApplying>> applyingExec = new ArrayList<>();
            List<CollectionMember<AdditionalExecutorNotApplying>> notApplyingExec = new ArrayList<>();

            for (CollectionMember<AdditionalExecutor> additionalExec : caseData.getSolsAdditionalExecutorList()) {
                if (ANSWER_YES.equalsIgnoreCase(additionalExec.getValue().getAdditionalApplying())) {
                    applyingExec.add( new CollectionMember<>(additionalExec.getId(), buildApplyingAdditionalExecutor(additionalExec.getValue())));
                } else if (ANSWER_NO.equalsIgnoreCase(additionalExec.getValue().getAdditionalApplying())) {
                    notApplyingExec.add( new CollectionMember<>(additionalExec.getId(), buildNotApplyingAdditionalExecutor(additionalExec.getValue())));
                }
            }

            builder
                    .additionalExecutorsApplying(applyingExec)
                    .additionalExecutorsNotApplying(notApplyingExec)
                    .solsAdditionalExecutorList(EMPTY_LIST);
        }
    }

    private void removeSolicitorAsPrimaryApplicant(ResponseCaseData.ResponseCaseDataBuilder<?, ?> builder) {
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

    public List<CollectionMember<AdditionalExecutor>> mapSolsAdditionalExecutors(CaseData caseData, List<CollectionMember<AdditionalExecutor>> execs,
                                                                                  SolicitorExecutorService solicitorExecutorService) {
        List<CollectionMember<AdditionalExecutor>> updatedExecs = new ArrayList<>();

        if (execs != null && !execs.isEmpty()) {
            updatedExecs.addAll(execs);
        }

        if (updatedExecs.stream().anyMatch(exec -> SOL_AS_EXEC_ID.equalsIgnoreCase(exec.getId()))) {
            return updatedExecs;
        }

        if (YES.equals(caseData.getSolsSolicitorIsExec()) && !isSolicitorMainApplicant(caseData)) {
            if (YES.equals(caseData.getSolsSolicitorIsApplying())) {
                updatedExecs = solicitorExecutorService.addSolicitorApplyingExecutor(caseData, updatedExecs);
            } else if (NO.equals(caseData.getSolsSolicitorIsApplying())) {
                updatedExecs = solicitorExecutorService.addSolicitorNotApplyingExecutor(caseData, updatedExecs);
            }
        }

        return updatedExecs;
    }

    private List<CollectionMember<AdditionalExecutorApplying>> mapApplyingAdditionalExecutors(CaseData caseData) {
        return caseData.getAdditionalExecutorsApplying()
                    .stream()
                    .map(this::buildApplyingAdditionalExecutors)
                    .collect(Collectors.toList());
    }

    private CollectionMember<AdditionalExecutorApplying> buildApplyingAdditionalExecutors(CollectionMember<AdditionalExecutorApplying> additionalExecutorApplying) {
        AdditionalExecutorApplying tempExec = additionalExecutorApplying.getValue();

        if (tempExec.getApplyingExecutorName() == null) {
            additionalExecutorApplying.getValue().setApplyingExecutorName(tempExec.getApplyingExecutorFirstName()
                    + " " + tempExec.getApplyingExecutorLastName());
        }

        return additionalExecutorApplying;
    }

    private AdditionalExecutorApplying buildApplyingAdditionalExecutor(AdditionalExecutor additionalExecutorApplying) {
        return AdditionalExecutorApplying.builder()
                .applyingExecutorName(additionalExecutorApplying.getAdditionalExecForenames()
                        + " " + additionalExecutorApplying.getAdditionalExecLastname())
                .applyingExecutorPhoneNumber(null)
                .applyingExecutorEmail(null)
                .applyingExecutorAddress(additionalExecutorApplying.getAdditionalExecAddress())
                .applyingExecutorOtherNames(additionalExecutorApplying.getAdditionalExecAliasNameOnWill())
                .build();
    }

    private AdditionalExecutorNotApplying buildNotApplyingAdditionalExecutor(AdditionalExecutor additionalExecutorNotApplying) {
        return AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(additionalExecutorNotApplying.getAdditionalExecForenames()
                        + " " + additionalExecutorNotApplying.getAdditionalExecLastname())
                .notApplyingExecutorReason(additionalExecutorNotApplying.getAdditionalExecReasonNotApplying())
                .notApplyingExecutorNameOnWill(additionalExecutorNotApplying.getAdditionalExecAliasNameOnWill())
                .build();
    }

    private boolean isSolicitorExecutor(CaseData caseData) {
        return YES.equals(caseData.getSolsSolicitorIsExec());
    }

    private boolean isSolicitorMainApplicant(CaseData caseData) { return YES.equals(caseData.getSolsSolicitorIsMainApplicant()); }

    private String getSolsSOTName(String firstNames, String surname) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstNames);
        sb.append(" " + surname);
        return sb.toString();
    }
}
