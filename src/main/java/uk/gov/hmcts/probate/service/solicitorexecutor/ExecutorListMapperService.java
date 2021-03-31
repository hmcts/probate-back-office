package uk.gov.hmcts.probate.service.solicitorexecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_NAMED;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_PROFESSIONAL;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_NOT_APPLYING_REASON;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.YES;

@Slf4j
@Service
public class ExecutorListMapperService {

    private static final String SOLICITOR_ID = "solicitor";

    public List<CollectionMember<AdditionalExecutorApplying>> addSolicitorToApplyingList(
            CaseData caseData, List<CollectionMember<AdditionalExecutorApplying>> execs) {

        List<CollectionMember<AdditionalExecutorApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOLICITOR_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorFromApplyingList(execs);
        }
        updatedExecs.add(mapFromSolicitorToApplyingExecutor(caseData));

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> addSolicitorToNotApplyingList(
            CaseData caseData, List<CollectionMember<AdditionalExecutorNotApplying>> execs) {

        List<CollectionMember<AdditionalExecutorNotApplying>> updatedExecs = new ArrayList<>();

        if (execs.stream().anyMatch(exec -> !SOLICITOR_ID.equals(exec.getId()))) {
            updatedExecs = removeSolicitorFromNotApplyingList(execs);
        }
        updatedExecs.add(mapFromSolicitorToNotApplyingExecutor(caseData));

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorApplying>> removeSolicitorFromApplyingList(
            List<CollectionMember<AdditionalExecutorApplying>> execsApplying) {

        if (execsApplying.isEmpty()) {
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

    private CollectionMember<AdditionalExecutorNotApplying> mapFromSolicitorToNotApplyingExecutor(CaseData caseData) {
        AdditionalExecutorNotApplying exec = AdditionalExecutorNotApplying.builder()
            .notApplyingExecutorName(FormattingService.capitaliseEachWord(caseData.getSolsSOTForenames()
                    + " " + caseData.getSolsSOTSurname()))
            .notApplyingExecutorReason(caseData.getSolsSolicitorNotApplyingReason())
            .build();

        return new CollectionMember<>(SOLICITOR_ID, exec);
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapAdditionalApplyingExecutors(CaseData caseData) {

        List<CollectionMember<AdditionalExecutorApplying>> tempList =
                new ArrayList<>(caseData.getAdditionalExecutorsApplying());
        // Update list
        tempList.forEach(exec -> exec.getValue().setApplyingExecutorName(FormattingService.capitaliseEachWord(
                    exec.getValue().getApplyingExecutorFirstName()
                            + " " + exec.getValue().getApplyingExecutorLastName())));
        // Return list
        return tempList;
    }

    public CollectionMember<AdditionalExecutorApplying> mapFromSolicitorToApplyingExecutor(
            CaseData caseData) {
        // Create applying executor collection member containing primary applicant names
        return new CollectionMember<>(SOLICITOR_ID, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(FormattingService.capitaliseEachWord(caseData.getSolsSOTForenames()))
                .applyingExecutorLastName(FormattingService.capitaliseEachWord(caseData.getSolsSOTSurname()))
                .applyingExecutorName(FormattingService.capitaliseEachWord(caseData.getSolsSOTForenames()
                        + " " + caseData.getSolsSOTSurname()))
                .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                .applyingExecutorAddress(caseData.getSolsSolicitorAddress())
                .build());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromTrustCorpExecutorsToApplyingExecutors(
            CaseData caseData) {
        return caseData.getAdditionalExecutorsTrustCorpList()
                .stream()
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorApplying.builder()
                        .applyingExecutorAddress(caseData.getTrustCorpAddress())
                        .applyingExecutorFirstName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecForenames()))
                        .applyingExecutorLastName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecLastname()))
                        .applyingExecutorName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecForenames()
                                        + " " + exec.getValue().getAdditionalExecLastname()))
                        .applyingExecutorType(EXECUTOR_TYPE_TRUST_CORP)
                        .applyingExecutorTrustCorpPosition(exec.getValue().getAdditionalExecutorTrustCorpPosition())
                        .build()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromPartnerExecutorsToApplyingExecutors(
            CaseData caseData) {
        return caseData.getOtherPartnersApplyingAsExecutors()
                .stream()
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorApplying.builder()
                        .applyingExecutorAddress(exec.getValue().getAdditionalExecAddress())
                        .applyingExecutorFirstName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecForenames()))
                        .applyingExecutorLastName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecLastname()))
                        .applyingExecutorType(EXECUTOR_TYPE_PROFESSIONAL)
                        .applyingExecutorName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecForenames()
                                + " " + exec.getValue().getAdditionalExecLastname()))
                        .build()))
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
            CaseData caseData) {
        return caseData.getDispenseWithNoticeOtherExecsList()
                .stream()
                .map(exec -> new CollectionMember<>(exec.getId(), AdditionalExecutorNotApplying.builder()
                        .notApplyingExecutorName(FormattingService.capitaliseEachWord(
                                exec.getValue().getNotApplyingExecutorName()))
                        .notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_REASON)
                        .notApplyingExecutorDispenseWithNotice(caseData.getDispenseWithNotice())
                        .notApplyingExecutorDispenseWithNoticeLeaveGiven(caseData.getDispenseWithNoticeLeaveGiven())
                        .notApplyingExecutorDispenseWithNoticeLeaveGivenDate(
                                caseData.getDispenseWithNoticeLeaveGivenDate())
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
                        .applyingExecutorFirstName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecForenames()))
                        .applyingExecutorLastName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecLastname()))
                        .applyingExecutorName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecForenames()
                                + " " + exec.getValue().getAdditionalExecLastname()))
                        .applyingExecutorType(EXECUTOR_TYPE_NAMED)
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
                        .notApplyingExecutorName(FormattingService.capitaliseEachWord(
                                exec.getValue().getAdditionalExecForenames()
                                + " " + exec.getValue().getAdditionalExecLastname()))
                        .notApplyingExecutorReason(exec.getValue().getAdditionalExecReasonNotApplying())
                        .notApplyingExecutorNameOnWill(exec.getValue().getAdditionalExecAliasNameOnWill())
                        .build()))
                .collect(Collectors.toList());
    }

    public CollectionMember<AdditionalExecutorApplying> mapFromPrimaryApplicantToApplyingExecutor(
            CaseData caseData) {
        // Create applying executor collection member containing primary applicant names
        return new CollectionMember<>(null, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(FormattingService.capitaliseEachWord(
                        caseData.getPrimaryApplicantForenames()))
                .applyingExecutorLastName(FormattingService.capitaliseEachWord(caseData.getPrimaryApplicantSurname()))
                .applyingExecutorName(FormattingService.capitaliseEachWord(caseData.getPrimaryApplicantFullName()))
                .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                .applyingExecutorAddress(caseData.getPrimaryApplicantAddress())
                .applyingExecutorOtherNames(caseData.getSolsExecutorAliasNames())
                .applyingExecutorOtherNamesReason(caseData.getPrimaryApplicantAliasReason())
                .build());
    }

    public CollectionMember<AdditionalExecutorNotApplying> mapFromPrimaryApplicantToNotApplyingExecutor(
            CaseData caseData) {
        // Create not applying executor collection member containing primary applicant names
        return new CollectionMember<>(null, AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(FormattingService.capitaliseEachWord(caseData.getPrimaryApplicantFullName()))
                .notApplyingExecutorReason(caseData.getSolsPrimaryExecutorNotApplyingReason())
                .notApplyingExecutorNameOnWill(caseData.getSolsExecutorAliasNames())
                .build());
    }

}
