package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutor;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
public class SolicitorExecutorService {

    private static final String SOL_AS_EXEC_ID = "solicitor";


    public List<CollectionMember<AdditionalExecutorApplying>> updateSolicitorApplyingExecutor(
            CaseData caseData, List<CollectionMember<AdditionalExecutorApplying>> execs) {

        List<CollectionMember<AdditionalExecutorApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOL_AS_EXEC_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorFromApplyingList(execs);
        }
        updatedExecs.add(getSolicitorApplyingExecutor(caseData));


        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> addSolicitorToNotApplyingList(
            CaseData caseData, List<CollectionMember<AdditionalExecutorNotApplying>> execs) {

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOL_AS_EXEC_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorFromNotApplyingList(execs);
        }
        updatedExecs.add(getSolicitorNotApplyingExecutor(caseData));

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorApplying>> removeSolicitorFromApplyingList(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying) {

        if (execsApplying.isEmpty()) return execsApplying;

        return execsApplying.stream()
                .filter(exec -> !SOL_AS_EXEC_ID.equals(exec.getId()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> removeSolicitorFromNotApplyingList(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying) {

        if (execsNotApplying.isEmpty()) return execsNotApplying;

        return execsNotApplying.stream()
                .filter(exec -> !SOL_AS_EXEC_ID.equals(exec.getId()))
                .collect(Collectors.toList());
    }

    private CollectionMember<AdditionalExecutorApplying> getSolicitorApplyingExecutor(CaseData caseData) {
        AdditionalExecutorApplying exec = AdditionalExecutorApplying.builder()
                .applyingExecutorName(caseData.getSolsSOTForenames() + " " + caseData.getSolsSOTSurname())
                .applyingExecutorPhoneNumber(caseData.getSolsSolicitorPhoneNumber())
                .applyingExecutorEmail(caseData.getSolsSolicitorEmail())
                .applyingExecutorAddress(caseData.getSolsSolicitorAddress())
                .build();

        return new CollectionMember<>(SOL_AS_EXEC_ID, exec);
    }

    private CollectionMember<AdditionalExecutorNotApplying> getSolicitorNotApplyingExecutor(CaseData caseData) {
        AdditionalExecutorNotApplying exec = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(caseData.getSolsSOTForenames() + " " + caseData.getSolsSOTSurname())
                .notApplyingExecutorReason(caseData.getSolsSolicitorNotApplyingReason())
                .build();

        return new CollectionMember<>(SOL_AS_EXEC_ID, exec);
    }

    public List<CollectionMember<AdditionalExecutor>> addSolicitorApplyingExecutor(CaseData caseData, List<CollectionMember<AdditionalExecutor>> execs) {

        // Initialise list
        List<CollectionMember<AdditionalExecutor>> updatedExecs = new ArrayList<>();
        if (execs != null && !execs.isEmpty()) updatedExecs.addAll(execs);

        AdditionalExecutor solicitor = AdditionalExecutor.builder()
                .additionalExecForenames(caseData.getSolsSOTForenames())
                .additionalExecLastname(caseData.getSolsSOTSurname())
                .additionalExecNameOnWill(NO)
                .additionalApplying(YES)
                .additionalExecAddress(caseData.getSolsSolicitorAddress())
                .build();

        CollectionMember<AdditionalExecutor> updatedExec = new CollectionMember<>(SOL_AS_EXEC_ID, solicitor);
        updatedExecs.add(updatedExec);

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutor>> addSolicitorNotApplyingExecutor(CaseData caseData, List<CollectionMember<AdditionalExecutor>> execs) {
        List<CollectionMember<AdditionalExecutor>> updatedExecs = new ArrayList<>();

        if (execs != null && !execs.isEmpty()) {
            updatedExecs.addAll(execs);
        }

        AdditionalExecutor solicitor = AdditionalExecutor.builder()
                .additionalExecForenames(caseData.getSolsSOTForenames())
                .additionalExecLastname(caseData.getSolsSOTSurname())
                .additionalExecNameOnWill(NO)
                .additionalApplying(NO)
                .additionalExecReasonNotApplying(caseData.getSolsSolicitorNotApplyingReason())
                .build();

        CollectionMember<AdditionalExecutor> updatedExec = new CollectionMember<>(SOL_AS_EXEC_ID, solicitor);
        updatedExecs.add(updatedExec);

        return updatedExecs;
    }
}

