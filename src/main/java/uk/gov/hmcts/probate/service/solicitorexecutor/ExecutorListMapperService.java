package uk.gov.hmcts.probate.service.solicitorexecutor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorApplying;
import uk.gov.hmcts.probate.model.ccd.raw.AdditionalExecutorNotApplying;
import uk.gov.hmcts.probate.model.ccd.raw.CollectionMember;
import uk.gov.hmcts.probate.model.ccd.raw.request.CaseData;
import uk.gov.hmcts.probate.model.ccd.raw.ApplicantFamilyDetails;
import uk.gov.hmcts.probate.model.ccd.raw.SolsApplicantFamilyDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_NOT_APPLYING_REASON;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_NAMED;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_PROFESSIONAL;
import static uk.gov.hmcts.probate.model.Constants.EXECUTOR_TYPE_TRUST_CORP;
import static uk.gov.hmcts.probate.model.Constants.NO;
import static uk.gov.hmcts.probate.model.Constants.SOLICITOR_ID;
import static uk.gov.hmcts.probate.model.Constants.YES;
import static uk.gov.hmcts.probate.model.Constants.getNonTrustPtnrTitleClearingTypes;
import static uk.gov.hmcts.probate.model.Constants.getTrustCorpTitleClearingTypes;

@Slf4j
@Service
public class ExecutorListMapperService {

