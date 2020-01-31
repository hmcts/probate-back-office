package uk.gov.hmcts.probate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SolicitorExecutorService {

    private static final String SOL_AS_EXEC_ID = "solicitor";


    public List<CollectionMember<AdditionalExecutorApplying>> updateSolicitorApplyingExecutor(
            CaseData caseData, List<CollectionMember<AdditionalExecutorApplying>> execs) {

        List<CollectionMember<AdditionalExecutorApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOL_AS_EXEC_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorAsApplyingExecutor(execs);
            updatedExecs.add(getSolicitorApplyingExecutor(caseData));
        } else {
            updatedExecs.add(getSolicitorApplyingExecutor(caseData));
        }

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> updateSolicitorNotApplyingExecutor(
            CaseData caseData, List<CollectionMember<AdditionalExecutorNotApplying>> execs) {

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOL_AS_EXEC_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorAsNotApplyingExecutor(execs);
            updatedExecs.add(getSolicitorNotApplyingExecutor(caseData));
        } else {
            updatedExecs.add(getSolicitorNotApplyingExecutor(caseData));
        }

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorApplying>> removeSolicitorAsApplyingExecutor(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying) {

        if (execsApplying.isEmpty()) return execsApplying;

        return execsApplying.stream()
                .filter(exec -> !SOL_AS_EXEC_ID.equals(exec.getId()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> removeSolicitorAsNotApplyingExecutor(
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
}

