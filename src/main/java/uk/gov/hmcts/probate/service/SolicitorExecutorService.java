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

import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Component
public class SolicitorExecutorService {

    private static final String SOLICITOR_ID = "solicitor";

    public List<CollectionMember<AdditionalExecutorNotApplying>> addSolicitorToNotApplyingList(
            CaseData caseData, List<CollectionMember<AdditionalExecutorNotApplying>> execs) {

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOLICITOR_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorFromNotApplyingList(execs);
        }
        updatedExecs.add(getSolicitorNotApplyingExecutor(caseData));

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorApplying>> removeSolicitorFromApplyingList(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying) {

        if (execsApplying.isEmpty())  {
            return execsApplying;
        }

        return execsApplying.stream()
            .filter(exec -> !SOLICITOR_ID.equals(exec.getId()))
            .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> removeSolicitorFromNotApplyingList(
            List<CollectionMember<AdditionalExecutorNotApplying>> execsNotApplying) {

        if (execsNotApplying.isEmpty()) {
            return execsNotApplying;
        }

        return execsNotApplying.stream()
            .filter(exec -> !SOLICITOR_ID.equals(exec.getId()))
            .collect(Collectors.toList());
    }

    private CollectionMember<AdditionalExecutorNotApplying> getSolicitorNotApplyingExecutor(CaseData caseData) {
        AdditionalExecutorNotApplying exec = AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName(caseData.getSolsSOTForenames() + " " + caseData.getSolsSOTSurname())
            .notApplyingExecutorReason(caseData.getSolsSolicitorNotApplyingReason())
            .build();

        return new CollectionMember<>(SOLICITOR_ID, exec);
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapApplyingAdditionalExecutors(CaseData caseData) {
        // Update list
        caseData.getAdditionalExecutorsApplying()
                .forEach(exec -> exec.getValue().setApplyingExecutorName(exec.getValue().getApplyingExecutorFirstName()
                        + " " + exec.getValue().getApplyingExecutorLastName()));

        // Return list
        return caseData.getAdditionalExecutorsApplying();
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromTrustCorpExecutorsToApplyingExecutors(
            CaseData caseData) {
        return caseData.getAdditionalExecutorsTrustCorpList()
                .stream()
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorApplying.builder()
                        .applyingExecutorAddress(caseData.getTrustCorpAddress())
                        .applyingExecutorFirstName(exec.getValue().getAdditionalExecForenames())
                        .applyingExecutorLastName(exec.getValue().getAdditionalExecLastname())
                        .applyingExecutorName(exec.getValue().getAdditionalExecForenames()
                                + " " + exec.getValue().getAdditionalExecLastname())
                        .build()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromPartnerExecutorsToApplyingExecutors(
            CaseData caseData) {
        return caseData.getOtherPartnersApplyingAsExecutors()
                .stream()
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorApplying.builder()
                        .applyingExecutorAddress(exec.getValue().getAdditionalExecAddress())
                        .applyingExecutorFirstName(exec.getValue().getAdditionalExecForenames())
                        .applyingExecutorLastName(exec.getValue().getAdditionalExecLastname())
                        .applyingExecutorName(exec.getValue().getAdditionalExecForenames()
                                + " " + exec.getValue().getAdditionalExecLastname())
                        .build()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
            CaseData caseData) {
        return caseData.getDispenseWithNoticeOtherExecsList()
                .stream()
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorNotApplying.builder()
                        .notApplyingExecutorName(exec.getValue().getNotApplyingExecutorName())
                        .notApplyingExecutorReason("PowerReserved")
                        .build()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromSolsAdditionalExecutorListToApplyingExecutors(
            CaseData caseData) {
        return caseData.getSolsAdditionalExecutorList()
                .stream()
                .filter(exec -> exec.getValue().getAdditionalApplying().equals(YES))
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorApplying.builder()
                        .applyingExecutorAddress(exec.getValue().getAdditionalExecAddress())
                        .applyingExecutorFirstName(exec.getValue().getAdditionalExecForenames())
                        .applyingExecutorLastName(exec.getValue().getAdditionalExecLastname())
                        .applyingExecutorName(exec.getValue().getAdditionalExecForenames()
                                + " " + exec.getValue().getAdditionalExecLastname())
                        .applyingExecutorOtherNames(exec.getValue().getAdditionalExecAliasNameOnWill())
                        .build()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> mapFromSolsAdditionalExecsToNotApplyingExecutors(
            CaseData caseData) {
        return caseData.getSolsAdditionalExecutorList()
                .stream()
                .filter(exec -> exec.getValue().getAdditionalApplying().equals(NO))
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorNotApplying.builder()
                        .notApplyingExecutorName(exec.getValue().getAdditionalExecForenames()
                                + " " + exec.getValue().getAdditionalExecLastname())
                        .notApplyingExecutorReason(exec.getValue().getAdditionalExecReasonNotApplying())
                        .notApplyingExecutorNameOnWill(exec.getValue().getAdditionalExecAliasNameOnWill())
                        .build()))
                .collect(Collectors.toList());
    }

}