    public List<CollectionMember<AdditionalExecutorApplying>> addSolicitorToApplyingList(
            CaseData caseData, List<CollectionMember<AdditionalExecutorApplying>> execs) {

        var updatedExecs = removeSolicitorFromApplyingList(execs);
        updatedExecs.add(0, mapFromSolicitorToApplyingExecutor(caseData));

        return updatedExecs;
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> addSolicitorToNotApplyingList(
            CaseData caseData, List<CollectionMember<AdditionalExecutorNotApplying>> execs) {

        var updatedExecs = removeSolicitorFromNotApplyingList(execs);
        updatedExecs.add(0, mapFromSolicitorToNotApplyingExecutor(caseData));

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
        final String capSolSotName = FormattingService.capitaliseEachWord(
                caseData.getSolsSOTForenames() + " " + caseData.getSolsSOTSurname(),
                "Not applying Solicitor - Statement of Truth Name");
        AdditionalExecutorNotApplying exec = AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(capSolSotName)
                .notApplyingExecutorReason(caseData.getSolsSolicitorNotApplyingReason())
                .build();

        return new CollectionMember<>(SOLICITOR_ID, exec);
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapAdditionalApplyingExecutors(CaseData caseData) {

        List<CollectionMember<AdditionalExecutorApplying>> tempList =
                new ArrayList<>(caseData.getAdditionalExecutorsApplying());
        // Update list
        tempList.forEach(exec -> {
            final String capApplExecName = FormattingService.capitaliseEachWord(
                    exec.getValue().getApplyingExecutorFirstName()
                            + " " + exec.getValue().getApplyingExecutorLastName(),
                    "Additional applying executor"
            );
            exec.getValue().setApplyingExecutorName(capApplExecName);
        });
        // Return list
        return tempList;
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromTrustCorpExecutorsToApplyingExecutors(
            CaseData caseData) {
        return caseData.getAdditionalExecutorsTrustCorpList()
                .stream()
                .map(exec -> {
                    final String applExecFNames = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecForenames(),
                            "Applying executor forenames");
                    final String applExecLName = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecLastname(),
                            "Applying executor last name");
                    final String applExecName = applExecFNames + " " + applExecLName;
                    final String applExecTCPosition = exec.getValue().getAdditionalExecutorTrustCorpPosition();

                    return new CollectionMember<>(
                            exec.getId(),
                            AdditionalExecutorApplying.builder()
                                    .applyingExecutorAddress(caseData.getTrustCorpAddress())
                                    .applyingExecutorFirstName(applExecFNames)
                                    .applyingExecutorLastName(applExecLName)
                                    .applyingExecutorName(applExecName)
                                    .applyingExecutorType(EXECUTOR_TYPE_TRUST_CORP)
                                    .applyingExecutorTrustCorpPosition(applExecTCPosition)
                                    .build());
                })
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromPartnerExecutorsToApplyingExecutors(
            CaseData caseData) {
        return caseData.getOtherPartnersApplyingAsExecutors()
                .stream()
                .map(exec -> {
                    final String applExecFNames = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecForenames(),
                            "Additional Partner Executor forenames");
                    final String applExecLName = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecLastname(),
                            "Additional Partner Executor last name");
                    final String applExecName = applExecFNames + " " + applExecLName;
                    return new CollectionMember<>(
                            exec.getId(),
                            AdditionalExecutorApplying.builder()
                                    .applyingExecutorAddress(exec.getValue().getAdditionalExecAddress())
                                    .applyingExecutorFirstName(applExecFNames)
                                    .applyingExecutorLastName(applExecLName)
                                    .applyingExecutorType(EXECUTOR_TYPE_PROFESSIONAL)
                                    .applyingExecutorName(applExecName)
                                    .build());
                })
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> mapFromDispenseWithNoticeExecsToNotApplyingExecutors(
            CaseData caseData) {
        return caseData.getDispenseWithNoticeOtherExecsList()
                .stream()
                .map(exec -> {
                    final String notApplExecName = FormattingService.capitaliseEachWord(
                            exec.getValue().getNotApplyingExecutorName(),
                            "Not Applying Executor Name");
                    final String dispWNoticeLeaveGiven = caseData.getDispenseWithNoticeLeaveGiven();
                    final LocalDate dispWNoticeLeaveGivenDate = caseData.getDispenseWithNoticeLeaveGivenDate();

                    return new CollectionMember<>(
                            exec.getId(),
                            AdditionalExecutorNotApplying.builder()
                                    .notApplyingExecutorName(notApplExecName)
                                    .notApplyingExecutorReason(EXECUTOR_NOT_APPLYING_REASON)
                                    .notApplyingExecutorDispenseWithNotice(caseData.getDispenseWithNotice())
                                    .notApplyingExecutorDispenseWithNoticeLeaveGiven(dispWNoticeLeaveGiven)
                                    .notApplyingExecutorDispenseWithNoticeLeaveGivenDate(dispWNoticeLeaveGivenDate)
                                    .build());
                })
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromSolsIntestacyExecutorListToApplyingExecutors(
            CaseData caseData) {
        return caseData.getSolsIntestacyExecutorList()
                .stream()
                .map(exec -> {
                    final String applExecFNames = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecForenames(),
                            "additional executor forenames");
                    final String applExecLName = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecLastname(),
                            "additional executor last name");
                    final String applExecName = applExecFNames + " " + applExecLName;
                    final SolsApplicantFamilyDetails solsApplicantFamilyDetails =
                            exec.getValue().getSolsApplicantFamilyDetails();
                    final String selectedRelationship = solsApplicantFamilyDetails.getRelationship().getValue()
                            .getCode();
                    ApplicantFamilyDetails applicantFamilyDetails = ApplicantFamilyDetails.builder()
                            .relationshipToDeceased(selectedRelationship)
                            .childAdoptedIn(solsApplicantFamilyDetails.getChildAdoptedIn())
                            .childAdoptionInEnglandOrWales(solsApplicantFamilyDetails
                                    .getChildAdoptionInEnglandOrWales())
                            .childAdoptedOut(solsApplicantFamilyDetails.getChildAdoptedOut())
                            .childDieBeforeDeceased(solsApplicantFamilyDetails.getChildDieBeforeDeceased())
                            .grandchildParentAdoptedIn(solsApplicantFamilyDetails.getGrandchildParentAdoptedIn())
                            .grandchildParentAdoptionInEnglandOrWales(solsApplicantFamilyDetails
                                    .getGrandchildParentAdoptionInEnglandOrWales())
                            .grandchildParentAdoptedOut(solsApplicantFamilyDetails.getGrandchildParentAdoptedOut())
                            .grandchildAdoptedIn(solsApplicantFamilyDetails.getGrandchildAdoptedIn())
                            .grandchildAdoptionInEnglandOrWales(solsApplicantFamilyDetails
                                    .getGrandchildAdoptionInEnglandOrWales())
                            .grandchildAdoptedOut(solsApplicantFamilyDetails.getGrandchildAdoptedOut())
                            .build();
                    return new CollectionMember<>(
                            exec.getId(),
                            AdditionalExecutorApplying.builder()
                                    .applyingExecutorAddress(exec.getValue().getAdditionalExecAddress())
                                    .applyingExecutorFirstName(applExecFNames)
                                    .applyingExecutorLastName(applExecLName)
                                    .applyingExecutorName(applExecName)
                                    .applicantFamilyDetails(applicantFamilyDetails)
                                    .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                                    .build());
                })
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorApplying>> mapFromSolsAdditionalExecutorListToApplyingExecutors(
            CaseData caseData) {
        return caseData.getSolsAdditionalExecutorList()
                .stream()
                .filter(exec -> YES.equals(exec.getValue().getAdditionalApplying()))
                .map(exec -> {
                    final String applExecFNames = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecForenames(),
                            "additional executor forenames");
                    final String applExecLName = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecLastname(),
                            "additional executor last name");
                    final String applExecName = applExecFNames + " " + applExecLName;
                    return new CollectionMember<>(
                            exec.getId(),
                            AdditionalExecutorApplying.builder()
                                    .applyingExecutorAddress(exec.getValue().getAdditionalExecAddress())
                                    .applyingExecutorFirstName(applExecFNames)
                                    .applyingExecutorLastName(applExecLName)
                                    .applyingExecutorName(applExecName)
                                    .applyingExecutorType(EXECUTOR_TYPE_NAMED)
                                    .applyingExecutorOtherNames(exec.getValue().getAdditionalExecAliasNameOnWill())
                                    .build());
                })
                .collect(Collectors.toList());
    }

    public List<CollectionMember<AdditionalExecutorNotApplying>> mapFromSolsAdditionalExecsToNotApplyingExecutors(
            CaseData caseData) {
        return caseData.getSolsAdditionalExecutorList()
                .stream()
                .filter(exec -> exec.getValue().getAdditionalApplying().equals(NO))
                .map(exec -> {
                    final String nApplExecName = FormattingService.capitaliseEachWord(
                            exec.getValue().getAdditionalExecForenames()
                                    + " " + exec.getValue().getAdditionalExecLastname(),
                            "additional executor name");
                    return new CollectionMember<>(
                            exec.getId(),
                            AdditionalExecutorNotApplying.builder()
                                    .notApplyingExecutorName(nApplExecName)
                                    .notApplyingExecutorReason(exec.getValue().getAdditionalExecReasonNotApplying())
                                    .notApplyingExecutorNameOnWill(exec.getValue().getAdditionalExecAliasNameOnWill())
                                    .build());
                })
                .collect(Collectors.toList());
    }

    public CollectionMember<AdditionalExecutorApplying> mapFromSolicitorToApplyingExecutor(
            CaseData caseData) {
        final String capSolSotFnames = FormattingService.capitaliseEachWord(
                caseData.getSolsSOTForenames(),
                "Solicitor Statement of Truth Forenames");
        final String capSolSotSname = FormattingService.capitaliseEachWord(
                caseData.getSolsSOTSurname(),
                "Solicitor Statement of Truth Surnames");
        final String capSolSotName = capSolSotFnames + " " + capSolSotSname;

        // Create applying executor collection member containing solicitor names
        return new CollectionMember<>(SOLICITOR_ID, AdditionalExecutorApplying.builder()
                .applyingExecutorFirstName(capSolSotFnames)
                .applyingExecutorLastName(capSolSotSname)
                .applyingExecutorName(capSolSotName)
                .applyingExecutorType(getSolExecType(caseData))
                .applyingExecutorAddress(caseData.getSolsSolicitorAddress())
                .applyingExecutorTrustCorpPosition(
                        getTrustCorpTitleClearingTypes().contains(caseData.getTitleAndClearingType())
                                && NO.equals(caseData.getSolsSolicitorIsExec())
                                && YES.equals(caseData.getSolsSolicitorIsApplying())
                                ? caseData.getProbatePractitionersPositionInTrust() : null)
                .build());
    }

    public CollectionMember<AdditionalExecutorApplying> mapFromPrimaryApplicantToApplyingExecutor(
            CaseData caseData) {
        return mapFromPrimaryApplicantToApplyingExecutor(caseData, UUID.randomUUID().toString());
    }

    public CollectionMember<AdditionalExecutorApplying> mapFromPrimaryApplicantToApplyingExecutor(
            CaseData caseData, String collectionMemberId) {
        final String pApplFNames = FormattingService.capitaliseEachWord(
                caseData.getPrimaryApplicantForenames(),
                "Primary applicant forenames");
        final String pApplLName = FormattingService.capitaliseEachWord(
                caseData.getPrimaryApplicantSurname(),
                "Primary applicant surname");
        final String pAppName = FormattingService.capitaliseEachWord(
                caseData.getPrimaryApplicantFullName(),
                "Primary applicant full name");
        // Create applying executor collection member containing primary applicant names
        return new CollectionMember<>(
                collectionMemberId,
                AdditionalExecutorApplying.builder()
                        .applyingExecutorFirstName(pApplFNames)
                        .applyingExecutorLastName(pApplLName)
                        .applyingExecutorName(pAppName)
                        .applyingExecutorType(getSolExecType(caseData))
                        .applyingExecutorAddress(caseData.getPrimaryApplicantAddress())
                        .applyingExecutorOtherNames(caseData.getSolsExecutorAliasNames())
                        .applyingExecutorOtherNamesReason(caseData.getPrimaryApplicantAliasReason())
                        .applyingExecutorTrustCorpPosition(
                                getTrustCorpTitleClearingTypes().contains(caseData.getTitleAndClearingType())
                                && NO.equals(caseData.getSolsSolicitorIsExec())
                                && YES.equals(caseData.getSolsSolicitorIsApplying())
                                        ? caseData.getProbatePractitionersPositionInTrust() : null)
                        .build());
    }

    public CollectionMember<AdditionalExecutorNotApplying> mapFromPrimaryApplicantToNotApplyingExecutor(
            CaseData caseData) {
        // Create not applying executor collection member containing primary applicant names
        return mapFromPrimaryApplicantToNotApplyingExecutor(caseData, UUID.randomUUID().toString());
    }

    public CollectionMember<AdditionalExecutorNotApplying> mapFromPrimaryApplicantToNotApplyingExecutor(
            CaseData caseData, String collectionMemberId) {
        final String capPriApplName = FormattingService.capitaliseEachWord(
                caseData.getPrimaryApplicantFullName(),
                "Primary applicant full name");
        // Create not applying executor collection member containing primary applicant names
        return new CollectionMember<>(collectionMemberId, AdditionalExecutorNotApplying.builder()
                .notApplyingExecutorName(capPriApplName)
                .notApplyingExecutorReason(caseData.getSolsPrimaryExecutorNotApplyingReason())
                .notApplyingExecutorNameOnWill(caseData.getSolsExecutorAliasNames())
                .build());
    }

    private String getSolExecType(CaseData caseData) {
        String executorType = "";

        if (NO.equals(caseData.getSolsSolicitorIsExec()) && YES.equals(caseData.getSolsSolicitorIsApplying())
            && getNonTrustPtnrTitleClearingTypes().contains(caseData.getTitleAndClearingType())) {
            executorType = EXECUTOR_TYPE_PROFESSIONAL;
        } else if (NO.equals(caseData.getSolsSolicitorIsExec()) && YES.equals(caseData.getSolsSolicitorIsApplying())
            && getTrustCorpTitleClearingTypes().contains(caseData.getTitleAndClearingType())) {
            executorType = EXECUTOR_TYPE_TRUST_CORP;
        } else {
            executorType = EXECUTOR_TYPE_NAMED;
        }

        return executorType;
    }

}
